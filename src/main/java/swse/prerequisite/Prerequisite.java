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
import static swse.prerequisite.AndPrerequisite.and;
import static swse.prerequisite.NotPrerequisite.not;
import static swse.prerequisite.OrPrerequisite.or;
import static swse.prerequisite.SimplePrerequisite.simple;
import static swse.talents.TalentExporter.DUPLICATE_TALENT_NAMES;
import swse.util.Util;
import static swse.util.Util.printUnique;
//import static swse.util.Util.printUnique;

public abstract class Prerequisite implements JSONy, Copyable<Prerequisite> {
    public static final List<String> FEAT_LIST = List.of("Force of Personality", "Moving Target", "Empathic Inspiration", "Precise Shot", "Prehensile Trunks", "Force Boon", "Mechanical Martial Arts", "Improved Rapid Strike", "Bantha Herder", "Musician", "Massan Archaeology", "Sniper Shot", "Force Regimen Mastery", "Skill Challenge: Catastrophic Avoidance", "Cleave", "Dual Weapon Defense", "Explosives Expert", "Expert Droid Repair", "Martial Arts I", "Mission Specialist", "Deadeye", "Acrobatic Strike", "Dual Weapon Mastery III", "Weapon Finesse", "Cold Resistance", "Peace Brigade Commander", "Tonal Qualities", "Dashade Connections", "Solo Flourish", "Dreadful Countenance", "Micro Vision", "Autofire Assault", "Halt", "Weapon Focus", "Dodge", "Bite Attack", "Dust Farmer", "Expert Mime", "Bone Crusher", "Cut the Red Tape", "Stand Tall", "Wookiee Grip", "Verpine Tech", "Master Terraformer", "Expansion Region", "Fleet-Footed", "Return Fire", "Trip", "Focused Rage", "Unified Squadron", "K'tara Training", "Sadistic Strike", "Analytical Detachment", "Sure Climber", "Regenerative Healing", "Natural Storyteller", "Salvage Expert", "Toughness", "Greater Awareness", "Wary Sentries", "Acrobatic Ally", "Resilient Strength", "Instinctive Attack", "Biotech Designer", "Frightening Cleave", "Suppression Fire", "Medical Expertise", "Crowd Fighting", "Bloodthirsty (Feat)", "Lightning Draw", "Anointed Hunter", "Indomitable Personality", "Crossfire", "Hobbling Strike", "Coordinated Attack", "Pinpoint Accuracy", "Rapid Reaction", "Forest Stalker", "Survivor of Ryloth", "Bowcaster Marksman", "Hideous Visage", "Blessing of Uuru", "Returning Bug", "Flawless Pilot", "Aiwha Rider", "Bureaucratic Specialist", "Dominating Intelligence", "Sport Hunter", "Binary Mind", "Slippery Maneuver", "Tumble Defense", "Vehicle Drag", "Inborn Resilience", "Botanist", "Thick Skin", "Resolute Stance", "Insightful Diplomat", "Tae-Jitsu Training", "Improved Damage Threshold", "Quick Draw", "Subterranean", "Increased Agility", "Opportunistic Shooter", "Attack Combo (Ranged)", "Pincer", "Predictive Defense", "Signature Device", "Trodatome Repairs", "Poison Resistance", "Cowardly", "Deadly Sniper", "Flood of Fire", "Grand Army of the Republic Training", "Follower of Quay", "Fatal Hit", "Multi-Grab", "Exotic Weapon Proficiency", "Tamer", "Shake It Off", "Unknown Regions", "Unreliable Repairs", "Deceitful", "Jango Jumper", "Regeneration", "Accelerated Strike", "Mathematical Mind", "Burrowing Tusks", "Running Attack", "Long Haft Strike", "Superior Shaping", "Resurgence", "Wide Frequency Vision", "A Few Maneuvers", "One with the Force", "Fight Through Pain", "Biotech Specialist", "Disavowed", "Recovering Surge", "Exile's Adaptation", "Resurgent Vitality", "Strong Bellow", "Desert Native", "Primitive Warrior", "Force Readiness", "Armor Proficiency (Light)", "Expert Warrior", "Annealing Rage", "Double Attack", "Impersonate", "Strafe", "Biologist Field Team", "Feat of Strength", "Silver Tongue", "Roper", "Unwavering Focus", "Gaderffii Master", "Surgical Expertise", "Weapon Proficiency", "Unwavering Devotion", "In Tune with the Force", "Prime Shot", "Metamorph II", "Rapid Shot", "Rapid Strike", "Power Blast", "Melee Defense", "Natural Leader", "Hasty Modification", "Extensive Connections", "Instinctive Tinkerer", "Logic Upgrade: Pyrowall", "Tactical Advantage", "Triple Attack", "Artillery Shot", "Knife Trick", "Targeted Research", "Disturbing Presence", "Starship Designer", "Flèche", "Perfect Swimmer", "Shadowbox", "Risk Taker", "Cornered", "Reesarian Cooperation", "Advanced Education", "Rancor Crush", "Improved Natural Healing", "Whirlwind Attack", "Composer", "Flash and Clear", "Whiner", "Colonies", "Tree Climber", "Improved Opportunistic Trickery", "Spray Shot", "Superior Tech", "Tough Hide", "Unhindered Approach", "Imperceptible Liar", "Instinctive Diver", "Skill Training", "Mounted Defense", "Greater Confusion", "Pin", "Dual Weapon Strike", "Perseverance", "Improvised Tools", "Follow Through", "Droid Shield Mastery", "Crush", "Carouser", "Master Tracker", "Strong in the Force", "Talented", "Armor Proficiency (Heavy)", "Maniacal Charge", "Strong Style", "Fast Surge", "Close Combat Escape", "Oathbound", "Skill Mastery", "Swarm", "Booming Voice", "Givin Designer", "Warrior Heritage", "Pacifist", "Bad Feeling", "Rapport", "Feline Agility", "Imperial Military Training", "Wicked Strike", "Covert Operatives", "Powerful Faith", "Improvised Weapon Mastery", "Partisan Upbringing", "Experienced Medic", "Targeted Area", "Easily Repaired", "Veknoid Brew", "Fortifying Recovery", "Galactic Alliance Military Training", "Cybernetic Surgery", "Hijkata Training", "Overwhelming Attack", "Unstoppable Combatant", "Instinctive Perception", "Tech Specialist", "Skill Focus", "Outer Rim", "Alertness", "Trample", "Tool Frenzy", "Lanvarok Master", "Sense Force Alignment", "Force Resistance", "Aura of Oppression", "Keeping a Secret", "Opportunistic Retreat", "Controlled Rage", "Committed", "Slicer Team", "Charging Fire", "Jee-dai Heretic", "Rapid Assault", "Dreadful Rage", "Mighty Throw", "Lasting Influence", "Impetuous Move", "Distracting Droid", "Perfect Intuition", "Far Shot", "Intimidator", "Channel Rage", "Unstoppable Force", "Duck and Cover", "Martial Arts III", "Dual Weapon Mastery II", "Adaptable Talent", "Triple Crit", "Mighty Swing", "Improved Disarm", "Brink of Death", "Coordinated Barrage", "Unswerving Resolve", "Instinctive Acrobat", "Combat Trickery", "Musical Genius", "Vitality Surge", "Improved Bantha Rush", "Node", "Mobility", "Disarming Scream", "Adept Networkers", "Angled Throw", "Blaster Barrage", "Aquatic Specialists", "Dashade Heritage", "Flurry", "Starship Tactics", "Throw", "Diving Attack", "Treacherous", "K'thri Training", "Recurring Success", "Scavenger", "Biotech Surgery", "Droidcraft", "Exceptional Hearing", "Blaster Geometry", "Nimble Hands and Feet", "Tusken Heritage", "Leader of Droids", "Sabacc Face", "Shrewd Bargainer", "Master of Disguise", "Medical Team", "Punishment", "Aiming Accuracy", "Extra Rage", "Collateral Damage", "Gearhead", "Officer Candidacy Training", "Mounted Regiment", "Web-Engineering", "Logic Upgrade: Skill Swap", "Improved Natural Telepathy", "Planetary Peacemaker", "Confident", "I Own Your Name", "Sensor Link", "Turn and Burn", "Insectoid Animosity", "Elomin Force Adept", "Vehicle Systems Expertise", "Overlooked", "Power Attack", "Unleashed", "Advantageous Attack", "Clawed Subspecies", "Navicomputer Brain", "Withdrawal Strike", "Kilmaulsi Heritage", "Echani Training", "Dark Inspiration", "Gossam Commando Training", "Rapid Takedown", "Stoicism", "Multi-Targeting", "Tail Trick", "Nikto Survival", "Improved Diving Attack", "Metamorph", "Tail Technique", "Trench Warrior", "Stava Training", "Battering Attack", "Shady Contacts", "Greater Accuracy", "Tireless Pursuer", "Force Sensitivity", "Read the Winds", "Wilderness Specialists", "Maze Navigator", "Republic Military Training", "Tireless Squad", "Fringe Benefits", "Sentient Tech Affinity", "Recall", "Underestimated", "Nimble Team", "Familiar Face", "Metamorph III", "Disabler", "Dive for Cover", "Forceful Recovery", "Improved Sleight of Hand", "Pistoleer", "Jedi Heritage", "Vua'sa Training", "Mind of Reason", "Opportunistic Trickery", "Relentless Attack", "Greater Deathstrike", "Burst Fire", "Tal-Gun", "Droid Hunter", "Networking Contacts", "Hunter's Instincts", "Gand Heritage", "Steadying Position", "Critical Strike", "Force Training", "Wilderness First Aid", "Droid Focus", "Skill Challenge: Last Resort", "Quick Comeback", "Vehicular Surge", "Pheromone Familiarity", "Ion Shielding", "Duplicitous Team", "Mon Calamari Shipwright", "Deft Charge", "Pirate Heritage", "Wrruushi Training", "Marksman", "Sniper", "Disarming Charm", "Imposing Glare", "Implant Training", "Fleet Tactics", "Thisspiasian Trance", "Devastating Bellow", "Pitiless Warrior", "Irrefutable Logic", "Elder's Knowledge", "Kaminoan Grace", "Wary Defender", "Canine Senses", "Skill Challenge: Recovery", "Verdanaian Training", "Combat Reflexes", "Improved Charge", "Mid Rim", "Improved Defenses", "Vong's Faith", "Riflemaster", "Fast Style", "Careful Shot", "Drag Away", "Impulsive Flight", "Separatist Military Training", "Jack of All Trades", "Ritualistic Tattoos", "Hand Gestures", "Mandalorian Training", "Rebel Military Training", "Sith Heritage", "Deep Sight", "Conditioning", "Unwavering Resolve", "Keen Scent", "Armor Proficiency (Medium)", "Heightened Senses", "Staggering Attack", "Scion of Dorin", "Great Pride", "Great Cleave", "Slammer", "Dual Weapon Mastery I", "Martial Arts II", "Deceptive Drop", "Logic Upgrade: Cross-Platform", "Point-Blank Shot", "Crocodilian Bite", "Wroshyr Rage", "Quick Read", "Shield Surge", "Web Brain", "Attack Combo (Melee)", "Cunning Attack", "Ryn Network", "Knock Heads", "Abject Cowardice", "Meat Shield", "Core Worlds", "Studio Musician", "Technical Experts", "New Republic Military Training", "Greedy", "Sith Military Training", "Nature Specialist", "Powerful Charge", "Advantageous Cover", "Flawless Mechanic", "Bothan Will", "Focusing Ritual", "Expert Briber", "Burrowing Flank", "Informer", "Experienced Negotiators", "Mounted Combat", "Assured Attack", "Baragwin Connections", "Unassuming Presence", "Reading the Swarm", "Justice Seeker", "Momentum Strike", "Swift Claws", "Brutish", "Bantha Rush", "Brilliant Defense", "Deep Core", "Human Heritage", "Attack Combo (Fire and Strike)", "Ascension Specialists", "Hyperblazer", "Vigilant Squad", "Reesarian Bond", "Friends in Low Places", "Improved Grab", "Gunnery Specialist", "Battle Anthem", "Larcenous Scavenging", "Pall of the Dark Side", "Demoralizing Strike", "Fast Swimmer", "Grazing Shot", "Duty Bound", "Instinctive Defense", "Instinctive Flexibility", "Grab Back", "Logic Upgrade: Self-Defense", "Hold Together", "Lapti Nek", "Increased Resistance", "Teräs Käsi Training", "Grapple Resistance", "Desperate Gambit", "Logic Upgrade: Tactician", "Ritual Mastery", "Quick Skill", "Forceful Blast", "Confident Success", "Taung Heritage", "Agile Riposte", "Damage Conversion", "Burst of Speed", "Savage Attack", "Erratic Target", "Sharp Senses", "Vehicular Combat", "Heavy Hitter", "Acrobatic Dodge", "Staggering Attack (GaW)", "Tactical Genius", "Destructive Force", "Linguist", "Never Surrender", "Darkness Dweller", "Methodical Technician", "Dexterous Feint", "Ample Foraging", "Rejuvenating Rest", "Autofire Sweep", "Brachiated Movement", "Keen Hearing", "Gungan Weapon Master", "Stay Up", "Spacer's Surge", "Jedi Familiarity", "Extra Second Wind", "Inner Rim", "Zero Range", "Powerful Rage", "Banter", "Medium Style", "Learned", "Veteran Spacer", "Beaked Subspecies");
    public static final List<String> SPECIES_LIST = List.of("Aar'aa", "Ab'Ugartte", "Abednedo", "Abinyshi", "Abyssin", "Adarian", "Advozse", "Aing-Tii", "Aki-Aki", "Aleena", "Altiri", "Amani", "Amaran", "Anarrian", "Anomid", "Anx", "Anzati", "Anzellan", "Aqualish", "Arcona", "Ardennian", "Arkanian", "Arkanian Offshoot", "Assembler", "Ayrou", "Balosar", "Barabel", "Baragwin", "Bartokk", "Besalisk", "Bimm", "Bimm (Near-Human)", "Bith", "Bivall", "Blarina", "Blood Carver", "Boltrunian", "Bothan", "Caamasi", "Caarite", "Calibop", "Ergesh", "Cathar", "Catuman", "Celegian", "Cerean", "Chadra-Fan", "Chagrian", "Charon", "Chazrach", "Chev", "Chevin", "Chironian", "Chiss", "Chistori", "Ciasi", "Clawdite", "Codru-Ji", "Colicoid", "Cosian", "Coway", "Cragmoloid", "Croke", "Cyclorrian", "Dantari", "Dashade", "Defel", "Devaronian", "Devlikk", "Diathim", "Didynon", "Dowutin", "Drackmarian", "Draethos", "Drall", "Dressellian", "Dug", "Duinuogwuin", "Dulok", "Duros", "Dybrinthe", "Ebranite", "Ebruchi", "Eirrauc", "Elom", "Elomin", "Em'liy", "Equani", "Esh-kha", "Evocii", "Ewok", "Falleen", "Farghul", "Feeorin", "Felucian", "Filordus", "Filvian", "Firrerreo", "Flesh Raider", "Fosh", "Frozian", "Gamorrean", "Gand", "Gastrulan", "Gen'Dai", "Geonosian", "Givin", "Gormak", "Gossam", "Gotal", "Gozzo", "Gran", "Gree", "Gungan", "Gurlanin", "H'nemthe", "Harch", "Hasikian", "Herglic", "Hexaclops", "Ho'Din", "Holwuff", "Houk", "Hrakian", "Human", "Huralok", "Hutt", "Hysalrian", "Iktotchi", "Imroosian", "Iotran", "Ishi Tib", "Ithorian", "Jablogian", "Jawa", "Jenet", "Kaleesh", "Kamarian", "Kaminoan", "Karkarodon", "Kel Dor", "Kerestian", "Kerkoiden", "Kessurian", "Khil", "Khommite", "Kian'thar", "Killik", "Kilmaulsi", "Kissai", "Kitonak", "Klatooinian", "Kobok", "Koorivar", "Kowakian", "Krevaaki", "Krish", "Kubaz", "Kudon", "Kushiban", "Kyuzo", "Lafrarian", "Lannik", "Lasat", "Lepi", "Lugubraa", "Lurmen", "Lurrian", "Maelibus", "Mandallian", "Mantellian Savrip", "Massassi", "Meerian", "Melitto", "Melodie", "Menahuun", "Miraluka", "Mirialan", "Mon Calamari", "Morseerian", "Mrlssi", "Murachaun", "Mustafarian, Northern", "Southern Mustafarian", "Muun", "Myneyrsh", "Nagai", "Nautolan", "Nazren", "Nazzar", "Nediji", "Neimoidian", "Nelvaanian", "Neti", "Nikto", "Nimbanel", "Noehon", "Noghri", "Nosaurian", "Nuknog", "Nyriaanan", "O'reenian", "Octeroid", "Omwati", "Ongree", "Ortolan", "Pa'lowick", "Pacithhip", "Paigun", "Pantoran", "Patitite", "Pau'an", "Phindian", "Pho Ph'eahian", "Polis Massan", "Psadan", "Pyke", "Qiraash", "Quarren", "Quermian", "Quor'sav", "Qwohog", "Rakata", "Rakririan", "Ranat", "Ranth", "Rattataki", "Reesarian", "Replica Droid", "Republic Clone", "Revwien", "Rishii", "Rodian", "Rodisar", "Roonan", "Ruurian", "Rybet", "Ryn", "S'kytri", "Saheelindeeli", "Sakiyan", "Sanyassan", "Sarkan", "Sauvax", "Selkath", "Selonian", "Sephi", "Shani", "Shard", "Shi'ido", "Shistavanen", "Siniteen", "Sith Offshoot", "Skakoan", "Skrilling", "Sljee", "Sludir", "Sluissi", "Snivvian", "Sorcerer of Rhand", "Spiner", "Squib", "Ssi-Ruuk", "Stenax", "Stennes Shifter", "Stereb", "Sullustan", "Sunesi", "Swokes Swokes", "Sy Myrthian", "T'landa Til", "T'surr", "Talortai", "Talz", "Tarasin", "Tarro", "Tash", "Taung", "Tchuukthai", "Teedo", "Teek", "Tel'a", "Temolak", "Terrelian", "Thakwaash", "Theelin", "Thisspiasian", "Tintinna", "Tiss'shar", "Tof", "Togorian", "Togruta", "Toong", "Toydarian", "Trandoshan", "Trianii", "Tridactyl", "Trodatome", "Trogodile", "Tunroth", "Turazza", "Tusken Raider", "Twi'lek", "Tynnan", "Ubese", "Ugnaught", "Ugor", "Umbaran", "Utai", "Vagaari", "Vahla", "Veknoid", "Verpine", "Vippit", "Viraanntesse", "Vodran", "Vor", "Vorzydiak", "Vratix", "Vulptereen", "Vultan", "Vurk", "Vuvrian", "Weequay", "Whiphid", "Wookiee", "Woostoid", "Wroonian", "X'Ting", "Xexto", "Yarkora", "Yevetha", "Yinchorri", "Yuuzhan Vong", "Yuzzem", "Yuzzum", "Zabrak", "Zehethbra", "Zeltron", "Zilkin", "Zuguruk", "Zygerrian", "1st-Degree Droid Model", "2nd-Degree Droid Model", "3rd-Degree Droid Model", "4th-Degree Droid Model", "5th-Degree Droid Model", "Astromech Droid", "Battle Droid", "Labor Droid", "Mechanic Droid", "Medical Droid", "Probe Droid", "Protocol Droid", "Service Droid");
    public static final List<String> TRAIT_LIST = List.of("Shapeshift", "Rage", "Bellow");
    public static final List<String> ITEM_LIST = List.of("Claw", "Hovering", "Flying", "Wheeled", "Tracked", "Basic Processor", "Hand", "Shield Generator", "Blaster", "Heuristic Processor");
    public static final List<String> TALENT_LIST = List.of("Armor Mastery (Armor Specialist Talent Tree)", "Armored Defense", "Improved Armored Defense", "Juggernaut", "Second Skin", "Shield Expert", "Art of Concealment", "Fast Talker", "Hidden Weapons", "Illicit Dealings", "Surprise Strike", "Advanced Planning", "Blend In (Master of Intrigue Talent Tree)", "Done It All", "Get Into Position (Master of Intrigue Talent Tree)", "Master Manipulator (Master of Intrigue Talent Tree)", "Retaliation", "Attune Weapon", "Empower Weapon", "Force Talisman", "Greater Force Talisman", "Focused Force Talisman", "Force Throw", "Greater Focused Force Talisman", "Primitive Block", "Everyone Has a Face", "Mistryl Weapon Training", "Mistryl Unarmed Combat", "Out of the Shadows", "Charm Beast (Beastwarden Talent Tree)", "Bonded Mount", "Entreat Beast", "Soothing Presence", "Wild Sense", "Animal Companion", "Animal Senses", "Calming Aura", "Comprehend Speech", "Improved Companion", "Nature Sense", "Shared Aptitude", "Bunker Buster", "Defensive Measures", "Enhance Cover", "Escort Fighter", "Launch Point", "Obscuring Defenses", "Relocate", "Safe Passage", "Safe Zone", "Zone of Recuperation", "Armored Spacer", "Attract Privateer", "Blaster and Blade I", "Blaster and Blade II", "Blaster and Blade III", "Boarder", "Ion Mastery", "Multiattack Proficiency (Advanced Melee Weapons) (Privateer Talent Tree)", "Preserving Shot", "Attract Minion", "Impel Ally I", "Impel Ally II", "Attract Superior Minion", "Bodyguard I", "Bodyguard II", "Bodyguard III", "Contingency Plan", "Impel Ally III", "Inspire Wrath", "Master's Orders", "Shelter", "Tactical Superiority", "Tactical Withdraw", "Urgency", "Wealth of Allies", "Bigger Bang", "Capture Droid", "Custom Model", "Improved Jury-Rig", "Improvised Device", "Conceal Force Use", "Force Direction", "Force Momentum", "Past Visions", "Blackguard Initiate", "Wilder Marauder", "Wilder Ravager", "Wilder Trinity Assassin", "Ignore Damage Reduction", "Teräs Käsi Basics", "Teräs Käsi Mastery", "Unarmed Counterstrike", "Unarmed Parry", "Direct", "Impart Knowledge", "Insight of the Force", "Master Advisor", "Scholarly Knowledge", "Block", "Deflect", "Lightsaber Defense", "Weapon Specialization (Lightsabers)", "Lightsaber Throw", "Redirect Shot", "Cortosis Gauntlet Block", "Precise Redirect", "Precision", "Riposte", "Shoto Focus", "Aura of Freedom", "Folded Space Mastery", "Liberate", "Many Shades of the Force", "Spatial Integrity", "Defensive Roll", "Force Intuition (Force Warrior Talent Tree)", "Improved Defensive Roll", "Unarmed Specialisation", "Close Cover", "Outrun", "Punch Through", "Small Target", "Watch This", "Another Coat of Paint", "Fly Casual", "Fade Out", "Keep Together", "Prudent Escape", "Reactive Stealth", "Sizing Up", "Blend In (Spy Talent Tree)", "Incognito", "Improved Surveillance", "Intimate Knowledge", "Surveillance", "Traceless Tampering", "Disciplined Trickery", "Group Perception", "Hasty Withdrawal", "Stalwart Subordinates", "Stay in the Fight (Fugitive Commander Talent Tree)", "Stealthy Withdrawal", "At Peace", "Attuned", "Focused Attack", "Surge of Light", "Resist Aging", "Engineering Savant", "Enhance Implant", "Identify Droid", "Modify Prosthetic", "Patch Job", "Patient Builder", "Skilled Mechanic", "Technological Master", "Bioengineering", "Binary Mindset", "Determine Weakness", "Focused Research", "Identify Creature", "Poisoncraft", "Smelling Salts", "Echoes of the Force", "Jedi Quarry", "Prepared for Danger", "Sense Deception", "Unclouded Judgement", "Crucial Advice", "Distracting Apparition", "Guardian Spirit", "Manifest Guardian Spirit", "Vital Encouragement", "Seyugi Cyclone", "Mobile Whirlwind", "Repelling Whirlwind", "Sudden Storm", "Tempest Tossed", "Gang Leader", "Melee Assault", "Melee Brute", "Melee Opportunist", "Squad Brutality", "Squad Superiority", "Power of the Dark Side", "Dark Presence", "Revenge", "Swift Power", "Consumed by Darkness", "Dark Preservation", "Dark Side Savant", "Drain Knowledge", "Transfer Essence", "Wrath of the Dark Side", "Blast of Hatred", "Crushing Power", "Dark Dream", "Dark Power", "Dark Side Maelstrom", "Instruction", "Idealist", "Know Your Enemy", "Known Dissident", "Lead by Example (Ideologue Talent Tree)", "Adept Assistant", "Mechanics Mastery", "Vehicle Mechanic", "Burst Transfer", "On-Board System Link", "Quick Astrogation", "Scomp Link Slicer", "Evasion", "Extreme Effort", "Sprint", "Surefooted", "Adrenaline Surge", "Voices", "Midi-chlorian Challenge", "Midi-chlorian Control", "Stop Messing With Me!", "Midi-chlorian Mastery", "Break Program", "Heuristic Mastery", "Scripted Routines", "Ultra Resilient", "Advantageous Strike", "Dirty Tricks", "Dual Weapon Flourish I", "Dual Weapon Flourish II", "Master of Elegance", "Multiattack Proficiency (Advanced Melee Weapons) (Melee Duelist Talent Tree)", "Out of Nowhere", "Single Weapon Flourish I", "Single Weapon Flourish II", "Arrogant Bluster", "Band Together", "Galactic Guidance", "Rant", "Self-Reliant", "Hyperdriven", "Spacehound", "Starship Raider", "Stellar Warrior", "Cramped Quarters Fighting", "Deep Space Raider", "Make a Break for It", "Battlefield Medic", "Bring Them Back", "Emergency Team", "Extra First Aid", "Medical Miracle", "Natural Healing", "Second Chance", "Steady Under Pressure", "Psychiatric Caregiver", "Mental Health Specialist", "Reconnaissance Team Leader", "Close-Combat Assault", "Get Into Position (Reconnaissance Talent Tree)", "Reconnaissance Actions", "Piercing Hit", "Quicktrap", "Speedclimber", "Surprisingly Quick", "Tripwire", "Discblade Arc", "Distant Discblade Throw", "Recall Discblade", "Telekinetic Vigilance", "Weapon Specialization (Discblade)", "Discblade Mastery", "Buried Presence", "Conceal Other", "Insightful Aim", "Vanish", "Clear Mind", "Dark Side Sense", "Dark Side Scourge", "Force Haze", "Resist the Dark Side", "Dampen Presence", "Dark Retaliation", "Dark Side Bane", "Gradual Resistance", "Master of the Great Hunt", "Persistent Haze", "Prime Targets", "Reap Retribution", "Sense Primal Force", "Sentinel Strike", "Sentinel's Gambit", "Sentinel's Observation", "Steel Resolve", "Unseen Eyes", "Force Track", "Intuit Danger", "Sentinel's Insight", "Cause Mutation", "Rapid Alchemy", "Sith Alchemy (Sith Alchemy Talent Tree)", "Sith Alchemy Specialist", "Inspire Loyalty", "Undying Loyalty", "Punishing Protection", "Protector Actions", "Blaster Turret I", "Blaster Turret II", "Blaster Turret III", "Ion Turret", "Stun Turret", "Turret Self-Destruct", "Mind Probe", "Perfect Telepathy", "Psychic Citadel", "Psychic Defenses", "Telepathic Intruder", "Dogfight Gunner", "Expert Gunner", "Quick Trigger", "System Hit", "Crippling Hit", "Fast Attack Specialist", "Great Shot", "Overcharged Shot", "Synchronized Fire", "Field Detection", "Improved Force Sight", "Luka Sene Master", "Quickseeing", "Born Leader", "Coordinate", "Distant Command", "Fearless Leader", "Rally", "Trust", "Commanding Presence (Leadership Talent Tree)", "Coordinated Leadership", "Reactionary Attack", "Tactical Savvy", "Unwavering Ally", "Force Fortification", "Greater Weapon Focus (Lightsabers)", "Greater Weapon Specialization (Lightsabers)", "Multiattack Proficiency (Lightsabers)", "Severing Strike", "Improved Lightsaber Throw", "Improved Riposte", "Improved Redirect", "Lightsaber Form Savant", "Thrown Lightsaber Mastery", "Shoto Master", "Twin Attack (Lightsabers)", "Double Agent", "Enemy Tactics", "Feed Information", "Friendly Fire", "Protection", "Competitive Drive", "Competitive Edge", "Corporate Clout", "Impose Confusion", "Impose Hesitation", "Willful Resolve", "Wrong Decision", "Champion", "Quick Study", "Simple Opportunity", "Warrior's Awareness", "Warrior's Determination", "In Balance", "Master of Balance", "Je'daii Blade Expert", "There is No Fear", "Enhanced Vision", "Impenetrable Cover", "Invisible Attacker", "Mark the Target", "Maximize Cover", "Shellshock", "Soften the Target", "Triangulate", "Disciplined Strike", "Telekinetic Power", "Telekinetic Savant", "Aversion", "Force Flow", "Illusion", "Illusion Bond", "Influence Savant", "Link", "Masquerade", "Move Massive Object", "Suppress Force", "Telekinetic Prodigy", "Telepathic Influence", "Telepathic Link", "Force Bond", "Force Prodigy", "Influence Natural", "Kinetic Might", "Telekinetic Natural", "Transfer Force", "Cover Bracing", "Intentional Crash", "Nonlethal Tactics", "Pursuit", "Respected Officer", "Slowing Stun", "Takedown", "Amphistaff Block", "Amphistaff Riposte", "Spearing Accuracy", "Spiral Shower", "Venom Rake", "Aggressive Surge", "Blast Back", "Fade Away", "Second Strike", "Swerve", "Battlefield Remedy", "Grizzled Warrior", "Reckless", "Seen It All", "Tested in Battle", "Force Directed Shot", "Negate and Redirect", "Rising Anger", "Rising Panic", "Noble Fencing Style", "Demoralizing Defense", "Leading Feint", "Personal Affront", "Transposing Strike", "Force Immersion", "Immerse Another", "Ride the Current", "Surrender to the Current", "White Current Adept", "Adrenaline Implant", "Precision Implant", "Resilience Implant", "Speed Implant", "Strength Implant", "Cover Your Tracks", "Difficult to Sense", "Force Veil", "Jedi Network", "Battle Analysis", "Cover Fire", "Demolitionist", "Draw Fire", "Harm's Way", "Indomitable", "Tough as Nails", "Coordinated Effort", "Dedicated Guardian", "Dedicated Protector", "Defensive Position", "Hard Target", "Keep Them at Bay", "Out of Harm's Way (Commando Talent Tree)", "Combat Instincts", "Grenadier", "Dull the Pain", "Interrogator", "Medical Droid", "Known Vulnerability", "Medical Analyzer", "Science Analyzer", "Triage Scan", "Corellian Security Force", "Journeyman Protector", "Sector Ranger", "Commanding Officer", "Coordinated Tactics", "Fire at Will", "Squad Actions", "Flurry of Blows", "Hardened Strike", "Punishing Strike", "Battlefield Sacrifice", "Built to Suffer", "Crusader's Fury", "Embrace the Pain", "Glorious Death", "Hail of Bugs", "Path of Humility", "Pray to the Pardoner", "Priest's Expertise", "Ritual Expertise", "Trickster's Disciple", "Vua'sa Expertise", "Yammka's Devotion", "Hunter's Mark", "Hunter's Target", "Notorious (Bounty Hunter Talent Tree)", "Nowhere to Hide", "Relentless", "Ruthless Negotiator", "Detective", "Dread", "Electronic Trail", "Familiar Enemies", "Familiar Situation", "Fearsome", "Jedi Hunter", "Nowhere to Run", "Quick Cuffs", "Revealing Secrets", "Signature Item", "Tag", "Akk Dog Master", "Akk Dog Trainer's Actions", "Akk Dog Attack Training", "Protective Reaction", "Lor Pelek", "Vibroshield Master", "Bolster Ally", "Ignite Fervor", "Inspire Confidence", "Inspire Haste", "Inspire Zeal", "Beloved", "Willpower", "Channel Vitality", "Closed Mind", "Esoteric Technique", "Mystic Mastery", "Regimen Mastery", "Commanding Presence (Mercenary Talent Tree)", "Dirty Fighting", "Feared Warrior", "Focused Warrior", "Ruthless (Mercenary Talent Tree)", "Combined Fire (Mercenary Talent Tree)", "Mercenary's Determination", "Mercenary's Grit", "Mercenary's Teamwork", "Accurate Blow", "Close-Quarters Fighter", "Ignore Armor", "Improved Stunning Strike", "Whirling Death", "Guaranteed Boon", "Leading Skill", "Learn from Mistakes", "Try Your Luck", "Device Jammer", "Droid Jammer", "Extreme Explosion", "Mine Mastery", "Shaped Explosion", "Skilled Demolitionist", "Attune Armor", "Force Cloak", "Force Cloak Mastery", "Linked Defense", "Force Warning", "Improved Quick Draw (Lightsabers)", "Sheltering Stance", "Vigilance", "Watchman's Advance", "Black Market Buyer", "Excellent Kit", "Just What is Needed", "Only the Finest", "Right Gear for the Job", "Find Openings", "Hit the Deck", "Lure Closer", "Risk for Reward", "Trick Step", "Dark Deception", "Improved Sentinel Strike", "Improved Sentinel's Gambit", "Rebuke the Dark", "Taint of the Dark Side", "Believer Intuition", "Defense Boost", "Hardiness", "High Impact", "Sith Reverence", "Thunderous Bellow", "Beast Speech", "Commune with Nature", "Constriction", "Combined Fire (Naval Officer Talent Tree)", "Fleet Deployment", "Fleet Tactics", "It's a Trap!", "Legendary Commander", "Ataru", "Djem So", "Jar'Kai", "Juyo", "Makashi", "Niman", "Shien", "Shii-Cho", "Sokan", "Soresu", "Trakata", "Vaapad", "Dun Möch", "Maho-Kai", "Tripzest", "Acrobatic Recovery", "Battle Meditation", "Elusive Target", "Force Intuition (Jedi Guardian Talent Tree)", "Resilience", "Close Maneuvering", "Cover Escape", "Defensive Acuity", "Exposing Strike", "Forceful Warrior", "Grenade Defense", "Guardian Strike", "Hold the Line", "Immovable", "Improved Battle Meditation", "Mobile Combatant (Jedi Guardian Talent Tree)", "Battle Meld", "Blaster and Saber", "Blaster Deflect", "Combat Sense", "Force Meld (Jedi Guardian Talent Tree)", "Guardian's Insight", "Jedi Ready", "Mettle", "Oafish", "Outsider's Eye", "Outsider's Query", "Wary", "Deny Move", "Extended Critical Range (Heavy Weapons)", "Extended Critical Range (Rifles)", "Flurry Attack", "Knockback", "Reduce Defense", "Reduce Mobility", "Extended Critical Range (Simple Weapons)", "Initiate of Vahl", "Reading the Flame", "Sword of Vahl", "Vahl's Brand", "Vahl's Flame", "Anticipate Movement", "Forewarn Allies", "Get Down", "Heavy Fire Zone", "Summon Aid", "Adept Spellcaster (Sorcerer of Tund Talent Tree)", "Dark Lore", "Delusion", "Illusionary Disguise", "Understanding the Force", "Unity of the Force", "Bloodthirsty", "Fight to the Death", "Keep Them Reeling (Piracy Talent Tree)", "Raider's Frenzy", "Raider's Surge", "Savage Reputation", "Take Them Alive", "Cargo Hauler", "Environmentally Shielded", "Power Supply", "Durable", "Heavy-Duty Actuators", "Load Launcher", "Task Optimization", "Force Blank", "Lightsaber Evasion", "Precision Fire", "Steel Mind", "Strong-Willed", "Telekinetic Resistance", "Fast Repairs", "Hotwire", "Quick Fix", "Personalized Modifications", "Defensive Jab", "Nimble Dodge", "Retaliation Jab", "Stinging Jab", "Stunning Shockboxer", "Cower Enemies", "Force Interrogation", "Inquisition", "Unsettling Presence", "Knight's Morale", "Oath of Duty", "Praetoria Ishu", "Praetoria Vonil", "Strength of the Empire", "Recruit Enemy", "Bolstered Numbers", "Noble Sacrifice", "Stay in the Fight (Rebel Recruiter Talent Tree)", "Team Recruiting", "Jet Pack Training", "Burning Assault", "Improved Trajectory", "Jet Pack Withdraw", "Aerial Maneuvers", "Comrades in Arms", "Focused Targeting", "Phalanx", "Stick Together", "Watch Your Back", "Cyborg Avatar", "Cyborg Martyr", "Droid Receptacle", "Enlightened Meditation", "Serene Courage", "SpyNet Agent", "Bothan Resources", "Knowledge is Life", "Knowledge is Power", "Knowledge is Strength", "Six Questions", "Bomb Thrower", "For the Cause", "Make an Example", "Revolutionary Rhetoric", "Brutal Attack", "Call Out", "Distracting Attack", "Exotic Weapons Master", "Lockdown Strike", "Multiattack Proficiency (Exotic Weapons)", "Personal Vendetta", "Unstoppable", "Defensive Electronics", "Ion Resistance 10", "Soft Reset", "Modification Specialist", "Repair Self", "Just a Droid", "Swift Droid", "Body Control", "Physical Surge", "Soft to Solid", "Wan-Shen Defense", "Wan-Shen Kata", "Wan-Shen Mastery", "Damage Reduction 10", "Equilibrium", "Force Focus", "Force Recovery", "Beast Trick", "Channel Energy", "Force Exertion", "Force Harmony", "Force Suppression", "Indomitable Will", "The Will to Resist", "Telekinetic Stability", "Force Absorb", "Force Concealment", "Force Stealth", "Trust the Force", "Velocity", "Vitality Strike", "Force Power Adept", "Force Treatment (Force Adept Talent Tree)", "Fortified Body", "Instrument of the Force", "Long Call", "Mystical Link", "Battle Precognition", "Combat Trance", "Improvised Weapon Mastery", "Twin Weapon Style", "Twin Weapon Mastery", "Shoto Pin", "Cheap Trick", "Easy Prey", "Quick Strike (Brigand Talent Tree)", "Sly Combatant", "Echani Expertise", "Hijkata Expertise", "K'tara Expertise", "K'thri Expertise", "Stava Expertise", "Tae-Jitsu Expertise", "Wrruushi Expertise", "Verdanaian Expertise", "Concentrate All Fire", "Escort Pilot", "Lose Pursuit", "Run Interference", "Wingman Retribution", "Action Exchange", "Force Delay", "Imbue Item", "Knowledge of the Force", "Advantageous Opening", "Retribution", "Slip By (Opportunist Talent Tree)", "Thrive on Chaos", "Vindication", "Cheap Shot", "No Escape", "Opportunistic Strike", "Slippery Strike", "Strike and Run", "Gimmick", "Master Slicer", "Trace", "Electronic Forgery", "Electronic Sabotage", "Security Slicer", "Virus", "Crash and Burn", "Ghost in the Machine", "Light Side Slicer", "Defensive Circle", "Force Revive", "Jedi Battle Commander", "Slashing Charge", "Mobile Attack (Lightsabers)", "Deception Awareness", "Greater Weapon Focus (Fira)", "Progenitor's Call", "Waveform", "Fira Mastery", "Hidden Movement", "Improved Stealth", "Total Concealment", "Dig In", "Extended Ambush", "Ghost Assailant", "Hide in Plain Sight", "Hunker Down", "Shadow Striker", "Slip By (Camouflage Talent Tree)", "Expert Grappler", "Gun Club", "Melee Smash", "Stunning Strike", "Unbalance Opponent", "Bayonet Master", "Cantina Brawler", "Counterpunch", "Crowd Control", "Devastating Melee Smash", "Disarm and Engage", "Entangler", "Experienced Brawler", "Grabber", "Hammerblow", "Make Do", "Man Down", "Pick a Fight", "Reverse Strength", "Strong Grab", "Sucker Punch", "Unrelenting Assault", "Droid Duelist", "Force Repair", "Heal Droid", "Mask Presence", "Silicon Mind", "Charm Beast (Felucian Shaman Talent Tree)", "Command Beast (Felucian Shaman Talent Tree)", "Detonate", "Hive Mind", "Infuse Weapon", "Sickening Blast", "Deadly Repercussions", "Manipulating Strike", "Improved Manipulating Strike", "Pulling the Strings", "Advanced Intel", "Hidden Eyes", "Hunt the Hunter", "Seek and Destroy", "Spotter", "Biotech Mastery", "Expedient Mending", "Expert Shaper", "Master Mender", "Skilled Implanter", "Dark Healing", "Dark Scourge", "Dark Side Adept", "Dark Side Master", "Force Deception", "Improved Dark Healing", "Wicked Strike", "Affliction", "Dark Healing Field", "Drain Force", "Sith Alchemy (Sith Talent Tree)", "Stolen Form", "Force Crush", "Vengeful Spirits", "Ambush Specialist", "Destructive Ambusher", "Keep It Going", "Keep Them Reeling (Ambusher Talent Tree)", "Perceptive Ambusher", "Spring the Trap", "Computer Language", "Computer Master", "Enhanced Manipulation", "Hotwired Processor", "Power Surge", "Skill Conversion", "Power Boost", "Advantageous Positioning", "Get Some Distance", "Murderous Arts I", "Murderous Arts II", "Ruthless (Assassin Talent Tree)", "Shift", "Sniping Assassin", "Sniping Marksman", "Sniping Master", "Cowards Flight", "Craven Appeal", "Feign Harmlessness", "Harmless Distraction", "Not in the Face", "Forward Patrol", "Mobile Combatant (Advance Patrol Talent Tree)", "Trailblazer", "Watchful Step", "Precision Shot", "Bullseye", "Draw a Bead", "Pinning Shot", "Harrying Shot", "Confounding Attack", "Double Up", "Find an Opening", "Opportunistic Defense", "Preternatural Senses", "Seize the Moment (Outlaw Talent Tree)", "Tangle Up", "Uncanny Instincts", "Force Perception", "Force Pilot", "Foresight", "Gauge Force Potential", "Visions", "Feel the Force", "Force Reflexes", "Heightened Awareness", "Instinctive Navigation", "Motion of the Future", "Psychometry", "Shift Sense", "Force Meld (Sense Talent Tree)", "Instinctive Astrogation", "Machine Empathy", "Machine Meld", "Reactive Precognition", "Presence", "Demand Surrender", "Improved Weaken Resolve", "Weaken Resolve", "Fluster", "Intimidating Defense", "Allure", "Captivate", "Conflict is my Strength", "Persuasive", "Findsman Ceremonies", "Findsman's Foresight", "Omens", "Target Visions", "Temporal Awareness", "Connections", "Educated", "Spontaneous Skill", "Wealth", "Engineer", "Influential Friends", "Powerful Friends", "Cross-Training", "Gifted Entertainer", "Favors", "Armored Guard", "Bodyguard's Sacrifice", "Guard's Endurance", "Lifesaver", "Out of Harm's Way (Protection Talent Tree)", "Roll With It", "Take the Hit", "Ward", "Devastating Attack", "Penetrating Attack", "Weapon Specialization", "Autofire Assault", "Crushing Assault", "Disarming Attack", "Impaling Assault", "Improved Suppression Fire", "Stinging Assault", "Biotech Adept", "Bugbite", "Curved Throw", "Surprising Weapons", "Veiled Biotech", "Armored Augmentation I", "Armored Augmentation II", "Armor Mastery (Knight's Armor Talent Tree)", "Cortosis Defense", "Cortosis Retaliation", "Controlled Burst", "Exotic Weapon Mastery", "Greater Devastating Attack", "Greater Penetrating Attack", "Greater Weapon Focus", "Greater Weapon Specialization", "Multiattack Proficiency (Heavy Weapons)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)", "Extended Threat", "Ferocious Assault", "Multiattack Proficiency (Simple Weapons)", "Two-For-One Throw", "Heavy Gunner", "Twin Attack", "Ambush (Republic Commando Talent Tree)", "Higher Yield", "Rapid Reload", "Shoulder to Shoulder", "Strength in Numbers", "Weapon Shift", "Malkite Techniques", "Modify Poison", "Numbing Poison", "Undetectable Poison", "Vicious Poison", "Deep-Space Gambit", "Guidance", "Hidden Attacker", "Hyperspace Savant", "Vehicle Sneak", "Silent Movement", "Hyperdrive Tinkerer", "Hyperlane Mastery", "Regional Expertise", "Dash and Blast", "Flanking Fire", "Guaranteed Shot", "Hailfire", "Twin Shot", "Breach Cover", "Breaching Explosive", "Droid Expert", "Prepared Explosive", "Problem Solver", "Quick Modifications", "Repairs on the Fly", "Sabotage Device", "Tech Savant", "Vehicular Boost", "Cast Suspicion", "Distress to Discord", "Friend or Foe", "Seize the Moment (Provocateur Talent Tree)", "Stolen Advantage", "True Betrayal", "Combustion", "Earth Buckle", "Fluidity", "Thunderclap", "Wind Vortex", "Clone Scientist", "Gene Splicing", "Mass Cloning", "Master Cloner", "Rapid Cloning", "Retrovirus", "Bando Gora Surge", "Force Fighter", "Resist Enervation", "Victorious Force Mastery", "Dastardly Strike", "Disruptive", "Skirmisher", "Sneak Attack", "Walk the Line", "Backstabber", "Befuddle", "Cunning Strategist", "Hesitate", "Improved Skirmisher", "Improved Sneak Attack", "Seducer", "Seize Object", "Sow Confusion", "Stymie", "Sudden Strike", "Weakening Strike", "Opportunist", "Quick Strike (Misfortune Talent Tree)", "Assault Tactics", "Deployment Tactics", "Field Tactics", "One for the Team", "Outmaneuver", "Shift Defense I", "Shift Defense II", "Shift Defense III", "Tactical Edge", "Commander's Prerogative", "Exploit Weakness", "Grand Leader", "Irregular Tactics", "Lead by Example (Military Tactics Talent Tree)", "Turn the Tide", "Uncanny Defense", "Blowback", "Close Contact", "Multiattack Proficiency (Rifles) (Carbineer Talent Tree)", "Old Faithful", "Opportunity Fire", "Rifle Master", "Shoot from the Hip", "Snap Shot", "Adept Spellcaster (Dathomiri Witch Talent Tree)", "Charm Beast (Dathomiri Witch Talent Tree)", "Command Beast (Dathomiri Witch Talent Tree)", "Flight", "Force Treatment (Jedi Healer Talent Tree)", "Healing Boost", "Improved Healing Boost", "Soothe", "Return to Life", "Vital Synchronism", "Chalactan Adept", "Lesser Mark of Illumination", "Greater Mark of Illumination", "Chalactan Enlightenment", "Fool's Luck", "Fortune's Favor", "Gambler", "Knack", "Lucky Shot", "Avert Disaster", "Better Lucky than Dead", "Dumb Luck", "Labyrinthine Mind", "Lucky Stop", "Ricochet Shot", "Uncanny Luck", "Unlikely Shot", "Gambler's Fortune", "Savant", "Armored Mandalorian", "Mandalorian Advance", "Mandalorian Ferocity", "Mandalorian Glory", "Enhanced Danger Sense", "Expanded Horizon", "Knowledge and Defense", "Planetary Attunement", "Precognitive Meditation", "Inspire Fear I", "Inspire Fear II", "Inspire Fear III", "Notorious (Infamy Talent Tree)", "Shared Notoriety", "Fear Me", "Frighten", "Master Manipulator (Infamy Talent Tree)", "Small Favor", "Terrify", "Unsavory Reputation", "Assured Skill", "Critical Skill Success", "Exceptional Skill", "Reliable Boon", "Skill Boon", "Skill Confidence", "Skillful Recovery", "Directed Action", "Directed Movement", "Full Control", "Remote Attack", "Begin Attack Run", "Regroup", "Squadron Maneuvers", "Squadron Tactics", "Force Commander", "Diverse Squadron", "Melded Squadron", "Combat Repairs", "Droid Smash", "Targeting Package", "Just a Scratch", "Target Acquisition", "Target Lock", "Weapons Power Surge", "Ambush (Disgrace Talent Tree)", "Castigate", "Dirty Tactics", "Misplaced Loyalty", "Two-Faced", "Unreadable", "Battle Mount", "Expert Rider", "Terrain Guidance", "Mechanized Rider", "Cloak of Shadows", "Phantasm", "Revelation", "Shadow Armor", "Shadow Vision", "Apprentice Boon", "Share Force Secret", "Share Force Technique", "Share Talent", "Transfer Power", "Debilitating Shot", "Deceptive Shot", "Improved Quick Draw", "Knockdown Shot", "Multiattack Proficiency (Pistols)", "Ranged Disarm", "Trigger Work", "Blind Shot", "Damaging Disarm", "Keep Them Honest", "Lingering Debilitation", "Mobile Attack (Pistols)", "Pistol Duelist", "Ranged Flank", "Retreating Fire", "Slowing Shot", "Swift Shot", "Always Ready", "Concealed Weapon Expert", "Creeping Approach", "Set for Stun", "Silent Takedown", "Adept Negotiator", "Force Persuasion", "Master Negotiator", "Skilled Advisor", "Adversary Lore", "Aggressive Negotiator", "Cleanse Mind", "Collective Visions", "Consular's Vitality", "Consular's Wisdom", "Entreat Aid", "Force of Will", "Guiding Strikes", "Improved Consular's Vitality", "Know Weakness", "Recall", "Renew Vision", "Visionary Attack", "Visionary Defense", "WatchCircle Initiate", "Healing Mastery", "Jedi Healer", "Elusive Dogfighter", "Full Throttle", "Juke", "Keep it Together (Expert Pilot Talent Tree)", "Relentless Pursuit", "Vehicular Evasion", "Blind Spot", "Clip", "Close Scrape", "Improved Attack Run", "Master Defender", "Renowned Pilot", "Roll Out", "Shunt Damage", "Vehicle Focus", "Wingman", "Diplomatic Poise", "Living Memory", "Master of Will", "One Word, Two Meanings", "When the Veils Move", "Willful Senator", "Call Weapon", "Lightsaber Specialist", "Masterwork Lightsaber", "Perfect Attunement", "Quick Modification", "Assault Gambit", "Direct Fire", "Face the Foe", "Lead From the Front", "Luck Favors the Bold", "Barter", "Fringe Savant", "Long Stride", "Jury-Rigger", "Flee", "Keep it Together (Fringer Talent Tree)", "Sidestep", "Surge", "Swift Strider", "Brutal Unarmed Strike", "Martial Resurgence", "Rebound Leap", "Simultaneous Strike", "Telekinetic Strike", "Telekinetic Throw", "Desperate Measures", "Focus Terror", "Incite Rage", "Power of Hatred", "Fall Back", "Form Up", "Full Advance", "Hold Steady", "Search and Destroy", "Adapt and Survive", "Defensive Protection", "Quick on Your Feet", "Ready and Willing", "Unbalancing Adaptation", "Automated Strike", "Droid Defense", "Droid Mettle", "Expanded Sensors", "Inspire Competence", "Maintain Focus", "Overclocked Troops", "Reinforce Commands", "Acute Senses", "Expert Tracker", "Improved Initiative", "Keen Shot", "Uncanny Dodge I", "Uncanny Dodge II", "Reset Initiative", "Weak Point", "Back on their Feet", "Capable Assistant", "Makeshift Treatment", "Medical Specialization", "Reliable Treatment", "Steady Hands", "Empower Siang Lance", "Shield Gauntlet Defense", "Shield Gauntlet Deflect", "Shield Gauntlet Redirect", "Siang Lance Mastery", "Dark Side Manipulation", "Krath Illusions", "Krath Intuition", "Krath Surge", "Armored Morgukai", "Cortosis Staff Block", "Morgukai Resolve", "Multiattack Proficiency (Cortosis Staff)", "Channel Aggression", "Channel Anger", "Crippling Strike", "Embrace the Dark Side", "Dark Side Talisman", "Greater Dark Side Talisman", "Cycle of Harmony", "Force Stabilize", "Repel Discord", "Stifle Conflict", "Tyia Adept", "Etiquette", "Helpful", "Protocol", "Nuanced", "Observant", "Supervising Droid", "Talkdroid", "Cunning Distraction", "Damaging Deception", "Distracting Shout", "Improved Soft Cover", "Innocuous", "Treacherous");
    public static final List<String> SPECIAL_LIST = List.of("Shedding of the Body", "Cyborg Hybrid", "Must possess an Implant", "Have a Destiny", "2+ Appendages", "2+ Tool Appendages", "Receive the Gamemaster's Approval");
    public static final List<String> TRADITION_LIST = List.of("The B'omarr Order", "The Chalactan Adepts", "The Chalactan Adepts", "The Je'daii Rangers", "The Droid Equality Foundation", "Death Watch", "The Knights of Ren", "The Sun Guards of Thyrsus", "The Lok Revenants", "ExplorCorps", "The Praetorite Vong", "The Resistance", "The Nature Priests", "The Ailon Nova Guard", "Tor-Ro-Bo Industries", "The Tommaba Brotherhood", "The Banvhar Combine", "The Ember of Vahl", "House Organa", "The O'reenian Imperium", "The Parallax Chain", "The Jensaarai", "The Prophets of the Dark Side", "The Wavelength Gale", "The Jedi", "The Eternal Empire", "The Blazing Chain", "The Merchants' Consortium", "The Zann Consortium", "The Agents of Ossus", "The Intergalactic Zoological Society", "EduCorps", "The Jal Shey", "The Anjiliac Clan", "The Flesh Raiders", "New Republic Intelligence", "The Iron Knights", "The Imperial Military", "The Matukai", "The Je'daii Rangers", "The Mistryl Shadow Guard", "The Lugubraa Hordes", "The Republic Rocket-Jumpers", "The Galactic Republic", "The Kilian Rangers", "Systino", "The Mnggal-Mnggal", "The Corellian Confederation", "The Cult of Veroleem", "The Hutt Kajidics", "The GenoHaradan", "The Krath", "The Miners' Union", "The Killik Hivemind", "The Core World Nobles", "The Felucian Shamans", "The Bounty Hunters' Guild", "Lightning Squadron", "Kota's Militia", "The Baran Do Sages", "The Firebird Society", "Alliance Intelligence", "General Units", "The Lakhasa Caravan", "The Fel Empire", "The Onderon Beast Riders", "AgriCorps", "The Red Fury Brotherhood", "The Inquisitorius", "Beasts", "The Mandalorians", "Churhee's Riflemen", "The Galactic Empire", "Epsis", "The Zeison Sha", "The Believers", "The Yuuzhan Vong Empire", "The Joruba Consortium", "Tangan Industries", "The Disciples of Twilight", "The Anarrian Empire", "The Corporate Sector Authority", "The Chiss Ascendancy", "The Senate Guard", "The Seyugi Dervishes", "The Crimson Stars", "The Fringe", "The Luka Sene", "Sabaoth Squadron", "The Bothan SpyNet", "The Ssi-Ruuvi Imperium", "The Ebruchi Fleet", "The Tapani Noble Houses", "The Sorcerers of Tund", "Skull Squadron", "The Mecrosa Order", "The Vagaari Empire", "The Ebon Strikers", "The Kolkpravis", "The Tenloss Syndicate", "The Tof Kingdom", "Black Sun", "Imperial Intelligence", "The Crimson Dawn", "The Car'das Smugglers", "The Katarn Commandos", "The Nimbus Commandos", "The Hapes Consortium", "The Order of Shasa", "The Sith", "The Shapers of Kro Var", "The Galactic Alliance Guard", "The Confederacy of Independent Systems", "The Nyriaanan Clans", "The Infinite Empire", "The Trianii Rangers", "MedCorps", "The Thalassian Slavers", "The Tyia", "Republic Intelligence", "The Witches of Dathomir", "The Nihil", "The Apex Society", "The Altirian Republic", "The Wing Guard", "The Mandalorian Protectors", "The Wardens of the Sky", "The First Order", "The New Republic", "The Uwanna Cartel", "TaggeCo", "The Galactic Alliance", "The Morgukai", "The Naboo Resistance", "The Horizon Guard", "Wraith Squadron", "The Hapan Royal Guard", "The Korunnai", "The Fallanassi", "Sando's Boys", "The Bando Gora", "The Iron Ring", "The Vipers", "The Antarian Rangers", "The Aing-Tii Monks", "The Old Republic", "The Techno Union", "The Keetael", "The Imperial Army", "The Imperial Navy", "The Veroleem Resistance", "The Sith Empire", "The Galactic Alliance Guard", "The Sable Dawn", "Eeook Mining and Reclamation", "The Peace Brigade", "The Blackguard", "The Rebel Alliance", "The Imperial Knights", "House Korden");
    public static final List<String> FORCE_POWER_LIST = List.of("Obscure", "Concentration", "Force Projection", "Force Blast", "Crucitorn", "Plant Surge", "Pass the Blade", "Fold Space", "Force Storm", "Deflecting Slash", "Slow", "Tempered Aggression", "Rising Whirlwind", "Phase", "Negate Energy", "Force Shield", "Resist Force", "Draw Closer", "Force Lightning", "Malacia", "Force Slam", "Valor", "Energy Resistance", "Rend", "Saber Swarm", "Ionize", "Falling Avalanche", "Ballistakinesis", "Force Grip", "Rearrangement", "Inertia", "Blind", "Lightning Burst", "Sith Word", "Shatterpoint", "Force Disarm", "Vornskr's Ferocity", "Conduction", "Dark Transfer", "Hatred", "Force Stun", "Move Object", "Detonate", "Technometry", "Dark Rage", "Inspire", "Shien Deflection", "High Ground Defense", "Pushing Slash", "Memory Walk", "Sever Force", "Makashi Riposte", "Mind Shard", "Force Scream", "Stagger", "Repulse", "Hawk-Bat Swoop", "Vital Transfer", "Rebuke", "Farseeing", "Circle of Shelter", "Assured Strike", "Contentious Opportunity", "Twin Strike", "Conjure Doubt", "Levitate", "Convection", "Force Light", "Force Whirlwind", "Glowball", "Enlighten", "Swift Flank", "Force Storm (FUCG)", "Unbalancing Block", "Wound", "Kinetic Combat", "Combustion", "Sarlacc Sweep", "Battle Strike", "Siphon Life", "Gaze of Darkness", "Prescience", "Fear", "Unhindered Charge", "Force Track", "Cryokinesis", "Corruption (Force Power)", "Barrier of Blades", "Cloak", "Morichro", "Sith Curse", "Force Thrust", "Disarming Slash", "Surge", "Inflict Pain", "Instill Turmoil", "Mind Trick", "Thought Bomb", "Drain Energy", "Fluid Riposte", "Intercept");
    public static final List<String> FORCE_SECRETS = List.of();
    public static final List<String> FORCE_TECHNIQUES = List.of();
    public static final List<String> CLASS_LIST = List.of("Jedi", "Noble", "Scoundrel", "Scout", "Soldier", "Technician", "Force Prodigy", "Nonheroic", "Beast", "Ace Pilot", "Bounty Hunter", "Crime Lord", "Elite Trooper", "Force Adept", "Force Disciple", "Gunslinger", "Jedi Knight", "Jedi Master", "Officer", "Sith Apprentice", "Sith Lord", "Corporate Agent", "Gladiator", "Melee Duelist", "Enforcer", "Independent Droid", "Infiltrator", "Master Privateer", "Medic", "Saboteur", "Assassin", "Charlatan", "Outlaw", "Droid Commander", "Military Engineer", "Vanguard", "Imperial Knight", "Shaper", "Improviser", "Pathfinder", "Martial Arts Master");

