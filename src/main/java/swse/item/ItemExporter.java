package swse.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.Attribute;
import swse.common.BaseExporter;
import swse.util.Context;
import static swse.util.Util.getDieEquation;
import static swse.util.Util.getNumber;
import static swse.util.Util.getParensContent;

public class ItemExporter extends BaseExporter {
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\items.json";
    public static final String AMMUNITION_ATTRIBITE_KEY = "ammo";
    public static final String DAMAGE_DIE = "damageDie";
    public static final String AMMO_USE_MULTIPLIER = "ammoUseMultiplier";
    public static final String TO_HIT_MODIFIER = "toHitModifier";
    public static final Mode MODE_AUTOFIRE = Mode.create("Autofire", "ROF", List.of(Attribute.create(TO_HIT_MODIFIER, "-5")));
    private static final String CONCEALMENT_BONUS = "concealmentBonus";
    private static final String PENETRATION = "penetration";
    private static final String STUN_DAMAGE = "stunDamageDie";
    public static final Mode MODE_SINGLE_SHOT = Mode.create("Single-Shot", "ROF", List.of());
    public static final Mode MODE_BARRAGE = Mode.create("Barrage", "ROF", List.of());
    public static final String TREATED_AS_ATTRIBUTE_KEY = "treatedAs";
    private static Multimap<String, String> fields = HashMultimap.create();
    public static final String IMAGE_FOLDER = "systems/swse/icon/item";
    public static final String ROOT = "G:/FoundryVTT/Data";
    private static Set<String> possibleAttributes = new HashSet<>();
    public static final Pattern ATTACK_PATTERN = Pattern.compile("\\+(\\d*) vs ([()\\w\\s]*)");
    public static final Pattern DAMAGE_PATTERN = Pattern.compile("^([/\\dd-]*) ?([-+()/\\w\\s;,]*)");
    public static final Pattern SPECIAL_PATTERN = Pattern.compile("^([\\w\\s+/]*)([\\w\\s()]*)?");


    public static void main(String[] args) {
        List<String> itemlinks = new ArrayList<String>();
        //weapons
        itemlinks.add("/wiki/Advanced_Melee_Weapons");
        itemlinks.add("/wiki/Exotic_Weapons_(Melee)");
        itemlinks.add("/wiki/Exotic_Weapons_(Ranged)");
        itemlinks.add("/wiki/Grenades");
        itemlinks.add("/wiki/Heavy_Weapons");
        itemlinks.add("/wiki/Lightsabers");
        itemlinks.add("/wiki/Mines");
        itemlinks.add("/wiki/Pistols");
        itemlinks.add("/wiki/Rifles");
        itemlinks.add("/wiki/Simple_Weapons_(Melee)");
        itemlinks.add("/wiki/Simple_Weapons_(Ranged)");
        //armor
        itemlinks.add("/wiki/Light_Armor");
        itemlinks.add("/wiki/Medium_Armor");
        itemlinks.add("/wiki/Heavy_Armor");
//        //general equipment and additional general equipment
        itemlinks.add("/wiki/Communications_Devices");
        itemlinks.add("/wiki/Computers_and_Storage_Devices");
        itemlinks.add("/wiki/Cybernetic_Devices");
        itemlinks.add("/wiki/Detection_and_Surveillance_Devices");
        itemlinks.add("/wiki/Explosives");
        itemlinks.add("/wiki/Life_Support");
        itemlinks.add("/wiki/Medical_Gear");
        itemlinks.add("/wiki/Poisons");
        itemlinks.add("/wiki/CL_1_Hazards");
        itemlinks.add("/wiki/CL_2_Hazards");
        itemlinks.add("/wiki/CL_3_Hazards");
        itemlinks.add("/wiki/CL_4_Hazards");
        itemlinks.add("/wiki/CL_5_Hazards");
        itemlinks.add("/wiki/CL_6_Hazards");
        itemlinks.add("/wiki/CL_7_Hazards");
        itemlinks.add("/wiki/CL_8_Hazards");
        itemlinks.add("/wiki/CL_9_Hazards");
        itemlinks.add("/wiki/CL_10_Hazards");
        itemlinks.add("/wiki/CL_11_Hazards");
        itemlinks.add("/wiki/CL_12_Hazards");
        itemlinks.add("/wiki/CL_13_Hazards");
        itemlinks.add("/wiki/CL_14_Hazards");
        itemlinks.add("/wiki/CL_15_Hazards");
        itemlinks.add("/wiki/CL_20_Hazards");
        itemlinks.add("/wiki/Survival_Gear");
        itemlinks.add("/wiki/Tools");
        itemlinks.add("/wiki/Weapon_and_Armor_Accessories");
        itemlinks.add("/wiki/Equipment_Upgrades");

        itemlinks.add("/wiki/Advanced_Cybernetics");
        itemlinks.add("/wiki/Heirloom_Items");
        itemlinks.add("/wiki/Holocrons");
        itemlinks.add("/wiki/Implants");
        itemlinks.add("/wiki/Sith_Artifacts");
        itemlinks.add("/wiki/Yuuzhan_Vong_Biotech");
//        //droid stuff
        itemlinks.add("/wiki/Locomotion_Systems");
        itemlinks.add("/wiki/Processor_Systems");
        itemlinks.add("/wiki/Appendages");
        itemlinks.add("/wiki/Droid_Accessories");

        List<JSONObject> entries = new ArrayList<>();
        double size = itemlinks.size();
        AtomicInteger i = new AtomicInteger();
        for (String itemLink : itemlinks) {
            entries.addAll(readItemMenuPage(itemLink, true));
            drawProgressBar(i.getAndIncrement() * 100 / size);
        }

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"));
    }


