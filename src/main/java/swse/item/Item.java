package swse.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Attribute;
import swse.common.FoundryItem;

class Item extends FoundryItem<Item> {
    private String type;
    private String subtype;
    private String size;
    private String cost;
    private String weight;
    private String availability;
    private String baseItem;
    private String source;
    private String bonusToReflexDefense;
    private String bonusToFortitudeDefense;
    private String maximumDexterityBonus;
    private String splash;
    private String heirloomBonus;
    private String seeAlso;
    private String baseSpeed;
    private String requires;
    private String trigger;
    private String recurrence;
    private String rejectionAttackBonus;
    private String installationCost;
    private String upgradePointCost;
    private String challengeLevel;
    private List<String> skillChecks;
    private List<String> special;
    private List<String> keywords;
    private Boolean isThrowable;
    private Boolean isReach;
    private List<Mode> modes;
    private String damageDie;
    private String stunDamageDie;
    private String damageType;
    private Integer unarmedDamage;
    private String unarmedModifier;
    private String prefix;
    private String suffix;

    public static Item create(String itemName) {
        return new Item(itemName);
    }

    public Item(String name) {
        super(name);
    }

    @Nonnull
    public JSONObject toJSON() {

        JSONObject json = super.toJSON();
        JSONObject data = json.getJSONObject("data");
        json.put("type",  type.toLowerCase());

        List<JSONObject> attributeObject = new ArrayList<>();
        attributeObject.add(createAttribute("baseItem", baseItem));
        attributeObject.add(createAttribute("isThrowable", isThrowable));
                attributeObject.add(createAttribute("isReach", isReach));
                attributeObject.add(createAttribute("reflexDefenseBonus", bonusToReflexDefense));
                attributeObject.add(createAttribute("fortitudeDefenseBonus", bonusToFortitudeDefense));
                attributeObject.add(createAttribute("maximumDexterityBonus", maximumDexterityBonus));
                attributeObject.add(createAttribute("rejectionAttackBonus", rejectionAttackBonus));
                attributeObject.add(createAttribute("splash", splash));
                attributeObject.add(createAttribute("heirloomBonus", heirloomBonus));
                attributeObject.add(createAttribute("seeAlso", seeAlso));
                attributeObject.add(createAttribute("baseSpeed", baseSpeed));
                attributeObject.add(createAttribute("requires", requires));
                attributeObject.add(createAttribute("trigger", trigger));
                attributeObject.add(createAttribute("recurrence", recurrence));
                attributeObject.add(createAttribute("installationCost", installationCost));
                attributeObject.add(createAttribute("upgradePointCost",upgradePointCost));
                attributeObject.add(createAttribute("challengeLevel", challengeLevel));
                attributeObject.add(createAttribute("skillChecks", skillChecks));
                attributeObject.add(createAttribute("special", special));
                attributeObject.add(createAttribute("keywords", keywords));
//                attributes.add(createAttribute("modes", modes));
                attributeObject.add(createAttribute("damageDie", damageDie));
               // attributeObject.add(createAttribute("stunDamageDie", stunDamageDie));
        modes.add(Mode.create("Stun", List.of(Attribute.create("stunDamageDie", stunDamageDie))));
                attributeObject.add(createAttribute("damageType", damageType));
                attributeObject.add(createAttribute("unarmedDamage", unarmedDamage));
                attributeObject.add(createAttribute("unarmedModifier", unarmedModifier));
                attributeObject.add(createAttribute("prefix", prefix));
                attributeObject.add(createAttribute("suffix", suffix));

                for(Attribute attribute : attributes){
                    attributeObject.add(createAttribute(attribute.getKey(), attribute.getValue()));
                }

        data
                .put("subtype", subtype)
                .put("size", size)
                .put("cost", cost)
                .put("weight", weight)
                .put("availability", availability)
                .put("attributes", createAttributes(attributeObject.stream().filter(Objects::nonNull).collect(Collectors.toList())))
                .put("modes", constructModes(modes))
                .put("source", source);
        return json;
    }


