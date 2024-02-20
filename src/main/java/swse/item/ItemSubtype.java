package swse.item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ItemSubtype {
    RIFLES("Rifles", "rifle", "rifles", "pistols, rifles"), //TODO we only support one type at this time.  it appears that all multi-type entries are homebrew
    ADVANCED_MELEE_WEAPONS("Advanced Melee Weapons", "advanced melee weapons"),
    EXOTIC_MELEE_WEAPONS("Exotic Melee Weapons", "exotic melee weapons", "exotic weapons (melee)"),
    EXOTIC_RANGED_WEAPONS("Exotic Ranged Weapons", "exotic ranged weapons", "exotic weapons (ranged)"),
    SIMPLE_MELEE_WEAPONS("Simple Melee Weapons", "simple melee weapons", "simple weapons (melee)", "simple weapon (melee)"),
    SIMPLE_RANGED_WEAPONS("Simple Ranged Weapons", "simple ranged weapons", "simple weapons (ranged)", "simple weapon (ranged)"),
    GRENADES("Grenades", "grenades"),
    LIGHTSABERS("Lightsabers", "lightsabers"),
    HEAVY_WEAPONS("Heavy Weapons", "heavy weapons"),
    PISTOLS("Pistols", "pistol", "pistols"),
    EQUIPMENT("Equipment", "equipment"),
    DROID_ACCESSORIES_SHIELD_GENERATOR_SYSTEMS("Droid Accessories (Shield Generator Systems)", "droid accessories (shield generator systems)"),
    DROID_ACCESSORIES_MISCELLANEOUS_SYSTEMS("Droid Accessories (Miscellaneous Systems)", "droid accessories (miscellaneous systems)"),
    DROID_ACCESSORIES_SENSOR_SYSTEMS("Droid Accessories (Sensor Systems)", "droid accessories (sensor systems)"),
    DROID_ACCESSORIES_TRANSLATOR_UNITS("Droid Accessories (Translator Units)", "droid accessories (translator units)"),
    DROID_ACCESSORIES_DROID_ARMOR("Droid Accessories (Droid Armor)", "droid accessories (droid armor)"),
    DROID_ACCESSORIES_COMMUNICATIONS_SYSTEMS("Droid Accessories (Communications Systems)", "droid accessories (communications systems)"),
    DROID_ACCESSORIES_DROID_STATIONS("Droid Accessories (Droid Stations)", "droid accessories (droid stations)"),
    ARMOR_UPGRADE("Armor Upgrade", "armor upgrade"),
    MEDICAL_GEAR("Medical Gear", "medical gear"),
    IMPLANTS("Implants", "implants"),
    TOOLS("Tools", "tools"),
    LIFE_SUPPORT("Life Support", "life support", "life support, survival gear"), //TODO we only support one type at this time.  it appears that all multi-type entries are homebrew
    SURVIVAL_GEAR("Survival Gear", "survival gear"),
    DETECTION_AND_SURVEILLANCE_DEVICES("Detection and Surveillance Devices", "detection and surveillance devices"),
    ADVANCED_CYBERNETICS("Advanced Cybernetics", "advanced cybernetics"),
    CYBERNETIC_DEVICES("Cybernetic Devices", "cybernetic devices"),
    WEAPON_UPGRADE("Weapon Upgrade", "weapon upgrade"),
    BIO_IMPLANTS("Bio-Implants", "bio-implants"),
    WEAPON_AND_ARMOR_ACCESSORIES("Weapon and Armor Accessories", "weapon and armor accessories"),
    COMPUTERS_AND_STORAGE_DEVICES("Computers and Storage Devices","computers and storage devices"),
    COMMUNICATIONS_DEVICES("Communications Devices", "communications devices"),
    EXPLOSIVES("Explosives", "explosives"),
    HEAVY_ARMOR("Heavy Armor", "heavy armor"),
    MEDIUM_ARMOR("Medium Armor", "medium armor"),
    LIGHT_ARMOR("Light Armor", "light armor"),
    HAZARD("Hazard", "hazard"),
    MINES("Mines", "mines"),
    SITH_ARTIFACTS("Sith Artifacts", "sith artifacts"),
    LOCOMOTION_SYSTEMS("Locomotion Systems", "locomotion systems"),
    PROCESSOR_SYSTEMS("Processor Systems","processor systems"),
    APPENDAGES("Appendages", "appendages"),
    UNIVERSAL_UPGRADE("Universal Upgrade", "universal upgrade");


    private final List<String> mapping;
    private final String normalizedString;

    ItemSubtype(String normalizedString, String... mapping) {
        this.normalizedString = normalizedString;
        this.mapping = Arrays.stream(mapping).map(String::toLowerCase).collect(Collectors.toList());
    }

    public static ItemSubtype getEnum(String trim) {
        trim = trim.trim().toLowerCase();
        for (ItemSubtype subType : ItemSubtype.values()){
            if(subType.mapping.contains(trim)){
                return subType;
            }
        }
        System.err.println("Unmapped type " + trim);
        return ItemSubtype.valueOf(trim);
    }

    public String toString(){
        return normalizedString;
    }
}
