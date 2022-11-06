package swse.common;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeFilter;

public abstract class BaseExporter {
    public static String ROOT = "https://swse.fandom.com";
public static String SYSTEM_LOCATION = "C:/Users/lijew/AppData/Local/FoundryVTT/Data/systems/swse";

    protected static void writeToDB(File jsonOutputFile, List<JSONObject> entries, boolean dryRun) {
        if (dryRun) {
            return;
        }
//        JSONObject data = new JSONObject();
//        StringWriter writer = new StringWriter();
//
//        JSONArray jsonArray = new JSONArray();
//
//        jsonArray.putAll(entries);
//
//        data.put("version", Instant.now().toEpochMilli());
//
//        data.put("entries", jsonArray);
//        data.write(writer);

        try {
            jsonOutputFile.getParentFile().mkdirs();
            jsonOutputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(jsonOutputFile)) {
            for (Iterator<JSONObject> it = entries.iterator(); it.hasNext(); ) {
                JSONObject o = it.next();
                pw.print(o);
            }
            //pw.print(writer);
        } catch (FileNotFoundException e) {
            System.err.println("oh fuck");
        }
    }
    protected static void writeToJSON(File jsonOutputFile, List<JSONObject> entries, boolean dryRun) {
        if (dryRun) {
            return;
        }
        JSONObject data = new JSONObject();
        StringWriter writer = new StringWriter();

        JSONArray jsonArray = new JSONArray();

        jsonArray.putAll(entries);

        data.put("version", Instant.now().toEpochMilli());

        data.put("entries", jsonArray);
        data.write(writer);

        try {
            jsonOutputFile.getParentFile().mkdirs();
            jsonOutputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(jsonOutputFile)) {
            pw.print(writer);
        } catch (FileNotFoundException e) {
            System.err.println("oh fuck");
        }
    }



    public static void addIdsFromDb(File dbFile, List<JSONObject> entries) {
        Map<String, String> nameToId = new HashMap<>();
        try {
            Scanner myReader = new Scanner(dbFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                JSONObject o = new JSONObject(data);
                nameToId.put(o.getString("name"), o.getString("_id"));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        for(JSONObject o : entries){
            String id = nameToId.get(o.getString("name"));
            if(id == null){
                System.out.println(o);
            }
            o.put("_id", id);
        }
    }

    protected static boolean hasArg(String[] args, String arg) {
        return Arrays.stream(args).anyMatch(arg::equalsIgnoreCase);
    }


    protected static String getItemName(Document doc) {
        Elements headingElements = doc.select(".page-header__title");

        if (headingElements.size() == 0 || headingElements.first() == null) {
            System.out.println(doc);
        }

        if (headingElements.size() > 1) {
            throw new IllegalStateException("too many headers " + headingElements);
        }
        return headingElements.first().text();
    }


    protected static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize(); ) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }


    public static List<String> getNames(List<JSONObject> names){
        return names.stream().map(name -> name.getString("name")).collect(Collectors.toList());
    }

    public static List<JSONObject> getOverrides(String folderName) {
        File folder = new File("src/main/resources/manual/" + folderName);
        List<JSONObject> manualEntries = new ArrayList<>();
        if(!folder.exists()){
            return manualEntries;
        }
        List<File> files = List.of(Objects.requireNonNull(folder.listFiles(pathname -> pathname.getName().endsWith(".json"))));

        for(File file : files){
            InputStream is = null;
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (is == null) {
                throw new NullPointerException("Cannot find resource file " + file.getAbsolutePath());
            }

            JSONTokener tokener = new JSONTokener(is);
            JSONObject object = new JSONObject(tokener);
            manualEntries.add(object);
        }
        return manualEntries;
    }

