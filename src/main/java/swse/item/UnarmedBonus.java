package swse.item;

import org.json.JSONObject;

class UnarmedBonus
{
    private String type;
    private String damage;

    public void withDamage(String damage)
    {
        this.damage = damage;
    }

    public void withTypeConversion(String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "UnarmedBonus{" +
                "type='" + type + '\'' +
                ", damage='" + damage + '\'' +
                '}';
    }

    public JSONObject toJSON()
    {
        if(type == null && damage == null){
            return null;
        }

        JSONObject json = new JSONObject();
        if(type != null){
            json.put("type", type);
        }
        if(damage != null){
            json.put("damage", damage);
        }
        return json;
    }
}
