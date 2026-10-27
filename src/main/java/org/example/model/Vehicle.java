package org.example.model;

import org.example.exception.InvalidCargoException;

public abstract class Vehicle {
    private final String id;
    private final double speed;
    private final int maintenanceCost;
    private final int capacity;
    private final int purchasePrice;

    private CargoType currentCargo;
    private int currentAmount;

    public Vehicle(String id, double speed, int maintenanceCost, int capacity, int purPrice){
        this.id=id;
        this.speed=speed;
        this.maintenanceCost=maintenanceCost;
        this.capacity=capacity;
        this.purchasePrice=purPrice;
        this.currentCargo=null;
        this.currentAmount=0;
    }
    //getters
    public String getId(){
        return this.id;
    }
    public double getSpeed(){
        return this.speed;
    }
    public int getMaintenanceCost(){
        return this.maintenanceCost;
    }

    public int getCapacity() {
        return this.capacity;
    }
    public CargoType getCurrentCargo(){
        return this.currentCargo;
    }
    public int getCurrentAmount(){
        return this.currentAmount;
    }
    public int getPurchasePrice(){
        return this.purchasePrice;
    }
    //Cargo loader
    public int loadCargo(CargoType cargo, int amount){
        int loadAmount=0;
        if (amount<=0){
            throw new IllegalArgumentException("A felrakni kívánt mennyiség pozitív szám kell legyen!");
        }
        if(cargo==null){
            throw new IllegalArgumentException("A rakomány típusa nem lehet nulla!");
        }
        if(currentCargo!=null && cargo!=currentCargo){
            throw new InvalidCargoException("Nem lehet eltérő árut felrakni! A járművön már van: " + currentCargo.getDisplayName() + ", felrakni kívánt: " + cargo.getDisplayName());
        }
        int availableSpace=this.capacity-this.currentAmount;
        this.currentCargo=cargo;
        if(availableSpace>=amount){
            this.currentAmount+=amount;
            loadAmount=amount;
            return loadAmount;
        }
        else{//bc u can't load more in it
            loadAmount=availableSpace;
            this.currentAmount=this.capacity;
            return loadAmount;

        }
    }
    //Cargo unload
    public int unloadCargo(){
        int unload=this.currentAmount;
        this.currentAmount=0;
        this.currentCargo=null;
        return unload;
    }
    //RouteType
    public abstract RouteType getRequiredRouteType();

    public boolean canTravelOn(RouteType routeType){
        return getRequiredRouteType()==routeType;
    }
}
