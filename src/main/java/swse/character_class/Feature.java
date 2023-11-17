package swse.character_class;

import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Change;
import swse.common.ChangeKey;
import swse.common.JSONy;
import swse.util.Util;

public class Feature implements JSONy
{
    private final String payload;
    private final String key;
    private final int amount;
    private String modifier;

    public Feature(String key, String payload, int amount)
    {
        this.key = key;
        this.payload = payload;
        this.amount = amount;
    }

    public static List<Change> parseFeature(String tok, String className)
    {
        if (List.of("Defense Bonuses", "Starting Feats", "Lightsaber Construction").contains(tok))
        {
            return List.of();
        }
        if ("Talent".equals(tok))
        {
            return List.of(Feature.create("PROVIDES", className + " Talent Trees"));
        }
        if ("Force Talent".equals(tok))
        {
            return List.of(Feature.create("PROVIDES", "Force Talent Trees"));
        }
        if ("Force Secret".equals(tok))
        {
            return List.of(Feature.create("PROVIDES", "Force Secret"));
        }
        if ("Force Technique".equals(tok))
        {
            return List.of(Feature.create("PROVIDES", "Force Technique"));
        }
        if (tok.startsWith("Bonus Feat "))
        {
            return List.of(Feature.create("PROVIDES", className + " Bonus Feats"));
        }
        if (tok.startsWith("Vehicle Dodge "))
        {
            return List.of(Feature.create("BONUS", "Vehicle Dodge"), Feature.create("TRAIT", "Vehicle Dodge"));
        }
        if (tok.startsWith("Familiar Foe "))
        {
            return List.of(Feature.create("BONUS", "Familiar Foe"), Feature.create("TRAIT", "Familiar Foe"));
        }
        if (tok.startsWith("Command Cover "))
        {
            return List.of(Feature.create("BONUS", "Command Cover"), Feature.create("TRAIT", "Command Cover"));
        }
        if (tok.startsWith("Damage Reduction "))
        {
            return List.of(Feature.create("BONUS", "Damage Reduction"), Feature.create("TRAIT", "Damage Reduction"));
        }
        if (tok.startsWith("Trusty Sidearm "))
        {
            return List.of(Feature.create("BONUS", "Trusty Sidearm"), Feature.create("TRAIT", "Trusty Sidearm"));
        }
        if (tok.startsWith("Executive Leadership "))
        {
            return List.of(Feature.create("BONUS", "Executive Leadership").withModifier("/Encounter"), Feature.create("TRAIT", "Executive Leadership"));
        }
        if (tok.startsWith("Unflinching "))
        {
            return List.of(Feature.create("BONUS", "Unflinching").withModifier("/Encounter"), Feature.create("TRAIT", "Unflinching"));
        }
        if (tok.startsWith("Master of Movement "))
        {
            return List.of(Feature.create("BONUS", "Master of Movement").withModifier("/Encounter"), Feature.create("TRAIT", "Master of Movement"));
        }
        if (tok.startsWith("Targeted Suspect "))
        {
            return List.of(Feature.create("BONUS", "Targeted Suspect"), Feature.create("TRAIT", "Targeted Suspect"));
        }
        if (tok.startsWith("Resources "))
        {
            return List.of(Feature.create("BONUS", "Master of Movement", 2000), Feature.create("TRAIT", "Master of Movement"));
        }
        if (tok.startsWith("Independent Spirit "))
        {
            return List.of(Feature.create("BONUS", "Independent Spirit"), Feature.create("TRAIT", "Independent Spirit"));
        }
        if (tok.startsWith("Veteran Privateer "))
        {
            return List.of(Feature.create("BONUS", "Veteran Privateer").withModifier("/Encounter"), Feature.create("TRAIT", "Veteran Privateer"));
        }
        if (tok.startsWith("Mark "))
        {
            return List.of(Feature.create("BONUS", "Mark"), Feature.create("TRAIT", "Mark"));
        }
        if (tok.startsWith("Swindle"))
        {
            if ("Swindle".equals(tok))
            {
                return List.of(Feature.create("BONUS", "Swindle", 0), Feature.create("TRAIT", "Swindle"));
            }
            if ("Swindle +5".equals(tok))
            {
                return List.of(Feature.create("BONUS", "Swindle", 3));
            }
            return List.of(Feature.create("BONUS", "Swindle"));
        }
        if (tok.startsWith("Fugitive "))
        {
            return List.of(Feature.create("BONUS", "Fugitive"), Feature.create("TRAIT", "Fugitive"));
        }
        if (tok.startsWith("Field-Created Weapon "))
        {
            return List.of(Feature.create("BONUS", "Field-Created Weapon"), Feature.create("TRAIT", "Field-Created Weapon"));
        }
        if (tok.startsWith("Networked Mind "))
        {
            return List.of(Feature.create("BONUS", "Networked Mind").withModifier("Droid Allies"), Feature.create("TRAIT", "Networked Mind"));
        }
        if (tok.startsWith("Surprise Attack "))
        {
            return List.of(Feature.create("BONUS", "Surprise Attack"), Feature.create("TRAIT", "Surprise Attack"));
        }
        if (tok.startsWith("Shaper Lore "))
        {
            return List.of(Feature.create("BONUS", "Shaper Lore"), Feature.create("TRAIT", "Shaper Lore"));
        }
        if (tok.startsWith("Create Cover "))
        {
            return List.of(Feature.create("BONUS", "Create Cover").withModifier("Squares"), Feature.create("TRAIT", "Create Cover"));
        }
        if (tok.startsWith("Tough as Durasteel "))
        {
            return List.of(Feature.create("BONUS", "Tough as Durasteel", 2), Feature.create("TRAIT", "Tough as Durasteel"));
        }
        if (tok.startsWith("Contraband "))
        {
            if ("Contraband (2,000 credits)".equals(tok))
        {
            return List.of(Feature.create("BONUS", "Contraband", 2000).withModifier("credits"), Feature.create("TRAIT", "Contraband"));
        }
            return List.of(Feature.create("BONUS", "Contraband", 1000).withModifier("credits"));
        }
        if (tok.startsWith("Lead Infiltrator"))
        {
            return List.of(Feature.create("BONUS", "Lead Infiltrator"), Feature.create("TRAIT", "Lead Infiltrator"));
        }
        if (tok.startsWith("Unarmed Stun "))
        {
            return List.of(Feature.create("BONUS", "Unarmed Stun"), Feature.create("TRAIT", "Unarmed Stun"));
        }
        if ("Fearless".equals(tok))
        {
            return List.of(Feature.create("TRAIT", "Fearless ("+className+")"));
        }


        return List.of(Feature.create("TRAIT", tok));
    }

    private static Change create(String key, String payload, int amount)
    {
        switch (key){
            case "PROVIDES":
                //printUnique(payload);
                return Change.create(ChangeKey.PROVIDES, payload);

            case "TRAIT":
                return Change.create(ChangeKey.PROVIDED_TRAIT, payload);

            case "BONUS":
//                try {
                    return Change.create(ChangeKey.valueOf(Util.toEnumCase(payload)), amount);
//                } catch (IllegalArgumentException e){
//                    printUnique(toEnumCase(payload) + "(\""+ toCamelCase(payload)+"\"),");
//                    return Attribute.create(AttributeKey.PROVIDED_TRAIT, payload);
//                }
        }
        throw new IllegalArgumentException("keys have to be PROVIDES, TRAIT, or BONUS.  it's currently:"+key);
    }

    private static Change create(String key, String payload)
    {

        return create(key, payload, 1);
    }

    @Nonnull
    @Override
    public JSONObject toJSON()
    {
        final JSONObject put = new JSONObject().put("key", key).put("value", payload).put("amount", amount);
        if(modifier != null){
            put.put("modifier", modifier);
        }
        return put;
    }
}
