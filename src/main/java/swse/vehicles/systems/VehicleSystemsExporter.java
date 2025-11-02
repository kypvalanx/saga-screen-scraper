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
import swse.common.Change;
import swse.common.ChangeKey;
import swse.common.BaseExporter;
import swse.common.JSONy;
import swse.item.Effect;
import swse.prerequisite.Prerequisite;
import swse.prerequisite.SimplePrerequisite;
import swse.util.Context;

import static org.jsoup.internal.StringUtil.isNumeric;
import static swse.util.Util.toEnumCase;

public class VehicleSystemsExporter extends BaseExporter {
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\Vehicle Systems.json";
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
    private static final int TOP_HEADER = 0;
    private static final int HEADER = 1;
    private static final int ROW = 2;

    public static void main(String[] args) {
        List<String> systemLinks = List.of("/wiki/Movement_Systems",
                "/wiki/Defense_Systems",
                "/wiki/Weapon_Systems",
                "/wiki/Starship_Accessories",
                "/wiki/Droid_Socket");

        List<JSONObject> entries = new LinkedList<>();
        List<String> names = new LinkedList<>();

        for (String systemLink :
                systemLinks) {
            final List<JSONObject> newEntities = generateItemsFromLink(systemLink, null, false, null);
            for (JSONObject newEntity : newEntities) {
                if (names.contains(newEntity.get("name"))) {
                    System.out.println("Duplicate: " + newEntity.get("name") + " from: " + systemLink);
                } else {
                    names.add((String) newEntity.get("name"));
                    entries.add(newEntity);
                }
            }
            drawProgressBar(entries.size() * 100.0 / 323.0);
        }
        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"), "Vehicle Systems", "Item");
    }

    private static List<JSONObject> generateItemsFromLink(String link, Map<String, String> context, boolean overwrite, String subType) {
        if (null == link) {
            return new ArrayList<>();
        }
        Document doc = getDoc(link, overwrite);
        if (doc == null) {
            return new ArrayList<>();
        }

        //System.out.println("Parsing " + link);

        Element parserOutput = doc.select(".page__main").first();

        ArrayList<JSONObject> jsonObjects = new ArrayList<>();

        boolean singlePage = false;
        for (Element element : parserOutput.children()) {
            if (element.text().contains("Type:")) {
                singlePage = true;
            }
        }

        if (singlePage) {

            jsonObjects.addAll(generateItemsFromSingleItemPage(parserOutput, context, subType));
        } else {

            jsonObjects.addAll(generateItemsFromMenuPage(parserOutput, getSubtypeFromLink(link)));
        }

        return jsonObjects;
    }

    private static String getSubtypeFromLink(String link) {
        return link.substring(6).replace("_", " ");
    }

    private static List<JSONObject> generateItemsFromMenuPage(Element element, String subType) {
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        String pageName = element.select(".mw-page-title-main").text();

        Element table = element.select("table.wikitable").first();

        Map<String, Map<String, String>> tableMap = parseTable(pageName, table);

        for (Map<String, String> tableValues : tableMap.values()) {
            jsonObjects.addAll(generateItemsFromLink(tableValues.get("LINK"), tableValues, false, subType));
        }

        return jsonObjects;
    }

    private static Map<String, Map<String, String>> parseTable(String pageName, Element table) {
        Element caption = table.select("caption").first();
        if (caption != null && caption.text().contains("Ranges")) {
            return parseRangeTable(table);
        }
        String[] headers = new String[table.select("th").size() * 2];

        String previousLikn = "";
        Map<String, Map<String, String>> tableMap = new HashMap<>();
        for (Element row : table.select("tr")) {
            Map<String, String> rowValues = new HashMap<>();

            for (Element cell : row.children()) {
                String colspan = cell.attr("colspan");
                if (!colspan.equals("")) {
                    continue;
                }

                if (cell.tag().getName().equals("th")) {
                    headers[cell.siblingIndex()] = getStandardColumnName(pageName, cell);
                } else {
                    rowValues.put(headers[cell.siblingIndex()], cell.text());
                    if (headers[cell.siblingIndex()].equals("NAME")) {
                        Element a = cell.select("a").first();
                        if (a != null) {

                            previousLikn = a.attr("href");
                            rowValues.put("LINK", previousLikn);

                        } else {
                            //System.out.println("couldn't find link on "+pageName+" for \"" + cell.text() + "\" using \"" + previousLikn + "\"");
                            rowValues.put("LINK", previousLikn);
                        }
                    }
                }


            }

            tableMap.put(rowValues.get("NAME"), rowValues);
        }
        return tableMap;
    }

    private static Map<String, Map<String, String>> parseRangeTable(Element table) {

        String[] topHeaders = new String[table.select("th").size() * 2];
        String[] headers = new String[table.select("th").size() * 2];

        int rowType = TOP_HEADER;

        Map<String, Map<String, String>> tableMap = new HashMap<>();
        for (Element row : table.select("tr")) {
            int i  = 0;
            for (Element cell : row.children()) {
                String colspan = cell.attr("colspan");
                int width = isNumeric(colspan) ? Integer.parseInt(colspan) : 1;
                for (int j = i; i < j + width; i++) {
                    switch (rowType){
                        case TOP_HEADER:
                            topHeaders[i] = cell.text();
                            break;
                        case HEADER:
                            headers[i] = cell.text();
                            break;
                        default:
                            if(!topHeaders[i].isEmpty()){
                                tableMap.computeIfAbsent(topHeaders[i], s -> new HashMap<>())
                                        .put(headers[i], cell.text());
                            }
                    }
                }
            }
            rowType++;
        }
        return tableMap;
    }

