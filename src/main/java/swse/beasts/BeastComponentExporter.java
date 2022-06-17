package swse.beasts;

import com.google.common.collect.Lists;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.*;
import swse.prerequisite.SimplePrerequisite;

import java.io.File;
import java.util.*;

public class BeastComponentExporter extends BaseExporter
{
    public static final String JSON_OUTPUT = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems\\swse\\raw_export\\beast components.json";
    private static List<String> allPowers = new ArrayList<>();

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        //talentLinks.add("/wiki/Category:Force_Powers");

        //List<JSONObject> entries = new BeastComponentExporter().getEntriesFromCategoryPage(talentLinks);

        List<JSONObject> entries = new LinkedList<>();
        entries.addAll(getManualItems());

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"));
    }

    private static Collection<? extends JSONObject> getManualItems() {
        Collection<JSONObject> items = Lists.newArrayList();

        items.add(BeastComponent.create("Bite")
                .withProvided(Attribute.create(AttributeKey.DAMAGE_TYPE, "Piercing"))
                .withProvided(Attribute.create(AttributeKey.DAMAGE_TYPE, "Poison"))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")))
                .withProvided(Attribute.create(AttributeKey.DAMAGE, 1).withParentPrerequisite(new SimplePrerequisite("Colossal Size", "SIZE", "Colossal")))
                .toJSON());

        return items;
    }


    protected List<JSONy> parseItem(String itemLink, boolean overwrite)
    {
        if (null == itemLink)
        {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null)
        {
            return new ArrayList<>();
        }
        String itemName = getItemName(doc);


        if ("home".equals(itemName.toLowerCase()))
        {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        allPowers.add(itemName);
        Set<Category> categories = Category.getCategories(doc);
//
        List<JSONy> traditions = new ArrayList<>();

        traditions.add(BeastComponent.create(itemName).withDescription(content).withCategories(categories).
                withProvided(Attribute.create(AttributeKey.TAKE_MULTIPLE_TIMES, "true")));

        return traditions;
    }

}
