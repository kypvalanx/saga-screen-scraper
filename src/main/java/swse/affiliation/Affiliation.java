package swse.affiliation;

import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import swse.common.Category;
import swse.common.Copyable;
import swse.common.FoundryItem;

class Affiliation extends FoundryItem implements Copyable<Affiliation> {

    public Affiliation(String name) {
        super(name);
    }

    public static Affiliation create(String traditionName) {
        return new Affiliation(traditionName);
    }

    public Affiliation withDescription(Element description) {
        this.description = description.toString();
        return this;
    }

    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();

//        JSONObject data = json.getJSONObject("data");
//
//        if (categories != null) {
//            data.put("categories", JSONy.toArray(categories));
//        }

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
