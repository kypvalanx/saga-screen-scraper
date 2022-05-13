package swse.traits;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Trait extends FoundryItem<Trait> implements Copyable<Trait>
{

    public Trait(String name)
    {
        super(name, "trait");
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


    public Trait copy() {
        return null;
    }
}