    protected static Document getDoc(String itemPageLink, boolean overwrite) {
        try {


            File local = new File(clean(itemPageLink) + ".html");

            if (overwrite && local.exists()) {
                local.delete();
            }

            String root = "https://swse.fandom.com";
            if (!local.exists()) {
                String[] toks = itemPageLink.split("/");
                String s = "C:";
                for (int i = 0; i < toks.length - 1; i++) {
                    s = s.concat("/" + toks[i]);
                    new File(s).mkdir();
                }
                try {
                    local.getParentFile().mkdirs();
                    local.createNewFile();

                    FileUtils.copyURLToFile(
                            new URL(root + itemPageLink),
                            local, 10000
                            ,
                            10000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return Jsoup.parse(local, "UTF-8", root);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    private static String clean(String itemPageLink) {
        return itemPageLink.replace(":", "").replace("?", "").replace("=", "");
    }

    protected static void drawProgressBar(double v) {
        String s = "";
        for (int i = 0; i < 100; i++) {
            s = s.concat(i < v ? "#" : " ");
        }
        System.out.print("|" + s + "|\r");
    }

    public static String getDescription(Element content) {
        if (content == null) {
            return "";
        }

        content = content.clone();
        content.select("span.mw-editsection").remove();
        final Elements toc = content.select("div.toc");
        try {
            toc.remove();
        }catch(Exception e){
            //Ignore
        }
        final Elements figure = content.select("img,figure");
        if(figure.size() > 0) {
            figure.remove();
        }

        Elements anchors = content.select("a");
        for (Element anchor : anchors) {
            if (!anchor.attr("href").startsWith("http")) {
                anchor.attr("href", "https://swse.fandom.com" + anchor.attr("href"));
            }
        }



        StringBuilder response = new StringBuilder();
        for (Node node : content.childNodes()) {

            if(node instanceof Element){
                String text = ((Element)node).text().toLowerCase();
                if (text.contains("return to main article") || text.contains("weapon type:") || text
                        .contains("size:") || text.contains("cost:") || text.contains("damage:") || text
                        .contains("stun setting:") || text.contains("type:") || text.contains("weight:") || text
                        .contains("rate of fire:") || text.contains("availability:") || text.isBlank()) {
                    continue;
                }
            } else if (node instanceof Comment) {continue;
            } else if (node instanceof TextNode && ((TextNode) node).text().isBlank()) {continue;
            }
            response.append(node.toString().trim());

        }

        return response.toString().trim();
    }

    protected static Object getProvidedItemOrChoiceOfProvidedItemsInList(String value, String listDelimiter, String description) {
        Object provided;
        if (value.contains(listDelimiter)) {
            String[] toks = value.split(listDelimiter);
            Choice choice = new Choice(description);
            for (String tok : toks) {
                String val = tok.trim();
                choice.withOption(val, new Option().withProvidedItem(ProvidedItem.create(val, ItemType.TRAIT)));
            }
            provided = choice;
        } else {
            provided = ProvidedItem.create(value, ItemType.TRAIT);
        }
        return provided;
    }

    protected static ArrayList<String> getHeaders(Element table) {
        ArrayList<String> headers = new ArrayList<>();

        Elements rows = table.select("tr:has(th)");

        final ListIterator<Element> rowsIterator = rows.listIterator();
        Elements first = rowsIterator.next().select("th");
        ListIterator<Element> next = null;
        if (rowsIterator.hasNext()) {
            next = rowsIterator.next().select("th").listIterator();
        }

        for (Element primary : first) {
            if (!primary.attr("rowspan").equals("") && Integer.parseInt(primary.attr("rowspan")) > 2) {
                continue;
            }
            if (primary.attr("rowspan").equals("2") || next == null) {
                headers.add(primary.text().trim());
            } else if (next != null) {
                final String colspan = primary.attr("colspan");
                int num = 1;
                if (colspan != null && !"".equals(colspan)) {
                    num = Integer.parseInt(colspan);
                }
                for (int i = 0; i < num; i++) {
                    try {
                        String e = primary.text().trim();
                        if (next.hasNext()) {
                            e += " " + next.next().text().trim();
                        }
                        headers.add(e);
                    } catch (NoSuchElementException d) {
                        System.out.println();
                    }
                }
            }
        }

        return headers;
    }

    protected static List<String> getAlphaLinks(String alphaCategory) {
        return getAlphaLinks(alphaCategory, 'Z');
    }

    protected static List<String> getAlphaLinks(String alphaCategory, char limit) {
        List<String> expandedLinks = new LinkedList<>();
        for (char alpha = 'A'; alpha <= limit; alpha++) {

            expandedLinks.add(alphaCategory + alpha);
        }
        return expandedLinks;
    }

    public static String toString(double numeric) {
        String s = Double.toString(numeric);
        if (s.contains("E")) {
            String[] toks = s.split("E");
            int exp = Integer.parseInt(toks[1]);

            if (exp > 0) {
                String[] nums = toks[0].split("\\.");
                StringBuilder response = new StringBuilder(nums[0]);

                for (int i = 0; i < exp; i++) {
                    final String num = nums[1];
                    if (num.length() > i) {
                        response.append(num.charAt(i));
                    } else {
                        response.append(0);
                    }
                }
                s = response.toString();
            }

        }
        return s;
    }

    public static double getKilograms(String value, String unit) {
        if (value.equalsIgnoreCase("None")) {
            return 0.0d;
        }
        double numeric = Double.parseDouble(value.replace(",", ""));

        if (!unit.startsWith("Ton")) {
            numeric /= 1000;
        }
        return numeric;
    }

    protected static List<Element> getParagraphs(Element content) {
        List<Element> response = new LinkedList<>();
        for (Element element : content.children()) {
            if (element.tag().equals(Tag.valueOf("p"))) {
                response.add(element);
            }
        }

        return response;
    }

    public static String getAttribute(Element content, String attributeName) {
        for (Element child : content.children()) {
            if (child.text().toLowerCase().startsWith(attributeName.toLowerCase())) {
                return child.text().substring(attributeName.length());
            }
        }
        return null;
    }


    //        List<JSONObject> entries = new LinkedList<>();
    //        List<String> names = new LinkedList<>();
    //        boolean overwrite = false;
    //        for (String vehicleSystemLink :
    //                vehicleSystemLinks) {
    //            final List<JSONObject> newEntities = readItemMenuPage(vehicleSystemLink, overwrite);
    //            for (JSONObject newEntity : newEntities) {
    //                if (names.contains(newEntity.get("name"))) {
    //                    System.out.println("Duplicate: " + newEntity.get("name") + " from: " + vehicleSystemLink);
    //                } else {
    //                    names.add((String) newEntity.get("name"));
    //                    entries.add(newEntity);
    //                }
    //            }
    //            drawProgressBar(entries.size() * 100.0 / 647.0);
    //        }


    public static void printUniqueNames(List<JSONObject> entries) {
        List<String> names = new LinkedList<>();
        for(JSONObject entry : entries){
            names.add((String) entry.get("name"));
        }

        System.out.println("List.of(\"" + names.stream().map(s -> s.replaceAll("\"", "\\\"")).collect(Collectors.joining("\", \"")) + "\")");
    }

    abstract protected Collection<JSONy> parseItem(String itemLink, boolean overwrite);

    private Collection<? extends JSONObject> readCategoryItemPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null) {
            return new ArrayList<>();
        }
        Element body = doc.body();

        List<String> hrefs = new LinkedList<>();

        Elements links = body.getElementsByClass("category-page__member-link");

        links.forEach(a -> hrefs.add(a.attr("href")));

        return hrefs.stream()
                .flatMap((Function<String, Stream<JSONy>>) itemLink -> parseItem(itemLink, overwrite).stream())
                .map(jsoNy -> jsoNy.toJSON()).collect(Collectors.toList());
    }

    public List<JSONObject> getEntriesFromCategoryPage(List<String> talentLinks, boolean overwrite) {
        return getEntriesFromCategoryPage(talentLinks, overwrite, List.of());
    }
    public List<JSONObject> getEntriesFromCategoryPage(List<String> talentLinks, boolean overwrite, List<String> exclusion) {
        List<JSONObject> entries = new ArrayList<>();
        List<String> names = new LinkedList<>();
        for (String talentLink : talentLinks) {
            //entries.addAll(readCategoryItemPage(talentLink, false));
            Collection<? extends JSONObject> newEntities = List.of();
            if(talentLink.contains("Category")) {
                newEntities = readCategoryItemPage(talentLink, overwrite);
            } else {
                newEntities = parseItem(talentLink, overwrite).stream().map(jsoNy -> jsoNy.toJSON()).collect(Collectors.toList());
            }
            for (JSONObject newEntity : newEntities) {
                String name = newEntity.getString("name");
                if(exclusion.contains(name)){
                    continue;
                }
                if (names.contains(name)) {
                    System.out.println("Duplicate: " + name + " from: " + talentLink);
                } else {
                    names.add(name);
                    entries.add(newEntity);
                }
            }
        }

        return entries;
    }
}
