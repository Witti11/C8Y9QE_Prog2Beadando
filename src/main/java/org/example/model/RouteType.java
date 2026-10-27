package org.example.model;

public enum RouteType {
    ROAD("Közút", 400),
    RAIL("Vasút",2000);

    private final String displayName;
    private final int unlockCost;

    RouteType (String displayName, int unlockCost){
        this.displayName=displayName;
        this.unlockCost=unlockCost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getUnlockCost() {
        return unlockCost;
    }
}
