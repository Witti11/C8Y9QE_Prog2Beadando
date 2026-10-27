package org.example.model;

public enum TruckType {
    V1(0.5, 50, 3, 200),
    V2(1.0, 55, 3, 250),
    V3(1.0, 60, 5, 300),
    V4(2.0, 60, 3, 400),
    V5(2.0, 80, 5, 550);

    private final double speed;
    private final int maintenanceCost;
    private final int capacity;
    private final int price;

    TruckType(double speed, int maintenanceCost, int capacity, int price){
        this.speed=speed;
        this.maintenanceCost=maintenanceCost;
        this.capacity=capacity;
        this.price=price;
    }

    public double getSpeed() {
        return speed;
    }

    public int getMaintenanceCost() {
        return maintenanceCost;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPrice() {
        return price;
    }
}