    private static String getStandardColumnName(String pageName, Element cell) {
        String text = cell.text();
        if (pageName.toLowerCase().contains(text.toLowerCase()) || "STARSHIP ACCESSORY".equals(text)) {
            text = "NAME";
        }

        if (text.equals("AVAILABLE")) {
            text = "AVAILABILITY";
        }
        return text;
    }

    private static List<JSONObject> generateItemsFromSingleItemPage(Element element, Map<String, String> context, String subType) {
        String name = element.select(".mw-page-title-main").text();
        Elements headers = element.select(".mw-headline");

        List<Map<String, Map<String, String>>> tables = getTables(element, name);


        Map<String, String> changes = new HashMap<>();
        String variation = name;
        if (context != null) {
            changes.putAll(context);
            variation = context.get("NAME");
        }

        //System.out.println(context);

        String specificVariation = getSpecificVariation(variation, name);

        changes.putAll(getManualChanges(name, specificVariation));
        for (Map<String, Map<String, String>> table :
                tables) {

            if (isRangeTable(table) && !isAmmo(name, specificVariation)) {
                Map<String, String> characterScale = table.get("RANGE BY STARSHIP SCALE");
                for (Map.Entry<String, String> range :
                        characterScale.entrySet()) {
                    changes.put("STARSHIP_" + range.getKey(), range.getValue());
                }
                changes.putAll(table.get("RANGE BY CHARACTER SCALE"));

            } else if(table.containsKey(specificVariation)){
                Map<String, String> tableRow = table.get(specificVariation);

                if (tableRow != null) {

                    for (Map.Entry<String, String> c :
                            tableRow.entrySet()) {
                        if (List.of("NAME", "LINK").contains(c.getKey())) {
                            continue;
                        }

                        if (!c.getValue().equals(changes.get(c.getKey()))) {
                            //System.out.println("Menu values should match for " + variation + ".  menu: " + changes.get(c.getKey()) + " page: " + c.getValue());
                        }
                    }
                }
            }



            //System.out.println(tableRow);
        }


        //System.out.println(changes);

        List<JSONObject> jsonObjects = new ArrayList<>();
        VehicleSystem vehicleSystem = new VehicleSystem(cleanName(variation));

        if(isAmmo(name, specificVariation)){
            vehicleSystem.withType("equipment"); //TODO change this to AMMO when an ammo type is created
            subType = "Ammunition";
        }

        if(List.of("Autoblaster", "Point-Defense").contains(name)){
            vehicleSystem.withType("template");
        }

        if(List.of("Droid Socket").contains(name)){
            vehicleSystem.withSubtype("Droid Accessories (Droid Stations)");
            subType = "Droid Accessories (Droid Stations)";
        }

        if("Fire-Linked Weapon, 4".equals(variation) || "Fire-Linked Weapon, 2".equals(variation)){
            String damage = changes.remove("DAMAGE");
            vehicleSystem.with(Effect.create("Fire-Linked Weapon", List.of(Change.create(ChangeKey.DAMAGE, damage))).tokenAccessible());
        }

        vehicleSystem.withDescription(element.selectFirst(".mw-parser-output"));

        if(subType != null){
            vehicleSystem.withSubtype(subType);
        }

        for (Map.Entry<String, String> change :
                changes.entrySet()) {
            String keyString = change.getKey();
            if (isIgnored(keyString)) {
                continue;
            }
            String value = change.getValue();
            if (isPrerequisite(keyString)) {

                vehicleSystem.withPrerequisite(Prerequisite.create(value));
                continue;
            }
            ChangeKey key = mapChangeKey(keyString);

            if (key == null) {
                continue;
            }

            if(ChangeKey.DAMAGE.equals(key)){
                value = value.replace("**", "");

                if(value.endsWith(" (Ion)")){
                    vehicleSystem.with(Change.create(ChangeKey.DAMAGE_TYPE, "Ion"));
                    value = value.substring(0, value.length()-6);
                }

                if(value.startsWith("+")){
                    vehicleSystem.withType("template");
                }
                if("-".equals(value) || "Special".equals(value)){
                    continue;
                }
            }

            if(ChangeKey.EMPLACEMENT_POINTS.equals(key)){
                if("-".equals(value)){
                    continue;
                }
            }

            vehicleSystem.with(Change.create(key, value));
        }

        jsonObjects.add(vehicleSystem.toJSON());


        //System.out.println(name+" : "+context);
        return jsonObjects;
    }

    private static String cleanName(String variation) {
        return variation.replace("*", "").trim();
    }

