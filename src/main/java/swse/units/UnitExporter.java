package swse.units;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.*;
import swse.util.GeneratedLists;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static swse.talents.TalentExporter.DUPLICATE_TALENT_NAMES;
import static swse.util.Util.printUnique;

public class UnitExporter extends BaseExporter {
    public static final String JSON_OUTPUT = SYSTEM_LOCATION + "\\raw_export\\Units-CL-";

    public static final List<String> colossal = Lists.newArrayList("(Frigate)", "(Cruiser)", "(Station)");
    public static final Pattern VARIANT_QUALIFIER = Pattern.compile("(\\(.+\\))$");
    public static final Pattern STRENGTH_PATTERN = Pattern.compile("Strength:? ([\\d-]*)");
    public static final Pattern DEXTERITY_PATTERN = Pattern.compile("Dexterity:? ([\\d-]*)");
    public static final Pattern CONSTITUTION_PATTERN = Pattern.compile("Constitution:? ([\\d-]*)");
    public static final Pattern INTELLIGENCE_PATTERN = Pattern.compile("Intelligence:? ([\\d-]*)");
    public static final Pattern WISDOM_PATTERN = Pattern.compile("Wisdom:? ([\\d-]*)");
    public static final Pattern CHARISMA_PATTERN = Pattern.compile("Charisma:? ([\\d-]*)");
    public static final Pattern SIZE_AND_SUBTYPE = Pattern.compile("^(Tiny|Fine|Diminuative|Small|Medium|Large|Huge|Gargantuan|Colossal|Colossal \\(Frigate\\)|Colossal \\(Cruiser\\)|Colossal \\(Station\\)) ([\\s\\w]*)(?:\\(([\\s\\w]* Template)\\))?");
    public static final Pattern DAMAGE_REDUCTION = Pattern.compile("Damage Reduction: (\\d*)");
    public static final Pattern SHIP_SCALE_SPEED = Pattern.compile("Fly (\\d*) Squares");
    public static final Pattern CHARACTER_SCALE_SPEED = Pattern.compile("Speed: Fly (\\d*) Squares");
    public static final Pattern MAXIMUM_VELOCITY = Pattern.compile("\\(Maximum Velocity ([,\\d]*) km/h\\)");
    public static final Pattern HYPERDRIVE_PATTERN = Pattern.compile("Hyperdrive: (Class [.\\d]*)(?: \\(Backup (Class [.\\d]*)\\))?");
    public static final Pattern CARGO_PATTERN = Pattern.compile("Cargo: (None|[\\d,.]*) ?(\\w*)?");
    public static final Pattern COVER_PATTERN = Pattern.compile("(Total Cover|\\+5 Cover Bonus|No Cover|\\+10 Cover Bonus) ?\\(?([\\s\\w]*)?\\)?");
    public static final Pattern CREW_PASSENGERS = Pattern.compile("Crew: ([\\d\\w,-]*|[\\d,]* to [\\d,]*)( plus Astromech Droid)? \\((\\w*) Crew Quality\\)(?:,|;)? (?:Passengers: )?([\\s\\w\\d()]*)");
    public static final Pattern CONSUMABLE_PATTERN = Pattern.compile("Consumables: ([\\d\\s\\w.*()-]*?)(?:;|$)");
    public static final Pattern HIT_POINT_PATTERN = Pattern.compile("Hit Points: ([,\\d]*)");
    public static final Pattern SHIELD_RATING_PATTERN = Pattern.compile("Shield Rating: ([,\\d]*)");
    public static final Pattern ARMOR_PATTERN = Pattern.compile("\\+(\\d*) Armor");
    public static final Pattern PAYLOAD_PATTERN = Pattern.compile("Payload: ([\\s\\w\\d]*)");
    public static final Pattern APPENDAGES = Pattern.compile("(\\d*) ?(\\w*) (?:Appendage|Mount|Locomotion)s? ?(.*)");
    public static final Pattern DROID_SYSTEMS_PATTERN = Pattern.compile("(\\d*) ?(Telescopic|Gyro-Stabilized|Stabilized|Multifunction Apparatus|Magnetic Hands|Concealed Item|F-187 Fusion Cutter|Power Recharger|Limited|Exclusive) ?(?:Appendage)?s?");
    public static final Pattern DARK_SIDE_PATTERN = Pattern.compile("Dark Side Score: (\\d+)");
    public static final Pattern OCCUPATION_PATTERN = Pattern.compile("Occupation \\((.*)\\):");
    public static final Pattern CLASS_SKILL_PATTERN = Pattern.compile("([\\w\\s()]+) (?:is always considered|as|is always) a Class Skill");
    public static final Pattern DESTINY_PATTERN = Pattern.compile("Destiny (Fulfilled)? ?\\((.*)\\):");
    public static final Pattern AVAILABILITY_PATTERN = Pattern.compile("Availability: ([\\w,\\s-()]*);|, Cost");
    public static final Pattern ORGANIZATION_SCORE_PATTERN = Pattern.compile("Organization Score \\((.*)\\): (\\d+)");
    public static final Pattern CL_PATTERN = Pattern.compile("\\(CL (\\d+)\\)");
    private static final List<String> DROID_TYPES = List.of("1st\\-Degree Droid", "2nd\\-Degree Droid", "3rd\\-Degree Droid", "4th\\-Degree Droid", "5th\\-Degree Droid");
    private static final List<String> FOLLOWERS = List.of("Utility Follower", "Defensive Follower", "Aggressive Follower", "Akk Dog Follower Template");

    private static final Pattern NEAR_HUMAN_PATTERN = Pattern.compile("Near-Human \\(([\\w\\s]*)\\)");
    private static final Pattern SHARD_DROID_TYPE_PATTERN = Pattern.compile("Shard \\(([\\w\\s]*)\\)");
    private static final Pattern AQUALISH_TYPE_PATTERN = Pattern.compile("Aqualish \\(([\\w\\s]*)\\)");
    private static final Pattern POSSIBLE_MULTIPLIER_TYPE_PATTERN = Pattern.compile("\\(([\\w\\s]*)\\)");
    private static final Table<String, String, String> ITEM_TALENT_MAPPING = HashBasedTable.create();
    public static final String SKILLS_REGEX = "Acrobatics|Climb|Deception|Endurance|Gather Information|Initiative|Jump|" +
            "Knowledge \\(Bureaucracy\\)|Knowledge \\(Galactic Lore\\)|Knowledge \\(Life Sciences\\)|Knowledge \\(Physical Sciences\\)|" +
            "Knowledge \\(Social Sciences\\)|Knowledge \\(Tactics\\)|Knowledge \\(Technology\\)|Mechanics|Perception|" +
            "Persuasion|Pilot|Ride|Stealth|Survival|Swim|Treat Injury|Use Computer|Use the Force";
    public static final Pattern VALUE_AND_PAYLOADS = Pattern.compile("(Multiattack Proficiency \\(Advanced Melee Weapons\\)|Multiattack Proficiency \\(Rifles\\)|[\\w\\s',-]+)(?:\\()?(" + SKILLS_REGEX + "|[\\s\\w,-;+-]+)?(?:\\))?(?:\\()?([\\s\\w,-;()+-]+)?(?:\\))?");
   // public static final Pattern VALUE_AND_PAYLOADS_ITEMS = Pattern.compile("(Multiattack Proficiency \\(Advanced Melee Weapons\\)|Multiattack Proficiency \\(Rifles\\)|[\\w\\s,'-]+)(?:\\()?(" + SKILLS_REGEX + "|[\\s\\w,-;+-]+)?(?:\\))?(?:\\()?([\\s\\w,-;()+-]+)?(?:\\))?");
    public static final Pattern SKILL_PATTERN = Pattern.compile("(" + SKILLS_REGEX + ") \\+(\\d+)");
    private static Map<String,String> ITEMS_BY_ALTERNATE_NAME;

    private static Map<String, String> namedCrewPosition = new HashMap<>();
    private static Pattern classPattern;
    private static Pattern speciesPattern;
    private static Pattern speciesTypePattern;
    private static Pattern traitTypePattern;
    private static Pattern templateTypePattern;
    private static Pattern ageTypePattern;
    private int i = 0;
    private static Set<Integer> cls = new HashSet<>();

