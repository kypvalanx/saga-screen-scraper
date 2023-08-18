package swse.item;

import java.util.*;

import swse.common.Change;
import swse.common.Link;

public class Effect extends FoundryEffect<Effect>{

    protected Effect(String name) {
        super(name);
    }

    public static Effect create(String name, List<Change> changes) {
        return new Effect(name).withChanges(changes);
    }

    public static Effect create(String name, String group, List<Change> changes) {
        return new Effect(name).withGroup(group).withChanges(changes);
    }

    public static Effect create(String name, String group, List<Change> changes, List<Link> links) {
        return new Effect(name).withGroup(group).withLinks(links.toArray(new Link[0])).withChanges(changes);
    }


}
