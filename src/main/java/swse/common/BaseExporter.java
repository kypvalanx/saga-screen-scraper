package swse.common;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.iq80.leveldb.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import static org.fusesource.leveldbjni.JniDBFactory.*;
import static swse.util.Util.printUnique;


public abstract class BaseExporter {
    public static final String LOCAL_ROOT = "C:/Users/lijew/AppData/Local/FoundryVTT/Data/";
    public static final String IMAGE_FOLDER = "systems/swse/icon";
    public static String ROOT = "https://swse.fandom.com";
public static String SYSTEM_LOCATION = "C:/Users/lijew/AppData/Local/FoundryVTT/Data/systems/swse";
    protected static Map<String, String> availableFiles = new HashMap<>();
    protected static List<String> itemsWithoutImages = new ArrayList<>();

    protected static void writeToDB(File dbFile, List<JSONObject> entries, boolean dryRun) throws IOException {
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
        DB levelDBStore;
        Options options = new Options();
        levelDBStore = factory.open(dbFile, options);
        try {


//            WriteBatch batch = levelDBStore.createWriteBatch();
//
//            int i = 0;
//            for (JSONObject entry : entries) {
//                batch.put(bytes(Integer.toString(i)), bytes(entry.toString()));
//            }
//
//            levelDBStore.write(batch);
        } finally {

            levelDBStore.close();
        }


//        try {
//            jsonOutputFile.getParentFile().mkdirs();
//            jsonOutputFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try (PrintWriter pw = new PrintWriter(jsonOutputFile)) {
//            for (Iterator<JSONObject> it = entries.iterator(); it.hasNext(); ) {
//                JSONObject o = it.next();
//                pw.print(o);
//            }
//            //pw.print(writer);
//        } catch (FileNotFoundException e) {
//            System.err.println("oh fuck");
//        }
    }
    protected static void writeToJSON(File jsonOutputFile, Collection<JSONObject> entries, boolean dryRun, String name) {
        writeToJSON( jsonOutputFile,  entries,  dryRun,  name,  "Item");
    }
    protected static void writeToJSON(File jsonOutputFile, Collection<JSONObject> entries, boolean dryRun, String name, String type) {
        if (dryRun) {
            return;
        }
        JSONObject data = new JSONObject();
        StringWriter writer = new StringWriter();

        JSONArray jsonArray = new JSONArray();

        jsonArray.putAll(entries);

        data.put("version", Instant.now().toEpochMilli());
        data.put("name", name);
        data.put("type", type);

        data.put("entries", jsonArray);
        data.write(writer, 4, 0);

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

        File parent = jsonOutputFile.getParentFile();


        File manifest = new File(parent.getAbsolutePath() + "/manifest.json");
        try{
            manifest.delete();
            manifest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject manifestData = new JSONObject();
        StringWriter manifestWriter = new StringWriter();
        JSONArray fileArray = new JSONArray();

        for(File file : Objects.requireNonNull(parent.listFiles(file -> !file.getName().endsWith("manifest.json")))){
            fileArray.put(file.getAbsolutePath().substring(45));
        }




        manifestData.put("files", fileArray);
        manifestData.write(manifestWriter);
        try (PrintWriter pw = new PrintWriter(manifest)) {
            pw.print(manifestWriter);
        } catch (FileNotFoundException e) {
            System.err.println("oh fuck");
        }
    }



    public static void addIdsFromDb(File dbFile, List<JSONObject> entries) throws IOException {
        //DB levelDBStore;
        if(Objects.requireNonNull(dbFile.listFiles((dir, name) -> name.endsWith("sst"))).length == 0){
            for(File f : Objects.requireNonNull(dbFile.listFiles((dir, name) -> name.endsWith("ldb")))){
                Files.createLink(Paths.get(f.getAbsolutePath().replace(".ldb", ".sst")), Paths.get(f.getAbsolutePath()));
            }
        }

        Options options = new Options();
        DB levelDBStore = factory.open(dbFile,options);

        DBIterator iterator = levelDBStore.iterator();

        try {
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> item = iterator.next();

                System.out.println(item);
            }
        } finally {
            levelDBStore.close();
        }

//        Map<String, String> nameToId = new HashMap<>();
//        try {
//            Scanner myReader = new Scanner(dbFile);
//            while (myReader.hasNextLine()) {
//                String data = myReader.nextLine();
//                JSONObject o = new JSONObject(data);
//                nameToId.put(o.getString("name"), o.getString("_id"));
//            }
//            myReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//        for(JSONObject o : entries){
//            String id = nameToId.get(o.getString("name"));
//            if(id == null){
//                System.out.println(o);
//            }
//            o.put("_id", id);
//        }
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
        return names.stream().map(name -> name.getJSONObject("data").getString("name")).collect(Collectors.toList());
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
            if(!object.has("data")){
                JSONObject temp = new JSONObject();
                temp.put("data", object);
                object = temp;
            }
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
        return itemPageLink.replace(":", "").replace("?", "").replace("=", "").replace("\\", "");
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
        removeEditSpan(content);
        removeTableOfContents(content);
        removeImagesAndFigures(content);
        removeComments(content);
        makeLocalLinksAbsolute(content);


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

        return getDescription(response.toString().trim());
    }

