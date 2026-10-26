package org.example.model;

public enum CargoType {

    GRAIN("Búza", 50, true),
    WOOD("Farönk", 70, true),
    IRON("Vas", 80, true),
    OIL("Olaj", 100, true),
    BREAD("Kenyér", 150, false),
    FURNITURE("Bútor", 200, false),
    SCREW("Csavar", 300, false),
    PETROL("Benzin", 500, false);

    private final String displayName;
    private final int baseValue;
    private final boolean raw;

    CargoType(String displayName, int baseValue, boolean raw){
        this.displayName=displayName;
        this.baseValue=baseValue;
        this.raw=raw;
    }
    public String getDisplayName(){
        return this.displayName;
    }
    public int getBaseValue(){
        return this.baseValue;
    }
    public boolean isRaw(){
        return this.raw;
    }

}
