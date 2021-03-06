package swse.vehicles.systems;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;
import static swse.util.Util.cloneList;

public class VehicleSystem extends FoundryItem<VehicleSystem> implements Copyable<VehicleSystem> {
    private String cost;
    private boolean asterisk = false;

    public VehicleSystem(String name) {
        super(name, "vehicleSystem");
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

        data.put("cost", cost);

        return json;
    }

    public VehicleSystem withCost(String cost) {
        this.cost = cost;
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
