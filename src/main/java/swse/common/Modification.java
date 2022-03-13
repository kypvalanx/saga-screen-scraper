package swse.common;

import javax.annotation.Nonnull;
import org.json.JSONObject;

public class Modification implements JSONy, Copyable<ProvidedItem>{
    private final ProvidedItem providedItem;

    public Modification(ProvidedItem providedItem) {
        this.providedItem = providedItem;
    }

    public static Modification create(ProvidedItem providedItem) {
        return new Modification(providedItem);
    }

    @Override
    public ProvidedItem copy() {
        return null;
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        return providedItem.toJSON();
    }
}
