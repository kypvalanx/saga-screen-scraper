package swse.item;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Attribute;
import swse.common.AttributeKey;
import swse.common.Copyable;
import swse.common.FoundryItem;
import static swse.util.Util.cloneList;

class Item extends FoundryItem<Item> implements Copyable<Item> {
    private String type;
    private String size;
    private String cost;
    private String weight;
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
                .put("size", size)
                .put("cost", cost)
                .put("weight", weight)
                .put("availability", availability)
                .put("modes", constructModes(modes));
        return json;
    }


    public Item withType(String type) {
        this.type = type;
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


    public Item withSplash(String splash) {
        this.withProvided(Attribute.create(AttributeKey.SPLASH, splash));
        return this;
    }

    public Item withHeirloomBonus(String heirloomBonus) {
        this.withProvided(Attribute.create(AttributeKey.HEIRLOOM_BONUS, heirloomBonus));
        return this;
    }

    public Item withSeeAlso(String seeAlso) {
        this.withProvided(Attribute.create(AttributeKey.SEE_ALSO, seeAlso));
        return this;
    }

    public Item withBaseSpeed(String baseSpeed) {
        this.withProvided(Attribute.create(AttributeKey.BASE_SPEED, baseSpeed));
        return this;
    }

    public Item withRequires(String requires) {
        this.withProvided(Attribute.create(AttributeKey.REQUIRES, requires));
        return this;
    }

    public Item withTrigger(String trigger) {
        this.withProvided(Attribute.create(AttributeKey.TRIGGER, trigger));
        return this;
    }

    public Item withRecurrence(String recurrence) {
        this.withProvided(Attribute.create(AttributeKey.RECURRENCE, recurrence));
        return this;
    }

    public Item withSkillChecks(List<String> skillChecks) {
        for (String s : skillChecks) {
            attributes.add(Attribute.create(AttributeKey.SKILL_CHECK, s));
        }


        return this;
    }

    public Item withRejectionAttackBonus(String rejectionAttackBonus) {
        this.withProvided(Attribute.create(AttributeKey.REJECTION_ATTACK_BONUS, rejectionAttackBonus));
        return this;
    }

    public Item withInstallationCost(String installationCost) {
        this.withProvided(Attribute.create(AttributeKey.INSTALLATION_COST, installationCost));
        return this;
    }

    public Item withUpgradePointCost(String upgradePointCost) {
        this.withProvided(Attribute.create(AttributeKey.UPGRADE_POINT_COST, upgradePointCost));
        return this;
    }

    public Item withChallengeLevel(String challengeLevel) {
        this.withProvided(Attribute.create(AttributeKey.CHALLENGE_LEVEL, challengeLevel));
        return this;
    }

    public Item withSpecial(List<String> specials) {
        if (specials != null) {
            for (String special : specials) {
                if (special.toLowerCase().contains("can be thrown")) {
                    this.withProvided(Attribute.create(AttributeKey.IS_THROWABLE, true));
                } else if (special.toLowerCase().contains("is a reach weapon")) {
                    this.withProvided(Attribute.create(AttributeKey.IS_REACH, true));
                } else {
                    this.withProvided(Attribute.create(AttributeKey.SPECIAL, special));
                }
            }
        }
        return this;
    }

    public Item withKeywords(List<String> keywords) {
        this.withProvided(Attribute.create(AttributeKey.KEYWORDS, keywords));
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
        this.withProvided(Attribute.create(AttributeKey.DAMAGE, damageDie));
        return this;
    }

    public Item withStunDamageDie(String stunDamageDie) {
        if(stunDamageDie != null) {
            modes.add(Mode.create("Stun", List.of(Attribute.create(AttributeKey.STUN_DAMAGE, stunDamageDie))));
        }
        return this;
    }

    public Item withDamageType(String damageType) {
        if(damageType == null){
            return this;
        }

        if(damageType.contains(" and ")){
            String[] toks = damageType.split(" and ");

            for(String tok : toks){
                this.withProvided(Attribute.create(AttributeKey.DAMAGE_TYPE, tok.trim()));
            }
        } else if(damageType.contains(" or ")){
            String[] toks = damageType.split(" or ");

            for(String tok : toks){

                modes.add(Mode.create(tok.trim(), "Damage Type", List.of(Attribute.create(AttributeKey.DAMAGE_TYPE, tok.trim()))));
            }
        } else {
            this.withProvided(Attribute.create(AttributeKey.DAMAGE_TYPE, damageType));
        }
        return this;
    }

    public Item withUnarmedDamage(Integer unarmedDamage) {
        this.withProvided(Attribute.create(AttributeKey.UNARMED_BONUS_DAMAGE, unarmedDamage));
        return this;
    }

    public Item withUnarmedModifier(String unarmedModifier) {
        this.withProvided(Attribute.create(AttributeKey.UNARMED_MODIFIER, unarmedModifier));
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
