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
    public static Feat create(String name)
    {
        return new Feat(name);
    }

    public Feat(String name)
    {
        super(name, "feat");
    }

    public Feat withCategories(Set<Category> categories)
    {
        for(Category category:categories){
            final String value = category.getValue();
            if(value.endsWith(" Bonus Feats")){
                providers.add(value);
            }else if (value.equals("Feats")){
                providers.add("General Feats");
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
