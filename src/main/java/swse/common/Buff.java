package swse.common;

import javax.annotation.Nonnull;
import org.json.JSONObject;

public class Buff extends FoundryItem<Buff> implements JSONy, Copyable<Buff>{

    public Buff(String name) {
        super(name, "buff");
    }

    public static Buff create(String name) {
        return new Buff(name);
    }

    @Override
    public Buff copy() {
        return null;
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        //JSONObject data = json.getJSONObject("data");

        return json;
    }
}