    private static boolean isAmmo(String name, String specificVariation) {
        if(specificVariation.endsWith("Torpedo") || specificVariation.endsWith("Missile") || specificVariation.endsWith("Bomb")|| name.endsWith("Bomb") && specificVariation.equals("") || specificVariation.endsWith("Grenade")
                || specificVariation.endsWith("Mine") || specificVariation.endsWith("Shell")
        ||List.of("Concussion MissileHeavy", "Concussion MissileMedium", "Concussion MissileLight", "Gravity MineMagnetic", "Space MineStandard", "Space MineAdvanced", "Space MineHeavy").contains(specificVariation)){
            System.out.println("AMMO FOUND: " + name + " " + specificVariation);
            return true;
        }

        return false;
    }

    private static Map<String, String> getManualChanges(String name, String specificVariation) {
        HashMap<String, String> changes = new HashMap<>();
        if ("Sublight Drive".equals(name)) {
            Pattern compile = Pattern.compile("Speed (\\d*) Square");
            Matcher m = compile.matcher(specificVariation);
            if (m.find()) {
                changes.put("STARSHIP_SPEED", m.group(1));
            }
        } else if ("Hyperdrive".equals(name)) {
            Pattern compile = Pattern.compile("Class (\\d*)");
            Matcher m = compile.matcher(specificVariation);
            if (m.find()) {
                changes.put("HYPERDRIVE", m.group(1));
            }
        } else if ("Combat Thrusters".equals(name)) {
            changes.put("EFFECTIVE_SIZE_WHEN_TARGETED", "-1");
            changes.put("DOGFIGHTING", "TRUE");
        } else if ("Atmospheric Thrusters".equals(name)) {
            if ("Advanced".equals(specificVariation)) {
                changes.put("ATMOSPHERIC_THRUSTER_BONUS", "25%");
            } else {
                changes.put("ATMOSPHERIC_THRUSTER_BONUS", "10%");
            }
        } else if ("Navicomputer".equals(name)) {
            if ("Advanced".equals(specificVariation)) {
                changes.put("ASTROGATION_BONUS", "10");
            } else if ("Limited".equals(specificVariation)) {
                changes.put("ASTROGATION_BONUS", "5");
                changes.put("JUMP_LIMIT", "2");
            } else {
                changes.put("ASTROGATION_BONUS", "5");
            }
        } else if ("Maneuvering Jets".equals(name)) {
            Pattern compile = Pattern.compile("\\+(\\d*)");
            Matcher m = compile.matcher(specificVariation);
            if(m.find()){
                changes.put("DEXTERITY_BONUS", m.group(1));
            }
        } else if ("SubLight Accelerator Motor".equals(name)) {
            changes.put("SUBLIGHT_ACCELERATOR_MOTOR", "true");
        } else if ("Starship Shields".equals(name)){
            Pattern compile = Pattern.compile("SR (\\d*)");
            Matcher m = compile.matcher(specificVariation);
            if(m.find()){
                changes.put("SHIELDS", m.group(1));
            }
        } else if ("Reinforced Bulkheads".equals(name)){
            Pattern compile = Pattern.compile("\\+(\\d*)");
            Matcher m = compile.matcher(specificVariation);
            if(m.find()){
                changes.put("HIT_POINT_EQ", m.group(1));
            }
        } else if ("Starship Armor".equals(name)){
            Pattern compile = Pattern.compile("\\+(\\d*)");
            Matcher m = compile.matcher(specificVariation);
            if(m.find()){
                changes.put("REFLEX_DEFENSE_BONUS", m.group(1));
            }
        } else if ("Jamming Array".equals(name)){
            changes.put("JAMMING_ARRAY", "true");
        } else if ("Jamming Suite".equals(name)){
            changes.put("JAMMING_SUITE", "true");
        } else if ("Security Bracing".equals(name)){
            changes.put("SECURITY_BRACING", "true");
        } else if ("Reinforced Keel".equals(name)){
            if("Boarding".equals(specificVariation)){

                changes.put("RAMMING", "boarding");
            } else {

                changes.put("RAMMING", "standard");
            }
        } else if ("Com Jammers".equals(name)){

            changes.put("COM_JAMMING", "true");
        } else if ("Droid Jammer".equals(name)){

            changes.put("DROID_JAMMER", "true");
        } else if ("Regenerating Shields".equals(name)){

            changes.put("REGENERATING_SHIELDS", "5");
        } else if ("Anti-Boarding Systems".equals(name)){

            changes.put("ANTI_BOARDING_SYSTEMS", "5");
        } else if ("Shieldbuster Torpedo Launcher".equals(name)){
            if("".equals(specificVariation)){
                changes.put("AMMO", "Shieldbuster Torpedo:1");
                changes.put("AMMO_CAPACITY", "Shieldbuster Torpedo:4");
                changes.put("AMMO_CAPACITY_INCREASE", "Shieldbuster Torpedo:25%:8");
            }
        } else if ("Concussion Missile Launcher".equals(name)){
            if(List.of("Light", "Medium", "Heavy").contains(specificVariation)){
                int capacity = "Light".equals(specificVariation) ? 6 : "Medium".equals(specificVariation) ? 16 : 30;
                String concussionMissileType = "Concussion Missile, " + specificVariation;
                changes.put("AMMO", concussionMissileType + ":1");
                changes.put("AMMO_CAPACITY", concussionMissileType+":"+capacity);
                changes.put("AMMO_CAPACITY_INCREASE", concussionMissileType+":20%:" + (capacity * 2));
            }
        } else if ("Space Mine Launcher".equals(name)){
            if(List.of("", "Heavy").contains(specificVariation)){
                String spaceMineType = "Space Mine" + ("Heavy".equals(specificVariation) ? ", Heavy" : "");
                changes.put("AMMO", spaceMineType + ":1");
                changes.put("AMMO_CAPACITY", spaceMineType + ":6");
                changes.put("AMMO_CAPACITY_INCREASE", spaceMineType + ":25%:12");
            } else if (List.of("Space MineStandard", "Space MineHeavy", "Space MineAdvanced").contains(specificVariation)) {
                if(List.of("Space MineStandard", "Space MineAdvanced").contains(specificVariation)){
                    changes.put("ACTS_AS", "Space Mine");
                }
            }
        } else if ("Proton Grenade Launcher".equals(name)){
            if("".equals(specificVariation)){
                changes.put("AMMO", "Proton Grenade:1");
            }
        } else if ("Proton Torpedo Launcher".equals(name)){
            if("".equals(specificVariation)){
                changes.put("AMMO", "Proton Torpedo:1");
                changes.put("AMMO_CAPACITY", "Proton Torpedo:3");
                changes.put("AMMO_CAPACITY_INCREASE", "Proton Torpedo:25%:16");
            }
        } else if ("Tractor Beam".equals(name)){
            changes.put("TRACTOR_BEAM", "TRACTOR_BEAM");
        } else if ("Microtractor-Pressor".equals(name)){
            changes.put("TRACTOR_BEAM", "TRACTOR_PRESSOR");
        } else if ("Turbolaser".equals(name)) {
            changes.put("SIZE RESTRICTION", "Colossal (Frigate) or larger");
        } else if ("Docking Gun".equals(name)){
            changes.put("ALLOW_ITEM_DROP", "weapon:ranged");
        } else if ("Gravity Well Projector".equals(name)){
            changes.put("GRAVITY_WELL", "standard");
        } else if ("Defoliator Launcher".equals(name) && "".equals(specificVariation)){
            changes.put("AMMO", "Defoliator Shell:1");
        } else if ("Defoliator Launcher".equals(name) && "Defoliator Shell".equals(specificVariation)){
            changes.put("DAMAGE_TYPE", "Defoliator");
        } else if ("Discord Missile Launcher".equals(name) && "".equals(specificVariation)){
            changes.put("AMMO", "Discord Missile:1");
        } else if ("Discord Missile Launcher".equals(name) && "Discord Missile".equals(specificVariation)){
            changes.put("SPAWN", "Buzz Droid:3");
        } else if ("Composite Beam Cannon".equals(name)){
            changes.put("BYPASS_SHIELDS", "5");
        } else if ("Energy Bomblet Generator".equals(name)){
            changes.put("AMMO", "Energy Sphere:1");
            changes.put("AMMO_CAPACITY", "Energy Sphere:10");
            changes.put("AMMO_GENERATION", "Energy Sphere:10");
        } else if ("Chaff Gun".equals(name)) {
            changes.put("AMMO", "Chaff Canister:1");
            changes.put("AMMO_CAPACITY", "Chaff Canister:6");
            changes.put("AMMO_CAPACITY_INCREASE", "Chaff Canister:25%:12");
        } else if ("Harpoon Gun".equals(name)) {
            changes.put("APPLY_EFFECT", "Harpooned");
        } else if ("Droid Socket".equals(name)) {
            changes.put("PROVIDES_SLOT", ASTROMECH_DROID);
            changes.put("AVAILABILITY", "Military");
            changes.put("WEIGHT", "1000");
            changes.put("COST", "10000");
        }
        return changes;
    }

