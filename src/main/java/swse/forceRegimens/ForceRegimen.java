package swse.forceRegimens;

import swse.common.Copyable;
import swse.common.FoundryItem;

class ForceRegimen extends FoundryItem<ForceRegimen> implements Copyable<ForceRegimen> {

    public ForceRegimen(String name) {
        super(name, "forceRegimen");
    }

    public static ForceRegimen create(String name) {
        return new ForceRegimen(name);
    }

    @Override
    public ForceRegimen copy() {
        return null;
    }
}
