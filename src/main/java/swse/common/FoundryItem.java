package swse.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.item.Mode;
import swse.prerequisite.Prerequisite;
import static swse.util.Util.cloneList;

public abstract class FoundryItem<T extends FoundryItem> implements JSONy {
    protected String name;
    protected String description;
    protected Prerequisite prerequisite;
    protected String image;
    protected final List<Attribute> attributes;
    protected final List<ProvidedItem> providedItems;
    protected final List<Category> categories;
    protected final List<Choice> choices;

    public FoundryItem(String name) {
        this.name = name;
        this.choices = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.attributes =  new ArrayList<>();
        this.providedItems =  new ArrayList<>();
    }

    public FoundryItem(FoundryItem<?> foundryItem) {
        this.name = foundryItem.name;
        this.description = foundryItem.description;
        this.prerequisite = foundryItem.prerequisite.copy();
        this.image = foundryItem.image;
        this.choices = cloneList(foundryItem.choices);
        this.categories = cloneList(foundryItem.categories);
        this.attributes = cloneList(foundryItem.attributes);
        this.providedItems = cloneList(foundryItem.providedItems);
    }

    public static JSONObject constructModes(List<Mode> modes) {
        final JSONObject modeObjects = new JSONObject();
        int i = 0;

        if(modes == null){
            return modeObjects;
        }

        for(Mode mode : modes){
            JSONObject modeObject = new JSONObject();
            modeObject.put("name", mode.getName());
            modeObject.put("group", mode.getGroup());

            modeObject.put("attributes",
                    createAttributes(mode.getAttributes().stream().filter(Objects::nonNull).map(Attribute::toJSON).collect(Collectors.toList()))
            );

            modeObject.put("modes", constructModes(mode.getModes()));

            modeObjects.put(String.valueOf(i++),modeObject);
        }
        return modeObjects;
    }

    @Nonnull
    @Override
    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("img", image);

        JSONObject data = new JSONObject();
        data.put("description", description);
        data.put("choices", JSONy.toArray(choices));
        if (prerequisite != null) {
            data.put("prerequisite", JSONy.toJSON(prerequisite));
        }
        if (categories != null) {
            data.put("categories", JSONy.toArray(categories));
            //categories.forEach(category -> providedItems.add(ProvidedItem.create(category.getValue(), ItemType.TRAIT)));
        }
        data.put("providedItems",JSONy.toArray(providedItems));

        data.put("attributes", createAttributes(attributes.stream().filter(Objects::nonNull).map(Attribute::toJSON).collect(Collectors.toList())));

        jsonObject.put("data", data);
        return jsonObject;
    }

    /**
     * returns a JSONObject that resembles an array.  we are doing this because we want to be able to add and remove attributes from items more easily in foundry
     * @param attributes
     * @return
     */
    public static JSONObject createAttributes(List<JSONObject> attributes){
        JSONObject json = new JSONObject();
        for(int i = 0; i<attributes.size(); i++){
            json.put(String.valueOf(i), attributes.get(i));
        }

        return json;
    }


    public static JSONObject createAttribute(Attribute attribute) {
        if(attribute == null){
            return null;
        }
        return attribute.toJSON();
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

    public T withProvided(Collection<?> objects) {
        if(objects != null) {
            objects.forEach(this::withProvided);
        }
        return (T) this;
    }

    public T withProvided(Object object) {
        if(object != null) {
            if(object instanceof Attribute) {
                this.attributes.add((Attribute)object);
            } else if(object instanceof Choice){
                choices.add((Choice)object);
            } else if(object instanceof ProvidedItem){
                providedItems.add((ProvidedItem)object);
            } else if(object instanceof Category){
                categories.add((Category)object);
            }
        }
        return (T) this;
    }

    public T withDescription(String description)
    {
        this.description = description;
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


}
