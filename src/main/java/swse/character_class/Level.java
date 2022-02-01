package swse.character_class;

import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Level extends FoundryItem<Level>  implements Copyable<Level> {
    private final int level;
    private Integer roughBAB;
    private List<Feature> parseFeatures;
    private Level previousLevel;

    public Level(int level) {
        super("");
        this.level = level;
    }

    public void withBAB(Integer roughBAB) {

        this.roughBAB = roughBAB;
    }

    public void withPreviousLevel(Level previousLevel) {

        this.previousLevel = previousLevel;
    }


    @Nonnull
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("level", level);
        json.remove("name");
        JSONObject attr = json.getJSONObject("data").getJSONObject("attributes");
        attr.put("" + attr.length(), createAttribute("baseAttackBonus",resolveBAB()));

        return json;
    }

    private int resolveBAB() {
        int bab = roughBAB;
        if (previousLevel != null) {
            bab -= previousLevel.getBab();
        }
        return bab;
    }

    private int getBab() {
        return roughBAB;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public Level copy() {
        return null;
    }
}
