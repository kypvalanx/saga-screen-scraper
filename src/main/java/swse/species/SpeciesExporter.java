package swse.species;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.character_class.StartingFeats;
import swse.common.Attribute;
import swse.common.BaseExporter;
import swse.common.Category;
import swse.common.Choice;
import swse.common.ItemType;
import swse.common.Option;
import swse.common.ProvidedItem;

public class SpeciesExporter extends BaseExporter
{
    public static final String IMAGE_FOLDER = "systems/swse/icon/species";
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\species.json";
    public static final String ROOT = "G:/FoundryVTT/Data";

    public static Set<String> allCategories = new HashSet();
    public static Set<String> keys = new HashSet();
    private static List<String> allSpecies = new ArrayList<>();
    public static final Pattern BONUS_FEAT_PATTERN = Pattern.compile("gain one bonus Feat at 1st level");
    public static final Pattern DAMAGE_REDUCTION = Pattern.compile("Damage Reduction (\\d*)");


    public static void main(String[] args)
    {
        List<String> speciesLinks = new ArrayList<>();
        speciesLinks.add("/wiki/Species");
        speciesLinks.add("/wiki/Droid_Heroes");
        speciesLinks.add("/wiki/Droid_Chassis");

        List<JSONObject> entries = new ArrayList<>();
        double size = speciesLinks.size();
        AtomicInteger i = new AtomicInteger();
        for (String itemLink : speciesLinks)
        {
            entries.addAll(readItemMenuPage(itemLink, false));
            drawProgressBar(i.getAndIncrement() *100 /size);
        }
        //System.out.println(allSpecies.stream().map(species -> "\""+species+"\"").collect(Collectors.toList()));

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"));
    }


