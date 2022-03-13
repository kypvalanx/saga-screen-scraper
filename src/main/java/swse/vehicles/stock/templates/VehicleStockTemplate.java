package swse.vehicles.stock.templates;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class VehicleStockTemplate  extends FoundryItem<VehicleStockTemplate> implements Copyable<VehicleStockTemplate> {
    private String cost;

    public VehicleStockTemplate(String name) {
        super(name);
    }

    public static VehicleStockTemplate create(String name) {
        return new VehicleStockTemplate(name);
    }

    @Override
    public VehicleStockTemplate copy() {
        return null;
    }

    @Nonnull
    @Override
    public JSONObject toJSON(){
        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        json.put("type", "vehicleTemplate");

        data.put("cost", cost);

        return json;
    }

    public VehicleStockTemplate withCost(String cost) {
        this.cost = cost;
        return this;
    }
}
