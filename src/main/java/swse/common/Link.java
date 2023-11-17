package swse.common;

import org.json.JSONObject;

import javax.annotation.Nonnull;

public class Link implements Copyable<Link>, JSONy {
    private final LinkType type;
    private final String group;

    public Link(String group, LinkType type) {
        this.group = group;
        this.type = type;
    }

    public static Link create(String group, LinkType type) {
        return new Link(group, type);
    }

    @Override
    public Link copy() {
        return null;
    }

    public String getGroup() {
        return group;
    }

    public LinkType getType() {
        return type;
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("group", group);
        jsonObject.put("type", type);
        return jsonObject;
    }
}
