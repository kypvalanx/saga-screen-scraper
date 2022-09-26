package swse.species;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
import swse.common.Choice;
import swse.common.ItemType;
import swse.common.Option;
import swse.common.ProvidedItem;
import swse.util.Util;

class StatBonuses
{
    private static List<Object> standardAgeBonuses;

    public static List<Object> getStatBonuses(Element content, String speciesName)
    {
        List<Object> bonuses = new ArrayList<>();
        Map<String, Map<String, Integer>> bonusMap = new HashMap<>();
        List<String> weirdSpecies = Lists.newArrayList("Devaronian", "Melodie", "Ruurian", "Rybet", "Arkanian Offshoot", "Republic Clone", "Hutt");


        String subset = "all";

        for (Element element : content.select("li,p"))
        {
            String text = element.text();
            if (text.toLowerCase().startsWith("ability modifier") || text.toLowerCase().startsWith("ability scores"))
            {
                if (text.contains("no Ability Score adjustments") || text.contains("no bonuses or penalties to their Ability Scores") || text.contains("None. "))
                {
                    continue;
                }
                if (weirdSpecies.contains(speciesName))
                {
                    if("Arkanian Offshoot".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("Constitution (-2)", ItemType.TRAIT));
                        bonuses.add(new Choice("Select an Attribute Bonus")
                                .withShowSelectionInName(false)
                                .withOption("Strength", new Option().withProvidedItem(ProvidedItem.create("Strength (+2)", ItemType.TRAIT)))
                                .withOption("Dexterity", new Option().withProvidedItem(ProvidedItem.create("Dexterity (+2)", ItemType.TRAIT))));
                    } else if("Melodie".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("Constitution (+2)", ItemType.TRAIT));

                        bonuses.add(ProvidedItem.create("Wisdom (+2)", ItemType.TRAIT, "TRAIT:Child"));
                        bonuses.add(ProvidedItem.create("Strength (-2)", ItemType.TRAIT, "TRAIT:Child"));

                        bonuses.add(ProvidedItem.create("Wisdom (+2)", ItemType.TRAIT, "TRAIT:Young adult"));
                        bonuses.add(ProvidedItem.create("Strength (-2)", ItemType.TRAIT, "TRAIT:Young adult"));

                        bonuses.add(ProvidedItem.create("Charisma (+2)", ItemType.TRAIT, "TRAIT:Adult"));
                        bonuses.add(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT, "TRAIT:Adult"));

                        bonuses.add(ProvidedItem.create("Charisma (+2)", ItemType.TRAIT, "TRAIT:Middle age"));
                        bonuses.add(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT, "TRAIT:Middle age"));

                        bonuses.add(ProvidedItem.create("Charisma (+2)", ItemType.TRAIT, "TRAIT:Old"));
                        bonuses.add(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT, "TRAIT:Old"));

