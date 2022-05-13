package swse.language;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Language extends FoundryItem<Language> implements Copyable<Language>
{
    public Language(String name)
    {
        super(name, "language");
    }

    public static Language create(String itemName)
    {
        return new Language(itemName);
    }

    @Nonnull
    public JSONObject toJSON()
    {
        JSONObject json = super.toJSON();
        json.put("type", "language");

        return json;
    }

    @Override
    public Language copy() {
        return null;
    }
}
