package swse.vehicles.stock.baseType;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class VehicleBaseType extends FoundryItem<VehicleBaseType> implements Copyable<VehicleBaseType> {
    private String cost;

    public VehicleBaseType(String name) {
        super(name, "vehicleBaseType");
    }

    public static VehicleBaseType create(String name) {
        return new VehicleBaseType(name);
    }

    @Override
    public VehicleBaseType copy() {
        return null;
    }

    @Nonnull
    @Override
    public JSONObject toJSON(){
        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        json.put("type", "vehicleBaseType");

        data.put("cost", cost);

        return json;
    }

    public VehicleBaseType withCost(String cost) {
        this.cost = cost;
        return this;
    }
}
