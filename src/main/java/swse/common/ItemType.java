package swse.common;

public enum ItemType {
    TRAIT,SPECIES,CLASS, ITEM, LEVEL, FEAT, TALENT, VEHICLE_SYSTEM("vehicleSystem"), VEHICLE_BASE_TYPE("vehicleBaseType"), TEMPLATE,
    LANGUAGE, BEAST_ATTACK("beastAttack"), SPECIES_TYPE("beastType"), AFFILIATION("affiliation"), FORCE_POWER("forcePower"), FORCE_SECRET("forceSecret"), FORCE_TECHNIQUE("forceTechnique"), FORCE_REGIMEN("forceRegimen"), BACKGROUND("background"), DESTINY("destiny");


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
