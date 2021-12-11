package swse.item;

import org.json.JSONObject;

class EquipmentAttributes
{
    private BaseSpeed baseSpeed;

    public void withBaseSpeed(String s)
    {
        this.baseSpeed = new BaseSpeed(s);
    }

    public JSONObject toJSON()
    {
        if(baseSpeed == null){
            return null;
        }

        return new JSONObject().put("baseSpeed", baseSpeed.toJSON());
    }

}
