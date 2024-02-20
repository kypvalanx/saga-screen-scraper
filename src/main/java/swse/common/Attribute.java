package swse.common;

import org.json.JSONObject;

import javax.annotation.Nonnull;

public class Attribute implements JSONy, Copyable<Attribute> {
    private final String manual;
    private final String base;
    private final ChangeKey key;

    public Attribute(ChangeKey key, String manual, String base) {
        this.key = key;
        this.manual = manual;
        this.base = base;
    }

    public static Attribute create(ChangeKey key, String value) {
        return new Attribute(key, value, null);
    }

    public static Attribute createWithBase(ChangeKey key, String value) {
        return new Attribute(key, null, value);
    }

    @Override
    public Attribute copy() {
        return null;
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        if(manual!= null){
            jsonObject.put("manual", manual);
        }
        if(base!= null){
            jsonObject.put("base", base);
        }
        return jsonObject;
    }

    public ChangeKey getKey() {
        return this.key;
    }
}
