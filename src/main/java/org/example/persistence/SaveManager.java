package org.example.persistence;

import org.example.controller.GameEngine;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static final String SAVE_DIR = "saves";

    private void ensureDirectoryExists() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveGame(GameEngine engine, String saveName) throws IOException {
        ensureDirectoryExists();

        String fileName;
        if (saveName.endsWith(".dat")) {
            fileName = saveName;
        } else {
            fileName = saveName + ".dat";
        }
        File file = new File(SAVE_DIR, fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(engine);
        }
    }

    public GameEngine loadGame(String saveName) throws IOException, ClassNotFoundException {
        String fileName;
        if (saveName.endsWith(".dat")) {
            fileName = saveName;
        } else {
            fileName = saveName + ".dat";
        }
        File file = new File(SAVE_DIR, fileName);

        if (!file.exists()) {
            throw new FileNotFoundException("A megadott mentés nem létezik: " + fileName);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (GameEngine) ois.readObject();
        }
    }

    public List<String> getAvailableSaves() {
        ensureDirectoryExists();
        List<String> saveFiles = new ArrayList<>();
        File dir = new File(SAVE_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));

        if (files != null) {
            for (File f : files) {
                saveFiles.add(f.getName());
            }
        }
        return saveFiles;
    }
}
