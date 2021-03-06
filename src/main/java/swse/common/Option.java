package swse.common;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import static swse.common.FoundryItem.createAttributes;

public class Option implements JSONy, Copyable<Option> {
    private String name;
    List<ProvidedItem> providedItems = new ArrayList<>();
    List<Attribute> attributes = new ArrayList<>();
    private final Map<String, String> payloads = new HashMap<>();
    private String rollRange;
    private boolean isDefault = false;

    public Option(){
        this.name = null;
    }

    public Option(String name){
        this.name = name;
    }


    @Nonnull
    public JSONObject toJSON() {
        JSONObject payloadsObj = new JSONObject();

        for (Map.Entry<String, String> entry : payloads.entrySet()) {
            payloadsObj.put(entry.getKey(), entry.getValue());
        }


        final JSONObject obj = new JSONObject()
                .put("providedItems", JSONy.toArray(providedItems))
                .put("attributes", createAttributes(attributes.stream().filter(Objects::nonNull).map(Attribute::toJSON)
                        .collect(Collectors
                                .toList())))
                .put("rollRange", rollRange)
                .put("payloads", payloadsObj);

        if(name != null){
            obj.put("name", name);
        }

        if(isDefault){
            obj.put("isDefault", true);
        }

        return obj;


    }

    public Option withProvidedItem(ProvidedItem item) {
        providedItems.add(item);
        return this;
    }

    public Option withPayload(String payload) {
        this.payloads.put("payload", payload);
        return this;
    }

    public Option withPayload(String key, String payload) {
        this.payloads.put(key, payload);
        return this;
    }

    public Option isDefault(){
        this.isDefault = true;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("providedItems", providedItems)
                .add("attributes", attributes)
                .add("payload", payloads)
                .add("rollRange", rollRange)
                .toString();
    }

    @Override
    public Option copy() {
        final Option option = new Option()
                .withPayloads(payloads);
        for (ProvidedItem providedItem : providedItems) {
            option.withProvidedItem(providedItem.copy());
        }
        for (Attribute attribute : attributes) {
            option.withAttribute(attribute.copy());
        }
        return option;
    }

    private Option withPayloads(Map<String, String> payloads) {
        for (Map.Entry<String, String> entry : payloads.entrySet()) {
            this.payloads.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Option withAttribute(Attribute attribute) {
        attributes.add(attribute);
        return this;
    }

    public Option withRollRange(String rollRange) {
        this.rollRange = rollRange;
        return this;
    }

    public Option withName(String name) {
        this.name = name;
        return this;
    }
}
