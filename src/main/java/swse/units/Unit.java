package swse.units;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONObject;
import swse.common.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        JSONObject data = json.getJSONObject("data");
        data.put("size", size); //size is provided most of the time.  this should be used to double-check that the size has been resolved correctly.
        data.put("speciesSubType", speciesSubType);
        data.put("cl", cl);
        if (age != null) {
            data.put("age", age);
        }
        if (cost != null) {
            data.put("cost", cost);
        }
        if (isForSale != null) {
            data.put("isForSale", isForSale);
        }
        if (darkSideScore != null) {
            data.put("darkSideScore", darkSideScore);
        }
        if (organizationScores != null) {
            data.put("organizationScores", JSONy.toObject(organizationScores));
        }
        if (hitPoints != null) {
            data.put("hitPoints", hitPoints);
        }

            JSONObject skills = new JSONObject();
        for (String skill :
                trainedSkills) {
            JSONObject value = new JSONObject();
            value.put("trained", true);
            skills.put(skill.toLowerCase(), value);
        }
            data.put("skills", skills);


        //json.put("type", "npc-vehicle");
        //data.remove("attributes");
        //data.put("defaultAttributes", createAttributes(attributes.stream().filter(Objects::nonNull).map(Attribute::toJSON).collect(Collectors.toList())));


        return json;
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
        this.attributes.add(Attribute.create(AttributeKey.valueOf(attribute), value));
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
}
