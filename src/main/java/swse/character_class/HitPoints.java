package swse.character_class;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.Attribute;

class HitPoints
{
    private final Integer firstLevelHitPoints;
    private final String levelUpHitPoints;

    public HitPoints(Integer firstLevelHitPoints, String levelUpHitPoints)
    {
        this.firstLevelHitPoints = firstLevelHitPoints;
        this.levelUpHitPoints = levelUpHitPoints;
    }

    static List<Attribute> getHitPoints(Elements entries)
    {
        List<Attribute> attributes = new ArrayList<>();
        boolean found = false;
        boolean allowP = true;
        for (Element entry : entries)
        {
            if (found)
            {
                if (allowP && entry.tag().equals(Tag.valueOf("p")))
                {
                    allowP = false;

                    Pattern firstLevelHitPointsPattern = Pattern.compile("begin play at 1st level with a number of hit points equal to (\\d*) \\+ (their )?constitution modifier");

                    Matcher m = firstLevelHitPointsPattern.matcher(entry.text().toLowerCase());

                    if (m.find())
                    {
                        attributes.add(Attribute.create("firstLevelHitPoints",Integer.parseInt(m.group(1))));
                    }

                    Pattern levelUpHitPointsPattern = Pattern.compile("(?:gain|hit points equal to) (\\d*d\\d*) (hit point|\\+ their)(s)?");

                    Matcher m2 = levelUpHitPointsPattern.matcher(entry.text().toLowerCase());

                    if (m2.find())
                    {
                        attributes.add(Attribute.create("levelUpHitPoints", m2.group(1)));
                    }

                }
            } else if ((entry.tag().equals(Tag.valueOf("h4")) || entry.tag().equals(Tag.valueOf("p"))) && entry.text().toLowerCase().contains("hit points"))
            {
                found = true;
            }
        }
        return attributes;
    }

    @Override
    public String toString()
    {
        return "HitPoints{" +
                "firstLevelHitPoints=" + firstLevelHitPoints +
                ", levelUpHitPoints='" + levelUpHitPoints + '\'' +
                '}';
    }

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("firstLevel", firstLevelHitPoints)
                .put("levelUp", levelUpHitPoints);
        return json;
    }
}
