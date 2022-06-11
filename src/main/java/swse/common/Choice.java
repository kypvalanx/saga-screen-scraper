package swse.common;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONArray;
import org.json.JSONObject;

public class Choice implements JSONy, Copyable<Choice>
{
    private boolean isFirstLevel;
    private final String description;
    private String noAvailableOptionsDescription;
    private final List<Option> options = new LinkedList<>();
    private String oneOption;
    private String rollOption;
    private int availableSelections = 1;
    private Type type = Type.SELECT;
    private String payload = "#payload#";
    private boolean showSelectionInName = true;

    public static Choice create(String description){
        return new Choice(description);
    }
    public static Choice create(String description, String noAvailableOptionsDescription){
        return new Choice(description, noAvailableOptionsDescription);
    }


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
        JSONArray optionJSON = new JSONArray();
        json.put("options", optionJSON);
        json.put("description", description);
        json.put("noOptions", noAvailableOptionsDescription);
        json.put("isFirstLevel", isFirstLevel);
        json.put("oneOption", oneOption);
        json.put("rollOption", rollOption);
        json.put("availableSelections", availableSelections);
        json.put("showSelectionInName", showSelectionInName);
        json.put("type", type);
        if(payload != null){
            json.put("payload", payload);
        }

        for ( Option entry :
                options)
        {
            optionJSON.put(entry.toJSON());
        }
        return json;
    }



    public Choice withShowSelectionInName(boolean showSelectionInName){
        this.showSelectionInName = showSelectionInName;
        return this;
    }

    public Choice withOption(String key, Option option){
        option.withName(key);
        return this.withOption(option);
    }

    public Choice withOption(Option option){
        options.add(option);
        return this;
    }

    public Choice withNoOptionsDescription(String noAvailableOptionsDescription){
        this.noAvailableOptionsDescription = noAvailableOptionsDescription;
        return this;
    }

    public Choice withAvailableSelections(int availableSelections){
        this.availableSelections = availableSelections;
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
        return toJSON().toString();
    }

    @Override
    public Choice copy() {
        Choice copy = new Choice(description, noAvailableOptionsDescription)
                .isFirstLevel(isFirstLevel).withOneOption(oneOption);
        for( Option entity: options){
            copy.withOption(entity.copy());
        }
        return copy;
    }

    public Choice withRollOption(String rollOption) {
        this.rollOption = rollOption;
        return this;
    }

    public Choice withType(Type type) {
        this.type = type;
        return this;
    }

    /**
     * what payload to use for non-select choices.  select choices carry their payload name in their options
     * @param payload
     * @return
     */
    public Choice withPayload(String payload) {
        payload = "#"+payload+"#";
        payload = payload.replaceAll("##", "#");

        this.payload = payload;
        return this;
    }

    public enum Type {
        INTEGER, SELECT
    }
}
