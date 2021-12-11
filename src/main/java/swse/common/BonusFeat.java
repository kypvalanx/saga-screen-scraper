package swse.common;

import org.json.JSONObject;

public class BonusFeat
{
    private String feat;
    private boolean acknowledgePrerequisites;
    private String trainedSkill;
    private String requiredFeat;
    private Integer score;
    private String attribute;

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("acknowledgePrerequisites", acknowledgePrerequisites);
        json.put("feat", feat);
        if(trainedSkill != null){
            json.put("trainedSkill", trainedSkill);
        }
        if(requiredFeat != null){
            json.put("requiredFeat", requiredFeat);
        }
        if(score != null){
            json.put("score", score);
        }
        if(attribute != null){
            json.put("attribute", attribute);
        }

        return json;
    }

    public static BonusFeat createTrainedSkillFeat(String trainedSkill, String feat)
    {
        BonusFeat bonusFeat = new BonusFeat();
        bonusFeat.trainedSkill = trainedSkill;
        bonusFeat.feat = feat;
        bonusFeat.acknowledgePrerequisites = false;
        return bonusFeat;
    }

    public static BonusFeat createFeatDependentFeat(String requiredFeat, String feat)
    {
        BonusFeat bonusFeat = new BonusFeat();
        bonusFeat.requiredFeat = requiredFeat;
        bonusFeat.feat = feat;
        bonusFeat.acknowledgePrerequisites = false;
        return bonusFeat;
    }

    public static BonusFeat createFeat(String feat)
    {
        BonusFeat bonusFeat = new BonusFeat();
        bonusFeat.feat = feat;
        bonusFeat.acknowledgePrerequisites = false;
        return bonusFeat;
    }

    public static BonusFeat createPrerequisiteDependentFeat(String feat)
    {
        BonusFeat bonusFeat = new BonusFeat();
        bonusFeat.feat = feat;
        bonusFeat.acknowledgePrerequisites = true;
        return bonusFeat;
    }

    public static BonusFeat createAttributeDependentFeat(String attribute, int score, String feat)
    {
        BonusFeat bonusFeat = new BonusFeat();
        bonusFeat.feat = feat;
        bonusFeat.acknowledgePrerequisites = false;
        bonusFeat.attribute = attribute;
        bonusFeat.score = score;
        return bonusFeat;
    }
}
