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
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.BaseExporter;
import swse.common.JSONy;
import swse.prerequisite.OrPrerequisite;
import swse.prerequisite.SimplePrerequisite;
import swse.util.Context;
import static swse.util.Util.getDieEquation;
import static swse.util.Util.getNumber;
import static swse.util.Util.getParensContent;

public class ItemExporter extends BaseExporter {
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\items.json";
    public static final Mode MODE_AUTOFIRE = Mode.create("Autofire", "ROF", List.of(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "-5")));
    public static final Mode MODE_SINGLE_SHOT = Mode.create("Single-Shot", "ROF", List.of());
    public static final Mode MODE_BARRAGE = Mode.create("Barrage", "ROF", List.of());
    public static final String IMAGE_FOLDER = "systems/swse/icon/item";
    public static final String ROOT = "G:/FoundryVTT/Data";
    private static final Set<String> unique = new HashSet<>();
    public static final Pattern SHOTS_PER_PACK = Pattern.compile("After (\\d*|one) \\w*, the ([\\w\\s]*) must be \\w*\\.");


    public static void main(String[] args) {
        List<String> itemLinks = new ArrayList<>();
        //weapons
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
            entries.addAll(itemExporter.readItemMenuPage(itemLink, true));
            drawProgressBar(i.getAndIncrement() * 100 / size);
        }

        entries.addAll(getManualItems());

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }

    private Collection<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);

        if(doc == null){
            return List.of();
        }

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

        if ("home".equalsIgnoreCase(itemName)) {
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

        Item item = Item.create(itemName)
                .withDescription(content.html())
                .withType(itemType)
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
                .withProvided(Attribute.create(AttributeKey.BASE_ITEM, baseItem))
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, bonusToReflexDefense))
                .withProvided(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, bonusToFortitudeDefense))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, maximumDexterityBonus))
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
                .withProvided(Attribute.create(AttributeKey.PREFIX, getPrefix(itemName)))
                .withProvided(Attribute.create(AttributeKey.SUFFIX, getSuffix(itemName)));

        if (isDroid) {
            item.withProvided(Attribute.create(AttributeKey.DROID_PART, true));
        }
        if (armorType != null) {
            item.withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, armorType));
        }
        if (List.of("Probe", "Instrument", "Tool", "Claw", "Hand").contains(itemName)) {
            item.withProvided(Attribute.create(AttributeKey.APPENDAGES, "1"));
            //printUnique(itemName);
        }
        if ("Stormtrooper Armor".equals(itemName)) {
            item.withProvided(Attribute.create(AttributeKey.PERCEPTION_MODIFIER, 2));
            item.withProvided(Attribute.create(AttributeKey.LOW_LIGHT_VISION, true));
            //response.add(swse.traits.Trait.create("Stormtrooper Perception Bonus").withProvided(Attribute.create("perceptionModifier", 2)).toJSON());
            //response.add(swse.traits.Trait.create("Low-Light Vision").withProvided(Attribute.create("lowLightVision", true)).toJSON());
        }

        jsonObjects.addAll(getItemVariants(item, itemName));

        jsonObjects.add(item);

        return jsonObjects;
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
                variant.withProvided(Attribute.create(AttributeKey.IMMUNITY, "Extreme Cold"));
                variant.withCost("18,000");
            }
            if ("Sandtrooper Armor".equals(variantName)) {
                variant.withProvided(Attribute.create(AttributeKey.IMMUNITY, "Extreme Heat"));
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
                attributes.add(Attribute.create(AttributeKey.TREATED_AS, standardizeTypes(m.group(1))));
            }
        }


        if (text.contains("Power Pack")) {
            Matcher m = SHOTS_PER_PACK.matcher(text);
            while (m.find()) {
                String group = m.group(2);
                if (group.equals("one")) {
                    group = "1";
                }
                attributes.add(Attribute.create(AttributeKey.AMMO, group + ":" + m.group(1)));
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
            attributes.add(Mode.create("Self-Built", List.of(Attribute.create(AttributeKey.TO_HIT_MODIFIER, 1))));
        }

        if ("Energy Lance".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:50"));
        }
        if ("E-5s Blaster Rifle".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:5"));
        }
        if ("SG-4 Blaster Rifle".equals(itemName)) {
            attributes.add(Mode.create("Blaster", "POWER", List.of(Attribute.create(AttributeKey.AMMO, "Power Pack:50"))));
            attributes.add(Mode.create("Harpoon", "POWER", List.of(Attribute.create(AttributeKey.AMMO, "Harpoon:1"))));
        }
        if ("HB-9 Blaster Rifle".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:25"));
            attributes.add(Attribute.create(AttributeKey.AMMO, "Gas Canister:200"));
        }
        if ("Commando Special Rifle".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:25"));
        }
        if ("Variable Blaster Rifle".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:500"));
            attributes.add(Mode.create("3d4", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d4"))));
            attributes.add(Mode.create("3d6", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d6"), Attribute.create(AttributeKey.AMMO_USE_MULTIPLIER, "5"))));
            attributes.add(Mode.create("3d8", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d8"), Attribute.create(AttributeKey.AMMO_USE_MULTIPLIER, "10"))));
        }
        if ("Heavy Variable Blaster Rifle".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:500"));
            attributes.add(Mode.create("Ascension gun", "POWER", List.of(Attribute.create(AttributeKey.AMMO, "Syntherope:2"))));
            attributes.add(Mode.create("3d6", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d6"))));
            attributes.add(Mode.create("3d8", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d8"), Attribute.create(AttributeKey.AMMO_USE_MULTIPLIER, "10"))));
            attributes.add(Mode.create("3d10", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d10"), Attribute.create(AttributeKey.AMMO_USE_MULTIPLIER, "20"))));
        }
        if ("Sonic Blaster".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Proprietary Power Pack:15:11:0.2"));
        }
        if ("Heavy Blaster Pistol".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:50"));
        }
        if ("Snap-Shot Blaster Pistol".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:1"));
            attributes.add(Attribute.create(AttributeKey.CONCEALMENT_BONUS, "5"));
        }
        if ("Sidearm Blaster Pistol".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:250"));
        }
        if ("Gee-Tech 12 Defender".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:2"));
            attributes.add(Attribute.create(AttributeKey.CONCEALMENT_BONUS, "5"));
        }

        if ("Thunderbolt Repeater Blaster".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "-5"));
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:20"));
            attributes.add(Mode.create("Braced", List.of(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "0"))));
        }
        if ("Z-6 Rotary Blaster".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:1"));
            attributes.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "-5"));
            attributes.add(Mode.create("Braced", List.of(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "0"))));
        }
        if ("Retrosaber".equals(itemName)) {
            attributes.add(Mode.create("Overcharge", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "2d10"))));
            attributes.add(Mode.create("Burnout", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "2d4"))));
        }
        if ("Slugthrower Pistol".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Slug Clip:10:40:0.2"));
        }
        if ("Slugthrower Rifle".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Slug Clip:20:40:0.2"));
        }
        if ("WESTAR-M5 Blaster Rifle".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:100"));
            attributes.add(Attribute.create(AttributeKey.AMMO, "Gas Canister:500"));
            attributes.add(Mode.create("Anti-Personnel", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d8"))));
            attributes.add(Mode.create("Anti-Vehicle", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d10"),
                    Attribute.create(AttributeKey.AMMO_USE_MULTIPLIER, "10"), Attribute.create(AttributeKey.PENETRATION, "5"))));
        }
        if ("DC-19 \"Stealth\" Carbine".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:10"));
            attributes.add(Attribute.create(AttributeKey.AMMO, "Stealth Mixture Gas Canister:500:500:0.25"));
        }
        if ("Amban Phase-Pulse Blaster".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:1"));
        }
        if ("Scatter Gun".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "10 Shells:10:20:1"));
            attributes.add(Mode.create("Point-Blank Range", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "3d8"))));
            attributes.add(Mode.create("Short Range", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "2d8"))));
            attributes.add(Mode.create("Medium Range", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "0"))));
            attributes.add(Mode.create("Long Range", "POWER", List.of(Attribute.create(AttributeKey.DAMAGE, "0"))));
        }
        if ("DC-15x Sniper Rifle".equals(itemName)) {
            attributes.add(Attribute.create(AttributeKey.AMMO, "Power Pack:5"));
        }
        if ("DC-17m IWS".equals(itemName)) {
            attributes.add(Mode.create("Blaster Rifle", "POWER", List.of(
                    Attribute.create(AttributeKey.DAMAGE, "3d8"),
                    Attribute.create(AttributeKey.STUN_DAMAGE, "3d8"),
                    Attribute.create(AttributeKey.AMMO, "Power Pack:60"),
                    Attribute.create(AttributeKey.AMMO, "Gas Canister:300")
            ), List.of(MODE_SINGLE_SHOT, MODE_AUTOFIRE)));
            attributes.add(Mode.create("Sniper Rifle", "POWER", List.of(
                    Attribute.create(AttributeKey.DAMAGE, "3d8"),
                    Attribute.create(AttributeKey.AMMO, "Sniper Power Pack:5:100:0.5")
            ), List.of(MODE_SINGLE_SHOT)));
            attributes.add(Mode.create("Anti-Armor", "POWER", List.of(
                    Attribute.create(AttributeKey.DAMAGE, "4d10"),
                    Attribute.create(AttributeKey.AMMO, "Explosive Shell:1:300:1")

            ), List.of(MODE_SINGLE_SHOT)));
            attributes.add(Mode.create("PEP Laser", "POWER", List.of(
                    Attribute.create(AttributeKey.STUN_DAMAGE, "3d6"),
                    Attribute.create(AttributeKey.AMMO, "PEP Cartridge:15:100:0.5")
            ), List.of(MODE_SINGLE_SHOT)));
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

    private static List<Mode> getModes(String rateOfFire, String itemName) {
        List<Mode> modes = new LinkedList<>();
        if (rateOfFire != null && rateOfFire.contains("Autofire")) {
            modes.add(MODE_AUTOFIRE);
        }
        if (rateOfFire != null && rateOfFire.contains("Barrage")) {
            modes.add(MODE_BARRAGE);
        }

        if (rateOfFire != null && rateOfFire.contains("Single-Shot")) {
            modes.add(MODE_SINGLE_SHOT);
        }

        if ("Amphistaff".equalsIgnoreCase(itemName)) {
            modes.add(Mode.create("Quarterstaff", "AMPHISTAFF_FORM", List.of(
                    Attribute.create(AttributeKey.DAMAGE, "1d6/1d6"),
                    Attribute.create(AttributeKey.DAMAGE_TYPE, "Bludgeoning"),
                    Attribute.create(AttributeKey.SPECIAL, List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                            "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                            "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                            "An Amphistaff can only spit venom once every 24 standard hours."
                    ))
            )));
            modes.add(Mode.create("Spear", "AMPHISTAFF_FORM", List.of(
                    Attribute.create(AttributeKey.DAMAGE, "1d8"),
                    Attribute.create(AttributeKey.DAMAGE_TYPE, "Piercing"),
                    Attribute.create(AttributeKey.SPECIAL, List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
                                    "(As a Standard Action). If this ranged attack hits both the target's Reflex Defense " +
                                    "and Fortitude Defense, the target moves -1 Persistent step on the Condition Track. " +
                                    "An Amphistaff can only spit venom once every 24 standard hours."
                            )
                    ))));
            modes.add(Mode.create("Whip", "AMPHISTAFF_FORM", List.of(
                    Attribute.create(AttributeKey.DAMAGE, "1d4"),
                    Attribute.create(AttributeKey.DAMAGE_TYPE, "Piercing"),
                    Attribute.create(AttributeKey.IS_REACH, "2"),
                    Attribute.create(AttributeKey.PROVIDED_ACTION, "Pin"),
                    Attribute.create(AttributeKey.PROVIDED_ACTION, "Trip"),
                    Attribute.create(AttributeKey.SPECIAL, List.of("An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away " +
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
        //printUnique(subType);
        if (List.of("advanced melee weapons", "exotic melee weapons", "simple melee weapons", "simple ranged weapons",
                "exotic ranged weapons", "pistols", "rifles", "lightsabers", "heavy weapons", "grenades", "mines", "explosives").contains(subType.toLowerCase())) {
            return "Weapon";
        } else if (List.of("light armor", "medium armor", "heavy armor", "droid accessories (droid armor)").contains(subType.toLowerCase())) {
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


    @Nonnull
    private static Collection<JSONObject> getManualItems() {
        List<JSONObject> items = new ArrayList<>();

        String sheildGeneratorDescription = "";
        items.add(Item.create("Shield Generator (SR 5)")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withDescription(sheildGeneratorDescription)
                .withProvided(Attribute.create(AttributeKey.SHIELD_RATING, 5))
                .withCost("2500 x Cost Factor")
                .withWeight("(10 x Cost Factor) kg")
                .withType("Equipment")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .toJSON());

        final String SR10Prerequisite = "Only Droids of Small size or larger can be equipped with a SR 10 Generator.";
        items.add(Item.create("Shield Generator (SR 10)")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withDescription(sheildGeneratorDescription)
                .withProvided(Attribute.create(AttributeKey.SHIELD_RATING, 10))
                .withCost("5000 x Cost Factor")
                .withWeight("(20 x Cost Factor) kg")
                .withType("Equipment")
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
        items.add(Item.create("Shield Generator (SR 15)")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withDescription(sheildGeneratorDescription)
                .withProvided(Attribute.create(AttributeKey.SHIELD_RATING, 15))
                .withCost("7500 x Cost Factor")
                .withWeight("(30 x Cost Factor) kg")
                .withType("Equipment")
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
        items.add(Item.create("Shield Generator (SR 20)")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withDescription(sheildGeneratorDescription)
                .withProvided(Attribute.create(AttributeKey.SHIELD_RATING, 20))
                .withCost("10000 x Cost Factor")
                .withWeight("(40 x Cost Factor) kg")
                .withType("Equipment")
                .withSubtype("Droid Accessories (Shield Generator Systems)")
                .withPrerequisite(new OrPrerequisite(SR20Prerequisite,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Translator Unit (DC 20)")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Equipment")
                .withSubtype("Droid Accessories (Translator Units)")
                .withCost("200")
                .withWeight("1 kg")
                .withProvided(Attribute.create(AttributeKey.TRANSLATE_DC, 20)).toJSON());

        items.add(Item.create("Translator Unit (DC 15)")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Equipment")
                .withSubtype("Droid Accessories (Translator Units)")
                .withCost("500")
                .withWeight("2 kg")
                .withProvided(Attribute.create(AttributeKey.TRANSLATE_DC, 15)).toJSON());

        items.add(Item.create("Translator Unit (DC 10)")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Equipment")
                .withSubtype("Droid Accessories (Translator Units)")
                .withCost("1000")
                .withWeight("4 kg")
                .withProvided(Attribute.create(AttributeKey.TRANSLATE_DC, 10)).toJSON());

        items.add(Item.create("Translator Unit (DC 5)")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Equipment")
                .withSubtype("Droid Accessories (Translator Units)")
                .withCost("2000")
                .withWeight("8 kg")
                .withProvided(Attribute.create(AttributeKey.TRANSLATE_DC, 5)).toJSON());


        final String hardenedSystem = "Droids of Large or greater size can be designed to have internal armor and redundant systems that enable it to continue functioning despite heavy damage";
        items.add(Item.create("Hardened Systems x2")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Equipment")
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("1000 x Cost Factor")
                .withWeight("(100 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_HARDENED_MULTIPLIER, 2))
                .withProvided(Attribute.create(AttributeKey.HEALTH_HARDENED_MULTIPLIER, 2))
                .withPrerequisite(new OrPrerequisite(hardenedSystem,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Hardened Systems x3")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Equipment")
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("2500 x Cost Factor")
                .withWeight("(250 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_HARDENED_MULTIPLIER, 3))
                .withProvided(Attribute.create(AttributeKey.HEALTH_HARDENED_MULTIPLIER, 3))
                .withPrerequisite(new OrPrerequisite(hardenedSystem,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Hardened Systems x4")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Equipment")
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("4000 x Cost Factor")
                .withWeight("(400 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_HARDENED_MULTIPLIER, 4))
                .withProvided(Attribute.create(AttributeKey.HEALTH_HARDENED_MULTIPLIER, 4))
                .withPrerequisite(new OrPrerequisite(hardenedSystem,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Hardened Systems x5")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Equipment")
                .withSubtype("Droid Accessories (Hardened Systems)")
                .withCost("6250 x Cost Factor")
                .withWeight("(650 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_HARDENED_MULTIPLIER, 5))
                .withProvided(Attribute.create(AttributeKey.HEALTH_HARDENED_MULTIPLIER, 5))
                .withPrerequisite(new OrPrerequisite(hardenedSystem,
                        List.of(
                                new SimplePrerequisite("Large", "TRAIT", "Large"),
                                new SimplePrerequisite("Huge", "TRAIT", "Huge"),
                                new SimplePrerequisite("Colossal", "TRAIT", "Colossal"),
                                new SimplePrerequisite("Gargantuan", "TRAIT", "Gargantuan")
                        ), 1))
                .toJSON());

        items.add(Item.create("Plasteel Shell")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("400 x Cost Factor")
                .withWeight("(2 x Cost Factor) kg")
                .withAvailability("-")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "2"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "5"))
                .toJSON());

        items.add(Item.create("Quadanium Shell")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("900 x Cost Factor")
                .withWeight("(3 x Cost Factor) kg")
                .withAvailability("-")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "3"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "4"))
                .toJSON());

        items.add(Item.create("Durasteel Shell")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("1600 x Cost Factor")
                .withWeight("(8 x Cost Factor) kg")
                .withAvailability("-")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "4"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "4"))
                .toJSON());

        items.add(Item.create("Quadanium Plating")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("2500 x Cost Factor")
                .withWeight("(10 x Cost Factor) kg")
                .withAvailability("Licensed")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "5"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Durasteel Plating")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Light Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("3600 x Cost Factor")
                .withWeight("(12 x Cost Factor) kg")
                .withAvailability("Licensed")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "6"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Quadanium Battle Armor")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Medium Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("4900 x Cost Factor")
                .withWeight("(7 x Cost Factor) kg")
                .withAvailability("Restricted")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "7"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Duranium Plating")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Medium Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("6400 x Cost Factor")
                .withWeight("(16 x Cost Factor) kg")
                .withAvailability("Restricted")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "8"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "2"))
                .toJSON());

        items.add(Item.create("Durasteel Battle Armor")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Medium Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("9600 x Cost Factor")
                .withWeight("(8 x Cost Factor) kg")
                .withAvailability("Restricted")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "8"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Mandalorian Steel Shell")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Heavy Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("8100 x Cost Factor")
                .withWeight("(9 x Cost Factor) kg")
                .withAvailability("Military, Rare")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "9"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "3"))
                .toJSON());

        items.add(Item.create("Duranium Battle Armor")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Heavy Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("10000 x Cost Factor")
                .withWeight("(10 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "10"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "2"))
                .toJSON());

        items.add(Item.create("Neutronium Plating")
                .withProvided(Attribute.create(AttributeKey.DROID_PART, true))
                .withType("Armor")
                .withProvided(Attribute.create(AttributeKey.ARMOR_TYPE, "Heavy Armor"))
                .withSubtype("Droid Accessories (Droid Armor)")
                .withCost("12100 x Cost Factor")
                .withWeight("(20 x Cost Factor) kg")
                .withAvailability("Military")
                .withProvided(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "11"))
                .withProvided(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_BONUS, "1"))
                .toJSON());
        return items;
    }

}
