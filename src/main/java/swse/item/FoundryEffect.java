package swse.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.json.JSONArray;
import org.json.JSONObject;
import swse.common.Change;
import swse.common.Copyable;
import swse.common.JSONy;
import swse.common.Link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static swse.util.Util.cloneList;

public class FoundryEffect<T> implements Copyable<FoundryEffect<T>> {
    protected List<Change> changes = Lists.newArrayList();
    protected String name;
    protected String group;
    protected List<Link> links;
    protected Map<String, Object> flags;

    public FoundryEffect(String name) {
        this.name = name;
        this.flags = Maps.newHashMap();
    }

    public static JSONArray constructEffectList(List<FoundryEffect<?>> effects) {
        final JSONArray modeObjects = new JSONArray();

        if(effects == null){
            return modeObjects;
        }

        for(FoundryEffect<?> effect : effects){
            modeObjects.put(effect.toJSON());
        }
        return modeObjects;
    }

    public T withChanges(List<Change> changes) {
        this.changes.addAll(changes);
        return (T) this;
    }

    public T withGroup(String group) {
        this.group = group;
        return (T) this;
    }

    public T withLinks(Link... links) {
        if(this.links == null){
            this.links = new ArrayList<>(links.length);
        }
        this.links.addAll(Arrays.asList(links));
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public String getGroup() {
        return group;
    }


    public JSONObject toJSON() {
        resolveDynamicValues();
        JSONObject effect = new JSONObject();
        effect.put("disables", true);
        effect.put("name", name);
        effect.put("changes", Change.constructChangeList(changes));
        effect.put("flags", getJsonFlags());
        return effect;
    }

    private JSONObject getJsonFlags() {
        JSONObject JSONFlags = new JSONObject();
        JSONObject swse = new JSONObject();

        for(Map.Entry<String, Object> flag : getFlags().entrySet()){
            if(flag.getValue() == null){
                continue;
            }
            switch(flag.getValue().getClass().toString()){
                case "class java.lang.Boolean":
                case "class java.lang.Integer":
                case "class java.lang.String":
                    swse.put(flag.getKey(), flag.getValue());
                    break;
                case "class java.util.ArrayList":
                    swse.put(flag.getKey(), toJSONArray((List<? extends JSONy>) flag.getValue()));
                    break;
                default:
                    System.err.println(flag.getValue().getClass());
            }
        }

        JSONFlags.put("swse", swse);
        return JSONFlags;
    }

    private JSONArray toJSONArray(List<? extends JSONy> values) {
        JSONArray array = new JSONArray();
        for (JSONy value:
             values) {
            array.put(value.toJSON());
        }
        return array;
    }

    private Map<String, Object> getFlags() {
        Map<String, Object> response = Maps.newHashMap();
        response.putAll(flags);
        response.put("linkData", links);
        response.put("group", group);
        return response;
    }
    public FoundryEffect<T> copy() {
        Effect effect = new Effect(name);
        effect.withGroup(group);
        effect.withLinks(cloneList(links).toArray(new Link[0]));
        effect.withChanges(cloneList(changes));
        return (FoundryEffect<T>) effect;
    }

    protected void resolveDynamicValues() {

    }
}
