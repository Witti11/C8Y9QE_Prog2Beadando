package org.example.command;

import org.example.model.Company;
import org.example.model.Route;

public class UnlockRouteCommand implements Command{
    private final Company company;
    private final Route route;

    public UnlockRouteCommand(Company company, Route route){
        this.company=company;
        this.route=route;
    }

    @Override
    public void execute() {
        int cost = route.getType().getUnlockCost();
        try {
            if (route.isUnlocked()){
                route.unlock();
            }
            company.spendMoney(cost);
            route.unlock();
            System.out.println(route.getId() + " útvonal sikeresen feloldva!");

        } catch (RuntimeException e) {
            System.out.println("Sikertelen útfeloldás: "+ e.getMessage());
        }
    }
}
