package swse.vehicles.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.Change;
import swse.common.ChangeKey;
import swse.common.BaseExporter;
import swse.common.ItemType;
import swse.common.JSONy;
import swse.common.Modification;
import swse.common.NamedCrew;
import swse.common.ProvidedItem;

public class VehicleExporter extends BaseExporter {
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\Vehicles.json";
    public static final List<String> SYSTEMS = List.of("+3 Added Power Couplings", "+2 Added Power Couplings", "+1 Added Power Couplings", "Amphibious Seals", "Light Anti-Air Concussion Missile Launcher", "Medium Anti-Air Concussion Missile Launcher", "Medium Anti-Air Concussion Missiles", "Light Anti-Air Concussion Missiles", "Anti-Air Proton Torpedoes", "Anti-Air Proton Torpedo Launcher", "Anti-Aircraft Flak Pod", "Anti-Boarding Systems", "Standard Atmospheric Thrusters", "Advanced Atmospheric Thrusters", "Autoblaster", "Advanced Automated Weapon Emplacement", "Basic Automated Weapon Emplacement", "+6 Auxiliary Generators", "+4 Auxiliary Generators", "+2 Auxiliary Generators", "Backup Battery", "Baffled Drive", "Heavy Blaster Cannon", "Medium Blaster Cannon", "Light Blaster Cannon", "Bubble Wort Projector", "Standard Cannon Enhancements", "Advanced Cannon Enhancements", "Triple Cannons", "Quad", "Double", "Autofire", "Dual", "Twin", "Triple", "Enhanced", "Cargo Jettison System", "Medium Cargo Pod", "Light Cargo Pod", "Heavy Cargo Pod", "Additional Emplacement Points from Cargo Pods", "Chaff Gun", "Chaff Projector", "Climbing Clamps", "Stygium Cloaking Device", "Hibridium Cloaking Device", "Cockpit Ejection System", "Com Jammers", "Combat Thrusters", "Composite Beam Cannon", "Composite Homing Laser", "Composite Laser", "Composite Prismatic Turbolaser Cannon", "Wide-Blast Missile", "Concussion Bomb", "Armor-Piercing Concussion Missiles", "EMP Missile", "Heavy Concussion Missile Launcher", "Light Concussion Missile Launcher", "Medium Concussion Missile Launcher", "Ionized Cluster Missile", "Diamond Boron Missile", "Homebrew Concussion Missiles", "Light Concussion Missiles", "Medium Concussion Missiles", "Heavy Concussion Missiles", "Narrow-Blast Missile", "Radiation-Burst Missile", "Super-Heavy Concussion Missile ", "Heavy Concussion Mortar Shells", "Standard Concussion Mortar Shells", "Standard Concussion Mortar Cannon", "Heavy Concussion Mortar Cannon", "Concussion Torpedoes", "Concussion Torpedo Tube", "Cotterdam", "Cruise Missiles", "Cruise Missile Launcher", "Cryogenic Chambers", "Decimator Beam", "Defoliator Shell", "Defoliator Launcher", "Discord Missiles", "Discord Missile Launcher", "Docking Clamps", "Docking Gun", "Droid Bay", "Miniaturized Droid Control Module", "Advanced Droid Control Module", "Standard Droid Control Module", "Droid Jammer", "Droid Repair Team", "Energy Bomblet Chute", "Energy Bomblets", "Energy Bomblet Generator", "High-Yield Energy Shells", "Standard Energy Shells", "Bunker Buster Energy Shells", "Armor Piercing Energy Shells", "Energy Shell Projector", "Environmental Filters", "Large Lifeboats", "Large Lifeboat Escape Pod", "Small Lifeboat Escape Pod", "Escape Pod Statistics (CL 1)", "Abilities", "Small Lifeboats", "Defenses", "Offense", "Ship Statistics", "Extended Range", "Optional Fire-Linked Weapon", "2 Fire-Linked Weapon", "4 Fire-Linked Weapon", "Optional Fire-Link", "Flame-Retardant Turret", "Force Harvester", "Force Harvester Statistics (CL 10)", "Fuel Converters", "Gemcutter Sensor", "Grappler Mag", "Gravity Mine Launcher", "Gravity Mines", "Gravity Mine, Magnetic", "Gravity Well Projector", "Hailfire Missile Launcher", "Hailfire Missiles", "Concealed Hangars", "Homebrew Colossal Hangars", "Concealed Hangar Bay", "Standard Hangar Bay", "Additional Vehicles and Hangar Space", "Hardpoint", "Harpoon Gun", "Heavy Ordnance Launcher", "Hex Weaponry", "75% of Cargo Hidden Cargo Hold", "25% of Cargo Hidden Cargo Hold", "50% of Cargo Hidden Cargo Hold", "Maximum Security", "Jedi Security", "Homebrew Holding Cells", "25% of Passengers Holding Cells", "50% of Passengers Holding Cells", "75% of Passengers Holding Cells", "Homing Concussion Missiles", "Homing Concussion Missile Launcher", "+3 Hull Plating", "+1 Hull Plating", "+2 Hull Plating", "Hyperdrive Sleds", "Class 3 Hyperdrive", "Class 0.75 Hyperdrive", "Class 8 Hyperdrive", "Class 5 Hyperdrive", "Class 1.5 Hyperdrive", "Class 10 Hyperdrive", "Class 2 Hyperdrive", "Class 4 Hyperdrive", "Class 6 Hyperdrive", "Class 15 Hyperdrive", "Class 1 Hyperdrive", "Hyperdrive Rings", "Hyperdrives for Starfighters", "Class 2.5 Hyperdrive", "Class 0.9 Hyperdrive", "Class 0.5 Hyperdrive", "Class 7 Hyperdrive", "Class 9 Hyperdrive", "Class 12 Hyperdrive", "Class 14 Hyperdrive", "Class 16 Hyperdrive", "Class 18 Hyperdrive", "Class 20 Hyperdrive", "Class 24 Hyperdrive", "Class 25 Hyperdrive", "Class 30 Hyperdrive", "Interior Sensor Mask", "Interrogation Chamber", "Single Ion Bomb", "Rack Ion Bomb", "Medium Ion Cannon", "Light Ion Cannon", "Hapan Triple Ion Cannon", "Heavy Ion Cannon", "Ion Encumbrance System", "Ion Pulse Cannon", "Jamming Array", "Jamming Suite", "Force Shock Wave (Repulse)", "Force Reflexes (Surge)", "Force Shield", "Force Lightning", "Jedi Meditation Chair", "Light Laser Cannon", "Heavy Laser Cannon", "Medium Laser Cannon", "Laser Panel Array", "Turbolaser1 Lok Sand Laser", "Laser Cannon Lok Sand Laser", "Blaster Cannon Lok Sand Laser", "LR1K Sonic Cannon", "Basic Luxury Upgrade", "Extreme Luxury Upgrade", "Advanced Luxury Upgrade", "MagnaCaster", "+2 Maneuvering Jets", "+4 Maneuvering Jets", "+6 Maneuvering Jets", "Medium Mass Driver Cannon", "Heavy Mass Driver Cannon", "Light Mass Driver Cannon", "Standard Mass-Driver Cannon", "Heavy Mass-Driver Cannon", "Medical Suite", "MG-3 Mini-Concussion Missiles", "MG-3 Mini-Concussion Missile Launcher", "MG1-A Heavy Proton Torpedo", "MG1-A Heavy Proton Torpedo Launcher", "Microtractor-Pressor", "Nano-Missile System", "Standard Navicomputer", "Limited Navicomputer", "Advanced Navicomputer", "Navicomputers in Starfighters", "Orbital Autocannon", "Outer Coating", "Particle Beam Cannon", "Quarters Passenger Conversion", "Seating Passenger Conversion", "Personalized Controls", "Plasma Punch", "Plasma Torch", "Point-Defense", "Proton Bomb", "Proton Bomb Rack", "Proton Grenade Launcher", "Proton Grenades", "Proton Torpedo Launcher", "Proton Torpedoes", "Medium Proton Torpedoes", "Heavy Proton Torpedoes", "Rail Cannon", "Light Rail Cannon System", "Medium Rail Cannon System", "Heavy Rail Cannon System", "Regenerating Shields", "+20% Reinforced Bulkheads", "+30% Reinforced Bulkheads", "+10% Reinforced Bulkheads", "Standard Reinforced Chassis", "Boarding Reinforced Chassis", "Boarding Chassis", "Boarding Reinforced Keel", "Standard Reinforced Keel", "Security Bracing", "Seismic Emitter", "+2 Sensor Array Computer", "+4 Sensor Array Computer", "+6 Sensor Array Computer", "Sensor Baffling", "Sensor Decoy", "Sensor Enhancement Package", "Sensor Mask", "Shieldbuster Torpedo Launcher", "Shieldbuster Torpedoes", "Recall Slave Circuits", "Advanced Slave Circuits", "Basic Slave Circuits", "Smuggler's Compartments", "Heavy Space Mines", "Advanced Space Mines", "Standard Space Mines", "Standard Space Mine Launcher", "Heavy Space Mine Launcher", "Speed Booster", "+2 Starship Armor", "+1 Starship Armor", "+4 Starship Armor", "+3 Starship Armor", "SR 200 Starship Shields", "SR 70 Starship Shields", "SR 60 Starship Shields", "SR 15 Starship Shields", "SR 175 Starship Shields", "SR 100 Starship Shields", "SR 45 Starship Shields", "SR 30 Starship Shields", "SR 25 Starship Shields", "SR 20 Starship Shields", "SR 90 Starship Shields", "SR 55 Starship Shields", "SR 150 Starship Shields", "SR 125 Starship Shields", "SR 80 Starship Shields", "SR 50 Starship Shields", "SR 10 Starship Shields", "SR 35 Starship Shields", "SR 40 Starship Shields", "SubLight Accelerator Motor", "Speed 1 Square Sublight Drive", "Speed 6 Square Sublight Drive", "Speed 5 Square Sublight Drive", "Speed 4 Square Sublight Drive", "Speed 2 Square Sublight Drive", "Speed 3 Square Sublight Drive", "Tall Walker", "Towing Cable", "Tractor Beam", "Tractor Clamp", "Hyper Transceiver", "HoloNet Transceiver", "Masked Transponder", "Disguised (1) Transponder", "Disguised (2) Transponder", "Disguised (3) Transponder", "IFF Transponder", "Tug Thrusters", "Homebrew Rapidfire Turbolaser", "Heavy Turbolaser", "Medium Turbolaser", "Light Turbolaser", "Workshop", "Droid Socket", "Grenade Launcher", "Missile Launcher", "Frag Grenade", "Heavy Repeating Blaster", "Rapid-Fire", "Rapid-Repeating", "Battery", "Heavy Yaret-kor", "Medium Yaret-kor", "Light Yaret-kor", "Magma Missile", "Heavy Plasma Projector", "Medium Plasma Projector", "Light Plasma Projector", "Dovin Basal", "Stun Cannon", "Suppression Cannon", "Bomblet Generator", "Antivehicle Cannon", "Interceptor Missile", "Superlaser", "Volcano Cannon");