    private static boolean isRangeTable(Map<String, Map<String, String>> table) {
        return table.containsKey("RANGE BY CHARACTER SCALE") || table.containsKey("RANGE BY STARSHIP SCALE");
    }

    private static List<Map<String, Map<String, String>>> getTables(Element element, String name) {
        List<Map<String, Map<String, String>>> tables = new ArrayList<>();

        for (Element table : element.select("table.wikitable")) {
            tables.add(parseTable(name.trim().toUpperCase(), table));
            //System.out.println(name + " : " + table);
        }
        return tables;
    }

    private static boolean isIgnored(String key) {
        return List.of("NAME", "LINK").contains(key);
    }

    private static boolean isPrerequisite(String key) {
        return "SIZE RESTRICTION".equals(key);
    }

    private static ChangeKey mapChangeKey(String key) {
        try{
            return ChangeKey.valueOf(ChangeKey.class, key.replaceAll(" ", "_"));
        } catch (Exception e){

           //System.out.println("COULD NOT FIND DIRECT MATCH, CHECKING MAPPING "+ key);
        }

        switch (key) {
            case "LONG":
                return ChangeKey.RANGE_LONG;
            case "SHORT":
                return ChangeKey.RANGE_SHORT;
            case "POINT-BLANK":
                return ChangeKey.RANGE_POINT_BLANK;
            case "MEDIUM":
                return ChangeKey.RANGE_MEDIUM;
            case "STARSHIP_LONG":
                return ChangeKey.RANGE_STARSHIP_LONG;
            case "STARSHIP_SHORT":
                return ChangeKey.RANGE_STARSHIP_SHORT;
            case "STARSHIP_POINT-BLANK":
                return ChangeKey.RANGE_STARSHIP_POINT_BLANK;
            case "STARSHIP_MEDIUM":
                return ChangeKey.RANGE_STARSHIP_MEDIUM;
            case "STARSHIP_SPEED":
                return ChangeKey.SPEED_STARSHIP_SCALE;
            case "SHIELDS":
                return ChangeKey.SHIELD_RATING;
            default:
                System.out.println("no mapping found for " + key);
                return null;
        }
    }

