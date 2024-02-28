package swse.species;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
import swse.common.Choice;
import swse.common.ItemType;
import swse.common.Option;
import swse.common.ProvidedItem;
import swse.util.Util;

import static swse.common.ItemType.TRAIT;
import static swse.common.ProvidedItem.create;

class StatBonuses
{
    public static final List<String> WEIRD_SPECIES = Lists.newArrayList("Devaronian", "Melodie", "Ruurian", "Rybet", "Arkanian Offshoot", "Republic Clone", "Hutt", "Umbaran", "Umbaran (Alternate Species Traits)");
    public static final String ABILITY_SCORE_SELECTOR = "(Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma)";
    public static final Pattern MANY_PENALTY_PATTERN = Pattern.compile("(-\\d*) penalties to (?:their )?" + ABILITY_SCORE_SELECTOR + ", " + ABILITY_SCORE_SELECTOR + ", and " + ABILITY_SCORE_SELECTOR);
    public static final Pattern DOUBLE_PENALTY_PATTERN = Pattern.compile("(-\\d*) (?:penalties|penalty) to (?:both )?(?:their )?" + ABILITY_SCORE_SELECTOR + " and " + ABILITY_SCORE_SELECTOR);
    public static final Pattern SINGLE_PENALTY_PATTERN = Pattern.compile("(-\\d*) penalty to (?:their )?" + ABILITY_SCORE_SELECTOR);
    public static final Pattern MULTIPLE_BONUS_PATTERN = Pattern.compile("(\\+\\d*) bonus(?:es)? to (?:both )?(?:their )?" + ABILITY_SCORE_SELECTOR + "(?:,)? and (?:their )?" + ABILITY_SCORE_SELECTOR);
    public static final Pattern SINGLE_BONUS_PATTERN = Pattern.compile("(\\+\\d*)(?: bonus)? to (?:their )?" + ABILITY_SCORE_SELECTOR);
    private static List<Object> standardAgeBonuses;