    protected final String plainText;
    protected final String type;

    public Prerequisite(String plainText, String type) {
        //printUnique("---"+type);
        this.plainText = plainText;
        this.type = type;
        //Util.printUnique("TYPE: "+ type);
    }


    public static Prerequisite getClassPrerequisite(Elements entries, String itemName) {
        boolean found = false;
        boolean allowUL = true;
        List<Prerequisite> prerequisites = new ArrayList<>();

        for (Element entry : entries) {
            String input = entry.text().toLowerCase();
            if (found) {
                if (allowUL && entry.tag().equals(Tag.valueOf("ul"))) {
                    allowUL = false;

                    for (Element li :
                            entry.select("li")) {
                        prerequisites.add(Prerequisite.create(li.text(), itemName));
                    }

                }
            } else if ((entry.tag().equals(Tag.valueOf("h4")) || entry.tag().equals(Tag.valueOf("p"))) && input.contains("prerequisites")) {
                found = true;
            }
        }
        return merge(prerequisites);
    }


    public static Prerequisite getPrerequisite(Elements elements) {

        List<Prerequisite> prerequisites = new ArrayList<>();
        for (Element element : elements) {
            prerequisites.add(getPrerequisite(null, element.text()));
        }
        return merge(prerequisites);
    }


    public static Prerequisite merge(Prerequisite... prerequisites) {
        return merge(Arrays.asList(prerequisites));
    }

