package swse.item;

import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.*;

import static swse.util.Util.cloneList;
import static swse.util.Util.printUnique;

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
        if(size != null){
            system.getJSONArray("changes").put(Change.create(ChangeKey.SIZE, size).toJSON());
        }

        return json;
    }


    public Item withSize(String size) {
        this.size = size;
        return this;
    }

    public Item withWeight(String weight) {
        if(weight == null || "Varies".equals(weight)){
            return this;
        }

        if(weight.endsWith(" (empty)")){
            weight = weight.substring(0, weight.length()-8);
        }

        if(weight.endsWith("+ (see below)")){
            weight = weight.substring(0, weight.length()-13);
        }

        if(weight.endsWith(" (when using a secondary data store) or - (when concealed in the Droid's main Processor)")){
            weight = weight.substring(0, weight.length()-88);
        }



        if(weight.endsWith("/Meter")){
            weight = weight.substring(0, weight.length()-6);
            this.withName(this.getName() + " (Meter)");
        }

        if("-".equals(weight) || "Appendage Weight + Weapon Weight".equals(weight)){
            weight = "0 kg";
        }
        //weight = weight.replaceAll("/Kilograms?/", "kg");
        if(!weight.endsWith("kg") && !weight.endsWith("Kilograms")&& !weight.endsWith("kilograms") && !weight.endsWith("Kilogram")&& !weight.endsWith("Tons") && !weight.contains("Appendage Weight")){

            printUnique(this.name + " [" + weight + "] "+this.getLink());
        }
        this.with(Change.create(ChangeKey.WEIGHT, weight));
        return this;
    }


    public Item withSplash(String splash) {
        return this.with(Change.create(ChangeKey.SPLASH, splash));
    }

    public Item withHeirloomBonus(String heirloomBonus) {
        return this.with(Change.create(ChangeKey.HEIRLOOM_BONUS, heirloomBonus));
    }

    public Item withSeeAlso(String seeAlso) {
        this.with(Change.create(ChangeKey.SEE_ALSO, seeAlso));
        return this;
    }

    public Item withBaseSpeed(String baseSpeed) {
        this.with(Change.create(ChangeKey.BASE_SPEED, baseSpeed));
        return this;
    }

    public Item withRequires(String requires) {
        this.with(Change.create(ChangeKey.REQUIRES, requires));
        return this;
    }

    public Item withTrigger(String trigger) {
        this.with(Change.create(ChangeKey.TRIGGER, trigger));
        return this;
    }

    public Item withRecurrence(String recurrence) {
        this.with(Change.create(ChangeKey.RECURRENCE, recurrence));
        return this;
    }

    public Item withSkillChecks(List<String> skillChecks) {
        for (String s : skillChecks) {
            changes.add(Change.create(ChangeKey.SKILL_CHECK, s));
        }


        return this;
    }

    public Item withRejectionAttackBonus(String rejectionAttackBonus) {
        this.with(Change.create(ChangeKey.REJECTION_ATTACK_BONUS, rejectionAttackBonus));
        return this;
    }

    public Item withInstallationCost(String installationCost) {
        this.with(Change.create(ChangeKey.INSTALLATION_COST, installationCost));
        return this;
    }

    public Item withUpgradePointCost(String upgradePointCost) {
        this.with(Change.create(ChangeKey.UPGRADE_POINT_COST, upgradePointCost));
        return this;
    }

    public Item withChallengeLevel(String challengeLevel) {
        this.with(Change.create(ChangeKey.CHALLENGE_LEVEL, challengeLevel));
        return this;
    }

    public Item withSpecial(List<String> specials) {
        if (specials != null) {
            for (String special : specials) {
                if (special.toLowerCase().contains("can be thrown")) {
                    this.with(Change.create(ChangeKey.IS_THROWABLE, true));
                } else if (special.toLowerCase().contains("is a reach weapon")) {
                    this.with(Change.create(ChangeKey.IS_REACH, true));
                } else {
                    this.with(Change.create(ChangeKey.SPECIAL, special));
                }
            }
        }
        return this;
    }

    public Item withKeywords(List<String> keywords) {
        this.with(Change.create(ChangeKey.KEYWORDS, keywords));
        return this;
    }

    public Item withDamageDie(String damageDie) {
        this.with(Change.create(ChangeKey.DAMAGE, damageDie));
        return this;
    }

    public Item withStunDamageDie(String stunDamageDie) {
        if(stunDamageDie != null) {
            Change change = Change.create(ChangeKey.DAMAGE_TYPE, "Stun");
            change.withMode(ActiveEffectMode.OVERRIDE);
            Change change1 = Change.create(ChangeKey.DAMAGE, stunDamageDie);
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
                this.with(Change.create(ChangeKey.DAMAGE_TYPE, tok.trim()));
            }
        } else if(damageType.contains(" or ")){
            String[] toks = damageType.split(" or ");

            for(String tok : toks){

                effects.add(Effect.create(tok.trim(), "Damage Type", List.of(Change.create(ChangeKey.DAMAGE_TYPE, tok.trim()))));
            }
        } else {
            this.with(Change.create(ChangeKey.DAMAGE_TYPE, damageType));
        }
        return this;
    }

    public Item withUnarmedDamage(Integer unarmedDamage) {
        this.with(Change.create(ChangeKey.UNARMED_BONUS_DAMAGE, unarmedDamage));
        return this;
    }

    public Item withUnarmedModifier(String unarmedModifier) {
        this.with(Change.create(ChangeKey.UNARMED_MODIFIER, unarmedModifier));
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
                .with(cloneList(effects))
                .with(cloneList(changes))
                .with(cloneList(providedItems))
                .with(cloneList(categories))
                .with(cloneList(choices));

        return item;

    }
}