    public static String getSource(Element content) {
        if (content == null) {
            return "";
        }


        //"Reference Book":
        //                        case "Homebrew Reference Book"
        content = content.clone();
        removeEditSpan(content);
        removeTableOfContents(content);
        removeImagesAndFigures(content);
        removeComments(content);
        makeLocalLinksAbsolute(content);


        for (Node node : content.childNodes()) {

            if(node instanceof Element){
                String text = ((Element)node).text();
                if (text.contains("Reference Book")) {
                    String[] toks = text.split(":");
                    return toks[1].trim();
                }
            }

        }

        return "";
    }

    private static void makeLocalLinksAbsolute(Element content) {
        Elements anchors = content.select("a");
        for (Element anchor : anchors) {
            if (!anchor.attr("href").startsWith("http")) {
                anchor.attr("href", "https://swse.fandom.com" + anchor.attr("href"));
            }
        }
    }

    private static void removeImagesAndFigures(Element content) {
        try {
            content.select("img,figure").remove();
        }catch(Exception e){
            //Ignore
        }
    }

    private static void removeTableOfContents(Element content) {
        try {
            content.select("div.toc").remove();
        }catch(Exception e){
            //Ignore
        }
    }

    private static void removeEditSpan(Element content) {
        try {
            content.select("span.mw-editsection").remove();
        }catch(Exception e){
            //Ignore
        }
    }

    public static String getDescription(String description) {
        return description;
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
        if(limit == '¡'){
            expandedLinks.add(alphaCategory + "¡");
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

        System.out.println("List.of(\"" + names.stream().map(s -> s.replaceAll("\"", "\\\\\"")).collect(Collectors.joining("\", \"")) + "\")");
    }

    ///C:\Users\lijew\AppData\Local\FoundryVTT\Data\systems\swse\icon\feat

//    protected static String getImage(String itemType, String itemName){
//        return getImage(itemType, List.of(itemName));
//    }
    protected static String getImage(String itemType, String... names){
        return getImage(itemType, List.of(names));
    }
    protected static String getImage(String itemType, List<String> itemNames) {
        if (itemType.contains(",")) {
            itemType = itemType.split(",")[0];
        }

        for (String itemName :
                itemNames) {

        String key = getFileKey(itemName);

        if (availableFiles.containsKey(key)) {
            String exists = availableFiles.remove(key);
            return IMAGE_FOLDER + "/" + itemType + "/"+exists;
        }
        }
        itemsWithoutImages.add(itemNames.get(0));
        if (new File(LOCAL_ROOT + IMAGE_FOLDER + "/" + itemType + "/default.png").exists()) {
            return IMAGE_FOLDER + "/" + itemType + "/default.png";
        } else {
            //System.out.println("could not find "+ IMAGE_FOLDER+"/" + itemType + "/default.png");
            new File(LOCAL_ROOT + IMAGE_FOLDER + "/" + itemType).mkdir();
        }

        return IMAGE_FOLDER + "/default.png";
    }

    private static String getFileKey(String itemName) {
        itemName = itemName.replace("'", "_").replace(":", "").replace("-", " ").replace("ä", "a");
        return itemName.toLowerCase();
    }

    abstract protected Collection<JSONy> parseItem(String itemLink, boolean overwrite, List<String> filter, List<String> nameFilter);

    private Collection<? extends JSONObject> readCategoryItemPage(String itemPageLink, boolean overwrite, List<String> exclusion, List<String> nameFilter) {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null) {
            return new ArrayList<>();
        }
        Element body = doc.body();

        List<String> hrefs = new LinkedList<>();

        Elements links = body.getElementsByClass("category-page__member-link");

        links.forEach(a -> hrefs.add(a.attr("href")));

        return hrefs.stream()
                .flatMap((Function<String, Stream<JSONy>>) itemLink -> parseItem(itemLink, overwrite, exclusion, nameFilter).stream())
                .map(jsoNy -> jsoNy.toJSON()).collect(Collectors.toList());
    }

