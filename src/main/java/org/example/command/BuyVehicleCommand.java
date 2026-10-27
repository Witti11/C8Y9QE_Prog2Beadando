package org.example.command;

import org.example.model.Company;
import org.example.model.Vehicle;

public class BuyVehicleCommand implements Command{
    private final Company company;
    private final Vehicle vehicle;

    public BuyVehicleCommand(Company company, Vehicle vehicle){
        this.company=company;
        this.vehicle=vehicle;
    }

    @Override
    public void execute() {
        int cost = vehicle.getPurchasePrice();

        try {company.spendMoney(cost);
            company.addVehicle(vehicle);
            System.out.println("Sikeres vásárlás! "+vehicle.getId()+" hozzáadva a flottához!");

        } catch (RuntimeException e) {
            System.out.println("Sikertelen vásárlás: "+e.getMessage());
        }
    }
}
