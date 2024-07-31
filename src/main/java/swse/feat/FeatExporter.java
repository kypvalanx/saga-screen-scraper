package swse.feat;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.Change;
import swse.common.ChangeKey;
import swse.common.BaseExporter;
import swse.common.Category;
import swse.common.Choice;
import swse.common.JSONy;
import swse.common.Option;
import swse.prerequisite.Prerequisite;
import swse.util.Context;


public class FeatExporter extends BaseExporter {

    //static Map<String, List<String>> classStartingFeats = Maps.newHashMap();

    static Multimap<String, String> startingFeats = ArrayListMultimap.create();

    static {
//        classStartingFeats.put("Jedi", List.of("Force Sensitivity", "Weapon Proficiency (Lightsabers)", "Weapon Proficiency (Simple Weapons)");
//classStartingFeats.put("Noble", List.of("Linguist", "Weapon Proficiency (Pistols)", "Weapon Proficiency (Simple Weapons)");
//classStartingFeats.put("Scoundrel", List.of("Point-Blank Shot", "Weapon Proficiency (Pistols)", "Weapon Proficiency (Simple Weapons)");
//classStartingFeats.put("Scout", List.of("Shake It Off", "Weapon Proficiency (Pistols)", "Weapon Proficiency (Rifles)", "Weapon Proficiency (Simple Weapons)");
//classStartingFeats.put("Soldier", List.of("Armor Proficiency (Light)", "Armor Proficiency (Medium)", "Weapon Proficiency (Pistols)", "Weapon Proficiency (Rifles)", "Weapon Proficiency (Simple Weapons)");
//classStartingFeats.put("Technician", List.of("Tech Specialist", "Weapon Proficiency (Simple Weapons)");
//classStartingFeats.put("Force Prodigy", List.of("Force Sensitivity", "Force Training", "Weapon Proficiency (Simple Weapons)");
//classStartingFeats.put("Nonheroic", List.of("Armor Proficiency (Light)", "Armor Proficiency (Medium)", "Skill Focus", "Skill Training", "Weapon Proficiency (Advanced Melee Weapons)", "Weapon Proficiency (Heavy Weapons)", "Weapon Proficiency (Pistols)", "Weapon Proficiency (Rifles)", "Weapon Proficiency (Simple Weapons)");

        startingFeats.put("Force Sensitivity", "Jedi");
        startingFeats.put("Weapon Proficiency (Lightsabers)", "Jedi");
        startingFeats.put("Weapon Proficiency (Simple Weapons)", "Jedi");
        startingFeats.put("Linguist", "Noble");
        startingFeats.put("Weapon Proficiency (Pistols)", "Noble");
        startingFeats.put("Weapon Proficiency (Simple Weapons)", "Noble");
        startingFeats.put("Point-Blank Shot", "Scoundrel");
        startingFeats.put("Weapon Proficiency (Pistols)", "Scoundrel");
        startingFeats.put("Weapon Proficiency (Simple Weapons)", "Scoundrel");
        startingFeats.put("Shake It Off", "Scout");
        startingFeats.put("Weapon Proficiency (Pistols)", "Scout");
        startingFeats.put("Weapon Proficiency (Rifles)", "Scout");
        startingFeats.put("Weapon Proficiency (Simple Weapons)", "Scout");
        startingFeats.put("Armor Proficiency (Light)", "Soldier");
        startingFeats.put("Armor Proficiency (Medium)", "Soldier");
        startingFeats.put("Weapon Proficiency (Pistols)", "Soldier");
        startingFeats.put("Weapon Proficiency (Rifles)", "Soldier");
        startingFeats.put("Weapon Proficiency (Simple Weapons)", "Soldier");
        startingFeats.put("Tech Specialist", "Technician");
        startingFeats.put("Weapon Proficiency (Simple Weapons)", "Technician");
        startingFeats.put("Force Sensitivity", "Force Prodigy");
        startingFeats.put("Force Training", "Force Prodigy");
        startingFeats.put("Weapon Proficiency (Simple Weapons)", "Force Prodigy");
        startingFeats.put("Armor Proficiency (Light)", "Nonheroic");
        startingFeats.put("Armor Proficiency (Medium)", "Nonheroic");
        startingFeats.put("Skill Focus", "Nonheroic");
        startingFeats.put("Skill Training", "Nonheroic");
        startingFeats.put("Weapon Proficiency (Advanced Melee Weapons)", "Nonheroic");
        startingFeats.put("Weapon Proficiency (Heavy Weapons)", "Nonheroic");
        startingFeats.put("Weapon Proficiency (Pistols)", "Nonheroic");
        startingFeats.put("Weapon Proficiency (Rifles)", "Nonheroic");
        startingFeats.put("Weapon Proficiency (Simple Weapons)", "Nonheroic");

        //System.out.println(startingFeats);

        JSONObject startingFeatLookup = new JSONObject();
        for (String key :
                startingFeats.keySet()) {
            Collection<String> classes = startingFeats.get(key);
            startingFeatLookup.put(key, classes);
        }
        //System.out.println(startingFeatLookup);
    }


    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\feats.json";
    public static final String DB_FILE = SYSTEM_LOCATION + "/packs/feats";
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\feats.csv";
    public static final String WEAPON_PROFICIENCY = "Weapon Proficiency";
    public static final String WEAPON_FOCUS = "Weapon Focus";
    public static final String SKILL_FOCUS = "Skill Focus";
    public static final String EXOTIC_WEAPON_PROFICIENCY = "Exotic Weapon Proficiency";
    private static final String SKILL_MASTERY = "Skill Mastery";
    private static Set<String> allFeats = new HashSet<>();

