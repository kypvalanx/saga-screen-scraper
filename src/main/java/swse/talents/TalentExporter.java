package swse.talents;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.Attribute;
import swse.common.BaseExporter;
import swse.common.Buff;
import swse.common.Category;
import swse.common.Choice;
import swse.common.ItemType;
import swse.common.Option;
import swse.common.ProvidedItem;
import swse.prerequisite.AndPrerequisite;
import swse.prerequisite.NotPrerequisite;
import swse.prerequisite.OrPrerequisite;
import swse.prerequisite.Prerequisite;
import swse.prerequisite.SimplePrerequisite;
import swse.util.Context;

public class TalentExporter extends BaseExporter
{

    public static List<String> DUPLICATE_TALENT_NAMES = List.of("Blend In",
    "Multiattack Proficiency (Advanced Melee Weapons)",
    "Get Into Position",
    "Commanding Presence",
    "Combined Fire",
    "Force Intuition",
    "Stay in the Fight",
    "Slip By",
    "Charm Beast",
        "Sith Alchemy",
        "Keep Them Reeling",
        "Ruthless",
        "Mobile Combatant",
        "Force Meld",
        "Out of Harm's Way",
        "Armor Mastery",
        "Seize the Moment",
        "Quick Strike",
        "Lead by Example",
        "Multiattack Proficiency (Rifles)",
        "Adept Spellcaster",
        "Command Beast",
        "Force Treatment",
        "Notorious",
        "Master Manipulator",
        "Ambush",
        "Keep it Together");
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\talents.csv";
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\talents.json";
    private static int num = 0;
    private static Set<Category> allCategories = new HashSet<Category>();
    private static List<String> allTalents = new ArrayList<>();

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        //talentLinks.add("/wiki/Talents");
        //talentLinks.add("/wiki/Force_Talents");
        //talentLinks.add("/wiki/Droid_Talents");
        talentLinks.add("/wiki/Category:Talent_Trees");

        List<JSONObject> entries = new ArrayList<>();
        for(String speciesLink : talentLinks){
            entries.addAll(readItemMenuPage(speciesLink, false));
        }

        //System.out.println(allTalents.stream().map(feat -> "\""+feat+"\"").collect(Collectors.toList()));

