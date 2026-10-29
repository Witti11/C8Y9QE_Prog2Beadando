package org.example.model;

public class Train extends Vehicle{
    private final TrainType type;

    public Train(String id, TrainType type){
        super(id, type.getSpeed(), type.getMaintenanceCost(), type.getCapacity(), type.getPrice());
        this.type=type;
    }

    public TrainType getType() {
        return type;
    }

    @Override
    public RouteType getRequiredRouteType(){
        return RouteType.RAIL;
    }

    @Override
    public  String getVersionName(){
        return this.type.name();
    }
}
