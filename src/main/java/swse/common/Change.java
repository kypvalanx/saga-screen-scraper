package swse.common;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.prerequisite.Prerequisite;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Change implements JSONy, Copyable<Change> {
    private final String key;
    private Object value;
    private String modifier;
    private Prerequisite prerequisite;
    private Prerequisite parentPrerequisite;
    private ActiveEffectMode mode = ActiveEffectMode.ADD;

    public static Change create(ChangeKey key, Object value) {
        if(value == null){
            return null;
        }
        return new Change(key.value(), value);
    }
    public static Change create(ChangeKey key, Object value, String modifier) {
        if(value == null){
            return null;
        }
        return new Change(key.value(), value).withModifier(modifier);
    }


    public Change(String key, Object value) {
//
//        printUnique("Attribute " + key);
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

    public static List<JSONObject> constructChangeList(List<Change> changes) {
        return changes.stream().filter(Objects::nonNull).map(Change::toJSON).collect(Collectors.toList());
    }

    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public Change withModifier(String modifier) {
        this.modifier = modifier;
        return this;
    }

    public Change withMode(ActiveEffectMode mode){
        this.mode = mode;
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
        jsonObject.put("mode", mode.getValue());


        if (prerequisite != null) {
            jsonObject.put("prerequisite", JSONy.toJSON(prerequisite));
        }


        if (parentPrerequisite != null) {
            jsonObject.put("parentPrerequisite", JSONy.toJSON(parentPrerequisite));
        }

        return jsonObject;
    }

    @Override
    public Change copy() {
        return new Change(key, value).withModifier(modifier);
    }

    public Change withValue(Object value) {
        this.value = value;
        return this;
    }

    public Change withParentPrerequisite(Prerequisite parentPrerequisite) {
        this.parentPrerequisite = parentPrerequisite;
        return this;
    }

    public Change withPrerequisite(Prerequisite prerequisite) {
        this.prerequisite = prerequisite;
        return this;
    }

    @Override
    public String toString(){
        return toJSON().toString();
    }
}
