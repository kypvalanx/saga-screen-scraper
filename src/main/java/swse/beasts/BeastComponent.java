package swse.beasts;

import org.json.JSONObject;
import swse.common.Category;
import swse.common.Copyable;
import swse.common.FoundryItem;

import javax.annotation.Nonnull;
import java.util.Set;

class BeastComponent extends FoundryItem<BeastComponent> implements Copyable<BeastComponent> {

    public BeastComponent(String name) {
        super(name, "beastAttack");
    }

    public static BeastComponent create(String name) {
        return new BeastComponent(name);
    }

    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();


        return json;
    }


    public BeastComponent withCategories(Set<Category> categories) {
        this.categories.addAll(categories);
        return this;
    }

    @Override
    public BeastComponent copy() {
        return null;
    }
}
