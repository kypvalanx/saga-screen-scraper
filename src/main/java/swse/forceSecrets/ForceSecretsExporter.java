package swse.forceSecrets;

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

public class ForceSecretsExporter extends BaseExporter
{
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\secrets.csv";
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\force secrets.json";

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        talentLinks.add("/wiki/Category:Force_Secrets");
        talentLinks.add("/wiki/Pure_Power");

        List<JSONObject> entries = new ForceSecretsExporter().getEntriesFromCategoryPage(talentLinks, true);
        printUniqueNames(entries);

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"), "Force Secrets");
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


        if ("home".equals(itemName.toLowerCase()))
        {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        Set<Category> categories = Category.getCategories(doc);

        List<JSONy> traditions = new ArrayList<>();
        traditions.add(ForceSecret.create(itemName).withDescription(content).withCategories(categories));

        return traditions;
    }

}
