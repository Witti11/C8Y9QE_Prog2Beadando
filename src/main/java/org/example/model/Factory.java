package org.example.model;

public class Factory extends Location{
    private final CargoType inputCargo;
    private final CargoType outputCargo;
    private final int processingRate;

    public Factory(String id, String displayName, CargoType inputCargo, CargoType outputCargo, int processingRate){
        super(id,displayName);
        this.inputCargo=inputCargo;
        this.outputCargo=outputCargo;
        this.processingRate=processingRate;
    }

    public int getProcessingRate() {
        return processingRate;
    }

    public CargoType getOutputCargo() {
        return outputCargo;
    }

    public CargoType getInputCargo() {
        return inputCargo;
    }

    @Override
    public void processTurn(){
        int availableInput = getStock(inputCargo);
        int toProcess = Math.min(availableInput,this.processingRate);

        if(toProcess>0){
            takeCargo(inputCargo, toProcess);
            addCargo(outputCargo,toProcess);
        }
    }
}
