package swse.character_class;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.Change;
import swse.common.ChangeKey;

public class DefenceBonuses
{
    public static final Pattern REFLEX_PATTERN = Pattern.compile("\\+(\\d*) (class )?bonus to (both )?(their|your) (fortitude defense, |will defense, )?reflex defense");
    public static final Pattern FORTITUDE_PATTERN = Pattern.compile("\\+(\\d*) (class )?bonus to (both )?(their|your) (reflex defense|will defense, reflex defense)?(, | and their |, and )?fortitude defense");
    public static final Pattern WILL_PATTERN = Pattern.compile("\\+(\\d*) (class )?bonus to (both )?(their|your) (fortitude defense, reflex defense, and |reflex defense, fortitude defense, and |reflex defense, fortitude defense and )?will defense");

    public static List<Change> getDefenseBonuses(Elements entries, String itemName)
    {
        List<Change> changes = new ArrayList<>();
        boolean found = false;
        boolean allowP = true;

        for (Element entry : entries)
        {
            String input = entry.text().toLowerCase();
            if (found)
            {
                if (allowP && entry.tag().equals(Tag.valueOf("p")))
                {
                    allowP = false;
                    //printUnique(input);

                    Matcher fortMatcher = FORTITUDE_PATTERN.matcher(input);
                    if (fortMatcher.find())
                    {
                        changes.add(Change.create(ChangeKey.FORTITUDE_DEFENSE_BONUS_CLASS, Integer.parseInt(fortMatcher.group(1))));
                    }

                    Matcher reflexMatcher = REFLEX_PATTERN.matcher(input);
                    if (reflexMatcher.find())
                    {
                        changes.add(Change.create(ChangeKey.REFLEX_DEFENSE_BONUS_CLASS, Integer.parseInt(reflexMatcher.group(1))));
                    }

                    Matcher willMatcher = WILL_PATTERN.matcher(input);
                    if (willMatcher.find())
                    {
                        changes.add(Change.create(ChangeKey.CLASS_WILL_DEFENSE_BONUS, Integer.parseInt(willMatcher.group(1))));
                    }
                }
            } else if ((entry.tag().equals(Tag.valueOf("h4")) || entry.tag().equals(Tag.valueOf("p"))) && input.contains("defense bonuses"))
            {
                found = true;
            }
        }
        //System.out.println(itemName);
       // attributes.forEach(System.out::println);
        return changes;
    }
}
