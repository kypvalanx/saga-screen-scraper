package swse.feat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.BaseExporter;
import swse.common.Category;
import swse.common.Choice;
import swse.common.JSONy;
import swse.common.Option;
import swse.prerequisite.Prerequisite;
import swse.util.Context;

public class FeatExporter extends BaseExporter {
    public static final String JSON_OUTPUT = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems\\swse\\raw_export\\feats.json";
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\feats.csv";
    public static final String WEAPON_PROFICIENCY = "Weapon Proficiency";
    public static final String WEAPON_FOCUS = "Weapon Focus";
    public static final String SKILL_FOCUS = "Skill Focus";
    public static final String EXOTIC_WEAPON_PROFICIENCY = "Exotic Weapon Proficiency";
    public static final String IMAGE_FOLDER = "systems/swse/icon/feat";
    private static final String SKILL_MASTERY = "Skill Mastery";
    private static Set<String> allFeats = new HashSet<>();

    public static void main(String[] args) {

        List<String> featLinks = new ArrayList<>(getAlphaLinks("/wiki/Category:Feats?from="));


        List<JSONObject> entries = new FeatExporter().getEntriesFromCategoryPage(featLinks);


        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }


    protected Collection<JSONy> parseItem(String itemLink, boolean overwrite) {
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

        List<JSONy> feats = new ArrayList<>();

        feats.add(Feat.create(itemName)
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
                                provided.add(Attribute.create(AttributeKey.TAKE_MULTIPLE_TIMES, "true"));
                            }
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

    private static List<Attribute> getManualAttributes(String itemName) {
        List<Attribute> attributes = new ArrayList<>();
        if (itemName == null) {
            return attributes;
        }

        switch (itemName) {
            case "Improved Damage Threshold":
                attributes.add(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_BONUS, 5));
                break;
            case "Toughness":
                attributes.add(Attribute.create(AttributeKey.HIT_POINT_EQ, "@charLevel"));
                break;
            case "Weapon Proficiency":
            case "Exotic Weapon Proficiency":
                attributes.add(Attribute.create(AttributeKey.WEAPON_PROFICIENCY, "#payload#"));
                break;
            case "Armor Proficiency":
                attributes.add(Attribute.create(AttributeKey.ARMOR_PROFICIENCY, "#payload#"));
                break;
            case "Weapon Focus":
                attributes.add(Attribute.create(AttributeKey.WEAPON_FOCUS, "#payload#"));
                break;
            case "Skill Focus":
                attributes.add(Attribute.create(AttributeKey.SKILL_FOCUS, "#payload#"));
                break;
            case "Skill Mastery":
                attributes.add(Attribute.create(AttributeKey.SKILL_MASTERY, "#payload#"));
                break;
            case "Double Attack":
                attributes.add(Attribute.create(AttributeKey.DOUBLE_ATTACK, "#payload#"));
                break;
            case "Triple Attack":
                attributes.add(Attribute.create(AttributeKey.TRIPLE_ATTACK, "#payload#"));
                break;
            case "Savage Attack":
                attributes.add(Attribute.create(AttributeKey.SAVAGE_ATTACK, "#payload#"));
            case "Relentless Attack":
                attributes.add(Attribute.create(AttributeKey.RELENTLESS_ATTACK, "#payload#"));
                break;
            case "Autofire Sweep":
                attributes.add(Attribute.create(AttributeKey.AUTOFIRE_SWEEP, "#payload#"));
                break;
            case "Autofire Assault":
                attributes.add(Attribute.create(AttributeKey.AUTOFIRE_ASSAULT, "#payload#"));
                break;
            case "Halt":
                attributes.add(Attribute.create(AttributeKey.HALT, "#payload#"));
                break;
            case "Return Fire":
                attributes.add(Attribute.create(AttributeKey.RETURN_FIRE, "#payload#"));
                break;
            case "Critical Strike":
                attributes.add(Attribute.create(AttributeKey.CRITICAL_STRIKE, "#payload#"));
                break;
            case "Force Sensitivity":
                attributes.add(Attribute.create(AttributeKey.FORCE_SENSITIVITY, "true"));
                attributes.add(Attribute.create(AttributeKey.BONUS_TALENT_TREE, "Force Talent Trees"));
                break;
            case "Weapon Finesse":
                attributes.add(Attribute.create(AttributeKey.FINESSE_STAT, "DEX"));
                break;
            case "Force Training":
                attributes.add(Attribute.create(AttributeKey.FORCE_TRAINING, "true"));
                attributes.add(Attribute.create(AttributeKey.PROVIDES, "Force Powers:MAX(1 + @WISMOD,1)"));
                break;
            case "Dual Weapon Mastery I":
                attributes.add(Attribute.create(AttributeKey.DUAL_WEAPON_MODIFIER, "-5"));
                break;
            case "Dual Weapon Mastery II":
                attributes.add(Attribute.create(AttributeKey.DUAL_WEAPON_MODIFIER, "-2"));
                break;
            case "Dual Weapon Mastery III":
                attributes.add(Attribute.create(AttributeKey.DUAL_WEAPON_MODIFIER, "0"));
                break;
            case "Skill Training":
                attributes.add(Attribute.create(AttributeKey.TRAINED_SKILLS, "1"));
                break;
            case "Improved Defenses":
                attributes.add(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS, "1"));
                attributes.add(Attribute.create(AttributeKey.WILL_DEFENSE_BONUS, "1"));
                attributes.add(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS, "1"));
                break;
            case "Armor Proficiency (Light)":
                attributes.add(Attribute.create(AttributeKey.ARMOR_PROFICIENCY, "light"));
                break;
            case "Armor Proficiency (Medium)":
                attributes.add(Attribute.create(AttributeKey.ARMOR_PROFICIENCY, "medium"));
                break;
            case "Armor Proficiency (Heavy)":
                attributes.add(Attribute.create(AttributeKey.ARMOR_PROFICIENCY, "heavy"));
                break;
            default:
        }
        if (itemName.startsWith("Martial Arts ")) {
            attributes.add(Attribute.create(AttributeKey.BONUS_UNARMED_DAMAGE_DIE_SIZE, "1"));
            attributes.add(Attribute.create(AttributeKey.BONUS_DODGE_REFLEX_DEFENSE, "1"));
        }
        return attributes;
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

    private static String getImage(String itemType) {
        itemType = (itemType != null ? itemType : "untyped");

        if (itemType.contains(",")) {
            itemType = itemType.split(",")[0];
        }

        if (new File("G:/FoundryVTT/Data/" + IMAGE_FOLDER + "/" + itemType + "/default.png").exists()) {
            return IMAGE_FOLDER + "/" + itemType + "/default.png";
        } else {
            //System.out.println("could not find "+ IMAGE_FOLDER+"/" + itemType + "/default.png");
            new File("G:/FoundryVTT/Data/" + IMAGE_FOLDER + "/" + itemType).mkdir();
        }
        return IMAGE_FOLDER + "/default.png";
    }

}
