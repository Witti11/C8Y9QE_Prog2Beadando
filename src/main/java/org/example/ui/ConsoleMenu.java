package org.example.ui;

import org.example.controller.GameEngine;
import org.example.model.*;
import org.example.command.*;
import org.example.persistence.SaveManager;


import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;


public class ConsoleMenu {
    private GameEngine engine;
    private final Scanner scanner;
    private boolean running;
    private final SaveManager saveManager;

    public ConsoleMenu() {
        this.scanner = new Scanner(System.in);
        this.running = false;
        this.saveManager = new SaveManager();
    }

    public void start() {
        this.running = true;
        System.out.println("\n===================================");
        System.out.println("ÜDVÖZÖL A MUTYI TYCOON!");
        System.out.println("\n===================================");
        boolean gameModeChoice = true;
        while (gameModeChoice) {
            switch (gameMode()) {
                case "1":
                    startNewGame();
                    gameModeChoice = false;
                    break;
                case "2":
                    loadGame();
                    gameModeChoice = false;
                    break;
                case "3":
                    showTutorial();
                    break;
                case "0":
                    gameModeChoice = false;
                    this.running = false;
                    break;
                default:
                    System.out.println("Érvénytelen válasz, próbálja újra!");
                    break;
            }
        }
        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    showStatistics();
                    break;
                case "2":
                    handleStore();
                    break;
                case "3":
                    if (engine.numOfUnlockedRoutes()<=0){
                        System.out.println("Még nem oldottál fel útvonalat!");
                        break;
                    }
                    routeSetter();
                    break;
                case "4":
                    if(engine.getCompany().getOwnedVehicles().isEmpty()){
                        System.out.println("Még nem vásároltál járművet");
                        break;
                    }
                    handleVehicles();
                    break;
                case "5":
                    NextTurnCommand nextTurn = new NextTurnCommand(engine);
                    nextTurn.execute();
                    if(nextTurn.isGameOver()){
                        this.running=false;
                    }
                    break;
                case "6":
                    saveGame();
                    break;
                case "0":
                    System.out.println("El akarod menteni a játékot?");
                    System.out.println("\tIgen: 1");
                    System.out.println("\tNem: 2");
                    String choice=scanner.nextLine().trim();
                    if (choice.equals("1")){
                        saveGame();
                        this.running=false;
                    } else if (choice.equals("2")) {
                        this.running=false;
                    }
                    else{
                        System.out.println("Hibás választ adtál meg. Próbáld újra!");
                        break;
                    }

                    break;
                default:
                    System.out.println("Érvénytelen válasz, próbálja újra!");
                    break;
            }
        }
        System.out.println("Viszlát legközelebb!");
    }

    private String gameMode() {
        System.out.println("1. Új játék");
        System.out.println("2. Mentés betöltése");
        System.out.println("3. Tutorial");
        System.out.println("0. Kilépés");
        System.out.println("Válaszd ki mit szeretnél csinálni!(0-3)");
        String choice = scanner.nextLine().trim();
        if (choice.equalsIgnoreCase("1") || choice.equalsIgnoreCase("2") || choice.equalsIgnoreCase("3") || choice.equalsIgnoreCase("0")) {
            return choice;
        } else {
            return gameMode();
        }
    }

    private void startNewGame() {
        System.out.println("Adja meg a vállalatának a nevét!");
        String companyName = scanner.nextLine().trim();
        if (companyName.isEmpty()) {
            companyName = "Mutyi Bt.";
        }

        Company company = new Company(companyName);
        this.engine = new GameEngine(company);
        setupMap();
    }

    private void loadGame() {
        List<String> saves = saveManager.getAvailableSaves();
        if (saves.isEmpty()) {
            System.out.println("Nincs elérhető mentés!\nÚj játék indítása!");
            startNewGame();
            return;
        }
        int num = 1;
        for (String s : saves) {
            System.out.println(num + ". " + s);
            num++;
        }
        System.out.println("Add meg melyik mentést szeretnéd folytatni!\nFONTOS a mentés sorszámát add meg!");
        String choice = scanner.nextLine().trim();
        int index = -1;
        try {
            index = Integer.parseInt(choice) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Hibás értéket adtál meg: " + e.getMessage());
        }

        String selectedSave;
        if (index >= 0 && index < saves.size()) {
            selectedSave = saves.get(index);
        } else {
            System.out.println("Nincs ilyen mentés!\nÚj játék indítása!");
            startNewGame();
            return;
        }
        try {
            this.engine = saveManager.loadGame(selectedSave);
            System.out.println("Sikeres betöltés: " + selectedSave);
        } catch (Exception e) {
            System.out.println("Nem sikerült betölteni a mentést: " + e.getMessage());
            System.out.println("Új játék indítása!");
            startNewGame();
        }
    }

    private void showTutorial() {
        File file = new File("src/main/resources/tutorial.txt");
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Hiba: A tutorial.txt fájl nem található!");
        }
        System.out.println("\nNyomj Enter-t a folytatáshoz...");
        scanner.nextLine();
    }

    private void printMenu() {
        head();
        System.out.println("1. Vállalati statisztikák");
        System.out.println("2. Bolt");
        System.out.println("3. Útvonal beállítása");
        System.out.println("4. Járművek kezelése");
        System.out.println("5. Kör befejezése");
        System.out.println("6. Játék mentése!");
        System.out.println("0. Kilépés");
        System.out.println("Mit szeretnél tenni? (0-6): ");
    }

    private void head() {
        int maintenanceCost=engine.getCompany().calculateTotalMaintenanceCost();
        System.out.println("\n===================================");
        System.out.println("* " + engine.getCompany().getCompanyName() + " * | Kör: " + engine.getCurrentTurn() + " | Egyenleg: " + engine.getCompany().getMoney() + "$ | Fenntartási költség: "+maintenanceCost+"$/kör");
        System.out.println("===================================");
    }

    private void showStatistics() {
        head();
        System.out.println("STATISZTIKÁK");
        System.out.println("===================================");
        System.out.println("===================================");
        System.out.println("--- HELYSZÍNEK ---");
        System.out.println("===================================");
        for (Location loc : engine.getLocations()) {
            if (loc instanceof Producer) {
                Producer p = (Producer) loc;
                int stock = p.getStock(p.getOutputCargo());
                System.out.println("TERMELŐ "+ p.getDisplayName()+" -> | Termék: " + p.getOutputCargo().getDisplayName() + " | Raktáron: " + stock
                        + " db ( + " + p.getProductionRate() + "/kör)");
            } else if (loc instanceof Factory) {
                Factory f = (Factory) loc;
                int inStock = f.getStock(f.getInputCargo());
                int outStock = f.getStock(f.getOutputCargo());
                System.out.println("GYÁR "+f.getDisplayName()+" -> | Alapanyag (" + f.getInputCargo().getDisplayName() + "): " + inStock + " db | Késztermék (" +
                        f.getOutputCargo().getDisplayName() + "): " + outStock + " db ( + " + f.getProcessingRate() + " /kör)");
            } else if (loc instanceof City) {
                City c = (City) loc;
                System.out.println("VÁROS "+c.getDisplayName()+" -> | Igények: ");
                for (CargoType demand : c.getDemands()) {
                    System.out.print("\t"+demand.getDisplayName() + " (Teljesítve: " + c.getFulfill(demand) + " db)\n");
                }
            }
        }
        System.out.println("\n===================================");
        System.out.println("--- JÁRMŰFLOTTA ---");
        System.out.println("===================================");
        System.out.println("KAMIONOK:");
        if (engine.getCompany().getOwnedVehicles().isEmpty()) {
            System.out.println("Még nincs saját kamionod.");
        } else {
            for (Vehicle v : engine.getCompany().getOwnedVehicles()) {

                if (v instanceof Train) {
                    continue;
                } else {
                    printVehicleStatus(v);
                }
            }
        }
        System.out.println();
        System.out.println("\n===================================");
        System.out.println("VONATOK:");
        if (engine.getCompany().getOwnedVehicles().isEmpty()) {
            System.out.println("Még nincs saját vonatod.");
        } else {
            for (Vehicle v : engine.getCompany().getOwnedVehicles()) {

                if (v instanceof Truck) {
                    continue;
                } else {
                    printVehicleStatus(v);
                }
            }

        }

        System.out.println("\n===================================");
        System.out.println("--- ÚTVONALAK ---");
        System.out.println("===================================");
        if(engine.numOfUnlockedRoutes()==0){
            System.out.println("Még nincs feloldott útvonalad!");
        }
        else{
            for(Route r : engine.getRoutes()){
                if (!r.isUnlocked()){
                    continue;
                }
                if(r.getAssignedCargo()!=null) {
                    System.out.println("[" + r.getId() + "] " + r.getSource().getDisplayName() + " -> " + r.getDestination().getDisplayName() + " ("
                            + r.getType().getDisplayName() + "," + r.getDistance() + " km) - Termék: " + r.getAssignedCargo().getDisplayName());
                }else{
                    System.out.println("[" + r.getId() + "] " + r.getSource().getDisplayName() + " -> " + r.getDestination().getDisplayName() + " ("
                            + r.getType().getDisplayName() + "," + r.getDistance() + " km) - Termék: Még nincs termék beállítva!");
                }
                if(r.getAssignedVehicle().isEmpty()){
                    System.out.println("\tMég nincsenek járművek az útvonalon.");
                }
                else{
                    System.out.print("\tJárművek: ");
                    for (Vehicle v:r.getAssignedVehicle()){
                        System.out.print("*"+v.getId()+" Típus: "+v.getVersionName()+"  ");
                    }
                    System.out.println();
                }
            }
        }

    }

    private void handleStore() {
        head();
        System.out.println("BOLT");
        System.out.println("1. Útvonal feloldása");
        System.out.println("2. Kamion vásárlás");
        System.out.println("3. Vonat vásárlás");
        System.out.println("0. Kilépés");
        System.out.println("Mit szeretne venni?(0-3):");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                buyRoute();
                break;
            case "2":
                buyTruck();
                break;
            case "3":
                buyTrain();
                break;
            case "0":
                break;
            default:
                System.out.println("Érvénytelen válasz!");
                break;
        }

    }

    private void printVehicleStatus(Vehicle v) {
        String routeText;
        if (v.getAssignedRoute() == null) {
            routeText = "Garázsban áll";
        } else {
            routeText = "Útvonal: " + v.getAssignedRoute().getId();
        }
        System.out.println("[" + v.getId() + "] " + routeText + " | Típus: " + v.getVersionName() + " | Sebesség: " + v.getSpeed() + " km/kör | Fenntartási költség: "
                + v.getMaintenanceCost() + "$/kör | Kapacitás: " + v.getCapacity() + " db");

    }

    private void buyRoute() {
        System.out.println("\n--- FELOLDHATÓ ÚTVONALAK ---");
        for (Route r : engine.getRoutes()) {
            if (!r.isUnlocked()) {
                System.out.println("[" + r.getId() + "] " + r.getSource().getDisplayName() + " -> " + r.getDestination().getDisplayName() + " ("
                        + r.getType().getDisplayName() + "," + r.getDistance() + " km) - Ár: " + r.getType().getUnlockCost() + "$");
            }
        }
        System.out.println("Add meg a feloldani kívánt útvonal azonosítjóját. Kilépés: 0!");
        String id = scanner.nextLine().trim();
        if ("0".equals(id)) return;

        Route route = findRouteById(id);
        if (route == null) {
            System.out.println("Nincs ilyen útvonal");
            return;
        }
        new UnlockRouteCommand(engine.getCompany(), route).execute();
    }

    private void buyTruck() {
        System.out.println("\n--- ELÉRHETŐ KAMIONOK ---");
        for (TruckType t : TruckType.values()) {
            System.out.println("[" + t.name() + "] Ár: " + t.getPrice() + "$ | Sebesség: " + t.getSpeed() + " km/kör | Fenntartási költség: "
                    + t.getMaintenanceCost() + "$/kör | Kapacitás: " + t.getCapacity() + " db");
        }
        System.out.println("Add meg a megvásárolni kívánt kamion azonosítjóját. Kilépés: 0!");
        String typeInput = scanner.nextLine().trim().toUpperCase();
        if ("0".equals(typeInput)) return;

        TruckType chosenType = null;
        for (TruckType t : TruckType.values()) {
            if (t.name().equals(typeInput)) {
                chosenType = t;
                break;
            }
        }
        if (chosenType == null) {
            System.out.println("Nincs ilyen kamion");
            return;
        }
        String newId = engine.getCompany().generateTruckId();
        Truck newTruck = new Truck(newId, chosenType);
        new BuyVehicleCommand(engine.getCompany(), newTruck).execute();
    }

    private void buyTrain() {
        System.out.println("\n--- ELÉRHETŐ VONATOK ---");
        for (TrainType t : TrainType.values()) {
            System.out.println("[" + t.name() + "] Ár: " + t.getPrice() + "$ | Sebesség: " + t.getSpeed() + " km/kör | Fenntartási költség: "
                    + t.getMaintenanceCost() + "$/kör | Kapacitás: " + t.getCapacity() + " db");
        }
        System.out.println("Add meg a megvásárolni kívánt vonat azonosítjóját. Kilépés: 0!");
        String typeInput = scanner.nextLine().trim().toUpperCase();
        if ("0".equals(typeInput)) return;

        TrainType chosenType = null;
        for (TrainType t : TrainType.values()) {
            if (t.name().equals(typeInput)) {
                chosenType = t;
                break;
            }
        }
        if (chosenType == null) {
            System.out.println("Nincs ilyen vonat");
            return;
        }
        String newId = engine.getCompany().generateTrainId();
        Train newTrain = new Train(newId, chosenType);
        new BuyVehicleCommand(engine.getCompany(), newTrain).execute();
    }

    private void routeSetter() {
        head();
        System.out.println("ÚTVONAL BEÁLLÍTÁSA");
        System.out.println("Feloldott útvonalak:");
        for (Route r : engine.getRoutes()) {
            if (r.isUnlocked()) {
                if(r.getAssignedCargo()!=null) {
                    System.out.println("[" + r.getId() + "] " + r.getSource().getDisplayName() + " -> " + r.getDestination().getDisplayName() + " ("
                            + r.getType().getDisplayName() + "," + r.getDistance() + " km) - Termék: " + r.getAssignedCargo().getDisplayName());
                }else{
                    System.out.println("[" + r.getId() + "] " + r.getSource().getDisplayName() + " -> " + r.getDestination().getDisplayName() + " ("
                            + r.getType().getDisplayName() + "," + r.getDistance() + " km) - Termék: Még nincs termék beállítva!");
                }
            }
        }
        System.out.println("Add meg a módosítani kívánt útvonal azonosítóját! Kilépés:0");
        String id = scanner.nextLine().trim();
        if ("0".equals(id)) return;
        Route route = findRouteById(id);
        if (route == null) {
            System.out.println("Nincs ilyen útvonal!");
            return;
        }
        if (!route.isUnlocked()) {
            System.out.println("Az útvonal még nincs feloldva!");
            return;
        }
        System.out.println("Módosítani kívánt útvonal:");
        if(route.getAssignedCargo()!=null) {
            System.out.println("[" + route.getId() + "] " + route.getSource().getDisplayName() + " -> " + route.getDestination().getDisplayName() + " ("
                    + route.getType().getDisplayName() + "," + route.getDistance() + " km) - Termék: " + route.getAssignedCargo().getDisplayName());
        }else{
            System.out.println("[" + route.getId() + "] " + route.getSource().getDisplayName() + " -> " + route.getDestination().getDisplayName() + " ("
                    + route.getType().getDisplayName() + "," + route.getDistance() + " km) - Termék: Még nincs termék beállítva!");
        }
        System.out.println("Választható termékek:");
        for (CargoType c : CargoType.values()) {
            System.out.println("Termék: " + c.getDisplayName() + " Ár: " + c.getBaseValue() + "$");
        }
        System.out.println("Add meg milyen terméket szeretne szállítani:");
        String cargoId = scanner.nextLine().trim().toUpperCase();
        CargoType chosenCargo = null;
        for (CargoType c : CargoType.values()) {
            if (c.name().equalsIgnoreCase(cargoId) || c.getDisplayName().equalsIgnoreCase(cargoId)) {
                chosenCargo = c;
                break;
            }
        }
        if (chosenCargo == null) {
            System.out.println("Nincs ilyen termék!");
            return;
        }
        new SetRouteCargoCommand(route, chosenCargo).execute();

    }

    private void handleVehicles() {
        head();
        System.out.println("JÁRMŰVEK BEÁLLÍTÁSA");
        for (Vehicle v : engine.getCompany().getOwnedVehicles()) {
            printVehicleStatus(v);
        }
        System.out.println("Add meg a modosítani kívánt jármű azonosítóját! Kilépés: 0!");
        String id = scanner.nextLine().trim();
        if ("0".equals(id)) return;
        Vehicle v = findVehicleById(id);
        if (v == null) {
            System.out.println("Nincs ilyen azonosítójú jármű!");
            return;
        }
        System.out.println("Kiválasztott jármű:");
        printVehicleStatus(v);
        System.out.println("Mit szeretne tenni ezzel a járművel?(0-2)");
        System.out.println("1. Útvonalhoz rendelés.");
        System.out.println("2. Jármű eladás.");
        System.out.println("0. Kilépés.");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1":
                System.out.println("Útvonalak:");
                for (Route r : engine.getRoutes()) {
                    if (r.isUnlocked() && r.getType() == v.getRequiredRouteType() && r.getAssignedCargo()!=null) {
                        System.out.println("[" + r.getId() + "] " + r.getSource().getDisplayName() + " -> " + r.getDestination().getDisplayName() + " ("
                                + r.getType().getDisplayName() + "," + r.getDistance() + " km) - Termék: " + r.getAssignedCargo().getDisplayName());
                    }
                }
                System.out.println("Melyik útvonalhoz szeretnéd adni a járművet?");
                String routeId = scanner.nextLine().trim();
                Route route = findRouteById(routeId);
                if (route == null) {
                    System.out.println("Nincs ilyen útvonal!");
                    return;
                }
                new AssignVehicleCommand(v, route).execute();
                break;
            case "2":
                new SellVehicleCommand(engine.getCompany(),v).execute();
                break;
            case "0":
                break;
            default:
                System.out.println("Érvénytelen válasz! Kilépés.");
                break;
        }

    }

    private void saveGame() {
        System.out.println("Add meg a mentés nevét!");
        String saveName = scanner.nextLine().trim();
        if (saveName.isEmpty()) {
            saveName = "quicksave";
        }
        new SaveGameCommand(engine, saveName).execute();
    }

    private Route findRouteById(String id) {
        if (id == null) {
            return null;
        }
        for (Route r : engine.getRoutes()) {
            if (r.getId().equalsIgnoreCase(id)) {
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
            if (v.getId().equalsIgnoreCase(id)) {
                return v;
            }
        }
        return null;
    }


    private void setupMap() {
        //1. Cities
        City c1 = new City("C_ERCFALVA", "Ércfalva");
        c1.addDemands(CargoType.BREAD);
        c1.addDemands(CargoType.SCREW);

        City c2 = new City("C_ZOLDHALOM", "Zöldhalom");
        c2.addDemands(CargoType.FURNITURE);
        c2.addDemands(CargoType.BREAD);

        City c3 = new City("C_KERESZTES", "Keresztes");
        c3.addDemands(CargoType.SCREW);
        c3.addDemands(CargoType.PETROL);

        City c4 = new City("C_MALOMMEZO", "Malommező");
        c4.addDemands(CargoType.BREAD);
        c4.addDemands(CargoType.FURNITURE);
        c4.addDemands(CargoType.PETROL);

        //2. Producers
        Producer pForest1 = new Producer("P_FOR_1", "Öregtölgy Erdészet", CargoType.WOOD, 5);
        pForest1.addCargo(CargoType.WOOD,5);
        Producer pForest2 = new Producer("P_FOR_2", "Sötétvölgyi Fatelep", CargoType.WOOD, 5);
        pForest2.addCargo(CargoType.WOOD,5);
        Producer pFarm1 = new Producer("P_FAR_1", "Aranykalász Birtok", CargoType.GRAIN, 5);
        pFarm1.addCargo(CargoType.GRAIN,5);
        Producer pFarm2 = new Producer("P_FAR_2", "Déli Ugar Gazdaság", CargoType.GRAIN, 5);
        pFarm2.addCargo(CargoType.GRAIN,5);
        Producer pMine1 = new Producer("P_MIN_1", "Vashegy Bánya", CargoType.IRON, 5);
        pMine1.addCargo(CargoType.IRON,5);
        Producer pOil1 = new Producer("P_OIL_1", "Fekete Arany Fúrótorony", CargoType.OIL, 5);
        pOil1.addCargo(CargoType.OIL,5);

        //3. Factories
        Factory fScrew1 = new Factory("F_SCR_1", "Precíziós Kötőelem Művek", CargoType.IRON, CargoType.SCREW, 5);
        Factory fBakery1 = new Factory("F_BAK_1", "Ropogós Cipó Manufaktúra", CargoType.GRAIN, CargoType.BREAD, 5);
        Factory fBakery2 = new Factory("F_BAK_2", "Hajnali Sütöde", CargoType.GRAIN, CargoType.BREAD, 5);
        Factory fIKEA1 = new Factory("F_IKE_1", "Fenyves Bútorüzem", CargoType.WOOD, CargoType.FURNITURE, 5);
        Factory fIKEA2 = new Factory("F_IKE_2", "Déli Faipari Zrt.", CargoType.WOOD, CargoType.FURNITURE, 5);
        Factory fOIL1 = new Factory("F_OIL_1", "Keresztesi Olajfinomító", CargoType.OIL, CargoType.PETROL, 5);

        //4. Adding places
        engine.addLocation(c1);
        engine.addLocation(c2);
        engine.addLocation(c3);
        engine.addLocation(c4);
        engine.addLocation(pForest1);
        engine.addLocation(pForest2);
        engine.addLocation(pFarm1);
        engine.addLocation(pFarm2);
        engine.addLocation(pMine1);
        engine.addLocation(pOil1);
        engine.addLocation(fScrew1);
        engine.addLocation(fBakery1);
        engine.addLocation(fBakery2);
        engine.addLocation(fIKEA1);
        engine.addLocation(fIKEA2);
        engine.addLocation(fOIL1);

        //5. Creating Routes
        engine.addRoute(new Route("1", fScrew1, c1, RouteType.ROAD, 2));//1
        engine.addRoute(new Route("2", c1, pForest1, RouteType.ROAD, 4));//2
        engine.addRoute(new Route("3", pForest1, c2, RouteType.ROAD, 3));//3
        engine.addRoute(new Route("4",fBakery1, c1, RouteType.ROAD, 3));//4
        engine.addRoute(new Route("5", fBakery1, pForest1, RouteType.ROAD, 3));//5
        engine.addRoute(new Route("6", pForest1, fIKEA1, RouteType.ROAD, 3));//6
        engine.addRoute(new Route("7", fIKEA1, c2, RouteType.ROAD, 2));//7
        engine.addRoute(new Route("8", fBakery1, c3, RouteType.ROAD, 6));//8
        engine.addRoute(new Route("9",  pFarm1, fBakery1, RouteType.ROAD, 4));//9
        engine.addRoute(new Route("10", fIKEA1, pMine1, RouteType.ROAD, 3));//10
        engine.addRoute(new Route("11", pFarm1, pMine1, RouteType.ROAD, 3));//11
        engine.addRoute(new Route("12", c3, pFarm1, RouteType.ROAD, 4));//12
        engine.addRoute(new Route("13", pFarm1, pForest2, RouteType.ROAD, 4));//13
        engine.addRoute(new Route("14", pForest2, pMine1, RouteType.ROAD, 5));//14
        engine.addRoute(new Route("15", c3, pForest2, RouteType.ROAD, 4));//15
        engine.addRoute(new Route("16", fOIL1, c3, RouteType.ROAD, 2));//16
        engine.addRoute(new Route("17", c3, fIKEA2, RouteType.ROAD, 4));//17
        engine.addRoute(new Route("18", pForest2, fIKEA2, RouteType.ROAD, 2));//18
        engine.addRoute(new Route("19", fIKEA2, c4, RouteType.ROAD, 5));//19
        engine.addRoute(new Route("20", fBakery2, c4, RouteType.ROAD, 2));//20
        engine.addRoute(new Route("21", pFarm2, fBakery2, RouteType.ROAD, 2));//21
        engine.addRoute(new Route("22", pOil1, fOIL1, RouteType.ROAD, 2));//22
        engine.addRoute(new Route("23", c1, fOIL1, RouteType.RAIL, 15));//23
        engine.addRoute(new Route("24", pMine1, fScrew1, RouteType.RAIL, 10));//24
        engine.addRoute(new Route("25", c2, c4, RouteType.RAIL, 10));//25
    }

}
