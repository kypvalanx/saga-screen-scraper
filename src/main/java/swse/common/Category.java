package swse.common;

import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

public class Category implements JSONy{
    private final String value;

    public Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public static Set<Category> getCategories(Element content){
        return content.select("li.category").stream().map(Element::text).map(text -> text.replace("Condition ", "Conditional ")).map(Category::create).collect(Collectors.toSet());
    }

    public static Category create(String value) {
        return new Category(value);
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        return new JSONObject().put("value", value);
    }
}
