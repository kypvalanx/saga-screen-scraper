package swse.item;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.JSONy;

class OrSubtype extends SubType {
    private final Set<SubType> children;

    public OrSubtype(Set<SubType> children) {
        super();
        this.type = "OR";
        this.children = children;
    }

    public static OrSubtype create(String trim) {
        if (trim.contains(" or ") || trim.contains("), ")) {
            String[] toks = trim.split(" or |, ");
            return new OrSubtype(Arrays.stream(toks).map(SubType::create).collect(Collectors.toSet()));
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
