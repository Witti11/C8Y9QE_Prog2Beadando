package org.example.model;

public enum TrainType {
    V1(5,100,10,900),
    V2(6,150,10,1000),
    V3(6,200,13,1500);

    private final double speed;
    private final int maintenanceCost;
    private final int capacity;
    private final int price;

    TrainType(double speed, int maintenanceCost, int capacity, int price){
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
