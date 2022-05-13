package swse.forceSecrets;

import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Category;
import swse.common.Copyable;
import swse.common.FoundryItem;

class ForceSecret extends FoundryItem<ForceSecret>  implements Copyable<ForceSecret> {
    public ForceSecret(String name) {
        super(name, "forceSecret");
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

    @Override
    public ForceSecret copy() {
        return null;
    }
}
