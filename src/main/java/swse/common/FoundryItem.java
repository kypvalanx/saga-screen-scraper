package swse.common;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import static swse.common.BaseExporter.getDescription;
import static swse.common.BaseExporter.getSource;
import static swse.util.Util.printUnique;

import swse.item.Effect;
import swse.item.FoundryEffect;
import swse.prerequisite.Prerequisite;

public abstract class FoundryItem<T extends FoundryItem> implements JSONy {
    protected final List<String> providers;
    protected String type;
    protected String name;
    protected String description = "";
    protected Prerequisite prerequisite;
    protected String image;
    protected final List<Change> changes;
    protected final List<ProvidedItem> providedItems;
    protected final List<Category> categories;
    protected final List<Choice> choices;
    protected String source;
    protected String subtype;
    protected List<FoundryEffect<?>> effects = new LinkedList<>();
    private final List<Modification> modifications;
    protected JSONObject system;
    private String id;
    private String link;

    public FoundryItem(String name, String type) {
        this.name = name;
        this.choices = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.changes =  new ArrayList<>();
        this.providedItems =  new ArrayList<>();
        this.modifications =  new ArrayList<>();
        this.type = type;
        this.providers = new ArrayList<>();
    }
    public FoundryItem(String name, String type, String subtype) {
        this.name = name;
        this.choices = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.changes =  new ArrayList<>();
        this.providedItems =  new ArrayList<>();
        this.modifications =  new ArrayList<>();
        this.type = type;
        this.providers = new ArrayList<>();
        this.subtype = subtype;
    }

    @Nonnull
    @Override
    public JSONObject toJSON(){
        preJSON();
        List<String> flags = getFlags();
        JSONObject root = new JSONObject();
        root.put("name", name);
        root.put("img", image);
        root.put("type", type);
        root.put("effects", FoundryEffect.constructEffectList(effects));
        root.put("flags", new JSONObject());
        root.put("folder", (String)null );
        root.put("sort", 0);
        if(id != null){
            root.put("_id", id);
        }
        root.put("permission", getPermission());

        system = system == null ? new JSONObject() : system;
        system.put("description", description);
        system.put("choices", JSONy.toArray(choices));
        system.put("source", source);
        system.put("subtype", subtype);
        if (prerequisite != null) {
            system.put("prerequisite", JSONy.toJSON(prerequisite));
        }
        system.put("categories", JSONy.toArray(categories));
        if (categories.size()>0){
            universalChangesFromCategories();
        }
        //categories.forEach(category -> providedItems.add(ProvidedItem.create(category.getValue(), ItemType.TRAIT)));
        system.put("providedItems",JSONy.toArray(providedItemPostFilter(providedItems)));
        system.put("modifications",JSONy.toArray(modifications));

        system.put("changes", createChangeArray(changes.stream().filter(Objects::nonNull).map(Change::toJSON).collect(Collectors.toList()), flags));


        if(providers.size()>0) {
            system.put("possibleProviders", providers);
        }

        root.put("system", system);
        return root;
    }

    public List<ProvidedItem> providedItemPostFilter(List<ProvidedItem> providedItems) {
        return providedItems;
    }

    private JSONArray createChangeArray(List<JSONObject> collect, List<String> flags) {
        JSONArray json = new JSONArray();

        for (JSONObject change :
                collect) {
            json.put(change);
        }

        return json;
    }

    private JSONObject getPermission() {
        JSONObject permission = new JSONObject();
        permission.put("default", 0);
        return permission;
    }

    private void universalChangesFromCategories() {
        List<String> categoryStrings = categories.stream().map(Category::getValue).collect(Collectors.toList());
        if (categoryStrings.contains("Homebrew Content")){
            changes.add(Change.create(ChangeKey.HOMEBREW, true));
        }
        if (categoryStrings.contains("Untested")){
            changes.add(Change.create(ChangeKey.HOMEBREW, true));
        }
    }

    protected List<String> getFlags(){
        return new ArrayList<>();
    }

    /**
     * returns a JSONObject that resembles an array.  we are doing this because we want to be able to add and remove attributes from items more easily in foundry
     */
    public static JSONObject createAttributes(List<JSONObject> attributes){
        return createAttributes(attributes, List.of());
    }

