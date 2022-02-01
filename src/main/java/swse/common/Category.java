package swse.common;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

public class Category implements JSONy, Copyable<Category>{
    private final String value;

    public Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public static Set<Category> getCategories(Element content){
        return content.select("li.category,a.newcategory").stream().map(Element::text).map(text -> text.replace("Condition ", "Conditional ")).map(Category::create).collect(Collectors.toSet());
    }

    public static Category create(String value) {
        return new Category(value);
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        return new JSONObject().put("value", value);
    }

    @Override
    public Category copy() {
        return new Category(value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equal(value, category.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
