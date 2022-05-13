package swse.forcePowers;

import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Category;
import swse.common.Copyable;
import swse.common.FoundryItem;

class ForcePower extends FoundryItem<ForcePower> implements Copyable<ForcePower> {

    public ForcePower(String name) {
        super(name, "forcePower");
    }

    public static ForcePower create(String name) {
        return new ForcePower(name);
    }

    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "forcePower");


        return json;
    }


    public ForcePower withCategories(Set<Category> categories) {
        this.categories.addAll(categories);
        return this;
    }

    @Override
    public ForcePower copy() {
        return null;
    }
}
