package org.example.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class City extends Location{
    private final Set<CargoType> demands;



    public City(String id, String displayName){
        super(id, displayName);
        this.demands = new HashSet<>();
    }

    public void addDemands(CargoType cargo){
        if (cargo!= null){
            this.demands.add(cargo);
        }
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
                takeCargo(demand, amount);
            }
        }
    }

}
