package swse.common;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import swse.prerequisite.AndPrerequisite;
import swse.prerequisite.Prerequisite;

//identifies a trait that should be added as opposed to a complete trait
public class ProvidedItem implements JSONy, Copyable<ProvidedItem>
{
    private final String name;
    private final ItemType type;
    private final Prerequisite prerequisite;
    private final List<Attribute> attributes = new LinkedList<>();
    private final List<ProvidedItem> providedItems = new LinkedList<>();
    private final List<Modification> modifications = new LinkedList<>();
    private final List<NamedCrew> namedCrewMembers = new LinkedList<>();
    private String equip;

    private ProvidedItem(String name, ItemType type, Prerequisite prerequisite){
        this.name = name;
        this.type = type;
        this.prerequisite = prerequisite;
    }

    public Prerequisite getPrerequisite() {
        return prerequisite;
    }

    public String getName() {
        return name;
    }

    public ItemType getType(){
        return type;
    }

    public static ProvidedItem create(String itemName, ItemType itemType, String... prerequisite){
        List<Prerequisite> emptyArray = new ArrayList<>();

        for (String s : prerequisite)
        {
            emptyArray.add(Prerequisite.create(s));
        }
        if(emptyArray.size() == 1){
            return create(itemName, itemType, emptyArray.get(0));
        }
        if(emptyArray.size() == 0){
            return create(itemName, itemType);
        }
        return create(itemName, itemType, new AndPrerequisite(emptyArray));
    }
    public static ProvidedItem create(String category, ItemType itemType, Prerequisite prerequisite){
        return new ProvidedItem(category, itemType, prerequisite);
    }
    public static ProvidedItem create(String itemName, ItemType itemType){
        return new ProvidedItem(itemName, itemType, null);
    }

    public static List<ProvidedItem> getTraits(Element content){
        return content.select("a.newcategory").stream().map(Element::text).map(text -> text.replace("Condition ", "Conditional ")).map(t -> create(t, ItemType.TRAIT)).collect(Collectors.toList());
    }

    @Nonnull
    public JSONObject toJSON(){
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("type", type.toString());
        data.put("prerequisite", JSONy.toJSON(prerequisite));
        if(equip != null){
            data.put("equip", equip);
        }

        data.put("attributes", JSONy.toArray(attributes));
        data.put("providedItems", JSONy.toArray(providedItems));
        data.put("modifications", JSONy.toArray(modifications));
        data.put("namedCrew", JSONy.toArray(namedCrewMembers));
        return data;
    }

    @Override
    public String toString()
    {
        return toJSON().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProvidedItem that = (ProvidedItem) o;
        return Objects.equal(name, that.name) && type == that.type && Objects.equal(prerequisite, that.prerequisite);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, type, prerequisite);
    }

    @Override
    public ProvidedItem copy() {
        return new ProvidedItem(name, type, prerequisite.copy());
    }

    public ProvidedItem withProvided(Attribute attribute) {
        attributes.add(attribute);
        return this;
    }
    public ProvidedItem overwriteProvided(Attribute attribute) {
        List<Attribute> filtered = attributes.stream().filter(a -> a.getKey().equals(attribute.getKey())).collect(Collectors.toList());

        if(filtered.size() > 0){
            filtered.forEach(a -> a.withValue(attribute.getValue()));
        } else {
            attributes.add(attribute);
        }
        return this;
    }
    public ProvidedItem withProvided(ProvidedItem providedItem) {
        providedItems.add(providedItem);
        return this;
    }
    public ProvidedItem withProvided(Modification modification) {
        modifications.add(modification);
        return this;
    }
    public ProvidedItem withProvided(NamedCrew namedCrew) {
        namedCrewMembers.add(namedCrew);
        return this;
    }

    public ProvidedItem withEquip(String type) {
        this.equip = type;
        return this;
    }
}
