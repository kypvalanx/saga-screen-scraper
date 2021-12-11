package swse.forceSecrets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.BaseExporter;
import swse.common.Category;

public class ForceSecretsExporter extends BaseExporter
{
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\secrets.csv";
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\force secrets.json";

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        talentLinks.add("/wiki/Force_Secrets");

        List<JSONObject> entries = new ArrayList<>();
        for(String speciesLink : talentLinks){
            entries.addAll(readItemMenuPage(speciesLink, false));
        }

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"));
    }


    private static List<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite)
    {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null)
        {
            return new ArrayList<>();
        }
        Element body = doc.body();

        Element div = body.select("div.mw-parser-output").first();
        Elements lis = div.select("li");
        Set<String> hrefs = new HashSet<>();
        lis.forEach(li -> hrefs.add(li.select("a[href]").first().attr("href")));


        return hrefs.stream().flatMap((Function<String, Stream<ForceSecret>>) itemLink -> parseItem(itemLink, overwrite).stream())
                .map(ForceSecret::toJSON).collect(Collectors.toList());
    }

    private static List<ForceSecret> parseItem(String itemLink, boolean overwrite)
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

        List<ForceSecret> traditions = new ArrayList<>();
        traditions.add(ForceSecret.create(itemName).withDescription(getDescription(content)).withCategories(categories));

        return traditions;
    }

}
