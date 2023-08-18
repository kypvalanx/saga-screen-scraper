package swse.character_class;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.Change;
import swse.common.AttributeKey;
import swse.common.Category;
import swse.common.ItemType;
import swse.common.ProvidedItem;

public class StartingFeats
{


    public static final Pattern BONUS_FEAT_PATTERN = compile("(?:Conditional )?Bonus Feat \\(([\\w\\s()]*)\\)");

    static List<Change> getStartingFeats(Elements entries, String itemName)
    {
        List<Change> changes = new ArrayList<>();
        boolean found = false;
        boolean allowUL = true;

        for (Element entry : entries)
        {
            if (found)
            {
                if (allowUL && entry.tag().equals(Tag.valueOf("ul")))
                {
                    allowUL = false;
                    found = false;
                    entry.select("li").stream()
                            .map(Element::text)
                            .map(StartingFeats::createAttribute).forEach(changes::add);
                    //attributes.addAll();

                } else if ((entry.tag().equals(Tag.valueOf("p")) && (entry.text().toLowerCase().startsWith("weapon proficiency") || entry.text().toLowerCase().startsWith("skill focus") || entry.text().toLowerCase().startsWith("technologist") || entry.text().toLowerCase().startsWith("force sensitivity") || entry.text().toLowerCase().startsWith("force training")|| entry.text().toLowerCase().startsWith("tech specialist")))
                        && !entry.text().toLowerCase().contains(" or "))
                {
                    allowUL = false;
                    changes.add(createAttribute(entry.text()));
                } else if (entry.tag().equals(Tag.valueOf("p")) && (entry.text().toLowerCase().startsWith("armor proficiency")))
                {
                    allowUL = false;
                    found = false;
                    changes.add(Change.create(AttributeKey.AVAILABLE_CLASS_FEATS, 3));
                    Arrays.stream(entry.text().split(",")).map(StartingFeats::createAttribute).forEach(changes::add);
                }
            } else if (entry.tag().equals(Tag.valueOf("h4")) && entry.text().toLowerCase().contains("starting feats"))
            {
                found = true;
            }
        }
        //System.out.println(found && !allowP && !allowUL);

        //printClassFeatList(attributes, itemName);

        return changes;
    }

    private static Change createAttribute(String text) {
        if("Skill Focus (Knowledge (Any), Mechanics, Treat Injury, or Use Computer)".equals(text))
        {
            return null;
        }
        return Change.create(AttributeKey.CLASS_FEAT, cleanStartingFeat(text));
    }

    private static String cleanStartingFeat(String text) {
        return text.trim().replace("*", "").replace(".", "");
    }

    private static void printClassFeatList(List<Change> changes, String itemName) {
        System.out.println("List<String> " + itemName.toUpperCase()+ "_STARTING_FEATS = List.of(" + changes.stream().filter(attribute -> attribute.getKey().equals("classFeat")).map(attribute -> "\"" + attribute.getValue() + "\"").collect(Collectors.joining(", ")) + ");");
    }

    public static List<Object> getStartingFeatsFromCategories(Set<Category> categories)
    {
        Set<String> catStrings = categories.stream().map(Category::getValue).collect(Collectors.toSet());

        List<Object> attributes = new ArrayList<>();
        catStrings.forEach(s -> {
            Matcher m = BONUS_FEAT_PATTERN.matcher(s);
           if(m.matches()){
               attributes.add(ProvidedItem.create(m.group(1), ItemType.FEAT));
           }
        });

        return attributes;
    }

}