    public static Prerequisite merge(List<Prerequisite> prerequisites) {
        prerequisites = prerequisites.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (prerequisites.size() == 1) {
            return prerequisites.get(0);
        } else if (prerequisites.size() > 1) {
            return and(prerequisites);
        }
        return null;
    }

    public static Prerequisite getPrerequisite(String itemName, String text) {
        List<Prerequisite> prerequisites = new ArrayList<>();
        if (text.toLowerCase().startsWith("prerequisite")) {
            String prerequisite = text.split(":")[1].trim();
            if ("Observant, Any Talent from the Influence Talent Tree, Inspiration Talent Tree, or Leadership Talent Tree".equals(prerequisite)) {
                prerequisite = prerequisite.replaceFirst(",", ";");
            }
            if ("At least 1 Talent from Lightsaber Combat Talent Tree, Duelist Talent Tree, or Lightsaber Forms Talent Tree".equals(prerequisite)
                    || "Any Talent from either the Expert Pilot Talent Tree or the Gunner Talent Tree".equals(prerequisite)
                    || "Any two Talents from the Slicer Talent Tree, Burst Transfer can be one of these Talents".equals(prerequisite)
                    || "Bothan, or two Talents from the Infiltration Talent Tree".equals(prerequisite)) {
                prerequisites.add(create(prerequisite, itemName));
            } else if (prerequisite.contains(";")) {
                prerequisites.addAll(Arrays.stream(prerequisite.trim().split(";")).map(prereq -> create(prereq, itemName)).collect(Collectors.toList()));
            } else {
                prerequisites.addAll(Arrays.stream(prerequisite.trim().split(",")).map(prereq -> create(prereq, itemName)).collect(Collectors.toList()));
            }
        }
        return merge(prerequisites);
    }