    private static Collection<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);

        Element body = doc.body();

        Elements tables = body.getElementsByClass("wikitable");

        Set<String> hrefs = new HashSet<>();
        tables.forEach(table ->
        {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row ->
            {
                Element first = row.getElementsByTag("td").first();
                if (first != null) {
                    Element anchor = first.getElementsByTag("a").first();
                    if (anchor != null) {
                        String href = anchor.attr("href");
                        hrefs.add(href);
                        //items.addAll(parseItem(href));
                    }
                }
            });
        });


        return hrefs.stream().flatMap((Function<String, Stream<JSONObject>>) itemLink -> parseItem2(itemLink, overwrite).stream()).collect(Collectors.toList());
    }

    private static List<JSONObject> parseItem2(String itemLink, boolean overwrite) {
        if (null == itemLink) {
            return new ArrayList<>();
        }
        Document doc = getDoc(itemLink, overwrite);
        String itemName = "";
        try {
            itemName = getItemName(doc);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        if ("home".equals(itemName.toLowerCase())) {
            return new ArrayList<>();
        }

        itemName = filterName(itemName);

        Context.setValue("name", itemName);

        Element content = doc.getElementsByClass("mw-parser-output").first();

        content.select("span.mw-editsection").remove();
        content.select("div.toc").remove();
        content.select("img,figure").remove();
        removeComments(content);

        String itemSubType = null;
        String damageType = null;
        String size = null;
        String cost = null;
        String weight = null;
        String availability = null;
        String book = null;
        String damage = null;
        String stunSetting = null;
        String rateOfFire = null;
        String bonusToReflexDefense = null;
        String bonusToFortitudeDefense = null;
        String maximumDexterityBonus = null;
        String splash = null;
        String baseItem = null;
        String heirloomBonus = null;
        String seeAlso = null;
        String baseSpeed = null;
        String requires = null;
        List<String> keywords = null;
        String trigger = null;
        String attack = null;
        String recurrence = null;
        List<String> skillChecks = new ArrayList<>();
        String rejectionAttackBonus = null;
        String installationCost = null;
        String upgradePointCost = null;
        String challengeLevel = null;
        List<String> special = new ArrayList<>();

        Integer unarmedDamage = null;
        String unarmedModifier = null;
        List<Attribute> complexAttributes = new ArrayList<>();
        List<Mode> complexModes = new ArrayList<>();
        String inaccurate;
        String accurate;

        for (Element child : content.children()) {
            if (child.text().contains(":")) {
                final String[] split = child.text().split(":");
                final String label = split[0];

                if (split.length == 1) {
                    continue;
                }

                final String payload = split[1].trim();
                boolean wasDefault = false;
                if (label.length() <= 42) {
                    switch (label) {
                        case "Size":
                            size = payload;
                            break;
                        case "Cost":
                            cost = payload;
                            break;
                        case "Weight":
                            weight = payload;
                            break;
                        case "Rejection Attack Bonus":
                            rejectionAttackBonus = payload;
                            break;
                        case "Installation Cost":
                            installationCost = payload;
                            break;
                        case "Avability":
                        case "Availability":
                            availability = payload;
                            break;
                        case "Damage":
                            damage = payload;
                            break;
                        case "Stun Damage":
                        case "Stun Setting":
                            stunSetting = payload;
                            break;
                        case "Rate of Fire":
                            //printUnique(payload);
                            rateOfFire = standardizeRateOfFire(payload);
                            break;
                        case "Bonus to Reflex Defense":
                            bonusToReflexDefense = payload;
                            break;
                        case "Bonus to Fortitude Defense":
                            bonusToFortitudeDefense = payload;
                            break;
                        case "Maximum Dexterity Bonus":
                            maximumDexterityBonus = payload;
                            break;
                        case "Splash":
                            splash = payload;
                            break;
                        case "Base Item":
                            baseItem = payload;
                            break;
                        case "Base Speed":
                            baseSpeed = payload;
                            break;
                        case "Requires":
                            requires = payload;
                            break;
                        case "Upgrade Point Cost":
                            upgradePointCost = payload;
                            break;
                        case "Challenge Level":
                            challengeLevel = payload;
                            break;
                        case "Keywords":
                            keywords = List.of(payload.split(", "));
                            itemSubType = "Hazard";
                            break;
                        case "Trigger":
                            trigger = payload;
                            break;
                        case "Attack":
                        case "Attacks":
                            attack = payload;
                            break;
                        case "Recurrence":
                            recurrence = payload;
                            break;
                        case "Perception DC":
                        case "Skill Check":
                        case "Use the Force (DC 25)":
                        case "Treat Injury (DC 15; Requires Medical Kit)":
                        case "Treat Injury (DC 20; Requires Medical Kit)":
                        case "Treat Injury (DC 23; Requires Medical Kit)":
                        case "Treat Injury (DC 25; Requires Medical Kit)":
                        case "Treat Injury (DC 16; Requires Medical Kit)":
                        case "Endurance (DC 10)":
                        case "Treat Injury (DC 28; Requires Medical Kit)":
                        case "Treat Injury (DC 30; Requires Medical Kit)":
                        case "Perception (DC 10)":
                        case "Treat Injury (DC 20)":
                        case "Jump (DC 13)":
                        case "Acrobatics (DC 18)":
                        case "Acrobatics (DC 13)":
                        case "Jump (DC 14)":
                        case "Mechanics (DC 14)":
                        case "Mechanics (DC 16)":
                        case "Knowledge (Life Sciences) (DC 14)":
                        case "Knowledge (Social Sciences) (DC 24)":
                        case "Stealth":
                        case "Treat Injury (DC 14; Requires Medical Kit)":
                        case "Mechanics (DC 24)":
                        case "Jump (DC 19)":
                        case "Treat Injury (DC 19)":
                        case "Perception (DC 14)":
                        case "Knowledge (Life Sciences) (DC 19)":
                        case "Endurance (DC 19)":
                        case "Knowledge (Physical Sciences) (DC 14)":
                        case "Pilot (DC 14)":
                        case "Swim (DC 14)":
                        case "Endurance (DC 24)":
                        case "Acrobatics (DC 26)":
                        case "Swim (DC 16)":
                        case "Endurance (DC 26)":
                        case "Knowledge (Life Sciences) (DC 31)":
                        case "Knowledge (Physical Sciences) (DC 21)":
                        case "Survival (DC 16)":
                        case "Survival (DC 21)":
                        case "Knowledge (Physical Sciences) (DC 16)":
                        case "Acrobatics (DC 21)":
                        case "Use Computer (DC 16)":
                        case "Knowledge (Life Sciences) (DC 21)":
                        case "Climb (DC 16)":
                        case "Acrobatics (DC 22)":
                        case "Jump (DC 27)":
                        case "Acrobatics (DC 27)":
                        case "Treat Injury (DC 35; Requires Medical Kit)":
                        case "Knowledge (Physical Sciences) (DC 17)":
                        case "Endurance (DC 27)":
                        case "Treat Injury (DC 40; Requires Medical Kit)":
                        case "Treat Injury (DC 25)":
                        case "Endurance (DC 22)":
                        case "Perception (DC 17)":
                        case "Acrobatics (DC 23)":
                        case "Knowledge (Life Sciences) (DC 18)":
                        case "Acrobatics (DC 28)":
                        case "Perception (DC 23)":
                        case "Pilot (DC 16)":
                        case "Pilot (DC 18)":
                        case "Perception (DC 22)":
                        case "Knowledge (Physical Sciences) (DC 23)":
                        case "Pilot (DC 23)":
                        case "Jump (DC 23)":
                        case "Perception (DC 19)":
                        case "Knowledge (Physical Sciences) (DC 18)":
                        case "Treat Injury (DC 25; Requires Reeksa Toot)":
                        case "Acrobatics (DC 24)":
                        case "Perception (DC 31)":
                        case "Climb (DC 27)":
                        case "Pilot (DC 24)":
                        case "Pilot (DC 32)":
                        case "Mechanics (DC 36)":
                            skillChecks.add(child.text());
                            break;
                        case "Mandalorian Armor Template":
                        case "Extra Modification (Protective Armor)":
                        case "Extra Modification (Improved Accuracy)":
                            special.add(child.text());
                            break;
                        case "See Also":
                        case "See also":
                            seeAlso = payload;
                            break;
                        case "Heirloom Bonus":
                            heirloomBonus = payload;
                            break;
                        case "Reference Book":
                        case "Homebrew Reference Book":
                            book = payload;
                            break;
                        case "Droid Armor Type":
                        case "Droid System Type":
                        case "Upgrade Type":
                        case "Equipment Type":
                        case "Weapon Type":
                            itemSubType = payload;
                            break;
                        case "Inaccurate":
                            inaccurate = payload;
                            break;
                        case "Accurate":
                            accurate = payload;
                            break;
                        case "Steeped in the Dark Side":
                        case "Compressed Crystal":
                        case "Legendary Icon":
                        case "Symbol of the Light":
                        case "Lambent Crystal":
                        case "Arc Weapon":
                        case "Required Items":
                        case "Creation Time":
                        case "Result":
                        case "Special":
                            special.add(child.text());
                            break;
                        case "Type":
                            if (payload.contains("Armor")) {
                                itemSubType = payload;
                            } else {
                                damageType = payload;
                            }
                            break;
                        case "Suggested Skills":
                        case "Clone Assassin":
                        case "Clone Pilot":
                        case "Clone Scout Trooper":
                        case "Clone Subtrooper":
                        case "Cold Assault Clone Trooper":
                        case "Combat Engineer Trooper":
                        case "Covert-Ops Trooper":
                        case "High-Orbit Precision Entry Trooper":
                        case "Senate Commando":
                        case "Sky Troopers":
                        case "Concealed Holster":
                        case "Hip Holster":
                        case "Standard":
                        case "Advanced":
                        case "Enhanced low-light":
                        case "Encryption":
                        case "Video Capability":
                        case "Holo Capability":
                        case "Audiorecorder":
                        case "Videorecorder":
                        case "Holorecorder":
                        case "There are two ways to deploy the HX2 Mine":
                        case "Energy Shields come in three varieties":
                        case "Phase I ARF Trooper":
                        case "1":
                        case "ADSD Pack":
                        case "Comlink":
                        case "An Electrostaff is a double weapon":
                        case "Example":
                            break;
                        default:
                            //printUnique(Context.getValue("name"), label);
                    }
                }
            }
            else{
                Map<List<Attribute>, List<Mode>> map = parseComplexWeapon(child.text(), itemName);
                complexAttributes.addAll((List<Attribute>) map.keySet().toArray()[0]);
                complexModes.addAll((List<Mode>) map.values().toArray()[0]);
            }
        }
        Map<List<Attribute>, List<Mode>> map = getCustom( itemName);
        complexAttributes.addAll((List<Attribute>) map.keySet().toArray()[0]);
        complexModes.addAll((List<Mode>) map.values().toArray()[0]);

        itemSubType = standardizeTypes(itemSubType);
        String itemType = getFoundryType(itemSubType);
        List<Mode> modes = getModes(rateOfFire, itemName);
        modes.addAll(complexModes);
        String damageDie = getDamageDie(itemName, damage);

        if(damageDie == null && damage != null){
            if(damage.contains(" to Unarmed attacks")){
                unarmedDamage = getNumber(damage);
                unarmedModifier = getParensContent(damage);
                //printUnique(Context.getValue("name"),unarmedModifier);
            }else {

            }
        }

        String stunDamageDie = getDieEquation(stunSetting, itemName);

        if(stunSetting != null && stunSetting.toLowerCase().contains("yes") && stunDamageDie == null){
            stunDamageDie = damageDie;
        }

        if("Amphistaff".equalsIgnoreCase(itemName)){
            damageDie = null;
            damageType = null;
            special.clear();
            special.add("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                    "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                    "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                    "An Amphistaff can only spit venom once every 24 standard hours.");
            special.add("The Amphistaff has three distinct weapon forms: Quarterstaff, Spear, " +
                    "and Whip. Switching from one Weapon Form to is a Swift Action.");
        }
        if("DC-17m IWS".equalsIgnoreCase(itemName)){
            damageDie = null;
            stunDamageDie = null;
            special = null;
        }

        //SubType subtype = SubType.create(damageType);



//        List<Attack> attacks = resolveAttacks(damage, stunSetting, attack, rateOfFire, damageType);

        final List<JSONObject> jsonObjects = new ArrayList<>();
        Item item = Item.create(itemName)
                .withDescription(content.html())
                .withType(itemType)
                .withSubtype(itemSubType)
                .withModes(modes)
                .withProvided(complexAttributes)
                .withDamageDie(damageDie)
                .withStunDamageDie(stunDamageDie)
                .withDamageType(damageType)
                .withCost(cost)
                .withSize(size)
                .withWeight(weight)
                .withSource(book)
                .withAvailability(availability)
                .withBaseItem(baseItem)
                .withBonusToReflexDefense(bonusToReflexDefense)
                .withBonusToFortitudeDefense(bonusToFortitudeDefense)
                .withMaxDexterityBonus(maximumDexterityBonus)
                .withSplash(splash)
                .withHeirloomBonus(heirloomBonus)
                .withSeeAlso(seeAlso)
                .withBaseSpeed(baseSpeed)
                .withRequires(requires)
                .withTrigger(trigger)
                .withRecurrence(recurrence)
                .withSkillChecks(skillChecks)
                .withRejectionAttackBonus(rejectionAttackBonus)
                .withInstallationCost(installationCost)
                .withUpgradePointCost(upgradePointCost)
                .withChallengeLevel(challengeLevel)
                .withSpecial(special)
                .withKeywords(keywords)
                .withUnarmedDamage(unarmedDamage)
                .withUnarmedModifier(unarmedModifier)
                .withPrefix(getPrefix(itemName))
                .withSuffix(getSuffix(itemName));

        jsonObjects.add(item.toJSON());

        return jsonObjects;
    }

    private static Map<List<Attribute>, List<Mode>> parseComplexWeapon(String text, String itemName) {
        List<Attribute> attributes = new ArrayList<>();
        List<Mode> modes = new ArrayList<>();
        Map<List<Attribute>, List<Mode>> map = new HashMap<>();
        map.put(attributes, modes);

        if(List.of("Energy Lance", "E-5s Blaster Rifle", "SG-4 Blaster Rifle", "HB-9 Blaster Rifle",
                "Variable Blaster Rifle", "Sonic Blaster", "Commando Special Rifle", "Heavy Variable Blaster Rifle",
                "Thunderbolt Repeater Blaster", "Vibrobayonet", "Stun Bayonet", "Neuronic Whip", "Blastsword",
                "Z-6 Rotary Blaster", "Archaic Lightsaber", "Heavy Blaster Pistol", "Snap-Shot Blaster Pistol",
                "Sidearm Blaster Pistol", "Gee-Tech 12 Defender", "Retrosaber", "Jury-Rigging a Power Pack Bomb",
                "Stun Baton", "Squib Battering Ram", "Utility Belt", "Gas Canister", "Power Recharger", "Power Pack",
                "Enhanced Energy Projector", "Improved Energy Cell", "Tremor Cell", "Slugthrower Pistol", "Slugthrower Rifle").contains(itemName)){
            return map;
        }

        if(text.contains("treated")){
            Pattern TREATED_AS_FOR_RANGE = Pattern.compile("treated as(?: a)? (Rifle|Rifles|Pistol|Simple Weapon \\(Ranged\\)|Simple Weapons \\(Ranged\\))(?:, not a Thrown Weapon,)? for");
            Matcher m = TREATED_AS_FOR_RANGE.matcher(text);
            if(m.find()) {
                attributes.add(Attribute.create(TREATED_AS_ATTRIBUTE_KEY, standardizeTypes(m.group(1))));
            }
        }


        if(text.contains("Power Pack")) {
            Pattern SHOTS_PER_PACK = Pattern.compile("After (\\d*|one) \\w*, the ([\\w\\s]*) must be \\w*\\.");
            Matcher m = SHOTS_PER_PACK.matcher(text);
            while (m.find()) {
                String group = m.group(2);
                if(group.equals("one")){
                    group = "1";
                }
                attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, group +":"+m.group(1)));
                //printUnique(m.group(0));
            }
            if(attributes.isEmpty() && modes.isEmpty()){
                //printUnique(itemName,text);
            }
        }

        return map;
    }
    private static Map<List<Attribute>, List<Mode>> getCustom(String itemName) {
        List<Attribute> attributes = new ArrayList<>();
        List<Mode> modes = new ArrayList<>();

        Map<List<Attribute>, List<Mode>> map = new HashMap<>();
        map.put(attributes, modes);

        if("Energy Lance".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:50"));
        }
        if("E-5s Blaster Rifle".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:5"));
        }
        if("SG-4 Blaster Rifle".equals(itemName)){
            modes.add(Mode.create("Blaster", "POWER", List.of(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:50"))));
            modes.add(Mode.create("Harpoon", "POWER", List.of(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Harpoon:1"))));
        }
        if("HB-9 Blaster Rifle".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:25"));
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Gas Canister:200"));
        }
        if("Commando Special Rifle".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:25"));
        }
        if("Variable Blaster Rifle".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:500"));
            modes.add(Mode.create("3d4", "POWER", List.of(Attribute.create(DAMAGE_DIE,"3d4"))));
            modes.add(Mode.create("3d6", "POWER", List.of(Attribute.create(DAMAGE_DIE,"3d6"), Attribute.create(AMMO_USE_MULTIPLIER,"5"))));
            modes.add(Mode.create("3d8", "POWER", List.of(Attribute.create(DAMAGE_DIE,"3d8"), Attribute.create(AMMO_USE_MULTIPLIER,"10"))));
        }
        if("Heavy Variable Blaster Rifle".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:500"));
            modes.add(Mode.create("Ascension gun", "POWER", List.of(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Syntherope:2"))));
            modes.add(Mode.create("3d6", "POWER", List.of(Attribute.create(DAMAGE_DIE,"3d6"))));
            modes.add(Mode.create("3d8", "POWER", List.of(Attribute.create(DAMAGE_DIE,"3d8"), Attribute.create(AMMO_USE_MULTIPLIER,"10"))));
            modes.add(Mode.create("3d10", "POWER", List.of(Attribute.create(DAMAGE_DIE,"3d10"), Attribute.create(AMMO_USE_MULTIPLIER,"20"))));
        }
        if("Sonic Blaster".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Proprietary Power Pack:15:11:0.2"));
        }
        if("Heavy Blaster Pistol".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:50"));
        }
        if("Snap-Shot Blaster Pistol".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:1"));
            attributes.add(Attribute.create(CONCEALMENT_BONUS,"5"));
        }
        if("Sidearm Blaster Pistol".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:250"));
        }
        if("Gee-Tech 12 Defender".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY,"Power Pack:2"));
            attributes.add(Attribute.create(CONCEALMENT_BONUS,"5"));
        }

        if("Thunderbolt Repeater Blaster".equals(itemName)){
            attributes.add(Attribute.create(TO_HIT_MODIFIER, "-5"));
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:20"));
            modes.add(Mode.create("Braced", List.of(Attribute.create(TO_HIT_MODIFIER, "0"))));
        }
        if("Z-6 Rotary Blaster".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:1"));
            attributes.add(Attribute.create(TO_HIT_MODIFIER, "-5"));
            modes.add(Mode.create("Braced", List.of(Attribute.create(TO_HIT_MODIFIER, "0"))));
        }
        if("Retrosaber".equals(itemName)){
            modes.add(Mode.create("Overcharge", "POWER", List.of(Attribute.create(DAMAGE_DIE, "2d10"))));
            modes.add(Mode.create("Burnout", "POWER", List.of(Attribute.create(DAMAGE_DIE, "2d4"))));
        }
        if("Slugthrower Pistol".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Slug Clip:10:40:0.2"));
        }
        if("Slugthrower Rifle".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Slug Clip:20:40:0.2"));
        }
        if("WESTAR-M5 Blaster Rifle".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:100"));
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Gas Canister:500"));
            modes.add(Mode.create("Anti-Personnel", "POWER", List.of(Attribute.create(DAMAGE_DIE, "3d8"))));
            modes.add(Mode.create("Anti-Vehicle", "POWER", List.of(Attribute.create(DAMAGE_DIE, "3d10"),
                    Attribute.create(AMMO_USE_MULTIPLIER, "10"), Attribute.create(PENETRATION, "5"))));
        }
        if("DC-19 \"Stealth\" Carbine".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:10"));
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Stealth Mixture Gas Canister:500:500:0.25"));
        }
        if("Amban Phase-Pulse Blaster".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:1"));
        }
        if("Scatter Gun".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "10 Shells:10:20:1"));
            modes.add(Mode.create("Point-Blank Range", "POWER", List.of(Attribute.create(DAMAGE_DIE, "3d8"))));
            modes.add(Mode.create("Short Range", "POWER", List.of(Attribute.create(DAMAGE_DIE, "2d8"))));
            modes.add(Mode.create("Medium Range", "POWER", List.of(Attribute.create(DAMAGE_DIE, "0"))));
            modes.add(Mode.create("Long Range", "POWER", List.of(Attribute.create(DAMAGE_DIE, "0"))));
        }
        if("DC-15x Sniper Rifle".equals(itemName)){
            attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:5"));
        }
        if("DC-17m IWS".equals(itemName)){
            modes.add(Mode.create("Blaster Rifle", "POWER", List.of(
                    Attribute.create(DAMAGE_DIE, "3d8"),
                    Attribute.create(STUN_DAMAGE, "3d8"),
                    Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Power Pack:60"),
                    Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Gas Canister:300")
            ), List.of(MODE_SINGLE_SHOT, MODE_AUTOFIRE)));
            modes.add(Mode.create("Sniper Rifle", "POWER", List.of(
                    Attribute.create(DAMAGE_DIE, "3d8"),
                    Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Sniper Power Pack:5:100:0.5")
            ), List.of(MODE_SINGLE_SHOT)));
            modes.add(Mode.create("Anti-Armor", "POWER", List.of(
                    Attribute.create(DAMAGE_DIE, "4d10"),
                    Attribute.create(AMMUNITION_ATTRIBITE_KEY, "Explosive Shell:1:300:1")

            ), List.of(MODE_SINGLE_SHOT)));
            modes.add(Mode.create("PEP Laser", "POWER", List.of(
                    Attribute.create(STUN_DAMAGE, "3d6"),
                    Attribute.create(AMMUNITION_ATTRIBITE_KEY, "PEP Cartridge:15:100:0.5")
            ), List.of(MODE_SINGLE_SHOT)));
        }

        return map;
    }

    private static String getDamageDie(String itemName, String damage) {
        return "Amphistaff".equalsIgnoreCase(itemName) ? null : getDieEquation(damage, itemName);
    }

    private static String getPrefix(String itemName){
        return null;
    }

    private static String getSuffix(String itemName){
        if("Bayonet Ring".equals(itemName)){
            return "Bayonet";
        }
        return null;
    }

    private static List<Mode> getModes(String rateOfFire, String itemName) {
        List<Mode> modes = new ArrayList<>();
        if (rateOfFire != null && rateOfFire.contains("Autofire")) {
            modes.add(MODE_AUTOFIRE);
        }
        if (rateOfFire != null && rateOfFire.contains("Barrage")) {
            modes.add(MODE_BARRAGE);
        }

        if (rateOfFire != null && rateOfFire.contains("Single-Shot")) {
            modes.add(MODE_SINGLE_SHOT);
        }

        if("Amphistaff".equalsIgnoreCase(itemName)){
            modes.add(Mode.create("Quarterstaff","AMPHISTAFF_FORM", List.of(
                    Attribute.create(DAMAGE_DIE, "1d6/1d6"),
                    Attribute.create("damageType", "Bludgeoning"),
                    Attribute.create("stunSetting", "NO"),
                    Attribute.create("special", List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                            "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                            "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                            "An Amphistaff can only spit venom once every 24 standard hours."
                    ))
            )));
            modes.add(Mode.create("Spear","AMPHISTAFF_FORM", List.of(
                    Attribute.create(DAMAGE_DIE, "1d8"),
                    Attribute.create("damageType", "Piercing"),
                    Attribute.create("stunSetting", "NO"),
                    Attribute.create("special", List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                            "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                            "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                            "An Amphistaff can only spit venom once every 24 standard hours."
                    )
            ))));
            modes.add(Mode.create("Whip","AMPHISTAFF_FORM", List.of(
                    Attribute.create(DAMAGE_DIE, "1d4"),
                    Attribute.create("damageType", "Piercing"),
                    Attribute.create("stunSetting", "NO"),
                    Attribute.create("reach", "2"),
                    Attribute.create("providedAction", "Pin"),
                    Attribute.create("providedAction", "Trip"),
                    Attribute.create("special", List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                            "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                            "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                            "An Amphistaff can only spit venom once every 24 standard hours.",
                            "Is a Reach Weapon"
                    )

            ))));
        }
        if("DC-17m IWS".equalsIgnoreCase(itemName)){
            return new ArrayList<>();
        }

        return modes;
    }

    private static String standardizeRateOfFire(String payload) {
        if (payload == null) {
            return null;
        }

        if ("S".equals(payload) || "Single-Fire".equals(payload)) {
            return "Single-Shot";
        }
        if ("Autofire only".equals(payload)) {
            return "Autofire Only";
        }


        return payload;
    }

    private static String filterName(String itemName) {
        if ("Lightsaber (Weapon)".equals(itemName)) {
            return "Lightsaber";
        }
        return itemName;
    }


    private static String getFoundryType(String subType) {
        if (List.of("advanced melee weapons", "exotic melee weapons", "simple melee weapons", "simple ranged weapons",
                "exotic ranged weapons", "pistols", "rifles", "lightsabers", "heavy weapons", "grenades", "mines", "explosives").contains(subType.toLowerCase())) {
            return "Weapon";
        } else if (List.of("light armor","medium armor","heavy armor", "droid accessories (droid armor)").contains(subType.toLowerCase())) {
            return "Armor";
        } else if (List.of("weapons upgrade", "armor upgrade").contains(subType.toLowerCase())) {
            return "Upgrade";
        }

        return "Equipment";
    }


    private static String standardizeTypes(String trim) {
        if (trim == null) {
            // printUnique(Context.getValue("name"));
            return "Equipment";
        }
        trim = trim.trim();
        if ("implant".equalsIgnoreCase(trim)) {
            return "Implants";
        }
        return trim.replace("Weapon ", "Weapons ")
                .replace("Weapons (Ranged)", "Ranged Weapons")
                .replace("Weapons (Melee)", "Melee Weapons").trim();
    }


    private static String getImage(String name, String itemType) {
        Document doc = null;
        String url = "https://starwars.fandom.com/wiki/" + name + "/Legends";
        String filename = null;
        try {
            doc = Jsoup.connect(url).get();

            Element body = doc.body();

            Elements images = body.select(".pi-image-thumbnail");
            if (images.size() > 0) {
                Element image = images.first();


                String src = image.attr("src");
                String alt = image.attr("data-image-key");

                filename = IMAGE_FOLDER + "/" + alt;

                URL imageUrl = new URL(src);
                BufferedImage bufferedImage = ImageIO.read(imageUrl);
                File file = new File(ROOT + "/" + filename);
                ImageIO.write(bufferedImage, "png", file);
            }

        } catch (IOException e) {
            //System.err.println(name);
        }


        itemType = (itemType != null ? itemType : "untyped");

        if (itemType.contains(",")) {
            itemType = itemType.split(",")[0];
        }

//        if (filename != null && false) //false for now, we're going to use defaults
//        {
//            return filename;
//        } else if (new File("G:/FoundryVTT/Data/" + IMAGE_FOLDER + "/" + itemType + "/default.png").exists())
//        {
//            return IMAGE_FOLDER + "/" + itemType + "/default.png";
//        } else
//        {
//            //System.out.println("could not find "+ IMAGE_FOLDER+"/" + itemType + "/default.png");
//            new File("G:/FoundryVTT/Data/" + IMAGE_FOLDER + "/" + itemType).mkdir();
//        }
        return IMAGE_FOLDER + "/default.png";
    }

}
