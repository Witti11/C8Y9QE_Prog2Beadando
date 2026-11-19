// [AI-GENERATED WEB GUI] Ez a fájl a grafikus megjelenítési réteg része.
package org.example.webui;

import org.example.controller.GameEngine;
import org.example.model.*;

public final class WebMapFactory {

    private WebMapFactory() {
    }

    public static GameEngine createNewGame(String companyName) {
        String safeName = (companyName == null || companyName.trim().isEmpty())
                ? "Mutyi Bt."
                : companyName.trim();

        Company company = new Company(safeName);
        GameEngine engine = new GameEngine(company);
        setupMap(engine);
        return engine;
    }

    private static void setupMap(GameEngine engine) {
        // 1. Cities
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

        // 2. Producers
        Producer pForest1 = new Producer("P_FOR_1", "Öregtölgy Erdészet", CargoType.WOOD, 5);
        pForest1.addCargo(CargoType.WOOD, 5);
        Producer pForest2 = new Producer("P_FOR_2", "Sötétvölgyi Fatelep", CargoType.WOOD, 5);
        pForest2.addCargo(CargoType.WOOD, 5);
        Producer pFarm1 = new Producer("P_FAR_1", "Aranykalász Birtok", CargoType.GRAIN, 5);
        pFarm1.addCargo(CargoType.GRAIN, 5);
        Producer pFarm2 = new Producer("P_FAR_2", "Déli Ugar Gazdaság", CargoType.GRAIN, 5);
        pFarm2.addCargo(CargoType.GRAIN, 5);
        Producer pMine1 = new Producer("P_MIN_1", "Vashegy Bánya", CargoType.IRON, 5);
        pMine1.addCargo(CargoType.IRON, 5);
        Producer pOil1 = new Producer("P_OIL_1", "Fekete Arany Fúrótorony", CargoType.OIL, 5);
        pOil1.addCargo(CargoType.OIL, 5);

        // 3. Factories
        Factory fScrew1 = new Factory("F_SCR_1", "Precíziós Kötőelem Művek", CargoType.IRON, CargoType.SCREW, 5);
        Factory fBakery1 = new Factory("F_BAK_1", "Ropogós Cipó Manufaktúra", CargoType.GRAIN, CargoType.BREAD, 5);
        Factory fBakery2 = new Factory("F_BAK_2", "Hajnali Sütöde", CargoType.GRAIN, CargoType.BREAD, 5);
        Factory fIKEA1 = new Factory("F_IKE_1", "Fenyves Bútorüzem", CargoType.WOOD, CargoType.FURNITURE, 5);
        Factory fIKEA2 = new Factory("F_IKE_2", "Déli Faipari Zrt.", CargoType.WOOD, CargoType.FURNITURE, 5);
        Factory fOIL1 = new Factory("F_OIL_1", "Keresztesi Olajfinomító", CargoType.OIL, CargoType.PETROL, 5);

        // 4. Adding places
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

        // 5. Creating Routes
        engine.addRoute(new Route("1", fScrew1, c1, RouteType.ROAD, 2));
        engine.addRoute(new Route("2", c1, pForest1, RouteType.ROAD, 4));
        engine.addRoute(new Route("3", pForest1, c2, RouteType.ROAD, 3));
        engine.addRoute(new Route("4", fBakery1, c1, RouteType.ROAD, 3));
        engine.addRoute(new Route("5", fBakery1, pForest1, RouteType.ROAD, 3));
        engine.addRoute(new Route("6", pForest1, fIKEA1, RouteType.ROAD, 3));
        engine.addRoute(new Route("7", fIKEA1, c2, RouteType.ROAD, 2));
        engine.addRoute(new Route("8", fBakery1, c3, RouteType.ROAD, 6));
        engine.addRoute(new Route("9", pFarm1, fBakery1, RouteType.ROAD, 4));
        engine.addRoute(new Route("10", fIKEA1, pMine1, RouteType.ROAD, 3));
        engine.addRoute(new Route("11", pFarm1, pMine1, RouteType.ROAD, 3));
        engine.addRoute(new Route("12", c3, pFarm1, RouteType.ROAD, 4));
        engine.addRoute(new Route("13", pFarm1, pForest2, RouteType.ROAD, 4));
        engine.addRoute(new Route("14", pForest2, pMine1, RouteType.ROAD, 5));
        engine.addRoute(new Route("15", c3, pForest2, RouteType.ROAD, 4));
        engine.addRoute(new Route("16", fOIL1, c3, RouteType.ROAD, 2));
        engine.addRoute(new Route("17", c3, fIKEA2, RouteType.ROAD, 4));
        engine.addRoute(new Route("18", pForest2, fIKEA2, RouteType.ROAD, 2));
        engine.addRoute(new Route("19", fIKEA2, c4, RouteType.ROAD, 5));
        engine.addRoute(new Route("20", fBakery2, c4, RouteType.ROAD, 2));
        engine.addRoute(new Route("21", pFarm2, fBakery2, RouteType.ROAD, 2));
        engine.addRoute(new Route("22", pOil1, fOIL1, RouteType.ROAD, 2));
        engine.addRoute(new Route("23", c1, fOIL1, RouteType.RAIL, 15));
        engine.addRoute(new Route("24", pMine1, fScrew1, RouteType.RAIL, 10));
        engine.addRoute(new Route("25", c2, c4, RouteType.RAIL, 10));
    }
}
