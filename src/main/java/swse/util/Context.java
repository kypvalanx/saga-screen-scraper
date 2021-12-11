package swse.util;

import java.util.HashMap;
import java.util.Map;

public class Context
{
    private static Map<String, String> map = new HashMap<>();
    public static void setValue(String key, String value)
    {
        map.put(key, value);
    }

    public static String getValue(String key){
        return map.get(key);
    }
}
