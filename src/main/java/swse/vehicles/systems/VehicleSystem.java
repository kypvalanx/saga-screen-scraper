package swse.vehicles.systems;

import swse.common.Copyable;
import swse.common.FoundryItem;
import static swse.util.Util.cloneList;

public class VehicleSystem extends FoundryItem<VehicleSystem> implements Copyable<VehicleSystem> {
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
                .withSubtype(subtype)
                .withDescription(description);
        if (prerequisite != null) {
            v.withPrerequisite(prerequisite.copy());
        }
        v.withImage(image)
                .withSource(source)
                .with(cloneList(changes))
                .with(cloneList(providedItems))
                .with(cloneList(categories))
                .with(cloneList(choices));
        return v ;
    }

    public VehicleSystem withAsterisk(boolean asterisk) {
        this.asterisk = asterisk;
        return this;
    }

    public boolean hasAsterisk(){
        return asterisk;
    }
}
