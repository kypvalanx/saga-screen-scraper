package swse.vehicles.stock.baseType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.BaseExporter;
import swse.common.JSONy;

public class VehicleBaseTypeExporter extends BaseExporter {
    public static final String JSON_OUTPUT = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems\\swse\\raw_export\\Vehicle Base Types.json";

    public static void main(String[] args) {

        List<String> stockTemplateLinks = new ArrayList<>();
        stockTemplateLinks.add("/wiki/New_Stock_Templates");
        stockTemplateLinks.add("/wiki/SotG_Starship_Modifications");


        List<JSONObject> entries = new ArrayList<>();

        boolean overwrite = false;
        final VehicleBaseTypeExporter vehicleBaseTypeExporter = new VehicleBaseTypeExporter();
        for (String stockTemplateLink :
                stockTemplateLinks) {
            entries.addAll(vehicleBaseTypeExporter.parseItem(stockTemplateLink, overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        }
        entries.add(createCustom());

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }

    private static JSONObject createCustom() {
        return VehicleBaseType.create("Custom").toJSON();
    }

    protected Collection<JSONy> parseItem(String itemLink, boolean overwrite) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }

        Elements tables = doc.select("table.wikitable:has(caption)");

        Map<String, JSONy> templates = new HashMap<>();
        for (Element table : tables) {
            List<String> headers = getHeaders(table);

            Elements rows = table.select("tr:has(td)");

            for (Element row : rows) {

                int templateNameIndex = Math.max(headers.indexOf("STOCK SHIP"), headers.indexOf("STOCK VEHICLE"));
                if (templateNameIndex == -1) {
                    continue;
                }
                List<String> rowValues = row.select("td").stream().map(element -> element.text().trim()).collect(Collectors.toList());
                String templateName = rowValues.get(templateNameIndex);
                VehicleBaseType template = (VehicleBaseType) templates.computeIfAbsent(templateName, tn -> VehicleBaseType.create(tn).withProvided(Attribute.create(AttributeKey.VEHICLE_SUB_TYPE, mapSubtype(tn))));

                for (int i = 0; i < headers.size(); i++) {
                    if (i == templateNameIndex) {
                        continue;
                    }
                    String key = headers.get(i);
                    String value = rowValues.get(i);

                    switch (key) {
                        case "SIZE":
                            template.withProvided(getProvidedItemOrChoiceOfProvidedItemsInList(value, "/", "Select an available size"));
                            break;
                        case "STRENGTH":
                            template.withProvided(Attribute.create(AttributeKey.BASE_STRENGTH, value));
                            break;
                        case "DEXTERITY":
                            template.withProvided(Attribute.create(AttributeKey.BASE_DEXTERITY, value));
                            break;
                        case "INTELLIGENCE":
                            template.withProvided(Attribute.create(AttributeKey.BASE_INTELLIGENCE, value));
                            break;
                        case "SPEED CHARACTER SCALE":
                            template.withProvided(Attribute.create(AttributeKey.SPEED_CHARACTER_SCALE, value));
                            break;
                        case "SPEED STARSHIP SCALE":
                            template.withProvided(Attribute.create(AttributeKey.SPEED_STARSHIP_SCALE, value));
                            break;
                        case "HIT POINTS":
                            template.withProvided(Attribute.create(AttributeKey.HIT_POINT_EQ, value));
                            break;
                        case "DR":
                            template.withProvided(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_BONUS, value));
                            break;
                        case "ARMOR":
                            template.withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, value));
                            break;
                        case "COST":
                            template.withCost(value);
                            break;
                        case "CREW":
                            template.withProvided(Attribute.create(AttributeKey.CREW, value));
                            break;
                        case "PASSENGERS":
                            template.withProvided(Attribute.create(AttributeKey.PASSENGERS, value));
                            break;
                        case "CARGO CAPACITY":
                            String[] toks = value.split(" ");

                            String unit = null;
                            if(toks.length > 1) {
                                unit = toks[1];
                            }
                            template.withProvided(Attribute.create(AttributeKey.CARGO_CAPACITY, toString(getKilograms(toks[0], unit))));
                            break;
                        case "CONSUMABLES":
                            template.withProvided(Attribute.create(AttributeKey.CONSUMABLES, value));
                            break;
                        case "EMPLACEMENT POINTS":
                        case "UNUSED EMPLACEMENT POINTS":
                            template.withProvided(Attribute.create(AttributeKey.EMPLACEMENT_POINTS, value));
                            break;
                    }
                }
            }

        }


        return templates.values();
    }

    private static String mapSubtype(String tn) {

        switch (tn) {
            case "Light Fighter":
            case "Bomber":
            case "Superiority Fighter":
            case "Interceptor":
                return "Starfighter";
            case "Heavy Freighter":
            case "Gunship":
            case "Shuttle":
            case "Light Freighter":
            case "Medium Freighter":
            case "Barge":
                return "Space Transport";
            case "Battlecruiser":
            case "Cruiser":
            case "Frigate":
            case "Corvette":
            case "Super Freighter":
                return "Capital Ship";
            case "Airspeeder":
            case "Airhook":
            case "Light Airspeeder":
                return "Airspeeder";
            case "Armored Walker":
            case "Scout Walker":
            case "Patrol Walker":
            case "Assault Walker":
                return "Walker";
            case "Speeder Truck":
            case "Landspeeder":
            case "Speeder Bike":
            case "Swoop":
            case "Repulsor Tank":
                return "Speeder";
            case "Wheelbike":
            case "Ground Assault Vehicle":
                return "Wheeled Vehicle";
            case "Artillery Tank":
            case "Heavy Armored Vehicle":
                return "Tracked Vehicle";
            default:
                System.out.println(tn);
        }

        return null;
    }

}
