package swse.common;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.prerequisite.Prerequisite;
import static swse.util.Util.printUnique;

public class Attribute implements JSONy, Copyable<Attribute> {
    private final String key;
    private Object value;
    private String modifier;
    private Prerequisite prerequisite;
    private Prerequisite parentPrerequisite;

    public static  Attribute create(AttributeKey key, Object value) {
        if(value == null){
            return null;
        }
        return new Attribute(key.value(), value);
    }
    public static  Attribute create(AttributeKey key, Object value, String modifier) {
        if(value == null){
            return null;
        }
        return new Attribute(key.value(), value).withModifier(modifier);
    }


    public Attribute(String key, Object value) {

        printUnique("Attribute " + key);
//        if("damage".equals(key)) {
//            value = ((String)value).replace("x", "*");
//        }

//        if("damageType".equals(key)){
//            printUnique(value);
//        }
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


        if (prerequisite != null) {
            jsonObject.put("prerequisite", JSONy.toJSON(prerequisite));
        }


        if (parentPrerequisite != null) {
            jsonObject.put("parentPrerequisite", JSONy.toJSON(parentPrerequisite));
        }

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

    public Attribute withParentPrerequisite(Prerequisite parentPrerequisite) {
        this.parentPrerequisite = parentPrerequisite;
        return this;
    }

    public Attribute withPrerequisite(Prerequisite prerequisite) {
        this.prerequisite = prerequisite;
        return this;
    }
}
