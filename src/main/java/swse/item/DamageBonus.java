package swse.item;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.JSONy;

class DamageBonus implements JSONy {
    private final String damage;
    private final List<String> modifiers = new ArrayList<>();

    public DamageBonus(String damage, String... modifiers) {
        if(modifiers != null) {
            this.modifiers.addAll(Arrays.asList(modifiers));
        }
        this.damage = damage;
    }

    public DamageBonus withModifier(String modifier){
        this.modifiers.add(modifier);
        return this;
    }

    public String getDamage() {
        return damage;
    }

    public List<String> getModifier() {
        return modifiers;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("damage", damage)
                .add("modifiers", modifiers)
                .toString();
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        return new JSONObject().put("damage", damage).put("modifiers", modifiers);
    }
}