    private static List<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite)
    {
        Document doc = getDoc(itemPageLink, overwrite);

        Element body = doc.body();

        Elements tables = body.getElementsByClass("wikitable");

        Map<String, String> hrefs = new HashMap<>();
        tables.forEach(table -> {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row -> {
                Elements tds = row.getElementsByTag("td");
                Element first = tds.first();
                if(first != null){
                    Element second = tds.get(1);
                    Element anchor = first.getElementsByTag("a").first();
                    if(anchor != null)
                    {
                        String href = anchor.attr("href");
                        hrefs.put(href, second.text());
                    }
                }
            });
        });

        return hrefs.entrySet().stream().flatMap((Function<Map.Entry<String, String>, Stream<JSONObject>>) itemLink -> parseItem(itemLink, overwrite).stream()).collect(Collectors.toList());
    }

    private static List<JSONObject> parseItem(Map.Entry<String, String> itemLink, boolean overwrite)
    {
        if(null == itemLink){
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink.getKey(), overwrite);


        Elements headingElements = doc.getElementsByClass("page-header__title");

        if(headingElements.size()>1){
            throw new IllegalStateException("too many headers " + headingElements);
        }

        String speciesName = headingElements.first().text();

        if("home".equals(speciesName)){
            return new ArrayList<>();
        }

        if(speciesName.contains("Droid Models")){
            speciesName = speciesName.replace("Droid Models", "Droid Model");
        }

        allSpecies.add(speciesName);

        Element content = doc.getElementsByClass("mw-parser-output").first();

        String imageFile = IMAGE_FOLDER + "/default.png";

        try
        {
            //imageFile = getSpeciesImage(content.select("img.thumbimage").first());
        } catch (NullPointerException e){
            //System.out.println(itemLink.getKey());
        }


        Set<Category> categories = new HashSet<>(Category.getCategories(doc));


        Species species = Species.create(speciesName)
                .withDescription(getDescription(content))
                .withImage(imageFile)
                .withProvided(categories)
                .withProvided(StartingFeats.getStartingFeatsFromCategories(categories))
                .withProvided(StatBonuses.getStatBonuses(content, speciesName))
                .withProvided(getDroidChoice(speciesName))
                .withProvided(getMechanicLocomotionChoice(speciesName))
                .withProvided(getSpeciesSpecificChoice(speciesName))
                .withProvided(Speed.getSpeed(content, speciesName))
                .withProvided(AgeCategories.getAgeCategories(content))
                .withProvided(getSize(content))
                .withProvided(getWeaponFamiliarity (speciesName))
                .withProvided(getBonusTree(speciesName))
                .withProvided(getManualBonusItems(speciesName))
                .withProvided(getBonusItems(content));

        return Lists.newArrayList(species.toJSON());
    }

    private static Collection<?> getBonusItems(Element content) {
        List<Object> provided = new ArrayList<>();

        for (Element child : content.select("p,li")) {
//            if(child.children().isEmpty()) {
            provided.addAll(getTrait(child));
//            } else {
//                provided.addAll(getBonusItems(child));
//            }
        }
        return provided;
    }

    private static Collection<?> getTrait(Element child) {

        List<Object> provided = new ArrayList<>();
        Matcher bonusFeat = BONUS_FEAT_PATTERN.matcher(child.text());
        if(bonusFeat.find()){
            provided.add(ProvidedItem.create("Bonus Feat", ItemType.TRAIT));
        }
        Matcher damageReduction = DAMAGE_REDUCTION.matcher(child.text());
        if(damageReduction.find()){
            provided.add(ProvidedItem.create("Damage Reduction " + damageReduction.group(1), ItemType.TRAIT));
        }
        return provided;
    }

    private static Collection<Object> getSize(Element content) {
        Set<Object> size = new HashSet<>();

        Elements lis = content.select("li");

        for(Element li : lis){
            String text = li.text();
            final String[] toks = text.split(":");
            if(toks.length > 1) {
                String speciesCategory = toks[0].trim();
                if (speciesCategory.endsWith("Size")) {
                    String category = speciesCategory.split(" ")[0];
                    size.add(ProvidedItem.create(category, ItemType.TRAIT));
                }
            }
        }

        return size;
    }

    private static List<Attribute> getBonusTree(String speciesName) {
        List<Attribute> attributes = new ArrayList<>();
        switch (speciesName){
            case "Medical Droid":
            case "1st-Degree Droid Model":
                attributes.add(Attribute.create("bonusTalentTree", "1st-Degree Droid Talent Tree"));
                break;
            case "Astromech Droid":
            case "Mechanic Droid":
            case "2nd-Degree Droid Model":
                attributes.add(Attribute.create("bonusTalentTree", "2nd-Degree Droid Talent Tree"));
                break;
            case "Protocol Droid":
            case "Service Droid":
            case "3rd-Degree Droid Model":
                attributes.add(Attribute.create("bonusTalentTree", "3rd-Degree Droid Talent Tree"));
                break;
            case "Battle Droid":
            case "Probe Droid":
            case "4th-Degree Droid Model":
                attributes.add(Attribute.create("bonusTalentTree", "4th-Degree Droid Talent Tree"));
                break;
            case "Labor Droid":
            case "5th-Degree Droid Model":
                attributes.add(Attribute.create("bonusTalentTree", "5th-Degree Droid Talent Tree"));
                break;

        }

        return attributes;
    }

    private static Set<ProvidedItem> getWeaponFamiliarity (String speciesName)
    {
        switch(speciesName){
            case "Chazrach":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Amphistaff(Quarterstaff,Spear):Simple Melee Weapon)", ItemType.TRAIT));
            case "Felucian":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Felucian Skullblade:Simple Melee Weapon)", ItemType.TRAIT));
            case "Gamorrean":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Arg'garok:Advanced Melee Weapon)", ItemType.TRAIT));
            case "Gand":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Gand Weapon Template:Analogous Simple Weapon)", ItemType.TRAIT));
            case "Gungan":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Atlatl:Simple Melee Weapon)", ItemType.TRAIT), ProvidedItem.create("Weapon Familiarity (Cesta:Simple Melee Weapon)", ItemType.TRAIT), ProvidedItem.create("Weapon Familiarity (Electropole:Simple Melee Weapon)", ItemType.TRAIT));
            case "Kerestian":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Darkstick:Simple Melee Weapon)", ItemType.TRAIT));
            case "Kissai":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Massassi Lanvarok:Simple Ranged Weapon)", ItemType.TRAIT));
            case "Kyuzo":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Kyuzo Battle Helmet:Simple Melee Weapon)", ItemType.TRAIT));
            case "Lasat":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Bo-Rifle:Rifle)", ItemType.TRAIT));
            case "Massassi":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Massassi Lanvarok:Simple Ranged Weapon)", ItemType.TRAIT));
            case "Nagai":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Tehk'la Blade:Simple Melee Weapon)", ItemType.TRAIT));
            case "Rakata":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Rakatan Weapon Template:Analogous Simple Weapon)", ItemType.TRAIT));
            case "Squib":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Squib Tensor Rifle:Rifle)", ItemType.TRAIT));
            case "Verpine":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Verpine Shattergun:Pistol)", ItemType.TRAIT));
            case "Wookiee":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Bowcaster:Rifle)", ItemType.TRAIT), ProvidedItem.create("Weapon Familiarity (Ryyk Blade:Advanced Melee Weapon)", ItemType.TRAIT));
            case "Yuuzhan Vong":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Amphistaff:Simple Melee Weapon)", ItemType.TRAIT));
        }

        return null;
    }

    private static Collection<Object> getSpeciesSpecificChoice(String speciesName)
    {
        Collection<Object> choices = new ArrayList<>();
        if("Arkanian Offshoot".equals(speciesName)){
            Choice choice = new Choice("Select a Bonus Feat:");
            choice.withOption("Skill Focus (Endurance)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Endurance))", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Endurance)", ItemType.FEAT)));
            choice.withOption("Skill Focus (Mechanics)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Mechanics))", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Mechanics)", ItemType.FEAT)));
            choice.withOption("Skill Focus (Pilot)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Pilot))", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Pilot)", ItemType.FEAT)));
            choice.withOption("Skill Focus (Survival)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Survival))", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Survival)", ItemType.FEAT)));
            choices.add(choice);
        } else if("Aqualish". equals(speciesName)){
            Choice choice = new Choice("Select a Subspecies:");
            choice.withOption("None", new Option());
            choice.withOption("Aquala", new Option().withProvidedItem(ProvidedItem.create("Aquala", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Swim Speed 2", ItemType.TRAIT)));
            choice.withOption("Kyuzo", new Option().withProvidedItem(ProvidedItem.create("Kyuzo", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Heightened Agility", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Bonus Trained Skill (Acrobatics)", ItemType.TRAIT)));
            choice.withOption("Quara", new Option().withProvidedItem(ProvidedItem.create("Quara", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Intimidating", ItemType.TRAIT)));
            choice.withOption("Ualaq", new Option().withProvidedItem(ProvidedItem.create("Ualaq", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Darkvision", ItemType.TRAIT)));
            choices.add(choice);
        } else if("Killik". equals(speciesName)){
            Choice choice = new Choice("Select a Size:");
            choice.withOption("Tiny", new Option().withProvidedItem(ProvidedItem.create("Tiny", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed 6", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+4 Dexterity", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("-4 Strength", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d3)", ItemType.ITEM)));
            choice.withOption("Small", new Option().withProvidedItem(ProvidedItem.create("Small", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed 6", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+2 Dexterity", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("-2 Strength", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d4)", ItemType.ITEM)));
            choice.withOption("Medium", new Option().withProvidedItem(ProvidedItem.create("Medium", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed 6", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d6)", ItemType.ITEM)));
            choice.withOption("Medium", new Option().withProvidedItem(ProvidedItem.create("Medium", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed 6", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d6)", ItemType.ITEM)));
            choice.withOption("Large", new Option().withProvidedItem(ProvidedItem.create("Large", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed 6", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+8 Strength", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+8 Constitution", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("-2 Dexterity", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d8)", ItemType.ITEM)));
            choice.withOption("Huge", new Option().withProvidedItem(ProvidedItem.create("Huge", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed 4", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+16 Strength", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+16 Constitution", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("-4 Dexterity", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (2d6)", ItemType.ITEM)));
            choice.withOption("Gargantuan", new Option().withProvidedItem(ProvidedItem.create("Gargantuan", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed 4", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+24 Strength", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+24 Constitution", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("-4 Dexterity", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (3d6)", ItemType.ITEM)));
            Option option = new Option().withProvidedItem(ProvidedItem.create("Colossal", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed 4", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("+32 Strength", ItemType.TRAIT));
            option.withProvidedItem(ProvidedItem.create("+32 Constitution", ItemType.TRAIT));
            choice.withOption("Colossal", option.withProvidedItem(ProvidedItem.create("-4 Dexterity", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (4d6)", ItemType.ITEM)));
            choices.add(choice);
        }

        return choices;
    }


    private static Collection<Object> getMechanicLocomotionChoice(String speciesName)
    {
        Collection<Object> choices = new ArrayList<>();
        if(!"Mechanic Droid".equals(speciesName)){
            return choices;
        }

        Choice locomotionChoice = new Choice("Select locomotion type:");

        locomotionChoice.withOption("Walking", new Option().withProvidedItem(ProvidedItem.create("Walking", ItemType.ITEM)));
        locomotionChoice.withOption("Tracked", new Option().withProvidedItem(ProvidedItem.create("Tracked", ItemType.ITEM)));
        locomotionChoice.withOption("Wheeled", new Option().withProvidedItem(ProvidedItem.create("Wheeled", ItemType.ITEM)));

        choices.add(locomotionChoice);

        return choices;
    }

    private static List<Object> getManualBonusItems(String speciesName)
    {
        List<Object> attributes = new ArrayList<>();
        for(String item: getBonusItemList(speciesName)){
            attributes.add(ProvidedItem.create(item, ItemType.ITEM));
        }
        return attributes;
    }

    private static ArrayList<String> getBonusItemList(String speciesName) {
        if("Replica Droid".equals(speciesName)){
            return Lists.newArrayList("Internal Comlink", "Darkvision", "Diagnosis Package", "Improved Sensor Package", "Internal Storage (Subject to size limitations)", "Translator Unit (DC 15)");
        } else if("Labor Droid".equals(speciesName)){
            return Lists.newArrayList("Walking", "Basic Processor", "Claw", "Claw", "Durasteel Shell", "Vocabulator");
        } else if("Service Droid".equals(speciesName)){
            return Lists.newArrayList("Walking", "Basic Processor", "Hand", "Hand", "Tool", "Vocabulator");
        } else if("Astromech Droid".equals(speciesName)){
            return Lists.newArrayList("Wheeled" , "Walking", "Magnetic Feet", "Heuristic Processor", "Tool", "Tool", "Tool", "Tool", "Tool", "Tool", "Claw", "Astrogation Buffer (5 Memory Units)", "Diagnosis Package", "Internal Storage (2 kg)");
        } else if("Battle Droid".equals(speciesName)){
            return Lists.newArrayList("Walking", "Basic Processor", "Hand", "Hand", "Plasteel Shell", "Internal Comlink", "Locked Access", "Vocabulator");
        } else if("Probe Droid".equals(speciesName)){
            return Lists.newArrayList("Hovering", "Heuristic Processor", "Hand", "Tool", "Improved Sensor Package", "Darkvision", "Internal Comlink", "Locked Access");
        } else if("Protocol Droid".equals(speciesName)){
            return Lists.newArrayList("Walking", "Basic Processor", "Hand", "Hand", "Translator Unit (DC 10)", "Vocabulator");
        } else if("Medical Droid".equals(speciesName)){
            return Lists.newArrayList("Walking", "Heuristic Processor", "Hand", "Hand", "Tool", "Improved Sensor Package", "Vocabulator");
        } else if("Mechanic Droid".equals(speciesName)){
            return Lists.newArrayList("Basic Processor", "Hand", "Hand", "Tool", "Tool", "Tool", "Tool", "Diagnosis Package", "Internal Storage (2 kg)", "Vocabulator");
        }
        return Lists.newArrayList();
    }

    private static Collection<Choice> getDroidChoice(String speciesName)
    {
        Collection<Choice> choices = new ArrayList<>();
        if(!speciesName.toLowerCase().contains("droid")){
            return choices;
        }

        Choice droidChoice = new Choice("Select the size of your droid's chassis:");

        Option option1 = new Option();
        option1.withProvidedItem(ProvidedItem.create("Small", ItemType.TRAIT));
        droidChoice.withOption("Small", option1);
        Option option = new Option();
        option.withProvidedItem(ProvidedItem.create("Medium", ItemType.TRAIT));
        droidChoice.withOption("Medium", option);
        choices.add(droidChoice);
        return choices;
    }

    private static String getSpeciesImage(Element img)
    {
        String src = img.attr("src");
        String alt = img.attr("alt");

        if(alt == null || alt.isBlank()){
            return null;
        }
        String filename = IMAGE_FOLDER + "/" + alt;
        try
        {
            URL url = new URL(src);
            BufferedImage bufferedImage = ImageIO.read(url);
            File file = new File(ROOT +"/" + filename);
            ImageIO.write(bufferedImage, "png", file);

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return filename;
    }



}
