package swse.forcePowers;

import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.FoundryItem;
import swse.common.Category;

class ForcePower extends FoundryItem<ForcePower> {

    public ForcePower(String name) {
        super(name);
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
}
