package swse.traits;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.BaseExporter;
import swse.common.BonusFeat;
import swse.common.ItemType;
import swse.common.ProvidedItem;
import swse.common.Regex;
import swse.common.Attribute;
import swse.util.Util;

public class TraitExporter extends BaseExporter {

    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\traits.json";
    public static final String UNARMED_DAMAGE_DIE = "unarmedDamageDie";

    public static void main(String[] args) {

        List<String> speciesLinks = new ArrayList<>();
        speciesLinks.add("/wiki/Species");
        //speciesLinks.add("/wiki/Droid_Heroes");
        speciesLinks.add("/wiki/Droid_Chassis");


        List<JSONObject> entries = new ArrayList<>();
        Set<String> abilities = new HashSet<>();

        abilities.add("/wiki/Droid_Traits");

        boolean overwrite = false;
        for (String speciesMenuLink :
                speciesLinks) {
            abilities.addAll(readSpeciesMenuPage(speciesMenuLink, overwrite));
        }

        for (String abilityLink : abilities) {
            entries.addAll(parseItem(abilityLink, overwrite));
        }

        entries.addAll(getManualAbilities());
        entries.addAll(getAttributeBonusAbilities());
        entries.addAll(getSpeedTraits());
        entries.addAll(getDroidUnarmedDamageTraits());
        entries.addAll(getAgeTraits());
        //entries.addAll(getGenderTraits());
        entries.addAll(getSizeTraits());


        //System.out.println(entries.size());
        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
        //writeToCSV(new File("G:\\FoundryVTT\\Data\\ability.csv"), entries);
    }

    private static Collection<? extends JSONObject> getSizeTraits() {
        List<String> sizes = Lists.newArrayList("Fine", "Diminutive", "Tiny", "Small", "Medium", "Large", "Huge", "Gargantuan", "Colossal");
        Set<JSONObject> response = new HashSet<>();
        for (String size : sizes) {
            response.add(Trait
                    .create(size)
                    .withProvided(getSizeSneakModifier(size))
                    .withProvided(getReflexDefenseModifier(size, null))
                    .withProvided(getDamageThresholdSizeModifier(size))
                    .withProvided(getUnarmedDieSize(size))
                    .toJSON());

        }
        return response;
    }


    private static Collection<? extends JSONObject> getGenderTraits() {
        Set<JSONObject> response = new HashSet<>();
        response.add(Trait.create("Male").toJSON());
        response.add(Trait.create("Female").toJSON());
        return response;
    }

    private static Collection<? extends JSONObject> getAgeTraits() {
        Set<JSONObject> response = new HashSet<>();
        response.add(Trait.create("Child").withDescription("A being is a child.").toJSON());
        response.add(Trait.create("Young adult").withDescription("A being is a young adult.").toJSON());
        response.add(Trait.create("Adult").withDescription("A being is an adult.").toJSON());
        response.add(Trait.create("Middle age").withDescription("A being is a middle aged.").toJSON());
        response.add(Trait.create("Old").withDescription("A being is old.").toJSON());
        response.add(Trait.create("Venerable").withDescription("A being is venerable.").toJSON());
        return response;
    }

    private static Set<JSONObject> getSpeedTraits() {
        Set<JSONObject> response = new HashSet<>();
        for (int speed = 1; speed < 21; speed++) {
            response.add(Trait.create("Base Speed " + speed).withDescription("A being has a base speed of " + speed + ".").withProvided(Attribute.create("speed", "Base Speed " + speed)).toJSON());
            response.add(Trait.create("Swim Speed " + speed).withDescription("A being has a swim speed of " + speed + ".").withProvided(Attribute.create("speed", "Swim Speed " + speed)).toJSON());
            response.add(Trait.create("Fly Speed " + speed).withDescription("A being has a fly speed of " + speed + ".").withProvided(Attribute.create("speed", "Fly Speed " + speed)).toJSON());
            response.add(Trait.create("Wheeled Speed " + speed).withDescription("A being has a wheeled speed of " + speed + ".").withProvided(Attribute.create("speed", "Wheeled Speed " + speed)).toJSON());
            response.add(Trait.create("Walking Speed " + speed).withDescription("A being has a walking speed of " + speed + ".").withProvided(Attribute.create("speed", "Walking Speed " + speed)).toJSON());
            response.add(Trait.create("Tracked Speed " + speed).withDescription("A being has a tracked speed of " + speed + ".").withProvided(Attribute.create("speed", "Tracked Speed " + speed)).toJSON());
            response.add(Trait.create("Hover Speed " + speed).withDescription("A being has a hover speed of " + speed + ".").withProvided(Attribute.create("speed", "Hover Speed " + speed)).toJSON());
        }
        response.add(Trait.create("Stationary Speed 0").withDescription("A being cannot move.").toJSON());
        return response;
    }

    private static Set<JSONObject> getDroidUnarmedDamageTraits() {
        Set<JSONObject> response = new HashSet<>();
        response.add(Trait.create("Droid Unarmed Damage 1").withDescription("A droid has a limb that grants it 1 damage when used in an unarmed attack.").withProvided(Attribute.create("droidUnarmedDamageDie", "1")).toJSON());
        response.add(Trait.create("Droid Unarmed Damage 1d2").withDescription("A droid has a limb that grants it 1d2 damage when used in an unarmed attack.").withProvided(Attribute.create("droidUnarmedDamageDie", "1d2")).toJSON());
        response.add(Trait.create("Droid Unarmed Damage 1d3").withDescription("A droid has a limb that grants it 1d3 damage when used in an unarmed attack.").withProvided(Attribute.create("droidUnarmedDamageDie", "1d3")).toJSON());
        response.add(Trait.create("Droid Unarmed Damage 1d4").withDescription("A droid has a limb that grants it 1d4 damage when used in an unarmed attack.").withProvided(Attribute.create("droidUnarmedDamageDie", "1d4")).toJSON());
        response.add(Trait.create("Droid Unarmed Damage 1d6").withDescription("A droid has a limb that grants it 1d6 damage when used in an unarmed attack.").withProvided(Attribute.create("droidUnarmedDamageDie", "1d6")).toJSON());
        response.add(Trait.create("Droid Unarmed Damage 1d8").withDescription("A droid has a limb that grants it 1d8 damage when used in an unarmed attack.").withProvided(Attribute.create("droidUnarmedDamageDie", "1d8")).toJSON());
        response.add(Trait.create("Droid Unarmed Damage 2d6").withDescription("A droid has a limb that grants it 2d6 damage when used in an unarmed attack.").withProvided(Attribute.create("droidUnarmedDamageDie", "2d6")).toJSON());
        response.add(Trait.create("Droid Unarmed Damage 2d8").withDescription("A droid has a limb that grants it 2d8 damage when used in an unarmed attack.").withProvided(Attribute.create("droidUnarmedDamageDie", "2d8")).toJSON());
        response.add(Trait.create("Droid Default Appendage Offset").withDescription("A droid has no appendages until appendage items are added.").withProvided(Attribute.create("appendages", "-2")).toJSON());

        return response;
    }

    private static Set<JSONObject> getAttributeBonusAbilities() {
        Set<JSONObject> response = new HashSet<>();
        List<String> attributes = Lists.newArrayList("Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom", "Charisma");


        for (String attribute : attributes) {
            for (int i = -10; i < 33; i++) {
                Trait trait = Trait.create((i > -1 ? "+" : "") + i + " " + attribute);
                String attributeBonus = (i > -1 ? "+" : "") + i;
                trait.withProvided(Attribute.create(attribute.toLowerCase() + "Bonus", attributeBonus));
                trait.withDescription("This trait grants " + i + " to " + attribute);
                response.add(trait.toJSON());
            }
        }

        return response;
    }

