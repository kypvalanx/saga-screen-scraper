package swse.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

class Damage
{
    private List<JSONObject> attacks = new ArrayList<>();
    private UnarmedBonus unarmedBonus;
    private String customText;
    private String damageDie;

    public Damage() {

    }

    public void put(String key, String value)
    {
        key = key == null? "base": key;

        if("base".equals(key)){
            this.damageDie = value;
        }
        attacks.add(createAttribute(key, value));
    }

    public void withUnarmedBonus(UnarmedBonus unarmedBonus)
    {
        this.unarmedBonus = unarmedBonus;
    }

    public boolean isEmpty()
    {
        return attacks.isEmpty() && unarmedBonus == null;
    }

    public void withCustomText(String customText)
    {
        this.customText = customText;
    }

    @Override
    public String toString()
    {
        return "Damage{" +
                "attacks=" + attacks +
                ", unarmedBonus=" + unarmedBonus +
                ", customText='" + customText + '\'' +
                '}';
    }

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();

        if(customText != null){
            json.put("custom", customText);
        }
        if(unarmedBonus != null){
            json.put("unarmed", unarmedBonus.toJSON());
        }
        json.put("attacks", attacks);

        return json;
    }

    private JSONObject createAttribute(String key, Object value)
    {


        JSONObject json = new JSONObject();

        if(value instanceof String){
            json.put("dtype", "String");
        }
        json.put("value", value);
        json.put("key", key);
        return json;
    }

    public String getBaseDamageDie() {
        return damageDie;
    }
}
