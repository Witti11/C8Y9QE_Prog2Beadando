package org.example.controller;

import org.example.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {
    private final Company company;
    private final List<Location> locations;
    private final List<Route> routes;
    private int currentTurn;

    public GameEngine(Company company){
        this.company = company;
        this.locations = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.currentTurn=1;
    }

    public void addLocation(Location location){
        if (location!=null){
            this.locations.add(location);
        }
    }

    public void addRoute(Route route){
        if(route!=null){
            this.routes.add(route);
        }
    }

    public Company getCompany() {
        return company;
    }

    public List<Location> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    public int getCurrentTurn() {
        return currentTurn;
    }
    public void moveVehicles(){
        for(Vehicle v : company.getOwnedVehicles()){
            Route route = v.getAssignedRoute();
            if(route==null){
                continue;
            }
            Location source = route.getSource();
            Location dest = route.getDestination();
            CargoType cargo = route.getAssignedCargo();
            double distance = route.getDistance();

            if(v.isMovingForward()){
                if(v.getCurrentPosition() == 0){//loading
                    if(cargo!=null) {
                        int space = v.getCapacity() - v.getCurrentAmount();
                        int taken = source.takeCargo(cargo, space);
                        if(taken>0){
                            v.loadCargo(cargo,taken);
                        }
                    }
                }
                //moving
                v.setCurrentPosition(v.getCurrentPosition()+v.getSpeed());

                if(v.getCurrentPosition()>=distance){//unloading
                    int delivered = v.unloadCargo();
                    if(delivered>0 && cargo != null){
                        dest.addCargo(cargo,delivered);
                        int reward=delivered*cargo.getBaseValue();
                        company.addMoney(reward);
                    }
                    v.setCurrentPosition(distance);
                    v.setMovingForward(false);
                }
            }
            else{
                v.setCurrentPosition(v.getCurrentPosition()-v.getSpeed());
                if(v.getCurrentPosition()<=0){
                    v.setCurrentPosition(0);
                    v.setMovingForward(true);
                }
            }
        }

    }

    public void nextTurn(){
        int maintenance = company.calculateTotalMaintenanceCost();
        company.spendMoney(maintenance);

        moveVehicles();

        for(Location loc : locations){
            loc.processTurn();
        }

        this.currentTurn++;
    }
}
