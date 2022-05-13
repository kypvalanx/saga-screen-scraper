package swse.vehicles.systems;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.BaseExporter;
import swse.common.JSONy;
import swse.prerequisite.Prerequisite;
import swse.prerequisite.SimplePrerequisite;
import swse.util.Context;
import static swse.util.Util.toEnumCase;

public class VehicleSystemsExporter extends BaseExporter {
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\Vehicle Systems.json";
    public static final Pattern HYPERDRIVE = Pattern.compile("Class ([\\d\\.]*) Hyperdrive");
    public static final String ASTROMECH_DROID = "Astromech Droid";
    public static final Pattern STARSHIP_SYSTEM_TYPE = Pattern.compile("(?:Starship Systems|Droid System|Weapon) Type: ([\\s\\w]*)");
    public static final Pattern SOURCE_PATTERN = Pattern.compile("Reference Book: ([\\w\\s-()]*)");
    public static final Pattern EMPLACEMENT_POINT_PATTERN = Pattern.compile("Emplacement Points: (\\d*)");
    public static final Pattern AVAILABILITY_PATTERN = Pattern.compile("Availability: ([\\w]*)");
    public static final Pattern COST_PATTERN = Pattern.compile("Cost: ([\\d]*)");
    public static final Pattern DAMAGE_PATTERN = Pattern.compile("Damage: ([\\d\\w\\s]*)(?:\\(([\\w\\s,]*)\\))?");
    public static final Pattern SEE_ALSO_PATTERN = Pattern.compile("See also: ([\\d\\w\\s]*)");
    public static final Pattern SIZE_REQUIREMENT_PATTERN = Pattern.compile("(?:Size Restriction|Size Requirement|Prerequisites): ([\\d\\w\\s]*)");

    public static void main(String[] args) {

        List<String> vehicleSystemLinks = new ArrayList<>(getAlphaLinks("/wiki/Category:Starship_Modifications?from="));
        //vehicleSystemLinks.addAll(getAlphaLinks("/wiki/Category:Starship_Accessories?from="));
        List<JSONObject> entries = new LinkedList<>();
        List<String> names = new LinkedList<>();
        VehicleSystemsExporter vehicleSystemsExporter = new VehicleSystemsExporter();
        boolean overwrite = false;
        for (String vehicleSystemLink :
                vehicleSystemLinks) {
            final List<JSONObject> newEntities = vehicleSystemsExporter.readItemMenuPage(vehicleSystemLink, overwrite);
            for (JSONObject newEntity : newEntities) {
                if (names.contains(newEntity.get("name"))) {
                    //System.out.println("Duplicate: " + newEntity.get("name") + " from: " + vehicleSystemLink);
                } else {
                    names.add((String) newEntity.get("name"));
                    entries.add(newEntity);
                }
            }
            drawProgressBar(entries.size() * 100.0 / 323.0);
        }
        List<JSONObject> newEntities = new ArrayList<>();
        newEntities.addAll(vehicleSystemsExporter.parseItem("/wiki/Droid_Socket", overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(vehicleSystemsExporter.parseItem("/wiki/Grenade_Launcher", overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(vehicleSystemsExporter.parseItem("/wiki/Missile_Launcher", overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(vehicleSystemsExporter.parseItem("/wiki/Frag_Grenade", overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(vehicleSystemsExporter.parseItem("/wiki/Heavy_Repeating_Blaster", overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Rapid-Fire").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Rapid-Repeating").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Battery").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Heavy Yaret-kor").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Medium Yaret-kor").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Light Yaret-kor").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Magma Missile").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Heavy Plasma Projector").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Medium Plasma Projector").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Light Plasma Projector").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Dovin Basal").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Stun Cannon").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Suppression Cannon").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Bomblet Generator").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Antivehicle Cannon").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Interceptor Missile").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Superlaser").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        newEntities.addAll(assembleFromNameAndChildren(null, "Volcano Cannon").stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));



        for (JSONObject newEntity : newEntities) {
            if (names.contains(newEntity.get("name"))) {
                //System.out.println("Duplicate: " + newEntity.get("name") + " from: " + vehicleSystemLink);
            } else {
                names.add((String) newEntity.get("name"));
                entries.add(newEntity);
            }
        }

        //entries.addAll(manualItems());

        System.out.println("processed " + entries.size() + " of 340");

        System.out.println("List.of(\"" + String.join("\", \"", names) + "\")");

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }


    private List<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
        if (null == itemPageLink) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemPageLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }


        List<String> hrefs = new LinkedList<>();

        Elements links = doc.select("a.category-page__member-link");
        links.forEach(a -> hrefs.add(a.attr("href")));


        return hrefs.stream().flatMap((Function<String, Stream<JSONy>>) itemLink -> parseItem(itemLink, overwrite).stream())
                .map(item -> item.toJSON())
                .collect(Collectors.toList());
    }

