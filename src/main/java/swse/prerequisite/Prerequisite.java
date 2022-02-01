package swse.prerequisite;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.Copyable;
import swse.common.JSONy;
import static swse.talents.TalentExporter.DUPLICATE_TALENT_NAMES;
import swse.util.Util;
//import static swse.util.Util.printUnique;

public abstract class Prerequisite implements JSONy, Copyable<Prerequisite>
{
    public static final List<String> FEAT_LIST = List.of("Force of Personality", "Moving Target", "Precise Shot", "Mechanical Martial Arts", "Force Boon", "Improved Rapid Strike", "Bantha Herder", "Musician", "Sniper Shot", "Force Regimen Mastery", "Skill Challenge: Catastrophic Avoidance", "Cleave", "Dual Weapon Defense", "Expert Droid Repair", "Martial Arts I", "Mission Specialist", "Deadeye", "Acrobatic Strike", "Weapon Finesse", "Dual Weapon Mastery III", "Peace Brigade Commander", "Solo Flourish", "Micro Vision", "Dreadful Countenance", "Autofire Assault", "Weapon Focus", "Halt", "Dodge", "Cut the Red Tape", "Bone Crusher", "Stand Tall", "Wookiee Grip", "Fleet-Footed", "Return Fire", "Trip", "Unified Squadron", "Focused Rage", "K'tara Training", "Sadistic Strike", "Analytical Detachment", "Sure Climber", "Regenerative Healing", "Salvage Expert", "Toughness", "Wary Sentries", "Acrobatic Ally", "Resilient Strength", "Instinctive Attack", "Suppression Fire", "Biotech Designer", "Frightening Cleave", "Medical Expertise", "Lightning Draw", "Anointed Hunter", "Indomitable Personality", "Hobbling Strike", "Crossfire", "Coordinated Attack", "Pinpoint Accuracy", "Rapid Reaction", "Forest Stalker", "Survivor of Ryloth", "Bowcaster Marksman", "Hideous Visage", "Returning Bug", "Flawless Pilot", "Dominating Intelligence", "Aiwha Rider", "Sport Hunter", "Binary Mind", "Slippery Maneuver", "Tumble Defense", "Vehicle Drag", "Inborn Resilience", "Thick Skin", "Resolute Stance", "Tae-Jitsu Training", "Quick Draw", "Improved Damage Threshold", "Increased Agility", "Opportunistic Shooter", "Attack Combo (Ranged)", "Signature Device", "Pincer", "Predictive Defense", "Poison Resistance", "Cowardly", "Deadly Sniper", "Flood of Fire", "Grand Army of the Republic Training", "Fatal Hit", "Treacherous (Feat)", "Multi-Grab", "Exotic Weapon Proficiency", "Shake It Off", "Accelerated Strike", "Mathematical Mind", "Running Attack", "Long Haft Strike", "Superior Shaping", "Resurgence", "A Few Maneuvers", "Fight Through Pain", "Biotech Specialist", "Recovering Surge", "Resurgent Vitality", "Strong Bellow", "Primitive Warrior", "Force Readiness", "Armor Proficiency (Light)", "Annealing Rage", "Impersonate", "Double Attack", "Strafe", "Biologist Field Team", "Feat of Strength", "Silver Tongue", "Roper", "Unwavering Focus", "Surgical Expertise", "Weapon Proficiency", "Unwavering Devotion", "Prime Shot", "Metamorph II", "Rapid Shot", "Rapid Strike", "Power Blast", "Natural Leader", "Melee Defense", "Hasty Modification", "Logic Upgrade: Pyrowall", "Tactical Advantage", "Triple Attack", "Knife Trick", "Artillery Shot", "Disturbing Presence", "Starship Designer", "Flèche", "Perfect Swimmer", "Risk Taker", "Cornered", "Rancor Crush", "Whirlwind Attack", "Composer", "Flash and Clear", "Superior Tech", "Improved Opportunistic Trickery", "Spray Shot", "Unhindered Approach", "Imperceptible Liar", "Skill Training", "Mounted Defense", "Pin", "Dual Weapon Strike", "Improvised Tools", "Follow Through", "Droid Shield Mastery", "Crush", "Carouser", "Master Tracker", "Strong in the Force", "Talented", "Armor Proficiency (Heavy)", "Maniacal Charge", "Fast Surge", "Close Combat Escape", "Swarm", "Skill Mastery", "Warrior Heritage", "Bad Feeling", "Rapport", "Imperial Military Training", "Wicked Strike", "Covert Operatives", "Powerful Faith", "Improvised Weapon Mastery", "Experienced Medic", "Targeted Area", "Easily Repaired", "Fortifying Recovery", "Galactic Alliance Military Training", "Cybernetic Surgery", "Overwhelming Attack", "Hijkata Training", "Unstoppable Combatant", "Tech Specialist", "Instinctive Perception", "Skill Focus", "Alertness", "Trample", "Tool Frenzy", "Opportunistic Retreat", "Controlled Rage", "Slicer Team", "Charging Fire", "Jee-dai Heretic", "Rapid Assault", "Dreadful Rage", "Mighty Throw", "Lasting Influence", "Impetuous Move", "Distracting Droid", "Perfect Intuition", "Far Shot", "Intimidator", "Channel Rage", "Unstoppable Force", "Duck and Cover", "Martial Arts III", "Dual Weapon Mastery II", "Adaptable Talent", "Triple Crit", "Mighty Swing", "Improved Disarm", "Brink of Death", "Unswerving Resolve", "Coordinated Barrage", "Combat Trickery", "Vitality Surge", "Improved Bantha Rush", "Mobility", "Angled Throw", "Blaster Barrage", "Aquatic Specialists", "Starship Tactics", "Flurry", "Throw", "Diving Attack", "K'thri Training", "Scavenger", "Recurring Success", "Droidcraft", "Biotech Surgery", "Blaster Geometry", "Sabacc Face", "Leader of Droids", "Shrewd Bargainer", "Medical Team", "Master of Disguise", "Aiming Accuracy", "Extra Rage", "Gearhead", "Collateral Damage", "Officer Candidacy Training", "Mounted Regiment", "Logic Upgrade: Skill Swap", "Sensor Link", "Turn and Burn", "Vehicle Systems Expertise", "Overlooked", "Unleashed", "Power Attack", "Navicomputer Brain", "Clawed Subspecies", "Advantageous Attack", "Withdrawal Strike", "Echani Training", "Rapid Takedown", "Multi-Targeting", "Nikto Survival", "Metamorph", "Improved Diving Attack", "Trench Warrior", "Stava Training", "Battering Attack", "Read the Winds", "Wilderness Specialists", "Force Sensitivity", "Republic Military Training", "Tireless Squad", "Fringe Benefits", "Recall", "Nimble Team", "Metamorph III", "Dive for Cover", "Disabler", "Forceful Recovery", "Improved Sleight of Hand", "Pistoleer", "Jedi Heritage", "Vua'sa Training", "Mind of Reason", "Opportunistic Trickery", "Relentless Attack", "Burst Fire", "Tal-Gun", "Droid Hunter", "Hunter's Instincts", "Steadying Position", "Force Training", "Critical Strike", "Wilderness First Aid", "Droid Focus", "Skill Challenge: Last Resort", "Quick Comeback", "Vehicular Surge", "Ion Shielding", "Mon Calamari Shipwright", "Deft Charge", "Wrruushi Training", "Marksman", "Sniper", "Disarming Charm", "Implant Training", "Fleet Tactics", "Pitiless Warrior", "Devastating Bellow", "Elder's Knowledge", "Wary Defender", "Kaminoan Grace", "Skill Challenge: Recovery", "Verdanaian Training", "Combat Reflexes", "Improved Charge", "Improved Defenses", "Vong's Faith", "Riflemaster", "Careful Shot", "Drag Away", "Impulsive Flight", "Separatist Military Training", "Mandalorian Training", "Rebel Military Training", "Deep Sight", "Conditioning", "Unwavering Resolve", "Keen Scent", "Armor Proficiency (Medium)", "Heightened Senses", "Staggering Attack", "Scion of Dorin", "Great Cleave", "Slammer", "Dual Weapon Mastery I", "Martial Arts II", "Deceptive Drop", "Point-Blank Shot", "Logic Upgrade: Cross-Platform", "Wroshyr Rage", "Shield Surge", "Attack Combo (Melee)", "Cunning Attack", "Ryn Network", "Knock Heads", "Meat Shield", "Studio Musician", "Technical Experts", "New Republic Military Training", "Sith Military Training", "Greedy", "Powerful Charge", "Nature Specialist", "Advantageous Cover", "Bothan Will", "Expert Briber", "Informer", "Mounted Combat", "Assured Attack", "Reading the Swarm", "Justice Seeker", "Momentum Strike", "Brutish", "Brilliant Defense", "Bantha Rush", "Attack Combo (Fire and Strike)", "Ascension Specialists", "Hyperblazer", "Friends in Low Places", "Improved Grab", "Gunnery Specialist", "Battle Anthem", "Pall of the Dark Side", "Grazing Shot", "Fast Swimmer", "Demoralizing Strike", "Instinctive Defense", "Logic Upgrade: Self-Defense", "Grab Back", "Hold Together", "Increased Resistance", "Teräs Käsi Training", "Grapple Resistance", "Desperate Gambit", "Logic Upgrade: Tactician", "Ritual Mastery", "Quick Skill", "Forceful Blast", "Confident Success", "Agile Riposte", "Damage Conversion", "Savage Attack", "Burst of Speed", "Erratic Target", "Sharp Senses", "Vehicular Combat", "Heavy Hitter", "Acrobatic Dodge", "Staggering Attack (GaW)", "Tactical Genius", "Never Surrender", "Destructive Force", "Linguist", "Darkness Dweller", "Ample Foraging", "Autofire Sweep", "Brachiated Movement", "Gungan Weapon Master", "Stay Up", "Spacer's Surge", "Jedi Familiarity", "Extra Second Wind", "Zero Range", "Powerful Rage", "Banter", "Veteran Spacer");
    public static final List<String> SPECIES_LIST = List.of("Bimm (Near-Human)", "Nyriaanan", "Kerkoiden", "Skakoan", "Bothan", "Nagai", "Colicoid", "Iotran", "Togruta", "Bith", "Firrerreo", "Sy Myrthian", "Kessurian", "Vodran", "Vor", "Utai", "Yuzzem", "Gastrulan", "Kowakian", "Sakiyan", "Anzati", "Omwati", "Givin", "Kian'thar", "Lannik", "Gamorrean", "Kyuzo", "Tridactyl", "Ryn", "Dressellian", "Drackmarian", "Stenax", "Myneyrsh", "Imroosian", "Sarkan", "Duros", "Maelibus", "Bartokk", "Pyke", "Eirrauc", "Tarasin", "Defel", "Miraluka", "Noghri", "Talz", "Ardennian", "Kobok", "Elomin", "Elom", "Tusken Raider", "Vratix", "Viraanntesse", "Ugnaught", "Anzellan", "Gree", "Siniteen", "Kaleesh", "Dowutin", "Lurmen", "Ranat", "Ebranite", "X'Ting", "Tof", "S'kytri", "Iktotchi", "Fosh", "Terrelian", "Tarro", "Toydarian", "Clawdite", "Sluissi", "Selkath", "Spiner", "Rodian", "Gossam", "Caarite", "Caamasi", "Jenet", "Polis Massan", "Kissai", "Aing-Tii", "Cerean", "Pa'lowick", "Rishii", "Nuknog", "Shi'ido", "Barabel", "Nimbanel", "Zygerrian", "Umbaran", "Thakwaash", "Vulptereen", "Dulok", "Esh-kha", "Pau'an", "Twi'lek", "Melitto", "Zabrak", "Psadan", "Ssi-Ruuk", "O'reenian", "Advozse", "Shard", "Rattataki", "Klatooinian", "Sanyassan", "Croke", "Sephi", "Yarkora", "Altiri", "Duinuogwuin", "Sauvax", "Whiphid", "Holwuff", "Lasat", "Turazza", "Patitite", "Mirialan", "Zeltron", "Yinchorri", "Nikto", "Jawa", "Vuvrian", "Morseerian", "Muun", "Hutt", "Besalisk", "Aqualish", "Krevaaki", "Hasikian", "Vagaari", "Quermian", "Boltrunian", "Coway", "Gen'Dai", "Nosaurian", "Bivall", "Zehethbra", "Chironian", "Kubaz", "Killik", "Gran", "Hysalrian", "Ruurian", "Jablogian", "Mustafarian, Northern", "Ithorian", "Neti", "Harch", "Toong", "Cathar", "Trianii", "Ab'Ugartte", "Stennes Shifter", "Kaminoan", "Anx", "Arcona", "Arkanian Offshoot", "Charon", "Ugor", "Filvian", "Pho Ph'eahian", "Teedo", "Quor'sav", "Shistavanen", "Nazren", "Revwien", "Togorian", "Ewok", "Devaronian", "Flesh Raider", "Gand", "Codru-Ji", "Qwohog", "Arkanian", "Diathim", "Trandoshan", "Snivvian", "Vippit", "Taung", "Tunroth", "Pacithhip", "Nelvaanian", "Bimm", "Frozian", "Abyssin", "Cragmoloid", "Mrlssi", "Ubese", "Dug", "Draethos", "Human", "Adarian", "Evocii", "Balosar", "Blood Carver", "Wookiee", "Amaran", "Anarrian", "Geonosian", "Ciasi", "T'surr", "Vahla", "Lurrian", "Aar'aa", "Republic Clone", "Chistori", "Theelin", "Chev", "Ebruchi", "Feeorin", "Drall", "Houk", "Vultan", "Ayrou", "Kerestian", "Woostoid", "Verpine", "Gungan", "Ishi Tib", "Gozzo", "Rakata", "Felucian", "Ho'Din", "Noehon", "Kudon", "Farghul", "Nazzar", "Tiss'shar", "Sullustan", "Weequay", "Anomid", "Neimoidian", "Kushiban", "Falleen", "Karkarodon", "Blarina", "Baragwin", "Kitonak", "Menahuun", "Gurlanin", "Nediji", "Aleena", "Chagrian", "Yevetha", "Tash", "Kel Dor", "Herglic", "Ranth", "Vorzydiak", "Southern Mustafarian", "Celegian", "Zuguruk", "Chazrach", "Ongree", "Skrilling", "Krish", "Equani", "Sludir", "Temolak", "Massassi", "Zilkin", "Rybet", "Replica Droid", "Khommite", "Mon Calamari", "Dashade", "Pantoran", "Tintinna", "Quarren", "Lugubraa", "Stereb", "Koorivar", "Vurk", "Filordus", "Sith Offshoot", "Meerian", "Lepi", "Ergesh", "Sorcerer of Rhand", "Phindian", "Calibop", "Chevin", "Em'liy", "Cosian", "Mantellian Savrip", "Khil", "Squib", "Amani", "Dantari", "Yuuzhan Vong", "Ortolan", "Thisspiasian", "Tchuukthai", "Chiss", "Gotal", "Xexto", "Abednedo", "Sunesi", "Nautolan", "Selonian", "Wroonian", "Chadra-Fan", "Qiraash", "Melodie", "H'nemthe", "1st-Degree Droid Model", "3rd-Degree Droid Model", "4th-Degree Droid Model", "2nd-Degree Droid Model", "5th-Degree Droid Model", "Labor Droid", "Service Droid", "Astromech Droid", "Battle Droid", "Probe Droid", "Protocol Droid", "Medical Droid", "Mechanic Droid");
    public static final List<String> TRAIT_LIST = List.of("Shapeshift", "Rage", "Bellow");
    public static final List<String> ITEM_LIST = List.of("Claw", "Hovering", "Flying", "Wheeled", "Tracked", "Basic Processor", "Hand", "Shield Generator", "Blaster", "Heuristic Processor");
    public static final List<String> TALENT_LIST = List.of("Armor Mastery", "Armored Defense", "Improved Armored Defense", "Juggernaut", "Second Skin", "Shield Expert", "Art of Concealment", "Fast Talker", "Hidden Weapons", "Illicit Dealings", "Surprise Strike", "Advanced Planning", "Blend In", "Done It All", "Get Into Position", "Master Manipulator", "Retaliation", "Attune Weapon", "Empower Weapon", "Force Talisman", "Greater Force Talisman", "Focused Force Talisman", "Force Throw", "Greater Focused Force Talisman", "Primitive Block", "Charm Beast", "Bonded Mount", "Entreat Beast", "Soothing Presence", "Wild Sense", "Animal Companion", "Animal Senses", "Calming Aura", "Comprehend Speech", "Improved Companion", "Nature Sense", "Shared Aptitude", "Bunker Buster", "Defensive Measures", "Enhance Cover", "Escort Fighter", "Launch Point", "Obscuring Defenses", "Relocate", "Safe Passage", "Safe Zone", "Zone of Recuperation", "Armored Spacer", "Attract Privateer", "Blaster and Blade I", "Blaster and Blade II", "Blaster and Blade III", "Boarder", "Ion Mastery", "Multiattack Proficiency (Advanced Melee Weapons)", "Preserving Shot", "Attract Minion", "Impel Ally I", "Impel Ally II", "Attract Superior Minion", "Bodyguard I", "Bodyguard II", "Bodyguard III", "Contingency Plan", "Impel Ally III", "Inspire Wrath", "Master's Orders", "Shelter", "Tactical Superiority", "Tactical Withdraw", "Urgency", "Wealth of Allies", "Bigger Bang", "Capture Droid", "Custom Model", "Improved Jury-Rig", "Improvised Device", "Conceal Force Use", "Force Direction", "Force Momentum", "Past Visions", "Blackguard Initiate", "Wilder Marauder", "Wilder Ravager", "Wilder Trinity Assassin", "Ignore Damage Reduction", "Teräs Käsi Basics", "Teräs Käsi Mastery", "Unarmed Counterstrike", "Unarmed Parry", "Direct", "Impart Knowledge", "Insight of the Force", "Master Advisor", "Scholarly Knowledge", "Block", "Deflect", "Lightsaber Defense", "Weapon Specialization (Lightsabers)", "Lightsaber Throw", "Redirect Shot", "Cortosis Gauntlet Block", "Precise Redirect", "Precision", "Riposte", "Shoto Focus", "Aura of Freedom", "Folded Space Mastery", "Liberate", "Many Shades of the Force", "Spatial Integrity", "Defensive Roll", "Force Intuition", "Improved Defensive Roll", "Unarmed Specialisation", "Close Cover", "Outrun", "Punch Through", "Small Target", "Watch This", "Another Coat of Paint", "Fly Casual", "Fade Out", "Keep Together", "Prudent Escape", "Reactive Stealth", "Sizing Up", "Blend In", "Incognito", "Improved Surveillance", "Intimate Knowledge", "Surveillance", "Traceless Tampering", "Disciplined Trickery", "Group Perception", "Hasty Withdrawal", "Stalwart Subordinates", "Stay in the Fight", "Stealthy Withdrawal", "At Peace", "Attuned", "Focused Attack", "Surge of Light", "Resist Aging", "Engineering Savant", "Enhance Implant", "Identify Droid", "Ingenious Builder", "Modify Prosthetic", "Patch Job", "Skilled Mechanic", "Technological Master", "Bioengineering", "Binary Mindset", "Determine Weakness", "Focused Research", "Identify Creature", "Poisoncraft", "Smelling Salts", "Echoes of the Force", "Jedi Quarry", "Prepared for Danger", "Sense Deception", "Unclouded Judgement", "Crucial Advice", "Distracting Apparition", "Guardian Spirit", "Manifest Guardian Spirit", "Vital Encouragement", "Seyugi Cyclone", "Mobile Whirlwind", "Repelling Whirlwind", "Sudden Storm", "Tempest Tossed", "Gang Leader", "Melee Assault", "Melee Brute", "Melee Opportunist", "Squad Brutality", "Squad Superiority", "Power of the Dark Side", "Dark Presence", "Revenge", "Swift Power", "Consumed by Darkness", "Dark Preservation", "Dark Side Savant", "Drain Knowledge", "Transfer Essence", "Wrath of the Dark Side", "Blast of Hatred", "Crushing Power", "Dark Dream", "Dark Power", "Dark Side Maelstrom", "Instruction", "Idealist", "Know Your Enemy", "Known Dissident", "Lead by Example", "Adept Assistant", "Mechanics Mastery", "Vehicle Mechanic", "Burst Transfer", "On-Board System Link", "Quick Astrogation", "Scomp Link Slicer", "Evasion", "Extreme Effort", "Sprint", "Surefooted", "Adrenaline Surge", "Break Program", "Heuristic Mastery", "Scripted Routines", "Ultra Resilient", "Advantageous Strike", "Dirty Tricks", "Dual Weapon Flourish I", "Dual Weapon Flourish II", "Master of Elegance", "Multiattack Proficiency (Advanced Melee Weapons)", "Out of Nowhere", "Single Weapon Flourish I", "Single Weapon Flourish II", "Arrogant Bluster", "Band Together", "Galactic Guidance", "Rant", "Self-Reliant", "Hyperdriven", "Spacehound", "Starship Raider", "Stellar Warrior", "Cramped Quarters Fighting", "Deep Space Raider", "Make a Break for It", "Battlefield Medic", "Bring Them Back", "Emergency Team", "Extra First Aid", "Medical Miracle", "Natural Healing", "Second Chance", "Steady Under Pressure", "Psychiatric Caregiver", "Mental Health Specialist", "Reconnaissance Team Leader", "Close-Combat Assault", "Get Into Position", "Reconnaissance Actions", "Piercing Hit", "Quicktrap", "Speedclimber", "Surprisingly Quick", "Tripwire", "Discblade Arc", "Distant Discblade Throw", "Recall Discblade", "Telekinetic Vigilance", "Weapon Specialization (Discblade)", "Buried Presence", "Conceal Other", "Insightful Aim", "Vanish", "Clear Mind", "Dark Side Sense", "Dark Side Scourge", "Force Haze", "Resist the Dark Side", "Dampen Presence", "Dark Retaliation", "Dark Side Bane", "Gradual Resistance", "Master of the Great Hunt", "Persistent Haze", "Prime Targets", "Reap Retribution", "Sense Primal Force", "Sentinel Strike", "Sentinel's Gambit", "Sentinel's Observation", "Steel Resolve", "Unseen Eyes", "Force Track", "Intuit Danger", "Sentinel's Insight", "Cause Mutation", "Rapid Alchemy", "Sith Alchemy", "Sith Alchemy Specialist", "Inspire Loyalty", "Undying Loyalty", "Punishing Protection", "Protector Actions", "Blaster Turret I", "Blaster Turret II", "Blaster Turret III", "Ion Turret", "Stun Turret", "Turret Self-Destruct", "Mind Probe", "Perfect Telepathy", "Psychic Citadel", "Psychic Defenses", "Telepathic Intruder", "Dogfight Gunner", "Expert Gunner", "Quick Trigger", "System Hit", "Crippling Hit", "Fast Attack Specialist", "Great Shot", "Overcharged Shot", "Synchronized Fire", "Field Detection", "Improved Force Sight", "Luka Sene Master", "Quickseeing", "Born Leader", "Coordinate", "Distant Command", "Fearless Leader", "Rally", "Trust", "Commanding Presence", "Coordinated Leadership", "Reactionary Attack", "Tactical Savvy", "Unwavering Ally", "Force Fortification", "Greater Weapon Focus (Lightsabers)", "Greater Weapon Specialization (Lightsabers)", "Multiattack Proficiency (Lightsabers)", "Severing Strike", "Improved Lightsaber Throw", "Improved Riposte", "Improved Redirect", "Lightsaber Form Savant", "Thrown Lightsaber Mastery", "Shoto Master", "Twin Attack (Lightsabers)", "Double Agent", "Enemy Tactics", "Feed Information", "Friendly Fire", "Protection", "Competitive Drive", "Competitive Edge", "Corporate Clout", "Impose Confusion", "Impose Hesitation", "Willful Resolve", "Wrong Decision", "Champion", "Quick Study", "Simple Opportunity", "Warrior's Awareness", "Warrior's Determination", "In Balance", "Master of Balance", "Je'daii Blade Expert", "There is No Fear", "Enhanced Vision", "Impenetrable Cover", "Invisible Attacker", "Mark the Target", "Maximize Cover", "Shellshock", "Soften the Target", "Triangulate", "Disciplined Strike", "Telekinetic Power", "Telekinetic Savant", "Aversion", "Force Flow", "Illusion", "Illusion Bond", "Influence Savant", "Link", "Masquerade", "Move Massive Object", "Suppress Force", "Telekinetic Prodigy", "Telepathic Influence", "Telepathic Link", "Force Bond", "Force Prodigy", "Influence Natural", "Kinetic Might", "Telekinetic Natural", "Transfer Force", "Cover Bracing", "Intentional Crash", "Nonlethal Tactics", "Pursuit", "Respected Officer", "Slowing Stun", "Takedown", "Amphistaff Block", "Amphistaff Riposte", "Spearing Accuracy", "Spiral Shower", "Venom Rake", "Aggressive Surge", "Blast Back", "Fade Away", "Second Strike", "Swerve", "Battlefield Remedy", "Grizzled Warrior", "Reckless", "Seen It All", "Tested in Battle", "Force Directed Shot", "Negate and Redirect", "Rising Anger", "Rising Panic", "Noble Fencing Style", "Demoralizing Defense", "Leading Feint", "Personal Affront", "Transposing Strike", "Force Immersion", "Immerse Another", "Ride the Current", "Surrender to the Current", "White Current Adept", "Adrenaline Implant", "Precision Implant", "Resilience Implant", "Speed Implant", "Strength Implant", "Cover Your Tracks", "Difficult to Sense", "Force Veil", "Jedi Network", "Battle Analysis", "Cover Fire", "Demolitionist", "Draw Fire", "Harm's Way", "Indomitable", "Tough as Nails", "Coordinated Effort", "Dedicated Guardian", "Dedicated Protector", "Defensive Position", "Hard Target", "Keep Them at Bay", "Out of Harm's Way", "Combat Instincts", "Grenadier", "Dull the Pain", "Interrogator", "Medical Droid", "Known Vulnerability", "Medical Analyzer", "Science Analyzer", "Triage Scan", "Corellian Security Force", "Journeyman Protector", "Sector Ranger", "Commanding Officer", "Coordinated Tactics", "Fire at Will", "Squad Actions", "Flurry of Blows", "Hardened Strike", "Punishing Strike", "Battlefield Sacrifice", "Built to Suffer", "Crusader's Fury", "Embrace the Pain", "Glorious Death", "Hail of Bugs", "Path of Humility", "Pray to the Pardoner", "Priest's Expertise", "Ritual Expertise", "Trickster's Disciple", "Vua'sa Expertise", "Yammka's Devotion", "Hunter's Mark", "Hunter's Target", "Notorious", "Nowhere to Hide", "Relentless", "Ruthless Negotiator", "Detective", "Dread", "Electronic Trail", "Familiar Enemies", "Familiar Situation", "Fearsome", "Jedi Hunter", "Nowhere to Run", "Quick Cuffs", "Revealing Secrets", "Signature Item", "Tag", "Akk Dog Master", "Akk Dog Trainer's Actions", "Akk Dog Attack Training", "Protective Reaction", "Lor Pelek", "Vibroshield Master", "Bolster Ally", "Ignite Fervor", "Inspire Confidence", "Inspire Haste", "Inspire Zeal", "Beloved", "Willpower", "Channel Vitality", "Closed Mind", "Esoteric Technique", "Mystic Mastery", "Regimen Mastery", "Commanding Presence", "Dirty Fighting", "Feared Warrior", "Focused Warrior", "Ruthless", "Combined Fire", "Mercenary's Determination", "Mercenary's Grit", "Mercenary's Teamwork", "Accurate Blow", "Close-Quarters Fighter", "Ignore Armor", "Improved Stunning Strike", "Whirling Death", "Guaranteed Boon", "Leading Skill", "Learn from Mistakes", "Try Your Luck", "Device Jammer", "Droid Jammer", "Extreme Explosion", "Mine Mastery", "Shaped Explosion", "Skilled Demolitionist", "Attune Armor", "Force Cloak", "Force Cloak Mastery", "Linked Defense", "Force Warning", "Improved Quick Draw (Lightsabers)", "Sheltering Stance", "Vigilance", "Watchman's Advance", "Black Market Buyer", "Excellent Kit", "Just What is Needed", "Only the Finest", "Right Gear for the Job", "Find Openings", "Hit the Deck", "Lure Closer", "Risk for Reward", "Trick Step", "Dark Deception", "Improved Sentinel Strike", "Improved Sentinel's Gambit", "Rebuke the Dark", "Taint of the Dark Side", "Believer Intuition", "Defense Boost", "Hardiness", "High Impact", "Sith Reverence", "Thunderous Bellow", "Beast Speech", "Commune with Nature", "Constriction", "Combined Fire", "Fleet Deployment", "Fleet Tactics", "It's a Trap!", "Legendary Commander", "Ataru", "Djem So", "Jar'Kai", "Juyo", "Makashi", "Niman", "Shien", "Shii-Cho", "Sokan", "Soresu", "Trakata", "Vaapad", "Dun Möch", "Maho-Kai", "Tripzest", "Acrobatic Recovery", "Battle Meditation", "Elusive Target", "Force Intuition", "Resilience", "Close Maneuvering", "Cover Escape", "Defensive Acuity", "Exposing Strike", "Forceful Warrior", "Grenade Defense", "Guardian Strike", "Hold the Line", "Immovable", "Improved Battle Meditation", "Mobile Combatant", "Battle Meld", "Blaster and Saber", "Blaster Deflect", "Force Meld", "Guardian's Insight", "Mettle", "Oafish", "Outsider's Eye", "Outsider's Query", "Wary", "Deny Move", "Extended Critical Range (Heavy Weapons)", "Extended Critical Range (Rifles)", "Flurry Attack", "Knockback", "Reduce Defense", "Reduce Mobility", "Extended Critical Range (Simple Weapons)", "Initiate of Vahl", "Reading the Flame", "Sword of Vahl", "Vahl's Brand", "Vahl's Flame", "Anticipate Movement", "Forewarn Allies", "Get Down", "Heavy Fire Zone", "Summon Aid", "Adept Spellcaster", "Dark Lore", "Delusion", "Illusionary Disguise", "Understanding the Force", "Unity of the Force", "Bloodthirsty", "Fight to the Death", "Keep Them Reeling", "Raider's Frenzy", "Raider's Surge", "Savage Reputation", "Take Them Alive", "Cargo Hauler", "Environmentally Shielded", "Power Supply", "Durable", "Heavy-Duty Actuators", "Load Launcher", "Task Optimization", "Force Blank", "Lightsaber Evasion", "Precision Fire", "Steel Mind", "Strong-Willed", "Telekinetic Resistance", "Fast Repairs", "Hotwire", "Quick Fix", "Personalized Modifications", "Defensive Jab", "Nimble Dodge", "Retaliation Jab", "Stinging Jab", "Stunning Shockboxer", "Cower Enemies", "Force Interrogation", "Inquisition", "Unsettling Presence", "Knight's Morale", "Oath of Duty", "Praetoria Ishu", "Praetoria Vonil", "Strength of the Empire", "Recruit Enemy", "Bolstered Numbers", "Noble Sacrifice", "Stay in the Fight", "Team Recruiting", "Jet Pack Training", "Burning Assault", "Improved Trajectory", "Jet Pack Withdraw", "Aerial Maneuvers", "Comrades in Arms", "Focused Targeting", "Phalanx", "Stick Together", "Watch Your Back", "Cyborg Avatar", "Cyborg Martyr", "Droid Receptacle", "Enlightened Meditation", "Serene Courage", "SpyNet Agent", "Bothan Resources", "Knowledge is Life", "Knowledge is Power", "Knowledge is Strength", "Six Questions", "Bomb Thrower", "For the Cause", "Make an Example", "Revolutionary Rhetoric", "Brutal Attack", "Call Out", "Distracting Attack", "Exotic Weapons Master", "Lockdown Strike", "Multiattack Proficiency (Exotic Weapons)", "Personal Vendetta", "Unstoppable", "Defensive Electronics", "Ion Resistance 10", "Soft Reset", "Modification Specialist", "Repair Self", "Just a Droid", "Swift Droid", "Body Control", "Physical Surge", "Soft to Solid", "Wan-Shen Defense", "Wan-Shen Kata", "Wan-Shen Mastery", "Damage Reduction 10", "Equilibrium", "Force Focus", "Force Recovery", "Beast Trick", "Channel Energy", "Force Exertion", "Force Harmony", "Force Suppression", "Indomitable Will", "The Will To Resist", "Telekinetic Stability", "Force Concealment", "Force Stealth", "Trust the Force", "Velocity", "Vitality Strike", "Force Power Adept", "Force Treatment", "Fortified Body", "Instrument of the Force", "Long Call", "Mystical Link", "Battle Precognition", "Combat Trance", "Improvised Weapon Mastery", "Twin Weapon Style", "Twin Weapon Mastery", "Shoto Pin", "Cheap Trick", "Easy Prey", "Quick Strike", "Sly Combatant", "Echani Expertise", "Hijkata Expertise", "K'tara Expertise", "K'thri Expertise", "Stava Expertise", "Tae-Jitsu Expertise", "Wrruushi Expertise", "Verdanaian Expertise", "Concentrate All Fire", "Escort Pilot", "Lose Pursuit", "Run Interference", "Wingman Retribution", "Action Exchange", "Force Delay", "Imbue Item", "Knowledge of the Force", "Advantageous Opening", "Retribution", "Slip By", "Thrive on Chaos", "Vindication", "Cheap Shot", "No Escape", "Opportunistic Strike", "Slippery Strike", "Strike and Run", "Gimmick", "Master Slicer", "Trace", "Electronic Forgery", "Electronic Sabotage", "Security Slicer", "Virus", "Defensive Circle", "Force Revive", "Jedi Battle Commander", "Slashing Charge", "Mobile Attack (Lightsabers)", "Deception Awareness", "Greater Weapon Focus (Fira)", "Progenitor's Call", "Waveform", "Hidden Movement", "Improved Stealth", "Total Concealment", "Dig In", "Extended Ambush", "Ghost Assailant", "Hide in Plain Sight", "Hunker Down", "Shadow Striker", "Slip By", "Expert Grappler", "Gun Club", "Melee Smash", "Stunning Strike", "Unbalance Opponent", "Bayonet Master", "Cantina Brawler", "Counterpunch", "Crowd Control", "Devastating Melee Smash", "Disarm and Engage", "Entangler", "Experienced Brawler", "Grabber", "Hammerblow", "Make Do", "Man Down", "Pick a Fight", "Reverse Strength", "Strong Grab", "Sucker Punch", "Unrelenting Assault", "Droid Duelist", "Force Repair", "Heal Droid", "Mask Presence", "Silicon Mind", "Charm Beast", "Command Beast", "Detonate", "Hive Mind", "Infuse Weapon", "Sickening Blast", "Deadly Repercussions", "Manipulating Strike", "Improved Manipulating Strike", "Pulling the Strings", "Advanced Intel", "Hidden Eyes", "Hunt the Hunter", "Seek and Destroy", "Spotter", "Biotech Mastery", "Expedient Mending", "Expert Shaper", "Master Mender", "Skilled Implanter", "Dark Healing", "Dark Scourge", "Dark Side Adept", "Dark Side Master", "Force Deception", "Improved Dark Healing", "Wicked Strike", "Affliction", "Dark Healing Field", "Drain Force", "Sith Alchemy", "Stolen Form", "Force Crush", "Vengeful Spirits", "Ambush Specialist", "Destructive Ambusher", "Keep It Going", "Keep Them Reeling", "Perceptive Ambusher", "Spring the Trap", "Computer Language", "Computer Master", "Enhanced Manipulation", "Hotwired Processor", "Power Surge", "Skill Conversion", "Power Boost", "Advantageous Positioning", "Get Some Distance", "Murderous Arts I", "Murderous Arts II", "Ruthless", "Shift", "Sniping Assassin", "Sniping Marksman", "Sniping Master", "Forward Patrol", "Mobile Combatant", "Trailblazer", "Watchful Step", "Precision Shot", "Bullseye", "Draw a Bead", "Pinning Shot", "Harrying Shot", "Confounding Attack", "Double Up", "Find an Opening", "Opportunistic Defense", "Preternatural Senses", "Seize the Moment", "Tangle Up", "Uncanny Instincts", "Force Perception", "Force Pilot", "Foresight", "Gauge Force Potential", "Visions", "Feel the Force", "Force Reflexes", "Heightened Awareness", "Instinctive Navigation", "Motion of the Future", "Psychometry", "Shift Sense", "Force Meld", "Instinctive Astrogation", "Machine Empathy", "Machine Meld", "Reactive Precognition", "Presence", "Demand Surrender", "Improved Weaken Resolve", "Weaken Resolve", "Fluster", "Intimidating Defense", "Allure", "Captivate", "Findsman Ceremonies", "Findsman's Foresight", "Omens", "Target Visions", "Temporal Awareness", "Connections", "Educated", "Spontaneous Skill", "Wealth", "Engineer", "Influential Friends", "Powerful Friends", "Cross-Training", "Gifted Entertainer", "Favors", "Armored Guard", "Bodyguard's Sacrifice", "Guard's Endurance", "Lifesaver", "Out of Harm's Way", "Roll With It", "Take the Hit", "Ward", "Devastating Attack", "Penetrating Attack", "Weapon Specialization", "Autofire Assault", "Crushing Assault", "Disarming Attack", "Impaling Assault", "Improved Suppression Fire", "Stinging Assault", "Biotech Adept", "Bugbite", "Curved Throw", "Surprising Weapons", "Veiled Biotech", "Armored Augmentation I", "Armored Augmentation II", "Armor Mastery", "Cortosis Defense", "Cortosis Retaliation", "Controlled Burst", "Exotic Weapon Mastery", "Greater Devastating Attack", "Greater Penetrating Attack", "Greater Weapon Focus", "Greater Weapon Specialization", "Multiattack Proficiency (Heavy Weapons)", "Multiattack Proficiency (Rifles)", "Extended Threat", "Ferocious Assault", "Multiattack Proficiency (Simple Weapons)", "Two-For-One Throw", "Heavy Gunner", "Twin Attack", "Ambush", "Higher Yield", "Rapid Reload", "Shoulder to Shoulder", "Strength in Numbers", "Weapon Shift", "Malkite Techniques", "Modify Poison", "Numbing Poison", "Undetectable Poison", "Vicious Poison", "Deep-Space Gambit", "Guidance", "Hidden Attacker", "Hyperspace Savant", "Vehicle Sneak", "Silent Movement", "Dash and Blast", "Flanking Fire", "Guaranteed Shot", "Hailfire", "Twin Shot", "Breach Cover", "Breaching Explosive", "Droid Expert", "Prepared Explosive", "Problem Solver", "Quick Modifications", "Repairs on the Fly", "Sabotage Device", "Tech Savant", "Vehicular Boost", "Cast Suspicion", "Distress to Discord", "Friend or Foe", "Seize the Moment", "Stolen Advantage", "True Betrayal", "Combustion", "Earth Buckle", "Fluidity", "Thunderclap", "Wind Vortex", "Clone Scientist", "Gene Splicing", "Mass Cloning", "Master Cloner", "Rapid Cloning", "Retrovirus", "Bando Gora Surge", "Force Fighter", "Resist Enervation", "Victorious Force Mastery", "Dastardly Strike", "Disruptive", "Skirmisher", "Sneak Attack", "Walk the Line", "Backstabber", "Befuddle", "Cunning Strategist", "Hesitate", "Improved Skirmisher", "Improved Sneak Attack", "Seducer", "Seize Object", "Sow Confusion", "Stymie", "Sudden Strike", "Weakening Strike", "Opportunist", "Quick Strike", "Assault Tactics", "Deployment Tactics", "Field Tactics", "One for the Team", "Outmaneuver", "Shift Defense I", "Shift Defense II", "Shift Defense III", "Tactical Edge", "Commander's Prerogative", "Exploit Weakness", "Grand Leader", "Irregular Tactics", "Lead by Example", "Turn the Tide", "Uncanny Defense", "Blowback", "Close Contact", "Multiattack Proficiency (Rifles)", "Old Faithful", "Opportunity Fire", "Rifle Master", "Shoot from the Hip", "Snap Shot", "Adept Spellcaster", "Charm Beast", "Command Beast", "Flight", "Force Treatment", "Healing Boost", "Improved Healing Boost", "Soothe", "Return to Life", "Vital Synchronism", "Chalactan Adept", "Lesser Mark of Illumination", "Greater Mark of Illumination", "Chalactan Enlightenment", "Fool's Luck", "Fortune's Favor", "Gambler", "Knack", "Lucky Shot", "Avert Disaster", "Better Lucky than Dead", "Dumb Luck", "Labyrinthine Mind", "Lucky Stop", "Ricochet Shot", "Uncanny Luck", "Unlikely Shot", "Savant", "Armored Mandalorian", "Mandalorian Advance", "Mandalorian Ferocity", "Mandalorian Glory", "Enhanced Danger Sense", "Expanded Horizon", "Knowledge and Defense", "Planetary Attunement", "Precognitive Meditation", "Inspire Fear I", "Inspire Fear II", "Inspire Fear III", "Notorious", "Shared Notoriety", "Fear Me", "Frighten", "Master Manipulator", "Small Favor", "Terrify", "Unsavory Reputation", "Assured Skill", "Critical Skill Success", "Exceptional Skill", "Reliable Boon", "Skill Boon", "Skill Confidence", "Skillful Recovery", "Directed Action", "Directed Movement", "Full Control", "Remote Attack", "Begin Attack Run", "Regroup", "Squadron Maneuvers", "Squadron Tactics", "Force Commander", "Diverse Squadron", "Melded Squadron", "Combat Repairs", "Droid Smash", "Targeting Package", "Just a Scratch", "Target Acquisition", "Target Lock", "Weapons Power Surge", "Ambush", "Castigate", "Dirty Tactics", "Misplaced Loyalty", "Two-Faced", "Unreadable", "Battle Mount", "Expert Rider", "Terrain Guidance", "Mechanized Rider", "Cloak of Shadows", "Phantasm", "Revelation", "Shadow Armor", "Shadow Vision", "Apprentice Boon", "Share Force Secret", "Share Force Technique", "Share Talent", "Transfer Power", "Debilitating Shot", "Deceptive Shot", "Improved Quick Draw", "Knockdown Shot", "Multiattack Proficiency (Pistols)", "Ranged Disarm", "Trigger Work", "Blind Shot", "Damaging Disarm", "Keep Them Honest", "Lingering Debilitation", "Mobile Attack (Pistols)", "Pistol Duelist", "Ranged Flank", "Retreating Fire", "Slowing Shot", "Swift Shot", "Always Ready", "Concealed Weapon Expert", "Creeping Approach", "Set for Stun", "Silent Takedown", "Adept Negotiator", "Force Persuasion", "Master Negotiator", "Skilled Advisor", "Adversary Lore", "Aggressive Negotiator", "Cleanse Mind", "Collective Visions", "Consular's Vitality", "Consular's Wisdom", "Entreat Aid", "Force of Will", "Guiding Strikes", "Improved Consular's Vitality", "Know Weakness", "Recall", "Renew Vision", "Visionary Attack", "Visionary Defense", "WatchCircle Initiate", "Healing Mastery", "Jedi Healer", "Elusive Dogfighter", "Full Throttle", "Juke", "Keep it Together", "Relentless Pursuit", "Vehicular Evasion", "Blind Spot", "Clip", "Close Scrape", "Improved Attack Run", "Master Defender", "Renowned Pilot", "Roll Out", "Shunt Damage", "Vehicle Focus", "Wingman", "Call Weapon", "Lightsaber Specialist", "Masterwork Lightsaber", "Perfect Attunement", "Quick Modification", "Assault Gambit", "Direct Fire", "Face the Foe", "Lead From the Front", "Luck Favors the Bold", "Barter", "Fringe Savant", "Long Stride", "Jury-Rigger", "Flee", "Keep it Together", "Sidestep", "Surge", "Swift Strider", "Brutal Unarmed Strike", "Martial Resurgence", "Rebound Leap", "Simultaneous Strike", "Telekinetic Strike", "Telekinetic Throw", "Desperate Measures", "Focus Terror", "Incite Rage", "Power of Hatred", "Fall Back", "Form Up", "Full Advance", "Hold Steady", "Search and Destroy", "Adapt and Survive", "Defensive Protection", "Quick on Your Feet", "Ready and Willing", "Unbalancing Adaptation", "Automated Strike", "Droid Defense", "Droid Mettle", "Expanded Sensors", "Inspire Competence", "Maintain Focus", "Overclocked Troops", "Reinforce Commands", "Acute Senses", "Expert Tracker", "Improved Initiative", "Keen Shot", "Uncanny Dodge I", "Uncanny Dodge II", "Reset Initiative", "Weak Point", "Back on their Feet", "Capable Assistant", "Makeshift Treatment", "Medical Specialization", "Reliable Treatment", "Steady Hands", "Empower Siang Lance", "Shield Gauntlet Defense", "Shield Gauntlet Deflect", "Shield Gauntlet Redirect", "Siang Lance Mastery", "Dark Side Manipulation", "Krath Illusions", "Krath Intuition", "Krath Surge", "Armored Morgukai", "Cortosis Staff Block", "Morgukai Resolve", "Multiattack Proficiency (Cortosis Staff)", "Channel Aggression", "Channel Anger", "Crippling Strike", "Embrace the Dark Side", "Dark Side Talisman", "Greater Dark Side Talisman", "Cycle of Harmony", "Force Stabilize", "Repel Discord", "Stifle Conflict", "Tyia Adept", "Etiquette", "Helpful", "Protocol", "Nuanced", "Observant", "Supervising Droid", "Talkdroid", "Cunning Distraction", "Damaging Deception", "Distracting Shout", "Improved Soft Cover", "Innocuous", "Treacherous");
    public static final List<String> SPECIAL_LIST = List.of("Shedding of the Body", "Cyborg Hybrid", "Must possess an Implant", "Have a Destiny", "2+ Appendages", "2+ Tool Appendages", "Receive the Gamemaster's Approval");
    public static final List<String> TRADITION_LIST = List.of("The B'omarr Order", "The Chalactan Adepts", "The Chalactan Adepts", "The Je'daii Rangers", "The Droid Equality Foundation", "Death Watch", "The Knights of Ren", "The Sun Guards of Thyrsus", "The Lok Revenants", "ExplorCorps", "The Praetorite Vong", "The Resistance", "The Nature Priests", "The Ailon Nova Guard", "Tor-Ro-Bo Industries", "The Tommaba Brotherhood", "The Banvhar Combine", "The Ember of Vahl", "House Organa", "The O'reenian Imperium", "The Parallax Chain", "The Jensaarai", "The Prophets of the Dark Side", "The Wavelength Gale", "The Jedi", "The Eternal Empire", "The Blazing Chain", "The Merchants' Consortium", "The Zann Consortium", "The Agents of Ossus", "The Intergalactic Zoological Society", "EduCorps", "The Jal Shey", "The Anjiliac Clan", "The Flesh Raiders", "New Republic Intelligence", "The Iron Knights", "The Imperial Military", "The Matukai", "The Je'daii Rangers", "The Mistryl Shadow Guard", "The Lugubraa Hordes", "The Republic Rocket-Jumpers", "The Galactic Republic", "The Kilian Rangers", "Systino", "The Mnggal-Mnggal", "The Corellian Confederation", "The Cult of Veroleem", "The Hutt Kajidics", "The GenoHaradan", "The Krath", "The Miners' Union", "The Killik Hivemind", "The Core World Nobles", "The Felucian Shamans", "The Bounty Hunters' Guild", "Lightning Squadron", "Kota's Militia", "The Baran Do Sages", "The Firebird Society", "Alliance Intelligence", "General Units", "The Lakhasa Caravan", "The Fel Empire", "The Onderon Beast Riders", "AgriCorps", "The Red Fury Brotherhood", "The Inquisitorius", "Beasts", "The Mandalorians", "Churhee's Riflemen", "The Galactic Empire", "Epsis", "The Zeison Sha", "The Believers", "The Yuuzhan Vong Empire", "The Joruba Consortium", "Tangan Industries", "The Disciples of Twilight", "The Anarrian Empire", "The Corporate Sector Authority", "The Chiss Ascendancy", "The Senate Guard", "The Seyugi Dervishes", "The Crimson Stars", "The Fringe", "The Luka Sene", "Sabaoth Squadron", "The Bothan SpyNet", "The Ssi-Ruuvi Imperium", "The Ebruchi Fleet", "The Tapani Noble Houses", "The Sorcerers of Tund", "Skull Squadron", "The Mecrosa Order", "The Vagaari Empire", "The Ebon Strikers", "The Kolkpravis", "The Tenloss Syndicate", "The Tof Kingdom", "Black Sun", "Imperial Intelligence", "The Crimson Dawn", "The Car'das Smugglers", "The Katarn Commandos", "The Nimbus Commandos", "The Hapes Consortium", "The Order of Shasa", "The Sith", "The Shapers of Kro Var", "The Galactic Alliance Guard", "The Confederacy of Independent Systems", "The Nyriaanan Clans", "The Infinite Empire", "The Trianii Rangers", "MedCorps", "The Thalassian Slavers", "The Tyia", "Republic Intelligence", "The Witches of Dathomir", "The Nihil", "The Apex Society", "The Altirian Republic", "The Wing Guard", "The Mandalorian Protectors", "The Wardens of the Sky", "The First Order", "The New Republic", "The Uwanna Cartel", "TaggeCo", "The Galactic Alliance", "The Morgukai", "The Naboo Resistance", "The Horizon Guard", "Wraith Squadron", "The Hapan Royal Guard", "The Korunnai", "The Fallanassi", "Sando's Boys", "The Bando Gora", "The Iron Ring", "The Vipers", "The Antarian Rangers", "The Aing-Tii Monks", "The Old Republic", "The Techno Union", "The Keetael", "The Imperial Army", "The Imperial Navy", "The Veroleem Resistance", "The Sith Empire", "The Galactic Alliance Guard", "The Sable Dawn", "Eeook Mining and Reclamation", "The Peace Brigade", "The Blackguard", "The Rebel Alliance", "The Imperial Knights", "House Korden");
    public static final List<String> FORCE_POWER_LIST = List.of("Obscure", "Concentration", "Force Projection", "Force Blast", "Crucitorn", "Plant Surge", "Pass the Blade", "Fold Space", "Force Storm", "Deflecting Slash", "Slow", "Tempered Aggression", "Rising Whirlwind", "Phase", "Negate Energy", "Force Shield", "Resist Force", "Draw Closer", "Force Lightning", "Malacia", "Force Slam", "Valor", "Energy Resistance", "Rend", "Saber Swarm", "Ionize", "Falling Avalanche", "Ballistakinesis", "Force Grip", "Rearrangement", "Inertia", "Blind", "Lightning Burst", "Sith Word", "Shatterpoint", "Force Disarm", "Vornskr's Ferocity", "Conduction", "Dark Transfer", "Hatred", "Force Stun", "Move Object", "Detonate", "Technometry", "Dark Rage", "Inspire", "Shien Deflection", "High Ground Defense", "Pushing Slash", "Memory Walk", "Sever Force", "Makashi Riposte", "Mind Shard", "Force Scream", "Stagger", "Repulse", "Hawk-Bat Swoop", "Vital Transfer", "Rebuke", "Farseeing", "Circle of Shelter", "Assured Strike", "Contentious Opportunity", "Twin Strike", "Conjure Doubt", "Levitate", "Convection", "Force Light", "Force Whirlwind", "Glowball", "Enlighten", "Swift Flank", "Force Storm (FUCG)", "Unbalancing Block", "Wound", "Kinetic Combat", "Combustion", "Sarlacc Sweep", "Battle Strike", "Siphon Life", "Gaze of Darkness", "Prescience", "Fear", "Unhindered Charge", "Force Track", "Cryokinesis", "Corruption (Force Power)", "Barrier of Blades", "Cloak", "Morichro", "Sith Curse", "Force Thrust", "Disarming Slash", "Surge", "Inflict Pain", "Instill Turmoil", "Mind Trick", "Thought Bomb", "Drain Energy", "Fluid Riposte", "Intercept");
    public static final List<String> FORCE_SECRETS = List.of();
    public static final List<String> FORCE_TECHNIQUES = List.of();
    public static final List<String> CLASS_LIST = List.of("Jedi", "Noble", "Scoundrel", "Scout", "Soldier", "Technician", "Force Prodigy", "Nonheroic", "Beast", "Ace Pilot", "Bounty Hunter", "Crime Lord", "Elite Trooper", "Force Adept", "Force Disciple", "Gunslinger", "Jedi Knight", "Jedi Master", "Officer", "Sith Apprentice", "Sith Lord", "Corporate Agent", "Gladiator", "Melee Duelist", "Enforcer", "Independent Droid", "Infiltrator", "Master Privateer", "Medic", "Saboteur", "Assassin", "Charlatan", "Outlaw", "Droid Commander", "Military Engineer", "Vanguard", "Imperial Knight", "Shaper", "Improviser", "Pathfinder", "Martial Arts Master");

