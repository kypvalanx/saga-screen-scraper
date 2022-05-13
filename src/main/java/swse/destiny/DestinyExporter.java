package swse.destiny;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.BaseExporter;
import swse.common.JSONy;

public class DestinyExporter extends BaseExporter {
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\destiny.json";

    public static void main(String[] args) {
        List<String> destinyLinks = new LinkedList<>();
        destinyLinks.add("/wiki/Destiny");
        final boolean overwrite = false;

        final DestinyExporter destinyExporter = new DestinyExporter();
        List<JSONObject> entries = new ArrayList<>();
        for (String destinyLink : destinyLinks) {
            entries.addAll(destinyExporter.readItemMenuPage(destinyLink, overwrite));
        }


        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }


    private Collection<? extends JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null) {
            return new ArrayList<>();
        }
        Element body = doc.body();

        Elements tables = body.select("table.wikitable");

        List<String> hrefs = new ArrayList<>();
        tables.forEach(table ->
        {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row ->
            {
                Elements tds = row.getElementsByTag("td");
                Element first = tds.first();
                if (first != null) {
                    Element anchor = first.getElementsByTag("a").first();
                    if (anchor != null) {
                        hrefs.add(anchor.attr("href"));
                    }
                }
            });
        });


        return hrefs.stream().flatMap(itemLink -> parseItem(itemLink, overwrite).stream()).map(item -> item.toJSON()).collect(Collectors.toList());

    }

    protected List<JSONy> parseItem(String itemLink, boolean overwrite) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }
        String itemName = getItemName(doc);

        if ("home".equals(itemName)) {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        content.select("img,figure").remove();

        List<JSONy> templates = new ArrayList<>();

        List<Element> ps = getParagraphs(content);
        String description = ps.stream()
                .filter(p -> !p.text().startsWith("Destiny Bonus") && !p.text().startsWith("Destiny Penalty")
                        && !p.text().startsWith("See also") && !p.text().startsWith("Destiny Fulfilled") && !p.text()
                        .startsWith("Reference Book")).map(Node::toString).collect(Collectors.joining());

        templates.add(Destiny
                .create(itemName)
                .withDescription(content)
                .withProvided(getDestinyAttributes(content)));

        return templates;
    }

    private Collection<?> getDestinyAttributes(Element content) {
        List<Object> response = new LinkedList<>();
        response.add(Attribute.create(AttributeKey.DESTINY_BONUS, getAttribute(content, "Destiny Bonus:")));
        response.add(Attribute.create(AttributeKey.DESTINY_PENALTY, getAttribute(content, "Destiny Penalty:")));
        response.add(Attribute.create(AttributeKey.DESTINY_FULFILLED, getAttribute(content, "Destiny Fulfilled:")));
        return response;
    }
}