        System.out.println("Generated " + entries.size() + " of 1381");
        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"));
    }


    private static List<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite)
    {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null)
        {
            return new ArrayList<>();
        }
        Element body = doc.body();

        Elements as = body.select("a.category-page__member-link");

        Set<String> hrefs = new HashSet<>();
        as.forEach(a -> hrefs.add(a.attr("href")));

        return hrefs.stream().flatMap((Function<String, Stream<JSONObject>>) itemLink -> parseItem(itemLink, overwrite).stream())
                .collect(Collectors.toList());
    }

    private static List<JSONObject> parseItem(String itemLink, boolean overwrite)
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

        Element content = doc.getElementsByClass("page__main").first();

        Set<Category> categories = Category.getCategories(doc.getElementsByClass("page-footer").first());

        List<String> possibleProviders = categories.stream().map(item -> item.getValue().equals("Talent Trees") ? null : item.getValue()).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        allCategories.addAll( categories);

        String tradition = getTradition(itemName, content);

        List<JSONObject> talents = new ArrayList<>();
        String talentName = null;
        String talentDescription = null;
        Prerequisite prerequisite = null;
        Elements entries = content.select("p,h4");

        for(Element element: entries){
            if((element.tag().equals(Tag.valueOf("h4")) && element.getElementsByClass("mw-headline").size()>0))
            {
                if(talentName != null && !talentName.isEmpty()){

                    talents.add(Talent.create(talentName)
                            .withTalentTreeUrl("https://swse.fandom.com" + itemLink)
                            .withDescription(talentDescription)
                            .withPrerequisite(prerequisite)
                            .withTalentTree(itemName)
                            .withProvided(categories)
                            .withPossibleProviders(possibleProviders)
                            .withForceTradition(tradition)
                            .withProvided(getAttributes(talentName))
                            .withProvided(getChoices(talentName))
                            .toJSON());
                }

                talentName = element.getElementsByClass("mw-headline").first().text();
                if(DUPLICATE_TALENT_NAMES.contains(talentName)){
                    talentName = talentName + " ("+itemName+")";
                }
                allTalents.add(talentName);
                talentDescription = null;
                prerequisite = null;
            } else if(element.tag().equals(Tag.valueOf("p"))){
                if(element.text().toLowerCase().startsWith("prerequisite")){
                    //printUnique("---"+itemName);
                    Context.setValue("name", itemName);
                    prerequisite = Prerequisite.getPrerequisite(element, itemName);
                    //allPrerequisites.addAll(prerequisite.getAll());
                } else{
                    if(talentDescription == null){
                        talentDescription = element.text();
                    }else {
                        talentDescription = talentDescription.concat("<br><br>").concat(element.text());
                    }
                }
            }

        }

        if(talentName != null && !talentName.isEmpty()){
            talents.add(Talent.create(talentName)
                    .withTalentTreeUrl("https://swse.fandom.com" +itemLink)
                    .withDescription(talentDescription)
                    .withPrerequisite(prerequisite)
                    .withTalentTree(itemName)
                    .withProvided(categories)
                    .withPossibleProviders(possibleProviders)
                    .withForceTradition(tradition)
                    .withProvided(getAttributes(talentName))
                    .withProvided(getChoices(talentName))
                    .toJSON());
        }

        return talents;
    }

    private static List<Object> getChoices(String talentName) {
        List<Object> attributes = new ArrayList<>();

        if ("Weapon Specialization".equals(talentName)){
            Choice choice = new Choice("Select a Focussed Weapon to use Weapon Specialization with.", "You Must Be Focussed on a weapon group or an Exotic Weapon to select one for Weapon Specialization.").withOneOption("You have a single weapon group or exotic weapon that qualifies for Weapon Specialization");
            choice.withOption("AVAILABLE_WEAPON_SPECIALIZATION", new Option().withPayload("AVAILABLE_WEAPON_SPECIALIZATION"));
            attributes.add(choice);
        }
        if ("Greater Weapon Specialization".equals(talentName)){
            Choice choice = new Choice("Select a Weapon to use Greater Weapon Specialization with.", "You Must have Weapon Specialization with a weapon group or an Exotic Weapon to select one for Greater Weapon Specialization.").withOneOption("You have a single weapon group or exotic weapon that qualifies for Greater Weapon Specialization");
            choice.withOption("AVAILABLE_GREATER_WEAPON_SPECIALIZATION", new Option().withPayload("AVAILABLE_GREATER_WEAPON_SPECIALIZATION"));
            attributes.add(choice);
        }
        if ("Greater Weapon Focus".equals(talentName)){
            Choice choice = new Choice("Select a Weapon to use Greater Weapon Focus.", "You Must have Weapon Focus with a weapon group or an Exotic Weapon to select one for Greater Weapon Focus.").withOneOption("You have a single weapon group or exotic weapon that qualifies for Greater Weapon Focus");
            choice.withOption("AVAILABLE_GREATER_WEAPON_FOCUS", new Option().withPayload("AVAILABLE_GREATER_WEAPON_FOCUS"));
            attributes.add(choice);
        }

        return attributes;
    }

    private static List<Object> getAttributes(String itemName) {
        List<Object> attributes = new ArrayList<>();

        switch (itemName) {
            case "Noble Fencing Style":
                attributes.add(Attribute.create("finesseStat", "CHA"));
                break;
            case "Weapon Specialization":
                attributes.add(Attribute.create("takeMultipleTimes", true));
                attributes.add(Attribute.create("weaponSpecialization", "#payload#"));
                break;
            case "Greater Weapon Specialization":
                attributes.add(Attribute.create("takeMultipleTimes", true));
                attributes.add(Attribute.create("greaterWeaponSpecialization", "#payload#"));
                break;
            case "Greater Weapon Focus":
                attributes.add(Attribute.create("takeMultipleTimes", true));
                attributes.add(Attribute.create("greaterWeaponFocus", "#payload#"));
                break;
            case "Weapon Specialization (Discblade)":
                attributes.add(Attribute.create("weaponSpecialization", "Discblade"));
                break;
            case "Weapon Specialization (Lightsabers)":
                attributes.add(Attribute.create("weaponSpecialization", "Lightsabers"));
                break;
            case "Stolen Form":
                attributes.add(Attribute.create("takeMultipleTimes", true));
                attributes.add(ProvidedItem.create("#payload#", ItemType.TALENT));
                attributes.add(new Choice("Choose one Talent from the Lightsaber Forms Talent Tree:")
                        .withNoOptionsDescription("There are no available Talents remaining in the Lightsaber Forms Talent Tree")
                        .withOption("AVAILABLE_LIGHTSABER_FORMS", new Option().withPayload("AVAILABLE_LIGHTSABER_FORMS")));
                break;
            case "Lightsaber Defense":
                //when getinheritableby id sees a $ it should lookup the following inheritable value
                attributes.add(Buff.create("Lightsaber Defense").withProvided(Attribute.create("deflectionBonus", "$lightsaberDefense")));
                attributes.add(Attribute.create("lightsaberDefense", 1));
                attributes.add(Attribute.create("takeMultipleTimes", 3));
            case "Ataru":
                //damageStat will replace STR to damage, than any specific(group or item name) damage type.  we'll take the highest one.  this will be doubled for 2 handers
                attributes.add(Attribute.create("lightsabersDamageStat", "DEX"));
                break;
            case "Djem So":
                attributes.add(Attribute.create("action", "Once per round when an opponent hits you with a melee attack, you may spend a Force Point as a Reaction to make an immediate attack against that opponent."));
                break;
            case "Jar'Kai":
                attributes.add(ProvidedItem.create("Jar'Kai",ItemType.TRAIT, new OrPrerequisite(List.of(new SimplePrerequisite("Two Lightsabers", "EQUIPPED", "Lightsabers:>1"), new SimplePrerequisite("Double-Bladed Lightsaber", "EQUIPPED", "Double-Bladed Lightsaber")))));
                break;
            case "Juyo":
                attributes.add(Attribute.create("action", "Once per encounter, you may spend a Force Point as a Swift Action to designate a single opponent in your line of sight. For the remainder of the encounter, you may reroll your first attack roll each round against that opponent, keeping the better of the two results."));
                break;
            case "Makashi":
                attributes.add(ProvidedItem.create("Makashi",ItemType.TRAIT, new AndPrerequisite(List.of(new SimplePrerequisite("One Lightsaber", "EQUIPPED", "Lightsabers:<2"), new NotPrerequisite(new SimplePrerequisite("Two-Handed", "EQUIPPED", "2 Hand"))))));
                break;
            case "Niman":
                attributes.add(ProvidedItem.create("Niman",ItemType.TRAIT, new SimplePrerequisite("A Lightsaber", "EQUIPPED", "Lightsabers")));
                break;
            case "Shien":
                attributes.add(Attribute.create("redirectedShotBonus", 5));
                break;
            case "Shii-cho":
                attributes.add(Attribute.create("blockBonus", 3));
                attributes.add(Attribute.create("deflectBonus", 3));
                break;
            case "Sokan":
                attributes.add(Attribute.create("note", "You may Take 10 on Acrobatics checks to Tumble, even when distracted or threatened. Additionally, each threatened or occupied square that you Tumble through only counts as 1 square of movement."));
                break;
            case "Soresu":
                attributes.add(Attribute.create("note", "You may reroll a failed Use the Force check when using the Block or Deflect Talents."));
                break;
            case "Trakata":
                attributes.add(Attribute.create("note", "By harnessing the unique characteristics of a Lightsaber, you can catch your opponent off guard by quickly shutting off and reigniting the blade. When wielding a Lightsaber, you may spend two Swift Actions to make a Deception check to Feint in combat."));
                break;
            case "Vaapad":
                attributes.add(Attribute.create("note", "When attacking with a Lightsaber, you score a critical hit on a natural roll of 19 or 20. However, a natural 19 is not considered an automatic hit; if you roll a natural 19 and still miss the target, you do not score a critical hit."));
                break;

        }

        return attributes;
    }

    private static String getTradition(String treeName, Element content)
    {
        if(treeName.equals("Guardian Spirit Talent Tree")){
            return null;
        }
        if(treeName.equals("Sorcerers of Tund Talent Tree")){
            return "The Sorcerers of Tund";
        }
        if(treeName.equals("B'omarr Monk Talent Tree")){
            return "The B'omarr Order";
        }
        if(treeName.equals("Jensaarai Defender Talent Tree")){
            return "The Jensaarai";
        }
        if(treeName.equals("Dathomiri Witch Talent Tree")){
            return "The Witches of Dathomir";
        }
        if(treeName.equals("Imperial Inquisitor Talent Tree")){
            return "The Inquisitorius";
        }
        if(treeName.equals("Aing-Tii Monk Talent Tree")){
            return "The Aing-Tii Monks";
        }
        for (Element element :
                content.children())
        {
            Pattern p = Pattern.compile("of ([\\w\\s']*) Force Tradition");

            Matcher m = p.matcher(element.text());


            if (m.find()){

                    return m.group(1);
            }
        }

        //System.out.println(treeName);
//
//
//        if(content.text().toLowerCase().contains("tradition")){
//            System.out.println(1);
//        }
        return null;
    }

}
