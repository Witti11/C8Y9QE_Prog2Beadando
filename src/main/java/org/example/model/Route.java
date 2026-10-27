package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {
    private final String id;
    private final Location source;
    private final Location destination;
    private final RouteType type;
    private final double distance;

    private boolean unlocked;
    private CargoType assignedCargo;
    private final List<Vehicle> assignedVehicle;

    public Route(String id, Location source, Location destination, RouteType type, double distance) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.distance = distance;

        this.unlocked=false;
        this.assignedCargo = null;
        this.assignedVehicle = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Location getSource() {
        return source;
    }

    public Location getDestination() {
        return destination;
    }

    public RouteType getType() {
        return type;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public CargoType getAssignedCargo() {
        return assignedCargo;
    }

    public List<Vehicle> getAssignedVehicle() {
        return Collections.unmodifiableList(assignedVehicle);
    }

    public void unlock(){
        if (isUnlocked()){
            System.out.println("Már feloldott");
            return;
        }
        else{
            this.unlocked=true;
        }
    }

    public void setAssignedCargo(CargoType assignedCargo) {
        this.assignedCargo = assignedCargo;
    }

    public void addVehicle(Vehicle vehicle){
        if (vehicle==null){return;}
        if(!isUnlocked()){return;}
        if(!vehicle.canTravelOn(this.type)){return;}
        this.assignedVehicle.add(vehicle);
        return;
    }
    public boolean removeVehicle(Vehicle vehicle){
        return this.assignedVehicle.remove(vehicle);
    }
}
