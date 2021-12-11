package swse.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex
{
    private static Map<String, Pattern> patterns = new HashMap<>();

    public static Optional<Matcher> find(String pattern, String value)
    {
        Pattern p = getPattern(pattern);
        Matcher m = p.matcher(value);
        if(m.find()){
            return Optional.of(m);
        }
        return Optional.empty();
    }

    private static Pattern getPattern(String pattern)
    {
        return patterns.computeIfAbsent(pattern, Pattern::compile);
    }
}