    protected final String plainText;
    protected final String type;

    public Prerequisite(String plainText, String type)
    {
        this.plainText = plainText;
        this.type = type;
        //Util.printUnique("TYPE: "+ type);
    }


    public static Prerequisite getClassPrerequisite(Elements entries)
    {
        boolean found = false;
        boolean allowUL = true;
        List<Prerequisite> prerequisites = new ArrayList<>();

        for (Element entry : entries)
        {
            String input = entry.text().toLowerCase();
            if (found)
            {
                if (allowUL && entry.tag().equals(Tag.valueOf("ul")))
                {
                    allowUL = false;

                    for (Element li :
                            entry.select("li"))
                    {
                        prerequisites.add(Prerequisite.create(li.text()));
                    }

                }
            } else if ((entry.tag().equals(Tag.valueOf("h4")) || entry.tag().equals(Tag.valueOf("p"))) && input.contains("prerequisites"))
            {
                found = true;
            }
        }
        return merge(prerequisites);
    }


    public static Prerequisite getPrerequisite(Elements elements)
    {

        List<Prerequisite> prerequisites = new ArrayList<>();
        for (Element element : elements)
        {
            prerequisites.add(getPrerequisite(element, null));
        }
        return merge(prerequisites);
    }


    public static Prerequisite merge(Prerequisite... prerequisites) {
        return merge(Arrays.asList(prerequisites));
    }

