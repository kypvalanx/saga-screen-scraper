package swse.species;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
import swse.common.AttributeKey;
import swse.common.BaseExporter;
import swse.common.Category;
import swse.common.Choice;
import swse.common.ItemType;
import swse.common.JSONy;
import swse.common.Option;
import swse.common.ProvidedItem;

public class SpeciesExporter extends BaseExporter {
    public static final String IMAGE_FOLDER = "systems/swse/icon/species";
    public static final String JSON_OUTPUT = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems\\swse\\raw_export\\species.json";
    public static final String ROOT = "G:/FoundryVTT/Data";
    public static final List<String> DUMMY_CATEGORYS = List.of("Damage Reduction", "Conditional Bonus Feat", "Natural Armor", "Bonus Class Skill", "Species");
    public static final Pattern BONUS_FEAT_PATTERN = Pattern.compile("gain one bonus Feat at 1st level");
    public static final Pattern DAMAGE_REDUCTION = Pattern.compile("Damage Reduction (\\d*)");
    private static List<Object> defaultDroidUnarmedDamage;
    private static int languages = 0;


    public static void main(String[] args) {
        List<String> speciesLinks = new ArrayList<>(getAlphaLinks("/wiki/Category:Species?from="));
        speciesLinks.add("/wiki/Droid_Heroes");
        speciesLinks.add("/wiki/Droid_Chassis");

        List<JSONObject> entries = new ArrayList<>();
        for (String itemLink : speciesLinks) {
            entries.addAll(readItemMenuPage(itemLink, false));
            drawProgressBar(entries.size() * 100.0 / 345.0);
        }
        System.out.println("processed "+ entries.size() + " of 345");
        System.out.println("species with auto languages " + languages);

        printUniqueNames(entries);

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

        final SpeciesExporter speciesExporter = new SpeciesExporter();

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

        if(speciesName.equals("t'landa Til")){
            speciesName = "T'landa Til";
        }

        if ("home".equals(speciesName)) {
            return new ArrayList<>();
        }

        if (speciesName.contains("Droid Models")) {
            speciesName = speciesName.replace("Droid Models", "Droid Model");
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        String imageFile = IMAGE_FOLDER + "/default.png";

        try {
            //imageFile = getSpeciesImage(content.select("img.thumbimage").first());
        } catch (NullPointerException e) {
            //System.out.println(itemLink.getKey());
        }


        Set<Category> categories = new HashSet<>(Category.getCategories(doc));


        Species species = Species.create(speciesName)
                .withDescription(content)
                .withImage(imageFile)
                .withProvided(categories)
                .withProvided(addTraitsFromCategories(categories, speciesName))
                .withProvided(StartingFeats.getStartingFeatsFromCategories(categories))
                .withProvided(StatBonuses.getStatBonuses(content, speciesName))
                .withProvided(getDroidUnarmedDamage(speciesName))
                .withProvided(getDroidChoice(speciesName))
                .withProvided(getMechanicLocomotionChoice(speciesName))
                .withProvided(getSpeciesSpecificChoice(speciesName))
                .withProvided(Speed.getSpeed(content, speciesName))
                .withProvided(AgeCategories.getAgeCategories(content))
                //.withProvided(getSize(content)) seems to be duplicated by addTraitsFromCategories
                .withProvided(getWeaponFamiliarity(speciesName))
                .withProvided(getBonusTree(speciesName))
                .withProvided(getManualBonusItems(speciesName))
                .withProvided(getBonusItems(content));

        return Lists.newArrayList(species);
    }

    private static Collection<Object> getDroidUnarmedDamage(String speciesName) {
        if (!speciesName.toLowerCase().contains("droid")) {
            return new ArrayList<>();
        }

        if(defaultDroidUnarmedDamage != null){
            return defaultDroidUnarmedDamage;
        }
        defaultDroidUnarmedDamage = new ArrayList<>();

        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1)", ItemType.TRAIT, "EQUIPPED:Claw", "TRAIT:Diminutive"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1)", ItemType.TRAIT, "EQUIPPED:Tool", "TRAIT:Tiny"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1)", ItemType.TRAIT, "EQUIPPED:Hand", "TRAIT:Tiny"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1)", ItemType.TRAIT, "EQUIPPED:Instrument", "TRAIT:Small"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1)", ItemType.TRAIT, "EQUIPPED:Probe", "TRAIT:Medium"));

        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d2)", ItemType.TRAIT, "EQUIPPED:Claw", "TRAIT:Tiny"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d2)", ItemType.TRAIT, "EQUIPPED:Tool", "TRAIT:Small"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d2)", ItemType.TRAIT, "EQUIPPED:Hand", "TRAIT:Small"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d2)", ItemType.TRAIT, "EQUIPPED:Instrument", "TRAIT:Medium"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d2)", ItemType.TRAIT, "EQUIPPED:Probe", "TRAIT:Large"));

        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d3)", ItemType.TRAIT, "EQUIPPED:Claw", "TRAIT:Small"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d3)", ItemType.TRAIT, "EQUIPPED:Tool", "TRAIT:Medium"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d3)", ItemType.TRAIT, "EQUIPPED:Hand", "TRAIT:Medium"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d3)", ItemType.TRAIT, "EQUIPPED:Instrument", "TRAIT:Large"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d3)", ItemType.TRAIT, "EQUIPPED:Probe", "TRAIT:Huge"));

        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d4)", ItemType.TRAIT, "EQUIPPED:Claw", "TRAIT:Medium"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d4)", ItemType.TRAIT, "EQUIPPED:Tool", "TRAIT:Large"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d4)", ItemType.TRAIT, "EQUIPPED:Hand", "TRAIT:Large"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d4)", ItemType.TRAIT, "EQUIPPED:Instrument", "TRAIT:Huge"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d4)", ItemType.TRAIT, "EQUIPPED:Probe", "TRAIT:Gargantuan"));

        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d6)", ItemType.TRAIT, "EQUIPPED:Claw", "TRAIT:Large"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d6)", ItemType.TRAIT, "EQUIPPED:Tool", "TRAIT:Huge"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d6)", ItemType.TRAIT, "EQUIPPED:Hand", "TRAIT:Huge"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d6)", ItemType.TRAIT, "EQUIPPED:Instrument", "TRAIT:Gargantuan"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d6)", ItemType.TRAIT, "EQUIPPED:Probe", "TRAIT:Colossal"));

        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d8)", ItemType.TRAIT, "EQUIPPED:Claw", "TRAIT:Huge"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d8)", ItemType.TRAIT, "EQUIPPED:Tool", "TRAIT:Gargantuan"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d8)", ItemType.TRAIT, "EQUIPPED:Hand", "TRAIT:Gargantuan"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (1d8)", ItemType.TRAIT, "EQUIPPED:Instrument", "TRAIT:Colossal"));

        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (2d6)", ItemType.TRAIT, "EQUIPPED:Claw", "TRAIT:Gargantuan"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (2d6)", ItemType.TRAIT, "EQUIPPED:Tool", "TRAIT:Colossal"));
        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (2d6)", ItemType.TRAIT, "EQUIPPED:Hand", "TRAIT:Colossal"));

        defaultDroidUnarmedDamage.add(ProvidedItem.create("Droid Unarmed Damage (2d8)", ItemType.TRAIT, "EQUIPPED:Claw", "TRAIT:Colossal"));

        return defaultDroidUnarmedDamage;
    }

    private static Collection<?> addTraitsFromCategories(Set<Category> categories, String speciesName) {
        List<Object> provided = new ArrayList<>();
        for (Category category : categories) {

            if(DUMMY_CATEGORYS.contains(category.getValue())){
                continue;
            }

            if ("Extra Arms".equals(category.getValue())) {
                provided.addAll(getExtraArms(speciesName));
            } else if(!"Weapon Familiarity".equals(category.getValue()) && !"Bonus Feat".equals(category.getValue())){
                provided.add(ProvidedItem.create(category.getValue(), ItemType.TRAIT));
                //printUnique(category.getValue());
            }

        }
        return provided;
    }

    private static List<Object> getExtraArms(String speciesName) {
        List<Object> extraArms = new ArrayList<>();
        if ("Besalisk".equals(speciesName)) {
            extraArms.add(ProvidedItem.create("Extra Arms 2", ItemType.TRAIT, "GENDER:Male"));
            extraArms.add(ProvidedItem.create("Extra Arms 4", ItemType.TRAIT, "GENDER:Female"));

//            extraArms.add(ProvidedItem.create("Extra Arms 4", ItemType.TRAIT, "GENDER:Female", "TRAIT:4 Arm Option"));
//            extraArms.add(ProvidedItem.create("Extra Arms 6", ItemType.TRAIT, "GENDER:Female", "TRAIT:6 Arm Option"));
//            extraArms.add(new Choice("Female Besalisks most commonly have 6 arms but can have as many as 8"));
        } else if ("Ebranite".equals(speciesName) || "Harch".equals(speciesName)) {
            extraArms.add(ProvidedItem.create("Extra Arms 4", ItemType.TRAIT));
        } else {
            extraArms.add(ProvidedItem.create("Extra Arms 2", ItemType.TRAIT));
            //printUnique(speciesName);

        }
        return extraArms;
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
        if (bonusFeat.find()) {
            provided.add(ProvidedItem.create("Bonus Feat", ItemType.TRAIT));
        }
        if(false && child.text().startsWith("Automatic Language")){
            languages++;

            Pattern p = Pattern.compile("can (?:both )?speak, read,? and write (?:both )?([\\w\\s-',]*)");

            Matcher m = p.matcher(child.text());
            if(m.find()){
                if(child.text().contains(". ")){
                     String complex = child.text().split("\\. ")[1];
                    if("After the fall of the Empire, Dressellese are also fluent in Bothese.".equals(complex)){
                        provided.add(new Choice("After the fall of the Empire, Dressellese are also fluent in Bothese.")
                                .withOption("Before the fall of the Empire", new Option())
                                .withOption("After the fall of the Empire", new Option()
                                        .withAttribute(Attribute.create(AttributeKey.SPEAKS, "Bothese"))
                                        .withAttribute(Attribute.create(AttributeKey.READS, "Bothese"))
                                        .withAttribute(Attribute.create(AttributeKey.WRITES, "Bothese")))
                        );
                    }

                    //printUnique(complex); //child.text());
                }
                    Set<String> langs = Arrays.stream(m.group(1).split(" and ")).filter(lang -> null != lang && !lang.isBlank()).map(String::trim).collect(Collectors.toSet());
                    for(String s : langs){
                        if("Basic, Chev,".equals(s)){
                            provided.add(Attribute.create(AttributeKey.SPEAKS, "Basic"));
                            provided.add(Attribute.create(AttributeKey.READS, "Basic"));
                            provided.add(Attribute.create(AttributeKey.WRITES, "Basic"));

                            provided.add(Attribute.create(AttributeKey.SPEAKS, "Chev"));
                            provided.add(Attribute.create(AttributeKey.READS, "Chev"));
                            provided.add(Attribute.create(AttributeKey.WRITES, "Chev"));
                        } else if("Basic, Huttese,".equals(s)){
                            provided.add(Attribute.create(AttributeKey.SPEAKS, "Basic"));
                            provided.add(Attribute.create(AttributeKey.READS, "Basic"));
                            provided.add(Attribute.create(AttributeKey.WRITES, "Basic"));

                            provided.add(Attribute.create(AttributeKey.SPEAKS, "Huttese"));
                            provided.add(Attribute.create(AttributeKey.READS, "Huttese"));
                            provided.add(Attribute.create(AttributeKey.WRITES, "Huttese"));
                        } else if("Nikto, as well as either Basic or Huttese".equals(s)){
                            provided.add(Attribute.create(AttributeKey.SPEAKS, "Nikto"));
                            provided.add(Attribute.create(AttributeKey.READS, "Nikto"));
                            provided.add(Attribute.create(AttributeKey.WRITES, "Nikto"));

                            provided.add(new Choice("Select an available language")
                                    .withOption("Basic", new Option()
                                            .withAttribute(Attribute.create(AttributeKey.SPEAKS, "Basic"))
                                            .withAttribute(Attribute.create(AttributeKey.READS, "Basic"))
                                            .withAttribute(Attribute.create(AttributeKey.WRITES, "Basic")))
                                    .withOption("Huttese", new Option()
                                            .withAttribute(Attribute.create(AttributeKey.SPEAKS, "Huttese"))
                                            .withAttribute(Attribute.create(AttributeKey.READS, "Huttese"))
                                            .withAttribute(Attribute.create(AttributeKey.WRITES, "Huttese")))
                            );
                        } else {
                            provided.add(Attribute.create(AttributeKey.SPEAKS, s));
                            provided.add(Attribute.create(AttributeKey.READS, s));
                            provided.add(Attribute.create(AttributeKey.WRITES, s));
                        }
                        //printUnique(s);
                        //do i want 3 systems?
                    }

            } else{
                //printUnique(child.text());
            }

            //printUnique(child.text());
        }
//        Matcher damageReduction = DAMAGE_REDUCTION.matcher(child.text());
//        if (damageReduction.find()) {
//            provided.add(ProvidedItem.create("Damage Reduction " + damageReduction.group(1), ItemType.TRAIT));
//        }
        return provided;
    }

    private static Collection<Object> getSize(Element content) {
        Set<Object> size = new HashSet<>();

        Elements lis = content.select("li");

        for (Element li : lis) {
            String text = li.text();
            final String[] toks = text.split(":");
            if (toks.length > 1) {
                String speciesCategory = toks[0].trim();
                if (speciesCategory.endsWith("Size")) {
                    String category = speciesCategory.split(" ")[0];
                    size.add(ProvidedItem.create(category, ItemType.TRAIT));
                }
            }
        }

        return size;
    }

    private static List<Object> getBonusTree(String speciesName) {
        List<Object> attributes = new ArrayList<>();
        switch (speciesName) {
            case "Medical Droid":
            case "1st-Degree Droid Model":
                attributes.add(Attribute.create(AttributeKey.BONUS_TALENT_TREE, "1st-Degree Droid Talent Tree"));
                break;
            case "Astromech Droid":
            case "Mechanic Droid":
            case "2nd-Degree Droid Model":
                attributes.add(Attribute.create(AttributeKey.BONUS_TALENT_TREE, "2nd-Degree Droid Talent Tree"));
                break;
            case "Protocol Droid":
            case "Service Droid":
            case "3rd-Degree Droid Model":
                attributes.add(Attribute.create(AttributeKey.BONUS_TALENT_TREE, "3rd-Degree Droid Talent Tree"));
                break;
            case "Battle Droid":
            case "Probe Droid":
            case "4th-Degree Droid Model":
                attributes.add(Attribute.create(AttributeKey.BONUS_TALENT_TREE, "4th-Degree Droid Talent Tree"));
                break;
            case "Labor Droid":
            case "5th-Degree Droid Model":
                attributes.add(Attribute.create(AttributeKey.BONUS_TALENT_TREE, "5th-Degree Droid Talent Tree"));
                break;

        }

        if (speciesName.contains(" Droid")) {
            attributes.add(Attribute.create(AttributeKey.IS_DROID, "true"));
            attributes.add(ProvidedItem.create("Droid Default Appendage Offset", ItemType.TRAIT));
        }

        return attributes;
    }

    private static Set<ProvidedItem> getWeaponFamiliarity(String speciesName) {
        switch (speciesName) {
            case "Chazrach":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Amphistaff(Quarterstaff,Spear):Simple Melee Weapons)", ItemType.TRAIT));
            case "Felucian":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Felucian Skullblade:Simple Melee Weapons)", ItemType.TRAIT));
            case "Gamorrean":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Arg'garok:Advanced Melee Weapons)", ItemType.TRAIT));
            case "Gand":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Gand Weapon Template:Analogous Simple Weapons)", ItemType.TRAIT));
            case "Gungan":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Atlatl:Simple Melee Weapons)", ItemType.TRAIT), ProvidedItem.create("Weapon Familiarity (Cesta:Simple Melee Weapon)", ItemType.TRAIT), ProvidedItem.create("Weapon Familiarity (Electropole:Simple Melee Weapon)", ItemType.TRAIT));
            case "Kerestian":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Darkstick:Simple Melee Weapons)", ItemType.TRAIT));
            case "Kissai":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Massassi Lanvarok:Simple Ranged Weapons)", ItemType.TRAIT));
            case "Kyuzo":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Kyuzo Battle Helmet:Simple Melee Weapons)", ItemType.TRAIT));
            case "Lasat":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Bo-Rifle:Rifles)", ItemType.TRAIT));
            case "Massassi":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Massassi Lanvarok:Simple Ranged Weapons)", ItemType.TRAIT));
            case "Nagai":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Tehk'la Blade:Simple Melee Weapons)", ItemType.TRAIT));
            case "Rakata":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Rakatan Weapon Template:Analogous Simple Weapons)", ItemType.TRAIT));
            case "Squib":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Squib Tensor Rifle:Rifless)", ItemType.TRAIT));
            case "Verpine":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Verpine Shattergun:Pistols)", ItemType.TRAIT));
            case "Wookiee":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Bowcaster:Rifles)", ItemType.TRAIT), ProvidedItem.create("Weapon Familiarity (Ryyk Blade:Advanced Melee Weapons)", ItemType.TRAIT));
            case "Yuuzhan Vong":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Amphistaff:Simple Melee Weapons)", ItemType.TRAIT));
        }

        return null;
    }

    private static Collection<Object> getSpeciesSpecificChoice(String speciesName) {
        Collection<Object> choices = new ArrayList<>();
        if ("Arkanian Offshoot".equals(speciesName)) {
            Choice choice = new Choice("Select a Bonus Feat:")
                    .withShowSelectionInName(false);
            choice.withOption("Skill Focus (Endurance)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Endurance))", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Endurance)", ItemType.FEAT)));
            choice.withOption("Skill Focus (Mechanics)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Mechanics))", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Mechanics)", ItemType.FEAT)));
            choice.withOption("Skill Focus (Pilot)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Pilot))", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Pilot)", ItemType.FEAT)));
            choice.withOption("Skill Focus (Survival)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Survival))", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Survival)", ItemType.FEAT)));
            choices.add(choice);
        } else if ("Aqualish".equals(speciesName)) {
            Choice choice = new Choice("Select a Subspecies:");
            choice.withOption("None", new Option());
            choice.withOption("Aquala", new Option().withProvidedItem(ProvidedItem.create("Aquala", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Swim Speed (2)", ItemType.TRAIT)));
            choice.withOption("Kyuzo", new Option().withProvidedItem(ProvidedItem.create("Kyuzo", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Heightened Agility", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Bonus Trained Skill (Acrobatics)", ItemType.TRAIT)));
            choice.withOption("Quara", new Option().withProvidedItem(ProvidedItem.create("Quara", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Intimidating", ItemType.TRAIT)));
            choice.withOption("Ualaq", new Option().withProvidedItem(ProvidedItem.create("Ualaq", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Darkvision", ItemType.TRAIT)));
            choices.add(choice);
        } else if ("Killik".equals(speciesName)) {
            Choice choice = new Choice("Select a Size:")
                    .withShowSelectionInName(false);
            choice.withOption("Tiny", new Option().withProvidedItem(ProvidedItem.create("Tiny", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (+4)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Strength (-4)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d3)", ItemType.ITEM)));
            choice.withOption("Small", new Option().withProvidedItem(ProvidedItem.create("Small", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (+2)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Strength (-2)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d4)", ItemType.ITEM)));
            choice.withOption("Medium", new Option().withProvidedItem(ProvidedItem.create("Medium", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d6)", ItemType.ITEM)));
            choice.withOption("Medium", new Option().withProvidedItem(ProvidedItem.create("Medium", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d6)", ItemType.ITEM)));
            choice.withOption("Large", new Option().withProvidedItem(ProvidedItem.create("Large", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Strength (+8)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Constitution (+8)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (-2)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d8)", ItemType.ITEM)));
            choice.withOption("Huge", new Option().withProvidedItem(ProvidedItem.create("Huge", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (4)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Strength (+16)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Constitution (+16)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (2d6)", ItemType.ITEM)));
            choice.withOption("Gargantuan", new Option().withProvidedItem(ProvidedItem.create("Gargantuan", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (4)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Strength (+24)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Constitution (+24)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (3d6)", ItemType.ITEM)));
            Option option = new Option().withProvidedItem(ProvidedItem.create("Colossal", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (4)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Strength (+32)", ItemType.TRAIT));
            option.withProvidedItem(ProvidedItem.create("Constitution (+32)", ItemType.TRAIT));
            choice.withOption("Colossal", option.withProvidedItem(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT)).withProvidedItem(ProvidedItem.create("Claw (4d6)", ItemType.ITEM)));
            choices.add(choice);
        }

        return choices;
    }


    private static Collection<Object> getMechanicLocomotionChoice(String speciesName) {
        Collection<Object> choices = new ArrayList<>();
        if (!"Mechanic Droid".equals(speciesName)) {
            return choices;
        }

        Choice locomotionChoice = new Choice("Select locomotion type:")
                .withShowSelectionInName(false);

        locomotionChoice.withOption("Walking", new Option().withProvidedItem(ProvidedItem.create("Walking", ItemType.ITEM)));
        locomotionChoice.withOption("Tracked", new Option().withProvidedItem(ProvidedItem.create("Tracked", ItemType.ITEM)));
        locomotionChoice.withOption("Wheeled", new Option().withProvidedItem(ProvidedItem.create("Wheeled", ItemType.ITEM)));

        choices.add(locomotionChoice);

        return choices;
    }

    private static List<Object> getManualBonusItems(String speciesName) {
        List<Object> attributes = new ArrayList<>();
        for (String item : getBonusItemList(speciesName)) {
            attributes.add(ProvidedItem.create(item, ItemType.ITEM).withEquip("equipped"));
        }
        return attributes;
    }

    private static ArrayList<String> getBonusItemList(String speciesName) {
        if ("Replica Droid".equals(speciesName)) {
            return Lists.newArrayList("Internal Comlink", "Darkvision", "Diagnosis Package", "Improved Sensor Package", "Internal Storage (Subject to size limitations)", "Translator Unit (DC 15)");
        } else if ("Labor Droid".equals(speciesName)) {
            return Lists.newArrayList("Walking", "Basic Processor", "Claw", "Claw", "Durasteel Shell", "Vocabulator");
        } else if ("Service Droid".equals(speciesName)) {
            return Lists.newArrayList("Walking", "Basic Processor", "Hand", "Hand", "Tool", "Vocabulator");
        } else if ("Astromech Droid".equals(speciesName)) {
            return Lists.newArrayList("Wheeled", "Walking", "Magnetic Feet", "Heuristic Processor", "Tool", "Tool", "Tool", "Tool", "Tool", "Tool", "Claw", "Astrogation Buffer (5 Memory Units)", "Diagnosis Package", "Internal Storage (2 kg)");
        } else if ("Battle Droid".equals(speciesName)) {
            return Lists.newArrayList("Walking", "Basic Processor", "Hand", "Hand", "Plasteel Shell", "Internal Comlink", "Locked Access", "Vocabulator");
        } else if ("Probe Droid".equals(speciesName)) {
            return Lists.newArrayList("Hovering", "Heuristic Processor", "Hand", "Tool", "Improved Sensor Package", "Darkvision", "Internal Comlink", "Locked Access");
        } else if ("Protocol Droid".equals(speciesName)) {
            return Lists.newArrayList("Walking", "Basic Processor", "Hand", "Hand", "Translator Unit (DC 10)", "Vocabulator");
        } else if ("Medical Droid".equals(speciesName)) {
            return Lists.newArrayList("Walking", "Heuristic Processor", "Hand", "Hand", "Tool", "Improved Sensor Package", "Vocabulator");
        } else if ("Mechanic Droid".equals(speciesName)) {
            return Lists.newArrayList("Basic Processor", "Hand", "Hand", "Tool", "Tool", "Tool", "Tool", "Diagnosis Package", "Internal Storage (2 kg)", "Vocabulator");
        }
        return Lists.newArrayList();
    }

    private static Collection<Choice> getDroidChoice(String speciesName) {
        Collection<Choice> choices = new ArrayList<>();
        if (!speciesName.toLowerCase().contains("droid")) {
            return choices;
        }

        Choice droidChoice = new Choice("Select the size of your droid's chassis:")
                .withShowSelectionInName(false);

        droidChoice.withOption(new Option("Fine (GM Only)").withProvidedItem(ProvidedItem.create("Fine", ItemType.TRAIT)));
        droidChoice.withOption(new Option("Diminutive (GM Only)").withProvidedItem(ProvidedItem.create("Diminutive", ItemType.TRAIT)));
        droidChoice.withOption(new Option("Tiny (GM Only)").withProvidedItem(ProvidedItem.create("Tiny", ItemType.TRAIT)));
        droidChoice.withOption(new Option("Small").withProvidedItem(ProvidedItem.create("Small", ItemType.TRAIT)));
        droidChoice.withOption(new Option("Medium").withProvidedItem(ProvidedItem.create("Medium", ItemType.TRAIT)));
        droidChoice.withOption(new Option("Large (GM Only)").withProvidedItem(ProvidedItem.create("Large", ItemType.TRAIT)));
        droidChoice.withOption(new Option("Huge (GM Only)").withProvidedItem(ProvidedItem.create("Huge", ItemType.TRAIT)));
        droidChoice.withOption(new Option("Gargantuan (GM Only)").withProvidedItem(ProvidedItem.create("Gargantuan", ItemType.TRAIT)));
        droidChoice.withOption(new Option("Colossal (GM Only)").withProvidedItem(ProvidedItem.create("Colossal", ItemType.TRAIT)));
        choices.add(droidChoice);
        return choices;
    }

    private static String getSpeciesImage(Element img) {
        String src = img.attr("src");
        String alt = img.attr("alt");

        if (alt == null || alt.isBlank()) {
            return null;
        }
        String filename = IMAGE_FOLDER + "/" + alt;
        try {
            URL url = new URL(src);
            BufferedImage bufferedImage = ImageIO.read(url);
            File file = new File(ROOT + "/" + filename);
            ImageIO.write(bufferedImage, "png", file);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return filename;
    }
}
