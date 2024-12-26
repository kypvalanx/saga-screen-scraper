package swse.species;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import org.json.JSONObject;
import swse.common.Copyable;
import swse.common.FoundryItem;
import swse.common.JSONy;
import swse.common.ProvidedItem;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Species extends FoundryItem<Species> implements Copyable<Species>
{
    public Species(String name)
    {
        super(name, "species");
    }

    public static Species create(String itemName)
    {
        return new Species(itemName);
    }

    @Override
    public Species copy() {
        return null;
    }

    public List<ProvidedItem> providedItemPostFilter(List<ProvidedItem> providedItems) {
        boolean hasVariableSize = providedItems.stream().anyMatch(item -> item.getName().equalsIgnoreCase("variable size"));

        List<ProvidedItem> filteredProvidedItems = Lists.newArrayList();
        for (ProvidedItem providedItem : providedItems) {
            if(providedItem.getName().equalsIgnoreCase(this.name+"s")) continue;
            if(hasVariableSize && List.of("fine", "diminutive", "tiny", "small", "medium", "large", "huge", "gargantuan", "colossal").contains(providedItem.getName().toLowerCase())) continue;

            filteredProvidedItems.add(providedItem);
        }
        return filteredProvidedItems;
    }
}
