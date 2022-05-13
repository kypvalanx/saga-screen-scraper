package swse.affiliation;

import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Category;
import swse.common.Copyable;
import swse.common.FoundryItem;

class Affiliation extends FoundryItem<Affiliation> implements Copyable<Affiliation> {

    public Affiliation(String name) {
        super(name, "affiliation");
    }

    public static Affiliation create(String traditionName) {
        return new Affiliation(traditionName);
    }


    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();


        return json.put("type", "affiliation");
    }

    public Affiliation withCategories(Set<Category> categories) {
        this.categories.addAll(categories);
        return this;
    }

    @Override
    public Affiliation copy() {
        return null;
    }
}
