package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Company {
    private final String companyName;
    private int money;
    private final List<Vehicle> ownedVehicles;

    public Company(String name){
        this.companyName=name;
        this.money=2500;  //Starting money
        this.ownedVehicles = new ArrayList<>();
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getMoney() {
        return money;
    }

    public List<Vehicle> getOwnedVehicles() {
        return Collections.unmodifiableList(ownedVehicles);
    }

    public void addVehicle(Vehicle vehicle){
        if (vehicle != null){
            this.ownedVehicles.add(vehicle);
        }
    }
    public boolean sellVehicle(Vehicle vehicle){
        return this.ownedVehicles.remove(vehicle);
    }

    public boolean canAfford(int cost){
        if (cost>this.money){
            return false;
        }
        else {
            return true;
        }
    }

    public void addMoney(int income){
        this.money+=income;
    }
    public boolean spendMoney(int cost){
        if(cost <=0){
            return false;
        }
        if(!canAfford(cost)){
            System.out.println("Nincs elég fedezet!");
            return false;
        }
        this.money-=cost;
        return true;
    }
    public int calculateTotalMaintenanceCost() {
        int total = 0;
        for (Vehicle v : ownedVehicles) {
            total += v.getMaintenanceCost();
        }
        return total;
    }

}
