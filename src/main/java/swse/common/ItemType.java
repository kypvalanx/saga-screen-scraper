package swse.common;

public enum ItemType {
    TRAIT,SPECIES,CLASS, ITEM, LEVEL, FEAT, TALENT, VEHICLE_SYSTEM("vehicleSystem"), VEHICLE_BASE_TYPE("vehicleBaseType"), TEMPLATE;


    String toStringOverride = null;
    ItemType(String vehicleSystem) {
        toStringOverride = vehicleSystem;
    }

    ItemType() {

    }

    @Override
    public String toString() {
        if(toStringOverride != null) {
            return toStringOverride;
        }
        return super.name().toLowerCase();
    }
}
