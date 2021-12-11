package swse.item;

import java.util.List;
import swse.common.Attribute;

public class Mode {
    private final List<Attribute> attributes;
    private final List<Mode> modes;
    private final String name;
    private final String group;

    public Mode(String name, String group, List<Attribute> attributes, List<Mode> modes) {
        this.name = name;
        this.attributes = attributes;
        this.group = group;
        this.modes = modes;
    }

    public static Mode create(String name, List<Attribute> attributes) {
        return new Mode(name, null, attributes, null);
    }

    public static Mode create(String name, String group, List<Attribute> attributes) {
        return new Mode(name, group, attributes, null);
    }

    public static Mode create(String name, List<Attribute> attributes, List<Mode> modes) {
        return new Mode(name, null, attributes, modes);
    }

    public static Mode create(String name, String group, List<Attribute> attributes, List<Mode> modes) {
        return new Mode(name, group, attributes, modes);
    }

    public String getName() {
        return name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public List<Mode> getModes() {
        return modes;
    }

    public String getGroup() {
        return group;
    }
}
