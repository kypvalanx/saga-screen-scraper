package swse.forceSecrets;

import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.FoundryItem;
import swse.common.Category;

class ForceSecret extends FoundryItem<ForceSecret> {
    public ForceSecret(String name) {
        super(name);
    }

    public static ForceSecret create(String name) {
        return new ForceSecret(name);
    }

    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "forceSecret");

        return json;
    }

    public ForceSecret withCategories(Set<Category> categories) {
        this.categories.addAll(categories);
        return this;
    }
}
