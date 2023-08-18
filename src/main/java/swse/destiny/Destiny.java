package swse.destiny;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Destiny extends FoundryItem<Destiny> implements Copyable<Destiny>
{
    public Destiny(String name)
    {
        super(name, "destiny");
    }

    public static Destiny create(String itemName)
    {
        return new Destiny(itemName);
    }

    public Destiny copy() {
        return null;
    }
}
