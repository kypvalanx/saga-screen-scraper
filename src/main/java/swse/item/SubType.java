package swse.item;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.JSONy;

class SubType implements JSONy {
    private List<String> modifications;
    public static Pattern pattern = Pattern.compile("(\\w*) \\(([\\w\\s,]*)\\)");
    private String value;
    protected String type;

    public SubType() {
    }

    public SubType(String trim) {
        this.type = "DAMAGE";
        Matcher m = pattern.matcher(trim);
        if (trim.contains("Armor")) {
            this.type = "ARMOR";
        }
        if (m.find()) {
            this.value = m.group(1);
            this.modifications = getModifications(m.group(2));
        } else {
            this.value = trim;
        }
    }

    private List<String> getModifications(String group) {
        if (group.equalsIgnoreCase("see above")) {
            //printUnique(Context.getValue("name"));

            //TODO Verpine Power Lance has different tips and works only while mounted
            //TODO Blorash Jelly no damage?
        }
        return Arrays.stream(group.split(", ")).collect(Collectors.toList());
    }

    public static SubType create(String trim) {
        if(trim == null){
            return null;
        }
        if (trim.contains(" and ")) {
            return AndSubtype.create(trim);
        } else if (trim.contains(" or ") || trim.contains("), ")) {
            return OrSubtype.create(trim);
        } else {
            return new SubType(trim);
        }
    }

    @Override
    @Nonnull
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject().put("type", type).put("value", value);
        if (modifications != null && modifications.size() > 0) {
            jsonObject.put("modifications", modifications);
        }
        return jsonObject;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("modifications", modifications)
                .add("value", value)
                .add("type", type)
                .toString();
    }
}
