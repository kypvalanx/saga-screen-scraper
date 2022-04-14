package swse.common;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;

import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import static swse.common.FoundryItem.createAttributes;

public class Option implements JSONy, Copyable<Option> {
    List<ProvidedItem> providedItems = new ArrayList<>();
    List<Attribute> attributes = new ArrayList<>();
    private String payload;
    private String rollRange;


    @Nonnull
    public JSONObject toJSON() {
        return new JSONObject()
                .put("payload", payload)
                .put("providedItems", JSONy.toArray(providedItems))
                .put("attributes", createAttributes(attributes.stream().filter(Objects::nonNull).map(Attribute::toJSON)
                        .collect(Collectors
                                .toList())))
                .put("rollRange", rollRange);


    }

    public Option withProvidedItem(ProvidedItem item) {
        providedItems.add(item);
        return this;
    }

    public Option withPayload(String payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("providedItems", providedItems)
                .add("attributes", attributes)
                .add("payload", payload)
                .add("rollRange", rollRange)
                .toString();
    }

    @Override
    public Option copy() {
        final Option option = new Option()
                .withPayload(payload);
        for (ProvidedItem providedItem : providedItems) {
            option.withProvidedItem(providedItem.copy());
        }
        for (Attribute attribute : attributes) {
            option.withAttribute(attribute.copy());
        }
        return option;
    }

    public Option withAttribute(Attribute attribute) {
        attributes.add(attribute);
        return this;
    }

    public Option withRollRange(String rollRange) {
        this.rollRange = rollRange;
        return this;
    }
}