    public static Prerequisite merge(List<Prerequisite> prerequisites)
    {
        prerequisites = prerequisites.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (prerequisites.size() == 1)
        {
            return prerequisites.get(0);
        } else if (prerequisites.size() > 1)
        {
            return new AndPrerequisite(prerequisites);
        }
        return null;
    }

    public static Prerequisite getPrerequisite(Element element, String itemName)
    {
        List<Prerequisite> prerequisites = new ArrayList<>();
        if (element.text().toLowerCase().startsWith("prerequisite"))
        {
            String prerequisite = element.text().split(":")[1].trim();
            if ("Observant, Any Talent from the Influence Talent Tree, Inspiration Talent Tree, or Leadership Talent Tree".equals(prerequisite))
            {
                prerequisite = prerequisite.replaceFirst(",", ";");
            }
            if ("At least 1 Talent from Lightsaber Combat Talent Tree, Duelist Talent Tree, or Lightsaber Forms Talent Tree".equals(prerequisite)
                    || "Any Talent from either the Expert Pilot Talent Tree or the Gunner Talent Tree".equals(prerequisite)
                    || "Any two Talents from the Slicer Talent Tree, Burst Transfer can be one of these Talents".equals(prerequisite)
                    || "Bothan, or two Talents from the Infiltration Talent Tree".equals(prerequisite))
            {
                prerequisites.add(create(prerequisite, itemName));
            } else if (prerequisite.contains(";"))
            {
                prerequisites.addAll(Arrays.stream(prerequisite.trim().split(";")).map(prereq -> create(prereq, itemName)).collect(Collectors.toList()));
            } else
            {
                prerequisites.addAll(Arrays.stream(prerequisite.trim().split(",")).map(prereq -> create(prereq, itemName)).collect(Collectors.toList()));
            }
        }
        return merge(prerequisites);
    }


