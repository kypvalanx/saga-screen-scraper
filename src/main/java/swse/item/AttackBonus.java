package swse.item;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.JSONy;

class AttackBonus implements JSONy {
    private final String modifier;
    private final int bonus;

    public AttackBonus(int bonus, String modifier) {
        this.bonus = bonus;
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("modifier", modifier)
                .add("bonus", bonus)
                .toString();
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        return new JSONObject().put("bonus", bonus).put("modifier", modifier);
    }
}
