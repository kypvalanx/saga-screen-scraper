package swse.traits;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.FoundryItem;

public class Trait extends FoundryItem<Trait>
{

    public Trait(String name)
    {
        super(name);
    }

    public static Trait create(String name)
    {
        return new Trait(name);
    }

    @Nonnull
    public JSONObject toJSON()
    {
        JSONObject json = super.toJSON();
        json.put("type", "trait");

        return json;
    }


}
