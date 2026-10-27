package org.example.command;

import org.example.model.CargoType;
import org.example.model.Route;

public class SetRouteCargoCommand implements Command{
    private final Route route;
    private final CargoType cargo;

    public SetRouteCargoCommand(Route route, CargoType cargo){
        this.route=route;
        this.cargo=cargo;
    }

    @Override
    public void execute() {
        if(cargo==null){
            System.out.println("A rakomány nem lehet üres!");
            return;
        }
        route.setAssignedCargo(cargo);
        System.out.println("A(z) "+ route.getId()+" útvonalon szállított árú beállítva: "+cargo.getDisplayName());
    }
}
