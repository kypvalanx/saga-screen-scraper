package swse.item;

import java.util.*;

import swse.common.Change;
import swse.common.Link;

public class Effect extends FoundryEffect<Effect>{

    protected Effect(String label) {
        super(label);
    }

    public static Effect create(String label, List<Change> changes) {
        return new Effect(label).withChanges(changes);
    }

    public static Effect create(String label, String group, List<Change> changes) {
        return new Effect(label).withGroup(group).withChanges(changes);
    }

    public static Effect create(String label, String group, List<Change> changes, List<Link> links) {
        return new Effect(label).withGroup(group).withLinks(links.toArray(new Link[0])).withChanges(changes);
    }


}
