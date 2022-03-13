package swse.vehicles.systems;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;
import static swse.util.Util.cloneList;

public class VehicleSystem extends FoundryItem<VehicleSystem> implements Copyable<VehicleSystem> {
    private String cost;
    private String subtype;
    private boolean asterisk = false;

    public VehicleSystem(String name) {
        super(name);
    }

    public static VehicleSystem create(String name) {
        return new VehicleSystem(name);
    }

    @Override
    public VehicleSystem copy() {
       VehicleSystem v = VehicleSystem.create(name)
                .withCost(cost)
                .withSubtype(subtype)
                .withDescription(description);
        if (prerequisite != null) {
            v.withPrerequisite(prerequisite.copy());
        }
        v.withImage(image)
                .withSource(source)
                .withAvailability(availability)
                .withProvided(cloneList(attributes))
                .withProvided(cloneList(providedItems))
                .withProvided(cloneList(categories))
                .withProvided(cloneList(choices));
        return v ;
    }

    @Nonnull
    @Override
    public JSONObject toJSON(){
        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        json.put("type", "vehicleSystem");

        data.put("cost", cost);
        data.put("subtype", subtype);
        data.put("subType", subtype);

        return json;
    }

    public VehicleSystem withCost(String cost) {
        this.cost = cost;
        return this;
    }
    public VehicleSystem withSubtype(String subtype) {
        this.subtype = subtype;
        return this;
    }

    public VehicleSystem withAsterisk(boolean asterisk) {
        this.asterisk = asterisk;
        return this;
    }

    public boolean hasAsterisk(){
        return asterisk;
    }
}
