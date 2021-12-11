package swse.character_class;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.BaseExporter;
import swse.common.Choice;
import swse.common.ItemType;
import swse.common.Option;
import swse.common.ProvidedItem;
import swse.prerequisite.Prerequisite;
import swse.util.Context;

public class CharacterClassExporter extends BaseExporter
{
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\classes.json";
    private static List<String> allClasses = new ArrayList<>();


    public static void main(String[] args)
    {
        List<String> classLinkList = new ArrayList<String>();
        classLinkList.add("/wiki/Jedi");
        classLinkList.add("/wiki/Noble");
        classLinkList.add("/wiki/Scoundrel");
        classLinkList.add("/wiki/Scout");
        classLinkList.add("/wiki/Soldier");
        classLinkList.add("/wiki/Technician");
        classLinkList.add("/wiki/Force_Prodigy");
        classLinkList.add("/wiki/Nonheroic");
        classLinkList.add("/wiki/Beast");
        //Prestige classes
        classLinkList.add("/wiki/Ace_Pilot");
        classLinkList.add("/wiki/Bounty_Hunter");
        classLinkList.add("/wiki/Crime_Lord");
        classLinkList.add("/wiki/Elite_Trooper");
        classLinkList.add("/wiki/Force_Adept");
        classLinkList.add("/wiki/Force_Disciple");
        classLinkList.add("/wiki/Gunslinger");
        classLinkList.add("/wiki/Jedi_Knight");
        classLinkList.add("/wiki/Jedi_Master");
        classLinkList.add("/wiki/Officer");
        classLinkList.add("/wiki/Sith_Apprentice");
        classLinkList.add("/wiki/Sith_Lord");
        classLinkList.add("/wiki/Corporate_Agent");
        classLinkList.add("/wiki/Gladiator");
        classLinkList.add("/wiki/Melee_Duelist");
        classLinkList.add("/wiki/Enforcer");
        classLinkList.add("/wiki/Independent_Droid");
        classLinkList.add("/wiki/Infiltrator");
        classLinkList.add("/wiki/Master_Privateer");
        classLinkList.add("/wiki/Medic");
        classLinkList.add("/wiki/Saboteur");
        classLinkList.add("/wiki/Assassin");
        classLinkList.add("/wiki/Charlatan");
        classLinkList.add("/wiki/Outlaw");
        classLinkList.add("/wiki/Droid_Commander");
        classLinkList.add("/wiki/Military_Engineer");
        classLinkList.add("/wiki/Vanguard");
        classLinkList.add("/wiki/Imperial_Knight");
        classLinkList.add("/wiki/Shaper");
        classLinkList.add("/wiki/Improviser");
        classLinkList.add("/wiki/Pathfinder");
        classLinkList.add("/wiki/Martial_Arts_Master");

        List<JSONObject> entries = new ArrayList<>();
        for (String speciesLink : classLinkList)
        {
            entries.addAll(parseItem(speciesLink, false));
        }

        //System.out.println(allClasses.stream().map(feat -> "\""+feat+"\"").collect(Collectors.toList()));
        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
        //writeToCSV(new File(OUTPUT_FILE), entries);
    }


    private static Collection<? extends JSONObject> parseItem(String itemLink, boolean overwrite)
    {
        if (null == itemLink)
        {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null)
        {
            return new ArrayList<>();
        }

        String itemName = getItemName(doc);

        if ("home".equals(itemName))
        {
            return new ArrayList<>();
        }

        allClasses.add(itemName);

        Element content = doc.getElementsByClass("mw-parser-output").first();

        content.select("img,figure").remove();
        content.select("span.mw-editsection").remove();
        content.select("div.toc").remove();
        content.select("table:not([class])").remove();

        Choice classChoice = getClassChoice(itemName);

        Context.setValue("name", itemName);
        JSONObject characterClass = CharacterClass.create(itemName)
                .withLeveledStats(Levels.getLeveledStats(content.select("table"), itemName))
                .withProvided(ClassSkill.getClassSkills(content.select("p,ul,h4")))
                .withProvided(HitPoints.getHitPoints(content.select("p,h4")))
                .withProvided(ForcePoints.getForcePoints(content.select("p,h4,h3")))
                .withProvided(DefenceBonuses.getDefenseBonuses(content.select("p,h4")))
                .withPrerequisite(Prerequisite.getClassPrerequisite(content.select("p,ul,h4")))
                .withProvided(StartingFeats.getStartingFeats(content.select("p,ul,h4")))
                .withProvided(classChoice)
                .withDescription(getDescription(content)).toJSON();

        return Lists.newArrayList(characterClass);
    }

    private static Choice getClassChoice(String itemName)
    {
        if("Technician".equals(itemName)){
            return new Choice("Select a Starting Feat:")
                    .isFirstLevel(true)
            .withOption("Skill Focus (Mechanics)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Mechanics))", ItemType.TRAIT)))
            .withOption("Skill Focus (Treat Injury)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Treat Injury))", ItemType.TRAIT)))
            .withOption("Skill Focus (Use Computer)", new Option().withProvidedItem(ProvidedItem.create("Conditional Bonus Feat (Skill Focus (Use Computer))", ItemType.TRAIT)));
        }else if("Jedi".equals(itemName)){

            return new Choice("Select a Starting Weapon:")
                    .isFirstLevel(true).withOneOption("On First level you receive:")
            .withOption("Lightsaber", new Option().withProvidedItem(ProvidedItem.create("Lightsaber", ItemType.ITEM)));
        }
        return null;
    }
}