    protected List<JSONy> parseItem(String itemLink, boolean overwrite) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }
        Element title = doc.select("h1.page-header__title").first();

        if (title.text().trim().equalsIgnoreCase("walkers")) {
            return new ArrayList<>();
        }


        final Elements children = doc.select("div.mw-parser-output").first().children();

        String itemName = nameCleanup(title.text().trim());

        return assembleFromNameAndChildren(children, itemName);
    }

    private static List<JSONy> assembleFromNameAndChildren(Elements children, String itemName) {
        Context.setValue("name", itemName);
        List<VehicleSystem> items = new LinkedList<>();
        VehicleSystem current = VehicleSystem.create(itemName);

        Map<String, Map<String, VehicleSystem>> variantData = new HashMap<>();
        Map<String, VehicleSystem> variantSubData = variantData.computeIfAbsent(itemName, i -> new HashMap<>());
        variantSubData.put(itemName, current);
        boolean isVariant = false;


        if (children != null) {
            for (Element cursor : children) {
                boolean found = false;
                Element subItem = cursor.select(".mw-headline").first(); //Check if a variation of the object is being defined

                //Finds and creates variations from tables on the page
                if (cursor.hasClass("wikitable")) {
                    String firstTh = cursor.select("th").first().text();
                    if (itemName.toLowerCase().startsWith(firstTh.toLowerCase())) {
                        List<String> headers = getHeaders(cursor);
                        Elements rows = cursor.select("tr:has(td)");
                        for (Element row : rows) {

                            List<String> rowValues = row.select("td").stream().map(element -> element.text().trim()).collect(Collectors.toList());
                            String nameModifier = rowValues.get(0).replaceAll("\\*", "");

                            final String variantName = nameModifier + " " + itemName;
                            VehicleSystem finalCurrent = current;
                            variantSubData = variantData.computeIfAbsent(itemName, i -> new HashMap<>());
                            VehicleSystem currentVariant = variantSubData.computeIfAbsent(variantName, k -> finalCurrent.copy().withName(variantName));

                            if(rowValues.get(0).contains("*")){
                                currentVariant.withAsterisk(true);
                            }
                            //System.out.println(variantName);

                            if ("Added Power Couplings".equalsIgnoreCase(itemName)) {
                                currentVariant.withProvided(Attribute.create(AttributeKey.EMPLACEMENT_POINTS_BONUS, nameModifier));
                            }

                            for (int i = 1; i < headers.size(); i++) {
                                String key = headers.get(i);
                                String value = rowValues.get(i);
                                switch (key) {
                                    case "AVAILABILITY":
                                    case "AVAILABLE":
                                        currentVariant.withAvailability(value);
                                        break;
                                    case "EMPLACEMENT POINTS":
                                    case "DAMAGE":
                                            currentVariant.withProvided(Attribute.create(AttributeKey
                                                    .valueOf(toEnumCase(key.toLowerCase())), value));
                                        break;
                                    case "COST":
                                        currentVariant.withCost(value);
                                        break;
                                    case "SIZE RESTRICTION":
                                        currentVariant.withProvided(Prerequisite.create(value));
                                        break;
                                    default:
                                        //System.out.println("- " + key + " : " + value);
                                }
                                //System.out.println("- "+key + " : " + value);
                            }
                        }
                    } else {
                        //printUnique(itemName + " : " +firstTh);
                    }
                    continue;
                }
                if (subItem != null) {
                    itemName = subItem.text().trim();
                    final String singularItemName = itemName.substring(0, itemName.length() - 1);
                    final Map<String, VehicleSystem> flatMap = variantData.values().stream().flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    if (flatMap.containsKey(itemName)) {
                        current = flatMap.get(itemName);
                        isVariant = !variantData.containsKey(itemName) && !variantData.containsKey(singularItemName);
                    } else if (flatMap.containsKey(singularItemName)) {
                        current = flatMap.get(singularItemName);
                        isVariant = !variantData.containsKey(itemName) && !variantData.containsKey(singularItemName);
                    } else {
                        current = VehicleSystem.create(itemName);
                        isVariant = false;

                        variantSubData = variantData.computeIfAbsent(itemName, i -> new HashMap<>());
                        variantSubData.put(itemName, current);
                    }

                    continue;
                }


                Matcher m = STARSHIP_SYSTEM_TYPE.matcher(cursor.text());
                if (m.find()) {
                    found = true;
                    String substring = m.group(1);

                    if(substring.equals("Heavy Weapons") || substring.equals("Grenades")){
                        substring = "Weapon Systems";
                    }

                    if(isVariant){
                        current.withSubtype(substring);
                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            variant.withSubtype(substring);
                        }
                    }
                }

                m = SOURCE_PATTERN.matcher(cursor.text());
                if (m.find()) {
                    found = true;
                    if(isVariant){
                        current.withSource(m.group(1));
                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            variant.withSource(m.group(1));
                        }
                    }
                }

                m = EMPLACEMENT_POINT_PATTERN.matcher(cursor.text());

                if (m.find()) {
                    found = true;
                    if(isVariant){
                        current.withProvided(Attribute.create(AttributeKey.EMPLACEMENT_POINTS, m.group(1)));
                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            variant.withProvided(Attribute.create(AttributeKey.EMPLACEMENT_POINTS, m.group(1)));
                        }
                    }
                }


                m = AVAILABILITY_PATTERN.matcher(cursor.text());

                if (m.find()) {
                    found = true;
                    if(isVariant){
                        current.withAvailability(m.group(1));
                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            variant.withAvailability(m.group(1));
                        }
                    }
                }

                m = COST_PATTERN.matcher(cursor.text());
                if (m.find()) {
                    found = true;
                    if(isVariant){
                        current.withCost(m.group(1));
                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            variant.withCost(m.group(1));
                        }
                    }
                }


                m = DAMAGE_PATTERN.matcher(cursor.text());
                if (m.find()) {
                    found = true;
                    if(isVariant){

                        final Attribute damage = Attribute.create(AttributeKey.DAMAGE, m.group(1).trim());
                        if (m.group(2) != null) {
                            damage.withModifier(m.group(2).trim());
                        }
                        current.withProvided(damage);
                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            final Attribute damage = Attribute.create(AttributeKey.DAMAGE, m.group(1).trim());
                            if (m.group(2) != null) {
                                damage.withModifier(m.group(2).trim());
                            }
                            variant.withProvided(damage);
                        }

                    }
                }

                //printUnique(m.group(1));
                //printUnique(cursor.text());

                m = SEE_ALSO_PATTERN.matcher(cursor.text());
                if (m.find()) {
                    found = true;
                    if(isVariant){

                        current.withProvided(Attribute.create(AttributeKey.SEE_ALSO, m.group(1)));
                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            variant.withProvided(Attribute.create(AttributeKey.SEE_ALSO, m.group(1)));
                        }

                    }
                }


                m = SIZE_REQUIREMENT_PATTERN.matcher(cursor.text());
                if (m.find()) {
                    found = true;
                    if(isVariant){
                        current.withProvided(Prerequisite.create(m.group(1).trim()));

                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            variant.withProvided(Prerequisite.create(m.group(1).trim()));
                        }

                    }
                }

                if(cursor.text().startsWith("*")){
                    if(isVariant){
                        if(current.hasAsterisk()){
                            current.withPrerequisite(Prerequisite.create(cursor.text()));
                        }
                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            if(variant.hasAsterisk()){
                                variant.withPrerequisite(Prerequisite.create(cursor.text()));
                            }
                        }
                    }
                }

                if (!cursor.text().contains(":") && !cursor.text().contains("*")  && cursor.text().length() > 100) {
                    found = true;
                    if(isVariant){
                        current.withDescription(cursor);

                    } else{
                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
                            variant.withDescription(cursor);
                        }

                    }

                }

                if (!found && !itemName.equals("Escape Pod Statistics (CL 1)")) {
                    //printUnique(itemName, cursor.text());
                }
            }
        }

        for (Map.Entry<String, Map<String, VehicleSystem>> entry : variantData.entrySet()) {
            if (entry.getValue().entrySet().size() > 1) {
                for (Map.Entry<String, VehicleSystem> systemEntry : entry.getValue().entrySet()) {
                    if (!systemEntry.getKey().equals(entry.getKey())) {
                        items.add(systemEntry.getValue());
                    }
                }
            } else {
                items.addAll(entry.getValue().values());
            }
        }

        List<VehicleSystem> manualItems = new LinkedList<>();
        for (VehicleSystem system : items) {
            manualItems.addAll(manualItems(system));
        }
        items.addAll(manualItems);

        return new ArrayList<>(items);
    }

    private static Collection<? extends VehicleSystem> manualItems(VehicleSystem system) {
        List<VehicleSystem> systems = new LinkedList<>();

        switch (system.getName()) {
            case "Droid Socket":
                system.withProvided(Attribute.create(AttributeKey.PROVIDES_SLOT, ASTROMECH_DROID));
                system.withSubtype("Droid Accessories (Droid Stations)");
                break;
            case "Double Cannon":
                system.withName("Double")
                        .withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
                        .withProvided(Attribute.create(AttributeKey.ITEM_MOD, "true"));
                system.withProvided(Attribute.create(AttributeKey.BONUS_DAMAGE, "1d10"));
                system.withProvided(Attribute.create(AttributeKey.COST, "*3"));
                system.withProvided(Attribute.create(AttributeKey.AUTOFIRE_ATTACK_BONUS, "0"));
                systems.add(system.copy().withName("Dual"));
                systems.add(system.copy().withName("Twin"));
                systems.add(system.copy().withName("Triple"));
                systems.add(system.copy().withName("Enhanced"));
                break;
            case "Rapid-Fire":
                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
                        .withProvided(Attribute.create(AttributeKey.ITEM_MOD, "true"));
                system.withProvided(Attribute.create(AttributeKey.AUTOFIRE_ATTACK_BONUS, "+3"));
                break;
            case "Rapid-Repeating":
                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
                        .withProvided(Attribute.create(AttributeKey.ITEM_MOD, "true"));
                system.withProvided(Attribute.create(AttributeKey.AUTOFIRE_ATTACK_BONUS, "0"));
                break;
            case "Quad Cannon":
                system.withName("Quad");
                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
                        .withProvided(Attribute.create(AttributeKey.ITEM_MOD, "true"));
                system.withProvided(Attribute.create(AttributeKey.MODIFIES, "TYPE:Weapon Systems"));
                system.withProvided(Attribute.create(AttributeKey.BONUS_DAMAGE, "2d10"));
                system.withProvided(Attribute.create(AttributeKey.COST, "*5"));
                system.withProvided(Attribute.create(AttributeKey.AUTOFIRE_ATTACK_BONUS, "0"));
                break;
            case "2 Fire-Linked Weapon":
            case "4 Fire-Linked Weapon":
            case "Standard Cannon Enhancements":
            case "Advanced Cannon Enhancements":
                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
                        .withProvided(Attribute.create(AttributeKey.ITEM_MOD, "true"));
                system.withProvided(Attribute.create(AttributeKey.MODIFIES, "TYPE:Weapon Systems"));
                system.withProvided(Attribute.create(AttributeKey.BONUS_DAMAGE, "2d10"));
                system.withProvided(Attribute.create(AttributeKey.COST, "*5"));
                system.withProvided(Attribute.create(AttributeKey.AUTOFIRE_ATTACK_BONUS, "0"));
                break;
            case "Proton Torpedo Launcher":
                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
                        .withProvided(Attribute.create(AttributeKey.ITEM_MOD, "true"));
                //systems.add(system.copy().withName("Light Proton Torpedoes")).replaceAttribute(Attribute.create("damage", ));
                systems.add(system.copy().withName("Medium Proton Torpedoes"));
                systems.add(system.copy().withName("Heavy Proton Torpedoes").replaceAttribute(Attribute.create(AttributeKey.DAMAGE, "9d10x5")));
                break;
            case "Battery":
                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
                        .withProvided(Attribute.create(AttributeKey.ITEM_MOD, "true"));
                system.withProvided(Attribute.create(AttributeKey.AID_ANOTHER_BONUS, "2"));
                //TODO battery should let you chose how many battery slots to add system.withProvided(Choice.create());
                break;
            case "Heavy Yaret-kor":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "10d10x5"));
                system.withProvided(Attribute.create(AttributeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
                break;
            case "Medium Yaret-kor":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "8d10x5"));
                system.withProvided(Attribute.create(AttributeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
                break;
            case "Light Yaret-kor":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "6d10x5"));
                break;
            case "Magma Missile":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "8d10x5"));
                system.withProvided(Attribute.create(AttributeKey.SPLASH, "4 square"));
                system.withProvided(Attribute.create(AttributeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
                break;
            case "Heavy Plasma Projector":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "10d10x5"));
                system.withProvided(Attribute.create(AttributeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
                break;
            case "Medium Plasma Projector":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "8d10x5"));
                system.withProvided(Attribute.create(AttributeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
                break;
            case "Light Plasma Projector":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "6d10x2"));
                break;
            case "Dovin Basal":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "7d10x2"));
                system.withProvided(Attribute.create(AttributeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
                break;
            case "Stun Cannon":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "7d10x2"));
                system.withProvided(Attribute.create(AttributeKey.DAMAGE_TYPE, "Stun"));
                break;
            case "Suppression Cannon":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "4d10x2"));
                system.withProvided(Attribute.create(AttributeKey.DAMAGE_TYPE, "Stun"));
                system.withProvided(Attribute.create(AttributeKey.AUTOFIRE_ATTACK_BONUS, "0"));
                break;
            case "Antivehicle Cannon":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "5d10x2"));
                system.withProvided(Attribute.create(AttributeKey.DAMAGE_TYPE, "Stun"));
                break;
            case "Interceptor Missile":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "4d10x2"));
                system.withProvided(Attribute.create(AttributeKey.SPLASH, "4 square"));
                break;
            case "Bomblet Generator":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "4d6"));
                system.withProvided(Attribute.create(AttributeKey.DAMAGE_TYPE, "Ion"));
                break;
            case "Superlaser":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "8d10x40"));
                system.withProvided(Attribute.create(AttributeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
                break;
            case "Volcano Cannon":
                system.withSubtype("Weapon Systems");
                system.withProvided(Attribute.create(AttributeKey.DAMAGE, "6d10x2"));
                break;
            case "Heavy Concussion Missile Launcher":
                final VehicleSystem superHeavy = system.copy().withName("Super-Heavy Concussion Missile ");
                superHeavy.withProvided(Attribute.create(AttributeKey.DAMAGE, "11d10x5"));
                systems.add(superHeavy);
                break;
        }

        Matcher hyperdriveMatcher = HYPERDRIVE.matcher(system.getName());

        if (hyperdriveMatcher.find()) {
            if(system.getName().equals("Class 3 Hyperdrive"))
            {
                systems.add(system.copy().withName("Class 2.5 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "2.5")));
            }
            if(system.getName().equals("Class 8 Hyperdrive"))
            {
                systems.add(system.copy().withName("Class 7 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "7")));
                systems.add(system.copy().withName("Class 9 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "9")));
            }
            if(system.getName().equals("Class 15 Hyperdrive"))
            {
                systems.add(system.copy().withName("Class 12 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "12")));
                systems.add(system.copy().withName("Class 14 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "14")));
                systems.add(system.copy().withName("Class 16 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "16")));
                systems.add(system.copy().withName("Class 18 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "18")));
                systems.add(system.copy().withName("Class 20 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "20")));
                systems.add(system.copy().withName("Class 24 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "24")));
                systems.add(system.copy().withName("Class 25 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "25")));
                systems.add(system.copy().withName("Class 30 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "25")));
            }
            if(system.getName().equals("Class 0.75 Hyperdrive"))
            {
                systems.add(system.copy().withName("Class 0.9 Hyperdrive").withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "0.9")));
                systems.add(system.copy().withName("Class 0.5 Hyperdrive")
                        .withProvided(Attribute.create(AttributeKey.HYPERDRIVE, "0.5")).withAvailability("Illegal")
                        .withPrerequisite(new SimplePrerequisite("The only way to achieve a Hyperdrive of this type is to modify a Class .75 Hyperdrive using the Starship Designer Feat.", "FEAT", "Starship Designer")));
            }

            system.withProvided(Attribute.create(AttributeKey.HYPERDRIVE, hyperdriveMatcher.group(1)));


        }


        return systems;
    }

    private static void addItemOrVariantsToCollection(List<VehicleSystem> items, VehicleSystem current, Map<String, List<Object>> variantData) {
        if (variantData.size() > 0) {
            for (Map.Entry<String, List<Object>> variant : variantData.entrySet()) {
                VehicleSystem v = current.copy();
                items.add(v);
                v.withProvided(getManualAttributes(variant.getKey()));
                v.withName(nameCleanup(variant.getKey()));
                for (Object o : variant.getValue()) {
                    if (o instanceof String && ((String) o).startsWith("COST:")) {
                        v.withCost(((String) o).substring(5).trim());
                    } else if (o instanceof Prerequisite) {
                        v.withPrerequisite((Prerequisite) o);
                    } else {
                        v.withProvided(o);
                    }
                }
            }
            variantData.clear();
        } else {
            current.withProvided(getManualAttributes(current.getName()));
            items.add(current);
        }
    }

    private static String nameCleanup(String name) {
        switch (name) {
            case "Cannon, Double/Quad":
                return "Cannon";
            case "Blaster Cannon (Vehicles)":
                return "Blaster Cannon";
        }
        return name;
    }

    private static Collection<Object> getManualAttributes(String name) {
        List<Object> attributes = new LinkedList<>();

        if (name.endsWith("Added Power Couplings")) {
            attributes.add(Attribute.create(AttributeKey.EMPLACEMENT_POINTS_BONUS, name.split(" ")[0]));
        } else {
            //System.out.println(name);
        }

        return attributes;
    }

}
