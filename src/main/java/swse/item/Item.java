package swse.item;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Attribute;
import swse.common.Copyable;
import swse.common.FoundryItem;
import static swse.util.Util.cloneList;

class Item extends FoundryItem<Item> implements Copyable<Item> {
    private String type;
    private String subtype;
    private String size;
    private String cost;
    private String weight;
    private String availability;
    private String source;
    private List<Mode> modes = new LinkedList<>();

    public static Item create(String itemName) {
        return new Item(itemName);
    }

    public Item(String name) {
        super(name);
    }

    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        json.put("type", type.toLowerCase());

        data
                .put("subtype", subtype)
                .put("size", size)
                .put("cost", cost)
                .put("weight", weight)
                .put("availability", availability)
                .put("modes", constructModes(modes))
                .put("source", source);
        return json;
    }


    public Item withType(String type) {
        this.type = type;
        return this;
    }

    public Item withSubtype(String subtype) {
        this.subtype = subtype;
        return this;
    }

    public Item withCost(String cost) {
        this.cost = cost;
        return this;
    }

    public Item withSize(String size) {
        this.size = size;
        return this;
    }

    public Item withWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public Item withSource(String source) {
        this.source = source;
        return this;
    }

    public Item withAvailability(String availability) {
        this.availability = availability;
        return this;
    }


    public Item withSplash(String splash) {
        this.withProvided(Attribute.create("splash", splash));
        return this;
    }

    public Item withHeirloomBonus(String heirloomBonus) {
        this.withProvided(Attribute.create("heirloomBonus", heirloomBonus));
        return this;
    }

    public Item withSeeAlso(String seeAlso) {
        this.withProvided(Attribute.create("seeAlso", seeAlso));
        return this;
    }

    public Item withBaseSpeed(String baseSpeed) {
        this.withProvided(Attribute.create("baseSpeed", baseSpeed));
        return this;
    }

    public Item withRequires(String requires) {
        this.withProvided(Attribute.create("requires", requires));
        return this;
    }

    public Item withTrigger(String trigger) {
        this.withProvided(Attribute.create("trigger", trigger));
        return this;
    }

    public Item withRecurrence(String recurrence) {
        this.withProvided(Attribute.create("recurrence", recurrence));
        return this;
    }

    public Item withSkillChecks(List<String> skillChecks) {
        for (String s : skillChecks) {
            attributes.add(Attribute.create("skillCheck", s));
        }


        return this;
    }

    public Item withRejectionAttackBonus(String rejectionAttackBonus) {
        this.withProvided(Attribute.create("rejectionAttackBonus", rejectionAttackBonus));
        return this;
    }

    public Item withInstallationCost(String installationCost) {
        this.withProvided(Attribute.create("installationCost", installationCost));
        return this;
    }

    public Item withUpgradePointCost(String upgradePointCost) {
        this.withProvided(Attribute.create("upgradePointCost", upgradePointCost));
        return this;
    }

    public Item withChallengeLevel(String challengeLevel) {
        this.withProvided(Attribute.create("challengeLevel", challengeLevel));
        return this;
    }

    public Item withSpecial(List<String> specials) {
        if (specials != null) {
            for (String special : specials) {
                if (special.toLowerCase().contains("can be thrown")) {
                    this.withProvided(Attribute.create("isThrowable", true));
                } else if (special.toLowerCase().contains("is a reach weapon")) {
                    this.withProvided(Attribute.create("isReach", true));
                } else {
                    this.withProvided(Attribute.create("special", special));
                }
            }
        }
        return this;
    }

    public Item withKeywords(List<String> keywords) {
        this.withProvided(Attribute.create("keywords", keywords));
        return this;
    }

    public Item withModes(List<Mode> modes) {
        this.modes.addAll(modes);
        return this;
    }

    public Item withProvided(Object object){
        super.withProvided(object);
        if(object instanceof Mode){
            modes.add((Mode)object);
        }
        return this;
    }

    public Item withDamageDie(String damageDie) {
        this.withProvided(Attribute.create("damageDie", damageDie));
        return this;
    }

    public Item withStunDamageDie(String stunDamageDie) {
        if(stunDamageDie != null) {
            modes.add(Mode.create("Stun", List.of(Attribute.create("stunDamageDie", stunDamageDie))));
        }
        return this;
    }

    public Item withDamageType(String damageType) {
        this.withProvided(Attribute.create("damageType", damageType));
        return this;
    }

    public Item withUnarmedDamage(Integer unarmedDamage) {
        this.withProvided(Attribute.create("unarmedDamage", unarmedDamage));
        return this;
    }

    public Item withUnarmedModifier(String unarmedModifier) {
        this.withProvided(Attribute.create("unarmedModifier", unarmedModifier));
        return this;
    }

    //    protected String image;
//    protected final List<Attribute> attributes;
//    protected final List<ProvidedItem> providedItems;
//    protected final List<Category> categories;
//    protected final List<Choice> choices;
    @Override
    public Item copy() {
        final Item item = new Item(name)
                .withDescription(description);
        if (prerequisite != null) {
            item.withPrerequisite(prerequisite.copy());
        }
        item.withImage(image)
                .withType(type)
                .withSubtype(subtype)
                .withSize(size)
                .withCost(cost)
                .withWeight(weight)
                .withAvailability(availability)
                .withSource(source)
                .withProvided(cloneList(modes))
                .withProvided(cloneList(attributes))
                .withProvided(cloneList(providedItems))
                .withProvided(cloneList(categories))
                .withProvided(cloneList(choices));

        return item;

    }
}
