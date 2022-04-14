package swse.prerequisite;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import org.json.JSONObject;

class RangePrerequisite extends Prerequisite
{
    private final String low;
    private final String high;

    public static Prerequisite range(String text, String type, String low, String high){
        return new RangePrerequisite(text, type, low, high);
    }

    public RangePrerequisite(String text, String type, String low, String high)
    {
        super(text, type);
        this.low = low;
        this.high = high;
    }

    @Nonnull
    @Override
    public JSONObject toJSON()
    {
        return new JSONObject().put("text", plainText).put("type", type).put("low", low).put("high", high);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("low", low)
                .add("high", high)
                .add("plainText", plainText)
                .add("type", type)
                .toString();
    }

    @Override
    public Prerequisite copy() {
        return new RangePrerequisite(plainText, type, low, high);
    }
}
