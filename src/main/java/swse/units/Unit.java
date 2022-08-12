package swse.units;

import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

import javax.annotation.Nonnull;

public class Unit extends FoundryItem<Unit> implements Copyable<Unit> {
    private String size;
    private String speciesSubType;
    private String age;

    public Unit(String name) {
        super(name, "npc");
    }

    public static Unit create(String name) {
        return new Unit(name);
    }

    @Nonnull
    @Override
    public JSONObject toJSON(){
        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        data.put("size", size); //size is provided most of the time.  this should be used to double-check that the size has been resolved correctly.
        data.put("speciesSubType", speciesSubType);
        data.put("age", age);
        //json.put("type", "npc-vehicle");
        //data.remove("attributes");
        //data.put("defaultAttributes", createAttributes(attributes.stream().filter(Objects::nonNull).map(Attribute::toJSON).collect(Collectors.toList())));


        return json;
    }

    @Override
    public Unit copy() {
        return null;
    }

    public Unit withSize(String size) {
        this.size = size;
        return this;
    }

    public Unit withSpeciesSubType(String group) {
        this.speciesSubType = group;
        return this;
    }

    public Unit withAge(String age) {
        this.age = age;
        return this;
    }
}
