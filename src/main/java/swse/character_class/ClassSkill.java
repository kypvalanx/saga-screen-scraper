package swse.character_class;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.AttributeKey;
import swse.common.JSONy;
import swse.common.Attribute;

class ClassSkill implements JSONy
{
    private final Integer classSkillsPerLevel;
    private final List<String> classSkills;

    public ClassSkill(Integer classSkillsPerLevel, List<String> classSkills)
    {
        this.classSkillsPerLevel = classSkillsPerLevel;
        this.classSkills = classSkills;
    }

    static List<Attribute> getClassSkills(Elements entries)
    {
        List<Attribute> attributes = new ArrayList<>();
        boolean found = false;
        boolean allowP = true;
        boolean allowUL = true;
        for (Element entry : entries)
        {
            if (found)
            {
                if (allowP && entry.tag().equals(Tag.valueOf("p")))
                {
                    allowP = false;

                    Pattern trainedCountPattern = Pattern.compile("(\\d*) \\+ (their )?intelligence modifier");

                    Matcher m = trainedCountPattern.matcher(entry.text().toLowerCase());

                    if (m.find())
                    {
                        attributes.add(Attribute.create(AttributeKey.TRAINED_SKILLS_FIRST_LEVEL, Integer.parseInt(m.group(1))));
                    }

                } else if (allowUL && entry.tag().equals(Tag.valueOf("ul")))
                {
                    allowUL = false;
                    attributes.addAll(entry.select("li").stream()
                            .map(e->classSkillCleanup(e.text()))
                            .map(skill -> Attribute.create(AttributeKey.CLASS_SKILL, skill))
                            .collect(Collectors.toList()));

                }
            } else if ((entry.tag().equals(Tag.valueOf("h4")) || entry.tag().equals(Tag.valueOf("p"))) && entry.text().toLowerCase().contains("class skills"))
            {
                found = true;
            }
        }
        //System.out.println(found && !allowP && !allowUL);
        return attributes;
    }

    private static String classSkillCleanup(String text)
    {
        return text;
    }

    @Override
    public String toString()
    {
        return "ClassSkill{" +
                "classSkillsPerLevel=" + classSkillsPerLevel +
                ", classSkills=" + classSkills +
                '}';
    }

    @Nonnull
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("perLevel", classSkillsPerLevel)
                .put("skills", classSkills);
        return json;
    }
}