    public List<JSONObject> getEntriesFromCategoryPage(List<String> talentLinks, boolean overwrite) {
        return getEntriesFromCategoryPage(talentLinks, overwrite, List.of(), List.of());
    }
    public List<JSONObject> getEntriesFromCategoryPage(List<String> talentLinks, boolean overwrite, List<String> exclusion, List<String> nameFilter) {
        List<JSONObject> entries = new ArrayList<>();
        List<String> names = new LinkedList<>();
        for (String talentLink : talentLinks) {
            //entries.addAll(readCategoryItemPage(talentLink, false));
            Collection<? extends JSONObject> newEntities = List.of();
            if(talentLink.contains("Category")) {
                newEntities = readCategoryItemPage(talentLink, overwrite, exclusion, nameFilter);
            } else {
                newEntities = parseItem(talentLink, overwrite, null, nameFilter).stream().map(jsoNy -> jsoNy.toJSON()).collect(Collectors.toList());
            }
            for (JSONObject newEntity : newEntities) {
                String name = newEntity.getString("name");
                if(exclusion.contains(name)){
                    continue;
                }
                if (names.contains(name)) {
                    //System.out.println("Duplicate: " + name + " from: " + talentLink);
                } else {
                    names.add(name);
                    entries.add(newEntity);
                }
            }
        }

        return entries;
    }

