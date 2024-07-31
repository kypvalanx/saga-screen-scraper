package swse.units;

import org.json.JSONObject;
import swse.common.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Unit extends FoundryItem<Unit> implements Copyable<Unit> {
    private String size;
    private String speciesSubType;
    private String age;
    private Integer cost;
    private Boolean isForSale = null;
    private Integer darkSideScore = null;
    private Map<String, Integer> organizationScores = new HashMap<>();
    private Integer hitPoints = null;
    private int cl = 0;
    private List<String> trainedSkills = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();
    private JSONObject data;
    private Map<String, Integer> defenses = new HashMap<String, Integer>();

    public Unit(String name) {
        super(name, "npc");
    }

    public static Unit create(String name) {
        return new Unit(name);
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        JSONObject system = json.getJSONObject("system");
        system.put("size", size);
        system.put("sizeOverride", size); //size is provided most of the time.  this should be used to double-check that the size has been resolved correctly.
        system.put("speciesSubType", speciesSubType);
        system.put("cl", cl);
        if (age != null) {
            system.put("age", age);
        }
        if (cost != null) {
            system.put("cost", cost);
        }
        if (isForSale != null) {
            system.put("isForSale", isForSale);
        }
        if (darkSideScore != null) {
            system.put("darkSideScore", darkSideScore);
        }
        if (organizationScores != null) {
            system.put("organizationScores", JSONy.toObject(organizationScores));
        }
        JSONObject health = new JSONObject();
        system.put("health", health);
        if (hitPoints != null) {
            health.put("override", hitPoints);
            health.put("value", hitPoints);
            health.put("max", hitPoints);
        }

        JSONObject defense = new JSONObject();
        system.put("defense", defense);
        if (defenses != null) {
            for (Map.Entry<String, Integer> entry: defenses.entrySet()){
                JSONObject d = new JSONObject();
                defense.put(entry.getKey(), d);
                d.put("expected", entry.getValue());
            }
        }

        system.put("skills", getSkills());
        system.put("attributes", getAttributes());
        system.put("attributeGenerationType", "Manual");


        //json.put("type", "npc-vehicle");
        //data.remove("attributes");
        //data.put("defaultAttributes", createAttributes(attributes.stream().filter(Objects::nonNull).map(Attribute::toJSON).collect(Collectors.toList())));


        return json;
    }

    private JSONObject getSkills() {
        JSONObject skills = new JSONObject();
        for (String skill :
                trainedSkills) {
            JSONObject value = new JSONObject();
            value.put("trained", true);
            skills.put(skill.toLowerCase(), value);
        }
        return skills;
    }
    private JSONObject getAttributes() {
        JSONObject attributeObjects = new JSONObject();
        for (Attribute attribute :
                attributes) {
            JSONObject value = attribute.toJSON();
            attributeObjects.put(attribute.getKey().value(), value);
        }
        return attributeObjects;
    }

    @Override
    public void preJSON(){
//        if(this.hitPoints != null){
//
//        }
//        List<ProvidedItem> classes = providedItems.stream().filter(providedItem -> providedItem.getType().equals(ItemType.CLASS)).collect(Collectors.toList());
//
//
//        System.out.println("preJSON");
    }

    @Override
    public Unit copy() {
        return null;
    }

    public Unit withSize(String size) {
        this.size = size;
        return this;
    }

    public Unit withSpeciesSubType(String group) {
        this.speciesSubType = group;
        return this;
    }

    public Unit withAge(String age) {
        this.age = age;
        return this;
    }

    public Unit withCost(int cost) {
        this.cost = cost;
        return this;
    }

    public Unit withIsForSale(boolean b) {

        this.isForSale = b;
        return this;
    }

    public Unit withDarkSideScore(int darkSideScore) {
        this.darkSideScore = darkSideScore;
        return this;
    }

    public Unit withOrganizationScore(String organization, int score) {
        this.organizationScores.put(organization, score);
        return this;
    }

    public Unit withHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
        return this;
    }

    public Unit withAttribute(String attribute, String value) {
        this.attributes.add(Attribute.create(ChangeKey.valueOf(attribute), value));
        return this;
    }

    public Unit withAttribute(Attribute attribute) {
        this.attributes.add(attribute);
        return this;
    }

    public Unit withCL(int cl) {
        this.cl = cl;
        return this;
    }

    public Unit withTrainedSkill(String trainedSkill) {
        this.trainedSkills.add(trainedSkill);
        return this;
    }

    @Override
    protected List<String> getFlags() {
        List<String> flags = super.getFlags();
        flags.add("ATTRIBUTES_ARE_ESTIMATE");
        flags.add("USE_NAME_IN_KEY");

        return flags;
    }

    public void addAction(String s) {
        this.changes.add(Change.create(ChangeKey.ACTION, s));
    }

    public Unit withType(String type) {
        this.type = type;
        return this;
    }

    public Unit withSystem(JSONObject system) {
        this.system = system;
        return this;
    }

    public Unit withDefense(String key, int value) {
        this.defenses.put(key, value);
        return this;
    }
}