    public Item withType(String type) {
        this.type = type;
        return this;
    }

    public Item withSubtype(String subtype) {
        this.subtype = subtype;
        return this;
    }

    public Item withCost(String cost) {
                this.cost = cost;
        return this;
    }

    public Item withSize(String size)  {
        this.size = size;
        return this;
    }

    public Item withWeight(String weight)  {
        this.weight = weight;
        return this;
    }

    public Item withSource(String source)  {
        this.source = source;
        return this;
    }

    public Item withAvailability(String availability)  {
        this.availability = availability;
        return this;
    }

    public Item withBaseItem(String baseItem)  {
        this.baseItem = baseItem;
        return this;
    }

    public Item withBonusToReflexDefense(String bonusToReflexDefense)  {
        this.bonusToReflexDefense = bonusToReflexDefense;
        return this;
    }

    public Item withBonusToFortitudeDefense(String bonusToFortitudeDefense)  {
        this.bonusToFortitudeDefense = bonusToFortitudeDefense;
        return this;
    }

    public Item withMaxDexterityBonus(String maximumDexterityBonus)  {
        this.maximumDexterityBonus = maximumDexterityBonus;
        return this;
    }

    public Item withSplash(String splash)  {
        this.splash = splash;
        return this;
    }

    public Item withHeirloomBonus(String heirloomBonus)  {
        this.heirloomBonus = heirloomBonus;
        return this;
    }

    public Item withSeeAlso(String seeAlso)   {
        this.seeAlso = seeAlso;
        return this;
    }

    public Item withBaseSpeed(String baseSpeed)   {
        this.baseSpeed = baseSpeed;
        return this;
    }

    public Item withRequires(String requires)   {
        this.requires = requires;
        return this;
    }

    public Item withTrigger(String trigger)   {
        this.trigger = trigger;
        return this;
    }

    public Item withRecurrence(String recurrence)   {
        this.recurrence = recurrence;
        return this;
    }

    public Item withSkillChecks(List<String> skillChecks)    {
        this.skillChecks = new ArrayList<>();
        for(String s : skillChecks){
            //printUnique(s);
        }


        return this;
    }

    public Item withRejectionAttackBonus(String rejectionAttackBonus)     {
        this.rejectionAttackBonus = rejectionAttackBonus;
        return this;
    }

    public Item withInstallationCost(String installationCost)     {
        this.installationCost = installationCost;
        return this;
    }

    public Item withUpgradePointCost(String upgradePointCost)     {
        this.upgradePointCost = upgradePointCost;
        return this;
    }

    public Item withChallengeLevel(String challengeLevel)   {
        this.challengeLevel = challengeLevel;
        return this;
    }

    public Item withSpecial(List<String> specials)   {
        if(specials != null){
        for (String special : specials){
            if(special.toLowerCase().contains("can be thrown")){
                this.isThrowable = true;
            }else if(special.toLowerCase().contains("is a reach weapon")){
                this.isReach = true;
            }else {
                //printUnique(special);
            }
        }
        }
        this.special = specials;
        return this;
    }

    public Item withKeywords(List<String> keywords)   {
        this.keywords = keywords;
        return this;
    }

    public Item withModes(List<Mode> modes) {
        this.modes = modes;
        return this;
    }

    public Item withDamageDie(String damageDie) {
        this.damageDie = damageDie;
        return this;
    }

    public Item withStunDamageDie(String stunDamageDie)  {
        this.stunDamageDie = stunDamageDie;
        return this;
    }

    public Item withDamageType(String damageType) {
        this.damageType = damageType;
        return this;
    }

    public Item withUnarmedDamage(Integer unarmedDamage) {
        this.unarmedDamage = unarmedDamage;
        return this;
    }

    public Item withUnarmedModifier(String unarmedModifier) {
        this.unarmedModifier = unarmedModifier;
        return this;
    }

    public Item withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public Item withSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

}
