package swse.forcePowers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.NodeFilter;
import swse.common.Change;
import swse.common.ChangeKey;
import swse.common.BaseExporter;
import swse.common.Category;
import swse.common.JSONy;

import static org.w3c.dom.traversal.NodeFilter.FILTER_ACCEPT;

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
                .withDescription(content)
                        .with(createForcePowerChecks(content))
                .withCategories(categories)
                .with(Change.create(ChangeKey.TAKE_MULTIPLE_TIMES, "true")
                ));

        return forcePowers;
    }

    private Collection<?> createForcePowerChecks(Element content) {
        content = content.clone();
        Collection<Change> changes = new ArrayList<>();

        List<Node> childNodes = content.childNodes();
        for (int i = childNodes.size() - 1 ; i >= 0; i--) {
            Node element = childNodes.get(i);
            if (element instanceof Comment || element instanceof TextNode) {
                element.remove();
            }
        }

        Elements table = content.select("table.wikitable").remove();


        if(table.size() == 1){
            boolean foundHeader = false;
            for (Element tr : table.get(0).children().get(0).children()) {
                Elements rowItems = tr.children();
                String dc = rowItems.get(0).text();
                String effect = rowItems.get(1).text().length() > 0 ? rowItems.get(1).text() : rowItems.get(2).text();

                if ("DC".equals(dc) && "EFFECT".equals(effect)) {
                    foundHeader = true;
                    continue;
                }
                changes.add(Change.create(ChangeKey.CHECK, dc + ":" + effect));
                System.out.println(dc + " " + effect);
            }
            if(!foundHeader){
                throw new RuntimeException("failed to find header");
            }
        }


        changes.add(Change.create(ChangeKey.FORCE_POWER_DESCRIPTION, content.text()));

        return changes;
    }

}
