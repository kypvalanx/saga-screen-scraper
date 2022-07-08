package swse.units;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.*;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static swse.vehicles.stock.baseType.vehicles.models.VehicleExporter.SYSTEMS;

public class BeastExporter extends BaseExporter {
    public static final String JSON_OUTPUT = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems\\swse\\raw_export\\Beasts.json";


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

        List<String> nonHeroicUnits = new ArrayList<>(getAlphaLinks("/wiki/Category:Nonheroic_Units?from="));
        List<String> heroicUnits = new ArrayList<>(getAlphaLinks("/wiki/Category:Heroic_Units?from="));
        //vehicleSystemLinks.addAll(getAlphaLinks("/wiki/Category:Starship_Accessories?from="));


        List<JSONObject> entries = new BeastExporter().getEntriesFromCategoryPage(nonHeroicUnits);
        entries.addAll(new BeastExporter().getEntriesFromCategoryPage(heroicUnits));


        System.out.println("processed " + entries.size() + " of 647");

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }


    protected List<JSONy> parseItem(String itemLink, boolean overwrite) {
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

        List<Beast> items = new LinkedList<>();

        String itemName = title.text().trim();
        Beast current = Beast.create(itemName);
        final ProvidedItem customTemplate = ProvidedItem.create("Custom", ItemType.VEHICLE_BASE_TYPE);
        current.withProvided(customTemplate);
        items.add(current);

        String subHeader = "";

        for (Element cursor : doc.select("div.mw-parser-output").first().children()) {
            boolean found = false;


            final String text = cursor.text();
            Matcher matcher = STRENGTH_PATTERN.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.BASE_STRENGTH, matcher.group(1)));
                found = true;
            }
            matcher = DEXTERITY_PATTERN.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.BASE_DEXTERITY, matcher.group(1)));
                found = true;
            }
            matcher = INTELLIGENCE_PATTERN.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.BASE_INTELLIGENCE, matcher.group(1)));
                found = true;
            }
            matcher = SIZE_AND_SUBTYPE.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.VEHICLE_SUB_TYPE, matcher.group(2)));
                customTemplate.withProvided(ProvidedItem.create(matcher.group(1), ItemType.TRAIT));
                if(matcher.group(3)!=null) {
                    customTemplate.withProvided(ProvidedItem.create(matcher.group(3), ItemType.TEMPLATE));
                }
                found = true;
            }
            matcher = DAMAGE_REDUCTION.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.DAMAGE_REDUCTION, matcher.group(1)));
                found = true;
            }
            matcher = CHARACTER_SCALE_SPEED.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.SPEED_CHARACTER_SCALE, matcher.group(1)));
                found = true;
            }
            matcher = SHIP_SCALE_SPEED.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.SPEED_STARSHIP_SCALE, matcher.group(1)));
                found = true;
            }
            matcher = MAXIMUM_VELOCITY.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.MAXIMUM_VELOCITY, matcher.group(1)));
                found = true;
            }
            matcher = CREW_PASSENGERS.matcher(text);
            if(text.contains("Crew:")) {
                if (matcher.find()) {
                    customTemplate.withProvided(Attribute.create(AttributeKey.CREW, matcher.group(1)));
                    customTemplate.withProvided(Attribute.create(AttributeKey.CREW_QUALITY, matcher.group(3)));
                    customTemplate.withProvided(Attribute.create(AttributeKey.PASSENGERS, matcher.group(4)));
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

                            customTemplate.withProvided(Attribute.create(AttributeKey.CREW_QUALITY, m2.group(1)));
                        }
                        customTemplate.withProvided(Attribute.create(AttributeKey.CREW, matcher.group(1)));
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
                    customTemplate.withProvided(Attribute.create(AttributeKey.COVER, matcher.group(1) + ":" + matcher.group(2)));
                } else {
                    customTemplate.withProvided(Attribute.create(AttributeKey.COVER, matcher.group(1)));
                }
                found = true;
            }
            matcher = CARGO_PATTERN.matcher(text);
            if (matcher.find()) {
                final String value = matcher.group(1);
                final String unit = matcher.group(2);

                customTemplate.withProvided(Attribute.create(AttributeKey.CARGO_CAPACITY, toString(getKilograms(value, unit))));
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
                    customTemplate.withProvided(Attribute.create(AttributeKey.CONSUMABLES, matcher.group(1)));
                    found = true;
                }
            matcher = HIT_POINT_PATTERN.matcher(text);
                if (matcher.find()) {
                    customTemplate.withProvided(Attribute.create(AttributeKey.HIT_POINT_EQ, matcher.group(1).replace(",", "")));
                    found = true;
                }
            matcher = SHIELD_RATING_PATTERN.matcher(text);
                    if (matcher.find()) {
                        customTemplate.withProvided(Attribute.create(AttributeKey.SHIELD_RATING, matcher.group(1).replace(",", "")));
                        found = true;
                    }

            matcher = ARMOR_PATTERN.matcher(text);
            if (matcher.find()) {
                customTemplate.withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, matcher.group(1)));
                found = true;
            }

            matcher = PAYLOAD_PATTERN.matcher(text);
                if (matcher.find()) {
                    customTemplate.withProvided(Attribute.create(AttributeKey.PAYLOAD, matcher.group(1)));
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
                vehicleWeapon.overwriteProvided(Attribute.create(AttributeKey.DAMAGE, m.group(1)));
            } else {
                //printUnique(damage); TODO weapons that are harder to aim at small targets
            }
        }

        //TODO make these into mods
        for (String modifier : modifiers) {
            vehicleWeapon.withProvided(Attribute.create(AttributeKey.SUFFIX, ", "+modifier));
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
                        vehicleWeapon.withProvided(Attribute.create(AttributeKey.PROVIDES_SLOT, crewPosition));
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
