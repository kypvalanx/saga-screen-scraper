package swse.item;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.JSONy;

class AndSubtype extends SubType {
    private final Set<SubType> children;

    public AndSubtype(Set<SubType> children) {
        this.type = "AND";
        this.children = children;
    }

    public static AndSubtype create(String trim) {
        if (trim.contains(" and ")) {
            String[] toks = trim.split(" and ");
            return new AndSubtype(Arrays.stream(toks).map(SubType::create).collect(Collectors.toSet()));
        }
        throw new IllegalArgumentException("should contain ' or '");
    }

    @Override
    @Nonnull
    public JSONObject toJSON() {
        return new JSONObject().put("type", type).put("children", JSONy.toArray(children));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("children", children)
                .add("type", type)
                .toString();
    }
}
