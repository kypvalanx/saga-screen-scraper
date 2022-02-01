package swse.common;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import org.json.JSONObject;

public class Choice implements JSONy, Copyable<Choice>
{
    private boolean isFirstLevel = false;
    private final String description;
    private String noAvailableOptionsDescription;
    private final Map<String, Option> options = new HashMap();
    private String oneOption;

    public Choice(String description)
    {
        this.description = description;
        this.noAvailableOptionsDescription = "NO_AVAILABLE_OPTIONS";
        this.isFirstLevel = false;
    }

    public Choice(String description, String noAvailableOptionsDescription)
    {
        this.description = description;
        this.noAvailableOptionsDescription = noAvailableOptionsDescription;
        this.isFirstLevel = false;
    }

    @Nonnull
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        JSONObject optionJSON = new JSONObject();
        json.put("options", optionJSON);
        json.put("description", description);
        json.put("noOptions", noAvailableOptionsDescription);
        json.put("isFirstLevel", isFirstLevel);
        json.put("oneOption", oneOption);

        for (Map.Entry<String, Option> entry :
                options.entrySet())
        {
            optionJSON.put(entry.getKey(), entry.getValue().toJSON());
        }
        return json;
    }

    public Choice withOption(String key, Option option){
        options.put(key, option);
        return this;
    }
    public Choice withNoOptionsDescription(String noAvailableOptionsDescription){
        this.noAvailableOptionsDescription = noAvailableOptionsDescription;
        return this;
    }
    public Choice isFirstLevel(boolean isFirstLevel){
        this.isFirstLevel = isFirstLevel;
        return this;
    }


    public Choice withOneOption(String oneOption)
    {
        this.oneOption = oneOption;
        return this;
    }

    @Override
    public String toString()
    {
        return "Choice{" +
                "isFirstLevel=" + isFirstLevel +
                ", description='" + description + '\'' +
                ", noAvailableOptionsDescription='" + noAvailableOptionsDescription + '\'' +
                ", options=" + options +
                ", oneOption='" + oneOption + '\'' +
                '}';
    }

//    private final Map<String, Option> options = new HashMap();
//    private String oneOption;
    @Override
    public Choice copy() {
        Choice copy = new Choice(description, noAvailableOptionsDescription)
                .isFirstLevel(isFirstLevel).withOneOption(oneOption);
        for(Map.Entry<String, Option> entity: options.entrySet()){
            copy.withOption(entity.getKey(),entity.getValue().copy());
        }
        return copy;
    }
}
