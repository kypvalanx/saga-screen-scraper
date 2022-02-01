package swse.species;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Species extends FoundryItem<Species> implements Copyable<Species>
{
    public Species(String name)
    {
        super(name);
    }

    public static Species create(String itemName)
    {
        return new Species(itemName);
    }

    @Nonnull
    public JSONObject toJSON()
    {
        JSONObject json = super.toJSON();
        json.put("type", "species");

        return json;
    }

    @Override
    public Species copy() {
        return null;
    }
}
