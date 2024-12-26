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
import swse.common.*;
import swse.item.Effect;

import static swse.common.ItemType.FEAT;
import static swse.common.ItemType.TRAIT;

public class SpeciesExporter extends BaseExporter {
    public static final String IMAGE_FOLDER = "systems/swse/icon/species";
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\species.json";
    public static final String ROOT = "G:/FoundryVTT/Data";
    public static final List<String> DUMMY_CATEGORIES = List.of("Damage Reduction", "Conditional Bonus Feat", "Natural Armor", "Bonus Class Skill", "Species", "Homebrew");
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
        System.out.println("processed " + entries.size() + " of 345");
        System.out.println("species with auto languages " + languages);

        printUniqueNames(entries);

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"), "Species");
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
                .parseItem(itemLink, overwrite, null, null).stream()).map(item -> item.toJSON()).collect(Collectors.toList());
    }

    protected List<JSONy> parseItem(String itemLink, boolean overwrite, List<String> filter, List<String> nameFilter) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);


        Elements headingElements = doc.getElementsByClass("page-header__title");

        if (headingElements.size() != 1) {
            return List.of();
        }

        String speciesName = headingElements.first().text();

        if (speciesName.equals("t'landa Til")) {
            speciesName = "T'landa Til";
        }

        if ("home".equals(speciesName)) {
            return new ArrayList<>();
        }


