package swse.forceSecrets;

import swse.common.Copyable;
import swse.common.FoundryItem;

class ForceSecret extends FoundryItem<ForceSecret>  implements Copyable<ForceSecret> {
    public ForceSecret(String name) {
        super(name, "forceSecret");
    }

    public static ForceSecret create(String name) {
        return new ForceSecret(name);
    }

    @Override
    public ForceSecret copy() {
        return null;
    }
}
