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

class ForcePoints
{
    private final int forcePoints;

    public ForcePoints(int forcePoints)
    {
        this.forcePoints = forcePoints;
    }

    static List<Change> getForcePoints(Elements entries)
    {
        List<Change> changes = new ArrayList<>();
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
                        changes.add(Change.create(ChangeKey.CLASS_FORCE_POINTS, Integer.parseInt(m.group(1))));
                    }
                }
            } else if ((entry.tag().equals(Tag.valueOf("h4")) || entry.tag().equals(Tag.valueOf("h3"))) && entry.text().toLowerCase().contains("force points"))
            {
                found = true;
            }
        }
        return changes;
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