    public static Prerequisite create(String plainText) {
        return create(plainText, null);
    }

    private static Prerequisite create(String plainText, String itemName) {
        final List<Prerequisite> prerequisites = parsePrerequisites(plainText.trim(), itemName);

        return merge(prerequisites);
    }

    private static List<Prerequisite> parsePrerequisites(String text, String itemName) {
        if ("None".equals(text) || "-".equals(text)) {
            return List.of();
        }

        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        if (text.endsWith(" Feat")) {
            text = text.substring(0, text.length() - 5);
        }
        if (text.endsWith(" Talent")) {
            text = text.substring(0, text.length() - 7);
        }
        if (text.endsWith(" Force Power")) {
            text = text.substring(0, text.length() - 12);
        }
        if (text.endsWith(" Species Trait")) {
            text = text.substring(0, text.length() - 14);
        }
        if (text.endsWith(" Locomotion")) {
            text = text.substring(0, text.length() - 11);
        }
        if (text.endsWith(" Appendage")) {
            text = text.substring(0, text.length() - 10);
        }
        if (text.endsWith(" (Droid Accessory)")) {
            text = text.substring(0, text.length() - 18);
        }
        if (text.endsWith(" (Kajain'sa'Nikto)")) {
            text = text.substring(0, text.length() - 18);
        }
        if (text.startsWith("Droid Systems: ")) {
            text = text.substring(15);
        }
        if (text.startsWith("Talents: ")) {
            text = text.substring(9);
        }
        if (text.startsWith("Talent: ")) {
            text = text.substring(8);
        }
        if (text.startsWith("Applicable To: ")) {
            text = text.substring(15);
        }
        if (text.startsWith("Species: ")) {
            text = text.substring(9);
        }
        if (text.startsWith("*")) {
            text = text.substring(1);
        }
        if (text.startsWith("or ")) {
            text = text.substring(3);
        }


        //if this is wrapped in paired parens, remove them
        if (text.startsWith("(") && text.endsWith(")") && text.chars().filter(ch -> ch == ')').count() == 1 && text.chars().filter(ch -> ch == '(').count() == 1) {
            final String substring = text.substring(1, text.length() - 1);
            return parsePrerequisites(substring, itemName);
        }


        //PRE OR:phrases that contain OR but should not be parsed as an or
        if ("Double Attack with either Advanced Melee Weapons, an Exotic Weapon (Melee), or Lightsabers".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "FEAT", "Double Attack (Advanced Melee Weapon)"),
                    simple(text, "FEAT", "Double Attack (#payload#)"),
                    simple(text, "FEAT", "Double Attack (Lightsabers)")
            )));
        }
        if ("Master of Elegance, Single Weapon Flourish I, Weapon Finesse".equals(text)) {
            return List.of(and(text, List.of(
                    simple(text, "TALENT", "Master of Elegance"),
                    simple(text, "TALENT", "Single Weapon Flourish I"),
                    simple(text, "FEAT", "Weapon Finesse")
            )));
        }

        //Medium or larger size
        if ("Colossal or Larger".equals(text) ||
                "This Weapon System can only be mounted on a Vehicle of Colossal size or larger.".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Colossal"),
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }

        //Medium or larger size
        if ("Colossal (Frigate) or Larger".equals(text) ||
                "This Weapon System can only be mounted on a Vehicle of Colossal (Frigate) size or larger".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }

        //Medium or larger size
        if ("Colossal or Smaller".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Diminutive"),
                    simple(text, "TRAIT", "Fine"),
                    simple(text, "TRAIT", "Small"),
                    simple(text, "TRAIT", "Medium"),
                    simple(text, "TRAIT", "Large"),
                    simple(text, "TRAIT", "Huge"),
                    simple(text, "TRAIT", "Gargantuan"),
                    simple(text, "TRAIT", "Colossal")
            )));
        }

        //Medium or larger size
        if ("Gargantuan or Smaller".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Diminutive"),
                    simple(text, "TRAIT", "Fine"),
                    simple(text, "TRAIT", "Small"),
                    simple(text, "TRAIT", "Medium"),
                    simple(text, "TRAIT", "Large"),
                    simple(text, "TRAIT", "Huge"),
                    simple(text, "TRAIT", "Gargantuan")
            )));
        }

        //Medium or larger size
        if ("Gargantuan or Larger".equalsIgnoreCase(text) || "Gargantuan or Colossal".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Gargantuan"),
                    simple(text, "TRAIT", "Colossal"),
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }

        //Medium or larger size
        if ("Colossal".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Colossal"),
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }


        //Medium or larger size
        if ("Colossal (Cruiser) or Larger".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }

        //Medium or larger size
        if ("Medium or larger size".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Medium"),
                    simple(text, "TRAIT", "Large"),
                    simple(text, "TRAIT", "Huge"),
                    simple(text, "TRAIT", "Gargantuan"),
                    simple(text, "TRAIT", "Colossal"),
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }
        //Medium or larger size
        if ("Huge or Larger".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Huge"),
                    simple(text, "TRAIT", "Gargantuan"),
                    simple(text, "TRAIT", "Colossal"),
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }
        //Small size or larger
        if ("Small size or larger".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Small"),
                    simple(text, "TRAIT", "Medium"),
                    simple(text, "TRAIT", "Large"),
                    simple(text, "TRAIT", "Huge"),
                    simple(text, "TRAIT", "Gargantuan"),
                    simple(text, "TRAIT", "Colossal"),
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }
        //Small size or larger
        if ("Large or Larger".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Large"),
                    simple(text, "TRAIT", "Huge"),
                    simple(text, "TRAIT", "Gargantuan"),
                    simple(text, "TRAIT", "Colossal"),
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)"),
                    simple(text, "TRAIT", "Colossal (Station)")
            )));
        }
        //Small size or larger
        if ("Colossal (Cruiser) or Smaller".equals(text)) {
            return List.of(or(text, List.of(
                    simple(text, "TRAIT", "Diminutive"),
                    simple(text, "TRAIT", "Fine"),
                    simple(text, "TRAIT", "Small"),
                    simple(text, "TRAIT", "Medium"),
                    simple(text, "TRAIT", "Large"),
                    simple(text, "TRAIT", "Huge"),
                    simple(text, "TRAIT", "Gargantuan"),
                    simple(text, "TRAIT", "Colossal"),
                    simple(text, "TRAIT", "Colossal (Frigate)"),
                    simple(text, "TRAIT", "Colossal (Cruiser)")
            )));
        }
        // Trained in at least one Knowledge Skill
        if ("Trained in at least one Knowledge Skill".equals(text)) {
            return List.of(or(text,
                    simple(text, "FEAT", "Knowledge (Bureaucracy)"),
                    simple(text, "FEAT", "Knowledge (Galactic Lore)"),
                    simple(text, "FEAT", "Knowledge (Life Sciences)"),
                    simple(text, "FEAT", "Knowledge (Physical Sciences)"),
                    simple(text, "FEAT", "Knowledge (Social Sciences)"),
                    simple(text, "FEAT", "Knowledge (Tactics)"),
                    simple(text, "FEAT", "Knowledge (Technology)")
            ));
        }

        // Not a Droid
        if ("Not a Droid".equals(text) || "Cannot be a Droid".equals(text)) {
            return List.of(simple(text, "SPECIAL", "Not a Droid"));
        }

        if ("Special: Must be a Droid".equals(text) || "Droid".equalsIgnoreCase(text)) {
            return List.of(simple(text, "SPECIAL", "Is a droid"));
        }

        if (List.of("weapon", "any weapon").contains(text.toLowerCase())) {
            return List.of(simple("Any Weapon", "TYPE", "weapon"));
        }
        if (List.of("armor", "any armor").contains(text.toLowerCase())) {
            return List.of(simple("Any Weapon", "TYPE", "armor"));
        }
        if (Objects.equals("Any Weapon with an Ion or Stun Setting", text)) {
            return List.of(and(text, List.of(
                    simple(text, "TYPE", "weapon"),
                    or(List.of(simple(text, "DAMAGE_TYPE", "Ion"),
                            simple(text, "MODE", "Stun"))))));
        }
        if (Objects.equals("Any Weapon with a Stun Setting", text)) {
            return List.of(and(text, List.of(
                    simple(text, "TYPE", "weapon"),
                    simple(text, "MODE", "Stun"))));
        }

        if (Objects.equals("Any Advanced Melee Weapon or Simple Weapon (Melee)", text)) {
            return List.of(or(text,
                    simple(text, "SUBTYPE", "Advanced Melee Weapons"),
                    simple(text, "SUBTYPE", "Advanced Melee Weapons")));
        }
        if (Objects.equals("Any Armor, Any Melee Slashing or Piercing Weapon", text)) {
            return List.of(or(text,
                    simple("Any Armor", "TYPE", "armor"),
                    and(text,simple(text, "WEAPON_GROUP", "Melee Weapons"),
                            or(simple(text, "DAMAGE_TYPE", "Slashing"),
                                    simple(text, "DAMAGE_TYPE", "Piercing")))));
        }

        if (Objects.equals("Any Armor, Any Melee non-Energy Weapon, Any Ranged Weapon with a Stun Setting", text)) {
            return List.of(or(text,
                    simple(text, "TYPE", "armor"),
                    and(text,simple(text, "WEAPON_GROUP", "Melee Weapons"),
                            not(or(simple(text, "DAMAGE_TYPE", "Energy"), simple(text, "MODE", "Energy")))),
                    and(text,simple(text, "WEAPON_GROUP", "Ranged Weapons"),
                            simple(text, "MODE", "Stun"))
                    ));
        }

        if (Objects.equals("Any Armor, Simple Weapons (Melee)", text)) {
            return List.of(or(text,
                    simple(text, "TYPE", "armor"),
                    simple(text, "SUBTYPE", "Simple Melee Weapons")
                    ));
        }

        if (Objects.equals("Any pre-Legacy era Powered Weapon, not including Weapons found in the Star Wars Saga Edition Core Rulebook", text)) {
            return List.of(not(text,
                    or(text,
                            simple(text, "SOURCE", "Star Wars Saga Edition Legacy Era Campaign Guide"),
                            simple(text, "SOURCE", "Star Wars Saga Edition Core Rulebook")
                    )));
        }
        if (Objects.equals("Any Ranged Weapon dealing Piercing damage", text)) {
            return List.of(and(text,
                    simple(text, "WEAPON_GROUP", "Ranged Weapons"),
                    or(simple(text, "DAMAGE_TYPE", "Piercing")), simple(text, "MODE", "Piercing")));
        }
        if (Objects.equals("Any Weapon with the Antiqued Weapon Template", text)) {
            return List.of(simple(text, "TEMPLATE", "Antiquated Weapon Template"));
        }
        if (Objects.equals("Any Armor that provides an Equipment bonus to Fortitude Defense", text)) {
            return List.of(and(text, List.of(
                    simple(text, "TYPE", "armor"),
                    simple(text, "ATTRIBUTE", "equipmentFortitudeDefenseBonus:>0"))));
        }
        if (Objects.equals("Any Armor, Any Ranged Energy Weapon", text)) {
            return List.of(or(text, List.of(
                    simple(text,"TYPE", "armor"),
                    and(List.of(
                            simple(text, "TYPE", "weapon"),
                            simple(text, "WEAPON_GROUP", "Ranged Weapons")
                    )))));
        }
        if (Objects.equals("Any Armor, Any Weapon", text)) {
            return List.of(or(text, List.of(
                    simple(text,"TYPE", "armor"),
                    simple(text, "TYPE", "weapon"))));
        }
        if (Objects.equals("Any Armor, Any Slashing or Piercing Weapon (Excluding Lightsabers)", text)) {

            return List.of(or(List.of(
                    simple("armor","TYPE", "armor"),
                    and(List.of(
                            simple("weapon", "TYPE", "weapon"),
                            or(List.of(
                                    simple("Slashing mode", "MODE", "Slashing"),
                                    simple("Piercing mode", "MODE", "Piercing"),
                                    simple("Slashing damage type","DAMAGE_TYPE", "Slashing"),
                                    simple("Piercing damage type", "DAMAGE_TYPE", "Piercing"))),
                            not(simple("Lightsaber", "SUBTYPE", "Lightsabers"))
                    )))));
        }
        if ("vehicle".equalsIgnoreCase(text)) {
            return List.of(or("Is a Vehicle", List.of(simple(text, "TYPE", "vehicle"),
                    simple(text, "TYPE", "npc-vehicle"))));
        }

        Pattern oneTalent = Pattern.compile("(?:Any|At least 1|At least one|Any one) Talent from (?:either )?(?:the )?([\\s\\w,]*)");
        Matcher oneTalentMatcher = oneTalent.matcher(text);
        if (oneTalentMatcher.find()) {
            String payload = oneTalentMatcher.group(1);
            if (payload.contains(" and ")) {
                String[] tokens = payload.split(", and | and |, ");
                return List.of(and(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(talentTree -> simple("Talent from the " + talentTree, "TALENT", talentTree)).collect(Collectors.toList())));
            } else if (payload.contains(" or ") || text.contains(", ")) {
                String[] tokens = payload.split(", or | or |, ");
                return List.of(or(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(talentTree -> simple("Talent from the " + talentTree, "TALENT", talentTree)).collect(Collectors.toList())));
            }
            return List.of(simple("Talent from the " + payload, "TALENT", payload));

        }

        if ("Any two Talents from the Slicer Talent Tree, Burst Transfer can be one of these Talents".equals(text)) {
            return List.of(or(text, 2, List.of(simple(text, "TALENT", "Slicer Talent Tree"), simple(text, "TALENT", "Burst Transfer"))));
        }

        Pattern twoTalents = Pattern.compile("(?:Any )?two Talents from (?:either )?the ([\\s\\w,]*)");
        Matcher twoTalentsMatcher = twoTalents.matcher(text);
        if (twoTalentsMatcher.find()) {
            String payload = twoTalentsMatcher.group(1);

            String[] tokens = new String[1];
            tokens[0] = payload;
            if (payload.contains(" or ") || text.contains(", ")) {
                tokens = payload.split(", or | or |, ");
            }
            return List.of(or(text, 2, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(talentTree -> simple("Talent from the " + talentTree, "TALENT", talentTree)).collect(Collectors.toList())));

        }

        if ("At least three Force Talents".equals(text)) {
            return List.of(or(text, 3, List.of(simple(text, "TALENT", "Force Talent Trees"))));
        }

        Pattern featsPattern = Pattern.compile("Feats: ([\\w\\s,()-:']*)");
        Matcher featsMatcher = featsPattern.matcher(text);
        if (featsMatcher.find()) {
            String payload = featsMatcher.group(1);
            if ("Feats: Martial Arts I, Martial Arts II, Melee Defense, and at least one of the following Feats: Echani Training, Hijkata Training, K'tara Training, K'thri Training, Stava Training, Tae-Jitsu Training, Teräs Käsi Training, or Wrruushi Training".equals(text)) {

                return List.of(and(text, List.of(simple("Martial Arts I", "FEAT", "Martial Arts I"),
                        simple("Martial Arts II", "FEAT", "Martial Arts II"),
                        simple("Melee Defense", "FEAT", "Melee Defense"),
                        or("At least one of the following Feats: Echani Training, Hijkata Training, K'tara Training, K'thri Training, Stava Training, Tae-Jitsu Training, Teräs Käsi Training, or Wrruushi Training", List.of(
                                simple("Echani Training", "FEAT", "Echani Training"),
                                simple("Hijkata Training", "FEAT", "Hijkata Training"),
                                simple("K'tara Training", "FEAT", "K'tara Training"),
                                simple("K'thri Training", "FEAT", "K'thri Training"),
                                simple("Stava Training", "FEAT", "Stava Training"),
                                simple("Tae-Jitsu Training", "FEAT", "Tae-Jitsu Training"),
                                simple("Teräs Käsi Training", "FEAT", "Teräs Käsi Training"),
                                simple("Wrruushi Training", "FEAT", "Wrruushi Training")
                        )))));
            }

            if (payload.contains(" and ") || payload.contains(", ")) {
                String[] tokens = payload.split(", and | and |, ");
                return List.of(and(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList())));
            } else if (payload.contains(" or ")) {
                String[] tokens = payload.split(", or | or |, ");
                return List.of(or(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList())));
            }
            String[] tokens = {payload};
            return Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList());

        }

        Pattern forcePowersPattern = Pattern.compile("Force Powers: ([\\w\\s,()-]*)");
        Matcher forcePowerMatcher = forcePowersPattern.matcher(text);
        if (forcePowerMatcher.find()) {
            String payload = forcePowerMatcher.group(1);
            if (payload.contains(" and ") || payload.contains(", ")) {
                String[] tokens = payload.split(", and | and |, ");
                return List.of(and(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList())));
            } else if (payload.contains(" or ")) {
                String[] tokens = payload.split(", or | or |, ");
                return List.of(or(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList())));
            }
            String[] tokens = {payload};
            return Arrays.stream(tokens).map(Prerequisite::cleanItem).map(prereq -> create(prereq, itemName)).collect(Collectors.toList());

        }

        //
        if (text.startsWith("Equipped with ")) {
            String payload = text.substring(0, 14);
            if (payload.contains(" and ")) {
                String[] tokens = payload.split(" and |, ");
                return List.of(and(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(item -> simple("Equipped with " + item, "EQUIPPED", item)).collect(Collectors.toList())));
            } else if (payload.contains(" or ") || text.contains(", ")) {
                String[] tokens = payload.split(" or |, ");
                return List.of(or(text, Arrays.stream(tokens).map(Prerequisite::cleanItem).map(skill -> simple("Equipped with " + skill, "EQUIPPED", skill)).collect(Collectors.toList())));
            }
            return List.of(simple("Equipped with " + payload, "EQUIPPED", payload));
        }


        // Trained in
        Pattern trainedSkills = Pattern.compile("(?:Trained in|Trained Skills:) ([\\w\\s#,()]*)");
        Matcher trainedSkillsMatcher = trainedSkills.matcher(text);
        if (trainedSkillsMatcher.find()) {
            String payload = trainedSkillsMatcher.group(1);
            if (payload.contains(" and ") || text.contains(", ")) {
                String[] tokens = payload.split(" and |, ");
                return List.of(and(text, Arrays.stream(tokens).map(skill -> simple("Trained in " + skill, "TRAINED SKILL", skill)).collect(Collectors.toList())));
            } else if (payload.contains(" or ")) {
                String[] tokens = payload.split(" or |, ");
                return List.of(or(text, Arrays.stream(tokens).map(skill -> simple("Trained in " + skill, "TRAINED SKILL", skill)).collect(Collectors.toList())));
            }
            return List.of(simple("Trained in " + payload, "TRAINED SKILL", payload));
        }
        // Member of
        if (text.startsWith("Must be a member of ")) {
            String payload = text.substring(0, 20);
            return List.of(simple("Must be a member of " + payload, "TRADITION", payload));
        }
        //Military Member
        if ("Special: Must belong to an organization with a military or paramilitary division, examples include the Trade Federation, the Galactic Empire, the Rebel Alliance, and the New Republic".equals(text)) {

            return List.of(simple(text, "SPECIAL", "is part of a military"));
        }
        //major interstellar corporation
        if ("Special: Must be employed by a major interstellar corporation".equals(text)) {

            return List.of(simple(text, "SPECIAL", "is part of a major interstellar corporation"));
        }


        if (FEAT_LIST.contains(text)) {
            return List.of(simple(text + " feat", "FEAT", text));
        }
        if (SPECIES_LIST.contains(text)) {
            return List.of(simple(text + " species", "SPECIES", text));
        }
        if (TALENT_LIST.contains(text) || DUPLICATE_TALENT_NAMES.contains(text)) {
            if (DUPLICATE_TALENT_NAMES.contains(text)) {
                text = text + " (" + itemName + ")";
            }
            return List.of(simple(text + " talent", "TALENT", text));
        }
        if (TRAIT_LIST.contains(text)) {
            return List.of(simple(text + " trait", "TRAIT", text));
        }
        if (ITEM_LIST.contains(text)) {
            return List.of(simple(text, "EQUIPPED", text));
        }
        if (SPECIAL_LIST.contains(text)) {
            return List.of(simple(text, "SPECIAL", text));
        }
        if (TRADITION_LIST.contains(text)) {
            return List.of(simple("a member of " + text, "TRADITION", text));
        }
        if (FORCE_POWER_LIST.contains(text)) {
            return List.of(simple(text + " force power", "FORCE POWER", text));
        }
        if (FORCE_SECRETS.contains(text)) {
            return List.of(simple(text + " force secret", "FORCE SECRET", text));
        }
        if (FORCE_TECHNIQUES.contains(text)) {
            return List.of(simple(text + " force technique", "FORCE TECHNIQUE", text));
        }

        if ("Weapon Focus with chosen Exotic Weapon or Weapon Group".equals(text))//TODO this needs an option
        {
            return List.of(simple(text, "FEAT", "Weapon Focus (#payload#)"));
        }


        //OR: creates an ORPrequisite
        if (text.contains(" or ")) {
            //printUnique(text);
            List<Prerequisite> prerequisites = new ArrayList<>();
            String[] tokens = text.split(", or | or |, ");

            for (String token : tokens) {
                prerequisites.addAll(parsePrerequisites(token, itemName));
            }
            return List.of(or(text, prerequisites));
        }

        if (text.contains(" and ")) {
            List<Prerequisite> prerequisites = new ArrayList<>();
            String[] tokens = text.split(" and |, ");

            for (String token : tokens) {
                prerequisites.addAll(parsePrerequisites(token, itemName));
            }
            return List.of(and(text, prerequisites));
        }

        if(text.equals("Weapon Focus (Melee Weapon)")){
            return List.of(or(simple("Weapon Focus (Advanced Melee Weapons)", "FEAT", "Weapon Focus (Advanced Melee Weapons)"),
                    simple("Weapon Focus (Lightsaber)", "FEAT", "Weapon Focus (Lightsabers)"),
                    simple("Weapon Focus (Simple Melee Weapons)", "FEAT", "Weapon Focus (Simple Melee Weapons)"),
                    simple("Weapon Focus (Melee Natural Weapons)", "FEAT", "Weapon Focus (Melee Natural Weapons)"),
                    simple("Weapon Focus (Exotic Melee Weapons)", "FEAT", "Weapon Focus (Exotic Melee Weapons)")));
        }

        if (text.startsWith("Skill Focus") || text.startsWith("Weapon Proficiency")
                || text.startsWith("Weapon Focus") || text.startsWith("Armor Proficiency")
                || text.startsWith("Double Attack") || text.startsWith("Exotic Weapon Proficiency")
                || text.startsWith("Brutal Attack")) {
            final String requirement = text.replace("Chosen Weapon", "#payload#").replace("Chosen Skill", "#payload#");
            if (requirement.contains("#payload#")) {
                //printUnique(Context.getValue("name"), requirement);
            }
            return List.of(simple(requirement, "FEAT", requirement));
        }

        if (text.startsWith("Devastating Attack")
                || text.startsWith("Penetrating Attack")) {
            final String requirement = text.replace("Chosen Weapon", "#payload#").replace("Chosen Skill", "#payload#");
            if (requirement.contains("#payload#")) {
                //printUnique(Context.getValue("name"), requirement);
            }
            return List.of(simple(requirement, "TALENT", requirement));
        }

        if (text.startsWith("Greater Weapon Focus") || text.startsWith("Weapon Specialization")) {
            final String requirement = text.replace("Chosen Weapon", "#payload#").replace("Chosen Skill", "#payload#");
            if (requirement.contains("#payload#")) {
                //printUnique(Context.getValue("name"), requirement);
            }
            return List.of(simple(requirement, "TALENT", requirement));
        }

        //TODO cleanup
        if (text.startsWith("Dark Side Score ") || text.startsWith("Dark Side Score:")) {
            String payload = text.substring(16);
            if (payload.equals("equal to Wisdom Score") || payload.startsWith(" Your Dark Side Score must be equal to your Wisdom score")) {
                return List.of(simple(text, "DARK SIDE SCORE", "@WISTOTAL"));
            }
            Integer m = Util.getNumber(text);
            if (m != null) {
                return List.of(simple(text, "DARK SIDE SCORE", m.toString()));
            }
        }

        if (text.startsWith("Intelligence") || text.startsWith("Dexterity") || text.startsWith("Strength") || text.startsWith("Constitution") || text.startsWith("Wisdom") || text.startsWith("Charisma")) {
            return List.of(simple(text, "ATTRIBUTE", text));
        }

        if (text.startsWith("Base Attack Bonus")) {
            Integer m = Util.getNumber(text);
            if (m != null) {
                return List.of(simple("Base Attack Bonus " + m, "BASE ATTACK BONUS", m.toString()));
            }
            throw new IllegalStateException("failed to parse: " + text);
        }

        if (text.endsWith("Character Level") || text.startsWith("Character Level") || text.startsWith("Minimum Level: ")) {
            Integer m = Util.getNumber(text);
            if (m != null) {
                return List.of(simple("Character Level " + m, "CHARACTER LEVEL", m.toString()));
            }
            throw new IllegalStateException("failed to parse: " + text);
        }


        //SUPER SPECIFIC THINGS
        Pattern proficiencyPattern = Pattern.compile("Proficient (?:with|in) ([\\s\\w]*)");
        Matcher proficiencyMatcher = proficiencyPattern.matcher(text);
        if (proficiencyMatcher.find()) {
            String payload = proficiencyMatcher.group(1);
            if (payload.equals("Chosen Weapon")) {
                return List.of(simple("Proficient with #payload#", "PROFICIENCY", "#payload#"));
            }
            return List.of(simple(text, "PROFICIENCY", cleanItem(payload)));
        }

        Pattern forceTechniquePattern = Pattern.compile("(?:Any|At least) (1|one|One) Force Technique");
        Matcher forceTechniqueMatcher = forceTechniquePattern.matcher(text);
        if (forceTechniqueMatcher.find() || "Force Techniques: At least one".equals(text)) {

            return List.of(simple("At least one Force Technique", "FORCE TECHNIQUE", "1"));
        }

        Pattern forceSecretPattern = Pattern.compile("(?:Any|At least) (1|one) Force Secret");
        Matcher forceSecretMatcher = forceSecretPattern.matcher(text);
        if (forceSecretMatcher.find()) {

            return List.of(simple(text, "FORCE SECRET", "1"));
        }

        if ("Inherent Fly Speed".equals(text)) {
            String finalText = text;
            return List.of(or(text, IntStream.range(1, 20).mapToObj(i -> simple(finalText, "TRAIT", "Fly Speed " + i)).collect(Collectors.toList())));
        }
        if ("at least 1 level in the Shaper class".equals(text)) {
            return List.of(simple(text, "CLASS", "Shaper"));
        }
