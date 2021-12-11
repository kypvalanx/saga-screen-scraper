package swse.item;

import org.json.JSONObject;

class ArmorAttributes
{
    private int fortitudeBonus = 0;
    private int reflexBonus = 0;
    private Integer maxDexterity = null;
    private String type;

    public void withFortitudeBonus(int fortitudeBonus)
    {
        this.fortitudeBonus = fortitudeBonus;
    }

    public void withReflexBonus(int reflexBonus)
    {
        this.reflexBonus = reflexBonus;
    }

    public void withMaxDexterity(int maxDexterity)
    {
        this.maxDexterity = maxDexterity;
    }

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
            json.put("fortitudeBonus", fortitudeBonus);
            json.put("reflexBonus", reflexBonus);
            json.put("maxDexterity", maxDexterity);

        if(type != null){
            json.put("type", type);
        }

        return json;
    }

    public void withType(String type)
    {
        this.type = type;
    }
}
