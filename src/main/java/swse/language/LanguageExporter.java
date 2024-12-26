package swse.language;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.BaseExporter;
import swse.common.Change;
import swse.common.ChangeKey;
import swse.common.JSONy;

public class LanguageExporter extends BaseExporter {
    public static final String IMAGE_FOLDER = "systems/swse/icon/species";
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\languages.json";


    public static void main(String[] args) {
        List<String> languages = Arrays.asList(
                "Basic",
                "Binary",
                "Bocce",
                "Bothese",
                "Cerean",
                "Dosh",
                "Durese",
                "Ewokese",
                "Gamorrean",
                "Gunganese",
                "High Galactic",
                "Huttese",
                "Ithorese",
                "Jawa Trade Language",
                "Kel Dor",
                "Mon Calamarian",
                "Quarrenese",
                "Rodese",
                "Ryl",
                "Shyriiwook",
                "Sullustese",
                "Zabrak",
                "Socorran",
                "Sy Bisti",
                "Old Corellian",
                "Arkanian",
                "Snivvian",
                "Mando'a",
                "Altirish",
                "Anzat",
                "Anarrese",
                "Dromnyr",
                "Chadra-Fan",
                "Cheunh",
                "Ebruchese",
                "Killik",
                "Kreva",
                "Minnisiat",
                "Nikto",
                "Rakata",
                "Rammocate",
                "Shistavanen",
                "Sluissese",
                "Squibbian",
                "Ssi-Ruuvi",
                "Sy Bisti",
                "Tibranese",
                "Tof*",
                "Vagaari",
                "Verpine",
                "Military Sign");

        List<String> localLanguages = Arrays.asList(
                "Ewokese",
                "Gunganese",
                "Jawa Trade Language",
                "Altirish",
                "Anzat",
                "Anarrese",
                "Dromnyr",
                "Chadra-Fan",
                "Cheunh",
                "Ebruchese",
                "Killik",
                "Shistavanen",
                "Sluissese",
                "Squibbian",
                "Ssi-Ruuvi",
                "Tibranese",
                "Tof",
                "Vagaari",
                "Verpine");

        List<String> popularDuringPreRepublic = Arrays.asList(
                "Rakata");

        List<String> tradeLanguages = Arrays.asList(
                "Jawa Trade Language",
                "Minnisiat",
                "Rammocate",
                "Sy Bisti");

        List<String> somaticLanguages = Arrays.asList(
                "Jawa Trade Language",
                "Military Sign");

        List<String> silent = Arrays.asList(
                "Military Sign");



        List<JSONObject> entries = languages.stream().map(string -> {
            Language language = Language.create(string);

            if(localLanguages.contains(string)){
                language.with(Change.create(ChangeKey.NOTE, "local language"));
            }
            if(popularDuringPreRepublic.contains(string)){
                language.with(Change.create(ChangeKey.NOTE, "popular during pre-republic"));
            }
            if(tradeLanguages.contains(string)){
                language.with(Change.create(ChangeKey.NOTE, "trade language"));
            }
            if(somaticLanguages.contains(string)){
                language.with(Change.create(ChangeKey.NOTE, "visual"));
            }
            if(silent.contains(string)){
                language.with(Change.create(ChangeKey.NOTE, "silent"));
            }

            language.withDescription(getDescription(string));

            return language
                    .toJSON();
        }).collect(Collectors.toList());



        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"), "Languages");
    }