    public static List<Object> getStatBonuses(Element content, String speciesName)
    {
        List<Object> bonuses = new ArrayList<>();


        if (WEIRD_SPECIES.contains(speciesName)) {
            switch (speciesName) {
                case "Arkanian Offshoot":
                    bonuses.add(createProvidedTrait("Constitution (-2)"));
                    bonuses.add(new Choice("Select an Attribute Bonus")
                            .withShowSelectionInName(false)
                            .withOption("Strength", new Option().withProvidedItem(createProvidedTrait("Strength (+2)")))
                            .withOption("Dexterity", new Option().withProvidedItem(createProvidedTrait("Dexterity (+2)"))));
                    break;
                case "Melodie":
                    bonuses.add(createProvidedTrait("Constitution (+2)"));

                    bonuses.add(createProvidedTrait("Wisdom (+2)", "TRAIT:Child"));
                    bonuses.add(createProvidedTrait("Strength (-2)", "TRAIT:Child"));

                    bonuses.add(createProvidedTrait("Wisdom (+2)", "TRAIT:Young adult"));
                    bonuses.add(createProvidedTrait("Strength (-2)", "TRAIT:Young adult"));

                    bonuses.add(createProvidedTrait("Charisma (+2)", "TRAIT:Adult"));
                    bonuses.add(createProvidedTrait("Dexterity (-4)", "TRAIT:Adult"));

                    bonuses.add(createProvidedTrait("Charisma (+2)", "TRAIT:Middle age"));
                    bonuses.add(createProvidedTrait("Dexterity (-4)", "TRAIT:Middle age"));

                    bonuses.add(createProvidedTrait("Charisma (+2)", "TRAIT:Old"));
                    bonuses.add(createProvidedTrait("Dexterity (-4)", "TRAIT:Old"));

                    bonuses.add(createProvidedTrait("Charisma (+2)", "TRAIT:Venerable"));
                    bonuses.add(createProvidedTrait("Dexterity (-4)", "TRAIT:Venerable"));

                    break;
                case "Devaronian":
                    bonuses.add(createProvidedTrait("Dexterity (+2)", "GENDER:Male"));
                    bonuses.add(createProvidedTrait("Wisdom (-2)", "GENDER:Male"));
                    bonuses.add(createProvidedTrait("Charisma (-2)", "GENDER:Male"));

                    bonuses.add(createProvidedTrait("Intelligence (+2)", "GENDER:Female"));
                    bonuses.add(createProvidedTrait("Wisdom (+2)", "GENDER:Female"));
                    bonuses.add(createProvidedTrait("Dexterity (-2)", "GENDER:Female"));
                    break;
                case "Ruurian":
                    bonuses.add(createProvidedTrait("Strength (-2)"));
                    bonuses.add(createProvidedTrait("Constitution (-2)"));

                    bonuses.add(createProvidedTrait("Intelligence (+4)", "TRAIT:Child"));

                    bonuses.add(createProvidedTrait("Intelligence (+4)", "TRAIT:Young adult"));

                    bonuses.add(createProvidedTrait("Intelligence (+4)", "TRAIT:Adult"));

                    bonuses.add(createProvidedTrait("Charisma (+4)", "TRAIT:Middle age"));

                    bonuses.add(createProvidedTrait("Charisma (+4)", "TRAIT:Old"));

                    bonuses.add(createProvidedTrait("Charisma (+4)", "TRAIT:Venerable"));

                    break;
                case "Rybet":
                    bonuses.add(createProvidedTrait("Dexterity (+2)", "GENDER:Male"));
                    bonuses.add(createProvidedTrait("Strength (-2)", "GENDER:Male"));

                    bonuses.add(createProvidedTrait("Strength (+2)", "GENDER:Female"));
                    bonuses.add(createProvidedTrait("Dexterity (-2)", "GENDER:Female"));

                    break;
                case "Republic Clone":
                    bonuses.add(createProvidedTrait("Strength (+5)"));
                    bonuses.add(createProvidedTrait("Dexterity (+3)"));
                    bonuses.add(createProvidedTrait("Intelligence (+2)"));
                    bonuses.add(createProvidedTrait("Charisma (-2)"));
                    bonuses.add(createProvidedTrait("Disable Attribute Modification"));

                    bonuses.add(new Choice("Select an Attribute Bonus")
                            .withShowSelectionInName(false)
                            .withOption("Strength", new Option().withProvidedItem(createProvidedTrait("Strength (+2)")))
                            .withOption("Dexterity", new Option().withProvidedItem(createProvidedTrait("Dexterity (+2)")))
                            .withOption("Constitution", new Option().withProvidedItem(createProvidedTrait("Constitution (+2)")))
                            .withOption("Intelligence", new Option().withProvidedItem(createProvidedTrait("Intelligence (+2)")))
                            .withOption("Wisdom", new Option().withProvidedItem(createProvidedTrait("Wisdom (+2)")))
                            .withOption("Charisma", new Option().withProvidedItem(createProvidedTrait("Charisma (+2)"))));
                    break;
                case "Hutt":

                    bonuses.add(createProvidedTrait("Constitution (+2)"));
                    bonuses.add(createProvidedTrait("Intelligence (+2)"));
                    bonuses.add(createProvidedTrait("Strength (+2)"));
                    bonuses.add(createProvidedTrait("Dexterity (-6)"));
                    break;
                case "Replica Droid":
                    bonuses.add(createProvidedTrait("Strength (+2)"));
                    bonuses.add(createProvidedTrait("Dexterity (+2)"));
                    bonuses.add(createProvidedTrait("Charisma (-2)"));
                    break;
                case "Umbaran (Alternate Species Traits)":
                    bonuses.add(createProvidedTrait("Wisdom (+2)"));
                    bonuses.add(createProvidedTrait("Charisma (+2)"));
                    bonuses.add(createProvidedTrait("Constitution (-2)"));
                    break;
                case "Umbaran":
                    bonuses.add(createProvidedTrait("Dexterity (+2)"));
                    bonuses.add(createProvidedTrait("Wisdom (+2)"));
                    bonuses.add(createProvidedTrait("Constitution (-2)"));
                    break;
            }
        } else {
            for (Element element : content.select("li,p"))
            {
                String text = element.text();
                if (text.toLowerCase().startsWith("ability modifier") || text.toLowerCase().startsWith("ability scores"))
                {
                    if (!text.contains("no Ability Score adjustments") && !text.contains("no bonuses or penalties to their Ability Scores") && !text.contains("None. ")) {

                        Matcher singleBonusMatcher = SINGLE_BONUS_PATTERN.matcher(text);
                        while (singleBonusMatcher.find()) {
                            bonuses.add(createProvidedTrait(singleBonusMatcher.group(2) + " (" + singleBonusMatcher.group(1) + ")"));
                        }

                        Matcher multipleBonusMatcher = MULTIPLE_BONUS_PATTERN.matcher(text);
                        while (multipleBonusMatcher.find()) {
                            bonuses.add(createProvidedTrait(multipleBonusMatcher.group(2) + " (" + multipleBonusMatcher.group(1) + ")"));
                            bonuses.add(createProvidedTrait(multipleBonusMatcher.group(3) + " (" + multipleBonusMatcher.group(1) + ")"));
                        }

                        Matcher singlePenaltyMatcher = SINGLE_PENALTY_PATTERN.matcher(text);
                        while (singlePenaltyMatcher.find()) {
                            bonuses.add(createProvidedTrait(singlePenaltyMatcher.group(2) + " (" + singlePenaltyMatcher.group(1) + ")"));
                        }

                        Matcher doublePenaltyMatcher = DOUBLE_PENALTY_PATTERN.matcher(text);
                        while (doublePenaltyMatcher.find()) {
                            bonuses.add(createProvidedTrait(doublePenaltyMatcher.group(2) + " (" + doublePenaltyMatcher.group(1) + ")"));
                            bonuses.add(createProvidedTrait(doublePenaltyMatcher.group(3) + " (" + doublePenaltyMatcher.group(1) + ")"));
                        }

                        Matcher manyPenaltyMatcher = MANY_PENALTY_PATTERN.matcher(text);
                        while (manyPenaltyMatcher.find()) {
                            bonuses.add(createProvidedTrait(manyPenaltyMatcher.group(2) + " (" + manyPenaltyMatcher.group(1) + ")"));
                            bonuses.add(createProvidedTrait(manyPenaltyMatcher.group(3) + " (" + manyPenaltyMatcher.group(1) + ")"));
                            bonuses.add(createProvidedTrait(manyPenaltyMatcher.group(4) + " (" + manyPenaltyMatcher.group(1) + ")"));
                        }
                    }
                }
            }
        }


        bonuses.addAll(getAgeStatMods(!speciesName.toLowerCase().contains("droid")));
        bonuses.addAll(getDroidSizeMods(speciesName.toLowerCase().contains("droid")));
        bonuses = Util.mergeBonuses(bonuses);
        //
        //keys.addAll(bonusMap.keySet().stream().map(key -> key + " : " + ));
        //final List<Object> objects = Util.mergeBonuses(bonuses);
        return bonuses;
    }

