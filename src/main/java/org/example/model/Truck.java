package org.example.model;

public class Truck extends Vehicle{
    private final TruckType type;

    public Truck(String id, TruckType type){
        super(id, type.getSpeed(), type.getMaintenanceCost(), type.getCapacity(), type.getPrice());
        this.type=type;
    }

    public TruckType getType() {
        return type;
    }
}
