package swse.forcePowers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.BaseExporter;
import swse.common.Category;
import swse.common.JSONy;

public class ForcePowersExporter extends BaseExporter
{
    public static final String JSON_OUTPUT = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems\\swse\\raw_export\\force powers.json";
    private static List<String> allPowers = new ArrayList<>();

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        talentLinks.add("/wiki/Category:Force_Powers");

        List<JSONObject> entries = new ForcePowersExporter().getEntriesFromCategoryPage(talentLinks);
        printUniqueNames(entries);

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"));
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

        traditions.add(ForcePower.create(itemName).withDescription(content).withCategories(categories).
                withProvided(Attribute.create(AttributeKey.TAKE_MULTIPLE_TIMES, "true")));

        return traditions;
    }

}
