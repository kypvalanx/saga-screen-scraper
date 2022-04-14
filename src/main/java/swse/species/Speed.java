package swse.species;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.ItemType;
import swse.common.ProvidedItem;
import swse.prerequisite.OrPrerequisite;
import swse.prerequisite.SimplePrerequisite;
import swse.util.Util;

public class Speed
{
    private static Collection<Object> defaultDroidSpeeds;

    public static Collection<Object> getSpeed(Element content, String name)
    {
        if(name.toLowerCase().contains("droid")){

            if(defaultDroidSpeeds != null){
                return defaultDroidSpeeds;
            }
            defaultDroidSpeeds = new ArrayList<>();
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (4)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Fine"));
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (4)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Diminutive"));
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (4)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Tiny"));
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (4)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Small"));
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (6)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Medium"));
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (8)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Large"));
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (8)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Huge"));
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (8)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Colossal"));
            defaultDroidSpeeds.add(ProvidedItem.create("Walking Speed (8)", ItemType.TRAIT, "EQUIPPED:Walking", "TRAIT:Gargantuan"));

            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (6)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Fine"));
            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (6)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Diminutive"));
            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (6)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Tiny"));
            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (6)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Small"));
            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (8)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Medium"));
            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (10)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Large"));
            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (10)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Huge"));
            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (10)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Colossal"));
            defaultDroidSpeeds.add(ProvidedItem.create("Wheeled Speed (10)", ItemType.TRAIT, "EQUIPPED:Wheeled", "TRAIT:Gargantuan"));

            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (4)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Fine"));
            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (4)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Diminutive"));
            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (4)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Tiny"));
            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (4)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Small"));
            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (6)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Medium"));
            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (8)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Large"));
            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (8)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Huge"));
            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (8)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Colossal"));
            defaultDroidSpeeds.add(ProvidedItem.create("Tracked Speed (8)", ItemType.TRAIT, "EQUIPPED:Tracked", "TRAIT:Gargantuan"));

            defaultDroidSpeeds.add(ProvidedItem.create("Hover Speed (6)", ItemType.TRAIT, "EQUIPPED:Hovering"));

            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (9)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Fine"));
            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (9)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Diminutive"));
            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (9)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Tiny"));
            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (9)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Small"));
            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (12)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Medium"));
            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (12)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Large"));
            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (12)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Huge"));
            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (12)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Colossal"));
            defaultDroidSpeeds.add(ProvidedItem.create("Fly Speed (12)", ItemType.TRAIT, "EQUIPPED:Flying", "TRAIT:Gargantuan"));

            defaultDroidSpeeds.add(ProvidedItem.create("Stationary Speed (0)", ItemType.TRAIT, "EQUIPPED:Stationary"));

            defaultDroidSpeeds = Util.mergeBonuses(defaultDroidSpeeds);

            return defaultDroidSpeeds;
        }

        if (name.equals("Aqualish"))
        {

            Collection<Object> categories = new ArrayList<>();
            categories.add(ProvidedItem.create("Base Speed (6)", ItemType.TRAIT));
            return categories;
        }

        if (name.equals("Ruurian"))
        {

            Collection<Object> categories = new ArrayList<>();
            categories.add(ProvidedItem.create("Base Speed (4)", ItemType.TRAIT));
            categories.add(ProvidedItem.create("Flying Speed (6)", ItemType.TRAIT, new OrPrerequisite("Chroma-Wing", List.of(new SimplePrerequisite("Middle Age", "TRAIT", "Middle Age"), new SimplePrerequisite("Venerable", "TRAIT", "Venerable")))));
            return categories;
        }

        Collection<ProvidedItem> categories = new ArrayList<>();
        Elements elements = content.select("p,li");
        Pattern baseSpeedPattern = Pattern.compile("base speed (?:of|is) (\\d*) squares");
        Pattern flySpeedPattern = Pattern.compile("fly speed (?:of|is) (\\d*) squares");
        Pattern swimSpeedPattern = Pattern.compile("swim speed (?:of|is) (\\d*) squares");
        Pattern walkingSpeedPattern = Pattern.compile("walking speed (?:of|is) (\\d*) squares");
        for (Element element : elements)
        {
            Matcher baseSpeedMatcher = baseSpeedPattern.matcher(element.text().toLowerCase());
            if (baseSpeedMatcher.find())
            {
                categories.add(ProvidedItem.create("Base Speed (" + baseSpeedMatcher.group(1) + ")", ItemType.TRAIT));
            }
            Matcher flySpeedMatcher = flySpeedPattern.matcher(element.text().toLowerCase());
            if (flySpeedMatcher.find())
            {
                categories.add(ProvidedItem.create("Fly Speed (" + flySpeedMatcher.group(1) + ")", ItemType.TRAIT));
            }
            Matcher swimSpeedMatcher = swimSpeedPattern.matcher(element.text().toLowerCase());
            if (swimSpeedMatcher.find())
            {
                categories.add(ProvidedItem.create("Swim Speed (" + swimSpeedMatcher.group(1) + ")", ItemType.TRAIT));
            }
            Matcher walkingSpeedMatcher = walkingSpeedPattern.matcher(element.text().toLowerCase());
            if (walkingSpeedMatcher.find())
            {
                categories.add(ProvidedItem.create("Walking Speed (" + walkingSpeedMatcher.group(1) + ")", ItemType.TRAIT));
            }
        }
        return Util.mergeBonuses(categories);
    }

}
