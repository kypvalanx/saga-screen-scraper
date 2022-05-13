package swse.feat;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Category;
import swse.common.Copyable;
import swse.common.FoundryItem;

class Feat extends FoundryItem<Feat> implements Copyable<Feat>
{
//    private String fortDefenceBonus;
//    private String refDefenceBonus;
    private final Set<String> featProviders = new HashSet<>();
//    private Map<String, String> attributes = new HashMap<>();

    public static Feat create(String name)
    {
        return new Feat(name);
    }

    public Feat(String name)
    {
        super(name, "feat");
    }

    @Nonnull
    public JSONObject toJSON()
    {
        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        json.put("type", "feat");

        data.put("possibleProviders", featProviders);

        return json;
    }

    public Feat withCategories(Set<Category> categories)
    {
        Set<Category> filtered = new HashSet<>();
        for(Category category:categories){
            final String value = category.getValue();
            if(value.endsWith(" Bonus Feats")){
                featProviders.add(value);
            }else if (value.equals("Feats")){
                featProviders.add("General Feats");
            } else {
                filtered.add(category);
            }
        }
        this.categories.addAll(categories);
        return this;
    }

    @Override
    public Feat copy() {
        return null;
    }
}
