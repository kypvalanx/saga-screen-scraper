package swse.affiliation;

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

public class AffiliationExporter extends BaseExporter
{
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\affiliations.json";

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        talentLinks.add("/wiki/Category:Affiliations");

        List<JSONObject> entries = new ArrayList<>();


        for(String speciesLink : talentLinks){
            entries.addAll(readItemMenuPage(speciesLink, true));
        }

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args,"d"));
    }


    private static List<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite)
    {

        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null)
        {
            return new ArrayList<>();
        }
        Element body = doc.body();

//        Set<String> hrefs = new HashSet<>();
//
//        Elements tables = body.getElementsByClass("wikitable");
//
//        tables.forEach(table ->
//        {
//            Elements rows = table.getElementsByTag("tr");
//            rows.forEach(row ->
//            {
//                Element first = row.getElementsByTag("td").first();
//                if (first != null)
//                {
//                    Element anchor = first.select("a[href]").first();
//                    if (anchor != null)
//                    {
//                        hrefs.add(anchor.attr("href"));
//                    }
//                }
//            });
//        });

        Elements as = body.select("a.category-page__member-link");

        Set<String> hrefs = new HashSet<>();
        as.forEach(a -> hrefs.add(a.attr("href")));

        return hrefs.stream().flatMap((Function<String, Stream<Affiliation>>) itemLink -> parseItem(itemLink, overwrite).stream())
                .map(Affiliation::toJSON).collect(Collectors.toList());
    }

    private static List<Affiliation> parseItem(String itemLink, boolean overwrite)
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
        List<Affiliation> traditions = new ArrayList<>();
        traditions.add(Affiliation.create(itemName).withDescription(content).withCategories(categories));

        return traditions;
    }

}