    public static void main(String[] args) throws IOException {

        String dir = LOCAL_ROOT + IMAGE_FOLDER + "/feat";

        for (String file :
                new File(dir).list()) {
            availableFiles.put(file.substring(0, file.lastIndexOf(".")).toLowerCase().trim().replace("-", " ").replace("ä", "a"), file);
        }

        List<String> featLinks = new ArrayList<>(getAlphaLinks("/wiki/Category:Feats?from="));


        List<JSONObject> entries = new FeatExporter().getEntriesFromCategoryPage(featLinks, true);

        //printUniqueNames(entries);
        
        //addIdsFromDb(new File(DB_FILE), entries);
        //writeToDB(new File(DB_FILE), entries, hasArg(args, "d"));


        System.out.println("unused feats images" + availableFiles);
        System.out.println("items without images" + itemsWithoutImages);



        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"), "Feats");
    }


    protected Collection<JSONy> parseItem(String itemLink, boolean overwrite, List<String> filter, List<String> nameFilter) {
        if (null == itemLink) {
            return new ArrayList<>();
        }
        Document doc = null;
        try {
            doc = Jsoup.connect(ROOT + itemLink).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc == null) {
            return new ArrayList<>();
        }
        String itemName = getItemName(doc);

        if ("home".equals(itemName)) {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        allFeats.add(itemName);
        Context.setValue("name", itemName);

        Prerequisite prerequisite = Prerequisite.getPrerequisite(content.select("p"));

        prerequisite = getPrerequisites(itemName, prerequisite);
        //prerequisites.addAll(getPrerequisites(itemName));

        Set<Category> categories = Category.getCategories(doc);

//        Collection<String> startingClasses = startingFeats.get(itemName);
//        if(startingClasses != null){
//            for (String startingClass :
//                    startingClasses) {
//                categories.add(Category.create(startingClass + " Starting Feats"));
//            }
//        }

        List<JSONy> feats = new ArrayList<>();

        feats.add(Feat.create(itemName)
                        .withImage(getImage("feat", itemName))
                        .withSource(content)
                .withDescription(content)
                .withProvided(getPayloadChoice(itemName))
                .withPrerequisite(prerequisite)
                .withCategories(categories)
                .withProvided(getGeneratedAttributes(content))
                .withProvided(getManualAttributes(itemName)));

        return feats;
    }

    private static Collection<?> getGeneratedAttributes(Element content) {
        List<Object> provided = new ArrayList<>();

        for (Element child : content.children()) {
            if (child.text().contains(":")) {
                final String[] split = child.text().split(":");
                if(split.length>1) {
                    final String label = split[0];
                    final String payload = split[1].trim();

                    switch (label) {
                        case "Special":
                            if (payload.startsWith("You can select this Feat multiple times") ||
                                    payload.startsWith("You may select this Feat multiple times") ||
                                    payload.startsWith("You can take this Feat more than once") ||
                                    payload.startsWith("You can gain this Feat multiple times") ||
                                    payload.startsWith("You can take this Feat multiple times") ||
                                    payload.startsWith("You may take this Feat more than once") ||
                                    payload.startsWith("This Feat may be selected multiple times")) {
                                provided.add(Change.create(ChangeKey.TAKE_MULTIPLE_TIMES, "true"));
                            }
//                        case "Effect":
//                            printUnique(payload);
                        default:
                    }
                }
            }
        }
        return provided;
    }

    private static Prerequisite getPrerequisites(String itemName, Prerequisite prerequisite) {
        Prerequisite added = null;
        if (SKILL_FOCUS.equals(itemName)) {
            added = Prerequisite.create("Trained in #payload#");
        }
        else if("Skill Mastery".equals(itemName)){
            added = Prerequisite.create("Skill Focus (#payload#)");
        }

        return Prerequisite.merge(prerequisite, added);
    }

    private static List<Object> getManualAttributes(String itemName) {
        List<Object> changes = new ArrayList<>();
        if (itemName == null) {
            return changes;
        }

        switch (itemName) {
            case "Improved Damage Threshold":
                changes.add(Change.create(ChangeKey.DAMAGE_THRESHOLD_BONUS, 5));
                break;
            case "Toughness":
                changes.add(Change.create(ChangeKey.HIT_POINT_EQ, "@charLevel"));
                break;
            case "Weapon Proficiency":
            case "Exotic Weapon Proficiency":
                changes.add(Change.create(ChangeKey.WEAPON_PROFICIENCY, "#payload#"));
                break;
            case "Armor Proficiency":
                changes.add(Change.create(ChangeKey.ARMOR_PROFICIENCY, "#payload#"));
                break;
            case "Weapon Focus":
                changes.add(Change.create(ChangeKey.WEAPON_FOCUS, "#payload#"));
                break;
            case "Skill Focus":
                changes.add(Change.create(ChangeKey.SKILL_FOCUS, "#payload#"));
                break;
            case "Skill Mastery":
                changes.add(Change.create(ChangeKey.SKILL_MASTERY, "#payload#"));
                break;
            case "Double Attack":
                changes.add(Change.create(ChangeKey.DOUBLE_ATTACK, "#payload#"));
                break;
            case "Triple Attack":
                changes.add(Change.create(ChangeKey.TRIPLE_ATTACK, "#payload#"));
                break;
            case "Savage Attack":
                changes.add(Change.create(ChangeKey.SAVAGE_ATTACK, "#payload#"));
            case "Relentless Attack":
                changes.add(Change.create(ChangeKey.RELENTLESS_ATTACK, "#payload#"));
                break;
            case "Autofire Sweep":
                changes.add(Change.create(ChangeKey.AUTOFIRE_SWEEP, "#payload#"));
                break;
            case "Autofire Assault":
                changes.add(Change.create(ChangeKey.AUTOFIRE_ASSAULT, "#payload#"));
                break;
            case "Halt":
                changes.add(Change.create(ChangeKey.HALT, "#payload#"));
                break;
            case "Return Fire":
                changes.add(Change.create(ChangeKey.RETURN_FIRE, "#payload#"));
                break;
            case "Critical Strike":
                changes.add(Change.create(ChangeKey.CRITICAL_STRIKE, "#payload#"));
                break;
            case "Force Sensitivity":
                changes.add(Change.create(ChangeKey.FORCE_SENSITIVITY, "true"));
                changes.add(Change.create(ChangeKey.BONUS_TALENT_TREE, "Force Talent Trees"));
                break;
            case "Weapon Finesse":
                changes.add(Change.create(ChangeKey.FINESSE_STAT, "DEX"));
                break;
            case "Force Training":
                changes.add(Change.create(ChangeKey.FORCE_TRAINING, "true"));
                changes.add(Change.create(ChangeKey.PROVIDES, "Force Powers:MAX(1 + @WISMOD,1)"));
                break;
            case "Dual Weapon Mastery I":
                changes.add(Change.create(ChangeKey.DUAL_WEAPON_MODIFIER, "-5"));
                break;
            case "Dual Weapon Mastery II":
                changes.add(Change.create(ChangeKey.DUAL_WEAPON_MODIFIER, "-2"));
                break;
            case "Dual Weapon Mastery III":
                changes.add(Change.create(ChangeKey.DUAL_WEAPON_MODIFIER, "0"));
                break;
            case "Skill Training":
                changes.add(Change.create(ChangeKey.TRAINED_SKILLS, "1"));

                //changes.add(Change.create(ChangeKey.AUTOMATIC_TRAINED_SKILL, "#payload#"));
                //changes.add(new Choice("Choose an automatically trained skill").withOption("AVAILABLE_UNTRAINED_SKILLS", new Option().withPayload("AVAILABLE_UNTRAINED_SKILLS")));
                break;
            case "Improved Defenses":
                changes.add(Change.create(ChangeKey.FORTITUDE_DEFENSE_BONUS, "1"));
                changes.add(Change.create(ChangeKey.WILL_DEFENSE_BONUS, "1"));
                changes.add(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS, "1"));
                break;
            case "Armor Proficiency (Light)":
                changes.add(Change.create(ChangeKey.ARMOR_PROFICIENCY, "light"));
                break;
            case "Armor Proficiency (Medium)":
                changes.add(Change.create(ChangeKey.ARMOR_PROFICIENCY, "medium"));
                break;
            case "Armor Proficiency (Heavy)":
                changes.add(Change.create(ChangeKey.ARMOR_PROFICIENCY, "heavy"));
                break;
            case "Grand Army of the Republic Training":
                //TODO this requires some very specific code...  maybe generalize this
                changes.add(Change.create(ChangeKey.APPLY_BONUS_TO, ChangeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT + ":will"));
                break;
            case "Force Regimen Mastery":
                changes.add(Change.create(ChangeKey.PROVIDES, "Force Regimen:MAX(1 + @WISMOD,1)"));
                break;
            case "Fight Through Pain":
                changes.add(Change.create(ChangeKey.DAMAGE_THRESHOLD_BONUS, "MAX(@FortDef, @WillDef) - @FortDef"));
                break;
            case "Force of Personality":
                changes.add(Change.create(ChangeKey.WILL_DEFENSE_BONUS, "MAX(@WISMOD, @CHAMOD) - @WISMOD"));
                break;
            case "Predictive Defense":
                changes.add(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS, "MAX(@DEXMOD, @INTMOD) - @DEXMOD"));
                break;
            case "Resilient Strength":
                changes.add(Change.create(ChangeKey.FORTITUDE_DEFENSE_BONUS, "MAX(@STRMOD, @CONMOD) - @CONMOD"));
                break;
            case "Extra Second Wind":
                changes.add(Change.create(ChangeKey.BONUS_SECOND_WIND, "1"));
                break;
            default:
        }
        if (itemName.startsWith("Martial Arts ")) {
            changes.add(Change.create(ChangeKey.BONUS_UNARMED_DAMAGE_DIE_SIZE, "1"));
            changes.add(Change.create(ChangeKey.BONUS_DODGE_REFLEX_DEFENSE, "1"));
        }
        return changes;
    }

    private static Choice getPayloadChoice(String itemName) {

        String[] WEAPON_PROFICIENCIES = {"Simple Weapons", "Pistols", "Rifles", "Lightsabers", "Heavy Weapons", "Advanced Melee Weapons"};
        String[] SKILL_FOCI = {
                "Acrobatics",
                "Climb",
                "Deception",
                "Endurance",
                "Gather Information",
                "Initiative",
                "Jump",
                "Knowledge (Bureaucracy)",
                "Knowledge (Galactic Lore)",
                "Knowledge (Life Sciences)",
                "Knowledge (Physical Sciences)",
                "Knowledge (Social Sciences)",
                "Knowledge (Tactics)",
                "Knowledge (Technology)",
                "Mechanics",
                "Perception",
                "Persuasion",
                "Pilot",
                "Ride",
                "Stealth",
                "Survival",
                "Swim",
                "Treat Injury",
                "Use Computer",
                "Use the Force"
        };
        if (SKILL_FOCUS.equals(itemName)) {
            Choice choice = new Choice("Select a Skill to Focus on", "You have no trained skills that you haven't already taken this feat for.");
            choice.withOneOption("You have the following skill available for Focus");
            choice.withOption("AVAILABLE_SKILL_FOCUS", new Option().withPayload("AVAILABLE_SKILL_FOCUS"));
            return choice;
        } else if (SKILL_MASTERY.equals(itemName)) {
            Choice choice = new Choice("Select a Skill to Master", "You have not taken the Skill Focus Feat.");
            choice.withOneOption("You have the following skill available for Mastery");
            choice.withOption("AVAILABLE_SKILL_MASTERY", new Option().withPayload("AVAILABLE_SKILL_MASTERY"));
            return choice;
        } else if (WEAPON_PROFICIENCY.equals(itemName)) {
            Choice choice = new Choice("Select a Weapon Proficiency");
            choice.withOption("AVAILABLE_WEAPON_PROFICIENCIES", new Option().withPayload("AVAILABLE_WEAPON_PROFICIENCIES"));
            return choice;
        } else if (WEAPON_FOCUS.equals(itemName)) {
            Choice choice = new Choice("Select a Weapon Focus", "You Must Be Proficient in a weapon group or an Exotic Weapon to select a Weapon Focus.");
            choice.withOption("AVAILABLE_WEAPON_FOCUS", new Option().withPayload("AVAILABLE_WEAPON_FOCUS"));
            return choice;
        } else if (EXOTIC_WEAPON_PROFICIENCY.equals(itemName)) {
            Choice choice = new Choice("Select a Weapon Proficiency");
            choice.withOption("AVAILABLE_EXOTIC_WEAPON_PROFICIENCY", new Option().withPayload("AVAILABLE_EXOTIC_WEAPON_PROFICIENCY"));
            return choice;
        } else if ("Double Attack".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use with Double Attack.", "You Must Be Focussed in a weapon group or an Exotic Weapon to select it for Double Attack.").withOneOption("You have a single weapon group or exotic weapon that qualifies for Double Attack");
            choice.withOption("AVAILABLE_DOUBLE_ATTACK", new Option().withPayload("AVAILABLE_DOUBLE_ATTACK"));
            return choice;
        } else if ("Triple Attack".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use with Triple Attack.", "You Must have Double Attack for a weapon group or an Exotic Weapon to select it for Triple Attack.").withOneOption("You have a single weapon group or exotic weapon that qualifies for Triple Attack");
            choice.withOption("AVAILABLE_TRIPLE_ATTACK", new Option().withPayload("AVAILABLE_TRIPLE_ATTACK"));
            return choice;
        } else if ("Savage Attack".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use with Savage Attack.", "You Must have Double Attack for a weapon group or an Exotic Weapon to select one for Savage Attack.");
            choice.withOption("AVAILABLE_SAVAGE_ATTACK", new Option().withPayload("AVAILABLE_SAVAGE_ATTACK"));
            return choice;
        } else if ("Relentless Attack".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use Relentless Attack with.", "You Must have Double Attack for a weapon group or an Exotic Weapon to select one for Relentless Attack.");
            choice.withOption("AVAILABLE_RELENTLESS_ATTACK", new Option().withPayload("AVAILABLE_RELENTLESS_ATTACK"));
            return choice;
        } else if ("Autofire Sweep".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use Autofire Sweep with.", "You Must Be Focussed in a weapon group or an Exotic Weapon to select one for Autofire Sweep.");
            choice.withOption("AVAILABLE_AUTOFIRE_SWEEP", new Option().withPayload("AVAILABLE_AUTOFIRE_SWEEP"));
            return choice;
        } else if ("Autofire Assault".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use Autofire Assault with.", "You Must Be Focussed in a weapon group or an Exotic Weapon to select one for Autofire Assault.");
            choice.withOption("AVAILABLE_AUTOFIRE_ASSAULT", new Option().withPayload("AVAILABLE_AUTOFIRE_ASSAULT"));
            return choice;
        } else if ("Halt".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use Halt with.", "You Must Be Focussed in a weapon group or an Exotic Weapon to select one for Halt.");
            choice.withOption("AVAILABLE_HALT", new Option().withPayload("AVAILABLE_HALT"));
            return choice;
        } else if ("Return Fire".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use Return Fire with.", "You Must Be Focussed in a weapon group or an Exotic Weapon to select one for Return Fire.");
            choice.withOption("AVAILABLE_RETURN_FIRE", new Option().withPayload("AVAILABLE_RETURN_FIRE"));
            return choice;
        } else if ("Critical Strike".equals(itemName)) {
            Choice choice = new Choice("Select a Weapon to use Critical Strike with.", "You Must Be Focussed in a weapon group or an Exotic Weapon to select one for Critical Strike.");
            choice.withOption("AVAILABLE_CRITICAL_STRIKE", new Option().withPayload("AVAILABLE_CRITICAL_STRIKE"));
            return choice;
        }

        return null;
    }

}
