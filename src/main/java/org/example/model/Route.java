package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.exception.InvalidRouteException;
import java.io.Serializable;


public class Route implements Serializable {
    private final String id;
    private final Location source;
    private final Location destination;
    private final RouteType type;
    private final double distance;

    private boolean unlocked;
    private CargoType assignedCargo;
    private final List<Vehicle> assignedVehicle;
    private static final long serialVersionUID = 1L;

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
            throw new InvalidRouteException("Ez az útvonal (" + this.id + ") már fel van oldva!");
        }
        else{
            this.unlocked=true;
        }
    }

    public void setAssignedCargo(CargoType assignedCargo) {
        this.assignedCargo = assignedCargo;
    }

    public void addVehicle(Vehicle vehicle){
        if (vehicle==null) {
            throw new IllegalArgumentException("A jármű nem lehet null!");
        }
        if(!isUnlocked()){
            throw new InvalidRouteException("Nem lehet járművet hozzáadni egy még fel nem oldott útvonalhoz (" + this.id + ")!");
        }
        if(!vehicle.canTravelOn(this.type)){
            throw new InvalidRouteException("A jármű (" + vehicle.getId() + ") nem közlekedhet ezen a pályatípuson (" + this.type.getDisplayName() + ")!");
        }
        if(this.assignedCargo == null){
            throw new InvalidRouteException("Az útvonalhoz ("+this.id+") még nincs hozzárendelve rakomány!");
        }
        this.assignedVehicle.add(vehicle);
        vehicle.setAssignedRoute(this);
        return;
    }
    public boolean removeVehicle(Vehicle vehicle){
        if(this.assignedVehicle.remove(vehicle)){
            vehicle.setAssignedRoute(null);
            return true;
        }
        return false;
    }
}
