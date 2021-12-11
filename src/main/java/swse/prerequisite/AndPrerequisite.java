package swse.prerequisite;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import org.json.JSONObject;

public class AndPrerequisite extends Prerequisite
{
    private final List<Prerequisite> children;

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
        return new JSONObject().put("text", plainText).put("type", type).put("children", children.stream().map(Prerequisite::toJSON).collect(Collectors.toList()));
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
}
