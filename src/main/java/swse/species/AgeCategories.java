package swse.species;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.ItemType;
import swse.common.ProvidedItem;

public class AgeCategories
{
    private final Map<String, String> ages;

    public AgeCategories(Map<String, String> ages)
    {
        this.ages = ages;
    }

    public static Set<Object> getAgeCategories(Element content, boolean isDroid)
    {
        Set<Object> ageCategories = new HashSet<>();
        if(isDroid){
            return ageCategories;
        }
        Elements tables = content.select("table.wikitable");
        //Map<String,String> ages = new HashMap<>();
        for(Element table : tables){
            if(table.text().toLowerCase().contains("adult")){
                Elements headers = table.select("th");
                Elements values = table.select("td");

                for (int i = 0; i < headers.size(); i++)
                {
                    final String agePrerequisite = "AGE:" + values.get(i).text().toLowerCase().replaceAll("years", "").trim();
                    ageCategories.add(
                            ProvidedItem.create(capitalize(headers.get(i).text().trim()), ItemType.TRAIT,
                                            agePrerequisite));
                }
            }
        }
        return ageCategories;
    }

    private static String capitalize(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public JSONObject toJSON()
    {
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, String> age : ages.entrySet()){
            jsonObject.put(age.getKey(), age.getValue());
        }
        return jsonObject;
    }
}
