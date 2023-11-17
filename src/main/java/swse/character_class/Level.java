package swse.character_class;

import swse.common.ChangeKey;
import swse.common.Change;
import swse.item.FoundryEffect;

public class Level extends FoundryEffect<Level> {
    private final int level;
    private Integer roughBAB;
    private Level previousLevel;

    public Level(int level) {
        super("Level " + level);
        this.flags.put("isLevel", true);
        this.flags.put("level", level);
        this.level = level;
    }

    public Level withBAB(Integer roughBAB) {
        this.roughBAB = roughBAB;
        return this;
    }

    public Level withPreviousLevel(Level previousLevel) {
        this.previousLevel = previousLevel;
        return this;
    }


    @Override
    protected void resolveDynamicValues() {
        super.resolveDynamicValues();
        this.changes.add(Change.create(ChangeKey.BASE_ATTACK_BONUS, resolveBAB()));
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
