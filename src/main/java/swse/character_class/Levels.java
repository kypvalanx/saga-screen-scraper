package swse.character_class;

import java.util.*;

import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.Change;
import swse.item.Effect;
import swse.item.FoundryEffect;

import static swse.util.Util.getNumber;

class Levels {
    private final Map<Integer, Level> leveledStats;

    public Levels(Map<Integer, Level> leveledStats) {
        this.leveledStats = leveledStats;
    }

    public static Levels getLeveledStats(Elements entries, String className) {
        Map<Integer, Level> leveledStats = new HashMap<>();
        for (Element entry : entries) {
            leveledStats.putAll(parseLevelTable(entry, className));
        }
        return new Levels(leveledStats);
    }

    @NonNull
    private static Map<Integer, Level> parseLevelTable(Element table, String className) {
        Elements headers = table.select("th");
        HashMap<Integer, Level> featuresByLevel = new HashMap<>();
        if (headers.size() == 0) {
            return featuresByLevel;
        }

        for (Element row :
                table.select("tr")) {
            Elements rowEntries = row.select("td");
            if (rowEntries.size() == 0) {
                continue;
            }
            Integer level = 0;
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i).text();
                if (header.isEmpty()) {
                    continue;
                }
                String text = rowEntries.get(i).text();

                switch (header) {
                    case "LEVEL":
                    case "CLASS LEVEL":
                        level = getNumber(text);
                        if (level == null) {
                            System.err.println("WAT");
                            continue;
                        }
                        featuresByLevel.put(level, new Level(level));
                        continue;
                    case "BASE ATTACK BONUS":
                        featuresByLevel.get(level).withBAB(getNumber(text));
                        continue;
                    case "CLASS FEATURES":
                        featuresByLevel.get(level).withChanges(parseFeatures(text, className));
                        continue;
                    default:
                        System.err.println("THING: " + header);
                }

            }

        }
        for (Level level : featuresByLevel.values()) {
            if (level.getLevel() > 1) {
                level.withPreviousLevel(featuresByLevel.get(level.getLevel() - 1));
            }
        }
        return featuresByLevel;
    }

    private static List<Change> parseFeatures(String text, String className) {
        List<Change> features = new ArrayList<>();
        for (String tok : text.split(", ")) {
            features.addAll(Feature.parseFeature(tok, className));
        }
        return features;
    }

    public JSONObject toJSON() {
        JSONObject leveledMapJson = new JSONObject();

        return leveledMapJson;
    }

    public Collection<? extends FoundryEffect<?>> getEffects() {
        List<FoundryEffect<?>> effects = Lists.newArrayList();
        for (Map.Entry<Integer, Level> leveledMap : leveledStats.entrySet()) {
            effects.add(leveledMap.getValue());
        }
        return effects;
    }
}
