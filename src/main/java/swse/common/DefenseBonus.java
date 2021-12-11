package swse.common;

import org.json.JSONObject;

public class DefenseBonus
{
    private String defense;
    private int bonus;
    private String modifier;

    public static Attribute createDefenseBonus(Integer bonus, String defense)
    {
        return Attribute.create(defense+"DefenseBonus", bonus);
    }

    public static Attribute createDefenseBonus(Integer bonus, String defense, String modifier)
    {
        if(bonus == null){
            return null;
        }
        return Attribute.create(defense+"DefenseBonus", bonus).withModifier(modifier);
    }

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("defense", defense);
        json.put("bonus", bonus);
        if(modifier != null)
        {
            json.put("modifier", modifier);
        }
        return json;
    }
}
