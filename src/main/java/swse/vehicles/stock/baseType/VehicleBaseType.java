package swse.vehicles.stock.baseType;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class VehicleBaseType extends FoundryItem<VehicleBaseType> implements Copyable<VehicleBaseType> {

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
}
