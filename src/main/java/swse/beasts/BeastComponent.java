package swse.beasts;

import swse.common.Copyable;
import swse.common.FoundryItem;

public class BeastComponent extends FoundryItem<BeastComponent> implements Copyable<BeastComponent> {

    public BeastComponent(String name) {
        super(name, "beastAttack");
    }

    public static BeastComponent create(String name) {
        return new BeastComponent(name);
    }

    @Override
    public BeastComponent copy() {
        return null;
    }

    public BeastComponent withType(String type) {
        this.type = type;
        return this;
    }
}
