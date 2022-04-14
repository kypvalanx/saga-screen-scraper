package swse.character_class;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.Attribute;
import swse.common.AttributeKey;

class ForcePoints
{
    private final int forcePoints;

    public ForcePoints(int forcePoints)
    {
        this.forcePoints = forcePoints;
    }

    static List<Attribute> getForcePoints(Elements entries)
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

                    Pattern forcePointsPattern = Pattern.compile("force points equal to (\\d*)");

                    Matcher m = forcePointsPattern.matcher(entry.text().toLowerCase());

                    if (m.find())
                    {
                        attributes.add(Attribute.create(AttributeKey.CLASS_FORCE_POINTS, Integer.parseInt(m.group(1))));
                    }
                }
            } else if ((entry.tag().equals(Tag.valueOf("h4")) || entry.tag().equals(Tag.valueOf("h3"))) && entry.text().toLowerCase().contains("force points"))
            {
                found = true;
            }
        }
        return attributes;
    }

    @Override
    public String toString()
    {
        return "ForcePoints{" +
                "forcePoints=" + forcePoints +
                '}';
    }

    public int toJSON()
    {
        return forcePoints;
    }
}
