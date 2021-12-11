package swse.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import org.json.JSONObject;

public class Option implements JSONy
{
    List<ProvidedItem> providedItems = new ArrayList<>();
    private String payload;


    @Nonnull
    public JSONObject toJSON()
    {
        return new JSONObject()
                .put("providedItems", providedItems)
                .put("payload", payload);
    }

    public Option withProvidedItem(ProvidedItem item){
        providedItems.add(item);
        return this;
    }

    public Option withPayload(String payload)
    {
        this.payload = payload;
        return this;
    }

    @Override
    public String toString()
    {
        return "Option{" +
                "providedItems=" + providedItems +
                ", payload='" + payload + '\'' +
                '}';
    }
}
