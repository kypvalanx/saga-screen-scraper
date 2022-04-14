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
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.Category;
import swse.common.ItemType;
import swse.common.ProvidedItem;

public class StartingFeats
{


    public static final Pattern BONUS_FEAT_PATTERN = compile("(?:Conditional )?Bonus Feat \\(([\\w\\s()]*)\\)");

    static List<Attribute> getStartingFeats(Elements entries)
    {
        List<Attribute> attributes = new ArrayList<>();
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
                    attributes.addAll(entry.select("li").stream()
                            .map(Element::text)
                            .map(text -> Attribute.create(AttributeKey.CLASS_FEAT, text)).collect(Collectors.toList()));

                } else if ((entry.tag().equals(Tag.valueOf("p")) && (entry.text().toLowerCase().startsWith("weapon proficiency") || entry.text().toLowerCase().startsWith("skill focus") || entry.text().toLowerCase().startsWith("technologist") || entry.text().toLowerCase().startsWith("force sensitivity") || entry.text().toLowerCase().startsWith("force training")|| entry.text().toLowerCase().startsWith("tech specialist")))
                        && !entry.text().toLowerCase().contains(" or "))
                {
                    allowUL = false;
                    attributes.add(Attribute.create(AttributeKey.CLASS_FEAT, entry.text()));
                } else if (entry.tag().equals(Tag.valueOf("p")) && (entry.text().toLowerCase().startsWith("armor proficiency")))
                {
                    allowUL = false;
                    found = false;
                    attributes.add(Attribute.create(AttributeKey.AVAILABLE_CLASS_FEATS, 3));
                    attributes.addAll(Arrays.stream(entry.text().split(",")).map(text -> Attribute.create(AttributeKey.CLASS_FEAT, text)).collect(Collectors.toList()));
                }
            } else if (entry.tag().equals(Tag.valueOf("h4")) && entry.text().toLowerCase().contains("starting feats"))
            {
                found = true;
            }
        }
        //System.out.println(found && !allowP && !allowUL);
        return attributes;
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
