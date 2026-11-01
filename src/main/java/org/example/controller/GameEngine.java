package org.example.controller;

import org.example.model.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine implements Serializable{
    private final Company company;
    private final List<Location> locations;
    private final List<Route> routes;
    private int currentTurn;
    private static final long serialVersionUID = 1L;

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

    public int numOfUnlockedRoutes(){
        int num = 0;
        for (Route r: this.getRoutes()){
            if(r.isUnlocked()){
                num++;
            }
        }
        return num;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    private void handleEndpoint(Vehicle v, Location loc, CargoType cargo){
        if (cargo == null) return;

        //Unload
        if(v.getCurrentAmount()>0){
            int delivered = v.unloadCargo();
            loc.addCargo(cargo,delivered);

            boolean getsReward = false;
            if(loc instanceof City){
                City city =(City) loc;
                getsReward=city.demands(cargo);
            }else if(loc instanceof Factory){
                Factory factory = (Factory) loc;
                getsReward = (factory.getInputCargo() == cargo);
            }

            if(getsReward){
                int reward = delivered * cargo.getBaseValue();
                company.addMoney(reward);
            }
        }else {
            //Load
            int space = v.getCapacity() - v.getCurrentAmount();
            if (space > 0 && loc.getStock(cargo) > 0) {
                int taken = loc.takeCargo(cargo, space);
                if (taken > 0) {
                    v.loadCargo(cargo, taken);
                }
            }
        }
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
                    handleEndpoint(v, source, cargo);
                }
                //moving
                v.setCurrentPosition(v.getCurrentPosition()+v.getSpeed());

                if(v.getCurrentPosition()>=distance){//unloading
                    v.setCurrentPosition(distance);
                    handleEndpoint(v, dest, cargo);
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