    public static Prerequisite create(String plainText)
    {
        return create(plainText, null);
    }

    private static Prerequisite create(String plainText, String itemName)
    {
        String plainText1 = plainText.trim();
        final List<Prerequisite> prerequisites = parsePrerequisites(plainText1, itemName);

        return merge(prerequisites);
    }

    private static List<Prerequisite> parsePrerequisites(String text, String itemName)
    {
        if (text.endsWith("."))
        {
            text = text.substring(0, text.length() - 1);
        }
        if (text.endsWith(" Feat"))
        {
            text = text.substring(0, text.length() - 5);
        }
        if (text.endsWith(" Talent"))
        {
            text = text.substring(0, text.length() - 7);
        }
        if (text.endsWith(" Force Power"))
        {
            text = text.substring(0, text.length() - 12);
        }
        if (text.endsWith(" Species Trait"))
        {
            text = text.substring(0, text.length() - 14);
        }
        if (text.endsWith(" Locomotion"))
        {
            text = text.substring(0, text.length() - 11);
        }
        if (text.endsWith(" Appendage"))
        {
            text = text.substring(0, text.length() - 10);
        }
        if (text.endsWith(" (Droid Accessory)"))
        {
            text = text.substring(0, text.length() - 18);
        }
        if (text.endsWith(" (Kajain'sa'Nikto)"))
        {
            text = text.substring(0, text.length() - 18);
        }
        if (text.startsWith("Droid Systems: "))
        {
            text = text.substring(15);
        }
        if (text.startsWith("Talents: "))
        {
            text = text.substring(9);
        }
        if (text.startsWith("Talent: "))
        {
            text = text.substring(8);
        }
        if (text.startsWith("Species: "))
        {
            text = text.substring(9);
        }


        //if this is wrapped in paired parens, remove them
        if (text.startsWith("(") && text.endsWith(")") && text.chars().filter(ch -> ch == ')').count() == 1 && text.chars().filter(ch -> ch == '(').count() == 1)
        {
            final String substring = text.substring(1, text.length() - 1);
            return parsePrerequisites(substring, itemName);
        }


        //PRE OR:phrases that contain OR but should not be parsed as an or
        if ("Double Attack with either Advanced Melee Weapons, an Exotic Weapon (Melee), or Lightsabers".equals(text))
        {
            return List.of(new OrPrerequisite(text, List.of(
                    new SimplePrerequisite(text, "FEAT", "Double Attack (Advanced Melee Weapon)"),
                    new SimplePrerequisite(text, "FEAT", "Double Attack (#Exotic Melee Weapon#)"),
                    new SimplePrerequisite(text, "FEAT", "Double Attack (Lightsabers)")
            )));
        }
        if ("Master of Elegance, Single Weapon Flourish I, Weapon Finesse".equals(text))
        {
            return List.of(new AndPrerequisite(text, List.of(
                    new SimplePrerequisite(text, "TALENT", "Master of Elegance"),
                    new SimplePrerequisite(text, "TALENT", "Single Weapon Flourish I"),
                    new SimplePrerequisite(text, "FEAT", "Weapon Finesse")
            )));
        }
        //Medium or larger size
        if ("Medium or larger size".equals(text))
        {
            return List.of(new OrPrerequisite(text, List.of(
                    new SimplePrerequisite(text, "TRAIT", "Medium"),
                    new SimplePrerequisite(text, "TRAIT", "Large"),
                    new SimplePrerequisite(text, "TRAIT", "Huge"),
                    new SimplePrerequisite(text, "TRAIT", "Colossal"),
                    new SimplePrerequisite(text, "TRAIT", "Gargantuan")
            )));
        }
        //Small size or larger
        if ("Small size or larger".equals(text))
        {
            return List.of(new OrPrerequisite(text, List.of(
                    new SimplePrerequisite(text, "TRAIT", "Small"),
                    new SimplePrerequisite(text, "TRAIT", "Medium"),
                    new SimplePrerequisite(text, "TRAIT", "Large"),
                    new SimplePrerequisite(text, "TRAIT", "Huge"),
                    new SimplePrerequisite(text, "TRAIT", "Colossal"),
                    new SimplePrerequisite(text, "TRAIT", "Gargantuan")
            )));
        }
        // Trained in at least one Knowledge Skill
        if ("Trained in at least one Knowledge Skill".equals(text))
        {
            return List.of(new OrPrerequisite(text, List.of(
                    new SimplePrerequisite(text, "FEAT", "Knowledge (Bureaucracy)"),
                    new SimplePrerequisite(text, "FEAT", "Knowledge (Galactic Lore)"),
                    new SimplePrerequisite(text, "FEAT", "Knowledge (Life Sciences)"),
                    new SimplePrerequisite(text, "FEAT", "Knowledge (Physical Sciences)"),
                    new SimplePrerequisite(text, "FEAT", "Knowledge (Social Sciences)"),
                    new SimplePrerequisite(text, "FEAT", "Knowledge (Tactics)"),
                    new SimplePrerequisite(text, "FEAT", "Knowledge (Technology)")
            )));
        }

        // Not a Droid
        if ("Not a Droid".equals(text) || "Cannot be a Droid".equals(text))
        {
            return List.of(new SimplePrerequisite(text, "SPECIAL", "Not a Droid"));
        }

        if ("Special: Must be a Droid".equals(text) || "Droid".equals(text))
        {
            return List.of(new SimplePrerequisite(text, "SPECIAL", "Is a droid"));
        }

        Pattern oneTalent = Pattern.compile("(?:Any|At least 1|At least one|Any one) Talent from (?:either )?(?:the )?([\\s\\w,]*)");
        Matcher oneTalentMatcher = oneTalent.matcher(text);
        if (oneTalentMatcher.find())
        {
            String payload = oneTalentMatcher.group(1);
            if (payload.contains(" and "))
            {
                String[] tokens = payload.split(", and | and |, ");
                return List.of(new AndPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(talentTree -> new SimplePrerequisite("Talent from the " + talentTree, "TALENT", talentTree)).collect(Collectors.toList())));
            } else if (payload.contains(" or ") || text.contains(", "))
            {
                String[] tokens = payload.split(", or | or |, ");
                return List.of(new OrPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(talentTree -> new SimplePrerequisite("Talent from the " + talentTree, "TALENT", talentTree)).collect(Collectors.toList())));
            }
            return List.of(new SimplePrerequisite("Talent from the " + payload, "TALENT", payload));

        }

        if ("Any two Talents from the Slicer Talent Tree, Burst Transfer can be one of these Talents".equals(text))
        {
            return List.of(new OrPrerequisite(text, List.of(new SimplePrerequisite(text, "TALENT", "Slicer Talent Tree"), new SimplePrerequisite(text, "TALENT", "Burst Transfer")), 2));
        }

        Pattern twoTalents = Pattern.compile("(?:Any )?two Talents from (?:either )?the ([\\s\\w]*)");
        Matcher twoTalentsMatcher = twoTalents.matcher(text);
        if (twoTalentsMatcher.find())
        {
            String payload = twoTalentsMatcher.group(1);
            if (payload.contains(" or ") || text.contains(", "))
            {
                String[] tokens = payload.split(", or | or |, ");
                return List.of(new OrPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(talentTree -> new SimplePrerequisite("Talent from the " + talentTree, "TALENT", talentTree)).collect(Collectors.toList()), 2));
            }
            return List.of(new SimplePrerequisite("Talent from the " + payload, "TALENT", payload));

        }

        if ("At least three Force Talents".equals(text))
        {
            return List.of(new OrPrerequisite(text, List.of(new SimplePrerequisite(text, "TALENT", "Force Talent")), 3));
        }

        Pattern featsPattern = Pattern.compile("Feats: ([\\w\\s,()-:']*)");
        Matcher featsMatcher = featsPattern.matcher(text);
        if (featsMatcher.find())
        {
            String payload = featsMatcher.group(1);
            if ("Feats: Martial Arts I, Martial Arts II, Melee Defense, and at least one of the following Feats: Echani Training, Hijkata Training, K'tara Training, K'thri Training, Stava Training, Tae-Jitsu Training, Teräs Käsi Training, or Wrruushi Training".equals(text))
            {

                return List.of(new AndPrerequisite(text, List.of(new SimplePrerequisite("Martial Arts I", "FEAT", "Martial Arts I"),
                        new SimplePrerequisite("Martial Arts II", "FEAT", "Martial Arts II"),
                        new SimplePrerequisite("Melee Defense", "FEAT", "Melee Defense"),
                        new OrPrerequisite("At least one of the following Feats: Echani Training, Hijkata Training, K'tara Training, K'thri Training, Stava Training, Tae-Jitsu Training, Teräs Käsi Training, or Wrruushi Training", List.of(
                                new SimplePrerequisite("Echani Training", "FEAT", "Echani Training"),
                                new SimplePrerequisite("Hijkata Training", "FEAT", "Hijkata Training"),
                                new SimplePrerequisite("K'tara Training", "FEAT", "K'tara Training"),
                                new SimplePrerequisite("K'thri Training", "FEAT", "K'thri Training"),
                                new SimplePrerequisite("Stava Training", "FEAT", "Stava Training"),
                                new SimplePrerequisite("Tae-Jitsu Training", "FEAT", "Tae-Jitsu Training"),
                                new SimplePrerequisite("Teräs Käsi Training", "FEAT", "Teräs Käsi Training"),
                                new SimplePrerequisite("Wrruushi Training", "FEAT", "Wrruushi Training")
                        )))));
            }

            if (payload.contains(" and ") || payload.contains(", "))
            {
                String[] tokens = payload.split(", and | and |, ");
                return List.of(new AndPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList())));
            } else if (payload.contains(" or "))
            {
                String[] tokens = payload.split(", or | or |, ");
                return List.of(new OrPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList())));
            }
            String[] tokens = {payload};
            return Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList());

        }

        Pattern forcePowersPattern = Pattern.compile("Force Powers: ([\\w\\s,()-]*)");
        Matcher forcePowerMatcher = forcePowersPattern.matcher(text);
        if (forcePowerMatcher.find())
        {
            String payload = forcePowerMatcher.group(1);
            if (payload.contains(" and ") || payload.contains(", "))
            {
                String[] tokens = payload.split(", and | and |, ");
                return List.of(new AndPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList())));
            } else if (payload.contains(" or "))
            {
                String[] tokens = payload.split(", or | or |, ");
                return List.of(new OrPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList())));
            }
            String[] tokens = {payload};
            return Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList());

        }

        //
        if (text.startsWith("Equipped with "))
        {
            String payload = text.substring(0, 14);
            if (payload.contains(" and "))
            {
                String[] tokens = payload.split(" and |, ");
                return List.of(new AndPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(item -> new SimplePrerequisite("Equipped with " + item, "EQUIPPED", item)).collect(Collectors.toList())));
            } else if (payload.contains(" or ") || text.contains(", "))
            {
                String[] tokens = payload.split(" or |, ");
                return List.of(new OrPrerequisite(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(skill -> new SimplePrerequisite("Equipped with " + skill, "EQUIPPED", skill)).collect(Collectors.toList())));
            }
            return List.of(new SimplePrerequisite("Equipped with " + payload, "EQUIPPED", payload));
        }


        // Trained in
        Pattern trainedSkills = Pattern.compile("(?:Trained in|Trained Skills:) ([\\w\\s#,()]*)");
        Matcher trainedSkillsMatcher = trainedSkills.matcher(text);
        if (trainedSkillsMatcher.find())
        {
            String payload = trainedSkillsMatcher.group(1);
            if (payload.contains(" and "))
            {
                String[] tokens = payload.split(" and |, ");
                return List.of(new AndPrerequisite(text, Arrays.stream(tokens).map(skill -> new SimplePrerequisite("Trained in " + skill, "TRAINED SKILL", skill)).collect(Collectors.toList())));
            } else if (payload.contains(" or ") || text.contains(", "))
            {
                String[] tokens = payload.split(" or |, ");
                return List.of(new OrPrerequisite(text, Arrays.stream(tokens).map(skill -> new SimplePrerequisite("Trained in " + skill, "TRAINED SKILL", skill)).collect(Collectors.toList())));
            }
            return List.of(new SimplePrerequisite("Trained in " + payload, "TRAINED SKILL", payload));
        }
        // Member of
        if (text.startsWith("Must be a member of "))
        {
            String payload = text.substring(0, 20);
            return List.of(new SimplePrerequisite("Must be a member of " + payload, "TRADITION", payload));
        }
        //Military Member
        if ("Special: Must belong to an organization with a military or paramilitary division, examples include the Trade Federation, the Galactic Empire, the Rebel Alliance, and the New Republic".equals(text))
        {

            return List.of(new SimplePrerequisite(text, "SPECIAL", "is part of a military"));
        }
        //major interstellar corporation
        if ("Special: Must be employed by a major interstellar corporation".equals(text))
        {

            return List.of(new SimplePrerequisite(text, "SPECIAL", "is part of a major interstellar corporation"));
        }


        if (FEAT_LIST.contains(text))
        {
            return List.of(new SimplePrerequisite(text+" feat", "FEAT", text));
        }
        if (SPECIES_LIST.contains(text))
        {
            return List.of(new SimplePrerequisite(text+" species", "SPECIES", text));
        }
        if (TALENT_LIST.contains(text))
        {
            if(DUPLICATE_TALENT_NAMES.contains(text)){
                final String plainText = text + " (" + itemName + ")";
                return List.of(new SimplePrerequisite(plainText+" talent", "TALENT", plainText));
            }
            return List.of(new SimplePrerequisite(text+" talent", "TALENT", text));
        }
        if (TRAIT_LIST.contains(text))
        {
            return List.of(new SimplePrerequisite(text+ " trait", "TRAIT", text));
        }
        if (ITEM_LIST.contains(text))
        {
            return List.of(new SimplePrerequisite(text, "ITEM", text));
        }
        if (SPECIAL_LIST.contains(text))
        {
            return List.of(new SimplePrerequisite(text, "SPECIAL", text));
        }
        if (TRADITION_LIST.contains(text))
        {
            return List.of(new SimplePrerequisite("a member of " + text, "TRADITION", text));
        }
        if (FORCE_POWER_LIST.contains(text))
        {
            return List.of(new SimplePrerequisite(text+ " force power", "FORCE POWER", text));
        }
        if (FORCE_SECRETS.contains(text))
        {
            return List.of(new SimplePrerequisite(text+ " force secret", "FORCE SECRET", text));
        }
        if (FORCE_TECHNIQUES.contains(text))
        {
            return List.of(new SimplePrerequisite(text+ " force technique", "FORCE TECHNIQUE", text));
        }

        if ("Weapon Focus with chosen Exotic Weapon or Weapon Group".equals(text))//TODO this needs an option
        {
            return List.of(new SimplePrerequisite(text, "FEAT", "Weapon Focus (#chosen weapon#)"));
        }


        //OR: creates an ORPrequisite
        if (text.contains(" or "))
        {
            //printUnique(text);
            List<Prerequisite> prerequisites = new ArrayList<>();
            String[] tokens = text.split(", or | or |, ");

            for (String token : tokens)
            {
                prerequisites.addAll(parsePrerequisites(token, itemName));
            }
            return List.of(new OrPrerequisite(text, prerequisites));
        }

        if (text.contains(" and "))
        {
            List<Prerequisite> prerequisites = new ArrayList<>();
            String[] tokens = text.split(" and |, ");

            for (String token : tokens)
            {
                prerequisites.addAll(parsePrerequisites(token, itemName));
            }
            return List.of(new AndPrerequisite(text, prerequisites));
        }

        if (text.startsWith("Skill Focus") || text.startsWith("Weapon Proficiency")
                || text.startsWith("Weapon Focus") || text.startsWith("Armor Proficiency")
                || text.startsWith("Double Attack") || text.startsWith("Exotic Weapon Proficiency")
                || text.startsWith("Greater Weapon Focus") || text.startsWith("Devastating Attack")
                || text.startsWith("Penetrating Attack") || text.startsWith("Weapon Specialization")
                || text.startsWith("Brutal Attack"))
        {
            final String requirement = text.replace("Chosen Weapon", "#payload#").replace("Chosen Skill", "#payload#");
            if(requirement.contains("#payload#")){
                //printUnique(Context.getValue("name"), requirement);
            }
            return List.of(new SimplePrerequisite(requirement, "FEAT", requirement));
        }

        //TODO cleanup
        if (text.startsWith("Dark Side Score ") || text.startsWith("Dark Side Score:"))
        {
            String payload = text.substring(16);
            if (payload.equals("equal to Wisdom Score") || payload.startsWith(" Your Dark Side Score must be equal to your Wisdom score"))
            {
                return List.of(new SimplePrerequisite(text, "DARK SIDE SCORE", "@WISTOTAL"));
            }
            Integer m = Util.getNumber(text);
            if (m!=null)
            {
                return List.of(new SimplePrerequisite(text, "DARK SIDE SCORE", m.toString()));
            }
        }

        if (text.startsWith("Intelligence") || text.startsWith("Dexterity") || text.startsWith("Strength") || text.startsWith("Constitution") || text.startsWith("Wisdom") || text.startsWith("Charisma"))
        {
            return List.of(new SimplePrerequisite(text, "ATTRIBUTE", text));
        }

        if (text.startsWith("Base Attack Bonus"))
        {
            Integer m = Util.getNumber(text);
            if (m!=null)
            {
                return List.of(new SimplePrerequisite("Base Attack Bonus "+m, "BASE ATTACK BONUS", m.toString()));
            }
            throw new IllegalStateException("failed to parse: " + text);
        }

        if (text.endsWith("Character Level") || text.startsWith("Character Level") || text.startsWith("Minimum Level: "))
        {
            Integer m = Util.getNumber(text);
            if (m!= null)
            {
                return List.of(new SimplePrerequisite("Character Level "+m, "CHARACTER LEVEL", m.toString()));
            }
            throw new IllegalStateException("failed to parse: " + text);
        }


        //SUPER SPECIFIC THINGS
        Pattern proficiencyPattern = Pattern.compile("Proficient (?:with|in) ([\\s\\w]*)");
        Matcher proficiencyMatcher = proficiencyPattern.matcher(text);
        if (proficiencyMatcher.find())
        {
            String payload = proficiencyMatcher.group(1);
            if (payload.equals("Chosen Weapon"))
            {
                return List.of(new SimplePrerequisite("Proficient with #payload#", "PROFICIENCY", "#payload#"));
            }
            return List.of(new SimplePrerequisite(text, "PROFICIENCY", cleanItem(payload)));
        }

        Pattern forceTechniquePattern = Pattern.compile("(?:Any|At least) (1|one|One) Force Technique");
        Matcher forceTechniqueMatcher = forceTechniquePattern.matcher(text);
        if (forceTechniqueMatcher.find() || "Force Techniques: At least one".equals(text))
        {

            return List.of(new SimplePrerequisite(text.replace("Force Techniques: ", ""), "FORCE TECHNIQUE", "1"));
        }

        Pattern forceSecretPattern = Pattern.compile("(?:Any|At least) (1|one) Force Secret");
        Matcher forceSecretMatcher = forceSecretPattern.matcher(text);
        if (forceSecretMatcher.find())
        {

            return List.of(new SimplePrerequisite(text, "FORCE SECRET", "1"));
        }

        if ("Inherent Fly Speed".equals(text))
        {
            String finalText = text;
            return List.of(new OrPrerequisite(text, IntStream.range(1, 20).mapToObj(i -> new SimplePrerequisite(finalText, "TRAIT", "Fly Speed " + i)).collect(Collectors.toList())));
        }
        if ("at least 1 level in the Shaper class".equals(text))
        {
            return List.of(new SimplePrerequisite(text, "CLASS", "Shaper"));
        }
