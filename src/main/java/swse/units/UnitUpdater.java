package swse.units;

import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONObject;
import swse.common.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class UnitUpdater extends BaseExporter {
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\Units-Player";
    public static String PATH = "E:/kotor2/data/actors.db";
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(PATH));
        List<JSONObject> units = Lists.newArrayList();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            JSONObject unit = parseUnit(line);
            units.add(unit);
        }
        writeToJSON(new File(JSON_OUTPUT  + ".json"), units, hasArg(args, "d"), "Units-players", "Actor");
    }

    private static JSONObject parseUnit(String line) {
        JSONObject json = new JSONObject(line);

        JSONObject data = json.getJSONObject("data");

        Unit unit = new Unit(json.getString("name"))
                .withType(json.getString("type"))
                .withImage(json.getString("img"))
                .withProvided(convertItemsToImportableItems(json.getJSONArray("items")))
                .withSystem(data);


        JSONObject skills = data.getJSONObject("skills");

        for (String key : skills.keySet()) {
            JSONObject skill = skills.getJSONObject(key);
            if(skill.getBoolean("trained")){
                unit.withTrainedSkill(key);
            }
        }

        JSONObject attributes = data.getJSONObject("attributes");
        for (String key : attributes.keySet()) {
            JSONObject skill = attributes.getJSONObject(key);
            unit.withAttribute(Attribute.createWithBase(ChangeKey.valueOf(key.toUpperCase()), ""+skill.getInt("base")));
            //unit.withAttribute(key.toUpperCase(), ""+skill.getInt("base"));
        }

        return unit
                .toJSON();
    }

    private static Collection<?> convertItemsToImportableItems(JSONArray items) {
        List<ProvidedItem> providedItems = Lists.newArrayList();
        for (Object o :
                items.toList()) {
            HashMap<String,String> item = (HashMap) o;

            if(item.get("type").equals("trait")){
                continue;
            }
            providedItems.add(ProvidedItem.create(item.get("name"), getType(item.get("type"))));
        }
        return providedItems;
    }

    private static ItemType getType(String type) {
        type = type.toUpperCase();
        if(type.equals("FORCEPOWER")){
            type = "FORCE_POWER";
        } else if ("EQUIPMENT".equals(type)) {
            type = "ITEM";
        }
        return ItemType.valueOf(ItemType.class, type);
    }

    @Override
    protected Collection<JSONy> parseItem(String itemLink, boolean overwrite) {
        return null;
    }
}
