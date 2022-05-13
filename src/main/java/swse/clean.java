package swse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class clean
{
    public static void main(String[] args)
    {
        String s = "Method\n" +
                "    create(AttributeKey, Object)\n" +
                "Found usages  (554 usages found)\n" +
                "    CharacterClassExporter.java  (3 usages found)\n" +
                "        129 items.add(Attribute.create(AttributeKey.INTELLIGENCE_MAX, 2));\n" +
                "        137 classTypes.add(Attribute.create(AttributeKey.IS_HEROIC, !List.of(\"Beast\", \"Nonheroic\").contains(itemName)));\n" +
                "        138 classTypes.add(Attribute.create(AttributeKey.IS_PRESTIGE, !List.of(\"Beast\", \"Nonheroic\", \"Jedi\", \"Noble\", \"Scoundrel\", \"Scout\", \"Soldier\", \"Technician\", \"Force Prodigy\").contains(itemName)));\n" +
                "    ClassSkill.java  (2 usages found)\n" +
                "        48 attributes.add(Attribute.create(\"trainedSkillsFirstLevel\", Integer.parseInt(m.group(1))));\n" +
                "        56 .map(skill -> Attribute.create(\"classSkill\", skill))\n" +
                "    DefenceBonuses.java  (3 usages found)\n" +
                "        36 attributes.add(Attribute.create(\"classFortitudeDefenseBonus\", Integer.parseInt(fortMatcher.group(1))));\n" +
                "        42 attributes.add(Attribute.create(\"classReflexDefenseBonus\", Integer.parseInt(reflexMatcher.group(1))));\n" +
                "        48 attributes.add(Attribute.create(\"classWillDefenseBonus\", Integer.parseInt(willMatcher.group(1))));\n" +
                "    FeatExporter.java  (34 usages found)\n" +
                "        196 provided.add(Attribute.create(\"takeMultipleTimes\", \"true\"));\n" +
                "        226 attributes.add(Attribute.create(\"damageThresholdBonus\", 5));\n" +
                "        229 attributes.add(Attribute.create(\"hitPointEq\", \"@charLevel\"));\n" +
                "        233 attributes.add(Attribute.create(\"weaponProficiency\", \"#payload#\"));\n" +
                "        236 attributes.add(Attribute.create(\"armorProficiency\", \"#payload#\"));\n" +
                "        239 attributes.add(Attribute.create(\"weaponFocus\", \"#payload#\"));\n" +
                "        242 attributes.add(Attribute.create(\"skillFocus\", \"#payload#\"));\n" +
                "        245 attributes.add(Attribute.create(\"skillMastery\", \"#payload#\"));\n" +
                "        248 attributes.add(Attribute.create(\"doubleAttack\", \"#payload#\"));\n" +
                "        251 attributes.add(Attribute.create(\"tripleAttack\", \"#payload#\"));\n" +
                "        254 attributes.add(Attribute.create(\"savageAttack\", \"#payload#\"));\n" +
                "        256 attributes.add(Attribute.create(\"relentlessAttack\", \"#payload#\"));\n" +
                "        259 attributes.add(Attribute.create(\"autofireSweep\", \"#payload#\"));\n" +
                "        262 attributes.add(Attribute.create(\"autofireAssault\", \"#payload#\"));\n" +
                "        265 attributes.add(Attribute.create(\"halt\", \"#payload#\"));\n" +
                "        268 attributes.add(Attribute.create(\"returnFire\", \"#payload#\"));\n" +
                "        271 attributes.add(Attribute.create(\"criticalStrike\", \"#payload#\"));\n" +
                "        274 attributes.add(Attribute.create(\"forceSensitivity\", \"true\"));\n" +
                "        275 attributes.add(Attribute.create(\"bonusTalentTree\", \"Force Talent Tree\"));\n" +
                "        278 attributes.add(Attribute.create(\"finesseStat\", \"DEX\"));\n" +
                "        281 attributes.add(Attribute.create(\"forceTraining\", \"true\"));\n" +
                "        282 attributes.add(Attribute.create(\"provides\", \"Force Powers:MAX(1 + @WISMOD,1)\"));\n" +
                "        285 attributes.add(Attribute.create(\"dualWeaponModifier\", \"-5\"));\n" +
                "        288 attributes.add(Attribute.create(\"dualWeaponModifier\", \"-2\"));\n" +
                "        291 attributes.add(Attribute.create(\"dualWeaponModifier\", \"0\"));\n" +
                "        294 attributes.add(Attribute.create(\"trainedSkills\", \"1\"));\n" +
                "        297 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", \"1\"));\n" +
                "        298 attributes.add(Attribute.create(\"willDefenseBonus\", \"1\"));\n" +
                "        299 attributes.add(Attribute.create(\"reflexDefenseBonus\", \"1\"));\n" +
                "        302 attributes.add(Attribute.create(\"armorProficiency\", \"light\"));\n" +
                "        305 attributes.add(Attribute.create(\"armorProficiency\", \"medium\"));\n" +
                "        308 attributes.add(Attribute.create(\"armorProficiency\", \"heavy\"));\n" +
                "        313 attributes.add(Attribute.create(\"bonusUnarmedDamageDieSize\", \"1\"));\n" +
                "        314 attributes.add(Attribute.create(\"bonusDodgeReflexDefense\", \"1\"));\n" +
                "    Feature.java  (3 usages found)\n" +
                "        170 return Attribute.create(\"provides\", payload);\n" +
                "        173 return Attribute.create(\"providedTrait\", payload);\n" +
                "        176 return Attribute.create(toCamelCase(payload), amount);\n" +
                "    ForcePoints.java  (1 usage found)\n" +
                "        40 attributes.add(Attribute.create(\"classForcePoints\", Integer.parseInt(m.group(1))));\n" +
                "    ForcePowersExporter.java  (1 usage found)\n" +
                "        106 withProvided(Attribute.create(\"takeMultipleTimes\", \"true\")));\n" +
                "    HitPoints.java  (6 usages found)\n" +
                "        44 attributes.add(Attribute.create(\"firstLevelHitPoints\",Integer.parseInt(m.group(1))));\n" +
                "        53 attributes.add(Attribute.create(\"levelUpHitPoints\", m2.group(1)));\n" +
                "        64 attributes.add(Attribute.create(\"firstLevelHitPoints\", \"1d8\"));\n" +
                "        65 attributes.add(Attribute.create(\"levelUpHitPoints\", \"1d8\"));\n" +
                "        69 attributes.add(Attribute.create(\"firstLevelHitPoints\", \"1d4\"));\n" +
                "        70 attributes.add(Attribute.create(\"levelUpHitPoints\", \"1d4\"));\n" +
                "    Item.java  (21 usages found)\n" +
                "        65 this.withProvided(Attribute.create(\"splash\", splash));\n" +
                "        70 this.withProvided(Attribute.create(\"heirloomBonus\", heirloomBonus));\n" +
                "        75 this.withProvided(Attribute.create(\"seeAlso\", seeAlso));\n" +
                "        80 this.withProvided(Attribute.create(\"baseSpeed\", baseSpeed));\n" +
                "        85 this.withProvided(Attribute.create(\"requires\", requires));\n" +
                "        90 this.withProvided(Attribute.create(\"trigger\", trigger));\n" +
                "        95 this.withProvided(Attribute.create(\"recurrence\", recurrence));\n" +
                "        101 attributes.add(Attribute.create(\"skillCheck\", s));\n" +
                "        109 this.withProvided(Attribute.create(\"rejectionAttackBonus\", rejectionAttackBonus));\n" +
                "        114 this.withProvided(Attribute.create(\"installationCost\", installationCost));\n" +
                "        119 this.withProvided(Attribute.create(\"upgradePointCost\", upgradePointCost));\n" +
                "        124 this.withProvided(Attribute.create(\"challengeLevel\", challengeLevel));\n" +
                "        132 this.withProvided(Attribute.create(\"isThrowable\", true));\n" +
                "        134 this.withProvided(Attribute.create(\"isReach\", true));\n" +
                "        136 this.withProvided(Attribute.create(\"special\", special));\n" +
                "        144 this.withProvided(Attribute.create(\"keywords\", keywords));\n" +
                "        162 this.withProvided(Attribute.create(\"damageDie\", damageDie));\n" +
                "        168 modes.add(Mode.create(\"Stun\", List.of(Attribute.create(\"stunDamageDie\", stunDamageDie))));\n" +
                "        174 this.withProvided(Attribute.create(\"damageType\", damageType));\n" +
                "        179 this.withProvided(Attribute.create(\"unarmedDamage\", unarmedDamage));\n" +
                "        184 this.withProvided(Attribute.create(\"unarmedModifier\", unarmedModifier));\n" +
                "    ItemExporter.java  (166 usages found)\n" +
                "        42 public static final Mode MODE_AUTOFIRE = Mode.create(\"Autofire\", \"ROF\", List.of(Attribute.create(TO_HIT_MODIFIER, \"-5\")));\n" +
                "        555 .withProvided(Attribute.create(\"baseItem\", baseItem))\n" +
                "        556 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", bonusToReflexDefense))\n" +
                "        557 .withProvided(Attribute.create(\"equipmentFortitudeDefenseBonus\", bonusToFortitudeDefense))\n" +
                "        558 .withProvided(Attribute.create(\"maximumDexterityBonus\", maximumDexterityBonus))\n" +
                "        575 .withProvided(Attribute.create(\"prefix\", getPrefix(itemName)))\n" +
                "        576 .withProvided(Attribute.create(\"suffix\", getSuffix(itemName)));\n" +
                "        579 item.withProvided(Attribute.create(\"droidPart\", true));\n" +
                "        582 item.withProvided(Attribute.create(\"armorType\", armorType));\n" +
                "        585 item.withProvided(Attribute.create(\"appendages\", \"1\"));\n" +
                "        589 item.withProvided(Attribute.create(\"perceptionModifier\", 2));\n" +
                "        590 item.withProvided(Attribute.create(\"lowLightVision\", true));\n" +
                "        619 variant.withProvided(Attribute.create(\"immunity\", \"Extreme Cold\"));\n" +
                "        623 variant.withProvided(Attribute.create(\"immunity\", \"Extreme Heat\"));\n" +
                "        659 attributes.add(Attribute.create(TREATED_AS_ATTRIBUTE_KEY, standardizeTypes(m.group(1))));\n" +
                "        671 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, group + \":\" + m.group(1)));\n" +
                "        686 attributes.add(Mode.create(\"Self-Built\", List.of(Attribute.create(\"toHitModifier\", 1))));\n" +
                "        690 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:50\"));\n" +
                "        693 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:5\"));\n" +
                "        696 attributes.add(Mode.create(\"Blaster\", \"POWER\", List.of(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:50\"))));\n" +
                "        697 attributes.add(Mode.create(\"Harpoon\", \"POWER\", List.of(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Harpoon:1\"))));\n" +
                "        700 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:25\"));\n" +
                "        701 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Gas Canister:200\"));\n" +
                "        704 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:25\"));\n" +
                "        707 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:500\"));\n" +
                "        708 attributes.add(Mode.create(\"3d4\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d4\"))));\n" +
                "        709 attributes.add(Mode.create(\"3d6\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d6\"), Attribute.create(AMMO_USE_MULTIPLIER, \"5\"))));\n" +
                "        709 attributes.add(Mode.create(\"3d6\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d6\"), Attribute.create(AMMO_USE_MULTIPLIER, \"5\"))));\n" +
                "        710 attributes.add(Mode.create(\"3d8\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d8\"), Attribute.create(AMMO_USE_MULTIPLIER, \"10\"))));\n" +
                "        710 attributes.add(Mode.create(\"3d8\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d8\"), Attribute.create(AMMO_USE_MULTIPLIER, \"10\"))));\n" +
                "        713 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:500\"));\n" +
                "        714 attributes.add(Mode.create(\"Ascension gun\", \"POWER\", List.of(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Syntherope:2\"))));\n" +
                "        715 attributes.add(Mode.create(\"3d6\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d6\"))));\n" +
                "        716 attributes.add(Mode.create(\"3d8\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d8\"), Attribute.create(AMMO_USE_MULTIPLIER, \"10\"))));\n" +
                "        716 attributes.add(Mode.create(\"3d8\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d8\"), Attribute.create(AMMO_USE_MULTIPLIER, \"10\"))));\n" +
                "        717 attributes.add(Mode.create(\"3d10\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d10\"), Attribute.create(AMMO_USE_MULTIPLIER, \"20\"))));\n" +
                "        717 attributes.add(Mode.create(\"3d10\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d10\"), Attribute.create(AMMO_USE_MULTIPLIER, \"20\"))));\n" +
                "        720 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Proprietary Power Pack:15:11:0.2\"));\n" +
                "        723 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:50\"));\n" +
                "        726 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:1\"));\n" +
                "        727 attributes.add(Attribute.create(CONCEALMENT_BONUS, \"5\"));\n" +
                "        730 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:250\"));\n" +
                "        733 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:2\"));\n" +
                "        734 attributes.add(Attribute.create(CONCEALMENT_BONUS, \"5\"));\n" +
                "        738 attributes.add(Attribute.create(TO_HIT_MODIFIER, \"-5\"));\n" +
                "        739 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:20\"));\n" +
                "        740 attributes.add(Mode.create(\"Braced\", List.of(Attribute.create(TO_HIT_MODIFIER, \"0\"))));\n" +
                "        743 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:1\"));\n" +
                "        744 attributes.add(Attribute.create(TO_HIT_MODIFIER, \"-5\"));\n" +
                "        745 attributes.add(Mode.create(\"Braced\", List.of(Attribute.create(TO_HIT_MODIFIER, \"0\"))));\n" +
                "        748 attributes.add(Mode.create(\"Overcharge\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"2d10\"))));\n" +
                "        749 attributes.add(Mode.create(\"Burnout\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"2d4\"))));\n" +
                "        752 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Slug Clip:10:40:0.2\"));\n" +
                "        755 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Slug Clip:20:40:0.2\"));\n" +
                "        758 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:100\"));\n" +
                "        759 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Gas Canister:500\"));\n" +
                "        760 attributes.add(Mode.create(\"Anti-Personnel\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d8\"))));\n" +
                "        761 attributes.add(Mode.create(\"Anti-Vehicle\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d10\"),\n" +
                "        762 Attribute.create(AMMO_USE_MULTIPLIER, \"10\"), Attribute.create(PENETRATION, \"5\"))));\n" +
                "        762 Attribute.create(AMMO_USE_MULTIPLIER, \"10\"), Attribute.create(PENETRATION, \"5\"))));\n" +
                "        765 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:10\"));\n" +
                "        766 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Stealth Mixture Gas Canister:500:500:0.25\"));\n" +
                "        769 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:1\"));\n" +
                "        772 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"10 Shells:10:20:1\"));\n" +
                "        773 attributes.add(Mode.create(\"Point-Blank Range\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"3d8\"))));\n" +
                "        774 attributes.add(Mode.create(\"Short Range\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"2d8\"))));\n" +
                "        775 attributes.add(Mode.create(\"Medium Range\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"0\"))));\n" +
                "        776 attributes.add(Mode.create(\"Long Range\", \"POWER\", List.of(Attribute.create(DAMAGE_DIE, \"0\"))));\n" +
                "        779 attributes.add(Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:5\"));\n" +
                "        783 Attribute.create(DAMAGE_DIE, \"3d8\"),\n" +
                "        784 Attribute.create(STUN_DAMAGE, \"3d8\"),\n" +
                "        785 Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Power Pack:60\"),\n" +
                "        786 Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Gas Canister:300\")\n" +
                "        789 Attribute.create(DAMAGE_DIE, \"3d8\"),\n" +
                "        790 Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Sniper Power Pack:5:100:0.5\")\n" +
                "        793 Attribute.create(DAMAGE_DIE, \"4d10\"),\n" +
                "        794 Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"Explosive Shell:1:300:1\")\n" +
                "        798 Attribute.create(STUN_DAMAGE, \"3d6\"),\n" +
                "        799 Attribute.create(AMMUNITION_ATTRIBITE_KEY, \"PEP Cartridge:15:100:0.5\")\n" +
                "        836 Attribute.create(DAMAGE_DIE, \"1d6/1d6\"),\n" +
                "        837 Attribute.create(\"damageType\", \"Bludgeoning\"),\n" +
                "        838 Attribute.create(\"stunSetting\", \"NO\"),\n" +
                "        839 Attribute.create(\"special\", List.of(\"An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away \" +\n" +
                "        846 Attribute.create(DAMAGE_DIE, \"1d8\"),\n" +
                "        847 Attribute.create(\"damageType\", \"Piercing\"),\n" +
                "        848 Attribute.create(\"stunSetting\", \"NO\"),\n" +
                "        849 Attribute.create(\"special\", List.of(\"An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away \" +\n" +
                "        856 Attribute.create(DAMAGE_DIE, \"1d4\"),\n" +
                "        857 Attribute.create(\"damageType\", \"Piercing\"),\n" +
                "        858 Attribute.create(\"stunSetting\", \"NO\"),\n" +
                "        859 Attribute.create(\"reach\", \"2\"),\n" +
                "        860 Attribute.create(\"providedAction\", \"Pin\"),\n" +
                "        861 Attribute.create(\"providedAction\", \"Trip\"),\n" +
                "        862 Attribute.create(\"special\", List.of(\"An Amphistaff may be coaxed by its wielder to spit venom up to 10 squares away \" +\n" +
                "        989 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        991 .withProvided(Attribute.create(\"srRating\", 5))\n" +
                "        1000 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1002 .withProvided(Attribute.create(\"srRating\", 10))\n" +
                "        1020 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1022 .withProvided(Attribute.create(\"srRating\", 15))\n" +
                "        1039 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1041 .withProvided(Attribute.create(\"srRating\", 20))\n" +
                "        1056 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1061 .withProvided(Attribute.create(\"translateDC\", 20)).toJSON());\n" +
                "        1064 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1069 .withProvided(Attribute.create(\"translateDC\", 15)).toJSON());\n" +
                "        1072 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1077 .withProvided(Attribute.create(\"translateDC\", 10)).toJSON());\n" +
                "        1080 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1085 .withProvided(Attribute.create(\"translateDC\", 5)).toJSON());\n" +
                "        1090 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1096 .withProvided(Attribute.create(\"damageThresholdHardenedMultiplier\", 2))\n" +
                "        1097 .withProvided(Attribute.create(\"healthHardenedMultiplier\", 2))\n" +
                "        1108 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1114 .withProvided(Attribute.create(\"damageThresholdHardenedMultiplier\", 3))\n" +
                "        1115 .withProvided(Attribute.create(\"healthHardenedMultiplier\", 3))\n" +
                "        1126 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1132 .withProvided(Attribute.create(\"damageThresholdHardenedMultiplier\", 4))\n" +
                "        1133 .withProvided(Attribute.create(\"healthHardenedMultiplier\", 4))\n" +
                "        1144 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1150 .withProvided(Attribute.create(\"damageThresholdHardenedMultiplier\", 5))\n" +
                "        1151 .withProvided(Attribute.create(\"healthHardenedMultiplier\", 5))\n" +
                "        1162 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1164 .withProvided(Attribute.create(\"armorType\", \"Light Armor\"))\n" +
                "        1169 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"2\"))\n" +
                "        1170 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"5\"))\n" +
                "        1174 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1176 .withProvided(Attribute.create(\"armorType\", \"Light Armor\"))\n" +
                "        1181 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"3\"))\n" +
                "        1182 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"4\"))\n" +
                "        1186 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1188 .withProvided(Attribute.create(\"armorType\", \"Light Armor\"))\n" +
                "        1193 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"4\"))\n" +
                "        1194 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"4\"))\n" +
                "        1198 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1200 .withProvided(Attribute.create(\"armorType\", \"Light Armor\"))\n" +
                "        1205 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"5\"))\n" +
                "        1206 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"3\"))\n" +
                "        1210 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1212 .withProvided(Attribute.create(\"armorType\", \"Light Armor\"))\n" +
                "        1217 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"6\"))\n" +
                "        1218 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"3\"))\n" +
                "        1222 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1224 .withProvided(Attribute.create(\"armorType\", \"Medium Armor\"))\n" +
                "        1229 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"7\"))\n" +
                "        1230 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"3\"))\n" +
                "        1234 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1236 .withProvided(Attribute.create(\"armorType\", \"Medium Armor\"))\n" +
                "        1241 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"8\"))\n" +
                "        1242 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"2\"))\n" +
                "        1246 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1248 .withProvided(Attribute.create(\"armorType\", \"Medium Armor\"))\n" +
                "        1253 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"8\"))\n" +
                "        1254 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"3\"))\n" +
                "        1258 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1260 .withProvided(Attribute.create(\"armorType\", \"Heavy Armor\"))\n" +
                "        1265 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"9\"))\n" +
                "        1266 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"3\"))\n" +
                "        1270 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1272 .withProvided(Attribute.create(\"armorType\", \"Heavy Armor\"))\n" +
                "        1277 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"10\"))\n" +
                "        1278 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"2\"))\n" +
                "        1282 .withProvided(Attribute.create(\"droidPart\", true))\n" +
                "        1284 .withProvided(Attribute.create(\"armorType\", \"Heavy Armor\"))\n" +
                "        1289 .withProvided(Attribute.create(\"armorReflexDefenseBonus\", \"11\"))\n" +
                "        1290 .withProvided(Attribute.create(\"maximumDexterityBonus\", \"1\"))\n" +
                "    SpeciesExporter.java  (33 usages found)\n" +
                "        286 .withAttribute(Attribute.create(\"speaks\", \"Bothese\"))\n" +
                "        287 .withAttribute(Attribute.create(\"reads\", \"Bothese\"))\n" +
                "        288 .withAttribute(Attribute.create(\"writes\", \"Bothese\")))\n" +
                "        297 provided.add(Attribute.create(\"speaks\", \"Basic\"));\n" +
                "        298 provided.add(Attribute.create(\"reads\", \"Basic\"));\n" +
                "        299 provided.add(Attribute.create(\"writes\", \"Basic\"));\n" +
                "        301 provided.add(Attribute.create(\"speaks\", \"Chev\"));\n" +
                "        302 provided.add(Attribute.create(\"reads\", \"Chev\"));\n" +
                "        303 provided.add(Attribute.create(\"writes\", \"Chev\"));\n" +
                "        305 provided.add(Attribute.create(\"speaks\", \"Basic\"));\n" +
                "        306 provided.add(Attribute.create(\"reads\", \"Basic\"));\n" +
                "        307 provided.add(Attribute.create(\"writes\", \"Basic\"));\n" +
                "        309 provided.add(Attribute.create(\"speaks\", \"Huttese\"));\n" +
                "        310 provided.add(Attribute.create(\"reads\", \"Huttese\"));\n" +
                "        311 provided.add(Attribute.create(\"writes\", \"Huttese\"));\n" +
                "        313 provided.add(Attribute.create(\"speaks\", \"Nikto\"));\n" +
                "        314 provided.add(Attribute.create(\"reads\", \"Nikto\"));\n" +
                "        315 provided.add(Attribute.create(\"writes\", \"Nikto\"));\n" +
                "        319 .withAttribute(Attribute.create(\"speaks\", \"Basic\"))\n" +
                "        320 .withAttribute(Attribute.create(\"reads\", \"Basic\"))\n" +
                "        321 .withAttribute(Attribute.create(\"writes\", \"Basic\")))\n" +
                "        323 .withAttribute(Attribute.create(\"speaks\", \"Huttese\"))\n" +
                "        324 .withAttribute(Attribute.create(\"reads\", \"Huttese\"))\n" +
                "        325 .withAttribute(Attribute.create(\"writes\", \"Huttese\")))\n" +
                "        328 provided.add(Attribute.create(\"speaks\", s));\n" +
                "        329 provided.add(Attribute.create(\"reads\", s));\n" +
                "        330 provided.add(Attribute.create(\"writes\", s));\n" +
                "        374 attributes.add(Attribute.create(\"bonusTalentTree\", \"1st-Degree Droid Talent Tree\"));\n" +
                "        379 attributes.add(Attribute.create(\"bonusTalentTree\", \"2nd-Degree Droid Talent Tree\"));\n" +
                "        384 attributes.add(Attribute.create(\"bonusTalentTree\", \"3rd-Degree Droid Talent Tree\"));\n" +
                "        389 attributes.add(Attribute.create(\"bonusTalentTree\", \"4th-Degree Droid Talent Tree\"));\n" +
                "        393 attributes.add(Attribute.create(\"bonusTalentTree\", \"5th-Degree Droid Talent Tree\"));\n" +
                "        399 attributes.add(Attribute.create(\"isDroid\", \"true\"));\n" +
                "    StartingFeats.java  (4 usages found)\n" +
                "        41 .map(text -> Attribute.create(\"classFeat\", text)).collect(Collectors.toList()));\n" +
                "        47 attributes.add(Attribute.create(\"classFeat\", entry.text()));\n" +
                "        52 attributes.add(Attribute.create(\"availableClassFeats\", 3));\n" +
                "        53 attributes.addAll(Arrays.stream(entry.text().split(\",\")).map(text -> Attribute.create(\"classFeat\", text)).collect(Collectors.toList()));\n" +
                "    TalentExporter.java  (23 usages found)\n" +
                "        229 attributes.add(Attribute.create(\"finesseStat\", \"CHA\"));\n" +
                "        232 attributes.add(Attribute.create(\"takeMultipleTimes\", true));\n" +
                "        233 attributes.add(Attribute.create(\"weaponSpecialization\", \"#payload#\"));\n" +
                "        236 attributes.add(Attribute.create(\"takeMultipleTimes\", true));\n" +
                "        237 attributes.add(Attribute.create(\"greaterWeaponSpecialization\", \"#payload#\"));\n" +
                "        240 attributes.add(Attribute.create(\"takeMultipleTimes\", true));\n" +
                "        241 attributes.add(Attribute.create(\"greaterWeaponFocus\", \"#payload#\"));\n" +
                "        244 attributes.add(Attribute.create(\"weaponSpecialization\", \"Discblade\"));\n" +
                "        247 attributes.add(Attribute.create(\"weaponSpecialization\", \"Lightsabers\"));\n" +
                "        250 attributes.add(Attribute.create(\"takeMultipleTimes\", true));\n" +
                "        258 attributes.add(Buff.create(\"Lightsaber Defense\").withProvided(Attribute.create(\"deflectionBonus\", \"$lightsaberDefense\")));\n" +
                "        259 attributes.add(Attribute.create(\"lightsaberDefense\", 1));\n" +
                "        260 attributes.add(Attribute.create(\"takeMultipleTimes\", 3));\n" +
                "        263 attributes.add(Attribute.create(\"lightsabersDamageStat\", \"DEX\"));\n" +
                "        266 attributes.add(Attribute.create(\"action\", \"Once per round when an opponent hits you with a melee attack, you may spend a Force Point\n" +
                "        272 attributes.add(Attribute.create(\"action\", \"Once per encounter, you may spend a Force Point as a Swift Action to designate a single\n" +
                "        281 attributes.add(Attribute.create(\"redirectedShotBonus\", 5));\n" +
                "        284 attributes.add(Attribute.create(\"blockBonus\", 3));\n" +
                "        285 attributes.add(Attribute.create(\"deflectBonus\", 3));\n" +
                "        288 attributes.add(Attribute.create(\"note\", \"You may Take 10 on Acrobatics checks to Tumble, even when distracted or threatened. Additionally\n" +
                "        291 attributes.add(Attribute.create(\"note\", \"You may reroll a failed Use the Force check when using the Block or Deflect Talents.\"));\n" +
                "        294 attributes.add(Attribute.create(\"note\", \"By harnessing the unique characteristics of a Lightsaber, you can catch your opponent off\n" +
                "        297 attributes.add(Attribute.create(\"note\", \"When attacking with a Lightsaber, you score a critical hit on a natural roll of 19 or 20.\n" +
                "    Template.java  (3 usages found)\n" +
                "        32 this.attributes.add(Attribute.create(\"application\", application));\n" +
                "        62 this.attributes.add(Attribute.create(\"prefix\", prefix));\n" +
                "        68 this.attributes.add(Attribute.create(\"suffix\", suffix));\n" +
                "    TemplateExporter.java  (1 usage found)\n" +
                "        139 .withProvided(Attribute.create(\"itemMod\", \"true\"))\n" +
                "    TraitExporter.java  (139 usages found)\n" +
                "        111 response.add(Trait.create(\"Base Speed \" + speed).withDescription(\"A being has a base speed of \" + speed + \".\").withProvided(Attribute.create(\"speed\", \"Base Speed \" + speed)).toJSON());\n" +
                "        112 response.add(Trait.create(\"Swim Speed \" + speed).withDescription(\"A being has a swim speed of \" + speed + \".\").withProvided(Attribute.create(\"speed\", \"Swim Speed \" + speed)).toJSON());\n" +
                "        113 response.add(Trait.create(\"Fly Speed \" + speed).withDescription(\"A being has a fly speed of \" + speed + \".\").withProvided(Attribute.create(\"speed\", \"Fly Speed \" + speed)).toJSON());\n" +
                "        114 + speed + \".\").withProvided(Attribute.create(\"speed\", \"Wheeled Speed \" + speed)).toJSON());\n" +
                "        115 + speed + \".\").withProvided(Attribute.create(\"speed\", \"Walking Speed \" + speed)).toJSON());\n" +
                "        116 + speed + \".\").withProvided(Attribute.create(\"speed\", \"Tracked Speed \" + speed)).toJSON());\n" +
                "        117 response.add(Trait.create(\"Hover Speed \" + speed).withDescription(\"A being has a hover speed of \" + speed + \".\").withProvided(Attribute.create(\"speed\", \"Hover Speed \" + speed)).toJSON());\n" +
                "        125 unarmed attack.\").withProvided(Attribute.create(\"droidUnarmedDamageDie\", \"1\")).toJSON());\n" +
                "        126 unarmed attack.\").withProvided(Attribute.create(\"droidUnarmedDamageDie\", \"1d2\")).toJSON());\n" +
                "        127 unarmed attack.\").withProvided(Attribute.create(\"droidUnarmedDamageDie\", \"1d3\")).toJSON());\n" +
                "        128 unarmed attack.\").withProvided(Attribute.create(\"droidUnarmedDamageDie\", \"1d4\")).toJSON());\n" +
                "        129 unarmed attack.\").withProvided(Attribute.create(\"droidUnarmedDamageDie\", \"1d6\")).toJSON());\n" +
                "        130 unarmed attack.\").withProvided(Attribute.create(\"droidUnarmedDamageDie\", \"1d8\")).toJSON());\n" +
                "        131 unarmed attack.\").withProvided(Attribute.create(\"droidUnarmedDamageDie\", \"2d6\")).toJSON());\n" +
                "        132 unarmed attack.\").withProvided(Attribute.create(\"droidUnarmedDamageDie\", \"2d8\")).toJSON());\n" +
                "        133 items are added.\").withProvided(Attribute.create(\"appendages\", \"-2\")).toJSON());\n" +
                "        147 trait.withProvided(Attribute.create(attribute.toLowerCase() + \"Bonus\", attributeBonus));\n" +
                "        158 withProvided(Lists.newArrayList(Attribute.create(\"weaponFamiliarity\", \"#payload#\"))).toJSON());\n" +
                "        314 \"</p>\").withProvided(Attribute.create(\"disableAttributeModification\", true))\n" +
                "        315 .withProvided(Attribute.create(\"baseStrength\", 10))\n" +
                "        316 .withProvided(Attribute.create(\"baseDexterity\", 10))\n" +
                "        317 .withProvided(Attribute.create(\"baseConstitution\", 10))\n" +
                "        318 .withProvided(Attribute.create(\"baseIntelligence\", 10))\n" +
                "        319 .withProvided(Attribute.create(\"baseWisdom\", 10))\n" +
                "        320 .withProvided(Attribute.create(\"baseCharisma\", 10))\n" +
                "        339 \"</p>\").withProvided(Attribute.create(\"appendages\", \"2\")).toJSON());\n" +
                "        342 \"</p>\").withProvided(Attribute.create(\"appendages\", \"4\")).toJSON());\n" +
                "        345 \"</p>\").withProvided(Attribute.create(\"appendages\", \"6\")).toJSON());\n" +
                "        349 .withProvided(Attribute.create(\"lightsaberDefense\", \"*2\")).toJSON());\n" +
                "        353 .withProvided(Attribute.create(\"lightsaberDefense\", \"2\")).toJSON());\n" +
                "        357 .withProvided(Attribute.create(REFLEX_DEFENSE_BONUS, \"1\")).withProvided(Attribute.create(\"willDefenseBonus\", \"1\")).toJSON());\n" +
                "        357 .withProvided(Attribute.create(REFLEX_DEFENSE_BONUS, \"1\")).withProvided(Attribute.create(\"willDefenseBonus\", \"1\")).toJSON());\n" +
                "        481 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", \"1\"));\n" +
                "        482 attributes.add(Attribute.create(\"willDefenseBonus\", \"1\"));\n" +
                "        483 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, \"1\"));\n" +
                "        497 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", bonus));\n" +
                "        503 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", 5).withModifier(\"Extreme Cold\"));\n" +
                "        506 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", -5).withModifier(\"Extreme Cold\"));\n" +
                "        509 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", 5).withModifier(\"Poisons\"));\n" +
                "        510 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", 5).withModifier(\"Toxic Atmospheres\"));\n" +
                "        513 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", 5).withModifier(\"Radiation\"));\n" +
                "        516 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", 5).withModifier(\"Extreme Temperatures\"));\n" +
                "        517 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", 5).withModifier(\"Radiation\"));\n" +
                "        520 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", -5).withModifier(\"Extreme Temperatures\"));\n" +
                "        523 attributes.add(Attribute.create(\"fortitudeDefenseBonus\", 5).withModifier(\"Extreme Heat\"));\n" +
                "        536 attributes.add(Attribute.create(\"willDefenseBonus\", bonus));\n" +
                "        542 attributes.add(Attribute.create(\"willDefenseBonus\", 5).withModifier(\"Fear Effects\"));\n" +
                "        546 attributes.add(Attribute.create(\"willDefenseBonus\", -5).withModifier(\"Fear Effects\"));\n" +
                "        550 attributes.add(Attribute.create(\"willDefenseBonus\", 2).withModifier(\"Persuasion checks made to improve their Attitude by any creature of a different Species\"));\n" +
                "        554 attributes.add(Attribute.create(\"willDefenseBonus\", 5).withModifier(\"any use of the Use the Force Skill\"));\n" +
                "        558 attributes.add(Attribute.create(\"willDefenseBonus\", 5).withModifier(\"Mind-Affecting Effects\"));\n" +
                "        561 attributes.add(Attribute.create(\"willDefenseBonus\", 2).withModifier(\"Deception Checks\"));\n" +
                "        562 attributes.add(Attribute.create(\"willDefenseBonus\", 2).withModifier(\"Persuasion Checks\"));\n" +
                "        589 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, bonus));\n" +
                "        599 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, -10));\n" +
                "        600 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, -10));\n" +
                "        601 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"2d8\"));\n" +
                "        602 attributes.add(Attribute.create(\"vehicleFightingSpace\", \"1 square\"));\n" +
                "        603 attributes.add(Attribute.create(SNEAK_MODIFIER, -20));\n" +
                "        604 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +100));\n" +
                "        605 attributes.add(Attribute.create(GRAPPLE_SIZE_MODIFIER, +25));\n" +
                "        608 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, -10));\n" +
                "        609 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, -10));\n" +
                "        610 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"2d8\"));\n" +
                "        611 attributes.add(Attribute.create(\"vehicleFightingSpace\", \"4 squares\"));\n" +
                "        612 attributes.add(Attribute.create(SNEAK_MODIFIER, -20));\n" +
                "        613 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +200));\n" +
                "        614 attributes.add(Attribute.create(GRAPPLE_SIZE_MODIFIER, +30));\n" +
                "        617 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, -10));\n" +
                "        618 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, -10));\n" +
                "        619 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"2d8\"));\n" +
                "        620 attributes.add(Attribute.create(\"vehicleFightingSpace\", \"4 squares\"));\n" +
                "        621 attributes.add(Attribute.create(SNEAK_MODIFIER, -20));\n" +
                "        622 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +500));\n" +
                "        623 attributes.add(Attribute.create(GRAPPLE_SIZE_MODIFIER, +35));\n" +
                "        626 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, -10));\n" +
                "        627 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, -10));\n" +
                "        628 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"2d8\"));\n" +
                "        629 attributes.add(Attribute.create(\"vehicleFightingSpace\", \"1 square\"));\n" +
                "        630 attributes.add(Attribute.create(SNEAK_MODIFIER, -20));\n" +
                "        631 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +50));\n" +
                "        632 attributes.add(Attribute.create(GRAPPLE_SIZE_MODIFIER, +20));\n" +
                "        635 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, -5));\n" +
                "        636 attributes.add(Attribute.create(\"characterFightingSpace\", \"16 squares\"));\n" +
                "        637 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, -5));\n" +
                "        638 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"2d6\"));\n" +
                "        639 attributes.add(Attribute.create(SNEAK_MODIFIER, -15));\n" +
                "        640 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +20));\n" +
                "        641 attributes.add(Attribute.create(GRAPPLE_SIZE_MODIFIER, +15));\n" +
                "        644 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, -2));\n" +
                "        645 attributes.add(Attribute.create(\"characterFightingSpace\", \"9 squares\"));\n" +
                "        646 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, -2));\n" +
                "        647 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"1d8\"));\n" +
                "        648 attributes.add(Attribute.create(SNEAK_MODIFIER, -10));\n" +
                "        649 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +10));\n" +
                "        650 attributes.add(Attribute.create(GRAPPLE_SIZE_MODIFIER, +10));\n" +
                "        653 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, -1));\n" +
                "        654 attributes.add(Attribute.create(\"characterFightingSpace\", \"4 squares\"));\n" +
                "        655 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, -1));\n" +
                "        656 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"1d6\"));\n" +
                "        657 attributes.add(Attribute.create(SNEAK_MODIFIER, -5));\n" +
                "        658 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +5));\n" +
                "        659 attributes.add(Attribute.create(GRAPPLE_SIZE_MODIFIER, +5));\n" +
                "        662 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, +0));\n" +
                "        663 attributes.add(Attribute.create(\"characterFightingSpace\", \"1 square\"));\n" +
                "        664 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, +0));\n" +
                "        665 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"1d4\"));\n" +
                "        666 attributes.add(Attribute.create(SNEAK_MODIFIER, +0));\n" +
                "        667 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +0));\n" +
                "        670 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, +1));\n" +
                "        671 attributes.add(Attribute.create(\"characterFightingSpace\", \"1 square\"));\n" +
                "        672 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, +1));\n" +
                "        673 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"1d3\"));\n" +
                "        674 attributes.add(Attribute.create(SNEAK_MODIFIER, +5));\n" +
                "        675 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +0));\n" +
                "        678 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, +2));\n" +
                "        679 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, +2));\n" +
                "        680 attributes.add(Attribute.create(\"characterFightingSpace\", \"1 square\"));\n" +
                "        681 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"1d2\"));\n" +
                "        682 attributes.add(Attribute.create(SNEAK_MODIFIER, +10));\n" +
                "        683 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +0));\n" +
                "        686 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, +5));\n" +
                "        687 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, +5));\n" +
                "        688 attributes.add(Attribute.create(\"characterFightingSpace\", \"1 square\"));\n" +
                "        689 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"1\"));\n" +
                "        690 attributes.add(Attribute.create(SNEAK_MODIFIER, +15));\n" +
                "        691 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +0));\n" +
                "        694 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, +10));\n" +
                "        695 attributes.add(Attribute.create(SHIP_SKILL_MODIFIER, +10));\n" +
                "        696 attributes.add(Attribute.create(\"characterFightingSpace\", \"1 square\"));\n" +
                "        697 attributes.add(Attribute.create(UNARMED_DAMAGE_DIE, \"1\"));\n" +
                "        698 attributes.add(Attribute.create(SNEAK_MODIFIER, +20));\n" +
                "        699 attributes.add(Attribute.create(DAMAGE_THRESHOLD_SIZE_MODIFIER, +0));\n" +
                "        712 return Attribute.create(\"damageReduction\", Integer.parseInt(m.group(1)));\n" +
                "        722 attributes.add(Attribute.create(REFLEX_DEFENSE_BONUS, \"#payload#\"));\n" +
                "        733 attributes.add(Attribute.create(\"classSkill\", m.group(1).toLowerCase()));\n" +
                "        738 attributes.add(Attribute.create(\"trainedSkills\", \"1\"));\n" +
                "        748 return Attribute.create(\"provides\", \"General Feats\");\n" +
                "        789 return Attribute.create(\"bonusFeat\", bonusFeat);\n" +
                "    VehicleBaseTypeExporter.java  (14 usages found)\n" +
                "        69 VehicleBaseType template = templates.computeIfAbsent(templateName, tn -> VehicleBaseType.create(tn).withProvided(Attribute.create(\"vehicleSubType\", mapSubtype(tn))));\n" +
                "        83 template.withProvided(Attribute.create(\"baseStrength\", value));\n" +
                "        86 template.withProvided(Attribute.create(\"baseDexterity\", value));\n" +
                "        89 template.withProvided(Attribute.create(\"baseIntelligence\", value));\n" +
                "        92 template.withProvided(Attribute.create(\"speedCharacterScale\", value));\n" +
                "        95 template.withProvided(Attribute.create(\"speedStarshipScale\", value));\n" +
                "        98 template.withProvided(Attribute.create(\"hitPointEq\", value));\n" +
                "        101 template.withProvided(Attribute.create(\"damageThresholdBonus\", value));\n" +
                "        104 template.withProvided(Attribute.create(\"armorReflexDefenseBonus\", value));\n" +
                "        110 template.withProvided(Attribute.create(\"crew\", value));\n" +
                "        113 template.withProvided(Attribute.create(\"passengers\", value));\n" +
                "        122 template.withProvided(Attribute.create(\"cargoCapacity\", toString(getKilograms(toks[0], unit))));\n" +
                "        125 template.withProvided(Attribute.create(\"consumables\", value));\n" +
                "        129 template.withProvided(Attribute.create(\"emplacementPoints\", value));\n" +
                "    VehicleExporter.java  (24 usages found)\n" +
                "        149 customTemplate.withProvided(Attribute.create(\"baseStrength\", matcher.group(1)));\n" +
                "        154 customTemplate.withProvided(Attribute.create(\"baseDexterity\", matcher.group(1)));\n" +
                "        159 customTemplate.withProvided(Attribute.create(\"baseIntelligence\", matcher.group(1)));\n" +
                "        164 customTemplate.withProvided(Attribute.create(\"vehicleSubType\", matcher.group(2)));\n" +
                "        173 customTemplate.withProvided(Attribute.create(\"damageReduction\", matcher.group(1)));\n" +
                "        178 customTemplate.withProvided(Attribute.create(\"speedCharacterScale\", matcher.group(1)));\n" +
                "        183 customTemplate.withProvided(Attribute.create(\"speedStarshipScale\", matcher.group(1)));\n" +
                "        188 customTemplate.withProvided(Attribute.create(\"maximumVelocity\", matcher.group(1)));\n" +
                "        194 customTemplate.withProvided(Attribute.create(\"crew\", matcher.group(1)));\n" +
                "        195 customTemplate.withProvided(Attribute.create(\"crewQuality\", matcher.group(3)));\n" +
                "        196 customTemplate.withProvided(Attribute.create(\"passengers\", matcher.group(4)));\n" +
                "        209 customTemplate.withProvided(Attribute.create(\"crewQuality\", m2.group(1)));\n" +
                "        211 customTemplate.withProvided(Attribute.create(\"crew\", matcher.group(1)));\n" +
                "        226 customTemplate.withProvided(Attribute.create(\"cover\", matcher.group(1) + \":\" + matcher.group(2)));\n" +
                "        228 customTemplate.withProvided(Attribute.create(\"cover\", matcher.group(1)));\n" +
                "        237 customTemplate.withProvided(Attribute.create(\"cargoCapacity\", toString(getKilograms(value, unit))));\n" +
                "        259 customTemplate.withProvided(Attribute.create(\"consumables\", matcher.group(1)));\n" +
                "        264 customTemplate.withProvided(Attribute.create(\"hitPointEq\", matcher.group(1).replace(\",\", \"\")));\n" +
                "        269 customTemplate.withProvided(Attribute.create(\"shieldRating\", matcher.group(1).replace(\",\", \"\")));\n" +
                "        275 customTemplate.withProvided(Attribute.create(\"armorReflexDefenseBonus\", matcher.group(1)));\n" +
                "        281 customTemplate.withProvided(Attribute.create(\"payload\", matcher.group(1)));\n" +
                "        415 vehicleWeapon.overwriteProvided(Attribute.create(\"damage\", m.group(1)));\n" +
                "        423 vehicleWeapon.withProvided(Attribute.create(\"suffix\", \", \"+modifier));\n" +
                "        447 vehicleWeapon.withProvided(Attribute.create(\"providesSlot\", crewPosition));\n" +
                "    VehicleSystemsExporter.java  (73 usages found)\n" +
                "        186 currentVariant.withProvided(Attribute.create(\"emplacementPointsBonus\", nameModifier));\n" +
                "        199 currentVariant.withProvided(Attribute.create(key.toLowerCase(), value));\n" +
                "        276 current.withProvided(Attribute.create(\"emplacementPoints\", m.group(1)));\n" +
                "        279 variant.withProvided(Attribute.create(\"emplacementPoints\", m.group(1)));\n" +
                "        316 final Attribute damage = Attribute.create(\"damage\", m.group(1).trim());\n" +
                "        323 final Attribute damage = Attribute.create(\"damage\", m.group(1).trim());\n" +
                "        341 current.withProvided(Attribute.create(\"seeAlso\", m.group(1)));\n" +
                "        344 variant.withProvided(Attribute.create(\"seeAlso\", m.group(1)));\n" +
                "        430 system.withProvided(Attribute.create(\"providesSlot\", ASTROMECH_DROID));\n" +
                "        436 .withProvided(Attribute.create(\"itemMod\", \"true\"));\n" +
                "        437 system.withProvided(Attribute.create(\"bonusDamage\", \"1d10\"));\n" +
                "        438 system.withProvided(Attribute.create(\"cost\", \"*3\"));\n" +
                "        439 system.withProvided(Attribute.create(\"autofireAttackBonus\", \"0\"));\n" +
                "        447 .withProvided(Attribute.create(\"itemMod\", \"true\"));\n" +
                "        448 system.withProvided(Attribute.create(\"autofireAttackBonus\", \"+3\"));\n" +
                "        452 .withProvided(Attribute.create(\"itemMod\", \"true\"));\n" +
                "        453 system.withProvided(Attribute.create(\"autofireAttackBonus\", \"0\"));\n" +
                "        458 .withProvided(Attribute.create(\"itemMod\", \"true\"));\n" +
                "        459 system.withProvided(Attribute.create(\"modifies\", \"TYPE:Weapon Systems\"));\n" +
                "        460 system.withProvided(Attribute.create(\"bonusDamage\", \"2d10\"));\n" +
                "        461 system.withProvided(Attribute.create(\"cost\", \"*5\"));\n" +
                "        462 system.withProvided(Attribute.create(\"autofireAttackBonus\", \"0\"));\n" +
                "        469 .withProvided(Attribute.create(\"itemMod\", \"true\"));\n" +
                "        470 system.withProvided(Attribute.create(\"modifies\", \"TYPE:Weapon Systems\"));\n" +
                "        471 system.withProvided(Attribute.create(\"bonusDamage\", \"2d10\"));\n" +
                "        472 system.withProvided(Attribute.create(\"cost\", \"*5\"));\n" +
                "        473 system.withProvided(Attribute.create(\"autofireAttackBonus\", \"0\"));\n" +
                "        477 .withProvided(Attribute.create(\"itemMod\", \"true\"));\n" +
                "        480 systems.add(system.copy().withName(\"Heavy Proton Torpedoes\").replaceAttribute(Attribute.create(\"damage\", \"9d10x5\")));\n" +
                "        484 .withProvided(Attribute.create(\"itemMod\", \"true\"));\n" +
                "        485 system.withProvided(Attribute.create(\"aidAnotherBonus\", \"2\"));\n" +
                "        490 system.withProvided(Attribute.create(\"damage\", \"10d10x5\"));\n" +
                "        491 system.withProvided(Attribute.create(\"targetSizeModifier\", \"<Colossal:-20\"));\n" +
                "        495 system.withProvided(Attribute.create(\"damage\", \"8d10x5\"));\n" +
                "        496 system.withProvided(Attribute.create(\"targetSizeModifier\", \"<Colossal:-20\"));\n" +
                "        500 system.withProvided(Attribute.create(\"damage\", \"6d10x5\"));\n" +
                "        504 system.withProvided(Attribute.create(\"damage\", \"8d10x5\"));\n" +
                "        505 system.withProvided(Attribute.create(\"splash\", \"4 square\"));\n" +
                "        506 system.withProvided(Attribute.create(\"targetSizeModifier\", \"<Colossal:-20\"));\n" +
                "        510 system.withProvided(Attribute.create(\"damage\", \"10d10x5\"));\n" +
                "        511 system.withProvided(Attribute.create(\"targetSizeModifier\", \"<Colossal:-20\"));\n" +
                "        515 system.withProvided(Attribute.create(\"damage\", \"8d10x5\"));\n" +
                "        516 system.withProvided(Attribute.create(\"targetSizeModifier\", \"<Colossal:-20\"));\n" +
                "        520 system.withProvided(Attribute.create(\"damage\", \"6d10x2\"));\n" +
                "        524 system.withProvided(Attribute.create(\"damage\", \"7d10x2\"));\n" +
                "        525 system.withProvided(Attribute.create(\"targetSizeModifier\", \"<Colossal:-20\"));\n" +
                "        529 system.withProvided(Attribute.create(\"stunDamage\", \"7d10x2\"));\n" +
                "        533 system.withProvided(Attribute.create(\"stunDamage\", \"4d10x2\"));\n" +
                "        534 system.withProvided(Attribute.create(\"autofireAttackBonus\", \"0\"));\n" +
                "        538 system.withProvided(Attribute.create(\"stunDamage\", \"5d10x2\"));\n" +
                "        542 system.withProvided(Attribute.create(\"damage\", \"4d10x2\"));\n" +
                "        543 system.withProvided(Attribute.create(\"splash\", \"4 square\"));\n" +
                "        547 system.withProvided(Attribute.create(\"damage\", \"4d6\"));\n" +
                "        548 system.withProvided(Attribute.create(\"damageType\", \"Ion\"));\n" +
                "        552 system.withProvided(Attribute.create(\"damage\", \"8d10x40\"));\n" +
                "        553 system.withProvided(Attribute.create(\"targetSizeModifier\", \"<Colossal:-20\"));\n" +
                "        557 system.withProvided(Attribute.create(\"damage\", \"6d10x2\"));\n" +
                "        561 superHeavy.withProvided(Attribute.create(\"damage\", \"11d10x5\"));\n" +
                "        571 systems.add(system.copy().withName(\"Class 2.5 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"2.5\")));\n" +
                "        575 systems.add(system.copy().withName(\"Class 7 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"7\")));\n" +
                "        576 systems.add(system.copy().withName(\"Class 9 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"9\")));\n" +
                "        580 systems.add(system.copy().withName(\"Class 12 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"12\")));\n" +
                "        581 systems.add(system.copy().withName(\"Class 14 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"14\")));\n" +
                "        582 systems.add(system.copy().withName(\"Class 16 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"16\")));\n" +
                "        583 systems.add(system.copy().withName(\"Class 18 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"18\")));\n" +
                "        584 systems.add(system.copy().withName(\"Class 20 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"20\")));\n" +
                "        585 systems.add(system.copy().withName(\"Class 24 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"24\")));\n" +
                "        586 systems.add(system.copy().withName(\"Class 25 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"25\")));\n" +
                "        587 systems.add(system.copy().withName(\"Class 30 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"25\")));\n" +
                "        591 systems.add(system.copy().withName(\"Class 0.9 Hyperdrive\").withProvided(Attribute.create(\"hyperdrive\", \"0.9\")));\n" +
                "        593 .withProvided(Attribute.create(\"hyperdrive\", \"0.5\")).withAvailability(\"Illegal\")\n" +
                "        597 system.withProvided(Attribute.create(\"hyperdrive\", hyperdriveMatcher.group(1)));\n" +
                "        644 attributes.add(Attribute.create(\"emplacementPointBonus\", name.split(\" \")[0]));\n";

        Pattern p = Pattern.compile("Attribute\\.create\\(\\\"([\\w]*)\\\"");

        Matcher m = p.matcher(s);

        while(m.find()){
            //printUnique(m.group(1));
    }

    }
}
