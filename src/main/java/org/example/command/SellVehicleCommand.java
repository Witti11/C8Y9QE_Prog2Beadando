package org.example.command;

import org.example.model.Company;
import org.example.model.Vehicle;

public class SellVehicleCommand implements Command{
    private final Company company;
    private final Vehicle vehicle;

    public SellVehicleCommand(Company company,Vehicle vehicle){
        this.company=company;
        this.vehicle=vehicle;
    }

    @Override
    public void execute(){
        if(vehicle.getAssignedRoute()!=null){
            vehicle.getAssignedRoute().removeVehicle(vehicle);
        }
        company.sellVehicle(vehicle);

        int refund=vehicle.getPurchasePrice()/2;
        company.addMoney(refund);

        System.out.println("A(z) "+vehicle.getId()+" sikeresen eladva. "+refund+"$ visszatérítést kaptál!");
    }
}