    private static Set<JSONObject> getManualAbilities() {
        Set<JSONObject> response = new HashSet<>();
        response.add(Trait.create("Weapon Familiarity").withDescription("<p>This Species treats an Exotic Weapon as another type of weapon.</p>").withProvided(Lists.newArrayList(Attribute.create("weaponFamiliarity", "#payload#"))).toJSON());

        response.add(Trait.create("Lightsaber").withDescription("<p>A Jedi begins play with a <a href=\"/wiki/Lightsaber\" class=\"mw-redirect\" title=\"Lightsaber\">Lightsaber</a> provided by their Master. Later, they can build their own <a href=\"/wiki/Lightsaber\" class=\"mw-redirect\" title=\"Lightsaber\">Lightsaber</a>.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Fearless (Jedi Master)").withDescription("<p>Jedi Masters are immune to Fear effects.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Fearless (Sith Lord)").withDescription("<p>Sith Lords are immune to Fear effects.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Serenity").withDescription("<p>You may enter a brief meditative state as a <a href=\"/wiki/Full-Round_Action\" class=\"mw-redirect\" title=\"Full-Round Action\">Full-Round Action</a>. You may remain in this trance as long as you wish, and you are still aware of your surroundings; however, you are otherwise <a href=\"/wiki/Helpless\" class=\"mw-redirect\" title=\"Helpless\">Helpless</a> and cannot perform any other <a href=\"/wiki/Actions\" class=\"mw-redirect\" title=\"Actions\">Actions</a>. Upon emerging from the trance (As a <a href=\"/wiki/Swift_Action\" class=\"mw-redirect\" title=\"Swift Action\">Swift Action</a>), your first attack roll or <a href=\"/wiki/Use_the_Force\" title=\"Use the Force\">Use the Force</a> skill check made in the following round is considered to be a <a href=\"/wiki/Natural_20\" class=\"mw-redirect\" title=\"Natural 20\">Natural 20</a>.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Vehicle Dodge").withDescription("<p>Beginning at 2nd level, you apply a dodge bonus to the <a href=\"/wiki/Reflex_Defense\" class=\"mw-redirect\" title=\"Reflex Defense\">Reflex Defense</a> of any <a href=\"/wiki/Vehicle\" class=\"mw-redirect\" title=\"Vehicle\">Vehicle</a> you Pilot. The dodge bonus is equal to one-half your Class Level, rounded down. Any condition that makes you lose your <a href=\"/wiki/Dexterity\" title=\"Dexterity\">Dexterity</a> bonus to <a href=\"/wiki/Reflex_Defense\" class=\"mw-redirect\" title=\"Reflex Defense\">Reflex Defense</a> also makes you lose dodge bonuses. Also, dodge bonuses stack with each other, unlike most other types of bonuses.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Mark").withDescription("<p>As an Assassin, you specialize in the sudden attack- the brutal stroke that eliminates your target by the fastest means possible. At the start of the encounter, you can select a single target within your line of sight to be your Mark. For the duration of the encounter, you gain a bonus equal to one-half your Class Level (Rounded down) on damage rolls against that opponent. This damage is in addition to the character's usual level bonus to damage. This damage is doubled on a successful Critical Hit, as normal. If you reduce your target to 0 Hit Points, you may place your Mark on another target within line of sight as a <a href=\"/wiki/Free_Action\" class=\"mw-redirect\" title=\"Free Action\">Free Action</a>.\n" +
                "</p><p>As a <a href=\"/wiki/Swift_Action\" class=\"mw-redirect\" title=\"Swift Action\">Swift Action</a>, you can sacrifice this bonus to render your target <a href=\"/wiki/Flat-Footed\" class=\"mw-redirect\" title=\"Flat-Footed\">Flat-Footed</a> against your next attack made before the end of your turn. Once you sacrifice this bonus, it is lost for the remainder of the encounter.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Familiar Foe").withDescription("<p>By observing your opponent in combat, you know how to defeat them more easily. If you spend a <a href=\"/wiki/Full-Round_Action\" class=\"mw-redirect\" title=\"Full-Round Action\">Full-Round Action</a> observing an opponent in combat, you gain a bonus on attack rolls against that opponent, and a bonus to your <a href=\"/wiki/Reflex_Defense\" class=\"mw-redirect\" title=\"Reflex Defense\">Reflex Defense</a> against attacks made by that opponent equal to one-half your Class Level (Rounded down). The effects last until the end of the encounter. You cannot use this ability until after your opponent has acted during the combat.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Score").withDescription("<p>When a Charlatan takes the time to observe a potential victim, they gain useful clues and insights about his or her desires and motives. If a Charlatan spends a <a href=\"/wiki/Full-Round_Action\" class=\"mw-redirect\" title=\"Full-Round Action\">Full-Round Action</a> watching a single target, until the end of the encounter that Charlatan can reroll all <a href=\"/wiki/Deception\" title=\"Deception\">Deception</a> checks made against that target, keeping the better of the two results.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Swindle").withDescription("<p>A Charlatan can take advantage of potential scores, using the information they learned to dupe them into believing that the Charlatan is on the up-and-up. A Charlatan can substitute <a href=\"/wiki/Deception\" title=\"Deception\">Deception</a> checks for <a href=\"/wiki/Stealth\" title=\"Stealth\">Stealth</a> checks made to <a href=\"/wiki/Pick_Pocket\" class=\"mw-redirect\" title=\"Pick Pocket\">Pick Pocket</a>. A successful check does not mean that the Charlatan picks the target's pockets, but rather that they have convinced the target to give them the desired object of the target's own free will. Generally, a target realizes what they have done at the end of the encounter.\n" +
                "</p><p>At 6th level, a Charlatan gains a +1 bonus to these checks. This bonus increases to +2 at 8th level, and +5 at 10th level.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Executive Leadership").withDescription("<p>As a <a href=\"/wiki/Swift_Action\" class=\"mw-redirect\" title=\"Swift Action\">Swift Action</a>, as many times as equal to half your Corporate Agent level, you can grant an ally within line of sight a temporary boost to their Speed, attack rolls, or <a href=\"/wiki/Defenses\" title=\"Defenses\">Defenses</a>. Until the end of their turn, they gain one of the following benefits:\n" +
                "</p><ul><li>Increase their base Speed by 2 squares.</li>\n" +
                "<li>Add a +2 morale boost to their attack rolls.</li>\n" +
                "<li>Add a +2 morale boost to all their <a href=\"/wiki/Defenses\" title=\"Defenses\">Defenses</a>.</li></ul>").toJSON());

        response.add(Trait.create("Command Cover").withDescription("<p>Starting at 2nd level, a Crime Lord can use their allies to shield them from harm. You can gain a +1 <a href=\"/wiki/Cover\" title=\"Cover\">Cover</a> bonus to your <a href=\"/wiki/Reflex_Defense\" class=\"mw-redirect\" title=\"Reflex Defense\">Reflex Defense</a> for each ally that is adjacent to you, up to a maximum bonus equal to one-half your Class Level (Maximum +5 at 10th level).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Networked Mind").withDescription("<p>A Droid Commander can designate a number of its Droid Allies equal to one-half its Class Level as being networked with it. A Networked Droid Ally can receive beneficial <a href=\"/wiki/Mind-Affecting\" class=\"mw-redirect\" title=\"Mind-Affecting\">Mind-Affecting</a> effects from the Droid Commander. Once the&nbsp;Droid Commander designates a Droid Ally as the recipient of this benefit, it cannot change the Droid beneficiary until after the end of the encounter, and designating a Droid to benefit from this Class Feature is a <a href=\"/wiki/Free_Action\" class=\"mw-redirect\" title=\"Free Action\">Free Action</a>. Additionally, the Droid Commander is considered to have line of sight to a Networked Droid Ally as long as they both have an active <a href=\"/wiki/Comlink\" title=\"Comlink\">Comlink</a> (If the <a href=\"/wiki/Comlinks\" class=\"mw-redirect\" title=\"Comlinks\">Comlinks</a> are subject to communications jamming, it loses this benefit).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Delay Damage").withDescription("<p>Elite Troopers are among the toughest individuals in the galaxy. After being exposed to numerous threats, foes, and combat situations, you've developed the ability to delay effects that would drop lesser creatures.\n" +
                "</p><p>Once per encounter as a <a href=\"/wiki/Reaction\" class=\"mw-redirect\" title=\"Reaction\">Reaction</a>, you can choose to delay the effect of a single attack, ability, or effect used against you. The damage or effect does not take hold until the end of your next turn.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Damage Reduction").withDescription("<p>At 2nd level, you gain <a href=\"/wiki/Damage_Reduction\" title=\"Damage Reduction\">Damage Reduction</a> 1 (DR 1), which means that you reduce the damage you take from any attack by 1.\n" +
                "</p><p>At every even-numbered level after 2nd, your <a href=\"/wiki/Damage_Reduction\" title=\"Damage Reduction\">Damage Reduction</a> improves by 1 (DR 2 at 4th level, DR 3 at 6th level, and so on).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Resources").withDescription("<p>An Enforcer has access to additional Resources provided by their department or organization. Each time an Enforcer gains a level, they receive <a href=\"/wiki/Restricted\" class=\"mw-redirect\" title=\"Restricted\">Restricted</a> or <a href=\"/wiki/Military\" class=\"mw-redirect\" title=\"Military\">Military</a> Equipment (Including <a href=\"/wiki/Weapons\" title=\"Weapons\">Weapons</a> or <a href=\"/wiki/Vehicles\" title=\"Vehicles\">Vehicles</a>) equal in value to their Heroic Level x 2,000 credits. The <a href=\"/wiki/Equipment\" title=\"Equipment\">Equipment</a> appears in a civilized, accessible location of the Enforcer's choice.\n" +
                "</p><p>An Enforcer may choose not to receive any Resources when they gain a level, instead adding the value of any Equipment they would normally gain to the Resources they gain at their next level.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Targeted Suspect").withDescription("<p>The time an Enforcer spends investigating a suspect results in benefits when dealing with the individual during an encounter. If an Enforcer spends a <a href=\"/wiki/Full-Round_Action\" class=\"mw-redirect\" title=\"Full-Round Action\">Full-Round Action</a> observing an opponent in combat, they gain a bonus on attack rolls and <a href=\"/wiki/Deception\" title=\"Deception\">Deception</a>, <a href=\"/wiki/Perception\" title=\"Perception\">Perception</a>, and <a href=\"/wiki/Persuasion\" title=\"Persuasion\">Persuasion</a> checks equal to one-half the Enforcer's Class Level (Rounded down). The benefits last for the remainder of the encounter. An Enforcer cannot use this ability until after their target has acted during combat.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Prophet").withDescription("<p>Every time you gain a level in this <a href=\"/wiki/Prestige_Class\" class=\"mw-redirect\" title=\"Prestige Class\">Prestige Class</a>, you receive two <a href=\"/wiki/Destiny_Points\" class=\"mw-redirect\" title=\"Destiny Points\">Destiny Points</a> instead of the usual one. In addition, you may sacrifice this extra <a href=\"/wiki/Destiny_Point\" class=\"mw-redirect\" title=\"Destiny Point\">Destiny Point</a> immediately after gaining a Class Level. If you choose to do so, you instead receive a prophetic vision from <a href=\"/wiki/The_Force\" title=\"The Force\">The Force</a>; the content of this vision is determined by the GM. The vision is instantaneous, so no time is required to exercise this option. Upon seeing the vision, you have the option to choose a new <a href=\"/wiki/Destiny\" title=\"Destiny\">Destiny</a>, so long as the new <a href=\"/wiki/Destiny\" title=\"Destiny\">Destiny</a> is related in some way to the vision. The GM is the final arbiter of what new <a href=\"/wiki/Destiny\" title=\"Destiny\">Destiny</a> (Or <a href=\"/wiki/Destinies\" class=\"mw-redirect\" title=\"Destinies\">Destinies</a>) are appropriate.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Indomitable").withDescription("<p>Force Disciples are immune to <a href=\"/wiki/Mind-Affecting\" class=\"mw-redirect\" title=\"Mind-Affecting\">Mind-Affecting</a> effects.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Unflinching").withDescription("<p>At 2nd level your training as a Gladiator has made you very difficult to intimidate or deter. For a number of times equal to half your Gladiator level (Rounded down) per encounter, you may add your Gladiator level to either your <a href=\"/wiki/Fortitude_Defense\" class=\"mw-redirect\" title=\"Fortitude Defense\">Fortitude Defense</a> (And <a href=\"/wiki/Damage_Threshold\" class=\"mw-redirect\" title=\"Damage Threshold\">Damage Threshold</a>), or <a href=\"/wiki/Will_Defense\" class=\"mw-redirect\" title=\"Will Defense\">Will Defense</a> until the start of your next turn as a <a href=\"/wiki/Reaction\" class=\"mw-redirect\" title=\"Reaction\">Reaction</a>; you can choose which <a href=\"/wiki/Defense\" class=\"mw-redirect\" title=\"Defense\">Defense</a> to apply the bonus to when you use the ability, but you must declare the use of this feature before you know the outcome of the effect targeting you.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Trusty Sidearm").withDescription("<p>Starting at 2nd level, the Gunslinger gains a bonus on <a href=\"/wiki/Damage\" title=\"Damage\">Damage</a> rolls equal to one-half their Class Level (Rounded down) when wielding a <a href=\"/wiki/Pistol\" class=\"mw-redirect\" title=\"Pistol\">Pistol</a>. This damage is in addition to the character's usual level bonus to damage. This damage is doubled on a successful critical hit, as normal.\n" +
                "</p>").toJSON());

        response.add(Trait.create("No Tools Required").withDescription("<p>The Improviser can use parts of machines and electronics as tools, eliminating the need for a <a href=\"/wiki/Security_Kit\" title=\"Security Kit\">Security Kit</a> or a <a href=\"/wiki/Tool_Kit\" title=\"Tool Kit\">Tool Kit</a> when attempting <a href=\"/wiki/Mechanics\" title=\"Mechanics\">Mechanics</a> or <a href=\"/wiki/Use_Computer\" title=\"Use Computer\">Use Computer</a> checks that would normally require such a tool. The Improviser is always considered to be using a <a href=\"/wiki/Tool_Kit\" title=\"Tool Kit\">Tool Kit</a> or <a href=\"/wiki/Security_Kit\" title=\"Security Kit\">Security Kit</a>, even when he or she does not have one.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Contraband").withDescription("<p>At 2nd level, an Improviser gains access to illegal goods through their underworld connections. An Improviser can obtain any combination of items that have an availability of <a href=\"/wiki/Rare\" class=\"mw-redirect\" title=\"Rare\">Rare</a> or <a href=\"/wiki/Illegal\" class=\"mw-redirect\" title=\"Illegal\">Illegal</a>, up to a total value of 2,000 credits x one-half their Class Level (Rounded down). The Improviser does not need to pay <a href=\"/wiki/Black_Market\" class=\"mw-redirect\" title=\"Black Market\">Black Market</a> multipliers on these goods, only their base value. Obtaining any combination of these goods requires one hour of work in a civilized or semi-civilized area.\n" +
                "</p><p>The Improviser can select those goods immediately, or over the course of their level. However, if the Improviser levels up without having reached their Contraband limit, any additional credits' worth of goods are lost, and the Improviser's budget for obtaining <a href=\"/wiki/Rare\" class=\"mw-redirect\" title=\"Rare\">Rare</a> and <a href=\"/wiki/Illegal\" class=\"mw-redirect\" title=\"Illegal\">Illegal</a> items resets with the new level.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Sapience").withDescription("<p>Independent Droids can choose to have their <a href=\"/wiki/Droid_Immunities\" class=\"mw-redirect\" title=\"Droid Immunities\">Droid Immunities</a> not apply to any <a href=\"/wiki/Mind-Affecting\" class=\"mw-redirect\" title=\"Mind-Affecting\">Mind-Affecting</a> effects, allowing them to benefit from <a href=\"/wiki/Mind-Affecting\" class=\"mw-redirect\" title=\"Mind-Affecting\">Mind-Affecting</a> abilities that provide positive effects.\n" +
                "</p><p>Additionally, Independent Droids have permanently disabled their <a href=\"/wiki/Behavioral_Inhibitor\" class=\"mw-redirect\" title=\"Behavioral Inhibitor\">Behavioral Inhibitor</a>, enabling them to take any desired action they wish. Furthermore, Independent Droids are immune to the effects of <a href=\"/wiki/Restraining_Bolt\" title=\"Restraining Bolt\">Restraining Bolts</a>.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Independent Spirit").withDescription("<p>At 2nd level, Independent Droids gain the ability to assert their independence and protect themselves from harm. Once per encounter, an Independent Droid can grant themselves a morale bonus to any <a href=\"/wiki/Defense_Score\" class=\"mw-redirect\" title=\"Defense Score\">Defense Score</a> (Their choice) against a single skill check or attack roll as a <a href=\"/wiki/Reaction\" class=\"mw-redirect\" title=\"Reaction\">Reaction</a>.  This bonus is equal to one-half the Independent Droid's Class Level (Rounded down).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Unarmed Stun").withDescription("<p>Starting at 2nd level, an Infiltrator can use their <a href=\"/wiki/Unarmed_Attacks\" title=\"Unarmed Attacks\">Unarmed Attacks</a> to deal <a href=\"/wiki/Stun\" class=\"mw-redirect\" title=\"Stun\">Stun</a> damage. The Infiltrator must designate their intention to <a href=\"/wiki/Stun\" class=\"mw-redirect\" title=\"Stun\">Stun</a> their target before the attack is made, and the Infiltrator deals +1 die of damage with their <a href=\"/wiki/Unarmed_Attack\" class=\"mw-redirect\" title=\"Unarmed Attack\">Unarmed Attack</a>, which deals <a href=\"/wiki/Stun\" class=\"mw-redirect\" title=\"Stun\">Stun</a> damage. At 6th level, this extra damage increases to +2 dice, and at 10th level it increases to +3 dice.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Lead Infiltrator").withDescription("<p>Starting at 4th level, an Infiltrator becomes an effective leader of infiltration teams. The Infiltrator can make a <a href=\"/wiki/Stealth\" title=\"Stealth\">Stealth</a> check for a number of allies within their line of sight equal to their <a href=\"/wiki/Charisma\" title=\"Charisma\">Charisma</a> bonus (Minimum 1), using the check result in place of their allies' <a href=\"/wiki/Stealth\" title=\"Stealth\">Stealth</a> checks. At 8th level, the Infiltrator doubles the number of allies they can lead using this ability. Allies must stay within the Infiltrator's line of sight to retain this bonus.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Tough as Durasteel").withDescription("<p>At 2nd level as part of their training, a Martial Arts Master learned how to ready themselves for the attacks, reducing the effectiveness of the attacker's blows. Whenever a Martial Arts Master damages a target with an <a href=\"/wiki/Unarmed_Attack\" class=\"mw-redirect\" title=\"Unarmed Attack\">Unarmed Attack</a> while wearing <a href=\"/wiki/Light_Armor\" title=\"Light Armor\">Light Armor</a> or no Armor, they gain <a href=\"/wiki/Bonus_Hit_Points\" class=\"mw-redirect\" title=\"Bonus Hit Points\">Bonus Hit Points</a> equal to their Class Level. The number of <a href=\"/wiki/Bonus_Hit_Points\" class=\"mw-redirect\" title=\"Bonus Hit Points\">Bonus Hit Points</a> gained with an <a href=\"/wiki/Unarmed_Attack\" class=\"mw-redirect\" title=\"Unarmed Attack\">Unarmed Attack</a> increases for every other level you gain in this Prestige Class.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Lure of Piracy").withDescription("<p>Even though Master Privateers are legitimate privateers, piracy is a great temptation. If a Master Privateer is lost to <a href=\"/wiki/The_Dark_Side\" title=\"The Dark Side\">The Dark Side</a>, they drop all pretence of privateering, becoming a known pirate, and may no longer take any further levels of the Master Privateer <a href=\"/wiki/Prestige_Class\" class=\"mw-redirect\" title=\"Prestige Class\">Prestige Class</a>.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Veteran Privateer").withDescription("<p>The Master Privateer's experience as a privateer has hardened them to the realities of battle, granting them the ability to subdue their foes with little difficulty. When a Master Privateer makes a melee attack roll, as a <a href=\"/wiki/Free_Action\" class=\"mw-redirect\" title=\"Free Action\">Free Action</a> they can grant themselves a +2 competence bonus on that attack roll. A Master Privateer can do this a number of times per encounter equal to one-half their Class Level (Rounded down).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Medical Secrets").withDescription("<p>As a Medic's medical skills grow, they gain insight into specific medical procedures and treatment of specific <a href=\"/wiki/Species\" title=\"Species\">Species</a>. At each even-numbered level (2nd, 4th, 6th and so on), the Medic gains a <a href=\"/wiki/Medical_Secret\" class=\"mw-redirect\" title=\"Medical Secret\">Medical Secret</a>, giving them a bonus when using a specific application of the <a href=\"/wiki/Treat_Injury\" title=\"Treat Injury\">Treat Injury</a> skill. A Medic can select a given treatment only once.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Master of Movement").withDescription("<p>You know how to take advantage of whatever terrain you are fighting in. A number of times per encounter equal to half your Melee Duelist level, you can either ignore the movement penalty for moving through <a href=\"/wiki/Difficult_Terrain\" title=\"Difficult Terrain\">Difficult Terrain</a> or over low objects on a single <a href=\"/wiki/Move_Action\" class=\"mw-redirect\" title=\"Move Action\">Move Action</a>, or reroll a single <a href=\"/wiki/Jump\" title=\"Jump\">Jump</a> or <a href=\"/wiki/Acrobatics\" title=\"Acrobatics\">Acrobatics</a> check, keeping the better of the two results.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Field-Created Weapon").withDescription("<p>Military Engineers are able to scavenge parts from other technological objects and use them to build a limited-use personal-sized weapon. The item they create has only a limited life span, and the parts used to build it are rendered useless afterward.\n" +
                "</p><p>As a <a href=\"/wiki/Standard_Action\" class=\"mw-redirect\" title=\"Standard Action\">Standard Action</a>, the Military Engineer makes a <a href=\"/wiki/Mechanics\" title=\"Mechanics\">Mechanics</a> check (DC 20) to create a melee or ranged weapon of their choice. The base value of the weapon can be no more than 600 credits x the Military Engineer's Class Level. Additionally, the weapon grants the Military Engineer an <a href=\"/wiki/Equipment\" title=\"Equipment\">Equipment</a> bonus on attack rolls equal to one-half their Class Level. This ability can be used only once per encounter, and at the end of the encounter the weapon is destroyed. If the weapon requires an <a href=\"/wiki/Energy_Cell\" title=\"Energy Cell\">Energy Cell</a> to operate, the Military Engineer creates one (At no additional cost) for the weapon at the time the weapons is created. A Military Engineer can only create <a href=\"/wiki/Weapons\" title=\"Weapons\">Weapons</a> they are proficient with.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Command Cover").withDescription("<p>Starting at 2nd level, an Officer can use their allies to shield them from harm. You can gain a +1 <a href=\"/wiki/Cover\" title=\"Cover\">Cover</a> bonus to your <a href=\"/wiki/Reflex_Defense\" class=\"mw-redirect\" title=\"Reflex Defense\">Reflex Defense</a> for each ally that is adjacent to you, up to a maximum bonus equal to one-half your Class Level (Maximum +5 at 10th level).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Share Talent").withDescription("<p>At every even-number level, choose a <a href=\"/wiki/Talent\" class=\"mw-redirect\" title=\"Talent\">Talent</a> that you already possess. The Talent you select must be under the <a href=\"/wiki/Commando_Talent_Tree\" title=\"Commando Talent Tree\">Commando Talent Tree</a>, the <a href=\"/wiki/Influence_Talent_Tree\" title=\"Influence Talent Tree\">Influence Talent Tree</a>, the <a href=\"/wiki/Inspiration_Talent_Tree\" title=\"Inspiration Talent Tree\">Inspiration Talent Tree</a>, or the <a href=\"/wiki/Military_Tactics_Talent_Tree\" title=\"Military Tactics Talent Tree\">Military Tactics Talent Tree</a>. Once per day, as a <a href=\"/wiki/Standard_Action\" class=\"mw-redirect\" title=\"Standard Action\">Standard Action</a>, you can impart the benefits of the chosen <a href=\"/wiki/Talent\" class=\"mw-redirect\" title=\"Talent\">Talent</a> to one or more allies, effectively granting them the <a href=\"/wiki/Talent\" class=\"mw-redirect\" title=\"Talent\">Talent</a> (Even if they don't meet the prerequisites). An ally must be within 10 squares of you, and must be able to see and hear you to gain the <a href=\"/wiki/Talent\" class=\"mw-redirect\" title=\"Talent\">Talent</a>; once gained, it's benefits last until the end of the encounter.\n" +
                "</p><p>You can share the <a href=\"/wiki/Talent\" class=\"mw-redirect\" title=\"Talent\">Talent</a> with a number of allies equal to one-half your Class Level (Rounded down).\n" +
                "</p><p>Each time you gain this ability, it applies to a different <a href=\"/wiki/Talent\" class=\"mw-redirect\" title=\"Talent\">Talent</a>. By 10th level, an Officer will have five different <a href=\"/wiki/Talents\" title=\"Talents\">Talents</a>, that they can share with up to five allies at a time.\n" +
                "</p><p>Once you select a Shared Talent, it cannot be changed.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Fugitive").withDescription("<p>Outlaw must stay one step ahead of the authorities and bounty hunters or risk capture and death. An Outlaw's experiences have taught them to be fast on their feet. Once per encounter, starting at 2nd level, an Outlaw can move 1 additional square whenever they use the <a href=\"/wiki/Withdraw\" class=\"mw-redirect\" title=\"Withdraw\">Withdraw</a> Action. Thus, if an Outlaw has a base Speed of 6, they can <a href=\"/wiki/Withdraw\" class=\"mw-redirect\" title=\"Withdraw\">Withdraw</a> up to 4 squares (3 for half speed, +1 for this ability). This bonus increases by 1 at every even level thereafter (+2 squares at 4th, +3 at 6th, +4 at 8th, and +5 at 10th).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Create Cover").withDescription("<p>Pathfinders know how to use the terrain to their advantage, creating obstacles and cover from the materials they have at hand. Starting at 2nd level, as a <a href=\"/wiki/Standard_Action\" class=\"mw-redirect\" title=\"Standard Action\">Standard Action</a> a Pathfinder can designate a number of squares equal to or less than one-half of the Pathfinder's Class Level, all of which must be within 6 squares of himself or herself. These squares are considered to be filled with low objects, providing anyone adjacent to the squares with <a href=\"/wiki/Cover\" title=\"Cover\">Cover</a> against distant attacks.\n" +
                "</p><p>At least one of these squares designates must be adjacent to the Pathfinder. A Pathfinder can use this ability multiple times per encounter, provided that the total number of squares designated across all uses never exceeds the one-half Class Level limit. Thus, a 6th level Pathfinder can spend one <a href=\"/wiki/Standard_Action\" class=\"mw-redirect\" title=\"Standard Action\">Standard Action</a> to create 2 squares of <a href=\"/wiki/Cover\" title=\"Cover\">Cover</a>, and on a subsequent round spend another <a href=\"/wiki/Standard_Action\" class=\"mw-redirect\" title=\"Standard Action\">Standard Action</a> to create a third square of <a href=\"/wiki/Cover\" title=\"Cover\">Cover</a> (One-half of 6).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Unexpected Results").withDescription("<p>Saboteurs occasionally see the results of their work, even at the most at unexpected moments. When an enemy making an attack roll against a Saboteur rolls a Natural 1 on an attack roll using an <a href=\"/wiki/Advanced_Melee_Weapon\" class=\"mw-redirect\" title=\"Advanced Melee Weapon\">Advanced Melee Weapon</a>, <a href=\"/wiki/Lightsaber\" class=\"mw-redirect\" title=\"Lightsaber\">Lightsaber</a>, <a href=\"/wiki/Pistol\" class=\"mw-redirect\" title=\"Pistol\">Pistol</a>, <a href=\"/wiki/Rifle\" class=\"mw-redirect\" title=\"Rifle\">Rifle</a>, or <a href=\"/wiki/Heavy_Weapon\" class=\"mw-redirect\" title=\"Heavy Weapon\">Heavy Weapon</a>, that Weapon is immediately disabled and ceases to function until it has received <a href=\"/wiki/Repairs\" class=\"mw-redirect\" title=\"Repairs\">Repairs</a> (Through the use of the <a href=\"/wiki/Repair_Object\" class=\"mw-redirect\" title=\"Repair Object\">Repair Object</a> application of the <a href=\"/wiki/Mechanics\" title=\"Mechanics\">Mechanics</a> Skill).\n" +
                "</p>").toJSON());

        response.add(Trait.create("Destructive").withDescription("<p>Starting at 2nd level, a Saboteur always deals double damage to unattended objects and <a href=\"/wiki/Vehicles\" title=\"Vehicles\">Vehicles</a>.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Quick Sabotage (Simple Devices)").withDescription("<p>Saboteurs know how to disable <a href=\"/wiki/Equipment\" title=\"Equipment\">Equipment</a> quickly and efficiently. Saboteurs can attempt a <a href=\"/wiki/Mechanics\" title=\"Mechanics\">Mechanics</a> check to disable a device by using improvised tools if no <a href=\"/wiki/Security_Kit\" title=\"Security Kit\">Security Kit</a> is available. At 4th level, a Saboteur can attempt to disable Simple Devices as a <a href=\"/wiki/Swift_Action\" class=\"mw-redirect\" title=\"Swift Action\">Swift Action</a>." +
                "</p>").toJSON());

        response.add(Trait.create("Quick Sabotage (Tricky Devices)").withDescription("<p>Saboteurs know how to disable <a href=\"/wiki/Equipment\" title=\"Equipment\">Equipment</a> quickly and efficiently. Saboteurs can attempt a <a href=\"/wiki/Mechanics\" title=\"Mechanics\">Mechanics</a> check to disable a device by using improvised tools if no <a href=\"/wiki/Security_Kit\" title=\"Security Kit\">Security Kit</a> is available. At 6th level, a Saboteur can attempt to disable Tricky Devices as a <a href=\"/wiki/Swift_Action\" class=\"mw-redirect\" title=\"Swift Action\">Swift Action</a>." +
                "</p>").toJSON());

        response.add(Trait.create("Quick Sabotage (Complex Devices)").withDescription("<p>Saboteurs know how to disable <a href=\"/wiki/Equipment\" title=\"Equipment\">Equipment</a> quickly and efficiently. Saboteurs can attempt a <a href=\"/wiki/Mechanics\" title=\"Mechanics\">Mechanics</a> check to disable a device by using improvised tools if no <a href=\"/wiki/Security_Kit\" title=\"Security Kit\">Security Kit</a> is available. At 8th level, a Saboteur can attempt to disable Complex Devices using two <a href=\"/wiki/Swift_Actions\" title=\"Swift Actions\">Swift Actions</a>.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Master Saboteur").withDescription("<p>When a Saboteur reaches 10th level, they excel at inhibiting and destroying enemy <a href=\"/wiki/Equipment\" title=\"Equipment\">Equipment</a>. 10th level Saboteurs can reroll any <a href=\"/wiki/Mechanics\" title=\"Mechanics\">Mechanics</a> check to disable a device or <a href=\"/wiki/Handle_Explosives\" class=\"mw-redirect\" title=\"Handle Explosives\">Handle Explosives</a>, keeping the better of the two results.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Shaper Lore").withDescription("<p>A Shaper is skilled at building, modifying, and repairing <a href=\"/wiki/Biotechnology\" class=\"mw-redirect\" title=\"Biotechnology\">Biotechnology</a>. Because of this intimate familiarity, a Shaper gains a bonus equal to one-half their Class Level (Rounded down) on all <a href=\"/wiki/Knowledge_(Life_Sciences)\" class=\"mw-redirect\" title=\"Knowledge (Life Sciences)\">Knowledge (Life Sciences)</a> and <a href=\"/wiki/Treat_Injury\" title=\"Treat Injury\">Treat Injury</a> checks made with regards to <a href=\"/wiki/Biotech\" class=\"mw-redirect\" title=\"Biotech\">Biotech</a>, regardless of size or complexity.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Shaper Hand").withDescription("<p>At 6th level, a <a href=\"/wiki/Yuuzhan_Vong\" title=\"Yuuzhan Vong\">Yuuzhan Vong</a> Shaper is expected to replace at least one of their hands with what is known as a Shaper Hand. A Shaper Hand is a bioengineered appendage equipped with a number of tools that assist in tasks specific to Shaping. A Shaper who declines to attach a Shaper Hand when permitted to do so is often viewed with suspicion by other <a href=\"/wiki/Yuuzhan_Vong\" title=\"Yuuzhan Vong\">Yuuzhan Vong</a>.\n" +
                "</p><p>A Shaper Hand replaces the need for a <a href=\"/wiki/Biotech_Tool_Kit\" title=\"Biotech Tool Kit\">Biotech Tool Kit</a>.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Temptation").withDescription("<p>You are adept at using Dun Möch, an ancient and vile technique for tempting others to tap into <a href=\"/wiki/The_Dark_Side\" title=\"The Dark Side\">The Dark Side</a> of <a href=\"/wiki/The_Force\" title=\"The Force\">The Force</a>. As a <a href=\"/wiki/Standard_Action\" class=\"mw-redirect\" title=\"Standard Action\">Standard Action</a>, a Sith Lord can make a <a href=\"/wiki/Persuasion\" title=\"Persuasion\">Persuasion</a> check and compare it to the <a href=\"/wiki/Will_Defense\" class=\"mw-redirect\" title=\"Will Defense\">Will Defense</a> of a single opponent within line of sight. If the check succeeds, the target is filled with fear or anger, briefly giving in to <a href=\"/wiki/The_Dark_Side\" title=\"The Dark Side\">The Dark Side</a>. If the target spends a <a href=\"/wiki/Force_Point\" class=\"mw-redirect\" title=\"Force Point\">Force Point</a> before your next turn, it must either add 1 point to its <a href=\"/wiki/Dark_Side_Score\" class=\"mw-redirect\" title=\"Dark Side Score\">Dark Side Score</a>, or move -1 step on the <a href=\"/wiki/Condition_Track\" class=\"mw-redirect\" title=\"Condition Track\">Condition Track</a>, as it is overcome by doubt and remorse. If the target spends a <a href=\"/wiki/Destiny_Point\" class=\"mw-redirect\" title=\"Destiny Point\">Destiny Point</a> before your next turn, it instead must either add 2 points to it's <a href=\"/wiki/Dark_Side_Score\" class=\"mw-redirect\" title=\"Dark Side Score\">Dark Side Score</a>, or move -2 steps on the <a href=\"/wiki/Condition_Track\" class=\"mw-redirect\" title=\"Condition Track\">Condition Track</a>.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Surprise Attack").withDescription("<p>Whenever a Vanguard attacks a target that is unaware of them or otherwise is otherwise <a href=\"/wiki/Flat-Footed\" class=\"mw-redirect\" title=\"Flat-Footed\">Flat-Footed</a>, they gain a bonus on their first attack roll in a round against that target equal to one-half their Class Level.\n" +
                "</p>").toJSON());

        response.add(Trait.create("Disable Attribute Modification").withDescription("<p>Some Species have set Attribute Arrays\n" +
                "</p>").toJSON());

        response.add(Trait.create("Aquala").withDescription("<p>Aquala Aqualish have finned hands, making them the strongest swimmers of all Aqualish." +
                "</p>").toJSON());

        response.add(Trait.create("Kyuzo").withDescription("<p>Kyuzo are a subspecies of Aqualish that adapted over time on the world of Phatrong. They are known for their incredible athleticism and agility." +
                "</p>").toJSON());

        response.add(Trait.create("Quara").withDescription("<p>The Quara are the most obstinate of Aqualish, and unfortunately the most commonly encountered in the galaxy." +
                "</p>").toJSON());

        response.add(Trait.create("Ualaq").withDescription("<p>The Ualaq have been mutated by radiation, and have grown an additional pair of eyes." +
                "</p>").toJSON());

        response.add(Trait.create("Intimidating").withDescription("<p>Aqualish can use their Strength modifier instead of their Charisma modifier for Persuasion checks made to Intimidate others." +
                "</p>").toJSON());

        response.add(Trait.create("Extra Arms 2").withDescription("<p>Beings can hold up to four items or Weapons at a time (Or six, depending on the Species). This ability does not grant extra attacks; however, it does mean a being can wield two two-handed weapons at a time (Or three, depending on the Species)." +
                "</p>").withProvided(Attribute.create("appendages", "2")).toJSON());

        response.add(Trait.create("Extra Arms 4").withDescription("<p>Beings can hold up to four items or Weapons at a time (Or six, depending on the Species). This ability does not grant extra attacks; however, it does mean a being can wield two two-handed weapons at a time (Or three, depending on the Species)." +
                "</p>").withProvided(Attribute.create("appendages", "4")).toJSON());

        response.add(Trait.create("Extra Arms 6").withDescription("<p>Beings can hold up to four items or Weapons at a time (Or six, depending on the Species). This ability does not grant extra attacks; however, it does mean a being can wield two two-handed weapons at a time (Or three, depending on the Species)." +
                "</p>").withProvided(Attribute.create("appendages", "6")).toJSON());

        response.add(Trait.create("Jar'Kai")
                .withDescription("When you use the Lightsaber Defense Talent, you gain twice the normal deflection bonus to your Reflex Defense when you are wielding two Lightsabers.")
                .withProvided(Attribute.create("lightsaberDefense", "*2")).toJSON());

        response.add(Trait.create("Makashi")
                .withDescription("When wielding a single Lightsaber in one hand, the deflection bonus you gain from the Lightsaber Defense Talent increases by 2 (To a maximum of +5).")
                .withProvided(Attribute.create("lightsaberDefense", "2")).toJSON());

        response.add(Trait.create("Niman")
                .withDescription("When wielding a Lightsaber, you gain a +1 bonus to your Reflex Defense and Will Defense.")
                .withProvided(Attribute.create("reflexDefenseBonus", "1")).withProvided(Attribute.create("willDefenseBonus", "1")).toJSON());
        //response.add(Trait.create("Stormtrooper Perception Bonus").withProvided(Attribute.create("perceptionModifier", 2)).toJSON());
        //response.add(Trait.create("Low-Light Vision").withProvided(Attribute.create("lowLightVision", true)).toJSON());
        //response.add(Trait.create("4 Arm Option").toJSON());
        //response.add(Trait.create("6 Arm Option").toJSON());
        return response;
    }

