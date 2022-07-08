package swse.units;

import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

import javax.annotation.Nonnull;

public class Beast extends FoundryItem<Beast> implements Copyable<Beast> {
    public Beast(String name) {
        super(name, "vehicle");
    }

    public static Beast create(String name) {
        return new Beast(name);
    }

    @Nonnull
    @Override
    public JSONObject toJSON(){
        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        //json.put("type", "npc-vehicle");
        //data.remove("attributes");
        //data.put("defaultAttributes", createAttributes(attributes.stream().filter(Objects::nonNull).map(Attribute::toJSON).collect(Collectors.toList())));


        return json;
    }

    @Override
    public Beast copy() {
        return null;
    }
}
