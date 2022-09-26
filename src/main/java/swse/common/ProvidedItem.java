package swse.common;

import com.google.common.base.Objects;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
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
    private boolean unlocked = false;
    private String quantity;
    private String nameOverride;
    private Map<String, String> payloads = new HashMap<>();
    private List<String> answers = new ArrayList<>();

    private ProvidedItem(String name, ItemType type, Prerequisite prerequisite){
//        Pattern p = Pattern.compile("(Dexterity|Strength|Constitution|Intelligence|Wisdom|Charisma)");
//        Matcher m = p.matcher(name);
//        if(m.find() && !name.contains("(")){
//            System.out.println(name + " " + type);
//        }
        //printUnique("ProvidedItem " + name);
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
        if(nameOverride!= null) {
            data.put("nameOverride", nameOverride);
        }
        data.put("type", type.toString());
        data.put("prerequisite", JSONy.toJSON(prerequisite));
        if(unlocked){
           data.put("unlocked", true);
        }
        if(equip != null){
            data.put("equip", equip);
        }
        if(answers.size() > 0){
            data.put("answers", answers);
        }

        data.put("attributes", JSONy.toArray(attributes));
        data.put("providedItems", JSONy.toArray(providedItems));
        data.put("modifications", JSONy.toArray(modifications));
        data.put("namedCrew", JSONy.toArray(namedCrewMembers));
        data.put("quantity", quantity);
        data.put("payloads", JSONy.toObject(payloads));
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

    public ProvidedItem withUnlocked(boolean b) {
        this.unlocked = b;
        return this;
    }

    public ProvidedItem withQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProvidedItem withCustomName(String nameOverride) {
        this.nameOverride = nameOverride;
        return this;
    }

    public ProvidedItem withPayload(String payload) {
        this.payloads.put("payload", payload);
        return this;
    }

    public ProvidedItem withPayload(String key, String payload) {
        this.payloads.put(key, payload);
        return this;
    }

    public ProvidedItem withAnswers(List<String> speciesAnswers) {
        this.answers.addAll(speciesAnswers);
        return this;
    }
}
