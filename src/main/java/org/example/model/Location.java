package org.example.model;

import java.util.HashMap;
import java.util.Map;

public abstract class Location {
    private final String id;
    private final String displayName;
    private final Map<CargoType, Integer> inventory;

    public Location(String id, String displayName){
        this.id=id;
        this.displayName=displayName;
        this.inventory = new HashMap<>();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    //Inventory controll
    public int getStock(CargoType cargo){
        return inventory.getOrDefault(cargo,0);
    }

    public void addCargo(CargoType cargo, int amount) {
        if (cargo == null){
            throw new IllegalArgumentException("A rakomány típusa nem lehet null!");
        }
        if( amount <=0){
            throw new IllegalArgumentException("A hozzáadni kívánt mennyiség pozitív egész szám kell legyen!");
        }
        int currentAmount = getStock(cargo);
        inventory.put(cargo, currentAmount + amount);
    }
    public int takeCargo(CargoType cargo, int requestedAmount){
        if (cargo == null){
            throw new IllegalArgumentException("A rakomány típusa nem lehet null!");
        }
        if( requestedAmount <=0){
            throw new IllegalArgumentException("A felvenni kívánt mennyiség pozitív egész szám kell legyen!");
        }
        int currentStock = getStock(cargo);
        int toTake = Math.min(currentStock, requestedAmount);
        inventory.put(cargo,currentStock - toTake);
        return toTake;

    }

    //Next turn
    public abstract void processTurn();
}
