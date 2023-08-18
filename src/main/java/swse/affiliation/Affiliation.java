package swse.affiliation;

import swse.common.Copyable;
import swse.common.FoundryItem;

class Affiliation extends FoundryItem<Affiliation> implements Copyable<Affiliation> {

    public Affiliation(String name) {
        super(name, "affiliation");
    }

    public static Affiliation create(String traditionName) {
        return new Affiliation(traditionName);
    }


    @Override
    public Affiliation copy() {
        return null;
    }
}