                        bonuses.add(ProvidedItem.create("Charisma (+2)", ItemType.TRAIT, "TRAIT:Venerable"));
                        bonuses.add(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT, "TRAIT:Venerable"));

                    } else if("Devaronian".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("Dexterity (+2)", ItemType.TRAIT, "GENDER:Male"));
                        bonuses.add(ProvidedItem.create("Wisdom (-2)", ItemType.TRAIT, "GENDER:Male"));
                        bonuses.add(ProvidedItem.create("Charisma (-2)", ItemType.TRAIT, "GENDER:Male"));

                        bonuses.add(ProvidedItem.create("Intelligence (+2)", ItemType.TRAIT, "GENDER:Female"));
                        bonuses.add(ProvidedItem.create("Wisdom (+2)", ItemType.TRAIT, "GENDER:Female"));
                        bonuses.add(ProvidedItem.create("Dexterity (-2)", ItemType.TRAIT, "GENDER:Female"));
                    } else if("Ruurian".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("Strength (-2)", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Constitution (-2)", ItemType.TRAIT));

                        bonuses.add(ProvidedItem.create("Intelligence (+4)", ItemType.TRAIT, "TRAIT:Child"));

                        bonuses.add(ProvidedItem.create("Intelligence (+4)", ItemType.TRAIT, "TRAIT:Young adult"));

                        bonuses.add(ProvidedItem.create("Intelligence (+4)", ItemType.TRAIT, "TRAIT:Adult"));

                        bonuses.add(ProvidedItem.create("Charisma (+4)", ItemType.TRAIT, "TRAIT:Middle age"));

                        bonuses.add(ProvidedItem.create("Charisma (+4)", ItemType.TRAIT, "TRAIT:Old"));

                        bonuses.add(ProvidedItem.create("Charisma (+4)", ItemType.TRAIT, "TRAIT:Venerable"));

                    } else if("Rybet".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("Dexterity (+2)", ItemType.TRAIT, "GENDER:Male"));
                        bonuses.add(ProvidedItem.create("Strength (-2)", ItemType.TRAIT, "GENDER:Male"));

                        bonuses.add(ProvidedItem.create("Strength (+2)", ItemType.TRAIT, "GENDER:Female"));
                        bonuses.add(ProvidedItem.create("Dexterity (-2)", ItemType.TRAIT, "GENDER:Female"));

                    } else if("Republic Clone".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("Strength (+5)", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Dexterity (+3)", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Intelligence (+2)", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Charisma (-2)", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Disable Attribute Modification", ItemType.TRAIT));

                        bonuses.add(new Choice("Select an Attribute Bonus")
                                .withShowSelectionInName(false)
                                .withOption("Strength", new Option().withProvidedItem(ProvidedItem.create("Strength (+2)", ItemType.TRAIT)))
                                .withOption("Dexterity", new Option().withProvidedItem(ProvidedItem.create("Dexterity (+2)", ItemType.TRAIT)))
                                .withOption("Constitution", new Option().withProvidedItem(ProvidedItem.create("Constitution (+2)", ItemType.TRAIT)))
                                .withOption("Intelligence", new Option().withProvidedItem(ProvidedItem.create("Intelligence (+2)", ItemType.TRAIT)))
                                .withOption("Wisdom", new Option().withProvidedItem(ProvidedItem.create("Wisdom (+2)", ItemType.TRAIT)))
                                .withOption("Charisma", new Option().withProvidedItem(ProvidedItem.create("Charisma (+2)", ItemType.TRAIT))));
                    } else if ("Hutt".equals(speciesName)) {

                        bonuses.add(ProvidedItem.create("Constitution (+2)", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Intelligence (+2)", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Strength (+2)", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Dexterity (-6)", ItemType.TRAIT));
                    }

                    //System.out.println(speciesName);
                    continue;
                }


                final String s = "(Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma)";
                Pattern singleBonusPattern = Pattern.compile("(\\+\\d*)(?: bonus)? to (?:their )?" + s);

                Matcher singleBonusMatcher = singleBonusPattern.matcher(text);
                while (singleBonusMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(singleBonusMatcher.group(2) + " (" + singleBonusMatcher.group(1) + ")", ItemType.TRAIT));
                }

                Pattern multipleBonusPattern = Pattern.compile("(\\+\\d*) bonus(?:es)? to (?:both )?(?:their )?"+s+"(?:,)? and (?:their )?"+s);

                Matcher multipleBonusMatcher = multipleBonusPattern.matcher(text);
                while (multipleBonusMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(multipleBonusMatcher.group(2) + " (" + multipleBonusMatcher.group(1) + ")", ItemType.TRAIT));
                    bonuses.add(ProvidedItem.create(multipleBonusMatcher.group(3) + " (" + multipleBonusMatcher.group(1) + ")", ItemType.TRAIT));
                }

                Pattern singlePenaltyPattern = Pattern.compile("(-\\d*) penalty to (?:their )?"+s);

                Matcher singlePenaltyMatcher = singlePenaltyPattern.matcher(text);
                while (singlePenaltyMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(singlePenaltyMatcher.group(2) + " (" + singlePenaltyMatcher.group(1) + ")", ItemType.TRAIT));
                }

                Pattern doublePenaltyPattern = Pattern.compile("(-\\d*) (?:penalties|penalty) to (?:both )?(?:their )?"+s+" and "+s);

                Matcher doublePenaltyMatcher = doublePenaltyPattern.matcher(text);
                while (doublePenaltyMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(doublePenaltyMatcher.group(2) + " (" + doublePenaltyMatcher.group(1) + ")", ItemType.TRAIT));
                    bonuses.add(ProvidedItem.create(doublePenaltyMatcher.group(3) + " (" + doublePenaltyMatcher.group(1) + ")", ItemType.TRAIT));
                }

                Pattern manyPenaltyPattern = Pattern.compile("(-\\d*) penalties to (?:their )?"+s+", "+s+", and "+s);

                Matcher manyPenaltyMatcher = manyPenaltyPattern.matcher(text);
                while (manyPenaltyMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(manyPenaltyMatcher.group(2) + " (" + manyPenaltyMatcher.group(1) + ")", ItemType.TRAIT));
                    bonuses.add(ProvidedItem.create(manyPenaltyMatcher.group(3) + " (" + manyPenaltyMatcher.group(1) + ")", ItemType.TRAIT));
                    bonuses.add(ProvidedItem.create(manyPenaltyMatcher.group(4) + " (" + manyPenaltyMatcher.group(1) + ")", ItemType.TRAIT));
                }
            }
        }
        bonuses = Util.mergeBonuses(bonuses);

        bonuses.addAll(getAgeStatMods(!speciesName.toLowerCase().contains("droid")));
        bonuses.addAll(getDroidSizeMods(speciesName.toLowerCase().contains("droid")));
        //
        //keys.addAll(bonusMap.keySet().stream().map(key -> key + " : " + ));
        //final List<Object> objects = Util.mergeBonuses(bonuses);
        return bonuses;
    }

    private static Collection<?> getDroidSizeMods(boolean droid)
    {
        Set<ProvidedItem> bonuses = new HashSet<>();
        if(!droid){
            return bonuses;
        }

        bonuses.add(ProvidedItem.create("Dexterity (+8)", ItemType.TRAIT,"TRAIT:Fine"));
        bonuses.add(ProvidedItem.create("Strength (-8)", ItemType.TRAIT, "TRAIT:Fine"));

        bonuses.add(ProvidedItem.create("Dexterity (+6)", ItemType.TRAIT,"TRAIT:Diminutive"));
        bonuses.add(ProvidedItem.create("Strength (-6)", ItemType.TRAIT, "TRAIT:Diminutive"));

        bonuses.add(ProvidedItem.create("Dexterity (+4)", ItemType.TRAIT,"TRAIT:Tiny"));
        bonuses.add(ProvidedItem.create("Strength (-4)", ItemType.TRAIT, "TRAIT:Tiny"));

        bonuses.add(ProvidedItem.create("Dexterity (+2)", ItemType.TRAIT,"TRAIT:Small"));
        bonuses.add(ProvidedItem.create("Strength (-2)", ItemType.TRAIT, "TRAIT:Small"));

        bonuses.add(ProvidedItem.create("Dexterity (-2)", ItemType.TRAIT,"TRAIT:Large"));
        bonuses.add(ProvidedItem.create("Strength (+8)", ItemType.TRAIT, "TRAIT:Large"));

        bonuses.add(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT,"TRAIT:Huge"));
        bonuses.add(ProvidedItem.create("Strength (+16)", ItemType.TRAIT, "TRAIT:Huge"));

        bonuses.add(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT,"TRAIT:Gargantuan"));
        bonuses.add(ProvidedItem.create("Strength (+24)", ItemType.TRAIT, "TRAIT:Gargantuan"));

        bonuses.add(ProvidedItem.create("Dexterity (-4)", ItemType.TRAIT,"TRAIT:Colossal"));
        bonuses.add(ProvidedItem.create("Strength (+32)", ItemType.TRAIT, "TRAIT:Colossal"));
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
        standardAgeBonuses.add(ProvidedItem.create("Strength (-3)", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("Constitution (-3)", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("Dexterity (-1)", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("Intelligence (-1)", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("Wisdom (-1)", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("Charisma (-1)", ItemType.TRAIT, "TRAIT:Child"));

        standardAgeBonuses.add(ProvidedItem.create("Strength (-1)", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("Constitution (-1)", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("Dexterity (-1)", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("Intelligence (-1)", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("Wisdom (-1)", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("Charisma (-1)", ItemType.TRAIT, "TRAIT:Young adult"));

        standardAgeBonuses.add(ProvidedItem.create("Strength (-1)", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("Constitution (-1)", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("Dexterity (-1)", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("Intelligence (+1)", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("Wisdom (+1)", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("Charisma (+1)", ItemType.TRAIT, "TRAIT:Middle age"));

        standardAgeBonuses.add(ProvidedItem.create("Strength (-2)", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("Constitution (-2)", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("Dexterity (-2)", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("Intelligence (+1)", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("Wisdom (+1)", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("Charisma (+1)", ItemType.TRAIT, "TRAIT:Old"));

        standardAgeBonuses.add(ProvidedItem.create("Strength (-3)", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("Constitution (-3)", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("Dexterity (-3)", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("Intelligence (+1)", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("Wisdom (+1)", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("Charisma (+1)", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses = Util.mergeBonuses(standardAgeBonuses);

        return standardAgeBonuses;
    }
}
