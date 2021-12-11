package swse.forceRegimens;

import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.FoundryItem;
import swse.common.Category;

class ForceRegimen extends FoundryItem<ForceRegimen> {

    public ForceRegimen(String name) {
        super(name);
    }

    public static ForceRegimen create(String name) {
        return new ForceRegimen(name);
    }

    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "forceRegimen");

        return json;
    }


    public ForceRegimen withCategories(Set<Category> categories) {
        this.categories.addAll(categories);
        return this;
    }
}
