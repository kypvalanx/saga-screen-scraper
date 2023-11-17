package swse.common;

import org.json.JSONObject;

import javax.annotation.Nonnull;

public class Attribute implements JSONy, Copyable<Attribute> {
    private final String manual;
    private final ChangeKey key;

    public Attribute(ChangeKey key, String manual) {
        this.key = key;
        this.manual = manual;
    }

    public static Attribute create(ChangeKey key, String value) {
        return new Attribute(key, value);
    }

    @Override
    public Attribute copy() {
        return null;
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("manual", manual);
        return jsonObject;
    }

    public ChangeKey getKey() {
        return this.key;
    }
}