    public static String getDescription(String description) {
        switch (description){
            case "Old Corellian":
                return "the original language spoken by the people of Corellia before that world joined the Republic and" +
                        " adopted Basic as the standard language. Though few actually speak Old Corellian these days, " +
                        "the language is far from dead, and many Corellians still pepper their speech with Old " +
                        "Corellian phrases.";
            case "Socorran":
                return "is the language spoken by the natives of the volcanic desert world of Socorro. Like Old " +
                        "Corellian, Socorran has largely been supplanted by Basic, but many pirates, smugglers, and " +
                        "other criminals use it as a secret language known only to those who have spent time on the " +
                        "seedy world. ";
            case "Sy Bisti":
                return "is a trade language spoken on The Outer Rim and also in parts of The Unknown Regions. Though" +
                        " the language is not as widespread as Bocce, many inhabitants of worlds on the edge of The " +
                        "Unknown Regions and Wild Space use Sy Bisti as a common language between Species that have " +
                        "not learned Basic.";
            case "Military Sign":
                return "Military Sign is not a language in the traditional sense, many military and paramilitary units" +
                        " use hand signals to communicate silently with one another. Favored by Alliance Spec Force, " +
                        "military sign allows beings who understand the signals to communicate with each other in basic " +
                        "terms. You can take military sign as a language just like any other language, although to use " +
                        "it, you must have line of sight to the person or people you are attempting to communicate with." +
                        " Military sign makes no noise, but it can express only simple concepts. Examples include: halt," +
                        " attention, danger, surround the target, move to a certain location, proceed with caution, trap" +
                        " ahead, ambush ahead, sensors ahead, bypass the hazard ahead, and execute orders. ";
            case "Binary":
                return "Nearly all Droids are programmed to understand a binary computer language used by most computers" +
                        " and intelligent machines. The simple version of this is a language skill called Binary. With " +
                        "it, Droids can communicate with computers and each other. Binary allows a computer or droid to " +
                        "communicate mathematical or technical information in great detail at a very high rate " +
                        "(approximately 100 times as fast as normal speech), but it has great difficulty expressing " +
                        "nontechnical topics such as emotion, art philosophy, or The Force. For example, as a Free " +
                        "Action, a Droid can use Binary to describe the exact location and physical description of " +
                        "all objects and characters that the Droid detects in a 10-by-10-square area, but the Droid " +
                        "would be unable to express the nuance of a conversation or the emotion conveyed by body " +
                        "language.\n" +
                        "\n" +
                        "Obviously, some Droids can understand additional languages as well- most Droids in the galaxy " +
                        "are programmed with Basic, even if they can't actually articulate the language. Some living " +
                        "beings learn to intercept the Binary language of the Droids, even if they can't themselves " +
                        "speak an approximation of it. A living being who understands Binary cannot understand the " +
                        "same volume of information as another Droid or computer, so the speaking Droid must " +
                        "voluntarily slow it's speech to normal rates (that is, the same as Basic or any other " +
                        "language) so that the living being can understand it.\n" +
                        "Additional Scavenger's Secret: Speaking Binary\n" +
                        "\n" +
                        "Reference Book: Star Wars Saga Edition Scavenger's Guide to Droids\n" +
                        "\n" +
                        "You know, a lot of first-time Astromech buyers, they're always worrying about communicating " +
                        "with their new purchase. It's no big deal. You want a sophisticated conversation? Buy a " +
                        "Protocol Droid. You need a detailed report? Plug that Astromech into just about anything with " +
                        "a view screen and just read what it has to say. Computer, Datapad, you know, whatever's handy. " +
                        "I've even seen one use its holographic projector to display text. Mind you, you might not like " +
                        "what you hear! Most Astromechs aren't exactly diplomatic, and many don't know what it means to " +
                        "be polite.\n" +
                        "\n" +
                        "But hang around enough Droids, and you'll pick up on what those whistles and chirps mean. " +
                        "Of course, you'll have to carry half the conversation yourself- you know, asking questions, " +
                        "assuming subjects and so on. You'll be best off trying to phrase your questions for yes or no " +
                        "answers, but after a while, you'll get the gist of what they're saying. Even those funny " +
                        "little wobbles and shakes mean something.\n" +
                        "\n" +
                        "What will really get you fouled up is when you think you get a Droid that understands Basic " +
                        "but it really understands Huttese or some oddball tongue the last owner installed. You'll " +
                        "think the thing has blown its language circuits. Worse, you might think it's ignoring or " +
                        "flat out contradicting you, when it's really trying to communicate something vitally " +
                        "important. Even a viewscreen might not help, if it can't display something you can read. " +
                        "You know, I've even seen a hasty fighter pilot fly into the middle of combat before " +
                        "realizing his replacement Astromech didn't speak his language. Entertaining, as they say. Of " +
                        "course, he survived the battle- how else could he have sold the Droid to me?\n" +
                        "\n" +
                        "-Raalo ";
        }
        return "";
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
                .parseItem(itemLink, overwrite, null, null).stream()).map(item -> item.toJSON()).collect(Collectors.toList());
    }

    protected List<JSONy> parseItem(String itemLink, boolean overwrite, List<String> filter, List<String> nameFilter) {
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


        for (Element child : content.children()) {
            if (child.tag().equals(Tag.valueOf("ul"))) {
                for (Element subChild : child.children()) {
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

        if (child.text().contains("Language")) {
            // System.out.println(child.text());
        }
        return provided;
    }

}