    private static ProvidedItem createProvidedTrait(String itemName, String... prerequisite) {
        return create(itemName, TRAIT, prerequisite);
    }

    private static Collection<?> getDroidSizeMods(boolean droid)
    {
        Set<ProvidedItem> bonuses = new HashSet<>();
        if(!droid){
            return bonuses;
        }

        bonuses.add(createProvidedTrait("Dexterity (+8)", "TRAIT:Fine"));
        bonuses.add(createProvidedTrait("Strength (-8)", "TRAIT:Fine"));

        bonuses.add(createProvidedTrait("Dexterity (+6)", "TRAIT:Diminutive"));
        bonuses.add(createProvidedTrait("Strength (-6)", "TRAIT:Diminutive"));

        bonuses.add(createProvidedTrait("Dexterity (+4)", "TRAIT:Tiny"));
        bonuses.add(createProvidedTrait("Strength (-4)", "TRAIT:Tiny"));

        bonuses.add(createProvidedTrait("Dexterity (+2)", "TRAIT:Small"));
        bonuses.add(createProvidedTrait("Strength (-2)", "TRAIT:Small"));

        bonuses.add(createProvidedTrait("Dexterity (-2)", "TRAIT:Large"));
        bonuses.add(createProvidedTrait("Strength (+8)", "TRAIT:Large"));

        bonuses.add(createProvidedTrait("Dexterity (-4)", "TRAIT:Huge"));
        bonuses.add(createProvidedTrait("Strength (+16)", "TRAIT:Huge"));

        bonuses.add(createProvidedTrait("Dexterity (-4)", "TRAIT:Gargantuan"));
        bonuses.add(createProvidedTrait("Strength (+24)", "TRAIT:Gargantuan"));

        bonuses.add(createProvidedTrait("Dexterity (-4)", "TRAIT:Colossal"));
        bonuses.add(createProvidedTrait("Strength (+32)", "TRAIT:Colossal"));
        return bonuses;
    }

