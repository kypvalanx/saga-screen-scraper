package swse.starshipManeuvers;

import org.json.JSONObject;
import org.jsoup.nodes.*;
import swse.common.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StarshipManeuverExporter extends BaseExporter
{
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\starship maneuvers.json";
    private static List<String> allManeuvers = new ArrayList<>();

    public static void main(String[] args)
    {
        List<String> links = new ArrayList<String>();
        links.add("/wiki/Category:Starship_Maneuvers");

        List<JSONObject> entries = new StarshipManeuverExporter().getEntriesFromCategoryPage(links, true);
        printUniqueNames(entries);

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"), "Starship Maneuvers");
    }


    protected List<JSONy> parseItem(String itemLink, boolean overwrite, List<String> filter, List<String> nameFilter)
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


        if ("home".equals(itemName.toLowerCase()) || "Force Powers".equals(itemName))
        {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        allManeuvers.add(itemName);

        return List.of(StarshipManeuver.create(itemName)
                .with(createCumulativeChecks(content))
                .with(getTags(content))
                .with(getTime(content))
                .with(getTargets(content))
                .with(getRollType(content))
                .withDescription(content)
        );
    }

}
