package swse.forceTechniques;

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

public class TechniquesExporter extends BaseExporter
{
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\techniques.csv";
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\force techniques.json";

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        talentLinks.add("/wiki/Category:Force_Techniques");


        List<JSONObject> entries = new TechniquesExporter().getEntriesFromCategoryPage(talentLinks);

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

        Set<Category> categories = Category.getCategories(doc);
//
        List<JSONy> traditions = new ArrayList<>();
        traditions.add(ForceTechnique.create(itemName).withDescription(content).withCategories(categories));

        return traditions;
    }

}