    public static final Pattern STRENGTH_PATTERN = Pattern.compile("Strength: ([\\d-]*)");
    public static final Pattern DEXTERITY_PATTERN = Pattern.compile("Dexterity: ([\\d-]*)");
    public static final Pattern INTELLIGENCE_PATTERN = Pattern.compile("Intelligence: ([\\d-]*)");
    public static final Pattern CONSTITUTION_PATTERN = Pattern.compile("Constitution: ([\\d-]*)");
    public static final Pattern SIZE_AND_SUBTYPE = Pattern.compile("^(Tiny|Fine|Diminuative|Small|Medium|Large|Huge|Gargantuan|Colossal|Colossal \\(Frigate\\)|Colossal \\(Cruiser\\)|Colossal \\(Station\\)) ([\\s\\w]*)(?:\\(([\\s\\w]* Template)\\))?");
    public static final Pattern DAMAGE_REDUCTION = Pattern.compile("Damage Reduction: (\\d*)");
    public static final Pattern SHIP_SCALE_SPEED = Pattern.compile("Fly (\\d*) Squares");
    public static final Pattern CHARACTER_SCALE_SPEED = Pattern.compile("Speed: Fly (\\d*) Squares");
    public static final Pattern MAXIMUM_VELOCITY = Pattern.compile("\\(Maximum Velocity ([,\\d]*) km/h\\)");
    public static final Pattern HYPERDRIVE_PATTERN = Pattern.compile("Hyperdrive: (Class [.\\d]*)(?: \\(Backup (Class [.\\d]*)\\))?");
    public static final Pattern CARGO_PATTERN = Pattern.compile("Cargo: (None|[\\d,.]*) ?(\\w*)?");
    public static final Pattern COVER_PATTERN = Pattern.compile("(Total Cover|\\+5 Cover Bonus|No Cover|\\+10 Cover Bonus) ?\\(?([\\s\\w]*)?\\)?");
    public static final Pattern CREW_PASSENGERS = Pattern.compile("Crew: ([\\d\\w,-]*|[\\d,]* to [\\d,]*)( plus Astromech Droid)? \\((\\w*) Crew Quality\\)(?:,|;)? (?:Passengers: )?([\\s\\w\\d()]*)");
    public static final Pattern CONSUMABLE_PATTERN = Pattern.compile("Consumables: ([\\d\\s\\w.*()-]*?)(?:;|$)");
    public static final Pattern HIT_POINT_PATTERN = Pattern.compile("Hit Points: ([,\\d]*)");
    public static final Pattern SHIELD_RATING_PATTERN = Pattern.compile("Shield Rating: ([,\\d]*)");
    public static final Pattern ARMOR_PATTERN = Pattern.compile("\\+(\\d*) Armor");
    public static final Pattern PAYLOAD_PATTERN = Pattern.compile("Payload: ([\\s\\w\\d]*)");
    private static Map<String, String> namedCrewPosition = new HashMap<>();

