package org.example.command;

import org.example.controller.GameEngine;

public class NextTurnCommand implements Command{
    private final GameEngine engine;

    public NextTurnCommand(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void execute() {
        engine.nextTurn();
        System.out.println("Kör léptetve! Aktuális kör: "+engine.getCurrentTurn());
    }
}