    private static JSONObject createAttributes(List<JSONObject> attributes, List<String> flags) {
        boolean isEstimate = flags.contains("ATTRIBUTES_ARE_ESTIMATE");
        boolean useNameInKey = flags.contains("USE_NAME_IN_KEY");
        JSONObject json = new JSONObject();
        int i = 0;
        for (JSONObject value : attributes) {
            if(isEstimate){
                value.put("estimate", value.get("value"));
            }
            json.put(useNameInKey ? (String)value.get("key") : String.valueOf(i++), value);
        }

        return json;
    }

    public static JSONObject createAttribute(String key, Object object) {
        if(object == null || (object instanceof Collection && ((Collection<?>)object).isEmpty())){
            return null;
        }
        JSONObject json = new JSONObject();
        json.put("key", key);
        if(object instanceof JSONy){
            json.put("value", ((JSONy)object).toJSON());
        } else {
            json.put("value", object);
        }
        json.put("type", getType(object));
        return json;
    }

    private static String getType(Object object) {
        if(object instanceof String){
            return "String";
        }
        if(object instanceof Collection){
            return "List";
        }
        if(object instanceof Boolean){
            return "Boolean";
        }
        return "Object";

    }

    public T with(Collection<?> objects) {
        if(objects != null) {
            objects.forEach(this::with);
        }
        return (T) this;
    }

    public T withSource(String source) {
        this.source = source;
        return (T) this;
    }


    public T withSource(Element content)
    {
        this.source = getSource(content);
        return (T) this;
    }


    public T withAvailability(String availability) {
        this.with(Change.create(ChangeKey.AVAILABILITY, availability));
        return (T) this;
    }


    public T withSubtype(String subtype) {
        this.subtype = subtype;
        return (T) this;
    }

    protected void preJSON() {
        //if(modes.stream().filter((mode) -> {})){}
    }

    public T withCost(String cost) {
        this.with(Change.create(ChangeKey.COST, cost));
        return (T) this;
    }

    public T with(Object object) {
        return with(object, false);
    }

    public T with(Object object, boolean unique) {
        //printUnique(object);
        if (object == null) {
            return (T) this;
        }

        if(object instanceof Change) {
            if(unique){
                boolean overwritten = false;
                for(Change change : changes){
                    if(change.getKey().equals(((Change)object).getKey())){
                        change.withValue(((Change)object).getValue());
                        overwritten = true;
                    }
                }
                if(!overwritten){
                    changes.add((Change)object);
                }
            } else {
                changes.add((Change) object);
            }
        } else if(object instanceof Choice){
            choices.add((Choice)object);
        } else if(object instanceof ProvidedItem){
            providedItems.add((ProvidedItem)object);
        } else if(object instanceof Category){
            categories.add((Category)object);
        } else if(object instanceof Modification){
            modifications.add((Modification)object);
        } else if(object instanceof Effect){
            effects.add((Effect)object);
        }
        return (T) this;
    }

    public T withDescription(String description)
    {
        this.description += getDescription(description);;
        return (T) this;
    }

    public T withDescription(Element content)
    {
        if(content == null) return (T) this;
        this.description += getDescription(content);
        return (T) this;
    }

    public T withDescription(String description, boolean overwrite)
    {
        if(overwrite){
            this.description = getDescription(description);
        } else {
            this.description += getDescription(description);;
        }
        return (T) this;
    }

    public T withImage(String image)
    {
        this.image = image;
        return (T) this;
    }

    public T withPrerequisite(Prerequisite prerequisite)
    {
        this.prerequisite = prerequisite;
        return (T) this;
    }

    public T withName(String name) {
        this.name = name;
        return (T) this;
    }

    public T withId(String id) {
        this.id = id;
        return (T) this;
    }


    public String getName() {
        return name;
    }

    public T replaceAttribute(Change change) {
        this.changes.removeAll(changes.stream().filter(a -> a.getKey().equals(change.getKey())).collect(Collectors.toList()));
        this.changes.add(change);
        return (T) this;
    }


    public T withPossibleProviders(List<String> providers) {
        this.providers.addAll(providers);
        return (T) this;
    }

    public T withCategories(Set<Category> categories) {
        this.categories.addAll(categories);
        return (T) this;
    }

    public T withLink(String link) {
        this.link = link;
        return (T) this;
    }

    public String getLink(){
        return "https://swse.fandom.com" + this.link;
    }
}