    protected Collection<?> getReroll(String content) {
        if(!content.toLowerCase().contains("reroll")){
            return List.of();
        }

        List<Object> of = Lists.newArrayList();
        String checks = List.of("Acrobatics", "Climb", "Deception", "Endurance", "Gather Information", "Initiative", "Jump",
                "Knowledge \\(Bureaucracy\\)", "Knowledge \\(Galactic Lore\\)", "Knowledge \\(Life Sciences\\)", "Knowledge \\(Physical Sciences\\)",
                "Knowledge \\(Social Sciences\\)", "Knowledge \\(Tactics\\)", "Knowledge \\(Technology\\)", "Mechanics", "Perception",
                "Persuasion", "Pilot", "Ride", "Stealth", "Survival", "Swim", "Treat Injury", "Use Computer", "Use the Force").stream().collect(Collectors.joining("|"));

        String effect = content;//.split("Effect: ")[1];

        if("You can reroll Deception checks made to Innuendo and Perception checks made to decipher an Innuendo, keeping the better result.".equals(effect)){

            of.add(Change.createReRoll("Deception (Make Innuendo)", "kh", effect));
            of.add(Change.createReRoll("Perception (Understand Innuendo)", "kh", effect));
        }

        if(effect.endsWith("You may reroll a Deception check to Cheat or a Persuasion check to Intimidate, but the result of the reroll must be accepted, even if it is worse.")){

            of.add(Change.createReRoll("Deception (Cheat)", "kh", effect));
            of.add(Change.createReRoll("Persuasion (Intimidate)", "kh", effect));
        }

        if(effect.endsWith("Once per day, you can reroll any check for a Knowledge Skill that you are Trained in, using the better result.")){
            of.add(Change.createReRoll("Knowledge", "kh", effect));
        }


        if(effect.endsWith("Whenever you roll a Natural 20 while making a Deception check (even if on a reroll), you gain a temporary Force Point. If this Force Point is not spent before the end of the encounter, it is lost.")){
            of.add(Change.create(ChangeKey.CHECK_TRIGGER, "Deception:20:Temporary Force Point:1"));
        }

        if(effect.endsWith("You can reroll any Strength- or Constitution-based Skill Checks for Skills that you are Trained in. " +
                "The result of the reroll must be accepted even if it is worse. Additionally, once per encounter you can add your " +
                "Strength modifier to your Fortitude Defense as a Reaction; this bonus lasts until the beginning of your next turn.")){

            of.add(Change.createReRoll("Strength", "", effect));
            of.add(Change.createReRoll("Constitution", "", effect));
        }
        if(effect.endsWith("Whenever you make a Persuasion check or a Use the Force check to activate a Fear effect, you may reroll the check, but the result of the reroll must be accepted, even if it is worse.")){

            of.add(Change.createReRoll("Perception (Fear Effect)", "", effect));
            of.add(Change.createReRoll("Use the Force (Fear Effect)", "", effect));
        }

        if(of.size()>0){
            return of;
        }

        //String checks = "[\\w\\s]*";
        Pattern p = Pattern.compile("Whenever you reroll an? (" + checks + ") check, you always keep the better result, even if you have multiple reroll abilities");
        Matcher m = p.matcher(effect);


        if(m.find()){
            of.add(Change.createReRoll(m.group(1), "akh", effect));
            //printUnique(of);
        }

        p = Pattern.compile("Whenever you reroll an? (" + checks +") check and");
        m = p.matcher(content);
        if(m.find()){
            of.add(Change.create(ChangeKey.TEMPORARY_FORCE_POINT, "use:"+m.group(1)));
            //printUnique(of, effect);
        }


        p = Pattern.compile("You (?:may|can)(?: choose to)? reroll(?: any)?(?: the)? (" + checks +") checks?(?: made)?(?: to)? ?([\\w\\s-]*)?, ([\\w\\s]*)");
        m = p.matcher(content);
        if(m.find()){
            String type = m.group(3).startsWith("keeping the better") ? ":kh" : "";
            String modifier = m.group(2) != null && m.group(2).length()>0 ? " ("+m.group(2)+")" : "";
            //of.add(Change.create(ChangeKey.SKILL_RE_ROLL, m.group(1) + modifier + type));
            of.add(Change.createReRoll(m.group(1) + modifier, type, effect));
            //printUnique(of, effect);
        }
//
//        p = Pattern.compile("You (?:may|can) choose to reroll any (" + checks + ") check, but the result of the reroll must be accepted, even if it is worse");
//        m = p.matcher(content);
//        if(m.find()){
//            of.add(Change.create(ChangeKey.SKILL_RE_ROLL, m.group(1)));
//            //printUnique(of);
//        }

//        p = Pattern.compile("You (?:may|can) reroll any (" + checks + ") checks made to ([\\w]*), keeping the better of the two results");
//        m = p.matcher(content);
//        if(m.find()){
//            of.add(Change.create(ChangeKey.SKILL_RE_ROLL, m.group(1) + " ("+m.group(2)+")"));
//            //printUnique(of);
//        }
//
//        p = Pattern.compile("You (?:may|can) reroll (" + checks + ") checks made to ([\\w]*), keeping the better result.");
//        m = p.matcher(content);
//        if(m.find()){
//            of.add(Change.create(ChangeKey.SKILL_RE_ROLL, m.group(1) + " ("+m.group(2)+"):kh"));
//            //printUnique(of);
//        }

//        p = Pattern.compile("You (?:may|can) choose to reroll any (" + checks + ") check, keeping the better of the two results");
//        m = p.matcher(content);
//        if(m.find()){
//            of.add(Change.create(ChangeKey.SKILL_RE_ROLL, m.group(1) +":kh"));
//            //printUnique(of);
//        }

        if(of.size() == 0 ){
            //of.add(Change.create(ChangeKey.SKILL_RE_ROLL, "any:unknown:"+effect));
            of.add(Change.createReRoll("any", "unknown", effect));
            printUnique(effect);
        }
        return of;
    }
}