    private static List<String> readSpeciesMenuPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);

        Element body = doc.body();

        Elements tables = body.getElementsByClass("wikitable");

        List<String> hrefs = new ArrayList<>();
        tables.forEach(table ->
        {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row ->
            {
                Elements tds = row.getElementsByTag("td");
                Element first = tds.first();
                if (first != null) {
                    Element second = tds.get(1);
                    Element anchor = first.getElementsByTag("a").first();
                    if (anchor != null) {
                        String href = anchor.attr("href");
                        hrefs.add(href);
                    }
                }
            });
        });

        return hrefs.stream().flatMap(itemLink -> getCatagoryLinks(itemLink, overwrite).stream()).collect(Collectors.toList());
    }

    private static List<String> getCatagoryLinks(String itemLink, boolean overwrite) {
        Document doc = getDoc(itemLink, overwrite);
        if (doc == null) {
            return new ArrayList<>();
        }


        return Util.getCategoryLinks(doc);
    }


    private static List<JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite) {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null) {
            return new ArrayList<>();
        }
        Element body = doc.body();

        Elements as = body.select("a.category-page__member-link");

        Set<String> hrefs = new HashSet<>();
        as.forEach(a -> hrefs.add(a.attr("href")));

        return hrefs.stream().flatMap((Function<String, Stream<JSONObject>>) itemLink -> parseItem(itemLink, overwrite).stream())
                .collect(Collectors.toList());
    }

    private static List<JSONObject> parseItem(String itemLink, boolean overwrite) {
        if (null == itemLink || "/wiki/Category:Droid_Traits".equals(itemLink)) {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }
        String itemName;
        try {
            itemName = getItemName(doc);
        } catch (Exception e) {
            System.err.println(itemLink);
            return new ArrayList<>();
        }

        if (Lists.newArrayList("home",
                "damage reduction", "fine", "tiny", "small", "medium", "large", "huge", "gargantuan", "colossal", "diminutive", "extra arms", "weapon familiarity")
                .contains(itemName.toLowerCase()) || itemName.toLowerCase().startsWith("bonus class skill ") || itemName.toLowerCase().startsWith("bonus feat ")
                || itemName.toLowerCase().startsWith("condition bonus feat") || itemName.toLowerCase().startsWith("conditional bonus feat ") || itemName.toLowerCase().startsWith("natural armor ") || itemName.toLowerCase().startsWith("bonus trained skill ")) {
            //System.out.println("IGNORED: "+itemName);
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();
        if (content != null) {
            content.select("span.mw-editsection").remove();
            content.select("div.toc").remove();
            content.select("img,figure").remove();

        }


        List<ProvidedItem> categories = ProvidedItem.getTraits(doc);

        List<JSONObject> trait = new ArrayList<>();

        trait.add(Trait.create(itemName)
                .withDescription(getDescription(content))
                .withProvided(categories)
                .withProvided(getBonusFeat(itemName, content))
                .withProvided(getClassSkill(itemName))
                .withProvided(getNaturalArmorBonus(itemName))
                .withProvided(getReflexDefenseModifier(itemName, content))
                .withProvided(getFortitudeDefenseModifier(itemName, content))
                .withProvided(getWillDefenseModifier(itemName, content))
                .withProvided(getSizeSneakModifier(itemName))
                .withProvided(getDamageThresholdSizeModifier(itemName))
                .withProvided(getDamageReduction(itemName))
                .withProvided(getManualAttributes(itemName))
                .withProvided(getItems(itemName)).toJSON());

        return trait;
    }

    private static Collection<Object> getManualAttributes(String itemName) {
        List<Object>  attributes = new ArrayList<>();

        switch(itemName){
            case "Superior Defenses":
                attributes.add(Attribute.create("fortitudeDefenseBonus", "1"));
                attributes.add(Attribute.create("willDefenseBonus", "1"));
                attributes.add(Attribute.create("reflexDefenseBonus", "1"));
                break;
        }

        return attributes;
    }

    private static Collection<Object> getFortitudeDefenseModifier(String itemName, Element content) {

        List<Object> attributes = new ArrayList<>();
        if (content != null) {
            Optional<Matcher> m = Regex.find("Beings gain a ([+-]\\d*) Species bonus to their Fortitude Defense\\.", content.text());
            if (m.isPresent()) {
                Integer bonus = Integer.parseInt(m.get().group(1));
                attributes.add(Attribute.create("fortitudeDefenseBonus", bonus));
            }
        }

        switch (itemName) {
            case "Cold Resistance":
                attributes.add(Attribute.create("fortitudeDefenseBonus", 5).withModifier("Extreme Cold"));
                break;
            case "Cold-Blooded":
                attributes.add(Attribute.create("fortitudeDefenseBonus", -5).withModifier("Extreme Cold"));
                break;
            case "Toxic Resistance":
                attributes.add(Attribute.create("fortitudeDefenseBonus", 5).withModifier("Poisons"));
                attributes.add(Attribute.create("fortitudeDefenseBonus", 5).withModifier("Toxic Atmospheres"));
                break;
            case "Radiation Resistance":
                attributes.add(Attribute.create("fortitudeDefenseBonus", 5).withModifier("Radiation"));
                break;
            case "Environmental Adaptation":
                attributes.add(Attribute.create("fortitudeDefenseBonus", 5).withModifier("Extreme Temperatures"));
                attributes.add(Attribute.create("fortitudeDefenseBonus", 5).withModifier("Radiation"));
                break;
            case "Climate Sensitivity":
                attributes.add(Attribute.create("fortitudeDefenseBonus", -5).withModifier("Extreme Temperatures"));
                break;
            case "Heat Resistance":
                attributes.add(Attribute.create("fortitudeDefenseBonus", 5).withModifier("Extreme Heat"));
                break;
        }
        return attributes;
    }

    private static Collection<Object> getWillDefenseModifier(String itemName, Element content) {
        List<Object> attributes = new ArrayList<>();
        if (content != null) {

            Optional<Matcher> m = Regex.find("Beings gain a ([+-]\\d*) Species bonus to their Will Defense\\.", content.text());
            if (m.isPresent()) {
                Integer bonus = Integer.parseInt(m.get().group(1));
                attributes.add(Attribute.create("willDefenseBonus", bonus));
            }
        }

        switch (itemName) {
            case "Fearless":
                attributes.add(Attribute.create("willDefenseBonus", 5).withModifier("Fear Effects"));
                break;
            case "Fearful":

                attributes.add(Attribute.create("willDefenseBonus", -5).withModifier("Fear Effects"));
                break;
            case "Xenophobia":

                attributes.add(Attribute.create("willDefenseBonus", 2).withModifier("Persuasion checks made to improve their Attitude by any creature of a different Species"));
                break;
            case "Force Resistance":

                attributes.add(Attribute.create("willDefenseBonus", 5).withModifier("any use of the Use the Force Skill"));
                break;
            case "Driven":

                attributes.add(Attribute.create("willDefenseBonus", 5).withModifier("Mind-Affecting Effects"));
                break;
            case "Mental Fortitude":
                attributes.add(Attribute.create("willDefenseBonus", 2).withModifier("Deception Checks"));
                attributes.add(Attribute.create("willDefenseBonus", 2).withModifier("Persuasion Checks"));
                break;
        }

        return attributes;
    }


    private static List<Object> getItems(String itemName) {
        if ("Natural Weapons".equals(itemName)) {
            return Lists.newArrayList(ProvidedItem.create("Natural Weapons", ItemType.ITEM));
        }
        if ("Bellow".equals(itemName)) {
            return Lists.newArrayList(ProvidedItem.create("Bellow", ItemType.ITEM));
        }

        return null;
    }

    private static List<Attribute> getReflexDefenseModifier(String itemName, Element content) {
        List<Attribute> attributes = new ArrayList<>();
        switch (itemName) {
            case "Colossal":
                attributes.add(Attribute.create("reflexDefenseBonus", -10));
                break;
            case "Gargantuan":
                attributes.add(Attribute.create("reflexDefenseBonus", -5));
                break;
            case "Huge":
                attributes.add(Attribute.create("reflexDefenseBonus", -2));
                break;
            case "Large":
                attributes.add(Attribute.create("reflexDefenseBonus", -1));
                break;
            case "Medium":
                attributes.add(Attribute.create("reflexDefenseBonus", +0));
                break;
            case "Small":
                attributes.add(Attribute.create("reflexDefenseBonus", +1));
                break;
            case "Tiny":
                attributes.add(Attribute.create("reflexDefenseBonus", +2));
                break;
            case "Diminutive":
                attributes.add(Attribute.create("reflexDefenseBonus", +5));
                break;
            case "Fine":
                attributes.add(Attribute.create("reflexDefenseBonus", +10));
                break;
            default:
        }


        if (content != null && content.text().toLowerCase().contains("reflex")) {
            Optional<Matcher> m = Regex.find("Beings gain a ([+-]\\d*) Species bonus to their Reflex Defense\\.", content.text());
            if (m.isPresent()) {
                Integer bonus = Integer.parseInt(m.get().group(1));
                attributes.add(Attribute.create("reflexDefenseBonus", bonus));
            }
        }
        return attributes;
    }

    private static Attribute getUnarmedDieSize(String itemName) {
        switch (itemName) {
            case "Colossal":
                return Attribute.create(UNARMED_DAMAGE_DIE, "2d8");
            case "Gargantuan":
                return Attribute.create(UNARMED_DAMAGE_DIE, "2d6");
            case "Huge":
                return Attribute.create(UNARMED_DAMAGE_DIE, "1d8");
            case "Large":
                return Attribute.create(UNARMED_DAMAGE_DIE, "1d6");
            case "Medium":
                return Attribute.create(UNARMED_DAMAGE_DIE, "1d4");
            case "Small":
                return Attribute.create(UNARMED_DAMAGE_DIE, "1d3");
            case "Tiny":
                return Attribute.create(UNARMED_DAMAGE_DIE, "1d2");
            case "Diminutive":
            case "Fine":
                return Attribute.create(UNARMED_DAMAGE_DIE, "1");
            default:
                return null;
        }
    }

    private static Attribute getSizeSneakModifier(String itemName) {
        switch (itemName) {
            case "Colossal":
                return Attribute.create("sneakModifier", -20);
            case "Gargantuan":
                return Attribute.create("sneakModifier", -15);
            case "Huge":
                return Attribute.create("sneakModifier", -10);
            case "Large":
                return Attribute.create("sneakModifier", -5);
            case "Medium":
                return Attribute.create("sneakModifier", +0);
            case "Small":
                return Attribute.create("sneakModifier", +5);
            case "Tiny":
                return Attribute.create("sneakModifier", +10);
            case "Diminutive":
                return Attribute.create("sneakModifier", +15);
            case "Fine":
                return Attribute.create("sneakModifier", +20);
            default:
                return null;
        }
    }

    private static Attribute getDamageThresholdSizeModifier(String itemName) {
        switch (itemName) {
            case "Colossal":
                return Attribute.create("damageThresholdSizeModifier", +50);
            case "Gargantuan":
                return Attribute.create("damageThresholdSizeModifier", +20);
            case "Huge":
                return Attribute.create("damageThresholdSizeModifier", +10);
            case "Large":
                return Attribute.create("damageThresholdSizeModifier", +5);
            case "Medium":
            case "Small":
            case "Tiny":
            case "Diminutive":
            case "Fine":
                return Attribute.create("damageThresholdSizeModifier", +0);
            default:
                return null;
        }
    }

    private static Attribute getDamageReduction(String itemName) {
        if (itemName.startsWith("Damage Reduction ") || "Armor Plating".equals(itemName)) {
            Pattern damageReductionPattern = Pattern.compile("Damage Reduction (\\d*)");
            Matcher m = damageReductionPattern.matcher(itemName);
            if (m.find()) {
                return Attribute.create("damageReduction", Integer.parseInt(m.group(1)));
            }
        }
        return null;
    }

    private static Collection<Object> getNaturalArmorBonus(String itemName) {

        List<Object> attributes = new ArrayList<>();
        if (itemName.equals("Natural Armor")) {
            attributes.add(Attribute.create("reflexDefenseBonus", "#payload#"));
        }
        return attributes;
    }

    private static Collection<Object> getClassSkill(String itemName) {
        List<Object> attributes = new ArrayList<>();
        if (itemName.startsWith("Bonus Class Skill ")) {
            Pattern classSkillPattern = Pattern.compile("Bonus Class Skill \\(([\\w\\s()-]*)\\)");
            Matcher m = classSkillPattern.matcher(itemName);
            if (m.find()) {
                attributes.add(Attribute.create("classSkill", m.group(1).toLowerCase()));
            }
        }
        if ("Bonus Trained Skill".equals(itemName)) {

            attributes.add(Attribute.create("trainedSkills", "1"));
        }
        return attributes;
    }

    private static Attribute getBonusFeat(String itemName, Element content) {
        if (content == null) {
            return null;
        }
        if (itemName.equals("Bonus Feat")) {
            return Attribute.create("provides", "General Feats");
        }

        BonusFeat bonusFeat = null;

        if (itemName.startsWith("Condition")) {
            Pattern bonusFeatOnTrainedSkill = Pattern.compile("A being with ([\\w\\s()-]*) as a Trained Skill gains? ([\\w\\s()-]*) as a bonus Feat.");
            Matcher m = bonusFeatOnTrainedSkill.matcher(content.text());
            if (m.find()) {
                bonusFeat = BonusFeat.createTrainedSkillFeat(m.group(1), m.group(2));
            } else {
                Pattern bonusFeatOnExistingFeat = Pattern.compile("A being with the ([\\w\\s()-]*) Feat gains the ([\\w\\s()-]*) feat as a bonus Feat.");
                Matcher m2 = bonusFeatOnExistingFeat.matcher(content.text());
                if (m2.find()) {
                    bonusFeat = BonusFeat.createFeatDependentFeat(m2.group(1), m2.group(2));
                } else {
                    Pattern bonusFeatIfQualified = Pattern.compile("A being gains ([\\w\\s()-]*) as a bonus Feat at 1st level, provided he or she meets the prerequisites of the Feat");
                    Matcher m3 = bonusFeatIfQualified.matcher(content.text());
                    if (m3.find()) {
                        bonusFeat = BonusFeat.createPrerequisiteDependentFeat(m3.group(1));
                    } else {
                        Pattern bonusFeatOnAttribute = Pattern.compile("A being with a (\\w*) score of (\\d*) or higher automatically gains the ([\\w\\s()-]*) Feat as a bonus Feat, even if they do not meet the other prerequisites.");
                        Matcher m4 = bonusFeatOnAttribute.matcher(content.text());
                        if (m4.find()) {
                            bonusFeat = BonusFeat.createAttributeDependentFeat(m4.group(1), Integer.parseInt(m4.group(2)), m4.group(3));
                        } else {
                            //System.err.println(content.text());
                        }
                    }
                }
            }
        } else if (itemName.startsWith("Bonus Feat ")) {
            Pattern bonusFeatOnTrainedSkill = Pattern.compile("Bonus Feat \\(([\\w\\s()-]*)\\)");
            Matcher m = bonusFeatOnTrainedSkill.matcher(itemName);
            if (m.find()) {
                bonusFeat = BonusFeat.createFeat(m.group(1));
            }
        }
        if (bonusFeat == null) {
            return null;
        }
        return Attribute.create("bonusFeat", bonusFeat);
    }

}
