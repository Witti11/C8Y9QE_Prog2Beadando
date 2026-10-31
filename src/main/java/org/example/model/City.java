package org.example.model;

import java.util.*;

public class City extends Location{
    private final Set<CargoType> demands;
    private final Map<CargoType, Integer> fulfilledDemands;


    public City(String id, String displayName){
        super(id, displayName);
        this.demands = new HashSet<>();
        this.fulfilledDemands=new HashMap<>();

    }

    public void addDemands(CargoType cargo){
        if (cargo!= null){
            this.demands.add(cargo);
        }
    }
    public void addfulfill(CargoType cargo, int amount){
        if (cargo != null && amount >0){
            int current = this.fulfilledDemands.getOrDefault(cargo,0);
            this.fulfilledDemands.put(cargo,current + amount);
        }
    }
    public int getFulfill(CargoType cargo){
        return fulfilledDemands.getOrDefault(cargo,0);
    }

    public boolean demands(CargoType cargo){
        return this.demands.contains(cargo);
    }

    public Set<CargoType> getDemands() {
        return Collections.unmodifiableSet(this.demands);
    }

    @Override
    public void processTurn() {

        for (CargoType demand : demands) {
            int amount = getStock(demand);
            if (amount > 0) {
                addfulfill(demand,amount);
                takeCargo(demand, amount);
            }
        }
    }

}