//        if (!speciesName.equals("Replica Droid")) {
//            return new ArrayList<>();
//        }

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

        boolean isDroid = speciesName.toLowerCase().contains("droid");

        List<String> variants = getVariants(speciesName);

        List<JSONy> jsonies = Lists.newArrayList();
        for (String variant :
                variants) {

            Set<Category> categories = new HashSet<>(Category.getCategories(doc, variant));

            Species species = Species.create(variant)
                    .withDescription(content)
                    .withImage(imageFile)
                    .with(categories)
                    .with(addTraitsFromCategories(categories, variant))
                    .with(StartingFeats.getStartingFeatsFromCategories(categories))
                    .with(StatBonuses.getStatBonuses(content, variant))
                    .with(getDroidChoice(variant))
                    .with(getSpeciesSpecificChoice(variant))
                    .with(Speed.getSpeed(content, variant))
                    .with(AgeCategories.getAgeCategories(content, isDroid))
                    .with(getWeaponFamiliarity(variant))
                    .with(getBonusTree(variant))
                    .with(getManualBonusItems(variant))
                    .with(getBonusItems(content, variant))
                    .with(getLanguageFeatures(variant));
            jsonies.add(species);
        }


        return jsonies;
    }

    private Collection<?> getLanguageFeatures(String variant) {
        List<Object> provided = new ArrayList<>();
        switch (variant){
            case "Gamorrean":
                provided.add(Change.create(ChangeKey.MAY_SPEAK, "Gamorrean"));
                break;
            case "Wookiee":
                provided.add(Change.create(ChangeKey.MAY_SPEAK, "Shyriiwook"));
                provided.add(Change.create(ChangeKey.MAY_SPEAK, "Thykarann"));
                provided.add(Change.create(ChangeKey.MAY_SPEAK, "Xaczik"));
                break;
            default:
                break;
        }
        return provided;
    }

    private List<String> getVariants(String speciesName) {
        if ("Umbaran".equals(speciesName)) {
            return List.of("Umbaran", "Umbaran (Alternate Species Traits)");
        }
        return List.of(speciesName);
    }

    private static Collection<?> addTraitsFromCategories(Set<Category> categories, String speciesName) {
        List<Object> provided = new ArrayList<>();
        boolean skipSizes = false;

        if ("Neti".equals(speciesName)) {
            skipSizes = true;
            provided.add(ProvidedItem.create("Medium", TRAIT));
            provided.add(Effect.create("Humanoid", "Metamorph", List.of(
                    Change.create(ChangeKey.SPEED, "Base Speed 4")
            ), List.of(Link.create("Metamorph", LinkType.EXCLUSIVE))).enabled());
            provided.add(Effect.create("Quadrupedal", "Metamorph", List.of(
                    Change.create(ChangeKey.SIZE_BONUS, 1),
                    Change.create(ChangeKey.SPEED, "Base Speed 2"),
                    Change.create(ChangeKey.DISABLE, "Run"),
                    Change.create(ChangeKey.DISABLE, "Charge"),
                    Change.create(ChangeKey.RESIST, "Prone:5")
            ), List.of(Link.create("Metamorph", LinkType.EXCLUSIVE))));
            provided.add(Effect.create("Treelike", "Metamorph", List.of(
                    Change.create(ChangeKey.SIZE_BONUS, 2),
                    Change.create(ChangeKey.SPEED, "Stationary 0"),
                    Change.create(ChangeKey.DISABLE, "Run"),
                    Change.create(ChangeKey.DISABLE, "Charge"),
                    Change.create(ChangeKey.RESIST, "Prone:15")
            ), List.of(Link.create("Metamorph", LinkType.EXCLUSIVE))));
        }


        for (Category category : categories) {

            if (DUMMY_CATEGORIES.contains(category.getValue())) {
                continue;
            }

            if ("Extra Arms".equals(category.getValue())) {
                provided.addAll(getExtraArms(speciesName));
            } else if ("Bonus Trained Skill".equals(category.getValue())) {
                if (!"Human".equals(speciesName)) {
                    provided.add(ProvidedItem.create(category.getValue(), TRAIT));
                    //System.out.println("BONUS TRAINED SKILL " + speciesName);
                }
                continue;
            }
            if ("Weapon Familiarity".equals(category.getValue()) || "Bonus Feat".equals(category.getValue())) {
                continue;
            }
            if(skipSizes && List.of("Tiny", "Small", "Medium", "Large", "Huge").contains(category.getValue())){
                continue;
            }
            provided.add(ProvidedItem.create(category.getValue(), TRAIT));


        }
        return provided;
    }

    private static List<Object> getExtraArms(String speciesName) {
        List<Object> extraArms = new ArrayList<>();
        if ("Besalisk".equals(speciesName)) {
            extraArms.add(ProvidedItem.create("Extra Arms 2", TRAIT, "GENDER:Male"));
            extraArms.add(ProvidedItem.create("Extra Arms 4", TRAIT, "GENDER:Female"));

//            extraArms.add(ProvidedItem.create("Extra Arms 4", ItemType.TRAIT, "GENDER:Female", "TRAIT:4 Arm Option"));
//            extraArms.add(ProvidedItem.create("Extra Arms 6", ItemType.TRAIT, "GENDER:Female", "TRAIT:6 Arm Option"));
//            extraArms.add(new Choice("Female Besalisks most commonly have 6 arms but can have as many as 8"));
        } else if ("Ebranite".equals(speciesName) || "Harch".equals(speciesName)) {
            extraArms.add(ProvidedItem.create("Extra Arms 4", TRAIT));
        } else {
            extraArms.add(ProvidedItem.create("Extra Arms 2", TRAIT));
            //printUnique(speciesName);

        }
        return extraArms;
    }

    private static Collection<?> getBonusItems(Element content, String variant) {
        List<Object> provided = new ArrayList<>();

        if ("Umbaran (Alternate Species Traits)".equals(variant)) {

            return provided;
        }

        for (Element child : content.select("p,li")) {
//            if(child.children().isEmpty()) {
            provided.addAll(getTrait(child, variant));
//            } else {
//                provided.addAll(getBonusItems(child));
//            }
        }
        return provided;
    }

    private static Collection<?> getTrait(Element child, String speciesName) {

        List<Object> provided = new ArrayList<>();

        if (!"Human".equals(speciesName)) {
            Matcher bonusFeat = BONUS_FEAT_PATTERN.matcher(child.text());
            if (bonusFeat.find()) {
                provided.add(ProvidedItem.create("Bonus Feat", TRAIT));
            }
        }
        if (false && child.text().startsWith("Automatic Language")) {
            languages++;

            Pattern p = Pattern.compile("can (?:both )?speak, read,? and write (?:both )?([\\w\\s-',]*)");

            Matcher m = p.matcher(child.text());
            if (m.find()) {
                if (child.text().contains(". ")) {
                    String complex = child.text().split("\\. ")[1];
                    if ("After the fall of the Empire, Dressellese are also fluent in Bothese.".equals(complex)) {
                        provided.add(new Choice("After the fall of the Empire, Dressellese are also fluent in Bothese.")
                                .withOption("Before the fall of the Empire", new Option())
                                .withOption("After the fall of the Empire", new Option()
                                        .withChange(Change.create(ChangeKey.SPEAKS, "Bothese"))
                                        .withChange(Change.create(ChangeKey.READS, "Bothese"))
                                        .withChange(Change.create(ChangeKey.WRITES, "Bothese")))
                        );
                    }

                    //printUnique(complex); //child.text());
                }
                Set<String> langs = Arrays.stream(m.group(1).split(" and ")).filter(lang -> null != lang && !lang.isBlank()).map(String::trim).collect(Collectors.toSet());
                for (String s : langs) {
                    if ("Basic, Chev,".equals(s)) {
                        provided.add(Change.create(ChangeKey.SPEAKS, "Basic"));
                        provided.add(Change.create(ChangeKey.READS, "Basic"));
                        provided.add(Change.create(ChangeKey.WRITES, "Basic"));

                        provided.add(Change.create(ChangeKey.SPEAKS, "Chev"));
                        provided.add(Change.create(ChangeKey.READS, "Chev"));
                        provided.add(Change.create(ChangeKey.WRITES, "Chev"));
                    } else if ("Basic, Huttese,".equals(s)) {
                        provided.add(Change.create(ChangeKey.SPEAKS, "Basic"));
                        provided.add(Change.create(ChangeKey.READS, "Basic"));
                        provided.add(Change.create(ChangeKey.WRITES, "Basic"));

                        provided.add(Change.create(ChangeKey.SPEAKS, "Huttese"));
                        provided.add(Change.create(ChangeKey.READS, "Huttese"));
                        provided.add(Change.create(ChangeKey.WRITES, "Huttese"));
                    } else if ("Nikto, as well as either Basic or Huttese".equals(s)) {
                        provided.add(Change.create(ChangeKey.SPEAKS, "Nikto"));
                        provided.add(Change.create(ChangeKey.READS, "Nikto"));
                        provided.add(Change.create(ChangeKey.WRITES, "Nikto"));

                        provided.add(new Choice("Select an available language")
                                .withOption("Basic", new Option()
                                        .withChange(Change.create(ChangeKey.SPEAKS, "Basic"))
                                        .withChange(Change.create(ChangeKey.READS, "Basic"))
                                        .withChange(Change.create(ChangeKey.WRITES, "Basic")))
                                .withOption("Huttese", new Option()
                                        .withChange(Change.create(ChangeKey.SPEAKS, "Huttese"))
                                        .withChange(Change.create(ChangeKey.READS, "Huttese"))
                                        .withChange(Change.create(ChangeKey.WRITES, "Huttese")))
                        );
                    } else {
                        provided.add(Change.create(ChangeKey.SPEAKS, s));
                        provided.add(Change.create(ChangeKey.READS, s));
                        provided.add(Change.create(ChangeKey.WRITES, s));
                    }
                    //printUnique(s);
                    //do i want 3 systems?
                }

            } else {
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
                    size.add(ProvidedItem.create(category, TRAIT));
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
                attributes.add(Change.create(ChangeKey.BONUS_TALENT_TREE, "1st-Degree Droid Talent Tree"));
                attributes.add(ProvidedItem.create("Droid Default Appendage Offset", TRAIT));
                break;
            case "Astromech Droid":
            case "Mechanic Droid":
            case "2nd-Degree Droid Model":
                attributes.add(Change.create(ChangeKey.BONUS_TALENT_TREE, "2nd-Degree Droid Talent Tree"));
                attributes.add(ProvidedItem.create("Droid Default Appendage Offset", TRAIT));
                break;
            case "Protocol Droid":
            case "Service Droid":
            case "3rd-Degree Droid Model":
                attributes.add(Change.create(ChangeKey.BONUS_TALENT_TREE, "3rd-Degree Droid Talent Tree"));
                attributes.add(ProvidedItem.create("Droid Default Appendage Offset", TRAIT));
                break;
            case "Battle Droid":
            case "Probe Droid":
            case "4th-Degree Droid Model":
                attributes.add(Change.create(ChangeKey.BONUS_TALENT_TREE, "4th-Degree Droid Talent Tree"));
                attributes.add(ProvidedItem.create("Droid Default Appendage Offset", TRAIT));
                break;
            case "Labor Droid":
            case "5th-Degree Droid Model":
                attributes.add(ProvidedItem.create("Droid Default Appendage Offset", TRAIT));
                attributes.add(Change.create(ChangeKey.BONUS_TALENT_TREE, "5th-Degree Droid Talent Tree"));
                break;

        }

        if (speciesName.contains(" Droid")) {
            attributes.add(Change.create(ChangeKey.IS_DROID, "true"));
        }

        return attributes;
    }

    private static Set<ProvidedItem> getWeaponFamiliarity(String speciesName) {
        switch (speciesName) {
            case "Chazrach":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Amphistaff(Quarterstaff,Spear):Simple Melee Weapons)", TRAIT));
            case "Felucian":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Felucian Skullblade:Simple Melee Weapons)", TRAIT));
            case "Gamorrean":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Arg'garok:Advanced Melee Weapons)", TRAIT));
            case "Gand":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Gand Weapon Template:Analogous Simple Weapons)", TRAIT));
            case "Gungan":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Atlatl:Simple Melee Weapons)", TRAIT), ProvidedItem.create("Weapon Familiarity (Cesta:Simple Melee Weapon)", TRAIT), ProvidedItem.create("Weapon Familiarity (Electropole:Simple Melee Weapon)", TRAIT));
            case "Kerestian":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Darkstick:Simple Melee Weapons)", TRAIT));
            case "Kissai":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Massassi Lanvarok:Simple Ranged Weapons)", TRAIT));
            case "Kyuzo":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Kyuzo Battle Helmet:Simple Melee Weapons)", TRAIT));
            case "Lasat":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Bo-Rifle:Rifles)", TRAIT));
            case "Massassi":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Massassi Lanvarok:Simple Ranged Weapons)", TRAIT));
            case "Nagai":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Tehk'la Blade:Simple Melee Weapons)", TRAIT));
            case "Rakata":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Rakatan Weapon Template:Analogous Simple Weapons)", TRAIT));
            case "Squib":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Squib Tensor Rifle:Rifless)", TRAIT));
            case "Verpine":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Verpine Shattergun:Pistols)", TRAIT));
            case "Wookiee":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Bowcaster:Rifles)", TRAIT), ProvidedItem.create("Weapon Familiarity (Ryyk Blade:Advanced Melee Weapons)", TRAIT));
            case "Yuuzhan Vong":
                return Sets.newHashSet(ProvidedItem.create("Weapon Familiarity (Amphistaff:Simple Melee Weapons)", TRAIT));
        }

        return null;
    }

    private static Collection<Object> getSpeciesSpecificChoice(String speciesName) {
        Collection<Object> choices = new ArrayList<>();
        if ("Arkanian Offshoot".equals(speciesName)) {
            Choice choice = new Choice("Select a Bonus Feat:")
                    .withShowSelectionInName(false);
            choice.withOption("Skill Focus (Endurance)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Endurance))", TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Endurance)", FEAT)));
            choice.withOption("Skill Focus (Mechanics)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Mechanics))", TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Mechanics)", FEAT)));
            choice.withOption("Skill Focus (Pilot)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Pilot))", TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Pilot)", FEAT)));
            choice.withOption("Skill Focus (Survival)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Survival))", TRAIT)).withProvidedItem(ProvidedItem.create("Skill Focus (Survival)", FEAT)));
            choices.add(choice);
        } else if ("Aqualish".equals(speciesName)) {
            Choice choice = new Choice("Select a Subspecies:");
            choice.withOption("None", new Option());
            choice.withOption("Aquala", new Option().withProvidedItem(ProvidedItem.create("Aquala", TRAIT)).withProvidedItem(ProvidedItem.create("Swim Speed (2)", TRAIT)));
            choice.withOption("Kyuzo", new Option().withProvidedItem(ProvidedItem.create("Kyuzo", TRAIT)).withProvidedItem(ProvidedItem.create("Heightened Agility", TRAIT)).withProvidedItem(ProvidedItem.create("Bonus Trained Skill (Acrobatics)", TRAIT)));
            choice.withOption("Quara", new Option().withProvidedItem(ProvidedItem.create("Quara", TRAIT)).withProvidedItem(ProvidedItem.create("Intimidating", TRAIT)));
            choice.withOption("Ualaq", new Option().withProvidedItem(ProvidedItem.create("Ualaq", TRAIT)).withProvidedItem(ProvidedItem.create("Darkvision", TRAIT)));
            choices.add(choice);
        } else if ("Killik".equals(speciesName)) {
            Choice choice = new Choice("Select a Size:")
                    .withShowSelectionInName(false);
            choice.withOption("Tiny", new Option().withProvidedItem(ProvidedItem.create("Tiny", TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (+4)", TRAIT)).withProvidedItem(ProvidedItem.create("Strength (-4)", TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d3)", ItemType.ITEM)));
            choice.withOption("Small", new Option().withProvidedItem(ProvidedItem.create("Small", TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (+2)", TRAIT)).withProvidedItem(ProvidedItem.create("Strength (-2)", TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d4)", ItemType.ITEM)));
            choice.withOption("Medium", new Option().withProvidedItem(ProvidedItem.create("Medium", TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d6)", ItemType.ITEM)));
            choice.withOption("Medium", new Option().withProvidedItem(ProvidedItem.create("Medium", TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d6)", ItemType.ITEM)));
            choice.withOption("Large", new Option().withProvidedItem(ProvidedItem.create("Large", TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (6)", TRAIT)).withProvidedItem(ProvidedItem.create("Strength (+8)", TRAIT)).withProvidedItem(ProvidedItem.create("Constitution (+8)", TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (-2)", TRAIT)).withProvidedItem(ProvidedItem.create("Claw (1d8)", ItemType.ITEM)));
            choice.withOption("Huge", new Option().withProvidedItem(ProvidedItem.create("Huge", TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (4)", TRAIT)).withProvidedItem(ProvidedItem.create("Strength (+16)", TRAIT)).withProvidedItem(ProvidedItem.create("Constitution (+16)", TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (-4)", TRAIT)).withProvidedItem(ProvidedItem.create("Claw (2d6)", ItemType.ITEM)));
            choice.withOption("Gargantuan", new Option().withProvidedItem(ProvidedItem.create("Gargantuan", TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (4)", TRAIT)).withProvidedItem(ProvidedItem.create("Strength (+24)", TRAIT)).withProvidedItem(ProvidedItem.create("Constitution (+24)", TRAIT)).withProvidedItem(ProvidedItem.create("Dexterity (-4)", TRAIT)).withProvidedItem(ProvidedItem.create("Claw (3d6)", ItemType.ITEM)));
            Option option = new Option().withProvidedItem(ProvidedItem.create("Colossal", TRAIT)).withProvidedItem(ProvidedItem.create("Base Speed (4)", TRAIT)).withProvidedItem(ProvidedItem.create("Strength (+32)", TRAIT));
            option.withProvidedItem(ProvidedItem.create("Constitution (+32)", TRAIT));
            choice.withOption("Colossal", option.withProvidedItem(ProvidedItem.create("Dexterity (-4)", TRAIT)).withProvidedItem(ProvidedItem.create("Claw (4d6)", ItemType.ITEM)));
            choices.add(choice);
        } else if ("Battle Droid".equals(speciesName)) {
            choices.add(ProvidedItem.create("Weapon Proficiency", FEAT));
            Choice choice = new Choice("Select an Armor Proficiency:")
                    .withShowSelectionInName(false);
            choice.withOption("Light", new Option().withProvidedItem(ProvidedItem.create("Armor Proficiency (Light)", FEAT)));
            choice.withOption("Medium", new Option().withProvidedItem(ProvidedItem.create("Armor Proficiency (Medium)", FEAT)));
            choice.withOption("Heavy", new Option().withProvidedItem(ProvidedItem.create("Armor Proficiency (Heavy)", FEAT)));
            choices.add(choice);
        } else if ("Astromech Droid".equals(speciesName)) {
            Choice choice = new Choice("Select a Bonus Feat:")
                    .withShowSelectionInName(false);
            choice.withOption("Skill Focus (Mechanics)", new Option().withProvidedItem(ProvidedItem.create("Skill Focus (Mechanics)", FEAT)));
            choice.withOption("Skill Focus (Use Computer)", new Option().withProvidedItem(ProvidedItem.create("Skill Focus (Use Computer)", FEAT)));
            choices.add(choice);
            choices.add(ProvidedItem.create("Bonus Trained Skill (Mechanics)", TRAIT));
        } else if ("Labor Droid".equals(speciesName)) {
            choices.add(Change.createReRoll("Strength", "kh", ""));
        } else if ("Mechanic Droid".equals(speciesName)) {
            choices.add(Change.createReRoll("Mechanics", "", ""));
            choices.add(ProvidedItem.create("Bonus Trained Skill (Mechanics)", TRAIT));
            Choice locomotionChoice = new Choice("Select locomotion type:")
                    .withShowSelectionInName(false);

            locomotionChoice.withOption("Walking", new Option().withProvidedItem(ProvidedItem.create("Walking", ItemType.ITEM)));
            locomotionChoice.withOption("Tracked", new Option().withProvidedItem(ProvidedItem.create("Tracked", ItemType.ITEM)));
            locomotionChoice.withOption("Wheeled", new Option().withProvidedItem(ProvidedItem.create("Wheeled", ItemType.ITEM)));

            choices.add(locomotionChoice);
        }else if ("Medical Droid".equals(speciesName)) {
            Choice choice = new Choice("Select a Bonus Feat:")
                    .withShowSelectionInName(false);
            choice.withOption("Skill Focus (Knowledge (Life Sciences))", new Option().withProvidedItem(ProvidedItem.create("Skill Focus (Knowledge (Life Sciences))", FEAT)));
            choice.withOption("Skill Focus (Treat Injury)", new Option().withProvidedItem(ProvidedItem.create("Skill Focus (Treat Injury)", FEAT)));
            choices.add(choice);

            choices.add(ProvidedItem.create("Bonus Trained Skill (Treat Injury)", TRAIT));
        } else if ("Probe Droid".equals(speciesName)) {
            Choice choice = new Choice("Select a Bonus Feat:")
                    .withShowSelectionInName(false);
            choice.withOption("Skill Focus (Perception)", new Option().withProvidedItem(ProvidedItem.create("Skill Focus (Perception)", FEAT)));
            choice.withOption("Skill Focus (Stealth)", new Option().withProvidedItem(ProvidedItem.create("Skill Focus (Stealth)", FEAT)));
            choices.add(choice);

            choices.add(ProvidedItem.create("Bonus Trained Skill (Perception)", TRAIT));
        } else if ("Protocol Droid".equals(speciesName)) {
            Choice choice = new Choice("Select a Bonus Feat:")
                    .withShowSelectionInName(false);
            choice.withOption("Skill Training (Knowledge (Bureaucracy))", new Option().withProvidedItem(ProvidedItem.create("Skill Training (Knowledge (Bureaucracy))", FEAT)));
            choice.withOption("Skill Training (Knowledge (Galactic Lore))", new Option().withProvidedItem(ProvidedItem.create("Skill Training (Knowledge (Galactic Lore))", FEAT)));
            choice.withOption("Skill Training (Knowledge (Social Sciences))", new Option().withProvidedItem(ProvidedItem.create("Skill Training (Knowledge (Social Sciences))", FEAT)));
            choices.add(choice);

            choices.add(ProvidedItem.create("Bonus Trained Skill (Persuasion)", TRAIT));
        } else if ("Service Droid".equals(speciesName)) {
            Choice choice = new Choice("Select a Bonus Feat:")
                    .withShowSelectionInName(false);
            choice.withOption("Skill Training (Perception)", new Option().withProvidedItem(ProvidedItem.create("Skill Training (Perception)", FEAT)));
            choice.withOption("Skill Training (Knowledge (Bureaucracy))", new Option().withProvidedItem(ProvidedItem.create("Skill Training (Knowledge (Bureaucracy))", FEAT)));
            choice.withOption("Skill Training (Knowledge (Galactic Lore))", new Option().withProvidedItem(ProvidedItem.create("Skill Training (Knowledge (Galactic Lore))", FEAT)));
            choices.add(choice);
        } else if ("Human".equals(speciesName)) {

            Choice choice = new Choice("Select Near-Human Option:");
            choice.withOption("Human (Default)", new Option().isDefault()
                    .withProvidedItem(ProvidedItem.create("Bonus Trained Skill", TRAIT))
                    .withProvidedItem(ProvidedItem.create("Bonus Feat", TRAIT)));
            choice.withOption("Near-Human (Give Up Bonus Feat)", new Option()
                    .withProvidedItem(ProvidedItem.create("Bonus Trained Skill", TRAIT))
                    .withProvidedItem(ProvidedItem.create("Near-Human Trait", TRAIT)));
            choice.withOption("Near-Human (Give Up Bonus Trained Skill)", new Option()
                    .withProvidedItem(ProvidedItem.create("Bonus Feat", TRAIT))
                    .withProvidedItem(ProvidedItem.create("Near-Human Trait", TRAIT)));
            choices.add(choice);
        }

        return choices;
    }

    private static List<Object> getManualBonusItems(String speciesName) {
        List<Object> attributes = new ArrayList<>();
        for (String item : getDroidComponents(speciesName)) {
            attributes.add(ProvidedItem.create(item, ItemType.ITEM).withEquip("equipped"));
        }
        return attributes;
    }

    private static ArrayList<String> getDroidComponents(String speciesName) {
        if ("Labor Droid".equals(speciesName)) {
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


    private static Collection<Object> getDroidChoice(String speciesName) {
        Collection<Object> choices = new ArrayList<>();
        if (!speciesName.toLowerCase().contains("droid")) {
            return choices;
        }
        if ("Replica Droid".equals(speciesName)) {
            Choice replicaSpecies = new Choice("Select the Species that this Droid will replicate");
            replicaSpecies.withOption("AVAILABLE_SPECIES", new Option().withPayload("AVAILABLE_SPECIES"));
            choices.add(replicaSpecies);

            Choice replicaOptionalComponents = new Choice("Select two optional components")
                    .withShowSelectionInName(false)
                    .withAvailableSelections(2)
                    .withOption(Option.create("Internal Comlink"))
                    .withOption(Option.create("Darkvision"))
                    .withOption(Option.create("Diagnosis Package"))
                    .withOption(Option.create("Improved Sensor Package"))
                    .withOption(Option.create("Internal Storage (Subject to size limitations)", "Internal Storage"))
                    .withOption(Option.create("Translator Unit (DC 15)"));
            choices.add(replicaOptionalComponents);

            return choices;
        }

        Choice droidChoice = new Choice("Select the size of your droid's chassis:")
                .withShowSelectionInName(false);

        droidChoice.withOption(new Option("Fine (GM Only)", "Fine").withProvidedItem(ProvidedItem.create("Fine", TRAIT)));
        droidChoice.withOption(new Option("Diminutive (GM Only)", "Diminutive").withProvidedItem(ProvidedItem.create("Diminutive", TRAIT)));
        droidChoice.withOption(new Option("Tiny (GM Only)", "Tiny").withProvidedItem(ProvidedItem.create("Tiny", TRAIT)));
        droidChoice.withOption(new Option("Small").withProvidedItem(ProvidedItem.create("Small", TRAIT)));
        droidChoice.withOption(new Option("Medium").withProvidedItem(ProvidedItem.create("Medium", TRAIT)));
        droidChoice.withOption(new Option("Large (GM Only)", "Large").withProvidedItem(ProvidedItem.create("Large", TRAIT)));
        droidChoice.withOption(new Option("Huge (GM Only)", "Huge").withProvidedItem(ProvidedItem.create("Huge", TRAIT)));
        droidChoice.withOption(new Option("Gargantuan (GM Only)", "Gargantuan").withProvidedItem(ProvidedItem.create("Gargantuan", TRAIT)));
        droidChoice.withOption(new Option("Colossal (GM Only)", "Colossal").withProvidedItem(ProvidedItem.create("Colossal", TRAIT)));
        choices.add(droidChoice);

        choices.add(ProvidedItem.create("Droid Traits", TRAIT));
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
