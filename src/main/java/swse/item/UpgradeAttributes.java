package swse.item;

import org.json.JSONObject;

public class UpgradeAttributes
{
    private int upgradePointCost = 0;
    private String upgradeType;

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();

        json.put("pointCost", upgradePointCost);
        json.put("type", upgradeType);

        return json;
    }

    public void withUpgradePointCost(int upgradePointCost)
    {
        this.upgradePointCost = upgradePointCost;
    }

    public void withUpgradeType(String upgradeType)
    {
        this.upgradeType = upgradeType;
    }
}
