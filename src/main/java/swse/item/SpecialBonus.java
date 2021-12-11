package swse.item;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.JSONy;

class SpecialBonus implements JSONy {
    private final String modifier;
    private final String special;

    public SpecialBonus(String special, String modifier) {
        //TODO add info for special, varies, and see above/below
        //printUnique(Context.getValue("name"), special, modifier);
        this.special = special;
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("modifier", modifier)
                .add("special", special)
                .toString();
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        return new JSONObject().put("special", special).put("modifier", modifier);
    }
}
