package swse.background;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.Change;
import swse.common.ChangeKey;
import swse.common.BaseExporter;
import swse.common.Choice;
import swse.common.ItemType;
import swse.common.JSONy;
import swse.common.Option;
import swse.common.ProvidedItem;

public class BackgroundExporter extends BaseExporter {
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\background.json";

    public static void main(String[] args) {
        List<String> destinyLinks = new LinkedList<>();
        destinyLinks.add("/wiki/Background");
        final boolean overwrite = true;

        final BackgroundExporter backgroundExporter = new BackgroundExporter();
        List<JSONObject> entries = new ArrayList<>();
        for (String destinyLink : destinyLinks) {
            entries.addAll(backgroundExporter.readItemMenuPage(destinyLink, overwrite));
        }


        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"), "Backgrounds");
    }


    private Collection<? extends JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null) {
            return new ArrayList<>();
        }
        Element body = doc.body();

        Elements tables = body.select("table.wikitable");

        List<Map<Integer, String>> hrefs = new ArrayList<>();
        tables.forEach(table ->
        {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row ->
            {
                Elements tds = row.getElementsByTag("td");
                if(tds.size() > 0) {
                    Element first = tds.first();
                    Element second = tds.get(1);
                    Element third = tds.get(2);
                    if (first != null) {
                        Element anchor = first.getElementsByTag("a").first();
                        if (anchor != null) {
                            Map<Integer, String> map = new HashMap<>();
                            map.put(0, anchor.attr("href"));
                            map.put(1, second.text());
                            map.put(2, third.text());

                            hrefs.add(map);
                        }
                    }
                }
            });
        });


        return hrefs.stream()
                .flatMap(itemLink -> parseItem(itemLink.get(0), itemLink.get(1), itemLink.get(2), overwrite).stream())
                .map(item -> item.toJSON()).collect(Collectors.toList());

    }

    protected List<JSONy> parseItem(String itemLink, String secondColumn, String thirdColumn, boolean overwrite) {
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

        List<JSONy> backgrounds = new ArrayList<>();


        String subtype = "";

        if (doc.text().contains("Background Event")) {
            subtype = "event";
        } else if (doc.text().contains("Background Occupation")) {
            subtype = "occupation";
        } else if (doc.text().contains("Background Planet of Origin")) {
            subtype = "planet of origin";
        }


//        List<Element> ps = getParagraphs(content);
//        String description = ps.stream()
//                .filter(p -> !p.text().startsWith("Relevant Skills") && !p.text().startsWith("Reference Book")
//                        && !p.text().startsWith("See also") && !p.text().startsWith("Background") && !p.text()
//                        .startsWith("Bonus Language")).map(Node::toString).collect(Collectors.joining());

        backgrounds.add(Background
                .create(itemName)
                .withSubtype(subtype)
                .withDescription(content)
                .withProvided(getSkillChoice(content, subtype))
                .withProvided(getAttributeFromSecondColumn(secondColumn, subtype))
                .withProvided(getAttributeFromName(itemName, subtype)));

        return backgrounds;
    }


    private Collection<?> getAttributeFromName(String itemName, String subtype) {
        Collection<Object> response = new LinkedList<>();
        switch (itemName) {
            case "Enslaved":
                response.add(Change.create(ChangeKey.GRAPPLE_BONUS, 2));
                break;
            case "Exiled":
                response.add(ProvidedItem.create("Skill Focus (Knowledge (Galactic Lore))", ItemType.FEAT));
                break;
        }
        return response;
    }

    private Collection<?> getAttributeFromSecondColumn(String secondColumn, String subtype) {

        Collection<Object> response = new LinkedList<>();
        if ("event".equals(subtype)) {
            response.add(Change.create(ChangeKey.SPECIAL, secondColumn));
        }
        return response;
    }

    private Collection<?> getSkillChoice(Element content, String subtype) {

        Collection<Object> response = new LinkedList<>();
        String attr = getAttribute(content, "Relevant Skills:");
        String bonusLanguage = getAttribute(content, "Bonus Language:");
        List<String> csv = getValuesFromString(attr);
        List<String> skills = explodeSkills(csv);

        Choice choice = new Choice("Select an additional class skill");
        for (String skill : skills) {
            choice.withOption(skill, new Option().withChange(Change.create(ChangeKey.CLASS_SKILL, skill)));
        }

        if ("planet of origin".equals(subtype)) {
            choice.withAvailableSelections(2);

            response.add(ProvidedItem.create(bonusLanguage, ItemType.LANGUAGE));
        }
        response.add(choice);

        if ("occupation".equals(subtype)) {
            for (String skill : skills) {
                response.add(Change.create(ChangeKey.UNTRAINED_SKILL_BONUS, skill).withModifier("2"));
            }
        }
        return response;
    }

    private List<String> explodeSkills(List<String> csv) {
        List<String> response = new LinkedList<>();

        for (String i :
                csv) {
            if ("Knowledge (Any)".equals(i)) {
                response.add("Knowledge (Bureaucracy)");
                response.add("Knowledge (Galactic Lore)");
                response.add("Knowledge (Life Sciences)");
                response.add("Knowledge (Physical Sciences)");
                response.add("Knowledge (Social Sciences)");
                response.add("Knowledge (Tactics)");
                response.add("Knowledge (Technology)");
            } else {
                response.add(i);
            }
        }

        return response;
    }

    private List<String> getValuesFromString(String attr) {
        attr = attr.replaceAll(" and ", " ");
        List<String> vals = List.of(attr.split(","));
        return vals.stream().map(String::trim).collect(Collectors.toList());
    }

    @Override
    protected Collection<JSONy> parseItem(String itemLink, boolean overwrite) {
        return null;
    }
}
