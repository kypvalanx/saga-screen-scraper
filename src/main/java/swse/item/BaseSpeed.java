package swse.item;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

class BaseSpeed
{

    public static final Pattern BASESPEED_2 = Pattern.compile("(\\d*) squares");
    private final HashMap<String, String> speeds;
    private static final Pattern BASESPEED_1 = Pattern.compile("(\\w*)(?: or bigger)?, (\\d*) squares");

    public BaseSpeed(String s)
    {
        speeds = new HashMap<>();
        if(s.contains(";")){
            String[] toks = s.split(";");
            for(String tok : toks){
                Matcher m = BASESPEED_1.matcher(tok);
                if(m.find()){
                    speeds.put(m.group(1).toLowerCase(), m.group(2));
                }
            }
        } else {
            Matcher m = BASESPEED_2.matcher(s);
            if(m.find()){
                speeds.put("all", m.group(1));
            } else {
                speeds.put("all", "0");
            }
        }
    }

    public JSONObject toJSON()
    {
        if(speeds == null){
            return null;
        }
        JSONObject json = new JSONObject();

        for(Map.Entry<String, String> entity: speeds.entrySet()){
            json.put(entity.getKey(), entity.getValue());
        }

        return json;
    }
}
