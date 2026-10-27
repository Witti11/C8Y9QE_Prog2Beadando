package org.example.command;

import org.example.model.Company;
import org.example.model.Route;
import org.example.model.Vehicle;

public class AssignVehicleCommand implements Command {
    private final Vehicle vehicle;
    private final Route route;

    public AssignVehicleCommand(Vehicle vehicle, Route route) {
        this.vehicle = vehicle;
        this.route = route;
    }

    @Override
    public void execute() {
        try {
            route.addVehicle(vehicle);
            System.out.println("A(z) " + vehicle.getId() + " sikeresen hozzá lett adva a(z) " + route.getId() + " úthoz!");
        } catch (RuntimeException e) {
            System.out.println("Hiba a jármű hozzárendelésekor: " + e.getMessage());
        }
    }
}

