package swse.common;

import com.google.common.base.MoreObjects;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.commons.text.WordUtils;
import org.json.JSONObject;

import static swse.common.FoundryItem.createAttributes;

public class Option implements JSONy, Copyable<Option> {
    private String display;
    private final String value;
    List<ProvidedItem> providedItems = new ArrayList<>();
    List<Change> changes = new ArrayList<>();
    private final Map<String, String> payloads = new HashMap<>();
    private String rollRange;
    private boolean isDefault = false;

    public static Option create(){
        return new Option();
    }
    public static Option create(String display){
        return new Option(display);
    }
    public static Option create(String display, String value){
        return new Option(display, value);
    }

    public Option() {
        this.display = null;
        this.value = null;
    }

    public Option(String display) {
        this.display = display;
        this.value = display;
    }

    public Option(String display, String value) {
        this.display = display;
        this.value = value;
    }

    public static Option[] createEach(String options) {
        String[] split = options.split(", ");
        Option[] a = new Option[split.length];
        return Arrays.stream(split).map((s1) -> Option.create(WordUtils.capitalize(s1))).collect(Collectors.toList()).toArray(a);
    }

    public static Option createFreeTextOption() {
        return new Option("%%%FREETEXT%%%");
    }


    @Nonnull
    public JSONObject toJSON() {
        JSONObject payloadsObj = new JSONObject();

        for (Map.Entry<String, String> entry : payloads.entrySet()) {
            payloadsObj.put(entry.getKey(), entry.getValue());
        }


        final JSONObject obj = new JSONObject()
                .put("providedItems", JSONy.toArray(providedItems))
                .put("attributes", createAttributes(changes.stream().filter(Objects::nonNull).map(Change::toJSON)
                        .collect(Collectors
                                .toList())))
                .put("rollRange", rollRange)
                .put("payloads", payloadsObj);


        if (value != null) {
            obj.put("value", value);
        }
        if (display != null) {
            obj.put("display", display);
            obj.put("name", display);
        }

        if (isDefault) {
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

    public Option isDefault() {
        this.isDefault = true;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("providedItems", providedItems)
                .add("attributes", changes)
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
        for (Change change : changes) {
            option.withChange(change.copy());
        }
        return option;
    }

    private Option withPayloads(Map<String, String> payloads) {
        for (Map.Entry<String, String> entry : payloads.entrySet()) {
            this.payloads.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Option withChange(Change change) {
        changes.add(change);
        return this;
    }

    public Option withRollRange(String rollRange) {
        this.rollRange = rollRange;
        return this;
    }

    public Option withDisplay(String name) {
        this.display = name;
        return this;
    }
}