    private static String getSpecificVariation(String variation, String name) {
        return variation.replace(name, "").replaceAll("[()*]*", "").replace("Squares", "Square").replace(".75", "0.75").replace(", ", "").trim();
    }


//    private List<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
//        if (null == itemPageLink) {
//            return new ArrayList<>();
//        }
//
//        Document doc = getDoc(itemPageLink, overwrite);
//
//        if (doc == null) {
//            return new ArrayList<>();
//        }
//
//
//        List<String> hrefs = new LinkedList<>();
//
//        Elements links = doc.select("a.category-page__member-link");
//        links.forEach(a -> hrefs.add(a.attr("href")));
//
//
//        return hrefs.stream().flatMap((Function<String, Stream<JSONy>>) itemLink -> parseItem(itemLink, overwrite, null, null).stream())
//                .map(item -> item.toJSON())
//                .collect(Collectors.toList());
//    }

    protected List<JSONy> parseItem(String itemLink, boolean overwrite, List<String> filter, List<String> nameFilter) {
//        if (null == itemLink) {
//            return new ArrayList<>();
//        }
//
//        Document doc = getDoc(itemLink, overwrite);
//
//        if (doc == null) {
//            return new ArrayList<>();
//        }
//        Element title = doc.select("h1.page-header__title").first();
//
//        if (title.text().trim().equalsIgnoreCase("walkers")) {
//            return new ArrayList<>();
//        }
//
//
//        final Elements children = doc.select("div.mw-parser-output").first().children();
//
//        String itemName = nameCleanup(title.text().trim());
//
//        return assembleFromNameAndChildren(children, itemName);
        return null;
    }

//    private static List<JSONy> assembleFromNameAndChildren(Elements children, String itemName) {
//        Context.setValue("name", itemName);
//        List<VehicleSystem> items = new LinkedList<>();
//        VehicleSystem current = VehicleSystem.create(itemName);
//
//        Map<String, Map<String, VehicleSystem>> variantData = new HashMap<>();
//        Map<String, VehicleSystem> variantSubData = variantData.computeIfAbsent(itemName, i -> new HashMap<>());
//        variantSubData.put(itemName, current);
//        boolean isVariant = false;
//
//
//        if (children != null) {
//            for (Element cursor : children) {
//                boolean found = false;
//                Element subItem = cursor.select(".mw-headline").first(); //Check if a variation of the object is being defined
//
//                //Finds and creates variations from tables on the page
//                if (cursor.hasClass("wikitable")) {
//                    String firstTh = cursor.select("th").first().text();
//                    if (itemName.toLowerCase().startsWith(firstTh.toLowerCase())) {
//                        List<String> headers = getHeaders(cursor);
//                        Elements rows = cursor.select("tr:has(td)");
//                        for (Element row : rows) {
//
//                            List<String> rowValues = row.select("td").stream().map(element -> element.text().trim()).collect(Collectors.toList());
//                            String nameModifier = rowValues.get(0).replaceAll("\\*", "");
//
//                            final String variantName = nameModifier + " " + itemName;
//                            VehicleSystem finalCurrent = current;
//                            variantSubData = variantData.computeIfAbsent(itemName, i -> new HashMap<>());
//                            VehicleSystem currentVariant = variantSubData.computeIfAbsent(variantName, k -> finalCurrent.copy().withName(variantName));
//
//                            if (rowValues.get(0).contains("*")) {
//                                currentVariant.withAsterisk(true);
//                            }
//                            //System.out.println(variantName);
//
//                            if ("Added Power Couplings".equalsIgnoreCase(itemName)) {
//                                currentVariant.with(Change.create(ChangeKey.EMPLACEMENT_POINTS_BONUS, nameModifier));
//                            }
//
//                            for (int i = 1; i < headers.size(); i++) {
//                                String key = headers.get(i);
//                                String value = rowValues.get(i);
//                                switch (key) {
//                                    case "AVAILABILITY":
//                                    case "AVAILABLE":
//                                        currentVariant.withAvailability(value);
//                                        break;
//                                    case "EMPLACEMENT POINTS":
//                                    case "DAMAGE":
//                                        currentVariant.with(Change.create(ChangeKey
//                                                .valueOf(toEnumCase(key.toLowerCase())), value));
//                                        break;
//                                    case "COST":
//                                        currentVariant.withCost(value);
//                                        break;
//                                    case "SIZE RESTRICTION":
//                                        currentVariant.with(Prerequisite.create(value));
//                                        break;
//                                    default:
//                                        //System.out.println("- " + key + " : " + value);
//                                }
//                                //System.out.println("- "+key + " : " + value);
//                            }
//                        }
//                    } else {
//                        //printUnique(itemName + " : " +firstTh);
//                    }
//                    continue;
//                }
//                if (subItem != null) {
//                    itemName = subItem.text().trim();
//                    final String singularItemName = itemName.substring(0, itemName.length() - 1);
//                    final Map<String, VehicleSystem> flatMap = variantData.values().stream().flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//                    if (flatMap.containsKey(itemName)) {
//                        current = flatMap.get(itemName);
//                        isVariant = !variantData.containsKey(itemName) && !variantData.containsKey(singularItemName);
//                    } else if (flatMap.containsKey(singularItemName)) {
//                        current = flatMap.get(singularItemName);
//                        isVariant = !variantData.containsKey(itemName) && !variantData.containsKey(singularItemName);
//                    } else {
//                        current = VehicleSystem.create(itemName);
//                        isVariant = false;
//
//                        variantSubData = variantData.computeIfAbsent(itemName, i -> new HashMap<>());
//                        variantSubData.put(itemName, current);
//                    }
//
//                    continue;
//                }
//
//
//                Matcher m = STARSHIP_SYSTEM_TYPE.matcher(cursor.text());
//                if (m.find()) {
//                    found = true;
//                    String substring = m.group(1);
//
//                    if (substring.equals("Heavy Weapons") || substring.equals("Grenades")) {
//                        substring = "Weapon Systems";
//                    }
//
//                    if (isVariant) {
//                        current.withSubtype(substring);
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            variant.withSubtype(substring);
//                        }
//                    }
//                }
//
//                m = SOURCE_PATTERN.matcher(cursor.text());
//                if (m.find()) {
//                    found = true;
//                    if (isVariant) {
//                        current.withSource(m.group(1));
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            variant.withSource(m.group(1));
//                        }
//                    }
//                }
//
//                m = EMPLACEMENT_POINT_PATTERN.matcher(cursor.text());
//
//                if (m.find()) {
//                    found = true;
//                    if (isVariant) {
//                        current.with(Change.create(ChangeKey.EMPLACEMENT_POINTS, m.group(1)));
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            variant.with(Change.create(ChangeKey.EMPLACEMENT_POINTS, m.group(1)));
//                        }
//                    }
//                }
//
//
//                m = AVAILABILITY_PATTERN.matcher(cursor.text());
//
//                if (m.find()) {
//                    found = true;
//                    if (isVariant) {
//                        current.withAvailability(m.group(1));
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            variant.withAvailability(m.group(1));
//                        }
//                    }
//                }
//
//                m = COST_PATTERN.matcher(cursor.text());
//                if (m.find()) {
//                    found = true;
//                    if (isVariant) {
//                        current.withCost(m.group(1));
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            variant.withCost(m.group(1));
//                        }
//                    }
//                }
//
//
//                m = DAMAGE_PATTERN.matcher(cursor.text());
//                if (m.find()) {
//                    found = true;
//                    if (isVariant) {
//
//                        final Change damage = Change.create(ChangeKey.DAMAGE, m.group(1).trim());
//                        if (m.group(2) != null) {
//                            damage.withModifier(m.group(2).trim());
//                        }
//                        current.with(damage);
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            final Change damage = Change.create(ChangeKey.DAMAGE, m.group(1).trim());
//                            if (m.group(2) != null) {
//                                damage.withModifier(m.group(2).trim());
//                            }
//                            variant.with(damage);
//                        }
//
//                    }
//                }
//
//                //printUnique(m.group(1));
//                //printUnique(cursor.text());
//
//                m = SEE_ALSO_PATTERN.matcher(cursor.text());
//                if (m.find()) {
//                    found = true;
//                    if (isVariant) {
//
//                        current.with(Change.create(ChangeKey.SEE_ALSO, m.group(1)));
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            variant.with(Change.create(ChangeKey.SEE_ALSO, m.group(1)));
//                        }
//
//                    }
//                }
//
//
//                m = SIZE_REQUIREMENT_PATTERN.matcher(cursor.text());
//                if (m.find()) {
//                    found = true;
//                    if (isVariant) {
//                        current.with(Prerequisite.create(m.group(1).trim()));
//
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            variant.with(Prerequisite.create(m.group(1).trim()));
//                        }
//
//                    }
//                }
//
//                if (cursor.text().startsWith("*")) {
//                    if (isVariant) {
//                        if (current.hasAsterisk()) {
//                            current.withPrerequisite(Prerequisite.create(cursor.text()));
//                        }
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            if (variant.hasAsterisk()) {
//                                variant.withPrerequisite(Prerequisite.create(cursor.text()));
//                            }
//                        }
//                    }
//                }
//
//                if (!cursor.text().contains(":") && !cursor.text().contains("*") && cursor.text().length() > 100) {
//                    found = true;
//                    if (isVariant) {
//                        current.withDescription(cursor);
//
//                    } else {
//                        for (VehicleSystem variant : variantData.computeIfAbsent(itemName, k -> new HashMap<>()).values()) {
//                            variant.withDescription(cursor);
//                        }
//
//                    }
//
//                }
//
//                if (!found && !itemName.equals("Escape Pod Statistics (CL 1)")) {
//                    //printUnique(itemName, cursor.text());
//                }
//            }
//        }
//
//        for (Map.Entry<String, Map<String, VehicleSystem>> entry : variantData.entrySet()) {
//            if (entry.getValue().entrySet().size() > 1) {
//                for (Map.Entry<String, VehicleSystem> systemEntry : entry.getValue().entrySet()) {
//                    if (!systemEntry.getKey().equals(entry.getKey())) {
//                        items.add(systemEntry.getValue());
//                    }
//                }
//            } else {
//                items.addAll(entry.getValue().values());
//            }
//        }
//
//        List<VehicleSystem> manualItems = new LinkedList<>();
//        for (VehicleSystem system : items) {
//            manualItems.addAll(manualItems(system));
//        }
//        items.addAll(manualItems);
//
//        return new ArrayList<>(items);
//    }
//
//    private static Collection<? extends VehicleSystem> manualItems(VehicleSystem system) {
//        List<VehicleSystem> systems = new LinkedList<>();
//
//        switch (system.getName()) {
//            case "Droid Socket":
//                system.with(Change.create(ChangeKey.PROVIDES_SLOT, ASTROMECH_DROID));
//                system.withSubtype("Droid Accessories (Droid Stations)");
//                break;
//            case "Double Cannon":
//                system.withName("Double")
//                        .withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
//                        .with(Change.create(ChangeKey.ITEM_MOD, "true"));
//                system.with(Change.create(ChangeKey.BONUS_DAMAGE, "1d10"));
//                system.with(Change.create(ChangeKey.COST, "*3"));
//                system.with(Change.create(ChangeKey.AUTOFIRE_ATTACK_BONUS, "0"));
//                systems.add(system.copy().withName("Dual"));
//                systems.add(system.copy().withName("Twin"));
//                systems.add(system.copy().withName("Triple"));
//                systems.add(system.copy().withName("Enhanced"));
//                break;
//            case "Rapid-Fire":
//                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
//                        .with(Change.create(ChangeKey.ITEM_MOD, "true"));
//                system.with(Change.create(ChangeKey.AUTOFIRE_ATTACK_BONUS, "+3"));
//                break;
//            case "Rapid-Repeating":
//                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
//                        .with(Change.create(ChangeKey.ITEM_MOD, "true"));
//                system.with(Change.create(ChangeKey.AUTOFIRE_ATTACK_BONUS, "0"));
//                break;
//            case "Quad Cannon":
//                system.withName("Quad");
//                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
//                        .with(Change.create(ChangeKey.ITEM_MOD, "true"));
//                system.with(Change.create(ChangeKey.MODIFIES, "TYPE:Weapon Systems"));
//                system.with(Change.create(ChangeKey.BONUS_DAMAGE, "2d10"));
//                system.with(Change.create(ChangeKey.COST, "*5"));
//                system.with(Change.create(ChangeKey.AUTOFIRE_ATTACK_BONUS, "0"));
//                break;
//            case "2 Fire-Linked Weapon":
//            case "4 Fire-Linked Weapon":
//            case "Standard Cannon Enhancements":
//            case "Advanced Cannon Enhancements":
//                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
//                        .with(Change.create(ChangeKey.ITEM_MOD, "true"));
//                system.with(Change.create(ChangeKey.MODIFIES, "TYPE:Weapon Systems"));
//                system.with(Change.create(ChangeKey.BONUS_DAMAGE, "2d10"));
//                system.with(Change.create(ChangeKey.COST, "*5"));
//                system.with(Change.create(ChangeKey.AUTOFIRE_ATTACK_BONUS, "0"));
//                break;
//            case "Proton Torpedo Launcher":
//                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
//                        .with(Change.create(ChangeKey.ITEM_MOD, "true"));
//                //systems.add(system.copy().withName("Light Proton Torpedoes")).replaceAttribute(Attribute.create("damage", ));
//                systems.add(system.copy().withName("Medium Proton Torpedoes"));
//                systems.add(system.copy().withName("Heavy Proton Torpedoes").replaceAttribute(Change.create(ChangeKey.DAMAGE, "9d10x5")));
//                break;
//            case "Battery":
//                system.withPrerequisite(new SimplePrerequisite("Can only be added to a Weapon System", "SUBTYPE", "Weapon Systems"))
//                        .with(Change.create(ChangeKey.ITEM_MOD, "true"));
//                system.with(Change.create(ChangeKey.AID_ANOTHER_BONUS, "2"));
//                //TODO battery should let you chose how many battery slots to add system.withProvided(Choice.create());
//                break;
//            case "Heavy Yaret-kor":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "10d10x5"));
//                system.with(Change.create(ChangeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
//                break;
//            case "Medium Yaret-kor":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "8d10x5"));
//                system.with(Change.create(ChangeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
//                break;
//            case "Light Yaret-kor":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "6d10x5"));
//                break;
//            case "Magma Missile":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "8d10x5"));
//                system.with(Change.create(ChangeKey.SPLASH, "4 square"));
//                system.with(Change.create(ChangeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
//                break;
//            case "Heavy Plasma Projector":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "10d10x5"));
//                system.with(Change.create(ChangeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
//                break;
//            case "Medium Plasma Projector":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "8d10x5"));
//                system.with(Change.create(ChangeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
//                break;
//            case "Light Plasma Projector":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "6d10x2"));
//                break;
//            case "Dovin Basal":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "7d10x2"));
//                system.with(Change.create(ChangeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
//                break;
//            case "Stun Cannon":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "7d10x2"));
//                system.with(Change.create(ChangeKey.DAMAGE_TYPE, "Stun"));
//                break;
//            case "Suppression Cannon":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "4d10x2"));
//                system.with(Change.create(ChangeKey.DAMAGE_TYPE, "Stun"));
//                system.with(Change.create(ChangeKey.AUTOFIRE_ATTACK_BONUS, "0"));
//                break;
//            case "Antivehicle Cannon":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "5d10x2"));
//                system.with(Change.create(ChangeKey.DAMAGE_TYPE, "Stun"));
//                break;
//            case "Interceptor Missile":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "4d10x2"));
//                system.with(Change.create(ChangeKey.SPLASH, "4 square"));
//                break;
//            case "Bomblet Generator":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "4d6"));
//                system.with(Change.create(ChangeKey.DAMAGE_TYPE, "Ion"));
//                break;
//            case "Superlaser":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "8d10x40"));
//                system.with(Change.create(ChangeKey.TARGET_SIZE_MODIFIER, "<Colossal:-20"));
//                break;
//            case "Volcano Cannon":
//                system.withSubtype("Weapon Systems");
//                system.with(Change.create(ChangeKey.DAMAGE, "6d10x2"));
//                break;
//            case "Heavy Concussion Missile Launcher":
//                final VehicleSystem superHeavy = system.copy().withName("Super-Heavy Concussion Missile ");
//                superHeavy.with(Change.create(ChangeKey.DAMAGE, "11d10x5"));
//                systems.add(superHeavy);
//                break;
//        }
//
//        Matcher hyperdriveMatcher = HYPERDRIVE.matcher(system.getName());
//
//        if (hyperdriveMatcher.find()) {
//            if (system.getName().equals("Class 3 Hyperdrive")) {
//                systems.add(system.copy().withName("Class 2.5 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "2.5")));
//            }
//            if (system.getName().equals("Class 8 Hyperdrive")) {
//                systems.add(system.copy().withName("Class 7 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "7")));
//                systems.add(system.copy().withName("Class 9 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "9")));
//            }
//            if (system.getName().equals("Class 15 Hyperdrive")) {
//                systems.add(system.copy().withName("Class 12 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "12")));
//                systems.add(system.copy().withName("Class 14 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "14")));
//                systems.add(system.copy().withName("Class 16 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "16")));
//                systems.add(system.copy().withName("Class 18 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "18")));
//                systems.add(system.copy().withName("Class 20 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "20")));
//                systems.add(system.copy().withName("Class 24 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "24")));
//                systems.add(system.copy().withName("Class 25 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "25")));
//                systems.add(system.copy().withName("Class 30 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "25")));
//            }
//            if (system.getName().equals("Class 0.75 Hyperdrive")) {
//                systems.add(system.copy().withName("Class 0.9 Hyperdrive").with(Change.create(ChangeKey.HYPERDRIVE, "0.9")));
//                systems.add(system.copy().withName("Class 0.5 Hyperdrive")
//                        .with(Change.create(ChangeKey.HYPERDRIVE, "0.5")).withAvailability("Illegal")
//                        .withPrerequisite(new SimplePrerequisite("The only way to achieve a Hyperdrive of this type is to modify a Class .75 Hyperdrive using the Starship Designer Feat.", "FEAT", "Starship Designer")));
//            }
//
//            system.with(Change.create(ChangeKey.HYPERDRIVE, hyperdriveMatcher.group(1)));
//
//
//        }
//
//
//        return systems;
//    }
//
//    private static void addItemOrVariantsToCollection(List<VehicleSystem> items, VehicleSystem current, Map<String, List<Object>> variantData) {
//        if (variantData.size() > 0) {
//            for (Map.Entry<String, List<Object>> variant : variantData.entrySet()) {
//                VehicleSystem v = current.copy();
//                items.add(v);
//                v.with(getManualAttributes(variant.getKey()));
//                v.withName(nameCleanup(variant.getKey()));
//                for (Object o : variant.getValue()) {
//                    if (o instanceof String && ((String) o).startsWith("COST:")) {
//                        v.withCost(((String) o).substring(5).trim());
//                    } else if (o instanceof Prerequisite) {
//                        v.withPrerequisite((Prerequisite) o);
//                    } else {
//                        v.with(o);
//                    }
//                }
//            }
//            variantData.clear();
//        } else {
//            current.with(getManualAttributes(current.getName()));
//            items.add(current);
//        }
//    }
//
//    private static String nameCleanup(String name) {
//        switch (name) {
//            case "Cannon, Double/Quad":
//                return "Cannon";
//            case "Blaster Cannon (Vehicles)":
//                return "Blaster Cannon";
//        }
//        return name;
//    }
//
//    private static Collection<Object> getManualAttributes(String name) {
//        List<Object> attributes = new LinkedList<>();
//
//        if (name.endsWith("Added Power Couplings")) {
//            attributes.add(Change.create(ChangeKey.EMPLACEMENT_POINTS_BONUS, name.split(" ")[0]));
//        } else {
//            //System.out.println(name);
//        }
//
//        return attributes;
//    }

}
