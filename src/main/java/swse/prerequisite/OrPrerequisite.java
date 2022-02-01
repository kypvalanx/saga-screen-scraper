package swse.prerequisite;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import static swse.util.Util.cloneList;

public class OrPrerequisite extends Prerequisite
{
    private final int count;
    private final List<Prerequisite> children;

    public OrPrerequisite(String plainText,
                          List<Prerequisite> children,
                          Integer count)
    {
        super(plainText, "OR");
        this.count = count == null? 1:count;
        this.children = children;
    }
    public OrPrerequisite(String plainText,
                          List<Prerequisite> children)
    {
        super(plainText, "OR");
        this.count = 1;
        this.children = children;
    }
    public OrPrerequisite(List<Prerequisite> children)
    {
        super(stringify(children, "or "), "OR");
        this.count = 1;
        this.children = children;
    }

    @Nonnull
    @Override
    public JSONObject toJSON()
    {
        return new JSONObject().put("text", plainText).put("type", type).put("count", count).put("children", children.stream().map(Prerequisite::toJSON).collect(Collectors.toList()));
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("count", count)
                .add("children", children)
                .add("plainText", plainText)
                .add("type", type)
                .toString();
    }

    @Override
    public Prerequisite copy() {
        return new OrPrerequisite(plainText, cloneList(children), count);
    }
}
