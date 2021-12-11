package swse.character_class;

import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.FoundryItem;

class CharacterClass extends FoundryItem<CharacterClass>
{
    private Levels leveledStatMap;

    private CharacterClass(String name)
    {
        super(name);
    }

    public static CharacterClass create(String name){
        return new CharacterClass(name);
    }

    @Nonnull
    public JSONObject toJSON()
    {
        JSONObject json = super.toJSON();
        json.put("type", "class");

        JSONObject data = json.getJSONObject("data");

        data.put("levels", leveledStatMap.toJSON());

        return json;
    }




        public CharacterClass withLeveledStats(Levels leveledStatMap)
        {
            this.leveledStatMap = leveledStatMap;
            return this;
        }


}