    public static void main(String[] args) {
        setMaps();

        List<String> nonHeroicUnits = new ArrayList<>(getAlphaLinks("/wiki/Category:Nonheroic_Units?from="));
        nonHeroicUnits.add("/wiki/Category:Nonheroic_Units");
        List<String> heroicUnits = new ArrayList<>(getAlphaLinks("/wiki/Category:Heroic_Units?from="));
        heroicUnits.add("/wiki/Category:Heroic_Units");

        classPattern = Pattern.compile("(" + String.join("|", GeneratedLists.CLASSES) + "|" + String.join("|", FOLLOWERS) + ")(?: )?(\\d+)?");
        speciesPattern = Pattern.compile("(" + GeneratedLists.SPECIES.stream().map(Pattern::quote).collect(Collectors.joining("|")) + "|" + String.join("|", DROID_TYPES) + "|Near-Human)");
        speciesTypePattern = Pattern.compile("(" + GeneratedLists.SPECIES_TYPE.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")");
        traitTypePattern = Pattern.compile("(" + GeneratedLists.TRAITS.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")");
        templateTypePattern = Pattern.compile("(" + GeneratedLists.TEMPLATES.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")");
        ageTypePattern = Pattern.compile("(" + GeneratedLists.AGES.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")");

        populateTalentMappings();

        //List<JSONObject> overrides = getOverrides("units");

        List<String> exclusionByName = List.of();//getNames(overrides);


        List<JSONObject> entries = new UnitExporter().getEntriesFromCategoryPage(nonHeroicUnits, false, exclusionByName);
        entries.addAll(new UnitExporter().getEntriesFromCategoryPage(heroicUnits, false, exclusionByName));
        //entries.addAll(overrides);

        List<Integer> filter = List.of();//, 1, 2, 3, 4, 5 );//2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);//0, 1, 2, 3, 4);
        List<String> nameFilter = List.of();//"A9G-Series Archive Droid"); //"B1-Series Battle Droid Squad");

        for (Integer i :
                cls.stream()
                        .filter(cl -> filter.size() == 0 || filter.contains(cl))
                        .collect(Collectors.toList())) {
            List<JSONObject> filtered = entries.stream()
                    .filter(entry -> entry.getJSONObject("system").get("cl").equals(i))
                    .filter(entry -> nameFilter.size() == 0 || nameFilter.contains(entry.get("name")))
                    .collect(Collectors.toList());

            if(filtered.size() > 0){

                writeToJSON(new File(JSON_OUTPUT + i + ".json"), filtered, hasArg(args, "d"), "Units-CL-" + i, "Actor");
            }
        }


        System.out.println("processed " + entries.size() + " of 2277");

    }

    private static void setMaps() {
        ITEMS_BY_ALTERNATE_NAME = Maps.newHashMap();
        ITEMS_BY_ALTERNATE_NAME.put("Mandalorian Light Armor", "Neo-Crusader Light Armor");
    }


    protected List<JSONy> parseItem(String itemLink, boolean overwrite) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

//        if(!itemLink.endsWith("Replica_Droid")){
//            return List.of();
//        }

        Matcher variant = VARIANT_QUALIFIER.matcher(itemLink);
        String variantQualifier = "";
        if (variant.find()) {
            variantQualifier = " " + variant.group(1);
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }

        Element title = doc.select("h1.page-header__title").first();

        if (title == null || title.text().trim().equalsIgnoreCase("AAT-2")) {
            return new ArrayList<>();
        }

        List<Unit> items = new LinkedList<>();

        String itemName = title.text().trim();
//        if(!itemName.equals("Veteran Imperial Officer")){
//            return List.of();
//        }
        Unit current = Unit.create(itemName + variantQualifier);
        current.withLink(itemLink);
        items.add(current);

        Elements select = doc.select("div.mw-parser-output");
        Element first = select.first();

        boolean skipFinished = false;

