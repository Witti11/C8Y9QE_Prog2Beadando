package org.example.command;

import org.example.controller.GameEngine;
import org.example.persistence.SaveManager;

import java.io.IOException;

public class SaveGameCommand implements Command{
    private final GameEngine engine;
    private final String saveName;
    private final SaveManager saveManager;

    public SaveGameCommand(GameEngine engine, String saveName){
        this.engine=engine;
        this.saveName=saveName;
        this.saveManager = new SaveManager();
    }
    @Override
    public void execute(){
        try {
            saveManager.saveGame(engine,saveName);
            System.out.println("Játék sikeresen mentve: "+saveName);
        } catch (IOException e) {
            System.out.println("Hiba történt a mentés során: "+e.getMessage());
        }
    }
}
