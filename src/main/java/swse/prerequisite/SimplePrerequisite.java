package swse.prerequisite;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import org.json.JSONObject;

public class SimplePrerequisite extends Prerequisite
{
    private final String requirement;

    public static Prerequisite simple(String plainText, String type, String requirement){
        return new SimplePrerequisite(plainText, type, requirement);
    }

    public SimplePrerequisite(String plainText, String type, String requirement)
    {
        super(plainText, type);
        this.requirement = requirement;
    }

    @Nonnull
    @Override
    public JSONObject toJSON()
    {
        return new JSONObject().put("text", plainText).put("type", type).put("requirement", requirement);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("requirement", requirement)
                .add("plainText", plainText)
                .add("type", type)
                .toString();
    }

    @Override
    public Prerequisite copy() {
        return new SimplePrerequisite(plainText, type, requirement);
    }
}
