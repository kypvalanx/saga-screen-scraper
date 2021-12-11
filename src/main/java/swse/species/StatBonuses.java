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
        List<String> weirdSpecies = Lists.newArrayList("Devaronian", "Melodie", "Ruurian", "Rybet", "Arkanian Offshoot", "Republic Clone");


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
                        bonuses.add(ProvidedItem.create("-2 Constitution", ItemType.TRAIT));
                        bonuses.add(new Choice("Select an Attribute Bonus")
                                .withOption("Strength", new Option().withProvidedItem(ProvidedItem.create("+2 Strength", ItemType.TRAIT)))
                                .withOption("Dexterity", new Option().withProvidedItem(ProvidedItem.create("+2 Dexterity", ItemType.TRAIT))));
                    } else if("Melodie".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("+2 Constitution", ItemType.TRAIT));

                        bonuses.add(ProvidedItem.create("+2 Wisdom", ItemType.TRAIT, "TRAIT:Child"));
                        bonuses.add(ProvidedItem.create("-2 Strength", ItemType.TRAIT, "TRAIT:Child"));

                        bonuses.add(ProvidedItem.create("+2 Wisdom", ItemType.TRAIT, "TRAIT:Young adult"));
                        bonuses.add(ProvidedItem.create("-2 Strength", ItemType.TRAIT, "TRAIT:Young adult"));

                        bonuses.add(ProvidedItem.create("+2 Charisma", ItemType.TRAIT, "TRAIT:Adult"));
                        bonuses.add(ProvidedItem.create("-4 Dexterity", ItemType.TRAIT, "TRAIT:Adult"));

                        bonuses.add(ProvidedItem.create("+2 Charisma", ItemType.TRAIT, "TRAIT:Middle age"));
                        bonuses.add(ProvidedItem.create("-4 Dexterity", ItemType.TRAIT, "TRAIT:Middle age"));

                        bonuses.add(ProvidedItem.create("+2 Charisma", ItemType.TRAIT, "TRAIT:Old"));
                        bonuses.add(ProvidedItem.create("-4 Dexterity", ItemType.TRAIT, "TRAIT:Old"));

                        bonuses.add(ProvidedItem.create("+2 Charisma", ItemType.TRAIT, "TRAIT:Venerable"));
                        bonuses.add(ProvidedItem.create("-4 Dexterity", ItemType.TRAIT, "TRAIT:Venerable"));

                    } else if("Devaronian".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("+2 Dexterity", ItemType.TRAIT, "GENDER:Male"));
                        bonuses.add(ProvidedItem.create("-2 Wisdom", ItemType.TRAIT, "GENDER:Male"));
                        bonuses.add(ProvidedItem.create("-2 Charisma", ItemType.TRAIT, "GENDER:Male"));

                        bonuses.add(ProvidedItem.create("+2 Intelligence", ItemType.TRAIT, "GENDER:Female"));
                        bonuses.add(ProvidedItem.create("+2 Wisdom", ItemType.TRAIT, "GENDER:Female"));
                        bonuses.add(ProvidedItem.create("-2 Dexterity", ItemType.TRAIT, "GENDER:Female"));
                    } else if("Ruurian".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("-2 Strength", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("-2 Constitution", ItemType.TRAIT));

                        bonuses.add(ProvidedItem.create("+4 Intelligence", ItemType.TRAIT, "TRAIT:Child"));

                        bonuses.add(ProvidedItem.create("+4 Intelligence", ItemType.TRAIT, "TRAIT:Young adult"));

                        bonuses.add(ProvidedItem.create("+4 Intelligence", ItemType.TRAIT, "TRAIT:Adult"));

                        bonuses.add(ProvidedItem.create("+4 Charisma", ItemType.TRAIT, "TRAIT:Middle age"));

                        bonuses.add(ProvidedItem.create("+4 Charisma", ItemType.TRAIT, "TRAIT:Old"));

                        bonuses.add(ProvidedItem.create("+4 Charisma", ItemType.TRAIT, "TRAIT:Venerable"));

                    } else if("Rybet".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("+2 Dexterity", ItemType.TRAIT, "GENDER:Male"));
                        bonuses.add(ProvidedItem.create("-2 Strength", ItemType.TRAIT, "GENDER:Male"));

                        bonuses.add(ProvidedItem.create("+2 Strength", ItemType.TRAIT, "GENDER:Female"));
                        bonuses.add(ProvidedItem.create("-2 Dexterity", ItemType.TRAIT, "GENDER:Female"));

                    } else if("Republic Clone".equals(speciesName)){
                        bonuses.add(ProvidedItem.create("+5 Strength", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("+3 Dexterity", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("+2 Intelligence", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("-2 Charisma", ItemType.TRAIT));
                        bonuses.add(ProvidedItem.create("Disable Attribute Modification", ItemType.TRAIT));

                        bonuses.add(new Choice("Select an Attribute Bonus")
                                .withOption("Strength", new Option().withProvidedItem(ProvidedItem.create("+2 Strength", ItemType.TRAIT)))
                                .withOption("Dexterity", new Option().withProvidedItem(ProvidedItem.create("+2 Dexterity", ItemType.TRAIT)))
                                .withOption("Constitution", new Option().withProvidedItem(ProvidedItem.create("+2 Constitution", ItemType.TRAIT)))
                                .withOption("Intelligence", new Option().withProvidedItem(ProvidedItem.create("+2 Intelligence", ItemType.TRAIT)))
                                .withOption("Wisdom", new Option().withProvidedItem(ProvidedItem.create("+2 Wisdom", ItemType.TRAIT)))
                                .withOption("Charisma", new Option().withProvidedItem(ProvidedItem.create("+2 Charisma", ItemType.TRAIT))));
                    }

                    //System.out.println(speciesName);
                    continue;
                }


                final String s = "(Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma)";
                Pattern singleBonusPattern = Pattern.compile("(\\+\\d*)(?: bonus)? to (?:their )?" + s);

                Matcher singleBonusMatcher = singleBonusPattern.matcher(text);
                while (singleBonusMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(singleBonusMatcher.group(1) + " " + singleBonusMatcher.group(2), ItemType.TRAIT));
                }

                Pattern multipleBonusPattern = Pattern.compile("(\\+\\d*) bonus(?:es)? to (?:both )?(?:their )?"+s+"(?:,)? and (?:their )?"+s);

                Matcher multipleBonusMatcher = multipleBonusPattern.matcher(text);
                while (multipleBonusMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(multipleBonusMatcher.group(1) + " " + multipleBonusMatcher.group(2), ItemType.TRAIT));
                    bonuses.add(ProvidedItem.create(multipleBonusMatcher.group(1) + " " + multipleBonusMatcher.group(3), ItemType.TRAIT));
                }

                Pattern singlePenaltyPattern = Pattern.compile("(-\\d*) penalty to (?:their )?"+s);

                Matcher singlePenaltyMatcher = singlePenaltyPattern.matcher(text);
                while (singlePenaltyMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(singlePenaltyMatcher.group(1) + " " + singlePenaltyMatcher.group(2), ItemType.TRAIT));
                }

                Pattern doublePenaltyPattern = Pattern.compile("(-\\d*) (?:penalties|penalty) to (?:both )?(?:their )?"+s+" and "+s);

                Matcher doublePenaltyMatcher = doublePenaltyPattern.matcher(text);
                while (doublePenaltyMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(doublePenaltyMatcher.group(1) + " " + doublePenaltyMatcher.group(2), ItemType.TRAIT));
                    bonuses.add(ProvidedItem.create(doublePenaltyMatcher.group(1) + " " + doublePenaltyMatcher.group(3), ItemType.TRAIT));
                }

                Pattern manyPenaltyPattern = Pattern.compile("(-\\d*) penalties to (?:their )?"+s+", "+s+", and "+s);

                Matcher manyPenaltyMatcher = manyPenaltyPattern.matcher(text);
                while (manyPenaltyMatcher.find())
                {
                    bonuses.add(ProvidedItem.create(manyPenaltyMatcher.group(1) + " " + manyPenaltyMatcher.group(2), ItemType.TRAIT));
                    bonuses.add(ProvidedItem.create(manyPenaltyMatcher.group(1) + " " + manyPenaltyMatcher.group(3), ItemType.TRAIT));
                    bonuses.add(ProvidedItem.create(manyPenaltyMatcher.group(1) + " " + manyPenaltyMatcher.group(4), ItemType.TRAIT));
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

        bonuses.add(ProvidedItem.create("+2 Dexterity", ItemType.TRAIT,"TRAIT:Small"));
        bonuses.add(ProvidedItem.create("-2 Strength", ItemType.TRAIT, "TRAIT:Small"));
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
        standardAgeBonuses.add(ProvidedItem.create("-3 Strength", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("-3 Constitution", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Dexterity", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Intelligence", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Wisdom", ItemType.TRAIT, "TRAIT:Child"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Charisma", ItemType.TRAIT, "TRAIT:Child"));

        standardAgeBonuses.add(ProvidedItem.create("-1 Strength", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Constitution", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Dexterity", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Intelligence", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Wisdom", ItemType.TRAIT, "TRAIT:Young adult"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Charisma", ItemType.TRAIT, "TRAIT:Young adult"));

        standardAgeBonuses.add(ProvidedItem.create("-1 Strength", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Constitution", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("-1 Dexterity", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Intelligence", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Wisdom", ItemType.TRAIT, "TRAIT:Middle age"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Charisma", ItemType.TRAIT, "TRAIT:Middle age"));

        standardAgeBonuses.add(ProvidedItem.create("-2 Strength", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("-2 Constitution", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("-2 Dexterity", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Intelligence", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Wisdom", ItemType.TRAIT, "TRAIT:Old"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Charisma", ItemType.TRAIT, "TRAIT:Old"));

        standardAgeBonuses.add(ProvidedItem.create("-3 Strength", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("-3 Constitution", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("-3 Dexterity", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Intelligence", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Wisdom", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses.add(ProvidedItem.create("+1 Charisma", ItemType.TRAIT, "TRAIT:Venerable"));
        standardAgeBonuses = Util.mergeBonuses(standardAgeBonuses);

        return standardAgeBonuses;
    }
}