//        if("Member of The Sith".equals(text)){
//            return List.of(simple(text, "TRADITION", "The Sith"));
//        }
        if (text.toLowerCase().contains("member of the jedi")) {
            return List.of(simple("Must be a member of The Jedi Force Tradition", "TRADITION", "The Jedi"));
        }
        if (text.toLowerCase().contains("member of the sith")) {
            return List.of(simple("Must be a member of The Sith Force Tradition", "TRADITION", "The Sith"));
        }
        if ("Understand Binary".equals(text) || "Must have Binary as a learned language".equals(text)) {
            return List.of(simple(text, "LANGUAGE", "Binary"));
        }
        //SPELLING ERROR
        if ("Impose Hesitance".equals(text)) {
            return List.of(simple(text, "TALENT", "Impose Hesitation"));
        }
        //SPELLING ERROR
        if ("Vibroshield Mastery".equals(text)) {
            return List.of(simple(text, "TALENT", "Vibroshield Master"));
        }
        //Bounty hunter class feature
        if ("Familiar Foe".equals(text)) {
            return List.of(simple(text, "TRAIT", "Familiar Foe"));
        }
        //Officer hunter class feature
        if ("Share Talent (Any)".equals(text)) {
            return List.of(simple(text, "TRAIT", "Share Talent"));
        }

        if ("Special: Must be a sworn defender of Emperor Roan Fel".equals(text)) {
            return List.of(simple(text.substring(9), "SPECIAL", "sworn defender of Emperor Roan Fel"));
        }
        if ("Special: Must have built their own Lightsaber".equals(text)) {
            return List.of(simple(text.substring(9), "SPECIAL", "Has Built Lightsaber"));
        }

        if (text.startsWith("AGE:")) {
            final String payload = text.substring(4);
            String low;
            String high;
            if (payload.contains("-")) {
                String[] toks = payload.split("-");
                low = toks[0];
                high = toks[1];
            } else {
                low = payload.replaceAll("\\+", "");
                high = "";
            }

            return List.of(new RangePrerequisite(payload, "AGE", low, high));
        }


        if(text.contains(":")){
            String[] toks = text.split(":");
            return List.of(simple(toks[1], toks[0], toks[1]));
        }

        //printUnique(Context.getValue("name") + " --- " + text);


        return List.of();
    }

    private static String cleanItem(String s) {
        if (s.startsWith("a ")) {
            s = s.substring(2);
        }
        if (s.startsWith("the ")) {
            s = s.substring(4);
        }
        return s;
    }


    protected static String stringify(List<Prerequisite> children, String delimiter) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) {
                string.append(", ");
            }
            if (i == children.size() - 1) {
                string.append(delimiter);
            }
            final Prerequisite child = children.get(i);
            if (child == null) {
                continue;
            }

            if ("AND".equals(child.getType()) || "OR".equals(child.getType())) {
                string.append("(").append(child.getPlainText()).append(")");
            } else {
                string.append(child.getPlainText());
            }
        }

        return string.toString();
    }

    @Nonnull
    public abstract JSONObject toJSON();

    public String getPlainText() {
        return plainText;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("plainText", plainText)
                .add("type", type)
                .toString();
    }

    public String getType() {
        return type;
    }
}