        boolean isProtocolSection = false;
        for (Element cursor : first.children()) {
            boolean found = false;

            final String text = cursor.text();

            if ("See also: Protocol Format".equals(text)) {
                isProtocolSection = true;
            }

            if (isProtocolSection) {
                continue;
            }

                Matcher clMatcher = CL_PATTERN.matcher(text);
                if (clMatcher.find()) {
                    int cl = parseInt(clMatcher.group(1));
                    cls.add(cl);
                    current.withCL(cl);
                }

                if (sizeClassAndSpeciesLine(current, text, itemName)) {
                    continue;
                }

                if (text.startsWith("Possessions: ") && !skipFinished) {
                    handlePossessions(current, cursor);
                    continue;
                }

                if (text.startsWith("Affiliation") && !skipFinished) {
                    List<String> affiliations = List.of(text.split(": ")[1].split(", "));

                    for (String affiliation : affiliations) {
                        if (GeneratedLists.AFFILIATIONS.contains(affiliation)) {
                            current.withProvided(ProvidedItem.create(affiliation, ItemType.AFFILIATION));
                        } else {
                            //TODO there are missing affiliations
                            //printUnique("MISSING AFFILIATION: " + affiliation);
                        }
                    }
                    continue;
                }


                if (text.startsWith("Organization Score")) {
                    Matcher organizationScorePattern = ORGANIZATION_SCORE_PATTERN.matcher(text);

                    if (organizationScorePattern.find()) {
                        current.withOrganizationScore(organizationScorePattern.group(1), parseInt(organizationScorePattern.group(2)));
                        continue;
                    }


                    //printUnique("MISSING ORGANIZATION SCORE: " + current.getName() + " " + text);
                }

                if ((text.startsWith("Talents:") || text.startsWith("Talent:")) && !skipFinished) {

                    handleTalents(itemName, current, text, itemLink, cursor);
                    continue;
                }

                if (text.startsWith("Feats:") && !skipFinished) {

                    handleFeats(current, cursor);
                    continue;
                }

                if (text.startsWith("Force Power Suite") && !skipFinished) {
                    handleForcePowers(itemName, current, cursor);
                    continue;
                }

                if ((text.startsWith("Force Secrets:") || text.startsWith("Force Techniques:")) && !skipFinished) {
                    handleForceSecretsAndPowers(current, cursor);
                    continue;
                }

                if (text.startsWith("Force Regimens:")) {
                    handleRegimens(current, cursor);
                    continue;
                }

                if (text.startsWith("Droid Systems:")) {
                    handleDroidPart(current, cursor);
                    continue;
                }

                if(text.startsWith("Archive Processor")){
                    handleAction(current, cursor);
                }


                if (text.startsWith("Languages:")) {
                    handleLanguages(current, cursor);
                    continue;
                }

                if (text.contains("Availability:") || text.contains("Cost:")) {
                    Matcher m = AVAILABILITY_PATTERN.matcher(text);

                    if (m.find()) {
                        current.withAvailability(m.group(1));
                    }

                    Pattern COST_PATTERN = Pattern.compile("(?:Cost:|cost|value) ([\\d,]+) credits");
                    Matcher m1 = COST_PATTERN.matcher(text);

                    if (m1.find()) {
                        current.withCost(parseInt(m1.group(1).replace(",", "")));
                    }

                    if (text.toLowerCase().contains("not available") || text.toLowerCase().contains("not availible")) {
                        current.withIsForSale(false);
                    }
                    continue;
                }

                if (text.startsWith("Dark Side Score:") || text.startsWith("Faith Points:") || text.startsWith("Destiny Points:") || text.startsWith("Force Points:")) {
                    Matcher m = DARK_SIDE_PATTERN.matcher(text);
                    if (m.find()) {
                        current.withDarkSideScore(parseInt(m.group(1)));
                    }
                    continue;
                }

                if (text.startsWith("Occupation")) {

                    Matcher occupationMatcher = OCCUPATION_PATTERN.matcher(text);

                    if (occupationMatcher.find()) {
                        ProvidedItem providedItem = ProvidedItem.create(occupationMatcher.group(1), ItemType.BACKGROUND);
                        Matcher m = CLASS_SKILL_PATTERN.matcher(text);
                        if (m.find()) {
                            String classSkill = m.group(1).replace("As such ", "").replace("gaining ", "");
                            providedItem.withPayload(classSkill);
                        } else {
                            providedItem.withPayload("");
                        }
                        current.withProvided(providedItem);
                        continue;
                    }

                    printUnique("MISSING OCCUPATION: " + text);
                }

                if (text.startsWith("Event")) {

                    Pattern EVENT_PATTERN = Pattern.compile("Event \\((.*)\\):");
                    Matcher eventMatcher = EVENT_PATTERN.matcher(text);

                    if (eventMatcher.find()) {
                        ProvidedItem providedItem = ProvidedItem.create(eventMatcher.group(1), ItemType.BACKGROUND);
                        Matcher m = CLASS_SKILL_PATTERN.matcher(text);
                        if (m.find()) {
                            String classSkill = m.group(1).replace("As such ", "").replace("gaining ", "");
                            providedItem.withPayload(classSkill);
                        } else {
                            providedItem.withPayload("");
                        }
                        current.withProvided(providedItem);

                        continue;
                    }
                }

                if (text.startsWith("Planet of Origin")) {

                    Pattern PLANET_OF_ORIGIN_PATTERN = Pattern.compile("Planet of Origin \\((.*)\\):");
                    Matcher planetOfOriginMatcher = PLANET_OF_ORIGIN_PATTERN.matcher(text);


                    if (planetOfOriginMatcher.find()) {
                        ProvidedItem providedItem = ProvidedItem.create(planetOfOriginMatcher.group(1), ItemType.BACKGROUND);
                        Matcher m = CLASS_SKILL_PATTERN.matcher(text);

                        Pattern CLASS_SKILLS_PATTERN = Pattern.compile("(?:and adding|and adds|As such) (.*) and (.*) (?:to his list of|to her list of|are always considered) Class Skills");
                        Matcher m1 = CLASS_SKILLS_PATTERN.matcher(text);

                        if (m.find()) {
                            String classSkill = m.group(1).replace("As such ", "").replace("gaining ", "");

                            providedItem.withPayload(classSkill);
                            providedItem.withPayload("payload2", "");
                        } else if (m1.find()) {
                            providedItem.withPayload(m1.group(1));
                            providedItem.withPayload("payload2", m1.group(2));
                        } else {
                            providedItem.withPayload("");
                            providedItem.withPayload("payload2", "");
                        }
                        current.withProvided(providedItem);

                        continue;
                    }

                    printUnique("MISSING PLANET OF ORIGIN: " + text);
                }

                if (text.startsWith("Destiny")) {
                    Matcher destinyMatcher = DESTINY_PATTERN.matcher(text);

                    if (destinyMatcher.find()) {
                        ProvidedItem providedItem = ProvidedItem.create(destinyMatcher.group(2), ItemType.DESTINY);

                        providedItem.withPayload(String.valueOf(destinyMatcher.group(1) != null && destinyMatcher.group(1).equals("Fulfilled")));

                        current.withProvided(providedItem);

                        continue;
                    }
                    printUnique("MISSING DESTINY: " + text);
                }

                if (text.startsWith("Hit Points:")) {
                    Pattern HIT_POINT_PATTERN = Pattern.compile("Hit Points: (\\d+)");
                    Matcher hitPointMatcher = HIT_POINT_PATTERN.matcher(text);

                    if (hitPointMatcher.find()) {
                        current.withHitPoints(parseInt(hitPointMatcher.group(1)));
                        continue;
                    }

                    //printUnique("MISSING HITPOINTS: " + text);
                }
                if (text.startsWith("Abilities:")) {
                    boolean abilityFound = false;

                    Matcher strengthMatcher = STRENGTH_PATTERN.matcher(text);
                    if (strengthMatcher.find()) {
                        current.withAttribute("STRENGTH", strengthMatcher.group(1));
                        abilityFound = true;
                    }

                    Matcher dexterityMatcher = DEXTERITY_PATTERN.matcher(text);
                    if (dexterityMatcher.find()) {
                        current.withAttribute("DEXTERITY", dexterityMatcher.group(1));
                        abilityFound = true;
                    }

                    Matcher constitutionMatcher = CONSTITUTION_PATTERN.matcher(text);
                    if (constitutionMatcher.find()) {
                        current.withAttribute("CONSTITUTION", constitutionMatcher.group(1));
                        abilityFound = true;
                    }

                    Matcher intelligenceMatcher = INTELLIGENCE_PATTERN.matcher(text);
                    if (intelligenceMatcher.find()) {
                        current.withAttribute("INTELLIGENCE", intelligenceMatcher.group(1));
                        abilityFound = true;
                    }

                    Matcher wisdomMatcher = WISDOM_PATTERN.matcher(text);
                    if (wisdomMatcher.find()) {
                        current.withAttribute("WISDOM",  wisdomMatcher.group(1));
                        abilityFound = true;
                    }

                    Matcher charismaMatcher = CHARISMA_PATTERN.matcher(text);
                    if (charismaMatcher.find()) {
                        current.withAttribute("CHARISMA", charismaMatcher.group(1));
                        abilityFound = true;
                    }

                    if (!abilityFound) {
                        printUnique("MISSING ABILITIES: " + text);
                    }
                    continue;
                }


                if (text.startsWith("Skills:")) {

                    Matcher m = SKILL_PATTERN.matcher(text);

                    while (m.find()) {
                        current.withTrainedSkill(m.group(1));
                        //printUnique(m.group(1));
                    }
                    continue;
                }


                //possibly a validator


                if (text.startsWith("Species Traits:")) {
                    continue;
                }

                if (text.startsWith("Fighting Space:")) {

                    continue;
                }

                //validator values

                if (text.startsWith("Ranged:") || text.startsWith("Melee:") || text.startsWith("Attack Options:")) {

                    continue;
                }

                if (text.startsWith("Base Attack Bonus:")) {

                    continue;
                }

                if (text.startsWith("Reflex Defense:")) {

                    continue;
                }

                if (text.startsWith("Initiative:")) {

                    continue;
                }

                if (text.startsWith("Speed:")) {

                    continue;
                }

                if (text.startsWith("Swarm Attack:") || text.startsWith("Stench:") || text.startsWith("Scent:")
                        || text.startsWith("Poison:") || text.startsWith("Special Actions:") || text.startsWith("Fast Healing 5:")
                        || text.startsWith("Camouflage:") || text.startsWith("Banshee's Wail:") || text.startsWith("Ambush:")
                        || text.startsWith("Pounce:") || text.startsWith("Special:") || text.startsWith("Leg Shields:")) {

                    continue;
                }

                //ignore

                if (text.startsWith("Reference Book:") || text.startsWith("Homebrew Reference Book:") || text.startsWith("Immune:")
                        || text.startsWith("Species Traits") || text.startsWith("Contents")) {

                    continue;
                }

                //modifications
                if (text.startsWith("Modification:")) {
                    continue;
                }

                if (text.contains(":")) {
                    //printUnique(text.split(":")[0] + " : " + itemName);
                }


        }


