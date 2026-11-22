// [AI-GENERATED WEB GUI] Ez a fájl a grafikus megjelenítési réteg része.
package org.example.webui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.example.command.*;
import org.example.controller.GameEngine;
import org.example.exception.InsufficientFundsException;
import org.example.exception.InvalidCargoException;
import org.example.exception.InvalidRouteException;
import org.example.model.*;
import org.example.persistence.SaveManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebServer {
    private static final int DEFAULT_PORT = 8080;

    private final int port;
    private final SaveManager saveManager;
    private final ObjectMapper objectMapper;
    private final Object stateLock = new Object();

    private GameEngine engine;
    private boolean gameOver;
    private boolean gameStarted;
    private Javalin app;

    public WebServer() {
        this(DEFAULT_PORT);
    }

    public WebServer(int port) {
        this.port = port;
        this.saveManager = new SaveManager();
        this.objectMapper = new ObjectMapper();
        this.engine = WebMapFactory.createNewGame("Mutyi Bt.");
        this.gameOver = false;
        this.gameStarted = false;
    }

    public void start() {
        app = Javalin.create(config -> {
            config.staticFiles.add("/web", io.javalin.http.staticfiles.Location.CLASSPATH);
            config.http.defaultContentType = "application/json; charset=utf-8";
        });

        registerExceptionHandlers(app);
        registerRoutes(app);

        app.start(port);
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }

    private void registerExceptionHandlers(Javalin server) {
        server.exception(InsufficientFundsException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", safeMessage(e, "Nincs elég fedezet a művelethez!"))));
        server.exception(InvalidRouteException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", safeMessage(e, "Érvénytelen útvonal művelet!"))));
        server.exception(InvalidCargoException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", safeMessage(e, "Érvénytelen rakomány művelet!"))));
        server.exception(IllegalArgumentException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", safeMessage(e, "Hibás bemeneti paraméter!"))));
        server.exception(IllegalStateException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", safeMessage(e, "A művelet jelenlegi állapotban nem hajtható végre!"))));
        server.exception(IOException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", "Fájlkezelési hiba: " + safeMessage(e, "ismeretlen IO hiba"))));
        server.exception(ClassNotFoundException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", "Nem kompatibilis vagy sérült mentésfájl!")));
    }

    private void registerRoutes(Javalin server) {
        server.get("/api/state", this::handleGetState);
        server.get("/api/tutorial", this::handleGetTutorial);
        server.get("/api/saves", this::handleGetSaves);

        server.post("/api/routes/unlock", this::handleUnlockRoute);
        server.post("/api/routes/cargo", this::handleSetRouteCargo);

        server.post("/api/vehicles/buy", this::handleBuyVehicle);
        server.post("/api/vehicles/assign", this::handleAssignVehicle);
        server.post("/api/vehicles/sell", this::handleSellVehicle);

        server.post("/api/turn/next", this::handleNextTurn);

        server.post("/api/game/save", this::handleSaveGame);
        server.post("/api/game/load", this::handleLoadGame);
        server.post("/api/game/new", this::handleNewGame);
    }

    private void handleGetState(Context ctx) {
        synchronized (stateLock) {
            ctx.json(buildState());
        }
    }

    private void handleGetTutorial(Context ctx) throws IOException {
        try (InputStream is = WebServer.class.getResourceAsStream("/tutorial.txt")) {
            if (is == null) {
                ctx.status(404).json(Map.of("error", "A tutorial.txt fájl nem található!"));
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            ctx.json(Map.of("content", content));
        }
    }

    private void handleGetSaves(Context ctx) {
        List<String> saves = new ArrayList<>(saveManager.getAvailableSaves());
        saves.sort(String::compareToIgnoreCase);
        ctx.json(Map.of("saves", saves));
    }

    private void handleUnlockRoute(Context ctx) throws IOException {
        Map<String, Object> body = readBody(ctx);
        String routeId = getStringParam(body, "routeId");

        synchronized (stateLock) {
            ensureGameActive();
            Route route = findRouteById(routeId);
            if (route == null) {
                throw new InvalidRouteException("Nem található ilyen útvonal: " + routeId);
            }
            if (route.isUnlocked()) {
                throw new InvalidRouteException("Ez az útvonal (#" + route.getId() + ") már fel van oldva!");
            }
            int cost = route.getType().getUnlockCost();
            Company company = engine.getCompany();
            if (!company.canAfford(cost)) {
                throw new InsufficientFundsException(
                        "Nincs elég fedezet az útvonal feloldásához! Szükséges: " + cost + "$, egyenleg: " + company.getMoney() + "$");
            }

            new UnlockRouteCommand(company, route).execute();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "#" + route.getId() + " útvonal sikeresen feloldva (-" + cost + "$)!");
            response.put("state", buildState());
            ctx.json(response);
        }
    }

    private void handleSetRouteCargo(Context ctx) throws IOException {
        Map<String, Object> body = readBody(ctx);
        String routeId = getStringParam(body, "routeId");
        String cargoStr = getStringParam(body, "cargo");

        synchronized (stateLock) {
            ensureGameActive();
            Route route = findRouteById(routeId);
            if (route == null) {
                throw new InvalidRouteException("Nem található ilyen útvonal: " + routeId);
            }
            if (!route.isUnlocked()) {
                throw new InvalidRouteException("Csak feloldott útvonalon lehet rakományt beállítani!");
            }
            CargoType cargo = parseCargoType(cargoStr);
            if (cargo == null) {
                throw new InvalidCargoException("Érvénytelen rakománytípus: " + cargoStr);
            }

            new SetRouteCargoCommand(route, cargo).execute();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "#" + route.getId() + " útvonal rakománya beállítva: " + cargo.getDisplayName());
            response.put("state", buildState());
            ctx.json(response);
        }
    }

    private void handleBuyVehicle(Context ctx) throws IOException {
        Map<String, Object> body = readBody(ctx);
        String kind = getStringParam(body, "kind").toUpperCase(Locale.ROOT);
        String version = getStringParam(body, "version").toUpperCase(Locale.ROOT);

        synchronized (stateLock) {
            ensureGameActive();
            Company company = engine.getCompany();
            Vehicle vehicle;

            if ("TRUCK".equals(kind)) {
                TruckType truckType = parseEnum(TruckType.class, version);
                if (truckType == null) {
                    throw new IllegalArgumentException("Ismeretlen kamion típus: " + version);
                }
                vehicle = new Truck(company.generateTruckId(), truckType);
            } else if ("TRAIN".equals(kind)) {
                TrainType trainType = parseEnum(TrainType.class, version);
                if (trainType == null) {
                    throw new IllegalArgumentException("Ismeretlen vonat típus: " + version);
                }
                vehicle = new Train(company.generateTrainId(), trainType);
            } else {
                throw new IllegalArgumentException("Ismeretlen jármű kategória: " + kind);
            }

            int price = vehicle.getPurchasePrice();
            if (!company.canAfford(price)) {
                throw new InsufficientFundsException(
                        "Nincs elég fedezet a jármű megvásárlásához! Szükséges: " + price + "$, egyenleg: " + company.getMoney() + "$");
            }

            new BuyVehicleCommand(company, vehicle).execute();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", vehicle.getId() + " (" + vehicle.getVersionName() + ") sikeresen megvásárolva (-" + price + "$)!");
            response.put("state", buildState());
            ctx.json(response);
        }
    }

    private void handleAssignVehicle(Context ctx) throws IOException {
        Map<String, Object> body = readBody(ctx);
        String vehicleId = getStringParam(body, "vehicleId");
        String routeId = getStringParam(body, "routeId");

        synchronized (stateLock) {
            ensureGameActive();
            Vehicle vehicle = findVehicleById(vehicleId);
            if (vehicle == null) {
                throw new IllegalArgumentException("Nem található ilyen jármű: " + vehicleId);
            }
            Route route = findRouteById(routeId);
            if (route == null) {
                throw new InvalidRouteException("Nem található ilyen útvonal: " + routeId);
            }
            if (!route.isUnlocked()) {
                throw new InvalidRouteException("Nem lehet járművet hozzáadni egy még fel nem oldott útvonalhoz (#" + route.getId() + ")!");
            }
            if (!vehicle.canTravelOn(route.getType())) {
                throw new InvalidRouteException(
                        "A jármű (" + vehicle.getId() + ") nem közlekedhet ezen a pályatípuson (" + route.getType().getDisplayName() + ")!");
            }
            if (route.getAssignedCargo() == null) {
                throw new InvalidRouteException("Az útvonalhoz (#" + route.getId() + ") még nincs hozzárendelve rakomány!");
            }
            if (vehicle.getAssignedRoute() == route) {
                throw new InvalidRouteException("A jármű (" + vehicle.getId() + ") már ezen az útvonalon közlekedik!");
            }

            new AssignVehicleCommand(vehicle, route).execute();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", vehicle.getId() + " hozzárendelve a(z) #" + route.getId() + " útvonalhoz!");
            response.put("state", buildState());
            ctx.json(response);
        }
    }

    private void handleSellVehicle(Context ctx) throws IOException {
        Map<String, Object> body = readBody(ctx);
        String vehicleId = getStringParam(body, "vehicleId");

        synchronized (stateLock) {
            ensureGameActive();
            Vehicle vehicle = findVehicleById(vehicleId);
            if (vehicle == null) {
                throw new IllegalArgumentException("Nem található ilyen jármű: " + vehicleId);
            }
            int refund = vehicle.getPurchasePrice() / 2;

            new SellVehicleCommand(engine.getCompany(), vehicle).execute();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", vehicle.getId() + " eladva (+" + refund + "$ visszatérítés)!");
            response.put("state", buildState());
            ctx.json(response);
        }
    }

    private void handleNextTurn(Context ctx) {
        synchronized (stateLock) {
            ensureGameActive();
            int moneyBefore = engine.getCompany().getMoney();
            int maintenance = engine.getCompany().calculateTotalMaintenanceCost();

            NextTurnCommand command = new NextTurnCommand(engine);
            command.execute();

            if (command.isGameOver()) {
                this.gameOver = true;
            }

            int moneyAfter = engine.getCompany().getMoney();
            int delta = moneyAfter - moneyBefore;

            String msg;
            if (this.gameOver) {
                msg = "JÁTÉK VÉGE! A vállalat nem tudta fedezni a " + maintenance + "$ fenntartási költséget és csődbe ment.";
            } else {
                String deltaStr = (delta >= 0 ? "+" : "") + delta + "$";
                msg = "Kör léptetve → Kör #" + engine.getCurrentTurn() + " (nettó egyenlegváltozás: " + deltaStr + ")";
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", msg);
            response.put("incomeDelta", delta);
            response.put("state", buildState());
            ctx.json(response);
        }
    }

    private void handleSaveGame(Context ctx) throws IOException {
        Map<String, Object> body = readBody(ctx);
        String saveName = getStringParam(body, "saveName").trim();
        if (saveName.isEmpty()) {
            saveName = "quicksave";
        }
        if (saveName.contains("..") || saveName.contains("/") || saveName.contains("\\")) {
            throw new IllegalArgumentException("A mentés neve nem tartalmazhat érvénytelen karaktereket (/, \\, ..)!");
        }

        synchronized (stateLock) {
            new SaveGameCommand(engine, saveName).execute();
            List<String> saves = new ArrayList<>(saveManager.getAvailableSaves());
            saves.sort(String::compareToIgnoreCase);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Játék sikeresen elmentve: " + saveName);
            response.put("saves", saves);
            ctx.json(response);
        }
    }

    private void handleLoadGame(Context ctx) throws IOException, ClassNotFoundException {
        Map<String, Object> body = readBody(ctx);
        String saveName = getStringParam(body, "saveName").trim();
        if (saveName.isEmpty()) {
            throw new IllegalArgumentException("Add meg a betölteni kívánt mentés nevét!");
        }
        if (saveName.contains("..") || saveName.contains("/") || saveName.contains("\\")) {
            throw new IllegalArgumentException("Érvénytelen mentés fájlnév!");
        }

        synchronized (stateLock) {
            GameEngine loaded = saveManager.loadGame(saveName);
            this.engine = loaded;
            this.gameOver = false;
            this.gameStarted = true;

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Mentés sikeresen betöltve: " + saveName);
            response.put("state", buildState());
            ctx.json(response);
        }
    }

    private void handleNewGame(Context ctx) throws IOException {
        Map<String, Object> body = readBody(ctx);
        String companyName = getStringParam(body, "companyName").trim();
        if (companyName.isEmpty()) {
            companyName = "Mutyi Bt.";
        }

        synchronized (stateLock) {
            this.engine = WebMapFactory.createNewGame(companyName);
            this.gameOver = false;
            this.gameStarted = true;

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Új játék indítva: " + engine.getCompany().getCompanyName());
            response.put("state", buildState());
            ctx.json(response);
        }
    }

    private Map<String, Object> buildState() {
        Company company = engine.getCompany();

        Map<String, Object> state = new LinkedHashMap<>();
        state.put("companyName", company.getCompanyName());
        state.put("money", company.getMoney());
        state.put("currentTurn", engine.getCurrentTurn());
        state.put("maintenanceCost", company.calculateTotalMaintenanceCost());
        state.put("unlockedRoutes", engine.numOfUnlockedRoutes());
        state.put("totalRoutes", engine.getRoutes().size());
        state.put("gameOver", gameOver);
        state.put("gameStarted", gameStarted);

        // Locations
        List<Map<String, Object>> locationDtos = new ArrayList<>();
        for (Location loc : engine.getLocations()) {
            Map<String, Object> locMap = new LinkedHashMap<>();
            locMap.put("id", loc.getId());
            locMap.put("displayName", loc.getDisplayName());

            Map<String, Integer> inventory = new LinkedHashMap<>();
            for (CargoType ct : CargoType.values()) {
                int stock = loc.getStock(ct);
                if (stock > 0) {
                    inventory.put(ct.name(), stock);
                }
            }
            locMap.put("inventory", inventory);

            if (loc instanceof Producer producer) {
                locMap.put("kind", "PRODUCER");
                locMap.put("outputCargo", producer.getOutputCargo().name());
                locMap.put("outputCargoName", producer.getOutputCargo().getDisplayName());
                locMap.put("productionRate", producer.getProductionRate());
                locMap.put("outputStock", producer.getStock(producer.getOutputCargo()));
            } else if (loc instanceof Factory factory) {
                locMap.put("kind", "FACTORY");
                locMap.put("inputCargo", factory.getInputCargo().name());
                locMap.put("inputCargoName", factory.getInputCargo().getDisplayName());
                locMap.put("outputCargo", factory.getOutputCargo().name());
                locMap.put("outputCargoName", factory.getOutputCargo().getDisplayName());
                locMap.put("processingRate", factory.getProcessingRate());
                locMap.put("inputStock", factory.getStock(factory.getInputCargo()));
                locMap.put("outputStock", factory.getStock(factory.getOutputCargo()));
            } else if (loc instanceof City city) {
                locMap.put("kind", "CITY");
                List<Map<String, Object>> demands = new ArrayList<>();
                int totalFulfilled = 0;
                List<CargoType> sortedDemands = new ArrayList<>(city.getDemands());
                sortedDemands.sort(Comparator.comparingInt(Enum::ordinal));
                for (CargoType demand : sortedDemands) {
                    int fulfilled = city.getFulfill(demand);
                    totalFulfilled += fulfilled;
                    Map<String, Object> dMap = new LinkedHashMap<>();
                    dMap.put("cargo", demand.name());
                    dMap.put("displayName", demand.getDisplayName());
                    dMap.put("baseValue", demand.getBaseValue());
                    dMap.put("fulfilled", fulfilled);
                    demands.add(dMap);
                }
                locMap.put("demands", demands);
                locMap.put("totalFulfilled", totalFulfilled);
            } else {
                locMap.put("kind", "LOCATION");
            }
            locationDtos.add(locMap);
        }
        state.put("locations", locationDtos);

        // Routes
        List<Map<String, Object>> routeDtos = new ArrayList<>();
        for (Route r : engine.getRoutes()) {
            Map<String, Object> rMap = new LinkedHashMap<>();
            rMap.put("id", r.getId());
            rMap.put("sourceId", r.getSource().getId());
            rMap.put("sourceName", r.getSource().getDisplayName());
            rMap.put("destinationId", r.getDestination().getId());
            rMap.put("destinationName", r.getDestination().getDisplayName());
            rMap.put("type", r.getType().name());
            rMap.put("typeName", r.getType().getDisplayName());
            rMap.put("distance", r.getDistance());
            rMap.put("unlockCost", r.getType().getUnlockCost());
            rMap.put("unlocked", r.isUnlocked());
            rMap.put("assignedCargo", r.getAssignedCargo() != null ? r.getAssignedCargo().name() : null);
            rMap.put("assignedCargoName", r.getAssignedCargo() != null ? r.getAssignedCargo().getDisplayName() : null);

            List<String> vehicleIds = new ArrayList<>();
            for (Vehicle v : r.getAssignedVehicle()) {
                vehicleIds.add(v.getId());
            }
            rMap.put("assignedVehicleIds", vehicleIds);
            rMap.put("vehicleCount", vehicleIds.size());
            routeDtos.add(rMap);
        }
        state.put("routes", routeDtos);

        // Vehicles
        List<Map<String, Object>> vehicleDtos = new ArrayList<>();
        for (Vehicle v : company.getOwnedVehicles()) {
            Map<String, Object> vMap = new LinkedHashMap<>();
            vMap.put("id", v.getId());
            vMap.put("kind", v instanceof Train ? "TRAIN" : "TRUCK");
            vMap.put("kindName", v instanceof Train ? "Vonat" : "Kamion");
            vMap.put("versionName", v.getVersionName());
            vMap.put("requiredRouteType", v.getRequiredRouteType().name());
            vMap.put("requiredRouteTypeName", v.getRequiredRouteType().getDisplayName());
            vMap.put("speed", v.getSpeed());
            vMap.put("maintenanceCost", v.getMaintenanceCost());
            vMap.put("capacity", v.getCapacity());
            vMap.put("purchasePrice", v.getPurchasePrice());
            vMap.put("sellValue", v.getPurchasePrice() / 2);
            vMap.put("currentCargo", v.getCurrentCargo() != null ? v.getCurrentCargo().name() : null);
            vMap.put("currentCargoName", v.getCurrentCargo() != null ? v.getCurrentCargo().getDisplayName() : null);
            vMap.put("currentAmount", v.getCurrentAmount());
            vMap.put("currentPosition", v.getCurrentPosition());
            vMap.put("movingForward", v.isMovingForward());
            vMap.put("assignedRouteId", v.getAssignedRoute() != null ? v.getAssignedRoute().getId() : null);
            vehicleDtos.add(vMap);
        }
        state.put("vehicles", vehicleDtos);

        // Catalog
        Map<String, Object> catalog = new LinkedHashMap<>();
        List<Map<String, Object>> trucks = new ArrayList<>();
        for (TruckType tt : TruckType.values()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("version", tt.name());
            m.put("speed", tt.getSpeed());
            m.put("maintenanceCost", tt.getMaintenanceCost());
            m.put("capacity", tt.getCapacity());
            m.put("price", tt.getPrice());
            trucks.add(m);
        }
        catalog.put("trucks", trucks);

        List<Map<String, Object>> trains = new ArrayList<>();
        for (TrainType tt : TrainType.values()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("version", tt.name());
            m.put("speed", tt.getSpeed());
            m.put("maintenanceCost", tt.getMaintenanceCost());
            m.put("capacity", tt.getCapacity());
            m.put("price", tt.getPrice());
            trains.add(m);
        }
        catalog.put("trains", trains);

        List<Map<String, Object>> cargos = new ArrayList<>();
        for (CargoType ct : CargoType.values()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", ct.name());
            m.put("displayName", ct.getDisplayName());
            m.put("baseValue", ct.getBaseValue());
            m.put("raw", ct.isRaw());
            cargos.add(m);
        }
        catalog.put("cargoTypes", cargos);

        state.put("catalog", catalog);
        return state;
    }

    private void ensureGameActive() {
        if (gameOver) {
            throw new IllegalStateException("A vállalat csődbe ment! Indíts új játékot vagy tölts be egy mentést.");
        }
    }

    private Route findRouteById(String id) {
        if (id == null) {
            return null;
        }
        for (Route r : engine.getRoutes()) {
            if (r.getId().equalsIgnoreCase(id.trim())) {
                return r;
            }
        }
        return null;
    }

    private Vehicle findVehicleById(String id) {
        if (id == null) {
            return null;
        }
        for (Vehicle v : engine.getCompany().getOwnedVehicles()) {
            if (v.getId().equalsIgnoreCase(id.trim())) {
                return v;
            }
        }
        return null;
    }

    private CargoType parseCargoType(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        for (CargoType ct : CargoType.values()) {
            if (ct.name().equalsIgnoreCase(trimmed) || ct.getDisplayName().equalsIgnoreCase(trimmed)) {
                return ct;
            }
        }
        return null;
    }

    private <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Map<String, Object> readBody(Context ctx) throws IOException {
        String raw = ctx.body();
        if (raw == null || raw.isBlank()) {
            return Collections.emptyMap();
        }
        return objectMapper.readValue(raw, new TypeReference<>() {});
    }

    private String getStringParam(Map<String, Object> body, String key) {
        Object val = body.get(key);
        return val == null ? "" : String.valueOf(val);
    }

    private String safeMessage(Exception e, String fallback) {
        return (e.getMessage() != null && !e.getMessage().isBlank()) ? e.getMessage() : fallback;
    }
}
