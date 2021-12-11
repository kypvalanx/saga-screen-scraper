package swse.forceTechniques;

import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.FoundryItem;
import swse.common.Category;

class ForceTechnique extends FoundryItem<ForceTechnique> {
    public ForceTechnique(String name) {
        super(name);
    }

    public static ForceTechnique create(String name) {
        return new ForceTechnique(name);
    }

    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "forceTechnique");

        return json;
    }

    public ForceTechnique withCategories(Set<Category> categories) {
        this.categories.addAll(categories);
        return this;
    }
}
