package swse.common;

import javax.annotation.Nonnull;
import org.json.JSONObject;

public class NamedCrew implements JSONy{
    private final String name;
    private final String position;

    public NamedCrew(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public static NamedCrew create(String entry) {
        String position = "Pilot";
        String name = entry;

        if(entry.contains(" as ")){
            String[] toks = entry.split(" as ");
            name = toks[0].trim();
            position = toks[1].trim();
        }

        return new NamedCrew(name, position);
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        return new JSONObject().put("name", name).put("position", position);
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

}
