package swse.prerequisite;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import static swse.util.Util.cloneList;

public class AndPrerequisite extends Prerequisite
{
    private final List<Prerequisite> children;

    public static Prerequisite and(String plainText,
                                   Prerequisite... children){
        return new AndPrerequisite(plainText, Arrays.asList(children));
    }
    public static Prerequisite and(String plainText,
                                   List<Prerequisite> children){
        return new AndPrerequisite(plainText, children);
    }

    public static Prerequisite and(List<Prerequisite> children){
        return new AndPrerequisite(children);
    }

    public AndPrerequisite(String plainText,
                           List<Prerequisite> children)
    {
        super(plainText, "AND");
        this.children = children;
    }

    public AndPrerequisite(List<Prerequisite> children)
    {
        super(stringify(children, "and "), "AND");
        this.children = children;
    }

    @Nonnull
    @Override
    public JSONObject toJSON()
    {
        return new JSONObject().put("text", plainText).put("type", type).put("children", children.stream().filter(Objects::nonNull).map(Prerequisite::toJSON).collect(Collectors.toList()));
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("children", children)
                .add("plainText", plainText)
                .add("type", type)
                .toString();
    }

    @Override
    public Prerequisite copy() {
        return new AndPrerequisite(plainText, cloneList(children));
    }
}
