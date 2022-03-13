package swse.common;

import javax.annotation.Nonnull;
import org.json.JSONObject;

public class Attribute implements JSONy, Copyable<Attribute> {
    private final String key;
    private Object value;
    private String modifier;

    public static  Attribute create(String key, Object value) {
        if(value == null){
            return null;
        }
        return new Attribute(key, value);
    }
    public static  Attribute create(String key, Object value, String modifier) {
        if(value == null){
            return null;
        }
        return new Attribute(key, value).withModifier(modifier);
    }


    public Attribute(String key, Object value) {
        if("ammo".equals(key)){
            if(((String)value).startsWith("case")){

                //printUnique(value);
            }
        }
        this.key = key;
        this.value = value;
    }
    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public Attribute withModifier(String modifier) {
        this.modifier = modifier;
        return this;
    }

    public String getModifier() {
        return modifier;
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", key);
                jsonObject.put("value", value);
        jsonObject.put("modifier", modifier);
        return jsonObject;
    }

    @Override
    public Attribute copy() {
        return new Attribute(key, value).withModifier(modifier);
    }

    public Attribute withValue(Object value) {
        this.value = value;
        return this;
    }
}
