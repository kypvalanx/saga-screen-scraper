package swse.beasts;

import com.google.common.collect.Lists;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import swse.common.*;
import swse.prerequisite.OrPrerequisite;
import swse.prerequisite.SimplePrerequisite;

import java.io.File;
import java.util.*;

public class BeastComponentExporter extends BaseExporter
{
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\beast components.json";
    private static List<String> allPowers = new ArrayList<>();

    public static void main(String[] args)
    {
        List<String> talentLinks = new ArrayList<String>();
        //talentLinks.add("/wiki/Category:Force_Powers");

        //List<JSONObject> entries = new BeastComponentExporter().getEntriesFromCategoryPage(talentLinks);

        List<JSONObject> entries = new LinkedList<>();
        entries.addAll(getManualItems());

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"), "Beast Components");
    }

    private static Collection<? extends JSONObject> getManualItems() {
        Collection<JSONObject> items = Lists.newArrayList();

        items.add(BeastComponent.create("Bite").withSubtype("Melee Natural Weapons")
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Piercing"))
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Poison").withParentPrerequisite(new SimplePrerequisite("Poison (Bite)", "ATTRIBUTE", "poison:Bite")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d2").withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d3").withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d4").withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d6").withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d8").withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "2d6").withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "4d6").withParentPrerequisite(new SimplePrerequisite("Colossal Size", "SIZE", "Colossal")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "4d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "4d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "4d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)")))
                .toJSON());

        items.add(BeastComponent.create("Gore").withSubtype("Melee Natural Weapons")
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Piercing"))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d2").withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d3").withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d4").withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d6").withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d8").withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "2d6").withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "4d6").withParentPrerequisite(new SimplePrerequisite("Colossal Size", "SIZE", "Colossal")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "4d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "4d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "4d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)")))
                .toJSON());

        items.add(BeastComponent.create("Claw").withSubtype("Melee Natural Weapons")
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Slashing"))
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Poison").withParentPrerequisite(new SimplePrerequisite("Poison (Claw)", "ATTRIBUTE", "poison:Claw")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d2").withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d3").withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d4").withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d6").withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d8").withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "2d6").withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal Size", "SIZE", "Colossal")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)")))
                .toJSON());

        items.add(BeastComponent.create("Slam").withSubtype("Melee Natural Weapons")
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Bludgeoning"))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d2").withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d3").withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d4").withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d6").withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d8").withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "2d6").withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal Size", "SIZE", "Colossal")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)")))
                .toJSON());

        items.add(BeastComponent.create("Sting").withSubtype("Melee Natural Weapons")
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Piercing"))
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Poison").withParentPrerequisite(new SimplePrerequisite("Poison (Sting)", "ATTRIBUTE", "poison:Sting")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d2").withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d3").withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d4").withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d6").withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d8").withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "2d6").withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal Size", "SIZE", "Colossal")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)")))
                .toJSON());

        items.add(BeastComponent.create("Tail Slam").withSubtype("Melee Natural Weapons")
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Bludgeoning"))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d2").withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d3").withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d4").withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d6").withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d8").withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "2d6").withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal Size", "SIZE", "Colossal")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)")))
                .toJSON());

        items.add(BeastComponent.create("Tail Slap").withSubtype("Melee Natural Weapons")
                .withProvided(Change.create(ChangeKey.DAMAGE_TYPE, "Bludgeoning"))
                .withProvided(Change.create(ChangeKey.IS_REACH, "true"))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Fine Size", "SIZE", "Fine")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1").withParentPrerequisite(new SimplePrerequisite("Diminutive Size", "SIZE", "Diminutive")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d2").withParentPrerequisite(new SimplePrerequisite("Tiny Size", "SIZE", "Tiny")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d3").withParentPrerequisite(new SimplePrerequisite("Small Size", "SIZE", "Small")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d4").withParentPrerequisite(new SimplePrerequisite("Medium Size", "SIZE", "Medium")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d6").withParentPrerequisite(new SimplePrerequisite("Large Size", "SIZE", "Large")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "1d8").withParentPrerequisite(new SimplePrerequisite("Huge Size", "SIZE", "Huge")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "2d6").withParentPrerequisite(new SimplePrerequisite("Gargantuan Size", "SIZE", "Gargantuan")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal Size", "SIZE", "Colossal")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Frigate) Size", "SIZE", "Colossal (Frigate)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Cruiser) Size", "SIZE", "Colossal (Cruiser)")))
                .withProvided(Change.create(ChangeKey.DAMAGE, "3d6").withParentPrerequisite(new SimplePrerequisite("Colossal (Station) Size", "SIZE", "Colossal (Station)")))
                .toJSON());


        //
        items.add(BeastComponent.create("Darkvision").withType("beastSense")
                .withDescription("A creature that has this ability can see in the dark, ignoring Concealment and Total Concealment due to darkness. Darkvision is black and white only, so there must be at least some light to discern colors. It is otherwise like normal sight, and a creature that has Darkvision can function with no light at all.")
                .withProvided(Change.create(ChangeKey.DARKVISION, "true"))
                .toJSON());

        items.add(BeastComponent.create("Low-Light Vision").withType("beastSense")
                .withDescription("A creature that has this ability can see without penalty in shadowy illumination, ignoring Concealment (but not Total Concealment) due to darkness. It retains the ability to distinguish color and detail under these conditions.")
                .withProvided(Change.create(ChangeKey.LOW_LIGHT_VISION, "true"))
                .toJSON());

        items.add(BeastComponent.create("Poor Vision").withType("beastSense")
                .withDescription("A Beast has very poor vision and takes a -5 penalty to Perception checks involving sight.")
                .withProvided(Change.create(ChangeKey.PERCEPTION_MODIFIER, "-5"))
                .toJSON());

        items.add(BeastComponent.create("Limited Vision").withType("beastSense")
                .withDescription("A Beast can see well in the dark but has poor long-range vision. Any creature 12 squares away from the Beast is considered to have Total Concealment from it.")
                        .withProvided(Change.create(ChangeKey.LIMITED_VISION, 12))
                .toJSON());

        items.add(BeastComponent.create("Heat Sense").withType("beastSense")
                .withDescription("A Beast can detect heat signatures from anything within 15 squares using a normal Perception check.")
                .withProvided(Change.create(ChangeKey.HEAT_SENSE, 15))
                .toJSON());

        items.add(BeastComponent.create("Motion Vision").withType("beastSense")
                .withDescription("A Beast takes a -5 penalty to attack rolls against opponents that have not moved in the previous round. Beasts usually also have the Scent Special Quality.")
                .withProvided(Change.create(ChangeKey.MOTION_VISION, "-5"))
                .toJSON());


        //
        items.add(BeastComponent.create("Airborne").withType("beastType")
                .withDescription("May reroll Initiative checks, but must keep the reroll result, even if it is worse.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "May reroll Initiative checks, but must keep the reroll result, even if it is worse."))
                .toJSON());

        items.add(BeastComponent.create("Aquatic").withType("beastType")
                .withDescription("Can't drown in water, and doesn't need to make Swim checks, also possess Low-Light Vision.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "Can't drown in water, and doesn't need to make Swim checks, also possess Low-Light Vision."))
                .toJSON());

        items.add(BeastComponent.create("Arctic").withType("beastType")
                .withDescription("May reroll Survival checks made to Endure Extreme Cold, keeping the better result.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "May reroll Survival checks made to Endure Extreme Cold, keeping the better result."))
                .toJSON());

        items.add(BeastComponent.create("Desert").withType("beastType")
                .withDescription("May reroll Survival checks made to Endure Extreme Heat, keeping the better result.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "May reroll Survival checks made to Endure Extreme Heat, keeping the better result."))
                .toJSON());

        items.add(BeastComponent.create("Subterranean").withType("beastType")
                .withDescription("May reroll Perception checks, but must keep the reroll result, even if it is worse; also possess Darkvision.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "May reroll Perception checks, but must keep the reroll result, even if it is worse; also possess Darkvision."))
                .toJSON());

        items.add(BeastComponent.create("Space-Dwelling").withType("beastType")
                .withDescription("Immune to the effects of Vacuum in space. Suffers the effects of Vacuum within planetary atmospheres.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "Immune to the effects of Vacuum in space. Suffers the effects of Vacuum within planetary atmospheres."))
                .toJSON());

        items.add(BeastComponent.create("Amphibian").withType("beastType")
                .withDescription("Has a swim speed equal to double its base speed. Can hold its breath for a number of hours equal to 1d4 + its Constitution modifier, and also possess Low-Light Vision.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "Has a swim speed equal to double its base speed. Can hold its breath for a number of hours equal to 1d4 + its Constitution modifier, and also possess Low-Light Vision."))
                .toJSON());


        //
        items.add(BeastComponent.create("Camouflage").withType("beastQuality")
                .withDescription("The Beast's natural coloration changes to mimic its surroundings. The Beast ignores its size modifier when it makes Stealth checks.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast's natural coloration changes to mimic its surroundings. The Beast ignores its size modifier when it makes Stealth checks."))
                .withProvided(Change.create(ChangeKey.CAMOUFLAGE, true))
                .toJSON());

        items.add(BeastComponent.create("Fast Healing 5").withType("beastQuality")
                .withDescription("The beast automatically regains 5 Hit Points every round at the end of its turn, up to its normal maximum, until it is killed.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The beast automatically regains 5 Hit Points every round at the end of its turn, up to its normal maximum, until it is killed."))
                .withProvided(Change.create(ChangeKey.FAST_HEALING, 5))
                .toJSON());

        items.add(BeastComponent.create("Fast Healing 10").withType("beastQuality")
                .withDescription("The beast automatically regains 10 Hit Points every round at the end of its turn, up to its normal maximum, until it is killed.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The beast automatically regains 10 Hit Points every round at the end of its turn, up to its normal maximum, until it is killed."))
                .withProvided(Change.create(ChangeKey.FAST_HEALING, 10))
                .toJSON());

        items.add(BeastComponent.create("Roar").withType("beastQuality")
                .withDescription("As a Move Action, the Beast produces a terrifying, rumbling sound and makes a Persuasion check to Intimidate any enemy target within 6 squares of it. If the attempt succeeds, the target must move away from the Beast on the target's next turn, and the target moves -1 step on the Condition Track. This is a Mind-Affecting effect.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "As a Move Action, the Beast produces a terrifying, rumbling sound and makes a Persuasion check to Intimidate any enemy target within 6 squares of it. If the attempt succeeds, the target must move away from the Beast on the target's next turn, and the target moves -1 step on the Condition Track. This is a Mind-Affecting effect."))
                .toJSON());

        items.add(BeastComponent.create("Scent").withType("beastQuality")
                .withDescription("The Beast ignores Concealment and Cover when making Perception checks to Notice Targets within 10 squares, and takes no penalty from poor visibility when Tracking.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast ignores Concealment and Cover when making Perception checks to Notice Targets within 10 squares, and takes no penalty from poor visibility when Tracking."))
                .toJSON());

        items.add(BeastComponent.create("Screech").withType("beastQuality")
                .withDescription("\tAs a Swift Action, the Beast produces a piercing, high-pitched sound. The Beast makes an attack roll (1d20+10) against the Fortitude Defense of every character within 6 squares of it. If the attack succeeds, the target takes a -5 circumstance penalty on Perception checks and skill checks requiring concentration, and the target has difficulty hearing until the end of the Beast's next turn.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "\tAs a Swift Action, the Beast produces a piercing, high-pitched sound. The Beast makes an attack roll (1d20+10) against the Fortitude Defense of every character within 6 squares of it. If the attack succeeds, the target takes a -5 circumstance penalty on Perception checks and skill checks requiring concentration, and the target has difficulty hearing until the end of the Beast's next turn."))
                .toJSON());


        items.add(BeastComponent.create("Bony Plates").withType("beastQuality")
                .withDescription("As a Reaction, can apply Damage Reduction 5 to mitigate one successful hit per round.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "As a Reaction, can apply Damage Reduction 5 to mitigate one successful hit per round."))
                .toJSON());

        items.add(BeastComponent.create("Dense Hair or Fur").withType("beastQuality")
                .withDescription("May reroll Endurance and Survival checks to counter the effect of cold, taking the better result.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "May reroll Endurance and Survival checks to counter the effect of cold, taking the better result."))
                .toJSON());

        items.add(BeastComponent.create("Hardened Exoskeleton").withType("beastQuality")
                .withDescription("DR 1 for Small and smaller creatures, DR 2 for Medium and Large creatures, DR 5 for Huge and larger creatures against Simple Weapons and Unarmed Attacks.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "DR 1 for Small and smaller creatures, DR 2 for Medium and Large creatures, DR 5 for Huge and larger creatures against Simple Weapons and Unarmed Attacks."))
                .toJSON());

        items.add(BeastComponent.create("Leathery Scales").withType("beastQuality")
                .withDescription("When taking damage that exceeds its Damage Threshold, the Beast takes half damage instead, although it still moves -1 step (or more) on the Condition Track.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "When taking damage that exceeds its Damage Threshold, the Beast takes half damage instead, although it still moves -1 step (or more) on the Condition Track."))
                .toJSON());

        items.add(BeastComponent.create("Metallic Scales").withType("beastQuality")
                .withDescription("When taking damage that exceeds its Damage Threshold, the Beast takes one-quarter damage instead, although it still moves -1 step (or more) on the Condition Track.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "When taking damage that exceeds its Damage Threshold, the Beast takes one-quarter damage instead, although it still moves -1 step (or more) on the Condition Track."))
                .toJSON());

        items.add(BeastComponent.create("Spiked Skin").withType("beastQuality")
                .withDescription("Gain an additional +2 to Reflex Defense against melee attacks. Once per turn per attacker, if an attacker misses on a melee attack against the Beast, the Beast can make a single attack as a Reaction, using its spikes as a Natural Weapon (Gore).")
                .withProvided(Change.create(ChangeKey.SPECIAL, "Gain an additional +2 to Reflex Defense against melee attacks. Once per turn per attacker, if an attacker misses on a melee attack against the Beast, the Beast can make a single attack as a Reaction, using its spikes as a Natural Weapon (Gore)."))
                .toJSON());

        items.add(BeastComponent.create("Thickened Hide").withType("beastQuality")
                .withDescription("Can use Fortitude Defense in place of Reflex Defense against melee attacks (except Lightsabers).")
                .withProvided(Change.create(ChangeKey.SPECIAL, "Can use Fortitude Defense in place of Reflex Defense against melee attacks (except Lightsabers)."))
                .toJSON());



        items.add(BeastComponent.create("Aerobatics").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Airborne Species Type", "SPECIES_TYPE", "Airborne"))
                .withDescription("As part of its Move Action, an Airborne Beast uses the Tumble ability of the Acrobatics skill to fly through a threatened area or the fighting space of an enemy without provoking an Attack of Opportunity. The Beast must make a successful DC 15 Acrobatics check and is considered Trained in Acrobatics for the purpose of using this Special Quality.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "As part of its Move Action, an Airborne Beast uses the Tumble ability of the Acrobatics skill to fly through a threatened area or the fighting space of an enemy without provoking an Attack of Opportunity. The Beast must make a successful DC 15 Acrobatics check and is considered Trained in Acrobatics for the purpose of using this Special Quality."))
                .toJSON());

        items.add(BeastComponent.create("Diving Attack").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Airborne Species Type", "SPECIES_TYPE", "Airborne"))
                .withDescription("The Airborne Beast dives sharply to attack an enemy at a lower altitude. When making a Charge attack, in addition to the competence bonus granted by the Charge attack, the Beast gains a + 1 circumstance bonus to its attack roll (up to a +5 total circumstance bonus) for every square the Beast moves downward. Additionally, the Beast can use a Move Action after resolving the Charge attack. Until the end of its next turn, the Beast takes a -2 penalty to its Reflex Defense because of the Charge Action.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Airborne Beast dives sharply to attack an enemy at a lower altitude. When making a Charge attack, in addition to the competence bonus granted by the Charge attack, the Beast gains a + 1 circumstance bonus to its attack roll (up to a +5 total circumstance bonus) for every square the Beast moves downward. Additionally, the Beast can use a Move Action after resolving the Charge attack. Until the end of its next turn, the Beast takes a -2 penalty to its Reflex Defense because of the Charge Action."))
                .toJSON());

        items.add(BeastComponent.create("Electroshock").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Aquatic Species Type", "SPECIES_TYPE", "Aquatic"))
                .withDescription("When in water or underwater, the Beast produces a short-range electrical jolt. As a Standard Action, the Beast makes an Area Attack (1d20+10) centered on one of its own squares against the Fortitude Defense of all creatures in adjacent squares. If the attack is successful, the target takes 4d6 points of energy damage. If unsuccessful, the target takes half damage.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "When in water or underwater, the Beast produces a short-range electrical jolt. As a Standard Action, the Beast makes an Area Attack (1d20+10) centered on one of its own squares against the Fortitude Defense of all creatures in adjacent squares. If the attack is successful, the target takes 4d6 points of energy damage. If unsuccessful, the target takes half damage."))
                .toJSON());

        items.add(BeastComponent.create("Ink Cloud").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Aquatic Species Type", "SPECIES_TYPE", "Aquatic"))
                .withDescription("When in water or underwater, as a Swift Action, the Beast produces an inky substance, which expands out from the creature in all directions at a rate of 1 square per round, up to a 3-square radius. The cloud grants Concealment to characters within or behind it.\n" +
                        "Additionally, the Beast makes an attack roll (1d20+5) against the Fortitude Defense of water-breathing creatures within the cloud. If the attack succeeds, the target takes 1d6 points of damage and moves -1 step on the Condition Track. If the attack fails, the target takes half damage and does not move on the Condition Track.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "When in water or underwater, as a Swift Action, the Beast produces an inky substance, which expands out from the creature in all directions at a rate of 1 square per round, up to a 3-square radius. The cloud grants Concealment to characters within or behind it.\n" +
                        "Additionally, the Beast makes an attack roll (1d20+5) against the Fortitude Defense of water-breathing creatures within the cloud. If the attack succeeds, the target takes 1d6 points of damage and moves -1 step on the Condition Track. If the attack fails, the target takes half damage and does not move on the Condition Track."))
                .toJSON());

        items.add(BeastComponent.create("Icy Shock").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Arctic Species Type", "SPECIES_TYPE", "Arctic"))
                .withDescription("When the Beast succeeds with a melee attack using its Natural Weapons and the attack deals damage greater than the target's Damage Threshold, the target takes an additional 5 points of damage and moves -1 step on the Condition Track. Targets with the Arctic Species Type are immune to this Special Quality.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "When the Beast succeeds with a melee attack using its Natural Weapons and the attack deals damage greater than the target's Damage Threshold, the target takes an additional 5 points of damage and moves -1 step on the Condition Track. Targets with the Arctic Species Type are immune to this Special Quality."))
                .toJSON());

        items.add(BeastComponent.create("Dustball").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Undertow Species Type", "SPECIES_TYPE", "Undertow"))
                .withDescription("The Beast can kick up a huge cloud when in dusty, dirty, or sandy terrain. Any square the beast passes through, as well as any adjacent squares, provides Concealment until the end of the Beast's next turn. The Beast can remain stationary, but it must expend a Move Action to produce the effect. Make an attack roll (1d20+5) against the Fortitude Defense of all targets within the cloud. If the attack is successful, the targets are Blinded for as long as they remain in the cloud.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast can kick up a huge cloud when in dusty, dirty, or sandy terrain. Any square the beast passes through, as well as any adjacent squares, provides Concealment until the end of the Beast's next turn. The Beast can remain stationary, but it must expend a Move Action to produce the effect. Make an attack roll (1d20+5) against the Fortitude Defense of all targets within the cloud. If the attack is successful, the targets are Blinded for as long as they remain in the cloud."))
                .toJSON());

        items.add(BeastComponent.create("Burrowing").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Subterranean Species Type", "SPECIES_TYPE", "Subterranean"))
                .withDescription("The Beast can move underground, burrowing through dirt and rocky soil but not solid rock. Typically, the Beast's speed is 3 squares when moving underground, although you can alter its speed due to the soil conditions.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast can move underground, burrowing through dirt and rocky soil but not solid rock. Typically, the Beast's speed is 3 squares when moving underground, although you can alter its speed due to the soil conditions."))
                .toJSON());

        items.add(BeastComponent.create("Rumble").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Subterranean Species Type", "SPECIES_TYPE", "Subterranean"))
                .withDescription("As a Full-Round Action, the Beast violently shakes the ground around it, making an attack roll [[1d20+10]] against the Reflex Defense of all characters on (or under) the ground within 6 squares of the Beast. If the attack is successful, standing or sitting targets are knocked Prone. If the attack fails, the cost of all movement on or through the ground is doubled until the end of the Beast's next turn (or until it is killed or incapacitated).\n" +
                        "Underground targets caught in a successful Rumble attack cannot move. Any target that is knocked Prone can get up as a Full-Round Action, but the target is subject to further Rumble attacks. The Rumble lasts for a number of rounds equal to the Beast's Constitution bonus (minimum 2), making an attack each round. The Beast can extend the duration with a successful DC 20 Endurance check.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "As a Full-Round Action, the Beast violently shakes the ground around it, making an attack roll [[1d20+10]] against the Reflex Defense of all characters on (or under) the ground within 6 squares of the Beast. If the attack is successful, standing or sitting targets are knocked Prone. If the attack fails, the cost of all movement on or through the ground is doubled until the end of the Beast's next turn (or until it is killed or incapacitated).\n" +
                        "Underground targets caught in a successful Rumble attack cannot move. Any target that is knocked Prone can get up as a Full-Round Action, but the target is subject to further Rumble attacks. The Rumble lasts for a number of rounds equal to the Beast's Constitution bonus (minimum 2), making an attack each round. The Beast can extend the duration with a successful DC 20 Endurance check."))
                .toJSON());

        items.add(BeastComponent.create("Undertow").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Subterranean Species Type", "SPECIES_TYPE", "Subterranean"))
                .withDescription("This Beast can pull its prey under the surface of the ground. As an attack action, the Beast makes an attack against a target's Fortitude Defense. If the attack is successful, the target is pulled below the surface of the ground and moves -1 step on the Condition Track. The target is unable to take any Actions until it succeeds on a DC 15 Climb check as a Standard Action.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "This Beast can pull its prey under the surface of the ground. As an attack action, the Beast makes an attack against a target's Fortitude Defense. If the attack is successful, the target is pulled below the surface of the ground and moves -1 step on the Condition Track. The target is unable to take any Actions until it succeeds on a DC 15 Climb check as a Standard Action."))
                .toJSON());



        items.add(BeastComponent.create("Ambush").withType("beastQuality")
                .withDescription("The Beast deals an additional 2d6 points of damage with its Natural Weapons against an enemy that is Flat-Footed or that is denied its Dexterity bonus.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast deals an additional 2d6 points of damage with its Natural Weapons against an enemy that is Flat-Footed or that is denied its Dexterity bonus."))
                        .withProvided(Change.create(ChangeKey.SNEAK_ATTACK, "2d6"))
                .toJSON());

        items.add(BeastComponent.create("Constrict").withType("beastQuality")
                .withPrerequisite(OrPrerequisite.or(new SimplePrerequisite("Crush", "FEAT", "Crush"),
                        new SimplePrerequisite("Pin", "FEAT", "Pin")))
                .withDescription("If the Beast successfully Grapples an enemy, it can use the Crush and Pin Feats as normal. When crushing, however, the Beast deals damage equal to 2d6 + its Strength modifier + half its Beast Level rounded down.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "If the Beast successfully Grapples an enemy, it can use the Crush and Pin Feats as normal. When crushing, however, the Beast deals damage equal to 2d6 + its Strength modifier + half its Beast Level rounded down."))
                .toJSON());

        items.add(BeastComponent.create("Devour").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Bite Attack", "BEAST_ATTACK", "Bite"))
                .withDescription("If the Beast hits with its Bite attack against a target two size categories or more smaller than itself, it can automatically make a Grapple check with its Bite attack at its full bonus (even if it has already taken a Full-Round Action).\n" +
                        "If the Grapple check is successful, the Beast can begin devouring its target, and each round the target is in the Beast's mouth, the Beast deals damage equal to 1d8 + its Strength modifier + half its Beast Level rounded down.\n" +
                        "\n" +
                        "If the target reaches 0 Hit Points, it is swallowed by the Beast and continues to take 1d6 points of Acid damage each round.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "If the Beast hits with its Bite attack against a target two size categories or more smaller than itself, it can automatically make a Grapple check with its Bite attack at its full bonus (even if it has already taken a Full-Round Action).\n" +
                        "If the Grapple check is successful, the Beast can begin devouring its target, and each round the target is in the Beast's mouth, the Beast deals damage equal to 1d8 + its Strength modifier + half its Beast Level rounded down.\n" +
                        "\n" +
                        "If the target reaches 0 Hit Points, it is swallowed by the Beast and continues to take 1d6 points of Acid damage each round."))
                .toJSON());

        items.add(BeastComponent.create("Ferocious").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("At least one Natural Weapon", "BEAST_ATTACK", ">0"))
                .withDescription("The Beast can reroll a failed attack with one of its Natural Weapons.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast can reroll a failed attack with one of its Natural Weapons."))
                .toJSON());

        items.add(BeastComponent.create("Pack Hunter").withType("beastQuality")
                .withDescription("The Beast gives a +4 bonus instead of a +2 bonus when using the Aid Another Action.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast gives a +4 bonus instead of a +2 bonus when using the Aid Another Action."))
                .toJSON());

        items.add(BeastComponent.create("Poison").withType("beastQuality")
                        .withProvided(Change.create(ChangeKey.POISON, "#payload#"))
                .withPrerequisite(OrPrerequisite.or(new SimplePrerequisite("Bite Attack", "BEAST_ATTACK", "Bite"),
                        new SimplePrerequisite("Claw Attack", "BEAST_ATTACK", "Claw"),
                        new SimplePrerequisite("Sting Attack", "BEAST_ATTACK", "Sting")))
                .withDescription("The Beast delivers poison through a Bite, Claw, or Sting Natural Weapon. If the Beast deals damage to a living target, the target is also poisoned. If the poison succeeds on an attack roll (1d20+10) against the target's Fortitude Defense, the target moves -1 step along the Condition Track.\n" +
                        "A target moved to the bottom of the Condition Track by the poison is Immobilized but not Unconscious. The poison attacks each round until cured with a successful DC 5, 10, 15, or 20 (select one) Treat Injury check.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast delivers poison through a Bite, Claw, or Sting Natural Weapon. If the Beast deals damage to a living target, the target is also poisoned. If the poison succeeds on an attack roll (1d20+10) against the target's Fortitude Defense, the target moves -1 step along the Condition Track.\n" +
                        "A target moved to the bottom of the Condition Track by the poison is Immobilized but not Unconscious. The poison attacks each round until cured with a successful DC 5, 10, 15, or 20 (select one) Treat Injury check."))
                        .withProvided(Choice.create("Select an attack to add poison to:")
                                .withOption("Bite", new Option("Bite").withPayload("Bite"))
                                .withOption("Claw", new Option("Claw").withPayload("Claw"))
                                .withOption("Sting", new Option("Sting").withPayload("Sting"))
                        )
                .toJSON());

        items.add(BeastComponent.create("Pounce").withType("beastQuality")
                .withDescription("When performing a Long Jump, the Beast does not require a running start for jumps of 4 squares or fewer and does not double the Jump DC when doing so. If the Beast successfully jumps into or adjacent to a target enemy's square, the Beast can make an immediate attack against the target.\n" +
                        "If the attack is successful, the Beast can attempt to trip the target (as if using the Trip Feat) as a Free Action that does not provoke Attacks of Opportunity.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "When performing a Long Jump, the Beast does not require a running start for jumps of 4 squares or fewer and does not double the Jump DC when doing so. If the Beast successfully jumps into or adjacent to a target enemy's square, the Beast can make an immediate attack against the target.\n" +
                        "If the attack is successful, the Beast can attempt to trip the target (as if using the Trip Feat) as a Free Action that does not provoke Attacks of Opportunity."))
                .toJSON());

        items.add(BeastComponent.create("Rend").withType("beastQuality")
                .withPrerequisite(OrPrerequisite.or( "At least 2 Claw Attacks", 2,
                                new SimplePrerequisite("Claw Attack", "BEAST_ATTACK", "Claw")))
                .withProvided(Change.create(ChangeKey.REND, "2d6"))
                .withDescription("If the Beast hits with both of its Claw Natural Weapons in the same turn, it rends the enemy for an additional 2d6 points of damage.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "If the Beast hits with both of its Claw Natural Weapons in the same turn, it rends the enemy for an additional 2d6 points of damage."))
                .toJSON());

        items.add(BeastComponent.create("Tremorsense").withType("beastQuality")
                .withDescription("The Beast automatically senses the location of anything that is in contact with the ground and within 100 squares (no Perception check required).")
                .withProvided(Change.create(ChangeKey.SPECIAL, "The Beast automatically senses the location of anything that is in contact with the ground and within 100 squares (no Perception check required)."))
                .toJSON());

        items.add(BeastComponent.create("Trip").withType("beastQuality")
                .withPrerequisite(new SimplePrerequisite("Tail Attack", "BEAST_ATTACK", "Tail"))
                .withDescription("If the Beast hits with its Tail Attack, it can attempt to trip an enemy (as if using the Trip Feat) as a Free Action that does not provoke Attacks of Opportunity.")
                .withProvided(Change.create(ChangeKey.SPECIAL, "If the Beast hits with its Tail Attack, it can attempt to trip an enemy (as if using the Trip Feat) as a Free Action that does not provoke Attacks of Opportunity."))
                .toJSON());

        return items;
    }


    protected List<JSONy> parseItem(String itemLink, boolean overwrite)
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


        if ("home".equals(itemName.toLowerCase()))
        {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        allPowers.add(itemName);
        Set<Category> categories = Category.getCategories(doc);
//
        List<JSONy> traditions = new ArrayList<>();

        traditions.add(BeastComponent.create(itemName).withDescription(content).withCategories(categories).
                withProvided(Change.create(ChangeKey.TAKE_MULTIPLE_TIMES, "true")));

        return traditions;
    }

}
