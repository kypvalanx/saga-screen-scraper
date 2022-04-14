package swse.templates;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.BaseExporter;
import swse.common.Choice;
import swse.common.ItemType;
import swse.common.JSONy;
import swse.common.Option;
import swse.common.ProvidedItem;
import swse.item.Mode;
import swse.prerequisite.AndPrerequisite;
import static swse.prerequisite.NotPrerequisite.not;
import static swse.prerequisite.OrPrerequisite.or;
import swse.prerequisite.Prerequisite;
import static swse.prerequisite.SimplePrerequisite.simple;

public class TemplateExporter extends BaseExporter {
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\templates.csv";
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\templates.json";
    private static int num = 0;
    private static Set<String> allPrerequisites = new HashSet<String>();
    public static final Pattern TEMPLATE_PREFIX_PATTERN = Pattern.compile("([\\w\\s\\d]*) (?:Droid|General|Weapon|Armor) Template");

    public static void main(String[] args) {
        Map<String, List<String>> templateLinks = new HashMap<>();
        //templateLinks.put("/wiki/Droid_Templates", List.of("droid"));
        templateLinks.put("/wiki/Weapon_Templates", List.of("weapon"));
        templateLinks.put("/wiki/Armor_Templates", List.of("armor"));
        templateLinks.put("/wiki/Vehicle_Templates", List.of("vehicle"));
        //templateLinks.put("/wiki/General_Templates", List.of());
        final boolean overwrite = false;

        final TemplateExporter templateExporter = new TemplateExporter();
        List<JSONObject> entries = new ArrayList<>();
        for (Map.Entry<String, List<String>> templateLink : templateLinks.entrySet()) {
            entries.addAll(templateExporter.readItemMenuPage(templateLink.getKey(), templateLink.getValue(), overwrite));
        }

        entries.addAll(templateExporter.parseItem("/wiki/Advanced_Droid_Template", List.of("droid"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Archaic_Droid_Template", List.of("droid"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Converted_Droid_Template", List.of("droid"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Junker_Droid_Template", List.of("droid"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));

        entries.addAll(templateExporter.parseItem("/wiki/Arkanian_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Cinnagaran_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Cortosis_Weave_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Phrik_Alloy_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Echani_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/GenoHaradan_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Iridonian_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Krath_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Mandalorian_General_Template", List.of("-"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Massassi_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Prototype_General_Template", List.of("droid", "weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));
        entries.addAll(templateExporter.parseItem("/wiki/Verpine_General_Template", List.of("weapon", "armor"), overwrite).stream().map(ability -> ability.toJSON()).collect(Collectors.toList()));

        ArrayList<String> strings = Lists.newArrayList(allPrerequisites);
        strings.sort(String::compareToIgnoreCase);
        for (String field : strings) {
            System.out.println(field);
        }

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
        //writeToCSV(new File(OUTPUT), entries);
    }


    private Collection<? extends JSONObject> readItemMenuPage(String itemPageLink, List<String> types, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null) {
            return new ArrayList<>();
        }
        Element body = doc.body();

        Elements tables = body.select("table.wikitable");

        List<String> hrefs = new ArrayList<>();
        tables.forEach(table ->
        {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row ->
            {
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


        return hrefs.stream().flatMap(itemLink -> parseItem(itemLink, types, overwrite).stream()).map(item -> item.toJSON()).collect(Collectors.toList());

    }

    protected List<JSONy> parseItem(String itemLink, List<String> types, boolean overwrite) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }
        String itemName = getItemName(doc);

        if ("home".equals(itemName)) {
            return new ArrayList<>();
        }
        if(itemLink.contains("Cortosis")){
            itemName = "Cortosis Weave General Template";
        }
        if(itemLink.contains("Phrik")){
            itemName = "Phrik Alloy General Template";
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        content.select("img,figure").remove();

        String cost = getCost(content);

        String templateType = getTemplateType(content);

        List<JSONy> templates = new ArrayList<>();

        for(String type: types){

            final String resolvedName = itemName + (types.size() > 1 ? " (" + type + ")" : "");
            templates.add(Template.create(resolvedName)
                    .withCost(cost)
                    .withProvided(Attribute.create(AttributeKey.ITEM_MOD, "true"))
                    .withSubtype(type.equals("droid")? "Droid Templates" : templateType)
                    .withPrerequisite(getItemPrerequisite(content, type))
                    //.withProvided(getAttributes(content))
                    .withProvided(getManualAttributes(resolvedName))
                    .withDescription(content)
                    .withPrefix(getPrefix(itemName))
                    .withSuffix(getSuffix(itemName)));
        }

        return templates;
    }

    private static Collection<?> getAttributes(Element content) {
        List<Object> response = new LinkedList<>();

        for(Element child : content.children()){
            if(child.tag().equals(Tag.valueOf("ul"))){
                response.addAll(getAttributes(child));
                continue;
            }
            String text = child.text();

            if(text.contains(": ")){
                String[] toks = text.split(": ");

                switch (toks[0]){
                    case "Reference Book":
                        break;
                    case "Template Type":
                        break;
                    case "Cost":
                        break;
                    case "Challenge Level (CL)":
                        getChallengeLevel(response, toks);
                        break;

                    case "Shield Rating (SR)":
                        if(toks[1].equals("Any time the SR value of an Archaic Vehicle is exceeded, there is a 50% chance they fail entirely. They may be recharged normally (Using the Recharge Shields Action).")){
                            response.add(Attribute.create(AttributeKey.SHIELD_FAILURE_CHANCE, "50%"));
                        } else if(toks[1].equals("Increase the ship's SR by an amount based on its size; Huge +5, Gargantuan +10, Colossal +20, Colossal (Frigate) +30, Colossal (Cruiser) +40, Colossal (Station) +50. If the base ship doesn't have Starship Shields normally, give it shields with a total SR of double the given bonus +10.")){

                        }
                        break;
                    case "Hit Points":
                        if(toks[1].equals("Increase the ship's total Hit Points by 25% (Rounded up).")){
                            response.add(Attribute.create(AttributeKey.HIT_POINT_EQ, "*1.5"));
                        } else if(toks[1].equals("Unlike a normal Vehicle, Mechanics checks cannot be made to Repair Hit Point damage to a Living Vehicle. The Treat Injury skill can be used to Repair the Living Vehicle's lost Hit Points.")){
                            response.add(Attribute.create(AttributeKey.LIVING_VEHICLE, "true"));
                        }
                        break;
                    case "Strength":
                        if(toks[1].equals("Increase the ship's Strength by the following bonus based on its size; Huge +2, Gargantuan +6, Colossal +10, Colossal (Frigate) +16, Colossal (Cruiser) +22, Colossal (Station) +36. Increase the ship's Fortitude Defense, Damage Threshold, and Grapple modifier to represent the new Strength modifier."))
                        {
                        }
                            break;
                    case "Dexterity":
                        if(toks[1].equals("Increase the Dexterity of Huge and Gargantuan Starships by 8, Colossal (Station) ships by 2, and all others by 4. Increase the ship's Initiative, Reflex Defense, and Pilot check modifier to represent the new Dexterity modifier.\n"))
                    {

                    }
                        break;
                    case "Intelligence":

                        break;
                    case "Weapon Systems":
                        if(toks[1].equals("Increase the damage dealt by all Weapon Systems by 1 die."))
                        {
                        } else if(toks[1].equals("The Weapon Systems of an Archaic Vehicle can't score Critical Hits against nonarchaic vessels. All attack rolls made by Archaic Vehicles against modern vessels take a -2 penalty, since older scanners have difficulty tracking modern ships."))
                        {
                        }
                        break;
                    case "Damage Threshold":
                        System.out.println(toks[1]);
                    case "Initiative":
                    case "Abilities":
                    case "System Failures":
                    case "Patchwork":
                    case "Speed":
                    case "Random Benefit/Drawback":
                    case "Maintenance Requirement":
                    case "Base Attack Bonus":
                    case "Availability":
                    case "Repairs":
                    case "Refitting":
                    case "Shield Rating":
                    case "Natural Healing":
                    case "Dovin Basals":
                    case "Tractor Beams":
                    case "Maintenance":
                    case "Manufactured By":
                    case "Applicable To":
                    case "Weapons":
                    case "Energy Cells and Power Packs":
                    case "Energy Cell":
                    case "Homebrew Reference Book":
                    case "Armor":
                    case "Armors":
                    case "Special":
                    case "Effects":
                    default:
                        //printUnique("case \""+toks[0] + "\":");
                }

            }

        }

        return response;
    }

    private static void getChallengeLevel(List<Object> response, String[] toks) {
        switch(toks[1]){
            case "The CL of an Advanced Vehicle is 125% of it's normal value (Rounded up).":
                response.add(Attribute.create(AttributeKey.CHALLENGE_LEVEL, "*1.25"));
                break;
            case "The CL of an Archaic Vehicle is 2/3 it's normal value (Rounded up).":
                response.add(Attribute.create(AttributeKey.CHALLENGE_LEVEL, "*2 /3"));
                break;
            case "Reduce the CL of a Junker by 20% (Rounded down).":
                response.add(Attribute.create(AttributeKey.CHALLENGE_LEVEL, "*0.8"));
                break;
            case "Reduce the base Vehicle's CL by -2.":
                response.add(Attribute.create(AttributeKey.CHALLENGE_LEVEL, "-2"));
                break;
            case "The CL of a Living Vehicle is equal to its normal value +2.":
                response.add(Attribute.create(AttributeKey.CHALLENGE_LEVEL, "+2"));
                break;
        }
    }

    private static Collection<?> getManualAttributes(String resolvedName) {
        List<Object> response = new LinkedList<>();
        switch(resolvedName){
            case "Advanced Vehicle Template":
                response.add(Attribute.create(AttributeKey.CHALLENGE_LEVEL, "+25%"));

                response.add(ProvidedItem.create("Advanced Shields (+5)", ItemType.TRAIT, "TRAIT:Huge"));
                response.add(ProvidedItem.create("Advanced Shields (+10)", ItemType.TRAIT, "TRAIT:Gargantuan"));
                response.add(ProvidedItem.create("Advanced Shields (+20)", ItemType.TRAIT, "TRAIT:Colossal"));
                response.add(ProvidedItem.create("Advanced Shields (+30)", ItemType.TRAIT, "TRAIT:Colossal (Frigate)"));
                response.add(ProvidedItem.create("Advanced Shields (+40)", ItemType.TRAIT, "TRAIT:Colossal (Cruiser)"));
                response.add(ProvidedItem.create("Advanced Shields (+50)", ItemType.TRAIT, "TRAIT:Colossal (Station)"));

                response.add(ProvidedItem.create("Strength (+2)", ItemType.TRAIT, "TRAIT:Huge"));
                response.add(ProvidedItem.create("Strength (+6)", ItemType.TRAIT, "TRAIT:Gargantuan"));
                response.add(ProvidedItem.create("Strength (+10)", ItemType.TRAIT, "TRAIT:Colossal"));
                response.add(ProvidedItem.create("Strength (+16)", ItemType.TRAIT, "TRAIT:Colossal (Frigate)"));
                response.add(ProvidedItem.create("Strength (+22)", ItemType.TRAIT, "TRAIT:Colossal (Cruiser)"));
                response.add(ProvidedItem.create("Strength (+36)", ItemType.TRAIT, "TRAIT:Colossal (Station)"));

                response.add(ProvidedItem.create("Dexterity (+8)", ItemType.TRAIT, "TRAIT:Huge"));
                response.add(ProvidedItem.create("Dexterity (+8)", ItemType.TRAIT, "TRAIT:Gargantuan"));
                response.add(ProvidedItem.create("Dexterity (+4)", ItemType.TRAIT, "TRAIT:Colossal"));
                response.add(ProvidedItem.create("Dexterity (+4)", ItemType.TRAIT, "TRAIT:Colossal (Frigate)"));
                response.add(ProvidedItem.create("Dexterity (+4)", ItemType.TRAIT, "TRAIT:Colossal (Cruiser)"));
                response.add(ProvidedItem.create("Dexterity (+2)", ItemType.TRAIT, "TRAIT:Colossal (Station)"));

                response.add(ProvidedItem.create("Intelligence (+4)", ItemType.TRAIT));

                response.add(Attribute.create(AttributeKey.DAMAGE_BONUS_VEHICLE, 1));

                break;
            case "Archaic Vehicle Template":
                response.add(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_BONUS, "-50%"));
                response.add(Attribute.create(AttributeKey.SHIELD_FAILURE_CHANCE, "50%"));
                response.add(Attribute.create(AttributeKey.CRITICAL_HITS, false));
                response.add(Attribute.create(AttributeKey.CRITICAL_HITS, true).withModifier("Archaic Vehicles"));
                response.add(Attribute.create(AttributeKey.ATTACK_BONUS_VEHICLE, -2));
                break;
            case "Junker Vehicle Template":
                response.add(Attribute.create(AttributeKey.INITIATIVE_BONUS, "-5"));
                response.add(ProvidedItem.create("Intelligence (-4)", ItemType.TRAIT));
                response.add(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT));
                response.add(ProvidedItem.create("Strength (-4)", ItemType.TRAIT));
                response.add(ProvidedItem.create("System Failures", ItemType.TRAIT));
                response.add(ProvidedItem.create("Patchwork", ItemType.TRAIT));
                break;
            case "Prototype Vehicle Template":

                response.add(ProvidedItem.create("Damage Threshold (-1)", ItemType.TRAIT, "TRAIT:Large"));
                response.add(ProvidedItem.create("Damage Threshold (-2)", ItemType.TRAIT, "TRAIT:Huge"));
                response.add(ProvidedItem.create("Damage Threshold (-5)", ItemType.TRAIT, "TRAIT:Gargantuan"));
                response.add(ProvidedItem.create("Damage Threshold (-10)", ItemType.TRAIT, or("Colossal or Larger",
                        simple("Colossal", "TRAIT", "Colossal"),
                        simple("Colossal (Frigate)", "TRAIT", "Colossal (Frigate)"),
                        simple("Colossal (Cruiser)", "TRAIT", "Colossal (Cruiser)"),
                        simple("Colossal (Station)", "TRAIT", "Colossal (Station)"))));

                response.add(ProvidedItem.create("Strength (+2)", ItemType.TRAIT, "CONDITION:0"));
                response.add(ProvidedItem.create("Dexterity (+2)", ItemType.TRAIT, "CONDITION:0"));
                response.add(ProvidedItem.create("Intelligence (+2)", ItemType.TRAIT, "CONDITION:0"));
                response.add(Attribute.create(AttributeKey.SPEED_STARSHIP_SCALE, 1));
                response.add(ProvidedItem.create("Maintenance Requirement", ItemType.TRAIT));

                response.add(Choice.create("Prototype Benefit")
                        .withRollOption("d9")
                        .withOption("Starship Armor", new Option().withRollRange("1").withProvidedItem(
                                ProvidedItem.create("Starship Armor (+2)", ItemType.TRAIT, "CONDITION:0")))
                        .withOption("Maximum Hit Points", new Option().withRollRange("2").withProvidedItem(
                                ProvidedItem.create("Maximum Hit Points (+10%)", ItemType.TRAIT, "CONDITION:0")))
                        .withOption("Shield Rating", new Option().withRollRange("3").withProvidedItem(
                                ProvidedItem.create("Shield Rating (+25%)", ItemType.TRAIT, "CONDITION:0")))
                        .withOption("Starship Scale Speed ", new Option().withRollRange("4").withProvidedItem(
                                ProvidedItem.create("Starship Scale Speed (+1)", ItemType.TRAIT, "CONDITION:0")))
                        .withOption("Required Crew", new Option().withRollRange("5").withProvidedItem(
                                ProvidedItem.create("Required Crew (-70%)", ItemType.TRAIT, "CONDITION:0")))
                        .withOption("Maximum Passengers", new Option().withRollRange("6").withProvidedItem(
                                ProvidedItem.create("Maximum Passengers (+50%)", ItemType.TRAIT, "CONDITION:0")))
                        .withOption("Cargo Capacity", new Option().withRollRange("7").withProvidedItem(
                                ProvidedItem.create("Cargo Capacity (+50%)", ItemType.TRAIT, "CONDITION:0")))
                        .withOption("Carried Craft", new Option().withRollRange("8").withProvidedItem(
                                ProvidedItem.create("Carried Craft (+25% minimum 1)", ItemType.TRAIT, "CONDITION:0")))
                        .withOption("Weapon System", new Option().withRollRange("9").withProvidedItem(
                                ProvidedItem.create("Weapon System (+1)", ItemType.TRAIT, "CONDITION:0")))
                );

                response.add(Choice.create("Prototype Drawback")
                        .withRollOption("d9")
                        .withOption("Starship Armor", new Option().withRollRange("1").withProvidedItem(
                                ProvidedItem.create("Starship Armor (-2)", ItemType.TRAIT)))
                        .withOption("Maximum Hit Points", new Option().withRollRange("2").withProvidedItem(
                                ProvidedItem.create("Maximum Hit Points (-10%)", ItemType.TRAIT)))
                        .withOption("Shield Rating", new Option().withRollRange("3").withProvidedItem(
                                ProvidedItem.create("Shield Rating (-25%)", ItemType.TRAIT)))
                        .withOption("Starship Scale Speed", new Option().withRollRange("4").withProvidedItem(
                                ProvidedItem.create("Starship Scale Speed (-1)", ItemType.TRAIT)))
                        .withOption("Required Crew", new Option().withRollRange("5").withProvidedItem(
                                ProvidedItem.create("Required Crew (+30%)", ItemType.TRAIT)))
                        .withOption("Maximum Passengers", new Option().withRollRange("6").withProvidedItem(
                                ProvidedItem.create("Maximum Passengers (-50%)", ItemType.TRAIT)))
                        .withOption("Cargo Capacity", new Option().withRollRange("7").withProvidedItem(
                                ProvidedItem.create("Cargo Capacity (-50%)", ItemType.TRAIT)))
                        .withOption("Carried Craft", new Option().withRollRange("8").withProvidedItem(
                                ProvidedItem.create("Carried Craft (-25% minimum 1)", ItemType.TRAIT)))
                        .withOption("Weapon System", new Option().withRollRange("9").withProvidedItem(
                                ProvidedItem.create("Weapon System (-1)", ItemType.TRAIT)))
                );


                break;
            case "Antiquated Vehicle Template":
                response.add(ProvidedItem.create("Damage Threshold (-25%)", ItemType.TRAIT));
                response.add(ProvidedItem.create("Base Attack Bonus (-2)", ItemType.TRAIT));
                response.add(Attribute.create(AttributeKey.AVAILABILITY, "Rare"));
                response.add(Attribute.create(AttributeKey.COST, "(USED OR 60%)"));
                response.add(Attribute.create(AttributeKey.SPECIAL, "Repairs: Antiquated Vehicles do not use modern technology and are likely to be unfamiliar to most ship engineers. The base DC of any Mechanics checks made to Build Object or Repair increase by +5 when associated with an Antiquated Vehicle."));
                response.add(Attribute.create(AttributeKey.SPECIAL, "Refitting: An Antiquated Vehicle can be Refitted. Apply the Refitted Vehicle Template to the base Vehicle instead of the Antiquated Vehicle Template."));
                break;
            case "Living Vehicle Template": break;
            case "Refitted Vehicle Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "Repairs: Since Refitted Vehicles combine antiquated technology with modern components, sometimes the Vehicle must be treated gently and repairs can be challenging. A Refitted Vehicle increases the base DC of any Mechanics checks made to Repair it by +5."));
                response.add(Attribute.create(AttributeKey.SPECIAL, "Maintenance: Unlike normal Vehicles, Refitted Vehicles must be given extra maintenance to ensure that they continue to function as expected. Once per week the Refitted Vehicle must receive regular maintenance, which requires 1 hour of work (no Mechanics check required, though a Tool Kit is necessary to perform the maintenance).\n" +
                        "Failure to perform this maintenance moves the Refitted Vehicle -1 Persistent step down the Condition Track for each day it does not receive maintenance after one week."));
                response.add(Attribute.create(AttributeKey.AVAILABILITY, "Rare"));
                response.add(Attribute.create(AttributeKey.COST, "(USED + 20%)"));
                break;
            case "Baragwin Weapon Template":
                response.add(Attribute.create(AttributeKey.CRITICAL_HIT_POSTMULTIPLIER_BONUS_DIE, 1));
                response.add(Attribute.create(AttributeKey.AVAILABILITY, "Illegal"));
                break;
            case "Bothan Weapon Template":
                response.add(Attribute.create(AttributeKey.BONUS_DAMAGE_DIE, "1").withPrerequisite(or(simple("damage type: ion", "DAMAGE_TYPE", "Ion"), simple("damage type: stun", "DAMAGE_TYPE", "Stun"))));
                response.add(Attribute.create(AttributeKey.BONUS_DAMAGE_DIE, "-1").withPrerequisite(not(or(simple("damage type: ion", "DAMAGE_TYPE", "Ion"), simple("damage type: stun", "DAMAGE_TYPE", "Stun")))));
                break;
            case "Dashade Weapon Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "A target moved down the Condition Track by an attack from a Dashade Manufacture Weapon can only spend 2 Swift Actions per turn to Recover. This effect lasts until the target return to its normal state (all conditions removed)."));
                response.add(Attribute.create(AttributeKey.DAMAGE, "-2"));
                break;
            case "Gand Weapon Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "If a Gand Manufacture Weapon moves a target down the Condition Track with it's Stun damage, that target has it's speed halved until it returns to its normal state (all conditions removed); a target that is -4 steps down the Condition Track (at the -10 step) is Immobilized instead."));
                response.add(Attribute.create(AttributeKey.DAMAGE, "-2"));
                response.add(Attribute.create(AttributeKey.EXOTIC_WEAPON, "true"));
                break;
            case "Quick Draw Weapon Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "A Quick Draw Weapon wielder that possesses the Quick Draw feat can draw or holster the weapon once per round as a Free Action."));
                break;
            case "Rakatan Weapon Template":
                response.add(Attribute.create(AttributeKey.BONUS_DAMAGE_DIE_TYPE, "1"));
                response.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "1"));
                response.add(Attribute.create(AttributeKey.EXOTIC_WEAPON, "true"));
                break;
            case "Sith Alchemical Weapon Template":
                response.add(Attribute.create(AttributeKey.BLOCKS_LIGHTSABER, "true"));
                response.add(Attribute.create(AttributeKey.ALLOWS_BLOCK, "true"));
                response.add(Attribute.create(AttributeKey.ALLOWS_DEFLECT, "true"));
                response.add(Attribute.create(AttributeKey.ALLOWS_REDIRECT_SHOT, "true"));
                response.add(Attribute.create(AttributeKey.SPECIAL, "A character proficient with a Sith Alchemical Weapon can spend a Force Point as a Swift Action to gain a bonus to damage rolls with the Sith Alchemical Weapon equal to his or her Dark Side Score. This bonus to damage applies to the next attack made with the Sith Alchemical Weapon before the end of the encounter, and activating this ability increases the wielder's Dark Side Score by 1."));
                break;
            case "Antiquated Weapon Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "Antiquated Energy Cells and Antiquated Power Packs for Antiquated Weapons cost 50% more than a standard Energy Cell or Power Pack. Unless otherwise specified, an Antiquated Weapon cannot use standard Energy Cells or Power Packs, only Antiquated Energy Cells or Antiquated Power Packs."));
                response.add(Attribute.create(AttributeKey.SPECIAL, "Antiquated Weapons do not use modern technology and are likely to be unfamiliar to most weaponsmiths. An Antiquated Weapon increases the base DC of any Mechanics check made to build or repair the Weapon by +5."));
                response.add(Attribute.create(AttributeKey.SPECIAL, "An Antiquated Weapon can be Refitted. Apply the Refitted Weapon Template to the base Weapon instead of the Antiquated Weapon Template."));
                        response.add(Attribute.create(AttributeKey.AVAILABILITY, "Rare"));
                break;
            case "Refitted Weapon Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "A Refitted Weapon can use standard Energy Cells and Power Packs. Additionally, weapons that use Power Packs to provide ammunition have their number of shots that can be fired before the Power Pack must be replaced increased by 10%."));
                response.add(Attribute.create(AttributeKey.SPECIAL, "Since Refitted Weapons combine antiquated technology with modern components, sometimes the weapon must be treated gently and repairs can be challenging. A Refitted Weapon increases the base DC of any Mechanics check made to build or repair the Weapon by +5."));
                response.add(Attribute.create(AttributeKey.SPECIAL, "Unlike normal weapons, Refitted Weapons require extra maintenance to ensure that they continue to function as expected. Once per week the weapon must receive regular maintenance, which requires 1 hour or work (no Mechanics check required, though a Tool Kit is necessary to perform the maintenance).\n" +
                        "\n" +
                        "Failure to perform this maintenance move the weapon -1 Persistent step down the Condition Track for each day it does not receive maintenance after one week."));
                break;
            case "Sawed-Off Weapon Template":
                response.add(Attribute.create(AttributeKey.SIZE_BONUS, "-1"));
                response.add(Attribute.create(AttributeKey.RANGE_MULTIPLIER, "/2"));
                break;
            case "Mandalorian Armor Template":
                response.add(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, "+1"));
                response.add(Attribute.create(AttributeKey.SPECIAL, "Additionally, if the wearer has Damage Reduction, they can apply its benefit against attacks made by Lightsabers."));
                response.add(Attribute.create(AttributeKey.SPECIAL, "In some eras, particularly during The Mandalorian Wars, the armor alone grants favorable circumstances on Persuasion checks made to Intimidate."));

                break;
            case "Bonadan-Alloy Armor Template":
                response.add(Attribute.create(AttributeKey.DAMAGE_REDUCTION, "2").withModifier("Slashing"));
                response.add(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, "-1"));
                break;
            case "Bronzium Cast Armor Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "Once per encounter, the wearer of a suit of Bronzium Cast Armor can redirect the damage from a successful incoming attack to the armor; the Bronzium Cast Armor takes the damage from the attack (armor has the same DR, Hit Points, Damage Threshold, and break DC as a weapon of the same size category as the wearer (see Attack an Object)).\n" +
                        "\n" +
                        "If the Bronzium Cast Armor is disabled or destroyed, it no longer provides any bonuses to the wearer. A suit of armor pushed down the Condition Track imposes its condition penalties to the wearer's Reflex Defense only."));
                break;
            case "Durasteel Cast Armor Template":
                response.add(Attribute.create(AttributeKey.DAMAGE_REDUCTION, "1"));
                response.add(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, "-1"));
                break;
            case "Environmental Bastion Armor Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "Environmental Bastion Armor acts as a sealed Space Suit, providing a number of hours of breathable air to the wielder equal to the armor's Equipment bonus to Fortitude Defense."));
                break;
            case "Eriadun Armor Template":
                response.add(Attribute.create(AttributeKey.DAMAGE_REDUCTION, "5").withModifier("Stun"));
                response.add(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, "-1"));
                break;
            case "Stygian-Triprismatic Polymer Armor Template":
                response.add(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, "+1"));
                response.add(Attribute.create(AttributeKey.STEALTH_BONUS, "+2").withModifier("electronic sensors"));
                break;
            case "Advanced Droid Template":
                response.add(Attribute.create(AttributeKey.BONUS_SPECIAL_TRAIT, 1));
                response.add(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_BONUS, "5"));
                response.add(Attribute.create(AttributeKey.SPEED_BONUS, "1"));
                response.add(Attribute.create(AttributeKey.COST, "*2"));
                response.add(Attribute.create(AttributeKey.SPECIAL, "The repair and maintenance costs associated with an Advanced Droid are doubled, and any repair DCs are increased by 5."));
                break;
            case "Archaic Droid Template":
                response.add(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_BONUS, "-5"));
                response.add(Attribute.create(AttributeKey.COST, "/2"));
                response.add(Attribute.create(AttributeKey.SPECIAL, "The DC for all Mechanics and Use Computer checks used to repair the Archaic Droid are increased by 10. Additionally, any built-in weaponry on an Archaic Droid is incapable of scoring a Critical Hit against non-archaic Droids and equipment."));
                break;
            case "Converted Droid Template":
                response.add(Attribute.create(AttributeKey.BONUS_DROID_QUIRK, 1));
                response.add(Attribute.create(AttributeKey.SPECIAL, "The Droid is treated as being the Degree of the Droid Chassis its been converted to, and it gains the bonus equipment, Ability Modifiers, and bonus Feats associated with the Droid Chassis."));
                break;
            case "Junker Droid Template":
                response.add(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_BONUS, "-5"));
                response.add(Attribute.create(AttributeKey.SPECIAL, "must develop a quirk (See Droid Quirks) each time the Droid takes damage that exceeds its Damage Threshold. The DC of the skill check associated with repairing that quirk is also reduced by 5, and the repair costs are reduced by 50%."));
                break;
            case "Arkanian General Template (weapon)":
                response.add(Attribute.create(AttributeKey.DAMAGE_TYPE, "Fire"));
                response.add(Attribute.create(AttributeKey.OVERHEAT_LIMIT, "1"));
                response.add(Attribute.create(AttributeKey.COOLDOWN_TIME, "1"));
                break;
            case "Arkanian General Template (armor)":
                response.add(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, "x2").withModifier("Extreme Cold"));
                response.add(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, "x0").withModifier("Extreme Heat"));
            break;
            case "Cinnagaran General Template (weapon)":
                response.add(Attribute.create(AttributeKey.DAMAGE_TYPE, "Bludgeoning"));
                break;
            case "Cinnagaran General Template (armor)":
                response.add(Attribute.create(AttributeKey.DAMAGE_REDUCTION, "5").withModifier("Sonic"));
                response.add(Attribute.create(AttributeKey.MAXIMUM_DEXTERITY_MODIFIER, "-1"));
                break;
            case "Cortosis Weave General Template (weapon)":
            case "Cortosis Weave General Template (armor)":
            case "Phrik Alloy General Template (weapon)":
            case "Phrik Alloy General Template (armor)":
                response.add(Attribute.create(AttributeKey.BLOCKS_LIGHTSABER, "true"));
                response.add(Attribute.create(AttributeKey.AVAILABILITY, "Rare").withModifier("EXCEPT:ERA:Old Republic Era")); //TODO disable for old republic era
                break;
            case "Echani General Template (weapon)": 
                response.add(Attribute.create(AttributeKey.ACTION, "Once per encounter the wielder of an Echani Manufacture Weapon can declare that the critical range of their weapon is increased by one (thus scoring a Critical Hit on a Natural 19 or Natural 20 on most weapons). The use of this ability can be declared after the attack roll is made, but before the attack is resolved."));
                response.add(Attribute.create(AttributeKey.HP_WEAPON, "*0.5"));
                break;
            case "Echani General Template (armor)":
                response.add(Attribute.create(AttributeKey.ACTION, "once per encounter the wearer can move at their normal speed (not the adjusted speed for wearing the Armor) for one round"));
                response.add(Attribute.create(AttributeKey.REFLEX_DEFENSE_BONUS_ARMOR, -1));
                break;
            case "GenoHaradan General Template (weapon)":
                response.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, 2).withModifier("to the attack roll of a Contact Poison applied to the weapon"));
                response.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, 1).withModifier("attack rolls with weapons set to Stun (for Ranged Weapons)"));
                response.add(Attribute.create(AttributeKey.FRAGILE, "true"));
                break;
            case "GenoHaradan General Template (armor)":
                response.add(Attribute.create(AttributeKey.STEALTH_BONUS, 2));
                response.add(Attribute.create(AttributeKey.FORTITUDE_DEFENSE_BONUS_EQUIPMENT, -1));
                break;
            case "Iridonian General Template (weapon)":
                response.add(Attribute.create(AttributeKey.POWER_ATTACK_BONUS_DAMAGE, 2));
                response.add(Attribute.create(AttributeKey.POWER_BLAST_BONUS_DAMAGE, 2));
                response.add(Attribute.create(AttributeKey.AVAILABILITY, "Illegal"));
                break;
            case "Iridonian General Template (armor)":
                response.add(Attribute.create(AttributeKey.DAMAGE_REDUCTION, "2").withModifier("Bludgeoning"));
                response.add(Attribute.create(AttributeKey.AVAILABILITY, "Illegal"));
                break;
            case "Krath General Template (weapon)": 
                response.add(Attribute.create(AttributeKey.DARKSIDE_TAINT, 1));
                response.add(Attribute.create(AttributeKey.SPECIAL, "If an attack roll with a Krath Manufacture Weapon equals or exceeds the target's Fortitude Defense as well as its Reflex Defense, the target takes 1d4 damage at the beginning of it's next turn."));
                break;
            case "Krath General Template (armor)":
                response.add(Attribute.create(AttributeKey.DARKSIDE_TAINT, 1)); 
                response.add(Attribute.create(AttributeKey.DAMAGE_REDUCTION, "2").withModifier("Energy"));
                break;
            case "Mandalorian General Template":
                response.add(Attribute.create(AttributeKey.SPECIAL, "Any character making a Mechanics check to repair or modify a Mandalorian Weapon or Mandalorian Armor gains a +5 Equipment bonus on the check."));
                response.add(Attribute.create(AttributeKey.SPECIAL, "If a Mandalorian Manufacture item is disabled, all of the Weapon and Armor Accessories to that item are destroyed (and must be purchased anew, not merely repaired)."));
                break;
            case "Massassi General Template (weapon)":
                response.add(Attribute.create(AttributeKey.CRITICAL_HIT_PREMULTIPLIER_BONUS, "@STR_MOD"));
                //response.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "-2"));
                response.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "-2").withParentPrerequisite(not(Prerequisite.create("Strength 15"))).withPrerequisite(simple("Medium", "WEAPON_SIZE", "Medium")));
                response.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "-2").withParentPrerequisite(not(Prerequisite.create("Strength 17"))).withPrerequisite(simple("Large", "WEAPON_SIZE", "Large")));
                response.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "-2").withParentPrerequisite(not(Prerequisite.create("Strength 19"))).withPrerequisite(simple("Huge", "WEAPON_SIZE", "Huge")));
                break;
            case "Massassi General Template (armor)":
                response.add(Attribute.create(AttributeKey.ACTION, "once per encounter the wearer can move at their normal speed (not the adjusted speed for wearing the Armor) for one round"));

                response.add(Attribute.create(AttributeKey.ARMOR_CHECK_PENALTY_OVERRIDE, "true").withParentPrerequisite(not(Prerequisite.create("Strength 13"))).withPrerequisite(simple("Light Armor", "ARMOR_TYPE", "Light")));
                response.add(Attribute.create(AttributeKey.ARMOR_CHECK_PENALTY_OVERRIDE, "true").withParentPrerequisite(not(Prerequisite.create("Strength 15"))).withPrerequisite(simple("Medium Armor", "ARMOR_TYPE", "Medium")));
                response.add(Attribute.create(AttributeKey.ARMOR_CHECK_PENALTY_OVERRIDE, "true").withParentPrerequisite(not(Prerequisite.create("Strength 17"))).withPrerequisite(simple("Heavy Armor", "ARMOR_TYPE", "Heavy")));
                break;
            case "Prototype General Template (droid)":
                response.add(Attribute.create(AttributeKey.BONUS_SPECIAL_TRAIT, 1));
                response.add(Attribute.create(AttributeKey.BONUS_DROID_QUIRK, 1));
                response.add(Attribute.create(AttributeKey.DAMAGE_THRESHOLD_BONUS, "*0.75"));
                break;
            case "Prototype General Template (weapon)":
                response.add(Attribute.create(AttributeKey.BONUS_SPECIAL_TRAIT, 1));
                response.add(Attribute.create(AttributeKey.EXTENDED_CRITICAL_FAILURE, 2));
                response.add(Attribute.create(AttributeKey.EXTENDED_CRITICAL_FAILURE, 3));
                response.add(Attribute.create(AttributeKey.EXTENDED_CRITICAL_FAILURE, 4));

                break;
            case "Prototype General Template (armor)":
                response.add(Attribute.create(AttributeKey.BONUS_SPECIAL_TRAIT, 1));
                response.add(Attribute.create(AttributeKey.SPECIAL, "A wearer of Prototype Armor takes 1 more die of damage from a Critical Hit (before multiplying)"));

                break;
            case "Verpine General Template (weapon)":
                response.add(Mode.create("Ion", List.of(
                        Attribute.create(AttributeKey.DAMAGE_TYPE, "Ion")
                )));
                response.add(Attribute.create(AttributeKey.TO_HIT_MODIFIER, "-2").withParentPrerequisite(not(Prerequisite.create("Intelligence 13"))));

                break;
            case "Verpine General Template (armor)":
                response.add(Attribute.create(AttributeKey.ARMOR_CHECK_PENALTY_OVERRIDE, "true").withParentPrerequisite(not(Prerequisite.create("Dexterity 13"))));
                response.add(Attribute.create(AttributeKey.DAMAGE_REDUCTION, "10").withModifier("Ion"));
                break;


            default:
                System.out.println("case \"" + resolvedName + "\": break;");
        }

        return response;
    }

    private static Prerequisite getItemPrerequisite(Element content, String type) {
        List<Prerequisite> prerequisites = new LinkedList<>();
                prerequisites.add( Prerequisite.create(type));



                if("droid".equals(type)){

                    return new AndPrerequisite(prerequisites);
                }
        for (Element child : content.children()){
            if (child.text().startsWith("Applicable To:")){
                prerequisites.add(Prerequisite.create(child.text()));
            }
        }
        return new AndPrerequisite(prerequisites);
    }

    private static String getSuffix(String itemName) {
        return "";
    }

    private static String getPrefix(String itemName) {
        Matcher m = TEMPLATE_PREFIX_PATTERN.matcher(itemName);
        if(m.find()){
            return m.group(1);
        }
        return "";
    }

    private static String getTemplateType(Element content) {
        for (Element element : content.children()) {
            if (element.text().toLowerCase().startsWith("template type")) {
                return element.text().substring(14).trim();
            }
        }
        return null;
    }

    private static String getCost(Element content) {
        for (Element element : content.children()) {
            if (element.text().toLowerCase().startsWith("cost")) {
                if ("10% or 1,000 credits more (Whichever is higher) than base item".equals(element.text().substring(6))) {
                    return "max(1000,@cost*0.1)";
                } else if ("20% or 2,000 credits more (Whichever is higher) than base item".equals(element.text().substring(6))) {
                    return "max(2000,@cost*0.2)";
                } else if ("30% or 3,000 credits more (Whichever is higher) than base item".equals(element.text().substring(6))) {
                    return "max(3000,@cost*0.3)";
                } else if ("10% more than base item".equals(element.text().substring(6)) || "10% credits more than base item".equals(element.text().substring(6))) {
                    return "@cost*0.1";
                } else if ("20% more than base item".equals(element.text().substring(6)) || "20% credits more than base item".equals(element.text().substring(6))) {
                    return "@cost*0.2";
                } else if ("30,000 credits more than base item".equals(element.text().substring(6)) || "20% credits more than base item".equals(element.text().substring(6))) {
                    return "30000";
                } else if ("Archaic Vehicles generally can be obtained on the Black Market for 50% of their original cost.".equals(element.text().substring(6)) || "20% credits more than base item".equals(element.text().substring(6))) {
                    return "-0.5*@Cost";
                } else {
                    return "??";
                }
            }
        }
        return null;
    }

    @Override
    protected Collection<JSONy> parseItem(String itemLink, boolean overwrite) {
        return null;
    }
}
