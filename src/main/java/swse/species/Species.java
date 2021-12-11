package swse.species;

import java.util.Collection;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.FoundryItem;

public class Species extends FoundryItem<Species>
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

    //TODO move this up or remove?  seems weird to have a chatch all but it is handy
}
