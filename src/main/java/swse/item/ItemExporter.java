package swse.item;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.*;
import swse.prerequisite.OrPrerequisite;
import swse.prerequisite.SimplePrerequisite;
import swse.util.Context;

import static swse.util.Util.*;

public class ItemExporter extends BaseExporter {
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\items.json";
    public static final String JSON_TEMP_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\";
    public static final Effect EFFECT_AUTOFIRE = Effect.create("Autofire", "ROF", List.of(Change.create(ChangeKey.TO_HIT_MODIFIER, "-5"), Change.create(ChangeKey.SKIP_CRITICAL_MULTIPLY, "true")));
    public static final Effect EFFECT_SINGLE_SHOT = Effect.create("Single-Shot", "ROF", List.of());
    public static final Effect EFFECT_BARRAGE = Effect.create("Barrage", "ROF", List.of());
    public static final String IMAGE_FOLDER = "systems/swse/icon/item";
    public static final String ROOT = "G:/FoundryVTT/Data";
    private static final Set<String> unique = new HashSet<>();
    public static final Pattern SHOTS_PER_PACK = Pattern.compile("After (\\d*|one) \\w*, the ([\\w\\s]*) must be \\w*\\.");


    public static void main(String[] args) {
        List<String> itemLinks = new ArrayList<>();
        //weapons
        itemLinks.addAll(getAlphaLinks("/wiki/Category:Weapons?from="));
        itemLinks.addAll(getAlphaLinks("/wiki/Category:General_Equipment?from="));
        itemLinks.addAll(getAlphaLinks("/wiki/Category:Armor?from="));
        itemLinks.add("/wiki/Advanced_Melee_Weapons");
        itemLinks.add("/wiki/Exotic_Weapons_(Melee)");
        itemLinks.add("/wiki/Exotic_Weapons_(Ranged)");
        itemLinks.add("/wiki/Grenades");
        itemLinks.add("/wiki/Heavy_Weapons");
        itemLinks.add("/wiki/Lightsabers");
        itemLinks.add("/wiki/Mines");
        itemLinks.add("/wiki/Pistols");
        itemLinks.add("/wiki/Rifles");
        itemLinks.add("/wiki/Simple_Weapons_(Melee)");
        itemLinks.add("/wiki/Simple_Weapons_(Ranged)");
        //armor
        itemLinks.add("/wiki/Light_Armor");
        itemLinks.add("/wiki/Medium_Armor");
        itemLinks.add("/wiki/Heavy_Armor");
//        //general equipment and additional general equipment
        itemLinks.add("/wiki/Communications_Devices");
        itemLinks.add("/wiki/Computers_and_Storage_Devices");
        itemLinks.add("/wiki/Cybernetic_Devices");
        itemLinks.add("/wiki/Detection_and_Surveillance_Devices");
        itemLinks.add("/wiki/Explosives");
        itemLinks.add("/wiki/Life_Support");
        itemLinks.add("/wiki/Medical_Gear");

        itemLinks.addAll(getAlphaLinks("/wiki/Category:Hazards?from="));
        itemLinks.add("/wiki/Poisons");
        itemLinks.add("/wiki/CL_1_Hazards");
        itemLinks.add("/wiki/CL_2_Hazards");
        itemLinks.add("/wiki/CL_3_Hazards");
        itemLinks.add("/wiki/CL_4_Hazards");
        itemLinks.add("/wiki/CL_5_Hazards");
        itemLinks.add("/wiki/CL_6_Hazards");
        itemLinks.add("/wiki/CL_7_Hazards");
        itemLinks.add("/wiki/CL_8_Hazards");
        itemLinks.add("/wiki/CL_9_Hazards");
        itemLinks.add("/wiki/CL_10_Hazards");
        itemLinks.add("/wiki/CL_11_Hazards");
        itemLinks.add("/wiki/CL_12_Hazards");
        itemLinks.add("/wiki/CL_13_Hazards");
        itemLinks.add("/wiki/CL_14_Hazards");
        itemLinks.add("/wiki/CL_15_Hazards");
        itemLinks.add("/wiki/CL_20_Hazards");
        itemLinks.add("/wiki/Survival_Gear");
        itemLinks.add("/wiki/Tools");
        itemLinks.add("/wiki/Weapon_and_Armor_Accessories");
        itemLinks.add("/wiki/Equipment_Upgrades");

        itemLinks.add("/wiki/Advanced_Cybernetics");
        itemLinks.add("/wiki/Heirloom_Items");
        itemLinks.add("/wiki/Holocrons");
        itemLinks.add("/wiki/Implants");
        itemLinks.add("/wiki/Bio-Implants");
        itemLinks.add("/wiki/Sith_Artifacts");
        itemLinks.add("/wiki/Yuuzhan_Vong_Biotech");
//        //droid stuff
        itemLinks.add("/wiki/Locomotion_Systems");
        itemLinks.add("/wiki/Processor_Systems");
        itemLinks.add("/wiki/Appendages");
        itemLinks.add("/wiki/Droid_Accessories");

        ItemExporter itemExporter = new ItemExporter();
        List<JSONObject> entries = new ArrayList<>();
        double size = itemLinks.size();
        AtomicInteger i = new AtomicInteger();
        for (String itemLink : itemLinks) {
            entries.addAll(itemExporter.readItemMenuPage(itemLink, false));
            drawProgressBar(i.getAndIncrement() * 100 / size);
        }

        entries.addAll(getManualItems());


        Multimap<String, JSONObject> entryMaps = ArrayListMultimap.create();
        for(JSONObject entry : entries){
            entryMaps.put(entry.getString("type"), entry);
        }

        printUniqueNames(entries);

        //System.out.println("DROID SYSTEMS:");

        //printUniqueNames(entries.stream().filter(entry -> ((String)((JSONObject)entry.get("system")).get("subtype")).toLowerCase().contains("droid")).collect(Collectors.toList()));

        //System.out.println(entryMaps.keySet());
        //System.out.println(entryMaps.size());


        for(String key : entryMaps.keySet()){

            writeToJSON(new File(JSON_TEMP_OUTPUT+key+".json"), entryMaps.get(key), hasArg(args, "d"), toTitleCase(key));
        }
        //writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }

    private Collection<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);

