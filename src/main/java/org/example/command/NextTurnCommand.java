package org.example.command;

import org.example.controller.GameEngine;
import org.example.exception.InsufficientFundsException;

public class NextTurnCommand implements Command{
    private final GameEngine engine;
    private boolean gameOver;

    public NextTurnCommand(GameEngine engine) {
        this.engine = engine;
        this.gameOver=false;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void execute() {
        try {

            engine.nextTurn();
            System.out.println("Kör léptetve! Aktuális kör: " + engine.getCurrentTurn());
        }catch (InsufficientFundsException e){
            System.out.println("\n===================================");
            System.out.println("  JÁTÉK VÉGE - A(Z) " +engine.getCompany().getCompanyName()+ " CSŐDBE MENT!  ");
            System.out.println("===================================");
            System.out.println("Nem tudtad fedezni a járműveid fenntartási költségét.");
            System.out.println("Elért körök száma: "+engine.getCurrentTurn());
            System.out.println("===================================");
            this.gameOver=true;
        }
    }
}
