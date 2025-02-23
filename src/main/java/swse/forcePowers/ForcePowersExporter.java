package swse.forcePowers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import swse.common.Change;
import swse.common.ChangeKey;
import swse.common.BaseExporter;
import swse.common.Category;
import swse.common.JSONy;

public class ForcePowersExporter extends BaseExporter
{
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\force powers.json";
    private static List<String> allPowers = new ArrayList<>();

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        talentLinks.add("/wiki/Category:Force_Powers");

        List<JSONObject> entries = new ForcePowersExporter().getEntriesFromCategoryPage(talentLinks, true);
        printUniqueNames(entries);

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"), "Force Powers");
    }


    protected List<JSONy> parseItem(String itemLink, boolean overwrite, List<String> filter, List<String> nameFilter)
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


        if ("home".equals(itemName.toLowerCase()) || "Force Powers".equals(itemName))
        {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        allPowers.add(itemName);
        Set<Category> categories = Category.getCategories(doc);
//
        List<JSONy> forcePowers = new ArrayList<>();



        forcePowers.add(ForcePower
                .create(itemName)
                .with(getTags(content))
                .with(getTime(content))
                .with(getTargets(content))
                .with(getRollType(content))
                .withDescription(content)
                        .with(createCumulativeChecks(content))
                .withCategories(categories)
                .with(Change.create(ChangeKey.TAKE_MULTIPLE_TIMES, "true")
                ));

        return forcePowers;
    }

}