        if(doc == null){
            return List.of();
        }

        Element body = doc.body();

        Elements tables = body.getElementsByClass("wikitable");

        Set<String> hrefs = new HashSet<>();


        Elements links = body.getElementsByClass("category-page__member-link");

        links.forEach(a -> hrefs.add(a.attr("href")));

        tables.forEach(table ->
        {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row ->
                    row.getElementsByTag("td").forEach(element -> {
                        if (element != null) {
                            Element anchor = element.getElementsByTag("a").first();
                            if (anchor != null) {
                                String href = anchor.attr("href");
                                hrefs.add(href);
                                //items.addAll(parseItem(href));
                            }
                        }
                    }));
        });




        return hrefs.stream().map(s -> {
            if (unique.contains(s)) {
                return null;
            }
            unique.add(s);
            return s;
        })
                .filter(Objects::nonNull)
                .flatMap((Function<String, Stream<JSONy>>) itemLink -> parseItem(itemLink, overwrite).stream()).map(item -> item.toJSON())
                .collect(Collectors.toList());
    }

    protected List<JSONy> parseItem(String itemLink, boolean overwrite) {
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

        if ("home".equalsIgnoreCase(itemName) || "datapad".equalsIgnoreCase(itemName) || "bacta tank".equalsIgnoreCase(itemName)) {
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
        boolean isDroid = false;

        Integer unarmedDamage = null;
        String unarmedModifier = null;
        List<Object> attributes = new ArrayList<>();
        String inaccurate;
        String accurate;
        String armorType = null;

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
                            isDroid = true;
                            armorType = payload;
                            break;
                        case "Droid System Type":
                            isDroid = true;
                            itemSubType = payload;
                            break;
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
                                armorType = payload;
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
            } else {
                attributes.addAll(parseComplexWeapon(child.text(), itemName));
            }
        }

        itemSubType = standardizeTypes(itemSubType);
        String itemType = getFoundryType(itemSubType);
        attributes.addAll(getManualAttributes(itemName, itemSubType));
        attributes.addAll(getModes(rateOfFire, itemName));
        String damageDie = getDamageDie(itemName, damage);

        if(itemType.equals("upgrade")){
            attributes.add(Change.create(ChangeKey.ITEM_MOD, true));
        }

        if (damageDie == null && damage != null) {
            if (damage.toLowerCase().contains(" to unarmed attacks")) {
                unarmedDamage = getNumber(damage);
                unarmedModifier = getParensContent(damage);
                //printUnique(Context.getValue("name"),unarmedModifier);
            } else {

            }
        }

        String stunDamageDie = getDieEquation(stunSetting, itemName);

        if (stunSetting != null && stunSetting.toLowerCase().contains("yes") && stunDamageDie == null) {
            stunDamageDie = damageDie;
        }

        if ("Amphistaff".equalsIgnoreCase(itemName)) {
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
        if ("DC-17m IWS".equalsIgnoreCase(itemName)) {
            damageDie = null;
            stunDamageDie = null;
            special = null;
        }

        //SubType subtype = SubType.create(damageType);


//        List<Attack> attacks = resolveAttacks(damage, stunSetting, attack, rateOfFire, damageType);

        final List<JSONy> jsonObjects = new ArrayList<>();

        if (itemName.equals("Energy Shields")) {
            //TODO build energy shield objects here

            return jsonObjects;
        }

        Item item = Item.create(itemName, itemType)
                .withLink(itemLink)
                .withDescription(content)
                .withSubtype(itemSubType)
                .withProvided(attributes)
                .withDamageDie(damageDie)
                .withStunDamageDie(stunDamageDie)
                .withDamageType(damageType)
                .withCost(cost)
                .withSize(size)
                .withWeight(weight)
                .withSource(book)
                .withAvailability(availability)
                .withProvided(Change.create(ChangeKey.BASE_ITEM, baseItem))
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, bonusToReflexDefense))
                .withProvided(Change.create(ChangeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, bonusToFortitudeDefense))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, maximumDexterityBonus))
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
                .withProvided(Change.create(ChangeKey.PREFIX, getPrefix(itemName)))
                .withProvided(Change.create(ChangeKey.SUFFIX, getSuffix(itemName)));

        if (isDroid) {
            item.withProvided(Change.create(ChangeKey.DROID_PART, true));
        }
        if (armorType != null) {
            item.withProvided(Change.create(ChangeKey.ARMOR_TYPE, armorType));
        }
        if (List.of("Probe", "Instrument", "Tool", "Claw", "Hand").contains(itemName)) {
            item.withProvided(getDroidAppendageAttributes(itemName));
        }
        if ("Stormtrooper Armor".equals(itemName)) {
            item.withProvided(Modification.create(ProvidedItem.create("Helmet Package", ItemType.ITEM)));
        }

        jsonObjects.addAll(getItemVariants(item, itemName));

        jsonObjects.add(item);

        return jsonObjects;
    }

    private List<Object> getDroidAppendageAttributes(String itemName) {
        List<Object> provided = new LinkedList<>();
        provided.add(Change.create(ChangeKey.APPENDAGES, "1"));
        provided.add(Change.create(ChangeKey.APPENDAGE_TYPE, itemName));

        switch(itemName){
            case "Probe":
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, 0)
                        .withParentPrerequisite(OrPrerequisite.or(
                                new SimplePrerequisite("Fine Size", "SIZE", "Fine"),
                                new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive"),
                                new SimplePrerequisite("Tiny Size", "SIZE", "Tiny"),
                                new SimplePrerequisite("Small Size", "SIZE", "Small"))));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, 1)
                        .withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d2")
                        .withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d3")
                        .withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d4")
                        .withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d6")
                        .withParentPrerequisite(OrPrerequisite.or(
                                new SimplePrerequisite("Colossal Size", "SIZE", "Colossal"),
                                new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)"),
                                new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)"),
                                new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)"))));
                break;
            case "Instrument":
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, 0)
                        .withParentPrerequisite(OrPrerequisite.or(
                                new SimplePrerequisite("Fine Size", "SIZE", "Fine"),
                                new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive"),
                                new SimplePrerequisite("Tiny Size", "SIZE", "Tiny"))));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, 1)
                        .withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d2")
                        .withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d3")
                        .withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d4")
                        .withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d6")
                        .withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d8")
                        .withParentPrerequisite(OrPrerequisite.or(
                                new SimplePrerequisite("Colossal Size", "SIZE", "Colossal"),
                                new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)"),
                                new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)"),
                                new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)"))));
                break;
            case "Tool":
            case "Hand":
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, 0)
                        .withParentPrerequisite(OrPrerequisite.or(
                                new SimplePrerequisite("Fine Size", "SIZE", "Fine"),
                                new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive"))));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, 1)
                        .withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d2")
                        .withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d3")
                        .withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d4")
                        .withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d6")
                        .withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d8")
                        .withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "2d6")
                        .withParentPrerequisite(OrPrerequisite.or(
                                new SimplePrerequisite("Colossal Size", "SIZE", "Colossal"),
                                new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)"),
                                new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)"),
                                new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)"))));
                break;
            case "Claw":
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, 0)
                        .withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, 1)
                        .withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d2")
                        .withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d3")
                        .withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d4")
                        .withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d6")
                        .withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "1d8")
                        .withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "2d6")
                        .withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")));
                provided.add(Change.create(ChangeKey.DROID_UNARMED_DAMAGE_DIE, "2d8")
                        .withParentPrerequisite(OrPrerequisite.or(
                                new SimplePrerequisite("Colossal Size", "SIZE", "Colossal"),
                                new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)"),
                                new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)"),
                                new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)"))));
                break;
        }
        return provided;
    }

    private static Collection<? extends JSONy> getItemVariants(Item item, String itemName) {
        List<String> variantNames = new ArrayList<>();
        List<JSONy> variants = new LinkedList<>();
        if ("Stormtrooper Armor".equals(itemName)) {
            variantNames.add("Snowtrooper Armor");
            variantNames.add("Sandtrooper Armor");
            variantNames.add("Sith Trooper Armor");
            //TODO Add clone trooper armor phase 1
            //TODO Add clone trooper armor phase 2
            //TODO Add packages to each armor set
        }

        for (String variantName : variantNames) {
            Item variant = item.copy();
            variant.withName(variantName);

            if ("Snowtrooper Armor".equals(variantName)) {
                variant.withProvided(Change.create(ChangeKey.IMMUNITY, "Extreme Cold"));
                variant.withCost("18,000");
            }
            if ("Sandtrooper Armor".equals(variantName)) {
                variant.withProvided(Change.create(ChangeKey.IMMUNITY, "Extreme Heat"));
                variant.withCost("18,000");
            }
            if ("Sith Trooper Armor".equals(variantName)) {
                variant.withDescription("<p>Designed by the onboard computers of The Star Forge and replicated " +
                        "millions of times over, Sith Trooper Armor is silver multi-piece plating worn over a sealed " +
                        "black body glove, with a helmet incorporating a blast shield, Comlink, and Helmet Package. " +
                        "Statistically, it is identical to Stormtrooper Armor. It includes rudimentary environmental " +
                        "protection, three-phase sonic filtering, and visual amplification.</p><p>Sith Trooper Armor " +
                        "grants a wearer who has the Armor Proficiency (Light) feat a +2 Equipment bonus on Perception" +
                        " checks, as well as Low-Light Vision. Sith Trooper Armor also includes an integrated Comlink " +
                        "in the helmet, allowing hands-free communication.</p>");
            }
            variants.add(variant);
        }
        return variants;
    }

    private static List<Object> parseComplexWeapon(String text, String itemName) {
        List<Object> attributes = new ArrayList<>();

        if (List.of("Energy Lance", "E-5s Blaster Rifle", "SG-4 Blaster Rifle", "HB-9 Blaster Rifle",
                "Variable Blaster Rifle", "Sonic Blaster", "Commando Special Rifle", "Heavy Variable Blaster Rifle",
                "Thunderbolt Repeater Blaster", "Vibrobayonet", "Stun Bayonet", "Neuronic Whip", "Blastsword",
                "Z-6 Rotary Blaster", "Archaic Lightsaber", "Heavy Blaster Pistol", "Snap-Shot Blaster Pistol",
                "Sidearm Blaster Pistol", "Gee-Tech 12 Defender", "Retrosaber", "Jury-Rigging a Power Pack Bomb",
                "Stun Baton", "Squib Battering Ram", "Utility Belt", "Gas Canister", "Power Recharger", "Power Pack",
                "Enhanced Energy Projector", "Improved Energy Cell", "Tremor Cell", "Slugthrower Pistol", "Slugthrower Rifle").contains(itemName)) {
            return attributes;
        }

        //Treated As For Range
        if (text.contains("treated")) {
            Pattern TREATED_AS_FOR_RANGE = Pattern.compile("treated as(?: a)? (Rifle|Rifles|Pistol|Simple Weapon \\(Ranged\\)|Simple Weapons \\(Ranged\\))(?:, not a Thrown Weapon,)? for");
            Matcher m = TREATED_AS_FOR_RANGE.matcher(text);
            if (m.find()) {
                attributes.add(Change.create(ChangeKey.TREATED_AS, standardizeTypes(m.group(1))));
            }
        }


        if (text.contains("Power Pack")) {
            Matcher m = SHOTS_PER_PACK.matcher(text);
            while (m.find()) {
                String group = m.group(2);
                if (group.equals("one")) {
                    group = "1";
                }
                attributes.add(Change.create(ChangeKey.AMMO, group + ":" + m.group(1)));
                //printUnique(m.group(0));
            }
            if (attributes.isEmpty() && attributes.isEmpty()) {
                //printUnique(itemName,text);
            }
        }

        return attributes;
    }

    private static List<Object> getManualAttributes(String itemName, String itemSubType) {
        List<Object> attributes = new LinkedList<>();

        if("Lightsabers".equals(itemSubType)){
            attributes.add(Effect.create("Self-Built", List.of(Change.create(ChangeKey.TO_HIT_MODIFIER, 1))));
        }
        else if ("Energy Lance".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:50"));
        }
        else if ("E-5s Blaster Rifle".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:5"));
        }
        else if ("SG-4 Blaster Rifle".equals(itemName)) {
            attributes.add(Effect.create("Blaster", "POWER", List.of(Change.create(ChangeKey.AMMO, "Power Pack:50"))));
            attributes.add(Effect.create("Harpoon", "POWER", List.of(Change.create(ChangeKey.AMMO, "Harpoon:1"))));
        }
        else if ("HB-9 Blaster Rifle".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:25"));
            attributes.add(Change.create(ChangeKey.AMMO, "Gas Canister:200"));
        }
        else if ("Commando Special Rifle".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:25"));
        }
        else if ("Variable Blaster Rifle".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:500"));
            attributes.add(Effect.create("3d4", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d4"))));
            attributes.add(Effect.create("3d6", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d6"), Change.create(ChangeKey.AMMO_USE_MULTIPLIER, "5"))));
            attributes.add(Effect.create("3d8", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d8"), Change.create(ChangeKey.AMMO_USE_MULTIPLIER, "10"))));
        }
        else if ("Heavy Variable Blaster Rifle".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:500"));
            attributes.add(Effect.create("Ascension gun", "POWER", List.of(Change.create(ChangeKey.AMMO, "Syntherope:2"))));
            attributes.add(Effect.create("3d6", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d6"))));
            attributes.add(Effect.create("3d8", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d8"), Change.create(ChangeKey.AMMO_USE_MULTIPLIER, "10"))));
            attributes.add(Effect.create("3d10", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d10"), Change.create(ChangeKey.AMMO_USE_MULTIPLIER, "20"))));
        }
        else if ("Sonic Blaster".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Proprietary Power Pack:15:11:0.2"));
        }
        else if ("Heavy Blaster Pistol".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:50"));
        }
        else if ("Snap-Shot Blaster Pistol".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:1"));
            attributes.add(Change.create(ChangeKey.CONCEALMENT_BONUS, "5"));
        }
        else if ("Sidearm Blaster Pistol".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:250"));
        }
        else if ("Gee-Tech 12 Defender".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:2"));
            attributes.add(Change.create(ChangeKey.CONCEALMENT_BONUS, "5"));
        }
        else if ("Thunderbolt Repeater Blaster".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.TO_HIT_MODIFIER, "-5"));
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:20"));
            attributes.add(Effect.create("Braced", List.of(Change.create(ChangeKey.TO_HIT_MODIFIER, "0"))));
        }
        else if ("Z-6 Rotary Blaster".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:1"));
            attributes.add(Change.create(ChangeKey.TO_HIT_MODIFIER, "-5"));
            attributes.add(Effect.create("Braced", List.of(Change.create(ChangeKey.TO_HIT_MODIFIER, "0"))));
        }
        else if ("Retrosaber".equals(itemName)) {
            attributes.add(Effect.create("Overcharge", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "2d10"))));
            attributes.add(Effect.create("Burnout", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "2d4"))));
        }
        else if ("Slugthrower Pistol".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Slug Clip:10:40:0.2"));
        }
        else if ("Slugthrower Rifle".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Slug Clip:20:40:0.2"));
        }
        else if ("WESTAR-M5 Blaster Rifle".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:100"));
            attributes.add(Change.create(ChangeKey.AMMO, "Gas Canister:500"));
            attributes.add(Effect.create("Anti-Personnel", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d8"))));
            attributes.add(Effect.create("Anti-Vehicle", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d10"),
                    Change.create(ChangeKey.AMMO_USE_MULTIPLIER, "10"), Change.create(ChangeKey.PENETRATION, "5"))));
        }
        else if ("DC-19 \"Stealth\" Carbine".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:10"));
            attributes.add(Change.create(ChangeKey.AMMO, "Stealth Mixture Gas Canister:500:500:0.25"));
        }
        else if ("Amban Phase-Pulse Blaster".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:1"));
        }
        else if ("Scatter Gun".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "10 Shells:10:20:1"));
            attributes.add(Effect.create("Point-Blank Range", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "3d8"))));
            attributes.add(Effect.create("Short Range", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "2d8"))));
            attributes.add(Effect.create("Medium Range", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "0"))));
            attributes.add(Effect.create("Long Range", "POWER", List.of(Change.create(ChangeKey.DAMAGE, "0"))));
        }
        else if ("DC-15x Sniper Rifle".equals(itemName)) {
            attributes.add(Change.create(ChangeKey.AMMO, "Power Pack:5"));
        }
        else if ("DC-17m IWS".equals(itemName)) {
            attributes.add(Effect.create("Blaster Rifle", "POWER", List.of(
                    Change.create(ChangeKey.DAMAGE, "3d8"),
                    Change.create(ChangeKey.DAMAGE_TYPE, "Energy"),
                    Change.create(ChangeKey.AMMO, "Power Pack:60"),
                    Change.create(ChangeKey.AMMO, "Gas Canister:300")
            ), List.of(Link.create("POWER", LinkType.EXCLUSIVE), Link.create("Blaster Rifle", LinkType.PARENT)))); //, List.of(EFFECT_SINGLE_SHOT, EFFECT_AUTOFIRE, Effect.create("Stun", List.of( change1, change)))

            attributes.add(Effect.create("Sniper Rifle", "POWER", List.of(
                    Change.create(ChangeKey.DAMAGE, "3d8"),
                    Change.create(ChangeKey.AMMO, "Sniper Power Pack:5:100:0.5")
            ), List.of(Link.create("POWER", LinkType.EXCLUSIVE)))); //, List.of(EFFECT_SINGLE_SHOT)

            attributes.add(Effect.create("Anti-Armor", "POWER", List.of(
                    Change.create(ChangeKey.DAMAGE, "4d10"),
                    Change.create(ChangeKey.AMMO, "Explosive Shell:1:300:1")

            ), List.of(Link.create("POWER", LinkType.EXCLUSIVE))));//, List.of(EFFECT_SINGLE_SHOT)

            attributes.add(Effect.create("PEP Laser", "POWER", List.of(
                    Change.create(ChangeKey.DAMAGE, "3d6"),
                    Change.create(ChangeKey.DAMAGE_TYPE, "Stun"),
                    Change.create(ChangeKey.AMMO, "PEP Cartridge:15:100:0.5")
            ), List.of(Link.create("POWER", LinkType.EXCLUSIVE))));//, List.of(EFFECT_SINGLE_SHOT)

            attributes.add(EFFECT_SINGLE_SHOT.copy().withLinks(Link.create("Fire Mode", LinkType.EXCLUSIVE)));
            attributes.add(EFFECT_AUTOFIRE.copy().withLinks(Link.create("Blaster Rifle", LinkType.CHILD), Link.create("Fire Mode", LinkType.EXCLUSIVE)));
            attributes.add(Effect.create("Stun", List.of(
                    Change.create(ChangeKey.DAMAGE, "3d8").withMode(ActiveEffectMode.OVERRIDE),
                    Change.create(ChangeKey.DAMAGE_TYPE, "Stun").withMode(ActiveEffectMode.OVERRIDE))).withLinks(Link.create("Blaster Rifle", LinkType.CHILD), Link.create("Fire Mode", LinkType.EXCLUSIVE)));
        }
        else if ("Credit Chip".equals(itemName)){
            attributes.add(Change.create(ChangeKey.CREDIT, 0));
            attributes.add(Change.create(ChangeKey.CREDIT_TYPE, "CREDIT"));
            attributes.add(Change.create(ChangeKey.CREDIT_ENTITY_TYPE, "CONTAINER"));
        }
        else if ("Heuristic Processor".equals(itemName)){
            attributes.add(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Basic Processor"));
        }
        else if ("Helmet Package".equals(itemName)){
            attributes.add(Change.create(ChangeKey.SKILL_BONUS, "perception:+2"));
            attributes.add(Change.create(ChangeKey.LOW_LIGHT_VISION, "true"));
            attributes.add(Modification.create(ProvidedItem.create("Hands-Free Comlink", ItemType.ITEM)));
        }

        return attributes;
    }

    private static String getDamageDie(String itemName, String damage) {
        return "Amphistaff".equalsIgnoreCase(itemName) ? null : getDieEquation(damage, itemName);
    }

    private static String getPrefix(String itemName) {
        return null;
    }

    private static String getSuffix(String itemName) {
        if ("Bayonet Ring".equals(itemName)) {
            return "Bayonet";
        }
        return null;
    }

    private static List<Effect> getModes(String rateOfFire, String itemName) {
        List<Effect> effects = new LinkedList<>();
        if (rateOfFire != null && rateOfFire.contains("Autofire")) {
            effects.add(EFFECT_AUTOFIRE.copy().withLinks(Link.create("Fire Mode", LinkType.EXCLUSIVE)));
        }
        if (rateOfFire != null && rateOfFire.contains("Barrage")) {
            effects.add(EFFECT_BARRAGE.copy().withLinks(Link.create("Fire Mode", LinkType.EXCLUSIVE)));
        }

        if (rateOfFire != null && rateOfFire.contains("Single-Shot")) {
            effects.add(EFFECT_SINGLE_SHOT.copy().withLinks(Link.create("Fire Mode", LinkType.EXCLUSIVE)));
        }

        if ("Amphistaff".equalsIgnoreCase(itemName)) {
            effects.add(Effect.create("Quarterstaff", "AMPHISTAFF_FORM", List.of(
                    Change.create(ChangeKey.DAMAGE, "1d6/1d6"),
                    Change.create(ChangeKey.DAMAGE_TYPE, "Bludgeoning"),
                    Change.create(ChangeKey.SPECIAL, List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                            "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                            "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                            "An Amphistaff can only spit venom once every 24 standard hours."
                    ))
            )));
            effects.add(Effect.create("Spear", "AMPHISTAFF_FORM", List.of(
                    Change.create(ChangeKey.DAMAGE, "1d8"),
                    Change.create(ChangeKey.DAMAGE_TYPE, "Piercing"),
                    Change.create(ChangeKey.SPECIAL, List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                                    "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                                    "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                                    "An Amphistaff can only spit venom once every 24 standard hours."
                            )
                    ))));
            effects.add(Effect.create("Whip", "AMPHISTAFF_FORM", List.of(
                    Change.create(ChangeKey.DAMAGE, "1d4"),
                    Change.create(ChangeKey.DAMAGE_TYPE, "Piercing"),
                    Change.create(ChangeKey.IS_REACH, "2"),
                    Change.create(ChangeKey.PROVIDED_ACTION, "Pin"),
                    Change.create(ChangeKey.PROVIDED_ACTION, "Trip"),
                    Change.create(ChangeKey.SPECIAL, List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                                    "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                                    "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                                    "An Amphistaff can only spit venom once every 24 standard hours.",
                            "Is a Reach Weapon"
                            )

                    ))));
        }
        if ("DC-17m IWS".equalsIgnoreCase(itemName)) {
            return new ArrayList<>();
        }

        return effects;
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
                "exotic ranged weapons", "pistols", "rifles", "lightsabers", "heavy weapons", "grenades", "mines", "explosives", "pistols, rifles").contains(subType.toLowerCase())) {
            return "weapon";
        } else if (List.of("light armor", "medium armor", "heavy armor", "droid accessories (droid armor)").contains(subType.toLowerCase())) {
            return "armor";
        } else if (List.of("weapons upgrade", "armor upgrade", "weapons and armor accessories").contains(subType.toLowerCase())) {
            return "upgrade";
        } else if (List.of("hazard").contains(subType.toLowerCase())) {
            return "hazard";
        } else if (List.of("bio-implants", "cybernetic devices", "implants", "advanced cybernetics").contains(subType.toLowerCase())) {
            return "implant";
        } else if (List.of("locomotion systems",
                "processor systems",
                "appendages",
                "droid accessories (sensor systems)",
                "droid accessories (translator units)",
                "droid accessories (miscellaneous systems)",
                "droid accessories (communications systems)",
                "droid accessories (droid stations)",
                "droid accessories (shield generator systems)").contains(subType.toLowerCase())) {
            return "droid system";
        }

        //printUnique("\"" + subType.toLowerCase()+"\"");
        return "equipment";
    }


    private static String standardizeTypes(String trim) {
        if(trim == null){
            trim = "equipment";
        }

        try{

            ItemSubtype subtype = ItemSubtype.getEnum(trim);
            return subtype.toString();
        } catch (IllegalArgumentException e){
            System.err.println(trim + " is not a valid subtype");
        }



        if (trim.equalsIgnoreCase("equipment")) {
            // printUnique(Context.getValue("name"));
            return "Equipment";
        }
        trim = trim.trim();
        if ("implant".equalsIgnoreCase(trim)) {
            return "Implants";
        }
        if ("pistol".equalsIgnoreCase(trim)) {
            return "Pistols";
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


    @Nonnull
    private static Collection<JSONObject> getManualItems() {
        List<JSONObject> items = new ArrayList<>();

        String energyShieldDescription = "Energy Shields give a character a Shield Rating, which functions exactly as normal shielding. An energy-shield generator is typically worn on the forearm or upper arm and must be activated as a Swift Action. Energy Shields typically have 5 charges, and energy shields can only be activated once per encounter (the stress on the shield generator causes the device to overload otherwise, so the manufacturers build in failsafes to prevent such an occurrence).\n" +
                "\n" +
                "Each activation consumes one charge and lasts through the end of the encounter. An Energy Shield only protects against weapons that deal Energy damage; a weapon that deals any other type of damage bypasses the shield's SR entirely.\n" +
                "\n" +
                "The Shield Rating provided by the Energy Shield determines the Energy Shield's price, as well as the type of Armor Proficiency feat required to operate the Energy Shield without penalty (Armor Proficiency (Light), Armor Proficiency (Medium), or Armor Proficiency (Heavy)).\n" +
                "\n" +
                "Energy Shields come in three varieties: Light, Medium, and Heavy. Each Energy Shield type corresponds to an armor type (Light is Light Armor, Medium is Medium Armor, and Heavy is Heavy Armor). A character with an active Energy Shield without the relevant Armor Proficiency feat takes a -5 penalty to their Reflex Defense, and the wearer is denied its Dexterity bonus to their Reflex Defense (though they still gain the benefits of the Energy Shield).\n" +
                "\n" +
                "Regardless of whether or not the character is proficient with the Energy Shield, the character always takes the Armor Check Penalty associated with the Energy Shield while it is activated. Additionally, each type of Energy Shield imposes its Maximum Dexterity Bonus restriction only when activated, not when worn and inert.\n" +
                "\n" +
                "An Energy Shield can be added to a suit of armor as an Armor Accessory. An Energy Shield can be modified by Armor Templates only if the Template specifically states that it can be used on Energy Shields, and the Energy Shield confers that benefit only when it is activated.";
        items.add(Item.create("Energy Shield (SR 5)", "armor")
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Energy Shield"))
                .withDescription(energyShieldDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 5))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, 4))
                .withCost("500")
                .withSubtype("Light Armor")
                .toJSON());

        items.add(Item.create("Energy Shield (SR 10)", "armor")
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Energy Shield"))
                .withDescription(energyShieldDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 10))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, 4))
                .withCost("2000")
                .withSubtype("Light Armor")
                .toJSON());

        items.add(Item.create("Energy Shield (SR 15)", "armor")
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Energy Shield"))
                .withDescription(energyShieldDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 15))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, 3))
                .withCost("4500")
                .withSubtype("Medium Armor")
                .toJSON());

        items.add(Item.create("Energy Shield (SR 20)", "armor")
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Energy Shield"))
                .withDescription(energyShieldDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 20))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, 3))
                .withCost("8000")
                .withSubtype("Medium Armor")
                .toJSON());

        items.add(Item.create("Energy Shield (SR 25)", "armor")
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Energy Shield"))
                .withDescription(energyShieldDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 25))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, 2))
                .withCost("12500")
                .withSubtype("Heavy Armor")
                .toJSON());

        items.add(Item.create("Energy Shield (SR 30)", "armor")
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Energy Shield"))
                .withDescription(energyShieldDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 30))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, 2))
                .withCost("18000")
                .withSubtype("Heavy Armor")
                .toJSON());

        String shieldGeneratorDescription = "The Droid is fitted with a deflector Shield Generator- the same type mounted on Starships. Whenever the Droid would take damage, reduce the damage by the Droid's Shield Rating (SR). If the damage is equal to or greater than the Droid's Shield Rating, the Droid's Shield Rating is reduced by 5. By spending three Swift Actions on the same or consecutive rounds, the Droid may make a DC 20 Endurance check to restore lost shield power. If the check succeeds, the Droid's Shield Rating increases by 5 points (up to its normal Shield Rating).";
        items.add(Item.create("Shield Generator (SR 5)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Shield Generator"))
                .withDescription(shieldGeneratorDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 5))
                .withCost("2500 x Cost Factor")
                .withWeight("(10 x Cost Factor) kg")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .toJSON());

        final String SR10Prerequisite = "Only Droids of Small size or larger can be equipped with a SR 10 Generator.";
        items.add(Item.create("Shield Generator (SR 10)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Shield Generator"))
                .withDescription(shieldGeneratorDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 10))
                .withCost("5000 x Cost Factor")
                .withWeight("(20 x Cost Factor) kg")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .withPrerequisite(new OrPrerequisite(SR10Prerequisite,
                        List.of(
                                new SimplePrerequisite("Small", "TRAIT", "Small"),
                                new SimplePrerequisite("Medium", "TRAIT", "Medium"),
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        final String SR15Prerequisite = "Only Droids of Small size or larger can be equipped with a SR 10 Generator.";
        items.add(Item.create("Shield Generator (SR 15)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Shield Generator"))
                .withDescription(shieldGeneratorDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 15))
                .withCost("7500 x Cost Factor")
                .withWeight("(30 x Cost Factor) kg")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .withPrerequisite(new OrPrerequisite(SR15Prerequisite,
                        List.of(
                                new SimplePrerequisite("Medium", "TRAIT", "Medium"),
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        final String SR20Prerequisite = "Only Droids of Large or bigger size can be equipped with a SR 20 generator.";
        items.add(Item.create("Shield Generator (SR 20)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Shield Generator"))
                .withDescription(shieldGeneratorDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 20))
                .withCost("10000 x Cost Factor")
                .withWeight("(40 x Cost Factor) kg")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .withPrerequisite(new OrPrerequisite(SR20Prerequisite,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

       items.add(Item.create("Ion Shield Generator (SR 5)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Shield Generator"))
                .withDescription(shieldGeneratorDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 5).withModifier("Ion"))
                .withCost("1250 x Cost Factor")
                .withWeight("(10 x Cost Factor) kg")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .toJSON());

        items.add(Item.create("Ion Shield Generator (SR 10)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Shield Generator"))
                .withDescription(shieldGeneratorDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 10).withModifier("Ion"))
                .withCost("2500 x Cost Factor")
                .withWeight("(20 x Cost Factor) kg")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .withPrerequisite(new OrPrerequisite(SR10Prerequisite,
                        List.of(
                                new SimplePrerequisite("Small", "TRAIT", "Small"),
                                new SimplePrerequisite("Medium", "TRAIT", "Medium"),
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Ion Shield Generator (SR 15)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Shield Generator"))
                .withDescription(shieldGeneratorDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 15).withModifier("Ion"))
                .withCost("3750 x Cost Factor")
                .withWeight("(30 x Cost Factor) kg")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .withPrerequisite(new OrPrerequisite(SR15Prerequisite,
                        List.of(
                                new SimplePrerequisite("Medium", "TRAIT", "Medium"),
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Ion Shield Generator (SR 20)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ACTS_AS_FOR_PROFICIENCY, "Shield Generator"))
                .withDescription(shieldGeneratorDescription)
                .withProvided(Change.create(ChangeKey.SHIELD_RATING, 20).withModifier("Ion"))
                .withCost("5000 x Cost Factor")
                .withWeight("(40 x Cost Factor) kg")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .withPrerequisite(new OrPrerequisite(SR20Prerequisite,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());


        items.add(Item.create("Translator Unit (DC 20)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Translator Units)")
                .withCost("200")
                .withWeight("1 kg")
                .withProvided(Change.create(ChangeKey.TRANSLATE_DC, 20)).toJSON());

        items.add(Item.create("Translator Unit (DC 15)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Translator Units)")
                .withCost("500")
                .withWeight("2 kg")
                .withProvided(Change.create(ChangeKey.TRANSLATE_DC, 15)).toJSON());

        items.add(Item.create("Translator Unit (DC 10)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Translator Units)")
                .withCost("1000")
                .withWeight("4 kg")
                .withProvided(Change.create(ChangeKey.TRANSLATE_DC, 10)).toJSON());

        items.add(Item.create("Translator Unit (DC 5)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Translator Units)")
                .withCost("2000")
                .withWeight("8 kg")
                .withProvided(Change.create(ChangeKey.TRANSLATE_DC, 5)).toJSON());


        final String hardenedSystem = "Droids of Large or greater size can be designed to have internal armor and redundant systems that enable it to continue functioning despite heavy damage";
        items.add(Item.create("Hardened Systems (x2)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("1000 x Cost Factor")
                .withWeight("(100 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Change.create(ChangeKey.DAMAGE_THRESHOLD_HARDENED_MULTIPLIER, 2))
                .withProvided(Change.create(ChangeKey.HEALTH_HARDENED_MULTIPLIER, 2))
                .withPrerequisite(new OrPrerequisite(hardenedSystem,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Hardened Systems (x3)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("2500 x Cost Factor")
                .withWeight("(250 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Change.create(ChangeKey.DAMAGE_THRESHOLD_HARDENED_MULTIPLIER, 3))
                .withProvided(Change.create(ChangeKey.HEALTH_HARDENED_MULTIPLIER, 3))
                .withPrerequisite(new OrPrerequisite(hardenedSystem,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Hardened Systems (x4)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("4000 x Cost Factor")
                .withWeight("(400 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Change.create(ChangeKey.DAMAGE_THRESHOLD_HARDENED_MULTIPLIER, 4))
                .withProvided(Change.create(ChangeKey.HEALTH_HARDENED_MULTIPLIER, 4))
                .withPrerequisite(new OrPrerequisite(hardenedSystem,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Hardened Systems (x5)", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("6250 x Cost Factor")
                .withWeight("(650 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Change.create(ChangeKey.DAMAGE_THRESHOLD_HARDENED_MULTIPLIER, 5))
                .withProvided(Change.create(ChangeKey.HEALTH_HARDENED_MULTIPLIER, 5))
                .withPrerequisite(new OrPrerequisite(hardenedSystem,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Repulsor-Assisted Lifting System", "equipment")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("200 x Cost Factor")
                .withWeight("(200 x Cost Factor) kg")
                .withAvailability("-")
                .withProvided(Change.create(ChangeKey.CARGO_CAPACITY, "x3"))
                .toJSON());

        items.add(Item.create("Plasteel Shell", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("400 x Cost Factor")
                .withWeight("(2 x Cost Factor) kg")
                .withAvailability("-")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "2"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "5"))
                .toJSON());

        items.add(Item.create("Stealth Shell", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("400 x Cost Factor")
                .withWeight("(2 x Cost Factor) kg")
                .withAvailability("-")
                .withProvided(Change.create(ChangeKey.SKILL_BONUS, "stealth:2"))
                .toJSON());

        items.add(Item.create("Quadanium Shell", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("900 x Cost Factor")
                .withWeight("(3 x Cost Factor) kg")
                .withAvailability("-")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "3"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "4"))
                .toJSON());

        items.add(Item.create("Durasteel Shell", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("1600 x Cost Factor")
                .withWeight("(8 x Cost Factor) kg")
                .withAvailability("-")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "4"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "4"))
                .toJSON());

        items.add(Item.create("Quadanium Plating", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("2500 x Cost Factor")
                .withWeight("(10 x Cost Factor) kg")
                .withAvailability("Licensed")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "5"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Durasteel Plating", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("3600 x Cost Factor")
                .withWeight("(12 x Cost Factor) kg")
                .withAvailability("Licensed")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "6"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Quadanium Battle Armor", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Medium Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("4900 x Cost Factor")
                .withWeight("(7 x Cost Factor) kg")
                .withAvailability("Restricted")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "7"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Duranium Plating", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Medium Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("6400 x Cost Factor")
                .withWeight("(16 x Cost Factor) kg")
                .withAvailability("Restricted")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "8"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "2"))
                .toJSON());

        items.add(Item.create("Durasteel Battle Armor", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Medium Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("9600 x Cost Factor")
                .withWeight("(8 x Cost Factor) kg")
                .withAvailability("Restricted")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "8"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Mandalorian Steel Shell", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Heavy Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("8100 x Cost Factor")
                .withWeight("(9 x Cost Factor) kg")
                .withAvailability("Military, Rare")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "9"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Duranium Battle Armor", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Heavy Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("10000 x Cost Factor")
                .withWeight("(10 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "10"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "2"))
                .toJSON());

        items.add(Item.create("Neutronium Plating", "armor")
                .withProvided(Change.create(ChangeKey.DROID_PART, true))
                .withProvided(Change.create(ChangeKey.ARMOR_TYPE, "Heavy Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("12100 x Cost Factor")
                .withWeight("(20 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_ARMOR, "11"))
                .withProvided(Change.create(ChangeKey.MAXIMUM_DEXTERITY_BONUS, "1"))
                .toJSON());

        items.add(Item.create("Datapad", "equipment")
                        .withDescription("These handheld personal computers serve as notebooks, day planners, calculators, and sketchpads. In addition to performing basic computer functions, Datapads can interface with larger computer networks directly or via Comlink.\n" +
                                "\n" +
                                "A Datapad is a computer with an Intelligence score of 12. Basic Datapads also exist (Intelligence 10, 100 credits), but they are actually just storage devices with display, input, and editing capability; they have no ability to run programs.")
                .withSubtype("Computers and Storage Devices")
                .withCost("1000")
                .withWeight("0.5 kg")
                .withProvided(Change.create(ChangeKey.INTELLIGENCE, 12))
                .toJSON());

        items.add(Item.create("Basic Datapad", "equipment")
                .withDescription("These handheld personal computers serve as notebooks, day planners, calculators, and sketchpads. In addition to performing basic computer functions, Datapads can interface with larger computer networks directly or via Comlink.\n" +
                        "\n" +
                        "A Datapad is a computer with an Intelligence score of 12. Basic Datapads also exist (Intelligence 10, 100 credits), but they are actually just storage devices with display, input, and editing capability; they have no ability to run programs.")

                .withSubtype("Computers and Storage Devices")
                .withCost("100")
                .withWeight("0.3 kg")
                .withProvided(Change.create(ChangeKey.INTELLIGENCE, 10))
                .toJSON());

        items.add(Item.create("Bacta Tank", "equipment")
                .withDescription("This large specialized tank is filled with the powerful healing agent, Bacta, which promotes rapid healing.\n" +
                        "\n" +
                        "A Bacta Tank can be used in conjunction with Surgery. If the Treat Injury check is successful, the patient heals a number of hit points equal to it's Character Level, in addition to that provided by Surgery.\n" +
                        "\n" +
                        "A Bacta Tank can also be used when treating Disease, Poison, or Radiation in a creature. In this case, the Bacta Tank grants a +5 Equipment bonus on your Treat Injury check.\n" +
                        "\n" +
                        "A Bacta Tank and a supply of Bacta is expensive, so much medical equipment is usually found only in hospitals, aboard Capital Ships, and within major military bases. Each hour of treatment consumes one liter of Bacta, which costs 100 credits. A typical Bacta Tank holds up to 300 liters of Bacta, and the Bacta Tank must hold at least 150 liters at all times to provide any benefit. Only one creature can be immersed in the tank at any given time.")
                .withSubtype("Medical Gear")
                .withCost("100000 + bacta * 100")
                        .withWeight("500 + bacta * 2 kg")
                .withProvided(Change.create(ChangeKey.BACTA, 0))
                .toJSON());

        items.add(Item.create("Bacta (liter)", "equipment")
                .withDescription("This large specialized tank is filled with the powerful healing agent, Bacta, which promotes rapid healing.\n" +
                        "\n" +
                        "A Bacta Tank can be used in conjunction with Surgery. If the Treat Injury check is successful, the patient heals a number of hit points equal to it's Character Level, in addition to that provided by Surgery.\n" +
                        "\n" +
                        "A Bacta Tank can also be used when treating Disease, Poison, or Radiation in a creature. In this case, the Bacta Tank grants a +5 Equipment bonus on your Treat Injury check.\n" +
                        "\n" +
                        "A Bacta Tank and a supply of Bacta is expensive, so much medical equipment is usually found only in hospitals, aboard Capital Ships, and within major military bases. Each hour of treatment consumes one liter of Bacta, which costs 100 credits. A typical Bacta Tank holds up to 300 liters of Bacta, and the Bacta Tank must hold at least 150 liters at all times to provide any benefit. Only one creature can be immersed in the tank at any given time.")
                .withSubtype("Medical Gear")
                .withCost("100")
                .withWeight("2 kg")
                .withProvided(Change.create(ChangeKey.BACTA, 1))
                .toJSON());
        return items;
    }

}