    public static void main(String[] args) {

        List<String> vehicleSystemLinks = new ArrayList<>(getAlphaLinks("/wiki/Category:Vehicles?from="));
        //vehicleSystemLinks.addAll(getAlphaLinks("/wiki/Category:Starship_Accessories?from="));


        List<JSONObject> entries = new VehicleExporter().getEntriesFromCategoryPage(vehicleSystemLinks, true);

        printUniqueNames(entries);

        System.out.println("processed " + entries.size() + " of 647");

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"), "Vehicles");
    }


    protected List<JSONy> parseItem(String itemLink, boolean overwrite, List<String> filter, List<String> nameFilter) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }
        List<String> categories = doc.select("li.category").stream().map(Element::text).collect(Collectors.toList());
        if(categories.contains("Mounts")){
            return new ArrayList<>();
        }


        Element title = doc.select("h1.page-header__title").first();

        if (title == null || title.text().trim().equalsIgnoreCase("AAT-2")) {
            return new ArrayList<>();
        }

//        if (!title.text().trim().equalsIgnoreCase("T-65B X-Wing Starfighter")) {
//            return new ArrayList<>();
//        }

//        if (!title.text().trim().equalsIgnoreCase("Protodeka Tank Droid")) {
//            return new ArrayList<>();
//        }
//        if (!title.text().trim().equalsIgnoreCase("A-24 Sleuth Scout Ship")) {
//            return new ArrayList<>();
//        }

        List<Vehicle> items = new LinkedList<>();

        String itemName = title.text().trim();
        Vehicle current = Vehicle.create(itemName);
        final ProvidedItem customTemplate = ProvidedItem.create("Custom", ItemType.VEHICLE_BASE_TYPE);
        current.with(customTemplate);
        items.add(current);

        String subHeader = "";

        for (Element cursor : doc.select("div.mw-parser-output").first().children()) {
            boolean found = false;


            final String text = cursor.text();
            Matcher matcher = STRENGTH_PATTERN.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.BASE_STRENGTH, matcher.group(1)));
                found = true;
            }
            matcher = DEXTERITY_PATTERN.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.BASE_DEXTERITY, matcher.group(1)));
                found = true;
            }
            matcher = INTELLIGENCE_PATTERN.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.BASE_INTELLIGENCE, matcher.group(1)));
                found = true;
            }
            matcher = SIZE_AND_SUBTYPE.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.VEHICLE_SUB_TYPE, matcher.group(2)));
                customTemplate.withProvided(ProvidedItem.create(matcher.group(1), ItemType.TRAIT));
                if(matcher.group(3)!=null) {
                    customTemplate.withProvided(ProvidedItem.create(matcher.group(3), ItemType.TEMPLATE));
                }
                found = true;
            }
            matcher = DAMAGE_REDUCTION.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.DAMAGE_REDUCTION, matcher.group(1)));
                found = true;
            }
            matcher = CHARACTER_SCALE_SPEED.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.SPEED_CHARACTER_SCALE, matcher.group(1)));
                found = true;
            }
            matcher = SHIP_SCALE_SPEED.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.SPEED_STARSHIP_SCALE, matcher.group(1)));
                found = true;
            }
            matcher = MAXIMUM_VELOCITY.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.MAXIMUM_VELOCITY, matcher.group(1)));
                found = true;
            }
            matcher = CREW_PASSENGERS.matcher(text);
            if(text.contains("Crew:")) {
                if (matcher.find()) {
                    customTemplate.withProvided(Change.create(ChangeKey.CREW, matcher.group(1)));
                    customTemplate.withProvided(Change.create(ChangeKey.CREW_QUALITY, matcher.group(3)));
                    customTemplate.withProvided(Change.create(ChangeKey.PASSENGERS, matcher.group(4)));
                    if (" plus Astromech Droid".equals(matcher.group(2))) {
                        customTemplate.withProvided(ProvidedItem.create("Droid Socket", ItemType.VEHICLE_SYSTEM).withEquip("installed"));
                    }
                    found = true;
                } else {
                    Pattern NAMED_CREW_PASSENGERS = Pattern.compile("Crew: (\\d*) \\(([\\w\\s,;-]*)\\); Passengers: (\\d*)");
                    Pattern NAMED_CREW_QUALITY_PATTERN = Pattern.compile("(\\w*) Crew Quality");
                    matcher = NAMED_CREW_PASSENGERS.matcher(text);
                    if(matcher.find()){
                        Matcher m2 = NAMED_CREW_QUALITY_PATTERN.matcher(text);
                        if(m2.find()){

                            customTemplate.withProvided(Change.create(ChangeKey.CREW_QUALITY, m2.group(1)));
                        }
                        customTemplate.withProvided(Change.create(ChangeKey.CREW, matcher.group(1)));
                        //printUnique(itemName, matcher.group(2));

                        for (NamedCrew namedCrew: resolveNamedCrew(matcher.group(2))){
                            customTemplate.withProvided(namedCrew);
                        }

                    } else {

                    }
                }
            }
            matcher = COVER_PATTERN.matcher(text);
            while (matcher.find()) {
                if (matcher.group(2) != null) {
                    customTemplate.withProvided(Change.create(ChangeKey.COVER, matcher.group(1) + ":" + matcher.group(2)));
                } else {
                    customTemplate.withProvided(Change.create(ChangeKey.COVER, matcher.group(1)));
                }
                found = true;
            }
            matcher = CARGO_PATTERN.matcher(text);
            if (matcher.find()) {
                final String value = matcher.group(1);
                final String unit = matcher.group(2);

                customTemplate.withProvided(Change.create(ChangeKey.CARGO_CAPACITY, toString(getKilograms(value, unit))));
                found = true;
            }


            matcher = HYPERDRIVE_PATTERN.matcher(text);
                if (matcher.find()) {
                    final String primary = matcher.group(1);
                    final String secondary = matcher.group(2);

                    customTemplate.withProvided(ProvidedItem.create(primary+" Hyperdrive", ItemType.VEHICLE_SYSTEM).withEquip("installed"));
                    if(secondary != null) {
                        customTemplate.withProvided(ProvidedItem.create(secondary + " Hyperdrive", ItemType.VEHICLE_SYSTEM).withEquip("installed"));
                    }

                    //customTemplate.withProvided(Attribute.create("cargoCapacity", toString(getKilograms(value, unit))));
                    found = true;
                }


            matcher = CONSUMABLE_PATTERN.matcher(text);
                if (matcher.find()) {
                    customTemplate.withProvided(Change.create(ChangeKey.CONSUMABLES, matcher.group(1)));
                    found = true;
                }
            matcher = HIT_POINT_PATTERN.matcher(text);
                if (matcher.find()) {
                    customTemplate.withProvided(Change.create(ChangeKey.HIT_POINT_EQ, matcher.group(1).replace(",", "")));
                    found = true;
                }
            matcher = SHIELD_RATING_PATTERN.matcher(text);
                    if (matcher.find()) {
                        customTemplate.withProvided(Change.create(ChangeKey.SHIELD_RATING, matcher.group(1).replace(",", "")));
                        found = true;
                    }

            matcher = ARMOR_PATTERN.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, matcher.group(1)));
                found = true;
            }

            matcher = PAYLOAD_PATTERN.matcher(text);
                if (matcher.find()) {
                    customTemplate.withProvided(Change.create(ChangeKey.PAYLOAD, matcher.group(1)));
                    found = true;
                }

            final String input = nameCleanup(text);
            if ("h3".equalsIgnoreCase(cursor.tagName())) {
                subHeader = input;
                found = true;
                //printUnique(header);
            } else if ("Weapon Systems".equalsIgnoreCase(subHeader) && "h4".equalsIgnoreCase(cursor.tagName())) {
                //for each weapon i want to extract the name of the intended weapon, and the stats used on the og stat block for possible override.
                //that should be added to an object that describes the slot to add it to


                Element sibling = cursor.nextElementSibling();

                Pattern pattern = Pattern.compile("([\\w\\s-]*)(?:\\(([\\w\\s-]*)\\))?(?:, ([\\w\\s-]*))?(?:, ([\\w\\s-]*))?(?:, ([\\w\\s-]*))?(?: \\(([\\w\\s]*)\\))");

                List<String> properNames = List.of("Boba Fett", "Sirona Okeefe","Cade Skywalker","Jariah Syn","Deliah Blue");

                Matcher m = pattern.matcher(input);
                if (m.find()) {
                    //System.out.println(itemName);
                    String crewPosition = m.group(6);
                    int positions = 0;
                    if(crewPosition.contains(" ") && !crewPosition.contains(" or ") && !properNames.contains(crewPosition)){
                        String[] toks = crewPosition.split(" ");
                        try {
                            positions = Integer.parseInt(toks[0]);
                        } catch (NumberFormatException e){
                            System.err.println(crewPosition);
                        }
                        crewPosition = toks[1];

                    }
                    final String weaponName = m.group(1);
                    final String overrideDescription = sibling.text();
                    final List<String> modifiers = new ArrayList<>();
                    addNonNullObject(modifiers, m.group(3));
                    addNonNullObject(modifiers, m.group(4));
                    addNonNullObject(modifiers, m.group(5));
                    ProvidedItem weapon = resolveWeapon(weaponName, overrideDescription, itemName, modifiers, crewPosition, positions);

                    customTemplate.withProvided(weapon);
                    //printUnique(text.chars().filter(ch -> ch == ',').count());
                    //printUnique(text.chars().filter(ch -> ch == ',').count(), itemName, m.group(2), m.group(3), m.group(4));//, weapon);
                } else {
                    //printUnique(nameCleanup(cursor.text())); //itemName + " " +
                }
            }

            if (!found) {
                //printUnique(cursor.text());
            }

        }


        return new ArrayList<>(items);
    }

    private static List<NamedCrew> resolveNamedCrew(String group) {
        List<String> seperated;
        if(group.contains("; ")){
            seperated = Arrays.asList(group.split("; "));
        } else if(group.contains(", ")){
            seperated = Arrays.asList(group.split(", "));
        } else if(group.contains(" and ")){
            seperated = Arrays.asList(group.split(" and "));
        } else {
            seperated = List.of(group);
        }
        List<NamedCrew> response = new LinkedList<>();
        for(String entry : seperated){
            if(entry.contains("Crew Quality")){
                continue;
            }
            final NamedCrew e = NamedCrew.create(entry);

            namedCrewPosition.put(e.getName(), e.getPosition());
            response.add(e);
        }


        return response;
    }

    private static void addNonNullObject(List<String> modifiers, String modifier) {
        if (modifier != null) {
            modifiers.add(modifier);
        }
    }

    private static String resolveInstallationPoint(String group) {
        if(group == null){
            return "installed";
        }
        if(namedCrewPosition.containsKey(group)){
            group = namedCrewPosition.get(group);
        }

        if(group.contains(" or ")){
            group = group.split(" or ")[0];
        }
        if(group.equals("Gunners")){
            group = "Gunner";
        }
        //printUnique("CREW: "+group);
        return group.toLowerCase()+"Installed";
    }

    private static ProvidedItem resolveWeapon(String group, String damage, String itemName, List<String> modifiers, String crewPosition, int positions) {
        Pattern DAMAGE_OVERRIDE_PATTERN = Pattern.compile("Damage: ([\\s\\d\\w]*)");
        Matcher m = DAMAGE_OVERRIDE_PATTERN.matcher(damage);

        if(group.startsWith("Advanced ")){
            group = group.substring(9);
            modifiers.add("Advanced");
        }
        if(group.startsWith("Dual ")){
            group = group.substring(5);
            modifiers.add("Dual");
        }
        if(group.endsWith(" with Burst Fire")){
            group = group.substring(0, group.length() - 16);
            modifiers.add("Burst Fire");
        }

        ProvidedItem vehicleWeapon = ProvidedItem.create(cleanWeaponName(group, itemName), ItemType.VEHICLE_SYSTEM);
        String installationPoint = resolveInstallationPoint(crewPosition);
        vehicleWeapon.withEquip(installationPoint);

        if(damage.contains("Damage:")){
            if(m.find()){
                vehicleWeapon.overwriteProvided(Change.create(ChangeKey.DAMAGE, m.group(1)));
            } else {
                //printUnique(damage); TODO weapons that are harder to aim at small targets
            }
        }

        //TODO make these into mods
        for (String modifier : modifiers) {
            vehicleWeapon.withProvided(Change.create(ChangeKey.SUFFIX, ", "+modifier));
            vehicleWeapon.withProvided(Modification.create(ProvidedItem.create(modifier, ItemType.VEHICLE_SYSTEM)));
            switch (modifier) {
                case "Rapid-Fire":
                    //vehicleWeapon.withProvided(Attribute.create("autofireAttackBonus", "+3"));
                    break;
                case "Rapid-Repeating":
                    //vehicleWeapon.withProvided(Attribute.create("autofireAttackBonus", "0"));
                    break;
                case "Enhanced":
                case "Triple":
                case "Double":
                case "Dual":
//                    vehicleWeapon.withProvided(Attribute.create("bonusDamage", "1d10"));
//                    vehicleWeapon.withProvided(Attribute.create("cost", "*3"));
//                    vehicleWeapon.withProvided(Attribute.create("autofireAttackBonus", "0"));
                    break;
                case "Quad":
//                    vehicleWeapon.withProvided(Attribute.create("bonusDamage", "2d10"));
//                    vehicleWeapon.withProvided(Attribute.create("cost", "*5"));
//                    vehicleWeapon.withProvided(Attribute.create("autofireAttackBonus", "0"));
                    break;
                case "Battery":
                    for(int i = 1; i < positions; i++) {
                        vehicleWeapon.withProvided(Change.create(ChangeKey.PROVIDES_SLOT, crewPosition));
                    }


                default:
                    //printUnique(itemName + " " + modifier);


            }

            //vehicleWeapon.withProvided(Attribute.create());
            //printUnique(itemName + toks[i]);
        }

        return vehicleWeapon;
    }

    private static String cleanWeaponName(String name, String itemName) {
        name = name.trim();
        //name = name.replaceAll("Turret", "Cannon");
        name = name.replaceAll("Cannons", "Cannon");
        //name = name.replaceAll("Blaster", "Laser");
        name = name.replaceAll("Turbolasers", "Turbolaser");


        name = name.equals("Concussion Missile Launchers") ? "Medium Concussion Missile Launcher" : name;
        name = name.equals("Light Concussion Missile Launchers") ? "Light Concussion Missile Launcher" : name;
        name = name.equals("Turbolaser Turret") ? "Medium Turbolaser" : name;
        name = name.equals("Ion Bomb") ? "Single Ion Bomb" : name;
        name = name.equals("Proton Torpedo Salvo") ? "Proton Torpedo Launcher" : name;
        name = name.equals("Suppression Cannons") ? "Suppression Cannon" : name;
        name = name.equals("Missile Launchers") ? "Missile Launcher" : name;
        name = name.equals("MG1-A Heavy Proton Torpedoes") ? "MG1-A Heavy Proton Torpedo" : name;
        name = name.equals("Heavy Laser Turret") ? "Heavy Laser Cannon" : name;
        name = name.equals("Laser Cannon Turrets") ? "Medium Laser Cannon" : name;
        name = name.equals("Laser Cannon Turret") ? "Medium Laser Cannon" : name;
        name = name.equals("Medium Laser Turret") ? "Medium Laser Cannon" : name;
        name = name.equals("Heavy Repeating Blasters") ? "Heavy Repeating Blaster" : name;
        name = name.equals("Heavy Proton Torpedo") ? "Heavy Proton Torpedoes" : name;
        name = name.equals("Proton Torpedo") ? "Proton Torpedoes" : name;
        name = name.equals("Grenade Launchers") ? "Grenade Launcher" : name;
        name = name.equals("Proton Grenade Launchers") ? "Proton Grenade Launcher" : name;
        name = name.equals("Space Mines") ? "Standard Space Mines" : name;
        name = name.equals("Heavy Concussion Missile") ? "Heavy Concussion Missiles" : name;
        name = name.equals("Repeating Blaster Cannon") ? "Light Blaster Cannon" : name;
        name = name.equals("Mass-Driver Cannon") ? "Standard Mass-Driver Cannon" : name;
        name = name.equals("Laser Cannon") ? "Medium Laser Cannon" : name;
        name = name.equals("Concussion Missiles") ? "Medium Concussion Missiles" : name;
        name = name.equals("Medium Concussion Missile") ? "Medium Concussion Missiles" : name;
        name = name.equals("Concussion Missile Launcher") ? "Medium Concussion Missile Launcher" : name;
        name = name.equals("Anti-Air Concussion Missile Launcher") ? "Medium Anti-Air Concussion Missile Launcher" : name;
        name = name.equals("Ion Cannon") ? "Medium Ion Cannon" : name;
        name = name.equals("Turbolaser") ? "Medium Turbolaser" : name;
        name = name.equals("Blaster Cannon") ? "Medium Blaster Cannon" : name;
        if (!SYSTEMS.contains(name) && !List.of("Dovin Basal", "Dovin Basal (Tractor Beam)", "Heavy Yaret-kor").contains(name)) {
            //printUnique(itemName + " : " + name);
            //printUnique(name);

        }
        return name;
    }

    private static String nameCleanup(String name) {
        return name.replaceAll("\\[]", "").replaceAll("\\*", "");
    }

}