        return new ArrayList<>(items);
    }

    private void handleAction(Unit current, Element cursor) {
        current.addAction(cursor.text());
    }

    private void handleForceSecretsAndPowers(Unit current, Element cursor) {
        String awaitingPayload = "";
        ItemType awaitingPayloadType = null;
        for (Element child : cursor.children()) {
            String secret = child.text();
            if (secret.startsWith("Force Secrets:") || secret.startsWith("Force Techniques:") || "".equals(secret)) {
                continue;
            }

            if (child.tag().equals(Tag.valueOf("i")) || (child.tag().equals(Tag.valueOf("a")) && child.children().size() > 0 && child.child(0).tag().equals(Tag.valueOf("i")))) {
                ProvidedItem providedItem = ProvidedItem.create(awaitingPayload, awaitingPayloadType);
                providedItem.withPayload(secret);
                current.withProvided(providedItem);
            } else if (secret.startsWith("Force Power Mastery")) {
                awaitingPayload = "Force Power Mastery";
                awaitingPayloadType = ItemType.FORCE_TECHNIQUE;
            } else if (GeneratedLists.SECRETS.contains(secret)) {
                current.withProvided(ProvidedItem.create(secret, ItemType.FORCE_SECRET));
            } else if (GeneratedLists.TECHNIQUES.contains(secret)) {
                current.withProvided(ProvidedItem.create(secret, ItemType.FORCE_TECHNIQUE));
            } else {
                printUnique(current.getName() + " SECRET: " + secret);
            }
        }
    }

    private void handleTalents(String itemName, Unit current, String text, String itemLink, Element cursor) {
        for (Element child : cursor.children()) {
            String talent = child.text();

            String answer = null;

            if ("Stolen Form".equals(talent)) {

                Element next = (Element) child.nextSibling().nextSibling();
                answer = next.text();
            }

            talent = talent.replace("Talents:", "").trim();


            if ("".equals(talent) || "Talents".equals(talent) || child.tag().equals(Tag.valueOf("i"))) {
                continue;
            }

            if (GeneratedLists.TALENTS.contains(talent)) {
                addTalent(current, talent, answer);
            } else {
                Matcher m = VALUE_AND_PAYLOADS.matcher(talent);
                if (m.find()) {
                    String talentName = m.group(1).trim();
                    if (GeneratedLists.TALENTS.contains(talentName)) {
                        ProvidedItem providedItem = ProvidedItem.create(talentName, ItemType.TALENT);
                        if (List.of("Coordinate", "Sneak Attack", "Demolitionist", "Lightsaber Defense", "Sentinel Strike", "Telekinetic Savant").contains(talentName)) {


                            Pattern value = Pattern.compile("\\+?(\\d)(?:d\\d)?");

                            Matcher m1 = value.matcher(m.group(2));

                            if (m1.find()) {

                                providedItem.withQuantity(m1.group(1));
                            } else {

                                printUnique("unparseable quantity: " + m.group(2));
                            }

                        } else {
                            providedItem.withPayload(m.group(2));
                        }

                        current.withProvided(providedItem);
                        continue;
                    }
                    if (DUPLICATE_TALENT_NAMES.contains(talentName)) {
                        String resolvedTalent = ITEM_TALENT_MAPPING.get(itemName, talentName);


                        if (resolvedTalent != null && !resolvedTalent.contains("|")) {
                            if (GeneratedLists.TALENTS.contains(resolvedTalent)) {
                                addTalent(current, resolvedTalent, answer);
                            } else {
                                printUnique(resolvedTalent);
                            }
                        } else {

                            List<String> possibleTalents = GeneratedLists.TALENTS.stream().filter(t -> t.startsWith(talentName)).collect(Collectors.toList());

                            printUnique("ITEM_TALENT_MAPPING.put(\"" + itemName + "\", \"" + talent + "\", \"" + String.join("|", possibleTalents) + "\");");
                            printUnique(++i + " https://swse.fandom.com" + itemLink + "      " + talent + " " + String.join("|", possibleTalents));
                        }
                        continue;
                    }
                    //TODO there are missing talents
                    //printUnique("MISSING TALENT1: " + itemName + " : " + talent + " : " + talentName);

                }
            }
        }
    }

    private void addTalent(Unit current, String talent, String answer) {

        ProvidedItem providedItem = ProvidedItem.create(talent, ItemType.TALENT);
        if (answer != null) {
            providedItem.withAnswers(List.of(answer));
        } else if ("Stolen Form".equals(talent)) {
            System.out.println(current.getName() + " " + current.getLink());
        }

        current.withProvided(providedItem);


    }

    private void handleForcePowers(String itemName, Unit current, Element cursor) {
        for (Element child : cursor.children()) {
            String forcePower = child.text();

            if (forcePower.startsWith("Force Power Suite")) {
                continue;
            }

            if (forcePower.endsWith(",")) {
                forcePower = forcePower.substring(0, forcePower.length() - 1);
            }


            Matcher m = VALUE_AND_PAYLOADS.matcher(forcePower);
            if (m.find()) {

                String group = m.group(1).trim();
                if (GeneratedLists.POWERS.contains(group)) {
                    ProvidedItem providedItem = ProvidedItem.create(forcePower, ItemType.FORCE_POWER);
                    String modifier = m.group(2);
                    if (modifier != null) {
                        providedItem.withQuantity(modifier);
                    }

                    current.withProvided(providedItem);
                    continue;
                }
            }

            if (GeneratedLists.FEATS.contains(forcePower) || GeneratedLists.TALENTS.contains(forcePower)) {
                continue;
            }

            printUnique(itemName + " : " + forcePower);
        }
    }

    private void handleFeats(Unit current, Element cursor) {
        for (Element child : cursor.children()) {
            String feat = child.text();
            String next = child.nextSibling().toString();
            if ("Feats:".equals(feat)) {
                continue;
            }
            next = next.replace(", ","").trim();

            feat = feat + next;
            if (GeneratedLists.FEATS.contains(feat)) {
                addFeat(current, feat, null);
                continue;
            }

            Matcher m = VALUE_AND_PAYLOADS.matcher(feat);
            if (!m.find()) {
                continue;
            }

            String featName = m.group(1).trim();
            String payload = m.group(2);
            if (isNumeric(payload) && GeneratedLists.FEATS.contains(featName)) {
                int count = Integer.parseInt(payload);

                for (int j = 0; j < count; j++) {
                    addFeat(current, featName, null);
                }

            }else if (GeneratedLists.FEATS.contains(featName)) {
                addFeat(current, featName, payload);

            } else {
                //printUnique("MISSING FEAT: " + current.getName() + " : " + feat + " : " + featName);
            }
        }
    }

    private void addFeat(Unit current, String featName, String payload) {
        ProvidedItem providedItem = ProvidedItem.create(featName, ItemType.FEAT);
        providedItem.withPayload(payload);

        if (featName.equals("Critical Strike")) {
            if (current.getName().startsWith("Chirrut")) {
                providedItem.withAnswers(Lists.newArrayList("Lightsabers"));
            } else {
                switch (current.getName()) {
                    case "Sith Lord (DMF)":
                    case "Jedi Guardian, Master":
                    case "Jedi Guardian, Knight":
                    case "Kirak Infil'a":
                    case "Meetra Surik, Aide of Revan":
                    case "Revan, Paragon":
                        providedItem.withAnswers(Lists.newArrayList("Lightsabers"));
                        break;
                    default:
                        System.out.println(current.getName() + " " + current.getLink());
                }
            }

        }

        if (featName.equals("Autofire Assault")) {
            switch (current.getName()) {
                case "Johana \"Valkyrie\" Forto":
                case "Aruk Enforcer":
                case "Clone Trooper Sergeant":
                case "Inquisitorius Purge Trooper":
                case "Bric":
                case "Dred Priest":
                case "Llats Ward":
                    providedItem.withAnswers(Lists.newArrayList("Rifles"));
                    break;
                case "Cydon Prax":
                case "Tobbi Dala":
                    providedItem.withAnswers(Lists.newArrayList("Heavy Weapons"));
                    break;

                default:
                    System.out.println(current.getName() + " " + current.getLink());
                    System.out.println("case \"" + current.getName() + "\":");
                    System.out.println("providedItem.withAnswers(Lists.newArrayList(\"Rifles\"));");
                    System.out.println("break;");
            }

        }

        if (featName.equals("Halt")) {
            switch (current.getName()) {
                case "Matukai Grand Master":
                    providedItem.withAnswers(Lists.newArrayList("Simple Weapons"));
                    break;
                case "Cort Davin":
                    providedItem.withAnswers(Lists.newArrayList("Pistols"));
                    break;
                case "Jastus Farr":
                    providedItem.withAnswers(Lists.newArrayList("Lightsabers"));
                    break;
                default:
                    System.out.println(current.getName() + " " + current.getLink());
                    System.out.println("case \"" + current.getName() + "\":");
                    System.out.println("providedItem.withAnswers(Lists.newArrayList(\"Rifles\"));");
                    System.out.println("break;");
            }

        }

        if (featName.equals("Autofire Sweep")) {
            switch (current.getName()) {
                case "Aruk Enforcer":
                case "Trandoshan Mercenary (DMF)":
                case "Bric":
                case "Dred Priest":
                case "Llats Ward":
                case "Tobbi Dala":
                    providedItem.withAnswers(Lists.newArrayList("Rifles"));
                    break;
                case "Mandalorian Heavy Soldier":
                case "Resistance Gunner":
                case "TX-1138 \"Terminax\" Assassin Droid":
                case "BT-1":
                    providedItem.withAnswers(Lists.newArrayList("Heavy Weapons"));
                    break;


                default:
                    System.out.println(current.getName() + " " + current.getLink());
                    System.out.println("case \"" + current.getName() + "\":");
                    System.out.println("providedItem.withAnswers(Lists.newArrayList(\"Rifles\"));");
                    System.out.println("break;");
            }

        }
        if (featName.equals("Savage Attack")) {
            switch (current.getName()) {
                case "Anzati Master Assassin":
                    providedItem.withAnswers(Lists.newArrayList("Advanced Melee Weapons"));
                    break;
                case "Elite Shadow Guard":
                case "Trenox":
                case "Sora Bulq":
                case "Sai Sircu":
                case "Kirak Infil'a":
                    providedItem.withAnswers(Lists.newArrayList("Lightsabers"));
                    break;
                case "Bric":
                case "Mahirkyyr":
                    providedItem.withAnswers(Lists.newArrayList("Rifles"));
                    break;
                case "Jace Malcom":
                    providedItem.withAnswers(Lists.newArrayList("Heavy Weapons"));
                    break;


                default:
                    System.out.println(current.getName() + " " + current.getLink());
                    System.out.println("case \"" + current.getName() + "\":");
                    System.out.println("providedItem.withAnswers(Lists.newArrayList(\"Rifles\"));");
                    System.out.println("break;");
            }

        }


        current.withProvided(providedItem);
    }


    private void handleRegimens(Unit current, Element cursor) {
        for (Element child : cursor.children()) {
            String text = child.text().trim();

            if (text.contains("Force Regimens:")) {
                continue;
            }

            if (GeneratedLists.FORCE_REGIMENS.contains(text)) {
                current.withProvided(ProvidedItem.create(text, ItemType.FORCE_REGIMEN));
                continue;
            }

            printUnique("MISSING FORCE REGIMEN: " + current.getName() + " : " + text);
        }
    }

    private void handleDroidPart(Unit current, Element cursor) {
        for (String text : cursor.text().split(",(?![^()]*\\))")) {

            text = text.replace("Droid Systems:", "");
            text = text.trim();
            if (text.isBlank()) {
                continue;
            }


            if ("Ion Shield Generator (SR 15 (vs Ion))".equals(text)) {
                text = "Ion Shield Generator (SR 15)";
            }

            if (GeneratedLists.ITEMS.contains(text)) {
                current.withProvided(ProvidedItem.create(text, ItemType.ITEM).withEquip("equipped"));
                continue;
            }

            if (text.contains(" with ")) {
                String[] toks = text.split(" with ");
                if (GeneratedLists.ITEMS.contains(toks[0])) {
                    ProvidedItem providedItem = ProvidedItem.create(toks[0], ItemType.ITEM).withEquip("equipped");
                    if (GeneratedLists.ITEMS.contains(toks[1])) {
                        providedItem.withProvided(Modification.create(ProvidedItem.create(toks[1], ItemType.ITEM)));
                    }
                    current.withProvided(providedItem);
                    continue;
                }
            }


            Matcher m = APPENDAGES.matcher(text);

            if (m.find()) {
                int count = 1;
                String num = m.group(1);
                String modifierString = m.group(3);

                Map<String, Integer> modifiers = new HashMap<>();
                String suffix = null;
                String special = null;

                if (modifierString != null && !"".equals(modifierString)) {
                    modifierString = modifierString.trim();
                    if (modifierString.startsWith("(") && modifierString.endsWith(")")) {
                        modifierString = modifierString.substring(1, modifierString.length() - 1);
                    }

                    for (String mod : modifierString.split(", ")) {
                        Matcher m1 = DROID_SYSTEMS_PATTERN.matcher(mod);
                        if (m1.find()) {
                            String modifierCount = m1.group(1);
                            int mCount = -1;
                            if (modifierCount != null && !"".equals(modifierCount)) {
                                mCount = parseInt(modifierCount);
                            }
                            String mType = m1.group(2);
                            String modifierType = null;
                            if (mType.contains("Telescopic")) {
                                modifierType = "Telescopic Appendage";
                            } else if (mType.contains("Gyro-Stabilized")) {
                                modifierType = "Gyro-Stabilized Appendage";
                            } else if (mType.contains("Stabilized")) {
                                modifierType = "Stabilized Appendage";
                            } else if (mType.contains("Magnetic Hands")) {
                                modifierType = "Magnetic Hands";
                            } else if (mType.contains("Concealed Item")) {
                                modifierType = "Concealed Item (Droid Accessory)";
                            } else if (mType.contains("F-187 Fusion Cutter")
                                    || mType.contains("Power Recharger")
                                    || mType.contains("Limited")
                                    || mType.contains("Exclusive")
                                    || mType.contains("Multifunction Apparatus")) {
                                modifierType = mType;
                            } else {
                                printUnique("FUCK " + mType);
                            }

                            modifiers.put(modifierType, mCount);
                        } else if (List.of("Bite", "Claw", "Claws", "Legs may be used as Hands", "Retractable").contains(mod)) {
                            suffix = mod.equals("Claws") ? "Claw" : mod;
                        } else if (List.of("Rail").contains(mod)) {
                            special = mod;
                        } else {

                            printUnique(current.getName() + " |" + mod + "|");
                        }
                    }


                }

                if (num != null && !"".equals(num)) {
                    count = parseInt(num);
                }

                String itemName = m.group(2);
                for (int i = 0; i < count; i++) {
                    ProvidedItem providedItem = ProvidedItem.create(itemName, ItemType.ITEM).withEquip("equipped");
                    if (suffix != null) {
                        providedItem.withProvided(Change.create(ChangeKey.SUFFIX, suffix));
                    }
                    if (special != null) {
                        providedItem.withProvided(Change.create(ChangeKey.SPECIAL, special));
                    }
                    modifiers.forEach((String mn, Integer c) -> {
                        if (c != 0) {
                            providedItem.withProvided(Modification.create(ProvidedItem.create(mn, ItemType.ITEM)));
                            modifiers.put(mn, --c);
                        }
                    });
                    current.withProvided(providedItem);
                }

                continue;
            }


            Pattern payload = Pattern.compile("([\\w\\s-]*) \\(([\\w\\s-+,]*)\\)");
            Matcher pm = payload.matcher(text);
            if (pm.find()) {

                String base = pm.group(1);

                if (base.equals("Internal Storage")) {
                    base = "Compartment Space";
                }
                if (base.equals("Concealed Item")) {
                    base = "Concealed Item (Droid Accessory)";
                }
                if (base.equals("Demolition Sensor")) {
                    base = "Demolitions Sensor";
                }

                if (GeneratedLists.ITEMS.contains(base)) {
                    ProvidedItem providedItem = ProvidedItem.create(base, ItemType.ITEM);
                    providedItem.withProvided(Change.create(ChangeKey.PAYLOAD, pm.group(2)));
                    current.withProvided(providedItem);
                    continue;
                }
                printUnique("MISSING DROID PART: " + current.getName() + " : " + text);
            }

        }
    }

    private void handleLanguages(Unit current, Element cursor) {
        for (String text : cursor.text().split(":|,|;|\\.(?![^()]*\\))")) {
            text = text.trim();

            if (text.equals("Languages")|| text.startsWith("Translator Unit")) {
                continue;
            }

            Pattern LANGUAGE_PATTERN = Pattern.compile("^(\\d*) ?([\\w\\s'-]*) ?\\(?([\\w\\s]*)\\)?$");

            Matcher m = LANGUAGE_PATTERN.matcher(text);

            if (m.find()) {
                String countString = m.group(1);
                int count = parseInt(countString == null || "".equals(countString) ? "1" : countString);
                String languageName = m.group(2).trim();
                String modifier = m.group(3);

                for (int i = 0; i < count; i++) {
                    ProvidedItem providedItem = ProvidedItem.create(languageName, ItemType.LANGUAGE);
                    if (modifier != null && !modifier.isBlank()) {
                        modifier = modifier.trim();
                        providedItem.withProvided(Change.create(ChangeKey.PAYLOAD, modifier));

                    }

                    current.withProvided(providedItem);
                }
                //printUnique(countString + " " + m.group(2));
                continue;
            }
            if ("N/A".equals(text)) {
                continue;
            }


            printUnique("MISSING LANGUAGE: " + current.getName() + " : |" + text + "|");
        }
    }


    private static void populateTalentMappings() {
        ITEM_TALENT_MAPPING.put("A-Series Assassin Droid", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Byss Elite Stormtrooper", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Byss Elite Stormtrooper Squad", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Elite Warrior", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("SpecForce Elite Soldier", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Trandoshan Sergeant", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Wheel Security", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mahirkyyr", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Rath Kelkko", "Multiattack Proficiency (Advanced Melee Weapons)", "Multiattack Proficiency (Advanced Melee Weapons) (Melee Duelist Talent Tree)");
        ITEM_TALENT_MAPPING.put("Simon the Killer Ewok", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Yoda, Jedi Paragon", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Ace Starfighter Pilot", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Aqualish Bodyguard", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("ARC Trooper Captain", "Ambush", "Ambush (Republic Commando Talent Tree)");
        ITEM_TALENT_MAPPING.put("Beastmaster", "Charm Beast", "Charm Beast (Beastwarden Talent Tree)");
        ITEM_TALENT_MAPPING.put("Black Sun Vigo", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Bounty Hunter, Veteran", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("Clawdite Freelance Spy", "Blend In", "Blend In (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Clone Officer", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Crime Boss", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Crime Lord", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dark Jedi Master", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Witch", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Witch", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Witch", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Elite Senate Guard", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Failed Jedi", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Felucian High Shaman", "Charm Beast", "Charm Beast (Felucian Shaman Talent Tree)");
        ITEM_TALENT_MAPPING.put("Gamorrean Boss", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Gand Huntsman", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Hutt Crime Lord", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Imperial Knight", "Armor Mastery", "Armor Mastery (Knight's Armor Talent Tree)");
        ITEM_TALENT_MAPPING.put("Imperial Knight", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("ISB Stormtrooper", "Lead by Example", "Lead by Example (Ideologue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jedi General", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jedi Healer", "Force Treatment", "Force Treatment (Jedi Healer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jedi Pathfinder", "Charm Beast", "Charm Beast (Beastwarden Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jedi Pathfinder", "Force Treatment", "Force Treatment (Jedi Healer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jedi Sentinel, Knight", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jedi Sentinel, Master", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jedi Sentinel, Padawan", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jedi Wanderer", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mandalorian Commander", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mandalorian Supercommando", "Armor Mastery", "Armor Mastery (Armor Specialist Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mandalorian Warrior", "Ruthless", "Ruthless (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mercenary Soldier", "Combined Fire", "Combined Fire (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nightsister Force Witch", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nightsister Force Witch", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nightsister of Dathomir", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nightsister of Dathomir", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nihil Strike", "Ambush", "Ambush (Disgrace Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nikto Guardian", "Out of Harm's Way", "Out of Harm's Way (Commando Talent Tree)");
        ITEM_TALENT_MAPPING.put("Noghri Bodyguard", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Recruitment Agent", "Blend In", "Blend In (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Resistance Agent", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Resistance Leader", "Lead by Example", "Lead by Example (Ideologue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Rodian Black Sun Vigo", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sable Dawn Assassin", "Ruthless", "Ruthless (Assassin Talent Tree)");
        ITEM_TALENT_MAPPING.put("Shadow Academy Student, Senior", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Shinie Clone Commando", "Ambush", "Ambush (Republic Commando Talent Tree)");
        ITEM_TALENT_MAPPING.put("Theelin Bodyguard", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Togorian Enforcer", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Trandoshan Bodyguard", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Veteran Clone Commando", "Ambush", "Ambush (Republic Commando Talent Tree)");
        ITEM_TALENT_MAPPING.put("Wookiee Brawler", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Yuzzem Brute", "Ruthless", "Ruthless (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Abeloth", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Adi Gallia", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Admiral Stazi", "Combined Fire", "Combined Fire (Naval Officer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Alkhara", "Blend In", "Blend In (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Alkhara", "Seize the Moment", "Seize the Moment (Outlaw Talent Tree)");
        ITEM_TALENT_MAPPING.put("Andren Biel", "Blend In", "Blend In (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Antares Draco", "Armor Mastery", "Armor Mastery (Knight's Armor Talent Tree)");
        ITEM_TALENT_MAPPING.put("Antares Draco", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Atlee Thanda", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Avan Post", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Avan Post", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Bannamu", "Blend In", "Blend In (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Belia Darzu", "Sith Alchemy", "Sith Alchemy (Sith Alchemy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Birok", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Blackhole", "Sith Alchemy", "Sith Alchemy (Sith Alchemy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Booster Terrik", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Bossk, Bounty Hunter", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Cad Bane", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("Calo Nord", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("Carth Onasi", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Cato Parasitti", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Caudle", "Force Treatment", "Force Treatment (Jedi Healer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Cleaver", "Slip By", "Slip By (Camouflage Talent Tree)");
        ITEM_TALENT_MAPPING.put("Cloud \"Slinky\" Wii'Tuc", "Armor Mastery", "Armor Mastery (Armor Specialist Talent Tree)");
        ITEM_TALENT_MAPPING.put("Cloud \"Slinky\" Wii'Tuc", "Ruthless", "Ruthless (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Conn Doruggan", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Corran Horn, Grand Master", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Count Dooku, Darth Tyranus", "Mobile Combatant", "Mobile Combatant (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dace Diath", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dal Perhi", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Darth Andeddu, Dark Side Spirit", "Sith Alchemy", "Sith Alchemy (Sith Alchemy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Darth Caedus", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Darth Plagueis", "Sith Alchemy", "Sith Alchemy (Sith Alchemy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Darth Sidious, Supreme Chancellor", "Sith Alchemy", "Sith Alchemy (Sith Talent Tree)");
        ITEM_TALENT_MAPPING.put("Derek \"Hobbie\" Klivian", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dob and Del Moomo", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dool Pundar", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dougan Filmore Baccus", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Drevveka Hoctu", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dryden Vos", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Embo", "Mobile Combatant", "Mobile Combatant (Advance Patrol Talent Tree)");
        ITEM_TALENT_MAPPING.put("Empress Marasiah Fel", "Armor Mastery", "Armor Mastery (Knight's Armor Talent Tree)");
        ITEM_TALENT_MAPPING.put("Enric Pryde", "Combined Fire", "Combined Fire (Naval Officer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Exar Kun", "Sith Alchemy", "Sith Alchemy (Sith Talent Tree)");
        ITEM_TALENT_MAPPING.put("Exar Kun, Dark Side Spirit", "Sith Alchemy", "Sith Alchemy (Sith Talent Tree)");
        ITEM_TALENT_MAPPING.put("Faltun Garr", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Finn, Strike Commander", "Slip By", "Slip By (Camouflage Talent Tree)");
        ITEM_TALENT_MAPPING.put("Flax'Supt'ai", "Force Treatment", "Force Treatment (Force Adept Talent Tree)");
        ITEM_TALENT_MAPPING.put("Gaff", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Garik \"Face\" Loran", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Geith Eris", "Lead by Example", "Lead by Example (Ideologue Talent Tree)");
        ITEM_TALENT_MAPPING.put("General Hux", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Gha Nachkt", "Keep it Together", "Keep it Together (Fringer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Ghez Hokan", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Goomi", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Grakkus the Hutt", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Han Solo", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Han Solo, Galactic Hero", "Combined Fire", "Combined Fire (Naval Officer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Han Solo, Galactic Hero", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Han Solo, Stormtrooper Armor", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Harll", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("HK-80 \"H-Katie\"", "Out of Harm's Way", "Out of Harm's Way (Protection Talent Tree)");
        ITEM_TALENT_MAPPING.put("Hondo Ohnaka", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Hurnoj Arqu'uthun", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Inquisitor Jorad", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jacen Solo", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jaius Yorub", "Combined Fire", "Combined Fire (Naval Officer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jango Fett", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jig'Lullubullulul \"Jiggles\"", "Seize the Moment", "Seize the Moment (Provocateur Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jon Antilles", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jona Grumby", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Jorj Car'das", "Ambush", "Ambush (Disgrace Talent Tree)");
        ITEM_TALENT_MAPPING.put("Joruus C'Baoth", "Force Meld", "Force Meld (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Juno Eclipse", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kadrian Sey", "Mobile Combatant", "Mobile Combatant (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kal Skirata", "Ambush", "Ambush (Republic Commando Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kal Skirata", "Blend In", "Blend In (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kar Vastor", "Force Treatment", "Force Treatment (Force Adept Talent Tree)");
        ITEM_TALENT_MAPPING.put("Karnak Tetsu", "Sith Alchemy", "Sith Alchemy (Sith Alchemy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kiaarie Starwatt", "Mobile Combatant", "Mobile Combatant (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kir Kanos", "Armor Mastery", "Armor Mastery (Armor Specialist Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kossak the Hutt", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kueller", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Lanoree Brock", "Sith Alchemy", "Sith Alchemy (Sith Alchemy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Leia Organa Solo, Ex-Chief of State", "Lead by Example", "Lead by Example (Ideologue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Leia Organa, General", "Lead by Example", "Lead by Example (Ideologue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Liash Keane", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Llats Ward", "Combined Fire", "Combined Fire (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Llats Ward", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Luminara Unduli", "Force Treatment", "Force Treatment (Jedi Healer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Lyshaa", "Ruthless", "Ruthless (Assassin Talent Tree)");
        ITEM_TALENT_MAPPING.put("Maarek Stele", "Mobile Combatant", "Mobile Combatant (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mara Jade, Jedi", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Marasiah Fel, Imperial Knight", "Armor Mastery", "Armor Mastery (Knight's Armor Talent Tree)");
        ITEM_TALENT_MAPPING.put("Maris Brood", "Charm Beast", "Charm Beast (Felucian Shaman Talent Tree)");
        ITEM_TALENT_MAPPING.put("Maris Brood", "Command Beast", "Command Beast (Felucian Shaman Talent Tree)");
        ITEM_TALENT_MAPPING.put("Maris Brood, Padawan", "Charm Beast", "Charm Beast (Felucian Shaman Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mathal", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Maul", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mij Gilamar", "Ambush", "Ambush (Republic Commando Talent Tree)");
        ITEM_TALENT_MAPPING.put("Misha Vekkian", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Moff Gideon", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Mother Talzin", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nahdar Vebb", "Force Treatment", "Force Treatment (Force Adept Talent Tree)");
        ITEM_TALENT_MAPPING.put("Natasi Daala", "Stay in the Fight", "Stay in the Fight (Fugitive Commander Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nazzer", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nien Nunb", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Odumin", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Oti'eno", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Padta Greel", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Plo Koon", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Ploovo Two-for-One", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Prince Xizor", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Prit Kessek", "Blend In", "Blend In (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Rath Kelkko, Renegade", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Rav", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Relli Likkec", "Force Treatment", "Force Treatment (Force Adept Talent Tree)");
        ITEM_TALENT_MAPPING.put("Rian Bruksah", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Ros Lai", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Rose Tico, Commander", "Stay in the Fight", "Stay in the Fight (Fugitive Commander Talent Tree)");
        ITEM_TALENT_MAPPING.put("Rulf Yage", "Keep it Together", "Keep it Together (Expert Pilot Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saato", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saato", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saato", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saato, Dark Acolyte", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saato, Dark Acolyte", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saato, Dark Acolyte", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sadras Koyan", "Get Into Position", "Get Into Position (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saesee Tiin", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sage of the Southern Wilds", "Charm Beast", "Charm Beast (Beastwarden Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sai Sircu", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sai Sircu", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sai Sircu", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sar Omant", "Combined Fire", "Combined Fire (Naval Officer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sar Omant", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saw Gerrera", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Saw Gerrera", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sev'Rance Tann", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Shaak Ti, Jedi General", "Charm Beast", "Charm Beast (Felucian Shaman Talent Tree)");
        ITEM_TALENT_MAPPING.put("Shaak Ti, Jedi General", "Command Beast", "Command Beast (Felucian Shaman Talent Tree)");
        ITEM_TALENT_MAPPING.put("Shahan Alama", "Ruthless", "Ruthless (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Shimrra Jamaane, Supreme Overlord", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Shoaneb Culu", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sisla", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("Solvek", "Notorious", "Notorious (Bounty Hunter Talent Tree)");
        ITEM_TALENT_MAPPING.put("Spar", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Stass Allie", "Force Treatment", "Force Treatment (Jedi Healer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Stroth", "Mobile Combatant", "Mobile Combatant (Advance Patrol Talent Tree)");
        ITEM_TALENT_MAPPING.put("Supreme Chancellor Saresh", "Lead by Example", "Lead by Example (Ideologue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Tae Diath", "Force Meld", "Force Meld (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Taeon Skywalker", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Tamith Kai", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Tamith Kai", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Tenel Ka Djo", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Tenth Brother", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("The Daughter", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("The Father", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("The Son", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Thrawn, Grand Admiral", "Combined Fire", "Combined Fire (Naval Officer Talent Tree)");
        ITEM_TALENT_MAPPING.put("Thrawn, Grand Admiral", "Commanding Presence", "Commanding Presence (Leadership Talent Tree)");
        ITEM_TALENT_MAPPING.put("Ti'con Oro", "Get Into Position", "Get Into Position (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Ti'con Oro", "Master Manipulator", "Master Manipulator (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Tott Doneeta", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Trask Ulgo", "Lead by Example", "Lead by Example (Ideologue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Tyber Zann", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Valeska", "Force Treatment", "Force Treatment (Force Adept Talent Tree)");
        ITEM_TALENT_MAPPING.put("Valk Alon", "Get Into Position", "Get Into Position (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Valk Alon", "Master Manipulator", "Master Manipulator (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Vergere", "Force Treatment", "Force Treatment (Force Adept Talent Tree)");
        ITEM_TALENT_MAPPING.put("Vice Admiral Holdo", "Ambush", "Ambush (Disgrace Talent Tree)");
        ITEM_TALENT_MAPPING.put("Walon Vau", "Ambush", "Ambush (Republic Commando Talent Tree)");
        ITEM_TALENT_MAPPING.put("Wumdi", "Notorious", "Notorious (Infamy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Xiaan Amersu", "Lead by Example", "Lead by Example (Ideologue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Yansu Grjak", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Yansu Grjak", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Yansu Grjak", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Zephata'ru'tor", "Mobile Combatant", "Mobile Combatant (Jedi Guardian Talent Tree)");


        ITEM_TALENT_MAPPING.put("ARC Trooper Captain", "Multiattack Proficiency (Rifles)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Beastwarden", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Beastwarden", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Beastwarden", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Beastwarden Clan Mother", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Beastwarden Clan Mother", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Clan Mother", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Clan Mother", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Dathomiri Clan Mother", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Elite Clone Commando", "Ambush", "Ambush (Republic Commando Talent Tree)");
        ITEM_TALENT_MAPPING.put("Endorian Chieftain", "Get Into Position", "Get Into Position (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Endorian Chieftain", "Master Manipulator", "Master Manipulator (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Endorian Commander", "Commanding Presence", "Commanding Presence (Mercenary Talent Tree)");
        ITEM_TALENT_MAPPING.put("Endorian Outcast", "Seize the Moment", "Seize the Moment (Outlaw Talent Tree)");
        ITEM_TALENT_MAPPING.put("Endorian Steward", "Get Into Position", "Get Into Position (Master of Intrigue Talent Tree)");
        ITEM_TALENT_MAPPING.put("Nightsister Shaman", "Force Treatment", "Force Treatment (Force Adept Talent Tree)");
        ITEM_TALENT_MAPPING.put("Order of Shasa Adept", "Charm Beast", "Charm Beast (Beastwarden Talent Tree)");
        ITEM_TALENT_MAPPING.put("Raining Leaves Clan Mother", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Raining Leaves Clan Mother", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Raining Leaves Warrior", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Singing Mountain Sentry", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Singing Mountain Sentry", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Singing Mountain Sentry", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sister of the Voritor", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Sister of the Voritor", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Arligan Zey", "Seize the Moment", "Seize the Moment (Provocateur Talent Tree)");
        ITEM_TALENT_MAPPING.put("Charal the Witch-Queen", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Ez Kor Im", "Force Intuition", "Force Intuition (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Gwynanya Djo", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Gwynanya Djo", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Gwynanya Djo", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Iri Camas", "Mobile Combatant", "Mobile Combatant (Jedi Guardian Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kyrisa", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kyrisa", "Charm Beast", "Charm Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Kyrisa", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Roan Shryne", "Blend In", "Blend In (Spy Talent Tree)");
        ITEM_TALENT_MAPPING.put("Selkath, Rancor Warden Clan Mother", "Adept Spellcaster", "Adept Spellcaster (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Selkath, Rancor Warden Clan Mother", "Command Beast", "Command Beast (Dathomiri Witch Talent Tree)");
        ITEM_TALENT_MAPPING.put("Shae Pishifta", "Blend In", "Blend In (Spy Talent Tree)");
        //Vokara Che : Force Power Mastery
        ITEM_TALENT_MAPPING.put("Vokara Che", "Force Treatment", "Force Treatment (Force Adept Talent Tree)");

    }

    private void handlePossessions(Unit current, Element cursor) {
        String text = cursor.text();


StringBuilder buffered = new StringBuilder();
        List<String> possessions = getPossessions(text);
        Map<String, String> linkByPosession = getLinksFromElements(cursor.children());
        for (String possession : possessions) {
            String trim = possession.trim();
            if("Possessions:".equals(trim)){
                continue;
            }

            if(buffered.length() > 0){
                buffered.append(trim);
                trim = buffered.toString();
                printUnique("RESOLVED ITEM: "+ trim);
                buffered = new StringBuilder();
            }

            String partial = getPartial(possession);
            if(partial != null){
                buffered.append(partial);
                continue;
            }

            Matcher m = VALUE_AND_PAYLOADS.matcher(trim);

            if (!m.find()) {
                continue;
            }

            String item = mapItemByName(m.group(1).trim());
            String modifier = m.group(2) == null ? "" : m.group(2).trim();
            String nameOverride = null;

            if (modifier.toLowerCase().startsWith("as ")) {
                nameOverride = item;
                item = modifier.substring(3);
            }

            if (!GeneratedLists.ITEMS.contains(item)) {
                if (item.contains(" with ")) {
                    possessionWith(current, item, nameOverride);
                } else if (item.toLowerCase().contains("credit")) {
                    Pattern CREDITS = Pattern.compile("(\\d+) (Credits|credits|unmarked Credits)");

                    Matcher m1 = CREDITS.matcher(item);
                    if (m1.find()) {
                        ProvidedItem providedItem = ProvidedItem.create("Credit Chip", ItemType.ITEM);
                        providedItem.withProvided(Change.create(ChangeKey.CREDIT, m1.group(1)));
                        current.withProvided(providedItem);
                    } else {
                        try {
                            if (!"".equals(modifier) && parseInt(modifier) > 0) {
                                ProvidedItem providedItem = ProvidedItem.create("Credit Chip", ItemType.ITEM);
                                providedItem.withProvided(Change.create(ChangeKey.CREDIT, modifier));
                                current.withProvided(providedItem);
                            } else if (item.equals("Multiple Credit Chips") || item.equals("Thousands of Credits")) {
                                ProvidedItem providedItem = ProvidedItem.create("Credit Chip", ItemType.ITEM);
                                providedItem.withProvided(Change.create(ChangeKey.CREDIT, "8d100"));
                                providedItem.withQuantity("1d4");
                                current.withProvided(providedItem);
                            } else if (item.equals("Credits") || item.equals("Credits for Strong Drinks")) {
                                ProvidedItem providedItem = ProvidedItem.create("Credit Chip", ItemType.ITEM);
                                providedItem.withProvided(Change.create(ChangeKey.CREDIT, "8d6"));
                                current.withProvided(providedItem);
                            } else {
                                //printUnique("MISSING ITEM: " + item + " : " + modifier + " : " + current.getName());
                            }
                        } catch (NumberFormatException e) {
                            //it wasn't a number
                        }
                    }
                } else {
//                    String link = linkByPosession.get(item);
//                    if(link != null){
//                        Document doc = getDoc(link, false);
//
//                        String itemName = "";
//                        try {
//                            itemName = getItemName(doc);
//                        } catch (Exception e) {
//                            //this is fine
//                        }
//
//                        printUnique("LINKED NAME: " + itemName);
//                    }

                    //TODO There are missing items
                    printUnique("MISSING ITEM: " +item + " : " + modifier + " : " + current.getName());
                }

            } else {

                ProvidedItem providedItem = ProvidedItem.create(item, ItemType.ITEM).withEquip("equipped");
                if (nameOverride != null) {
                    providedItem.withCustomName(nameOverride);
                }
                current.withProvided(providedItem);
            }
        }
    }

    private Map<String, String> getLinksFromElements(Elements children) {
        Map<String, String> response = Maps.newHashMap();

        for (Element e :
                children) {
            if(e.hasAttr("href")){
                response.put(e.text().trim(), ROOT + e.attr("href"));
            }
        }
        return response;
    }

    private String getPartial(String possession) {

        List<String> partials = Lists.newArrayList("Clone Trooper Armor");
        if(partials.contains(possession)){
            return possession + ", ";
        }

        return null;
    }

    private List<String> getPossessions(String text) {
//        if(text.contains(";")){
//            printUnique("SEMICOLON FOUND: " + text);
//            return List.of(text.substring(13).split(";(?![^(),]*+\\))"));
//        }
        return List.of(text.substring(13).split("(?:,|;)(?![^()]*+\\))"));
    }

    private String mapItemByName(String item) {
        String actual = ITEMS_BY_ALTERNATE_NAME.get(item);
        if(actual!=null){
            return actual;
        }
        return item;
    }

    private void possessionWith(Unit current, String item, String nameOverride) {
        String[] split = item.split(" with ");

        if (GeneratedLists.ITEMS.contains(split[0])) {
            ProvidedItem provided = ProvidedItem.create(split[0], ItemType.ITEM).withEquip("equipped");

            if (nameOverride != null) {
                provided.withCustomName(nameOverride);
            }

            current.withProvided(provided);


            for (String modifier : List.of(split[1].split(" and "))) {

                Pattern ITEM_WITH_QUANTITY = Pattern.compile("([\\dd]+)?(?: )?([\\w\\s]+)");

                Matcher quantity = ITEM_WITH_QUANTITY.matcher(modifier);

                if (quantity.find()) {
                    String one = quantity.group(1);
                    String two = quantity.group(2);

                    if (two.equalsIgnoreCase("credits")) {
                        provided.withProvided(Change.create(ChangeKey.CREDIT, one));
                    } else {
                        if (!GeneratedLists.ITEMS.contains(two) && !GeneratedLists.ITEMS.contains(two.substring(0, two.length() - 1))) {
                            ProvidedItem providedItem;
                            if (one != null) {
                                providedItem = ProvidedItem.create(two.substring(0, two.length() - 1), ItemType.ITEM);
                                providedItem.withQuantity(one);
                            } else {
                                providedItem = ProvidedItem.create(two, ItemType.ITEM);
                            }
                            current.withProvided(providedItem);
                        }
                    }
                } else {
                    System.err.println("QUANTITY NOT FOUND: " + modifier);
                }
            }
        }
    }

    private boolean sizeClassAndSpeciesLine(Unit current, String text, String itemName) {
        if (startsWithOneOf(GeneratedLists.sizes, text) && !text.startsWith("Small Appendages:") && !text.startsWith("Small teams of Soldiers")) {
            List<String> children = List.of(text.split(" "));

            String size = getUnitSize(children);

            if (size != null) {
                current.withSize(size);
            }

            Matcher m = classPattern.matcher(text);

            List<Pair<String,String>> classes = new ArrayList<>();

            boolean isBeast = false;

            while (m.find()) {
                String level = m.group(2);
                if (level == null) {
                    level = "1";
                }
                classes.add(Pair.of(m.group(1).trim(), level));
                if("Beast".equals(m.group(1).trim())){
                    isBeast = true;
                }
            }

            m = speciesPattern.matcher(text);

            String species = null;
            List<String> speciesAnswers = new ArrayList<>();
            speciesAnswers.add(getSizeAnswer(size));
            if (m.find()) {
                species = m.group(1);

                switch (species) {
                    case "1st-Degree Droid":
                        species = "1st-Degree Droid Model";
                        break;
                    case "2nd-Degree Droid":
                        species = "2nd-Degree Droid Model";
                        break;
                    case "3rd-Degree Droid":
                        species = "3rd-Degree Droid Model";
                        break;
                    case "4th-Degree Droid":
                        species = "4th-Degree Droid Model";
                        break;
                    case "5th-Degree Droid":
                        species = "5th-Degree Droid Model";
                        break;
                    default:
                }
            }

            if (species == null && !isBeast) {
                species = "Human";
            }


            if (classes.size() != 0) {
                boolean isFirstClass = true;
                for (Pair<String, String> pair : classes) {
                    int count = parseInt(pair.getRight());
                    for (int i = 0; i < count; i++) {
                        ProvidedItem providedItem = ProvidedItem.create(pair.getLeft(), ItemType.CLASS);
                        if(isFirstClass){
                            providedItem.isFirstLevel();
                        }
                        current.withProvided(providedItem);
                        isFirstClass = false;
                    }
                }
            }

            m = NEAR_HUMAN_PATTERN.matcher(text);
            if (m.find()) {
                current.withSpeciesSubType(m.group(1));
            }

            m = SHARD_DROID_TYPE_PATTERN.matcher(text);
            if (m.find()) {
                current.withSpeciesSubType(m.group(1));
            }

            if ("Aqualish".equals(species)) {
                m = AQUALISH_TYPE_PATTERN.matcher(text);
                if (m.find()) {
                    current.withSpeciesSubType(m.group(1));
                    speciesAnswers.add(m.group(1));
                } else {
                    current.withSpeciesSubType("None");
                    speciesAnswers.add("None");
                }
            }

            if ("Republic Clone".equals(species)) {
                speciesAnswers.add("Dexterity");
            }


            if (species != null) {
                ProvidedItem providedItem = ProvidedItem.create(species, ItemType.SPECIES);
                providedItem.withAnswers(speciesAnswers);
                current.withProvided(providedItem);
            }

            m = ageTypePattern.matcher(text);
            if (m.find()) {
                current.withAge(m.group(1).replaceAll("-", " "));
            }

            m = speciesTypePattern.matcher(text);
            if (m.find()) {
                current.withProvided(ProvidedItem.create(m.group(1), ItemType.SPECIES_TYPE));
            }
            m = traitTypePattern.matcher(text);
            if (m.find()) {
                current.withProvided(ProvidedItem.create(m.group(1), ItemType.TRAIT));
            }
            m = templateTypePattern.matcher(text);
            if (m.find()) {
                current.withProvided(ProvidedItem.create(m.group(1), ItemType.TEMPLATE));
            }
            return true;
        }
        return false;
    }

    private String getSizeAnswer(String size) {
        return size;
    }

    private String getUnitSize(List<String> children) {
        String size = children.get(0);
        String possibleModifier = children.get(1);
        if (colossal.contains(possibleModifier)) {
            size = size.concat(" ").concat(possibleModifier);
        }
        return size;
    }

    private boolean startsWithOneOf(List<String> sizes, String text) {
        return sizes.stream().map(text::startsWith).reduce(false, (a, b) -> a || b);
    }

}
