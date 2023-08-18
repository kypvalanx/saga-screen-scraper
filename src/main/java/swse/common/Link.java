package swse.common;

public class Link implements Copyable<Link> {
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
}
