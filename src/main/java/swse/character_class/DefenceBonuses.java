package swse.character_class;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.JSONy;
import swse.common.Attribute;

class DefenceBonuses implements JSONy
{
    private final Integer reflex;
    private final Integer fortitude;
    private final Integer will;
    public static final Pattern REFLEX_PATTERN = Pattern.compile("\\+(\\d*) (class )?bonus to (their|your) (fortitude defense, )?reflex defense");
    public static final Pattern FORTITUDE_PATTERN = Pattern.compile("\\+(\\d*) (class )?bonus to (their|your) fortitude defense");
    public static final Pattern WILL_PATTERN = Pattern.compile("\\+(\\d*) (class )?bonus to (their|your) (fortitude defense, reflex defense, and )?will defense");

    public DefenceBonuses(Integer reflex, Integer fortitude, Integer will)
    {
        this.reflex = reflex;
        this.fortitude = fortitude;
        this.will = will;
    }

    static List<Attribute> getDefenseBonuses(Elements entries)
    {
        List<Attribute> attributes = new ArrayList<>();
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

                    Matcher fortMatcher = FORTITUDE_PATTERN.matcher(input);
                    if (fortMatcher.find())
                    {
                        attributes.add(Attribute.create("classFortitudeDefenseBonus", Integer.parseInt(fortMatcher.group(1))));
                    }

                    Matcher reflexMatcher = REFLEX_PATTERN.matcher(input);
                    if (reflexMatcher.find())
                    {
                        attributes.add(Attribute.create("classReflexDefenseBonus", Integer.parseInt(reflexMatcher.group(1))));
                    }

                    Matcher willMatcher = WILL_PATTERN.matcher(input);
                    if (willMatcher.find())
                    {
                        attributes.add(Attribute.create("classWillDefenseBonus", Integer.parseInt(willMatcher.group(1))));
                    }
                }
            } else if ((entry.tag().equals(Tag.valueOf("h4")) || entry.tag().equals(Tag.valueOf("p"))) && input.contains("defense bonuses"))
            {
                found = true;
            }
        }

        return attributes;
    }

    @Override
    public String toString()
    {
        return "DefenceBonuses{" +
                "reflex=" + reflex +
                ", fortitude=" + fortitude +
                ", will=" + will +
                '}';
    }

    @Nonnull
    public JSONObject toJSON()
    {
        return new JSONObject().put("reflex", reflex).put("fortitude",fortitude).put("will", will);
    }
}
