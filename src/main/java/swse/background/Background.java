package swse.background;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Background extends FoundryItem<Background> implements Copyable<Background>
{
    public Background(String name)
    {
        super(name, "background");
    }

    public static Background create(String itemName)
    {
        return new Background(itemName);
    }

    public Background copy() {
        return null;
    }
}