//        if("Member of The Sith".equals(text)){
//            return List.of(new SimplePrerequisite(text, "TRADITION", "The Sith"));
//        }
        if (text.toLowerCase().contains("member of the jedi"))
        {
            return List.of(new SimplePrerequisite("Must be a member of The Jedi Force Tradition", "TRADITION", "The Jedi"));
        }
        if (text.toLowerCase().contains("member of the sith"))
        {
            return List.of(new SimplePrerequisite("Must be a member of The Sith Force Tradition", "TRADITION", "The Sith"));
        }
        if ("Understand Binary".equals(text) || "Must have Binary as a learned language".equals(text))
        {
            return List.of(new SimplePrerequisite(text, "LANGUAGE", "Binary"));
        }
        //SPELLING ERROR
        if ("Impose Hesitance".equals(text))
        {
            return List.of(new SimplePrerequisite(text, "TALENT", "Impose Hesitation"));
        }
        //SPELLING ERROR
        if ("Vibroshield Mastery".equals(text))
        {
            return List.of(new SimplePrerequisite(text, "TALENT", "Vibroshield Master"));
        }
        //Bounty hunter class feature
        if ("Familiar Foe".equals(text))
        {
            return List.of(new SimplePrerequisite(text, "TRAIT", "Familiar Foe"));
        }
        //Officer hunter class feature
        if ("Share Talent (Any)".equals(text))
        {
            return List.of(new SimplePrerequisite(text, "TRAIT", "Share Talent"));
        }

        if ("Special: Must be a sworn defender of Emperor Roan Fel".equals(text))
        {
            return List.of(new SimplePrerequisite(text.substring(9), "SPECIAL", "sworn defender of Emperor Roan Fel"));
        }
        if ("Special: Must have built their own Lightsaber".equals(text))
        {
            return List.of(new SimplePrerequisite(text.substring(9), "SPECIAL", "Has Built Lightsaber"));
        }

        if (text.startsWith("AGE:"))
        {
            final String payload = text.substring(4);
            String low;
            String high;
            if (payload.contains("-"))
            {
                String[] toks = payload.split("-");
                low = toks[0];
                high = toks[1];
            } else
            {
                low = payload.replaceAll("\\+", "");
                high = "";
            }

            return List.of(new RangePrerequisite(payload, "AGE", low, high));
        }


        if (text.startsWith("TRAIT:"))
        {
            final String payload = text.substring(6);
            return List.of(new SimplePrerequisite(payload, "TRAIT", payload));
        }
        if (text.startsWith("GENDER:"))
        {
            final String payload = text.substring(7);
            return List.of(new SimplePrerequisite(payload, "GENDER", payload));
        }
        if (text.startsWith("EQUIPPED:"))
        {
            final String payload = text.substring(9);
            return List.of(new SimplePrerequisite(payload, "EQUIPPED", payload));
        }


        //printUnique(Context.getValue("name") + " --- " + text);


        return List.of();
    }

    private static String cleanItem(String s)
    {
        if (s.startsWith("a "))
        {
            s = s.substring(2);
        }
        if (s.startsWith("the "))
        {
            s = s.substring(4);
        }
        return s;
    }


    protected static String stringify(List<Prerequisite> children, String delimiter)
    {
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < children.size(); i++){
            if (i > 0){
                string.append(", ");
            }
            if(i == children.size() - 1){
                string.append(delimiter);
            }
            final Prerequisite child = children.get(i);
            if(child.getType().equals("AND") || child.getType().equals("OR")){
                string.append("(").append(child.getPlainText()).append(")");
            }else {
                string.append(child.getPlainText());
            }
        }

        return string.toString();
    }

    @Nonnull
    public abstract JSONObject toJSON();

    public String getPlainText()
    {
        return plainText;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("plainText", plainText)
                .add("type", type)
                .toString();
    }

    public String getType() {
        return type;
    }
}
