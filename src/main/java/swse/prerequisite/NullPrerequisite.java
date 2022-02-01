package swse.prerequisite;

import javax.annotation.Nonnull;
import org.json.JSONObject;

class NullPrerequisite extends Prerequisite
{

    public NullPrerequisite(String plainText)
    {
        super(plainText, "NULL");
    }

    @Nonnull
    @Override
    public JSONObject toJSON()
    {
        return new JSONObject().put("text", plainText).put("type", type);
    }

    @Override
    public Prerequisite copy() {
        return new NullPrerequisite(plainText);
    }
}
