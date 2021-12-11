package swse.item;

import org.json.JSONObject;

class StunSetting
{
    private boolean isAvailable;
    private boolean isOnly;
    private String customStun;
    private String dieEquation;

    public void isAvailable(boolean isAvailable)
    {
        this.isAvailable = isAvailable;
    }

    public void isOnly(boolean isOnly)
    {
        this.isOnly = isOnly;
    }

    public void withCustom(String customStun)
    {
        this.customStun = customStun;
    }

    public void withDieEquation(String dieEquation)
    {
        this.dieEquation = dieEquation;
    }

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();

        if(isAvailable){
            json.put("isAvailable", true);
        }
        if(isOnly){
            json.put("isOnly", true);
        }
        if(customStun != null){
            json.put("custom", customStun);
        }
        if(dieEquation != null){
            json.put("dieEquation", dieEquation);
        }

        return json;
    }

    public String getDieEquation() {
        return dieEquation;
    }
}