    private static Collection<?> getAgeStatMods(boolean hasAgeRanges)
    {
        if(!hasAgeRanges){
            return new ArrayList<>();
        }
        if(standardAgeBonuses != null){
            return standardAgeBonuses;
        }

        standardAgeBonuses = new ArrayList<>();
        standardAgeBonuses.add(createProvidedTrait("Strength (-3)", "TRAIT:Child"));
        standardAgeBonuses.add(createProvidedTrait("Constitution (-3)", "TRAIT:Child"));
        standardAgeBonuses.add(createProvidedTrait("Dexterity (-1)", "TRAIT:Child"));
        standardAgeBonuses.add(createProvidedTrait("Intelligence (-1)", "TRAIT:Child"));
        standardAgeBonuses.add(createProvidedTrait("Wisdom (-1)", "TRAIT:Child"));
        standardAgeBonuses.add(createProvidedTrait("Charisma (-1)", "TRAIT:Child"));

        standardAgeBonuses.add(createProvidedTrait("Strength (-1)", "TRAIT:Young adult"));
        standardAgeBonuses.add(createProvidedTrait("Constitution (-1)", "TRAIT:Young adult"));
        standardAgeBonuses.add(createProvidedTrait("Dexterity (-1)", "TRAIT:Young adult"));
        standardAgeBonuses.add(createProvidedTrait("Intelligence (-1)", "TRAIT:Young adult"));
        standardAgeBonuses.add(createProvidedTrait("Wisdom (-1)", "TRAIT:Young adult"));
        standardAgeBonuses.add(createProvidedTrait("Charisma (-1)", "TRAIT:Young adult"));

        standardAgeBonuses.add(createProvidedTrait("Strength (-1)", "TRAIT:Middle age"));
        standardAgeBonuses.add(createProvidedTrait("Constitution (-1)", "TRAIT:Middle age"));
        standardAgeBonuses.add(createProvidedTrait("Dexterity (-1)", "TRAIT:Middle age"));
        standardAgeBonuses.add(createProvidedTrait("Intelligence (+1)", "TRAIT:Middle age"));
        standardAgeBonuses.add(createProvidedTrait("Wisdom (+1)", "TRAIT:Middle age"));
        standardAgeBonuses.add(createProvidedTrait("Charisma (+1)", "TRAIT:Middle age"));

        standardAgeBonuses.add(createProvidedTrait("Strength (-2)", "TRAIT:Old"));
        standardAgeBonuses.add(createProvidedTrait("Constitution (-2)", "TRAIT:Old"));
        standardAgeBonuses.add(createProvidedTrait("Dexterity (-2)", "TRAIT:Old"));
        standardAgeBonuses.add(createProvidedTrait("Intelligence (+1)", "TRAIT:Old"));
        standardAgeBonuses.add(createProvidedTrait("Wisdom (+1)", "TRAIT:Old"));
        standardAgeBonuses.add(createProvidedTrait("Charisma (+1)", "TRAIT:Old"));

        standardAgeBonuses.add(createProvidedTrait("Strength (-3)", "TRAIT:Venerable"));
        standardAgeBonuses.add(createProvidedTrait("Constitution (-3)", "TRAIT:Venerable"));
        standardAgeBonuses.add(createProvidedTrait("Dexterity (-3)", "TRAIT:Venerable"));
        standardAgeBonuses.add(createProvidedTrait("Intelligence (+1)", "TRAIT:Venerable"));
        standardAgeBonuses.add(createProvidedTrait("Wisdom (+1)", "TRAIT:Venerable"));
        standardAgeBonuses.add(createProvidedTrait("Charisma (+1)", "TRAIT:Venerable"));
        standardAgeBonuses = Util.mergeBonuses(standardAgeBonuses);

        return standardAgeBonuses;
    }
}
