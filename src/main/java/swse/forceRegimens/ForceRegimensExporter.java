package swse.forceRegimens;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.BaseExporter;
import swse.common.Category;
import swse.common.JSONy;

public class ForceRegimensExporter extends BaseExporter
{
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\regimens.csv";
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\force regimens.json";

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        talentLinks.add("/wiki/Category:Force_Regimens");

        List<JSONObject> entries = new ForceRegimensExporter().getEntriesFromCategoryPage(talentLinks, true);

        printUniqueNames(entries);

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"), "Force Regimes");
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


        if ("home".equalsIgnoreCase(itemName))
        {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        Set<Category> categories = Category.getCategories(doc);
//
        List<JSONy> traditions = new ArrayList<>();
        traditions.add(ForceRegimen.create(itemName).withDescription(content).withCategories(categories));

        return traditions;
    }

}
