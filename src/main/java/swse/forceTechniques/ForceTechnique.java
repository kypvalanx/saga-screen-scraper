package swse.forceTechniques;

import swse.common.Copyable;
import swse.common.FoundryItem;

class ForceTechnique extends FoundryItem<ForceTechnique> implements Copyable<ForceTechnique> {
    public ForceTechnique(String name) {
        super(name, "forceTechnique");
    }

    public static ForceTechnique create(String name) {
        return new ForceTechnique(name);
    }

    @Override
    public ForceTechnique copy() {
        return null;
    }
}
