package swse.language;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.BaseExporter;
import swse.common.JSONy;

public class LanguageExporter extends BaseExporter {
    public static final String IMAGE_FOLDER = "systems/swse/icon/species";
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\languages.json";


    public static void main(String[] args) {
        List<String> speciesLinks = new ArrayList<>(getAlphaLinks("/wiki/Category:Species?from="));
        speciesLinks.add("/wiki/Droid_Heroes");
        speciesLinks.add("/wiki/Droid_Chassis");

        List<JSONObject> entries = new ArrayList<>();
        for (String itemLink : speciesLinks) {
            entries.addAll(readItemMenuPage(itemLink, true));
        }

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }


    private static List<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);

        Element body = doc.body();

        List<String> hrefs = new LinkedList<>();

        Elements links = body.getElementsByClass("category-page__member-link");

        links.forEach(a -> hrefs.add(a.attr("href")));

        Elements tables = body.getElementsByClass("wikitable");

        //Map<String, String> hrefs = new HashMap<>();
        tables.forEach(table -> {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row -> {
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

        final LanguageExporter speciesExporter = new LanguageExporter();

        return hrefs.stream().flatMap((Function<String, Stream<JSONy>>) itemLink -> speciesExporter
                .parseItem(itemLink, overwrite).stream()).map(item ->item.toJSON()).collect(Collectors.toList());
    }

    protected List<JSONy> parseItem(String itemLink, boolean overwrite) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);


        Elements headingElements = doc.getElementsByClass("page-header__title");

        if (headingElements.size() > 1) {
            throw new IllegalStateException("too many headers " + headingElements);
        }

        String speciesName = headingElements.first().text();


        if ("home".equals(speciesName)) {
            return new ArrayList<>();
        }


        Element content = doc.getElementsByClass("mw-parser-output").first();


        try {
            //imageFile = getSpeciesImage(content.select("img.thumbimage").first());
        } catch (NullPointerException e) {
            //System.out.println(itemLink.getKey());
        }


        for(Element child : content.children()){
            if(child.tag().equals(Tag.valueOf("ul"))){
                for(Element subChild : child.children()){
                    getLanguages(subChild);
                }
            } else {
                getLanguages(child);
            }
        }

        //Language language = Language.create(speciesName);

        return Lists.newArrayList();
    }





    private static Collection<?> getLanguages(Element child) {

        List<Object> provided = new ArrayList<>();

        if(child.text().contains("Language")) {
           // System.out.println(child.text());
        }
        return provided;
    }

}
