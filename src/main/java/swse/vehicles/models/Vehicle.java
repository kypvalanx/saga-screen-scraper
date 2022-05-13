package swse.vehicles.models;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Vehicle extends FoundryItem<Vehicle> implements Copyable<Vehicle> {
    public Vehicle(String name) {
        super(name, "vehicle");
    }

    public static Vehicle create(String name) {
        return new Vehicle(name);
    }

    @Nonnull
    @Override
    public JSONObject toJSON(){
        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        //json.put("type", "npc-vehicle");
        //data.remove("attributes");
        //data.put("defaultAttributes", createAttributes(attributes.stream().filter(Objects::nonNull).map(Attribute::toJSON).collect(Collectors.toList())));


        return json;
    }

    @Override
    public Vehicle copy() {
        return null;
    }
}
