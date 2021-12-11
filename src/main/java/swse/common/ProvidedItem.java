package swse.common;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import swse.prerequisite.AndPrerequisite;
import swse.prerequisite.Prerequisite;

//identifies a trait that should be added as opposed to a complete trait
public class ProvidedItem implements JSONy
{
    private final String name;
    private final ItemType type;
    private final Prerequisite prerequisite;

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
        return content.select("li.category").stream().map(Element::text).map(text -> text.replace("Condition ", "Conditional ")).map(t -> create(t, ItemType.TRAIT)).collect(Collectors.toList());
    }

    @Nonnull
    public JSONObject toJSON(){
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("type", type);
        data.put("prerequisite", JSONy.toJSON(prerequisite));
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
}
