package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.exception.InsufficientFundsException;

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
            throw new IllegalArgumentException("A költség nem lehet nulla!");
        }
        if(!canAfford(cost)){
            throw new InsufficientFundsException("Nincs elég fedezet a tranzakcióhoz! Szükséges: " + cost + "$,egyenleg: " + this.money + "$");
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
