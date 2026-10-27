package org.example.model;

public class Producer extends Location {
    private final CargoType outputCargo;
    private final int productionRate;

    public Producer(String id, String displayName,CargoType outputCargo, int productionRate){
        super(id,displayName);
        this.outputCargo=outputCargo;
        this.productionRate=productionRate;
    }

    public CargoType getOutputCargo() {
        return outputCargo;
    }

    public int getProductionRate() {
        return productionRate;
    }


    @Override
    public void processTurn(){
        addCargo(this.outputCargo,this.productionRate);

    }
}
