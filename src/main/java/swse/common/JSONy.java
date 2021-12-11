package swse.common;

import java.util.Collection;
import javax.annotation.Nonnull;
import org.json.JSONArray;
import org.json.JSONObject;

public interface JSONy
{
    static JSONArray toArray(Collection<? extends JSONy> jsonies)
    {
        JSONArray array = new JSONArray();
        if(jsonies != null){
            for(JSONy jsony:jsonies){
                array.put(jsony.toJSON());
            }
        }
        if(array.toList().size() == 0){
            return null;
        }
        return array;
    }

    static JSONObject toJSON(JSONy jsony) {
        if(jsony == null){
            return null;
        }
        return jsony.toJSON();
    }

    @Nonnull
    JSONObject toJSON();
}
