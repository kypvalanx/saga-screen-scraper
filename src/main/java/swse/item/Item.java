package swse.item;

import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.*;

import static swse.util.Util.cloneList;

class Item extends FoundryItem<Item> implements Copyable<Item> {
    private String size;

    public static Item create(String itemName, String type) {
        return new Item(itemName, type);
    }

    public Item(String name, String type) {
        super(name, type);
    }

    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        JSONObject system = json.getJSONObject("system");

        system
                .put("size", size);
        return json;
    }


    public Item withSize(String size) {
        this.size = size;
        return this;
    }

    public Item withWeight(String weight) {
        this.withProvided(Change.create(AttributeKey.WEIGHT, weight));
        return this;
    }


    public Item withSplash(String splash) {
        return this.withProvided(Change.create(AttributeKey.SPLASH, splash));
    }

    public Item withHeirloomBonus(String heirloomBonus) {
        return this.withProvided(Change.create(AttributeKey.HEIRLOOM_BONUS, heirloomBonus));
    }

    public Item withSeeAlso(String seeAlso) {
        this.withProvided(Change.create(AttributeKey.SEE_ALSO, seeAlso));
        return this;
    }

    public Item withBaseSpeed(String baseSpeed) {
        this.withProvided(Change.create(AttributeKey.BASE_SPEED, baseSpeed));
        return this;
    }

    public Item withRequires(String requires) {
        this.withProvided(Change.create(AttributeKey.REQUIRES, requires));
        return this;
    }

    public Item withTrigger(String trigger) {
        this.withProvided(Change.create(AttributeKey.TRIGGER, trigger));
        return this;
    }

    public Item withRecurrence(String recurrence) {
        this.withProvided(Change.create(AttributeKey.RECURRENCE, recurrence));
        return this;
    }

    public Item withSkillChecks(List<String> skillChecks) {
        for (String s : skillChecks) {
            changes.add(Change.create(AttributeKey.SKILL_CHECK, s));
        }


        return this;
    }

    public Item withRejectionAttackBonus(String rejectionAttackBonus) {
        this.withProvided(Change.create(AttributeKey.REJECTION_ATTACK_BONUS, rejectionAttackBonus));
        return this;
    }

    public Item withInstallationCost(String installationCost) {
        this.withProvided(Change.create(AttributeKey.INSTALLATION_COST, installationCost));
        return this;
    }

    public Item withUpgradePointCost(String upgradePointCost) {
        this.withProvided(Change.create(AttributeKey.UPGRADE_POINT_COST, upgradePointCost));
        return this;
    }

    public Item withChallengeLevel(String challengeLevel) {
        this.withProvided(Change.create(AttributeKey.CHALLENGE_LEVEL, challengeLevel));
        return this;
    }

    public Item withSpecial(List<String> specials) {
        if (specials != null) {
            for (String special : specials) {
                if (special.toLowerCase().contains("can be thrown")) {
                    this.withProvided(Change.create(AttributeKey.IS_THROWABLE, true));
                } else if (special.toLowerCase().contains("is a reach weapon")) {
                    this.withProvided(Change.create(AttributeKey.IS_REACH, true));
                } else {
                    this.withProvided(Change.create(AttributeKey.SPECIAL, special));
                }
            }
        }
        return this;
    }

    public Item withKeywords(List<String> keywords) {
        this.withProvided(Change.create(AttributeKey.KEYWORDS, keywords));
        return this;
    }

    public Item withModes(List<Effect> effects) {
        this.effects.addAll(effects);
        return this;
    }

    public Item withProvided(Object object){
        super.withProvided(object);
        if(object instanceof Effect){
            effects.add((Effect)object);
        }
        return this;
    }

    public Item withDamageDie(String damageDie) {
        this.withProvided(Change.create(AttributeKey.DAMAGE, damageDie));
        return this;
    }

    public Item withStunDamageDie(String stunDamageDie) {
        if(stunDamageDie != null) {
            Change change = Change.create(AttributeKey.DAMAGE_TYPE, "Stun");
            change.withMode(ActiveEffectMode.OVERRIDE);
            Change change1 = Change.create(AttributeKey.DAMAGE, stunDamageDie);
            change1.withMode(ActiveEffectMode.OVERRIDE);
            effects.add(Effect.create("Stun", List.of(change1, change)));
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
                this.withProvided(Change.create(AttributeKey.DAMAGE_TYPE, tok.trim()));
            }
        } else if(damageType.contains(" or ")){
            String[] toks = damageType.split(" or ");

            for(String tok : toks){

                effects.add(Effect.create(tok.trim(), "Damage Type", List.of(Change.create(AttributeKey.DAMAGE_TYPE, tok.trim()))));
            }
        } else {
            this.withProvided(Change.create(AttributeKey.DAMAGE_TYPE, damageType));
        }
        return this;
    }

    public Item withUnarmedDamage(Integer unarmedDamage) {
        this.withProvided(Change.create(AttributeKey.UNARMED_BONUS_DAMAGE, unarmedDamage));
        return this;
    }

    public Item withUnarmedModifier(String unarmedModifier) {
        this.withProvided(Change.create(AttributeKey.UNARMED_MODIFIER, unarmedModifier));
        return this;
    }

    @Override
    public Item copy() {
        final Item item = new Item(name, type)
                .withDescription(description);
        if (prerequisite != null) {
            item.withPrerequisite(prerequisite.copy());
        }
        item.withImage(image)
                .withSubtype(subtype)
                .withSize(size)
                .withSource(source)
                .withProvided(cloneList(effects))
                .withProvided(cloneList(changes))
                .withProvided(cloneList(providedItems))
                .withProvided(cloneList(categories))
                .withProvided(cloneList(choices));

        return item;

    }
}
