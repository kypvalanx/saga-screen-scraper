package swse.item;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.JSONy;

class Attack implements JSONy {
    private AttackBonus attackBonus;
    private DamageBonus damageBonus;
    private SpecialBonus specialBonus;
    private final String type;
    private SubType subtype;

    public Attack(String type) {
        this.type = type;
    }

    public void withAttackBonus(AttackBonus attackBonus) {
        this.attackBonus = attackBonus;
    }

    public void withDamage(DamageBonus damageBonus) {
        this.damageBonus = damageBonus;

    }

    public void withSpecial(SpecialBonus specialBonus) {

        this.specialBonus = specialBonus;
    }

    public boolean isEmpty() {
        return attackBonus == null && damageBonus == null && specialBonus == null;
    }

    public DamageBonus getDamage() {
        return damageBonus;
    }

    public void withDamage(DamageBonus damage, String modifier) {
        if (damage != null) {
            ArrayList<String> modifiers = new ArrayList<>(damage.getModifier());
            modifiers.add(modifier);
            withDamage(new DamageBonus(damage.getDamage(), modifiers.toArray(new String[0])));
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("attackBonus", attackBonus)
                .add("damageBonus", damageBonus)
                .add("specialBonus", specialBonus)
                .add("type", type)
                .add("subtype", subtype)
                .toString();
    }

    public void withDamageType(SubType subtype) {
        this.subtype = subtype;
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {

        return new JSONObject()
                .put("attack", JSONy.toJSON(attackBonus))
                .put("damage", JSONy.toJSON(damageBonus))
                .put("special", JSONy.toJSON(specialBonus))
                .put("type", type)
                .put("damageType", subtype);
    }
}
