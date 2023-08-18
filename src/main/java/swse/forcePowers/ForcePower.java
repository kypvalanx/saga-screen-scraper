package swse.forcePowers;

import swse.common.Copyable;
import swse.common.FoundryItem;

class ForcePower extends FoundryItem<ForcePower> implements Copyable<ForcePower> {

    public ForcePower(String name) {
        super(name, "forcePower");
    }

    public static ForcePower create(String name) {
        return new ForcePower(name);
    }

    @Override
    public ForcePower copy() {
        return null;
    }
}
