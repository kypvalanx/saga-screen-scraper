package swse.units;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import swse.common.*;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static swse.talents.TalentExporter.DUPLICATE_TALENT_NAMES;
import static swse.util.Util.printUnique;

public class UnitExporter extends BaseExporter {
    public static final String JSON_OUTPUT = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems\\swse\\raw_export\\Beasts.json";

    public static final List<String> sizes = Lists.newArrayList("Fine ", "Diminutive ", "Tiny ", "Small ", "Medium ", "Large ", "Huge ", "Gargantuan ", "Colossal ", "Colossal (Frigate) ", "Colossal (Cruiser) ", "Colossal (Station) ");
    public static final List<String> colossal = Lists.newArrayList("(Frigate)", "(Cruiser)", "(Station)");
    public static final List<String> CLASSES = List.of("Jedi", "Noble", "Scoundrel", "Scout", "Soldier", "Technician", "Force Prodigy", "Nonheroic", "Beast", "Ace Pilot", "Bounty Hunter", "Crime Lord", "Elite Trooper", "Force Adept", "Force Disciple", "Gunslinger", "Jedi Knight", "Jedi Master", "Officer", "Sith Apprentice", "Sith Lord", "Corporate Agent", "Gladiator", "Melee Duelist", "Enforcer", "Independent Droid", "Infiltrator", "Master Privateer", "Medic", "Saboteur", "Assassin", "Charlatan", "Outlaw", "Droid Commander", "Military Engineer", "Vanguard", "Imperial Knight", "Shaper", "Improviser", "Pathfinder", "Martial Arts Master");

    public static final List<String> SPECIES = List.of("Aar'aa", "Ab'Ugartte", "Abednedo", "Abinyshi", "Abyssin", "Adarian", "Advozse", "Aing-Tii", "Aki-Aki", "Aleena", "Altiri", "Amani", "Amaran", "Anarrian", "Anomid", "Anx", "Anzati", "Anzellan", "Aqualish", "Arcona", "Ardennian", "Arkanian", "Arkanian Offshoot", "Assembler", "Ayrou", "Balosar", "Barabel", "Baragwin", "Bartokk", "Besalisk", "Bimm", "Bimm (Near-Human)", "Bith", "Bivall", "Blarina", "Blood Carver", "Boltrunian", "Bothan", "Caamasi", "Caarite", "Calibop", "Ergesh", "Cathar", "Catuman", "Celegian", "Cerean", "Chadra-Fan", "Chagrian", "Charon", "Chazrach", "Chev", "Chevin", "Chironian", "Chiss", "Chistori", "Ciasi", "Clawdite", "Codru-Ji", "Colicoid", "Cosian", "Coway", "Cragmoloid", "Croke", "Cyclorrian", "Dantari", "Dashade", "Defel", "Devaronian", "Devlikk", "Diathim", "Didynon", "Dowutin", "Drackmarian", "Draethos", "Drall", "Dressellian", "Dug", "Duinuogwuin", "Dulok", "Duros", "Dybrinthe", "Ebranite", "Ebruchi", "Eirrauc", "Elom", "Elomin", "Em'liy", "Equani", "Esh-kha", "Evocii", "Ewok", "Falleen", "Farghul", "Feeorin", "Felucian", "Filordus", "Filvian", "Firrerreo", "Flesh Raider", "Fosh", "Frenk", "Frozian", "Gamorrean", "Gand", "Gastrulan", "Gen'Dai", "Geonosian", "Givin", "Gormak", "Gossam", "Gotal", "Gozzo", "Gran", "Gree", "Gungan", "Gurlanin", "H'drachi", "H'nemthe", "Harch", "Hasikian", "Herglic", "Hexaclops", "Ho'Din", "Holwuff", "Houk", "Hrakian", "Human", "Huralok", "Hutt", "Hysalrian", "Iktotchi", "Imroosian", "Iotran", "Ishi Tib", "Ithorian", "Jablogian", "Jawa", "Jenet", "Kaleesh", "Kamarian", "Kaminoan", "Karkarodon", "Kel Dor", "Kerestian", "Kerkoiden", "Kessurian", "Khil", "Khommite", "Kian'thar", "Killik", "Kilmaulsi", "Kissai", "Kitonak", "Klatooinian", "Kobok", "Koorivar", "Kowakian", "Krevaaki", "Krish", "Kubaz", "Kudon", "Kushiban", "Kyuzo", "Lafrarian", "Lannik", "Lasat", "Lepi", "Lugubraa", "Lurmen", "Lurrian", "Maelibus", "Mandallian", "Mantellian Savrip", "Massassi", "Meerian", "Melitto", "Melodie", "Menahuun", "Miraluka", "Mirialan", "Mon Calamari", "Morseerian", "Mrlssi", "Murachaun", "Mustafarian, Northern", "Southern Mustafarian", "Muun", "Myneyrsh", "Nagai", "Nautolan", "Nazren", "Nazzar", "Nediji", "Neimoidian", "Nelvaanian", "Neti", "Nikto", "Nimbanel", "Noehon", "Noghri", "Nosaurian", "Nuknog", "Nyriaanan", "O'reenian", "Octeroid", "Omwati", "Ongree", "Ortolan", "Pa'lowick", "Pacithhip", "Paigun", "Pantoran", "Patitite", "Patrolian", "Pau'an", "Phindian", "Pho Ph'eahian", "Polis Massan", "Psadan", "Pyke", "Qiraash", "Quarren", "Quermian", "Quor'sav", "Qwohog", "Rakata", "Rakririan", "Ranat", "Ranth", "Rattataki", "Reesarian", "Replica Droid", "Republic Clone", "Revwien", "Rishii", "Rodian", "Rodisar", "Roonan", "Ruurian", "Rybet", "Ryn", "S'kytri", "Saheelindeeli", "Sakiyan", "Sanyassan", "Sarkan", "Sauvax", "Selkath", "Selonian", "Sephi", "Shani", "Shard", "Shi'ido", "Shistavanen", "Siniteen", "Sith Offshoot", "Skakoan", "Skrilling", "Sljee", "Sludir", "Sluissi", "Snivvian", "Sorcerer of Rhand", "Spiner", "Squib", "Ssi-Ruuk", "Ssori", "Stenax", "Stennes Shifter", "Stereb", "Sullustan", "Sunesi", "Swokes Swokes", "Sy Myrthian", "T'landa Til", "T'surr", "Talortai", "Talz", "Tarasin", "Tarro", "Tash", "Taung", "Tchuukthai", "Teedo", "Teek", "Tel'a", "Temolak", "Terrelian", "Thakwaash", "Theelin", "Thisspiasian", "Tintinna", "Tiss'shar", "Tof", "Togorian", "Togruta", "Toong", "Toydarian", "Trandoshan", "Trianii", "Tridactyl", "Trodatome", "Trogodile", "Tunroth", "Turazza", "Tusken Raider", "Twi'lek", "Tynnan", "Ubese", "Ugnaught", "Ugor", "Umbaran", "Utai", "Vagaari", "Vahla", "Veknoid", "Verpine", "Vippit", "Viraanntesse", "Vodran", "Vor", "Vorzydiak", "Vratix", "Vulptereen", "Vultan", "Vurk", "Vuvrian", "Weequay", "Whiphid", "Wookiee", "Woostoid", "Wroonian", "X'Ting", "Xamster", "Xexto", "Xi Charrian", "Yarkora", "Yevetha", "Yinchorri", "Yuuzhan Vong", "Yuzzem", "Yuzzum", "Zabrak", "Zehethbra", "Zeltron", "Zilkin", "Zuguruk", "Zygerrian", "Astromech Droid", "Battle Droid", "Labor Droid", "Mechanic Droid", "Medical Droid", "Probe Droid", "Protocol Droid", "Service Droid");


    public static final Pattern VARIANT_QUALIFIER = Pattern.compile("(\\(.+\\))$");

    public static final Pattern STRENGTH_PATTERN = Pattern.compile("Strength: ([\\d-]*)");
    public static final Pattern DEXTERITY_PATTERN = Pattern.compile("Dexterity: ([\\d-]*)");
    public static final Pattern INTELLIGENCE_PATTERN = Pattern.compile("Intelligence: ([\\d-]*)");
    public static final Pattern CONSTITUTION_PATTERN = Pattern.compile("Constitution: ([\\d-]*)");
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
    private static final List<String> DROID_TYPES = List.of("1st\\-Degree Droid", "2nd\\-Degree Droid", "3rd\\-Degree Droid", "4th\\-Degree Droid", "5th\\-Degree Droid");
    private static final List<String> FOLLOWERS = List.of("Utility Follower", "Defensive Follower", "Aggressive Follower", "Akk Dog Follower Template");

    //should these be templates?
    private static final List<String> TRAITS = List.of("Minion", "Squad", "Pack", "Swarm");
    private static final List<String> TEMPLATES = List.of("Chrysalis Beast", "Shaped Beast Template", "Sith Abomination", "Light Side Spirit", "Dark Side Spirit", "Weakened Beast");
    private static final List<String> DISEASES = List.of("Vongspawn");
    private static final List<String> AGES = List.of("(Old)", "(Middle Age)", "(Middle-Age)", "(Venerable)", "(Child)", "(Young Adult)");

    private static final List<String> SPECIES_TYPE = List.of("Airborne", "Aquatic", "Arctic", "Desert", "Subterranean", "Space-Dwelling", "Amphibian", "Jungle");

    private static final List<String> ITEMS = List.of("Vibro-Axe", "Vibrosword", "Shockstaff", "Power Hammer", "Vibroblade", "Vibrorapier", "Power Lance", "Double Vibroblade", "Stun Bayonet", "Electropole", "Vibrobayonet", "Vibrolance", "Shock Stick", "Dire Vibroblade", "San-Ni Staff", "Restricted Items", "Vibrodagger", "Restricted Items", "Force Pike", "Shock Whip", "Restricted Items", "Static Pike", "Electrostaff", "Vibroknucklers", "Energy Lance", "Arg'garok", "Fira", "Neuronic Whip", "Darkstick", "Vibro-Saw", "Tehk'la Blade", "Amphistaff", "Felucian Skullblade", "Garrote", "Ryyk Blade", "Shyarn", "Blastsword", "Atlatl", "Zhaboka", "Cesta", "Flamethrower", "Fire", "Aurial Blaster", "Concealed Dart Launcher", "Neural Inhibitor", "Restricted Items", "Magna Caster", "Siang Lance", "Sonic", "Squib Tensor Rifle", "Blast Cannon", "Deck Sweeper", "Pulse Rifle", "Verpine Shattergun", "Sith Lanvarok", "CR-1 Blast Cannon", "Massassi Lanvarok", "Bowcaster", "Discblade", "Restricted Items", "Wrist Rocket Launcher", "CryoBan Grenade", "Remote Grenade", "Stun Damage", "Ion Damage", "Thermal Detonator", "Gas Grenade", "Flash Canister", "Stun Grenade", "Smoke Grenade", "Ion Grenade", "Radiation Grenade", "EMP Grenade", "Frag Grenade", "Adhesive Grenade", "Concussion Grenade", "Jury-Rigging a Power Pack Bomb", "Flame Cannon", "Mortar Launcher", "Miniature Proton Torpedo Launcher", "Light Concussion Missile Launcher", "E-Web Missile Launcher", "Heavy Blaster Cannon", "Carbonite Rifle", "Electronet", "PLX-2M Portable Missile Launcher", "Heavy Repeating Blaster", "Grenade Launcher", "Missile Launcher", "HH-15 Projectile Launcher", "Tactical Tractor Beam", "E-Web Repeating Blaster", "Rotary Blaster Cannon", "Blaster Cannon", "Guard Shoto", "Modern Lightfoil", "Archaic Lightsaber", "Double-Bladed Lightsaber", "Dual-Phase Lightsaber", "Retrosaber", "Dueling Lightsaber", "Long-Handle Lightsaber", "Great Lightsaber", "Lightsaber Pike", "Lightsaber", "Archaic Lightfoil", "Lightfoil", "Crossguard Lightsaber", "Lightwhip", "Short Lightsaber", "Bounding Mine", "Flechette Mine", "Antivehicle Proton Mine", "Land Mine", "Proximity Mine", "Smart Mine", "Limpet Mine", "Laser Trip Mine", "Razor Wire", "HX2 Antipersonnel Mine", "Ion Mine", "Repulsorlift Inhibitor", "Antivehicle Mine", "Fragmentation Wire", "Antipersonnel Mine", "Static Mine", "Vehicular Mine", "Sonic Stunner", "Ripper", "Heavy Slugthrower Pistol", "Heavy Blaster Pistol", "Snare Pistol", "Hold-Out Blaster Pistol", "S-5 Heavy Blaster Pistol", "Sporting Blaster Pistol", "Ion Pistol", "Sonic Disruptor", "DT-12 Heavy Blaster", "Blaster Pistol", "Stun Pistol", "Adjudicator Slugthrower", "Snap-Shot Blaster Pistol", "Defender MicroBlaster", "Wrist Blaster", "Bryar Pistol", "Sidearm Blaster Pistol", "Disruptor Pistol", "Heavy Sonic Pistol", "DH-23 Blaster Pistol", "Model 434 DeathHammer", "Subrepeating Blaster", "Sonic Pistol", "Bluebolt Blaster Pistol", "Ascension Gun", "Slugthrower Pistol", "Pulse-Wave Pistol", "Black-Powder Pistol", "Needler", "Bryar Rifle", "Firelance Blaster Rifle", "Heavy Variable Blaster", "Concussion Rifle", "Slugthrower Rifle", "Heavy Blaster Rifle", "Flechette Launcher", "Assault Blaster Rifle", "Repeating Blaster Carbine", "DLT-20A Longblaster", "Stokhli Spray Stick", "Targeting Blaster Rifle", "Incinerator Rifle", "Commando Special Rifle", "Variable Blaster", "Micro Grenade Launcher", "Blaster Carbine", "Stealth Blaster Carbine", "SG-4 Blaster Rifle", "ARC-9965 Blaster", "Sonic Rifle", "Adventurer Slugthrower", "Sporting Blaster Rifle", "Xerrol Nightstinger", "Ion Carbine", "ESPO 500 Riot Gun", "Pulse-Wave Rifle", "Disruptor Rifle", "Snare Rifle", "Light Repeating Blaster", "Ion Rifle", "Double-Barreled Blaster", "Sporting Blaster Carbine", "Scattergun", "Rail Detonator Gun", "Sniper Blaster Rifle", "Interchangeable Rifle System", "Hunting Blaster Carbine", "Blaster Rifle", "Heavy Assault Blaster", "Combat Gloves", "Quarterstaff", "Entrenching Tool", "Club/Baton", "Dire Sword", "Sith Sword", "Slaver Blade", "Contact Stunner", "Mace", "Stunning Gauntlet", "Bayonet", "War Sword", "Knife", "Double-Bladed Sword", "Riot Shield", "Axe", "Snap Baton", "Mythosaur Axe", "Wan-Shen", "Survival Knife", "Stun Baton", "Fire Blade", "Gaderffii", "Shockboxing Gloves", "Throwing Knife", "Short Sword", "Spear", "Datadagger", "Darter", "Targeting Laser", "Bow", "Razor Bug", "Thud Bug", "Sling", "Net", "Saberdart Launcher", "Battering Ram", "Crossbow", "Repeating Crossbow", "Energy Ball", "Light Beskar'gam", "Snowtrooper Armor", "Sandtrooper Armor", "Sith Trooper Armor", "Stormtrooper Armor", "Galactic Alliance Armor", "Light Powered Battle Armor", "Blast Helmet and Vest", "Padded Flight Suit", "Tracker Utility Vest", "Fiber Armor", "Marine Armor", "Blinding Helmet", "Shadowsuit", "Seatrooper Armor", "Shield Gauntlet", "Light Jedi Battle Armor", "Republic Light Armor", "Thinsuit", "KZZ Riot Armor", "Neo-Crusader Light Armor", "Microbe Armor", "Combat Jumpsuit", "Half-Vest", "Light Dark Armor", "Stun Cloak", "Light Pressure Suit", "Mandalorian Combat Suit", "Armored Flight Suit", "Light Battle Armor", "Vonduun Crabshell", "Medium Beskar'gam", "Biohazard Suit", "Medium Pressure Suit", "Mandalorian Battle Armor", "Commando Armor", "Camo Scout Armor", "Republic Combat Armor", "Dark Armor", "Mesh Armor", "Imperial Knight Armor", "Corellian Powersuit", "Knighthunter Armor", "Weave Armor", "Scout Armor", "Camo Armor", "Powered Battle Armor", "Battle Armor", "Cortosis Gauntlet", "Ceremonial Armor", "Stalker Armor", "Jedi Battle Armor", "Personal Armor", "Venom Assault Armor", "Matrix Armor", "Orbalisk Armor", "Zero-Gravity Stormtrooper Armor", "Heavy Battle Armor", "Heavy Dark Armor", "Republic Heavy Armor", "Armored Space Suit", "Heavy Pressure Suit", "Heavy Beskar'gam", "Heavy Powered Battle Armor", "Combat Armor", "Vacuum Pod", "Neo-Crusader Assault Armor", "Pocket Scrambler", "Vox-Box", "Signal Wand", "Comlink", "Com Scrambler", "Panic Ring", "Targeting Beacon", "Visual Wrist Comm", "Holo Converter", "Hands-Free Comlink", "Tightbeam Comlink", "Earbud Comlink", "Computer Interface Visor", "Code Cylinder", "Credit Chip", "Computer Spike", "Triangulation Visor", "Bracer Computer", "Xcalq Stealth Pack", "HiBaka 2000 Mem-Stik", "Datapad", "Portable Computer", "Lectroticker", "Xcalq-3GA \"Slicer Special\" Portable Computer", "Blank Datacards", "Personal Holoprojector", "Rhen-Orm Biocomputer", "Subelectronic Converter", "Energy-Binding Prosthesis", "Cybernetic Prosthesis", "Demolitions Sensor", "Fusion Lantern", "Ooglith Masquer", "Recording Unit", "Motion Sensing Visor", "Sound Sponge", "Holoshroud", "Ambient Aural Amplifier", "Surveillance Tagger", "Electrobinoculars", "Heat Sensor", "Sonar Mapper", "Spy Bug", "Neural Band", "Radiation Detector", "Halo Lamp", "Proximity Flare", "Communication Scanner", "Veridicator", "Surveillance Detector", "Stealth Field Generator", "Sensor Pack", "Vid-Vox Scrambler", "Glow Rod", "Decoy Glow Rod", "Aural Amplifier", "Explosive Charge", "Manual Trigger", "Detonite", "Detonite Cord", "Aquata Breather", "Celegian Life-Support Chamber", "Shipsuit", "Space Suit", "Propulsion Pack", "Breath Mask", "Flight Suit", "Vacuum Mask", "Emergency Vacuum Seal", "Bacta Tank", "FastFlesh Medpac", "Bioscanner", "Medical Interface Visor", "Medical Kit", "MDS-50 Medisensor", "Healing Stimulant", "Hypoinjector Wristband", "Anti-Rad Dose", "Cryogenic Pouch", "Surgery Kit", "Medpac", "Medical Bundle", "Antitoxin Patch", "Antidote Synthesizer", "Toxin Detector", "Sith Poison", "Obah", "Quongoosh Essence", "Bundar Root", "Falsin's Rot", "Dioxis", "Chuba Poison", "Paralytic Poison", "Devaronian Blood-Poison", "Distilled Trihexalon", "Null Gas", "Trauger", "Irksh Poison", "Trihexalon", "Knockout Drugs", "Dawn of Defiance", "Galaxy of Intrigue", "Jungle Hazards", "The Unknown Regions", "Loose Sand", "Crashing Waves", "Muddy Sinkhole", "Space Sickness", "Sucking Mud", "Desert Hazards", "Space Hazards", "Aquatic Hazards", "Poisons", "Korfaise", "Rocky Ground", "Subterranean Hazards", "Crowd", "Acid", "Razor Mushroom", "Nebula", "Thornbushes", "Icy Ground", "Exploding Fungus", "Arctic Hazards", "Set Explosive", "Smoke", "Cardooine Chills", "Climatic Hazards", "Blaster Turret", "Civilized Hazards", "Leeches", "Diseases", "Galaxy at War", "Acid Pool", "Low-Hanging Branches", "Radiation", "Flash Moss", "Animal Trap", "Rising Water", "Blizzard", "Blaster Rifle Turret", "Insect Swarm", "Rough Waters", "Malfunctioning Blast Door", "Undertow", "Freezing Rain", "Kryotin", "Sandstorm", "Tainted Supplies", "Flooded River", "Corrosive Atmosphere", "Extreme Heat or Cold", "Swamp Gas", "Sinkhole", "Micrometeor Shower", "Floating Away", "Electrified Fence", "Dust Devil", "Krytos Virus", "Sandstone Pillars", "Toxic Atmosphere", "Strangle Vines", "Legacy Era Campaign Guide", "Rakghoul Disease", "Carnivorous Plants", "Frozen Lake", "Knights of the Old Republic Campaign Guide", "Vongspawn", "Sith Scrolls and Tomes", "Depressurization", "Solar Wind", "Flash Flood", "Avka Eggs", "Underground Rapids", "Spiner Plant", "Speeder Traffic", "Vacuum", "Cave-In", "Plant Toxin", "Quicksand", "Orbital Defense Mines", "Whirlpool", "Jagged Sinkhole", "Icy Cliffs", "Rock Slide", "Acidic Storm", "Exposed Plasma Conduit", "Avalanche", "Meteorite Storm", "Sea Monster", "Asteroid Field", "Trash Compactor", "Deadly Carnivorous Plants", "Production Assembly Line", "Overloaded Conduit", "Heavy Fog", "Sarlacc Pit", "Forgotten Minefield", "Primed Fuel Line", "Building Decompression", "Lava Flow", "Meteor Shower", "Space Minefield", "Black Hole", "Portable Beacon", "Ration Pack", "Field Camouflage Netting", "All-Temperature Cloak", "Field Food Processor", "Hush-About Personal Jet Pack", "Mandalorian Jet Pack", "Plasma Bridge", "Spacer's Chest", "Camouflage Netting", "Liquid Cable Dispenser", "Climbing Harness", "Personal Field Shelter", "Chain", "ABC Scrambler", "Plastent", "Field Kit", "Vacuum Survival Pouch", "Jet Pack", "Sith Battle Harness", "War Saddle", "Riding Saddle", "Camouflage Poncho", "Subsonic Field Emitter", "Syntherope", "Power Recharger", "Utility Belt", "Repulsor Pad", "Droid Diagnostic", "Security Kit", "Binder Cuffs", "Redirection Crystal", "Microlab", "Plasma Cutter", "Lock Breaking Kit", "Fire Extinguisher", "Power Generator", "Force Detector", "Personal Translator", "Tool Kit", "Mesh Tape", "Force Cage", "Mechanical Interface Visor", "Antisecurity Blades", "Universal Energy Cage", "Fire Rod", "Power Pack", "Biotech Tool Kit", "Water Extractor", "Shield Cage", "Man Trap", "Energy Cell", "Fire Paste", "Repulsor Hitch", "Force Training Aid", "Personal Multitool", "Repulsor Boots", "Beam Splitter", "Armor Reinforcement", "Durasteel Bonding", "Enhanced Energy Projector", "Flash Suppressor/Silencer", "Improved Energy Cell", "Tremor Cell", "Bandolier", "Hair Trigger", "Helmet Package", "Mesh Underlay", "Computerized Interface Scope", "Targeting Scope", "Holster", "Skeletal Reinforcement", "Subcutaneous Comlink", "Tremor Sensor", "Targeting Eye", "Sensory Enhancement", "Telescopic Eye", "BioTech Borg Construct AJ-6", "Infrared Sensor Eye", "Chewbacca's Bowcaster", "Luke Skywalker's Lightsaber", "Emperor Palpatine's Lightsaber", "Han Solo's Heavy Blaster Pistol", "Anakin Solo's Lightsaber", "Boba Fett's Mandalorian Armor", "Extra Modification", "The Darksaber", "Symbol of the Light", "Steeped in the Dark Side", "Legendary Icon", "Sensory Implant", "Nerve Reinforcement Implant", "Cardio Implant", "Memory Implant", "Regenerative Implant", "Combat Implant", "Bio-Stabilizer Implant", "Natural Weapon (Bio-Implant)", "Replacement Body Part", "Natural Armor (Bio-Implant)", "Enhanced Vision", "Body Spikes", "Cosmetic Enhancements", "Poison Filter", "The Sith", "The Muur Talisman", "Sith Amulet", "Darth Revan's Battle Armor", "The Old Republic Era", "Light Armor", "Sith Mask", "The Krath", "The Mask of Darth Nihilus", "Sith Scrolls and Tomes", "Darth Krayt", "Sith Artifacts", "Korriban", "Sith Torture Mask", "The Dark Side", "Sith Talismans", "The Fell Star", "Darth Krayt's Holocron", "Darth Andeddu's Holocron", "Magnetic Feet", "Underwater Drive", "Stationary", "Extra Legs", "Jump Servos", "Burrower Drive", "Wheeled", "Tracked", "Hovering", "Walking", "Flying", "Droid Heroes", "Gyroscopic Stabilizers", "Tactician Battle Computer", "Remote Receiver", "Basic Processor", "Restraining Bolt", "Personality Downloader", "Remote Processor", "Droid Remote Control", "Remote Starship Starter", "Synchronized Fire Circuits", "Specialized Subprocessor", "Hidden Core", "Backup Processor", "Heuristic Processor", "Telescopic Appendage", "Tool", "Rocket Arm", "Stabilized Mount", "Projectile Appendage", "Claw", "Instrument", "Probe", "Climbing Claws", "Magnetic Hands", "Remote Limb Control", "Multifunction Apparatus", "Quick-Release Coupling", "Hand", "YV Sensor Package", "Earphone Binary-to-Basic Translator", "Crystadurium Plating", "Darkvision", "Silence-Bubble Generator", "Droids", "Voice-Print Command Lock", "Mesh Tape Dispenser", "Remote Receiver Signal Booster", "Droid Socket", "Diagnostics Package", "Droid Caller", "Miniaturized Self-Destruct System", "Vocabulator", "Sensor Countermeasure Package", "Spring-Loaded Mechanism", "Laminanium Plating", "Antitheft Comlink Locator", "Droid Oil Bath", "Weapon-Detector Package", "Internal Grapple Gun", "High-Speed Cutting Torch and Welder", "Rental Restraining Bolt", "Secondary Battery", "Blaster Recharge Interface", "Survival Kit (Droid Accessory)", "Communications Jammer", "Remote Receiver Jammer", "Taser", "Micro Shield", "Droid Command Station", "Automap", "Remote Viewer", "Sensor Booster", "Communications Countermeasures", "Droid Recharge Station", "Concealed Item (Droid Accessory)", "Internal Defenses", "Scomp Link", "Duravlex Shell", "Repulsorcam", "Holographic Game System", "Multispectrum Searchlight", "Radiant Heat Element", "Droid Battle Station", "Audio-Radial Stunner", "Laminanium Heavy Plating", "Emergency Oxygen Supply", "Improved Coordination Circuitry", "Collapsible Construction", "ID Dodge", "Improved Sensor Package", "Credit Reader", "Space-Beacon Launcher", "Audio Enhancers", "Courier Compartments", "Shield Expansion Module", "Hidden Holster (Droid Accessory)", "Self-Destruct System", "Video Screen", "Vehicles", "Interference Generator", "Locked Access", "Holographic Image Disguiser", "Compartment Space", "Internal Comlink", "Electric Defense Grid", "Energy Shield (SR 5)", "Energy Shield (SR 10)", "Energy Shield (SR 15)", "Energy Shield (SR 20)", "Energy Shield (SR 25)", "Energy Shield (SR 30)", "Shield Generator (SR 5)", "Shield Generator (SR 10)", "Shield Generator (SR 15)", "Shield Generator (SR 20)", "Translator Unit (DC 20)", "Translator Unit (DC 15)", "Translator Unit (DC 10)", "Translator Unit (DC 5)", "Hardened Systems x2", "Hardened Systems x3", "Hardened Systems x4", "Hardened Systems x5", "Plasteel Shell", "Quadanium Shell", "Durasteel Shell", "Quadanium Plating", "Durasteel Plating", "Quadanium Battle Armor", "Duranium Plating", "Durasteel Battle Armor", "Mandalorian Steel Shell", "Duranium Battle Armor", "Neutronium Plating");

    private static final List<String> VEHICLES = List.of("A-1 Deluxe Floater", "A-24 Sleuth Scout Ship", "A-7 Hunter Interceptor", "A-A5 Speeder Truck", "A-vek Iiluunu Carrier", "A-Wing Starfighter", "A519 Invader Close Support Starfighter", "AA-9 Freighter-Liner", "AAAT", "AAAT/h", "AAT-1", "Aay'han", "Acclamator II-Class Assault Ship", "Acclamator-Class Assault Ship", "Advanced V-19 Torrent Starfighter", "AEA-83 Prospecting Crane", "AEG-77 \"Vigo\" Gunship", "AeroChaser Speeder Bike", "Aerosled Mk III", "Agr Starfighter", "Air-2 Racing Swoop", "Air-Mobile Refresh Droid", "Alderaan Royal Frigate", "All-Terrain Roller", "Alpha-52 Starfighter", "Alpha-Class Xg-1 Star Wing", "Alpha-Class Xg-2 Star Wing", "AML Platform", "Appazanna Dropship", "AR-37 Paragon-Class Light Transport", "ARC-170 Starfighter", "Archaic Heavy Cruiser", "Ardent-Class Fast Frigate", "ARK-II Series Landmaster", "Armored Groundcar", "Arquitens-Class Command Cruiser", "Arquitens-Class Light Cruiser", "Arrow-23 Landspeeder", "Assassin-Class Corvette", "AST", "AT-AA", "AT-AHT", "AT-AP", "AT-AT", "AT-CT", "AT-HE", "AT-KT", "AT-MI", "AT-OT", "AT-PT", "AT-RCT", "AT-RE", "AT-RE/h", "AT-RT", "AT-ST", "AT-TE", "AT-XT", "Aurek Tactical Strikefighter", "AV-7 Antivehicle Cannon", "B-7 Light Transport", "B-Wing Shuttle", "B-Wing Starfighter", "B-Wing/E2 Starfighter", "Baas-Class Space Station", "Baktoid Troop Transport", "Bantha-II Cargo Skiff", "BARC Speeder", "Baronial-Class Yacht", "Basilisk War Droid", "Baudo-Class Star Yacht", "BB-2 Starfire Bomber", "BC-714 Luxury Transport", "Belbullab Heavy Starfighter", "Bellicose-Class Heavy Lifter", "Besh-Type Starfighter", "Blade Heavy Bomber", "Boring Machine", "Braha'tok-Class Gunship", "Broadside-Class Cruiser", "BSX-5 Dreadnought", "Bulwark-Class Mk1 Battlecruiser", "C-73 Tracker Interceptor", "C-9979 Landing Craft", "C-Wing Ugly Fighter", "Canderous-Class Assault Tank", "Cardan I-Class Space Station", "Cardan II-Class Space Station", "Cardan III-Class Space Station", "Carrack-Class Light Cruiser", "CAV PX-10", "Centax-Class Heavy Frigate", "Centurion-Class Battlecruiser", "Chryya-Class Courier", "Chu'unthor", "CIS-Advanced Starfighter", "Citadel-Class Cruiser", "CK-6 Freeco Speeder Bike", "CloakShape Fighter", "Clutch Ugly Fighter", "Commerce Guild Security Corvette", "Conductor-Class Short-Haul Landing Craft", "Conqueror-Class Assault Ship", "Consular-Class Assault Cruiser", "Consular-Class Charger c70", "Consular-Class Cruiser", "Consular-Class Escort Cruiser", "Consular-Class Fighter Carrier", "Consular-Class Medical Cruiser", "Consular-Class Missile Cruiser", "Consular-Class Scout Ship", "Coralskipper", "Cord-Class Starfighter", "Corellian Action VI Transport", "Corellian Barloz-Class Medium Freighter", "Corellian Coronet-Class Fleet Carrier", "Corellian CR-20 Troop Transport", "Corellian CR-25 Troop Carrier", "Corellian Crix-Class Assault Shuttle", "Corellian Crix-Class Courier Shuttle", "Corellian D-Class Starfarer", "Corellian G9 Rigger", "Corellian HWK-290 Light Freighter", "Corellian HWK-390 Light Courier", "Corellian KR-TB Doomtreader", "Corellian Lancet Interceptor", "Corellian MT Dropship", "Corellian Nemesis-Class Patrol Ship", "Corellian PB-950 Patrol Boat", "Corellian Rescue Frigate", "Corellian S-100 Stinger-Class Starfighter", "Corellian S-250 Chela-Class Starfighter", "Corellian Star Cruiser", "Corellian Star Shuttle", "Corellian VCX Auxiliary Shuttle", "Corellian VCX-100 Light Freighter", "Corellian VCX-350 Light Freighter", "Corellian XS Stock Light Freighter", "Corellian YM-2800 Limpet Ship", "Corellian YT-1250 Freighter", "Corellian YT-1300 Transport", "Corellian YT-1760 Courier Transport", "Corellian YT-1930 Transport", "Corellian YT-2000 Transport", "Corellian YT-2400 Transport", "Corellian YU-410 Light Freighter", "Corellian YV-545 Light Freighter", "Corellian YV-560 Light Freighter", "Corellian YV-865 Aurore-Class Freighter", "Corellian YV-929 Armed Freighter", "Corellian YX-1980 Transport", "Corellian YZ-2500 Transport", "Corellian YZ-775 Medium Transport", "Cormelish-Class Battle Mound", "Cormelish-Class Heavy Battle Mound", "Corona Luxury Groundspeeder", "Corona-Class Armed Frigate", "CR70 Corellian Corvette", "CR90 Corellian Corvette", "Crusader-Class Corvette", "CS-1 Cargo Sled", "CSS-1 Corellian Star Shuttle", "Cutlass-9 Patrol Fighter", "CX-133 Chaos Fighter", "Dagger-Class Starfighter", "Dark Squadron TIE Fighter", "User:Darthauthor/The Charging Bantha", "User:Darthauthor/The Regulator", "Davaab-Type Starfighter", "DC0052 \"Intergalactic\" Speeder", "Decimator Tank", "Deep-X Explorer", "DeepWater-Class Light Freighter", "Defender-Class Light Corvette", "Delta-12 Skysprite Trainer", "Delta-6 Stratosprite Interceptor", "Delta-7 Aethersprite Interceptor", "Delta-7 High-Maneuver Aethersprite", "Delta-7 High-Speed Aethersprite", "Delta-7B Aethersprite Light Interceptor", "Derriphan-Class Battleship", "DF.9 Anti-Infantry Battery", "Diamond-Class Cruiser", "DN-25 Treadable", "Doxiadis-Class Warship", "DP-2 Probe Droid", "DP20 Corellian Frigate", "Dreadnaught-Class Heavy Cruiser", "Drexl-Class Starfighter", "Droch-Class Boarding Ship", "Droid Tri-Fighter", "Drop Pod", "Dynamic-Class Freighter", "E-9 Explorer", "E-Wing Starfighter", "Eclipse-Class Star Dreadnought", "Eddicus-Class Planetary Shuttle", "Endurance-Class Fleet Carrier", "Escape Pod", "Espo Walker 101", "Espo Walker 91", "Eta-2 Actis Interceptor", "Eta-2 Heavy-Defense Actis", "Eta-2 High-Maneuver Actis", "Etti Light Transport", "Executor-Class Star Dreadnought", "Exodus-Class Heavy Courier", "F-143 Firespeeder", "F9-TZ Transport", "Fantail-Class Cruiser", "Faraway-Class Scout Ship", "FC-20 Speeder Bike", "FD-79 \"Longfin\" Corvette", "Ferret-Class Reconnaissance Vessel", "Firefly-623 Patrol Craft", "Firespray-31 Patrol Craft", "Flare-S Swoop", "FlitKnot Combat Swoop", "FlitKnot Swoop", "Flyer I-Class Passenger Speeder", "Flyer II-Class Cargo Speeder", "Foray-Class Blockade Runner", "Formicidae-Class Space Tug", "Forward Command Center", "FPC 6.7 Anti-Aircraft Battery", "Freefall-Class Starfighter", "Fury-Class Imperial Interceptor", "Fury-Class Starfighter", "G-Type Light Shuttle", "G1-M4-C Dunelizard Fighter", "Gaba-18m Landspeeder", "Gamma-Class Assault Shuttle", "GAT", "GAT-12h \"Skipray\"", "Gemini-Class Tandem Assault Lander", "Ghtroc 720 Freighter", "Ginivex-Class Starfighter", "Gladiator-Class Star Destroyer", "Gladius-Class Light Freighter", "Golan VIII Space Defense Platform", "Gozanti Cruiser", "GPE-3300 Airspeeder", "GPE-7300 Space Transport", "GR-75 Medium Transport", "Gravsled", "Grievous' Personal Wheel Bike", "Groundcar", "Guardian-Class Light Cruiser", "GX12 Hovervan", "Gymsnor-3 Light Freighter", "H-1 Shuttle", "H-60 Tempest Bomber", "H-HAG-M Tank", "H-Type Royal Yacht", "H-Type Space Yacht", "HAET-221 Gunboat", "HAG Tank", "HAG-M Tank", "HAML Platform", "Hammerhead-Class Cruiser", "Hapan Battle Dragon", "Hardcell-Class Transport", "Harrower-Class Dreadnought", "HAVw A5 Juggernaut", "HAVw A6 Juggernaut", "HeavyLifter-Class Cargo Sled", "Helot-Class Medium Transport", "Heraklon-Class Transport", "Herald-Class Shuttle", "HH-87 Starhopper", "HKD Missile Frigate", "HMP Droid Gunship", "Hornet-Class Interceptor", "Hound's Tooth", "Hover Chair", "HSP-10 Pursuit Airspeeder", "HTT-26 Heavy Troop Transport", "Hulk-Class Junk Compactor", "HummBike", "Hunter-Killer Probot", "Huppla Corvette", "Huppla Cruiser", "Huppla Troop Transport", "Hyena-Class Bomber", "Hyperspace Beacon", "I'Friil Ma-Nat Corvette", "I4 Ionizer Starfighter", "IF-120 Landing Craft", "IG-227 Hailfire-Class Anti-Air Droid", "IG-227 Hailfire-Class Droid Tank", "Imperial Customs Corvette", "Imperial Customs Frigate", "Imperial I-Class Star Destroyer", "Imperial II-Class Frigate", "Imperial II-Class Star Destroyer", "Imperious-Class Star Destroyer", "Inexpugnable-Class Tactical Command Vessel", "Interdictor-Class Star Destroyer", "Interdictor-Class Warship", "Invincible-Class Dreadnaught", "IPV-1 Patrol Craft", "IRD Starfighter", "IRD-A Starfighter", "ISP", "Ixiyen-Class Fast Attack Craft", "J-1 Shuttle", "J-Type Royal Skiff", "J-Type Space Cruiser", "J-Type Star Skiff", "Jadthu-Class Landing Craft", "Jehavey'ir-Type Assault Ship", "JG-8 Luxury Landspeeder", "JumpMaster 5000 Scout Ship", "Jumpstar HPF Starfighter", "Junker TYE-Wing", "JX40 Jailspeeder", "K-222 Aero-Interceptor", "K-Wing Assault Starfighter", "KAAC Freerunner", "Kaloth-Style Battlecruiser", "Kandosii-Type Dreadnaught", "Kantrey-Class Amphibious Starfighter", "Kappa-Class Shuttle", "Kas Air Cruiser", "Kas Heavy Tank", "Kas Tank", "Kazellis-Class Light Freighter", "KE-8 Enforcer Craft", "Keldabe-Class Battleship", "Kettrifree Air Mover", "Kihraxz Assault Fighter", "Killik Dartship", "Kiree Starfighter", "Kor Chokk Grand Cruiser", "Koro-2 Exodrive Airspeeder", "Koros-Strohna Worldship", "Krayt's Honor", "KT-400 Military Droid Carrier", "Kybuck Speeder Bike", "Kyramud-Type Battleship", "LAAB-2", "LAAG-2", "LAAT/a Gunship", "LAAT/c Gunship", "LAAT/i Gunship", "LAAT/s Gunship", "LAAT/v Gunship", "LAATAK", "Lady-Class Luxury Liner", "Lambda-Class Shuttle", "Lancer-Class Frigate", "Lancer-Class Pursuit Craft", "Lancet Aerial Artillery", "Land Crawler", "Laser Borer", "Lethisk-Class Armed Freighter", "Liberator-Class Starfighter", "Lictor-Class Dungeon Ship", "Lifeline-Class Escape Pod", "LM-432 \"Infantry Support\" Crab Droid", "LM-432 \"Light Assault\" Crab Droid", "LM-432 \"Medium Assault\" Crab Droid", "LM-432 \"Sentry\" Crab Droid", "LM-432 \"Trailblazer\" Crab Droid", "Longarm-Class Customs Inspection Craft", "Loronar Medium Transport", "LR1K Sonic Cannon", "Lucartipede-Class Cargo Speeder", "Lucrehulk-Class Battleship", "Lucrehulk-Class Cargo Hauler", "Lucrehulk-Class Core Ship", "Lucrehulk-Class Cruiser", "Lucrehulk-Class Destroyer", "Lucrehulk-Class Droid Control Ship", "Luke's Snowspeeder", "LuxurPort Zisparanza", "Luxury 3000 Space Yacht", "Luxury-Class Sail Barge", "M12-L Kimogila Heavy Fighter", "M22-T \"Krayt\" Gunship", "M3-A Scyk Fighter", "M31-Airspeeder", "MAF Gunship", "Magnaline 3000 Airbus", "Majestic-Class Heavy Cruiser", "Maka-Eekai L4000 Transport", "Mandator I-Class Star Dreadnought", "Mandator II-Class Star Dreadnought", "Mankvim-814 Light Interceptor", "Manta Droid Subfighter", "Marauder Corvette", "MC-24a Light Shuttle", "MC20 Star Corvette", "MC80 Star Cruiser", "MC80a Star Cruiser", "MC85 Star Cruiser", "Medlifter Troop Transport", "MedStar-Class Frigate", "Mekuun Heavy Tracker", "Mere Cruiser", "Mere Light Transport", "Mere Space Tug", "Metrocab Landspeeder", "MG-100 StarFortress Bomber", "Miid Ro'ik Warship", "Ministry-Class Orbital Shuttle", "Minstrel-Class Space Yacht", "Miy'til Assault Bomber", "Miy'til Fighter", "Mk 2 Droid Bomber", "MMLT Missile Launcher", "Mobile Spacedock 220", "Mobquet Medium Transport", "Model 67 Shrieker", "Modified T-47 Airspeeder", "Modified Z-95 Headhunter", "MorningStar Assault Starfighter", "MPTL-2a Mobile Proton Torpedo Launcher", "MR/RV", "MRX-BR Pacifier", "MT-AT", "MTT", "Mu-Class Long Range Shuttle", "Munificent-Class Frigate", "MVR-3 Combat Speeder Bike", "MVR-3 Mini-Sub", "MVR-3 Speeder Bike", "N-1 Royal Starfighter", "Nantex-Class Territorial Starfighter", "Nebula-Class Star Destroyer", "Nebulon-B Frigate", "Nebulon-Q Swoop Racer", "Neutralizer-Class Bomber", "Neutron Star-Class Bulk Cruiser", "Nova-Class Battlecruiser", "NovaSword Superiority Fighter", "NR-N11 Energy Digger", "NR-N38 Energy Pummel", "NR-N77 Dissuader-Class Artillery Platform", "NR-N79 Dissuader-Class Heavy Artillery Platform", "NR-N99 Persuader-Class Droid Enforcer", "Nssis-Class Clawcraft", "NTB-630 Naval Bomber", "Nu-Class Attack Shuttle", "Nune-Class Imperial Shuttle", "Octuptarra Magna Tri-Droid", "OG-10 Repeater Spider Droid", "OG-9 Homing Spider Droid", "Orbital Service Shuttle 23K", "Outbound Flight", "P-38 Starfighter", "PAC", "Palpatine's Theta Shuttle", "Pellaeon-Class Star Destroyer", "Pelta-Class Medical Frigate", "Penumbra-Class Attack Shuttle", "Phoebos-Class Starfighter", "Pinook Fighter", "PL-90 Luxury Speeder", "Porter-Class Transport", "Praetorian-Class Frigate", "Predator-Class Starfighter", "Preybird-Class Starfighter", "Procurator-Class Star Battlecruiser", "Protector VI Patrol Speeder", "Protodeka Tank Droid", "Prototype Rebel Assault Frigate", "Prototype War Walker", "Providence-Class Destroyer", "Prowler-Class Reconnaissance Vessel", "PTB-625 Planetary Bomber", "Pteropter Hover Pod", "PTV-2100 Incarcerator", "Punworcca 116-Class Interstellar Sloop", "Pursuer-Class Enforcement Ship", "PX-4 Mobile Command Base", "QH-7 Chariot Command Speeder", "Quartermaster-Class Supply Carrier", "Quasar Fire-Class Carrier", "QuickFire Speeder Bike", "R-2000 Raptor Speeder Bike", "R-28 Starfighter", "R-41 Starchaser", "R-42 Starchaser", "Raider-Class Corvette", "Rampart-Class Assault Shuttle", "Rapid Deployment Airspeeder", "Razor-Class Starfighter", "RC-2 Twilight Scoutship", "Rebel Assault Frigate Mk I", "Rebel Assault Frigate Mk II", "Recusant-Class Light Destroyer", "Redthorn-Class Scout Ship", "Relay Starfighter", "Rian-327 Airspeeder", "Rihkxyrk Assault Fighter", "Rin Assid Bulk Hauler", "Ripper Speeder Bike", "Ro'ik Chuun M'arh Frigate", "Rogue Shadow", "Royal Fortune-Class Light Clipper", "Royal TIE Interceptor", "RTT", "Runner-Class Short-Range Shuttle", "RX4 Patrol Ship", "S-130 \"Shelter\" Speeder", "S40K Phoenix Hawk-Class Light Pinnace", "Sabaoth Defender", "Sabaoth Destroyer", "Sabaoth Frigate", "Sabaoth Hex Bomber", "Sabaoth Hex Deployer", "Sabaoth Spy Ship", "Sabaoth Starfighter", "Sabertooth-Class Assault & Rescue Vessel", "Saesee Tiin's Aethersprite Interceptor", "Sandcrawler", "Scarab-Class Droid Starfighter", "Scimitar Assault Bomber", "SCT Scout Craft", "Scurrg H-6 Prototype Bomber", "Scythe-Class Battlecruiser", "Seismic Mining Vehicle", "Seltaya-Class Fast Courier", "Seltiss-2 Caravel", "Seltiss-2 Caravel Cabin", "Sentinel-Class Heavy Strike Mech", "Sentinel-Class Landing Craft", "Sentinel-Class Strike Mech", "Sh'rip Sh'pa Spawn Ship", "Shaadlar-Type Troopship", "Shackles of Nizon", "ShaShore-Class Frigate", "Sheathipede-Class Shuttle", "Sigma-Class Long-Range Shuttle", "Sith Enforcer Tank", "Sith Infiltrator", "Sith Interceptor", "Skirmisher Boarding Craft", "SkyBlind Landing Sphere", "SkyBlind Recon Ship", "Skyfire-Class Heavy Assault Cruiser", "Slave I", "Spacetrooper Squad", "SPHA", "Sphyrna-Class Corvette", "Spinward-Class Tender", "Sprint-Class Rescue Craft", "SRV-1 Scout/Retrieval Vehicle", "STAP", "STAP-2", "Star Galleon-Class Frigate", "Star Saber XC-01 Starfighter", "Star Seeder-Class Colony Ship", "StarForge Station", "StarSpeeder-3000 Transport", "StarViper-Class Assault Fighter", "Storm IV Cloud Car", "Strike-Class Medium Cruiser", "Strikebreaker Repulsorlift", "Supa Fighter", "Supremacy-Class Attack Ship", "Surfeik Cruiser", "Suuv Ban D'Krid Cruiser", "Swift Assault 5 Hovercraft", "Swoop Racer", "SX-4 Troop Transport", "SX20 Airskimmer", "T-16 Skyhopper", "T-19 Starfighter", "T-65B X-Wing Starfighter", "T-65BR X-Wing Starfighter", "T-65XJ3 X-Wing Starfighter", "T-70 X-Wing Starfighter", "T-Wing Interceptor", "T4-B Heavy Tank", "T8 Loading Vehicle", "Talon I Cloud Car", "Talon-Class Starfighter", "Tartan-Class Patrol Cruiser", "Tavya-Class Picket Starfighter", "Tector-Class Star Destroyer", "Terminus-Class Destroyer", "Teroch-Type Gunship", "The Anakin Solo", "The Ark Angel", "The Arkanian Legacy", "The Azure Angel", "The Banshee", "The Blaze of Glory", "The Blood Brother", "The Bloody Credit", "The Crimson Axe", "The Death Star", "The Dragon's Tooth", "The Drunk Dancer", "The Ebon Hawk", "The Errant Venture", "The Eye of Shiblizar", "The FarStar", "The Ghost", "The Grinning Liar", "The Guardian Mantis", "The Hasty Harpy", "The Indomitable", "The Inferno", "The Invisible Hand", "The Last Resort", "The Lusankya", "The Millennium Falcon", "The Moomo Williwaw", "The Mynock", "The Nova Eclipse", "The Phantom", "The Pincer", "The Scarlet Star", "The Shark", "The Sharp Spiral", "The Skorp-ION", "The Soulless One", "The Star Home", "The Virago", "The Wheel", "The Zoomer", "Theta-Class Shuttle", "Thranta-Class Warship", "Tibanna Gas Hauler", "Tibanna Refinery Platform", "TIE Advanced", "TIE Aggressor", "TIE Avenger", "TIE Bomber", "TIE Crawler", "TIE Defender", "TIE Fighter", "TIE Hunter", "TIE Interceptor", "TIE Mauler", "TIE Oppressor", "TIE Phantom", "TIE Prototype", "TIE Reaper", "TIE Scout", "TIE Shuttle", "TIE Striker", "TIE-E M3", "TIE/sf Fighter", "TIS Zeta 19", "TL-1800 Light Freighter", "Ton-Falk-Class Escort Carrier", "Toscan 8-Q Starfighter", "Trade Federation Cargo Freighter", "Trade Federation Troop Transport", "Trakworx-Class Maintenance Cart", "Transit-Class Cargo Shuttle", "Transpeeder", "TransTrak-Class Train Car", "Tri-Mark VII Interceptor", "Tri-Scythe-Class Frigate", "Tri-Wing Shuttle", "Trident-Class Assault Ship", "Tsmeu-6 Personal Wheel Bike", "TT-6 Landspeeder", "Turbostorm-Class Gunship", "Twin-228 Airspeeder", "TX-130 Saber-Class Fighter Tank", "TX-130J Saber-Class Fighter Tank", "TX-130T Fighter Tank", "U-LAV", "U-Wing Starfighter/Support Craft", "UR-40M Patrol Speeder", "Urban Bombardier Speeder Bike", "Urban Navigator Speeder Bike", "UT-AT", "Util-313 Airbus", "Uumufalh Gunship", "v-150 Planet Defender", "v-188 \"Penetrator\" Turbolaser", "V-19 Torrent Starfighter", "V-35 Courier", "V-Wing Starfighter", "VAAT/e Transport", "VAC", "Vaksai Fighter", "Valor-Class Cruiser", "Vault-Class Armored Speeder", "Vaya-Class Scout Ship", "Veltiss-2 Airspeeder", "Venator-Class Star Destroyer", "Victory I-Class Star Destroyer", "Victory II-Class Star Destroyer", "Vindicator-Class Heavy Cruiser", "Viper Swoop Bike", "Viscount-Class Star Defender", "VLD2261 Laser Turret", "VT-49 Decimator", "Vua'spar Interdictor", "VXL Hotrod Speeder", "Wandering Flyer 191", "Warpod Pinnace", "Wavecrest-Class Frigate", "Wawaatt Arms Dual Missile Turret", "Wawaatt Arms Quadrail Launcher", "Wayfarer-Class Medium Transport", "Whitecloak Fighter", "Wind Skiff", "WLO-5 Speeder Tank", "WLZ 101 Groundcoach", "X-34 Landspeeder", "X-34 Speeder Escort", "X-70B Phantom-Class Operations Craft", "X-83 TwinTail Starfighter", "X-TIE Ugly Fighter", "X10 Groundcruiser", "X4 Gunship", "Xiytiar-Class Heavy Transport", "XJ-15 Airspeeder", "XJ-2 Airspeeder", "XJ-6 Airspeeder", "XQ2 Space Platform", "Xyston-Class Star Destroyer", "Y-Bike-Class Speeder Bike", "Y-Wing \"Courier\"", "Y-Wing \"Longprobe\"", "Y-Wing Bomber", "Y-Wing Starfighter", "Y164 Slave Transport", "YE-4 Gunship", "YKL-37R Nova Courier", "Yorik-Stronha Spy Ship", "Yorik-trema", "Yorik-vec Cruiser", "Z-10 Seeker-Class Scout Ship", "Z-95 Headhunter", "Zebra Starfighter", "Zephyr-G Swoop Bike");

    private static final List<String> AFFILIATIONS = List.of("AgriCorps", "Alliance Intelligence", "Anzati Assassins", "Bearers of the Claatuvac", "Beasts", "Black Sun", "Churhee's Riflemen", "Core Craft", "CorSec", "Death Watch", "Doc's Mod Ring", "EduCorps", "Eeook Mining and Reclamation", "Epsis", "ExplorCorps", "Freedom's Sons", "General Units", "House Korden", "House Organa", "Imperial Intelligence", "Kota's Militia", "Lhosan Industries", "Lightning Squadron", "MedCorps", "New Republic Intelligence", "Ploovo's Protocol Team", "Republic Intelligence", "Sabaoth Squadron", "Sando's Boys", "Skull Squadron", "Sugi's Crew", "Systino", "TaggeCo", "Tangan Industries", "The Agents of Ossus", "The Ailon Nova Guard", "The Aing-Tii Monks", "The Altirian Republic", "The Altisian Jedi", "The Ammuud Clans", "The Anarrian Empire", "The Anjiliac Clan", "The Antarian Rangers", "The Apex Society", "The Arkanian Dominion", "The Bando Gora", "The Banvhar Combine", "The Baran Do Sages", "The Believers", "The Black Hole Pirates", "The Blackguard", "The Blazing Chain", "The Bothan SpyNet", "The Bounty Hunters' Guild", "The Car'das Smugglers", "The Chiss Ascendancy", "The Claatuvac Guild", "The Confederacy of Independent Systems", "The Core World Nobles", "The Corellian Confederation", "The Corporate Sector Authority", "The Crimson Dawn", "The Crimson Nova", "The Crimson Stars", "The Cult of Veroleem", "The Despot Army", "The Disciples of Twilight", "The Droid Equality Foundation", "The Duskhan League", "The Ebon Strikers", "The Ebruchi Fleet", "The Ember of Vahl", "The Esh-kha Collective", "The Eternal Alliance", "The Eternal Empire", "The Fallanassi", "The Fel Empire", "The Felucian Shamans", "The Feudal Empire", "The Firebird Society", "The First Order", "The Flesh Raiders", "The Freedom Convoy", "The Fringe", "The Galactic Alliance", "The Galactic Alliance Guard", "The Galactic Empire", "The Galactic Republic", "The Galactic Triumvirate", "The GenoHaradan", "The Guild of Blood", "The Guild of Dust", "The Guild of Fire", "The Guild of Steel", "The Hapan Royal Guard", "The Hapes Consortium", "The Horizon Guard", "The Hutt Kajidics", "The Imperial Army", "The Imperial Knights", "The Imperial Military", "The Imperial Navy", "The Infinite Empire", "The Inquisitorius", "The Intergalactic Zoological Society", "The Iron Knights", "The Iron Ring", "The Jal Shey", "The Je'daii Rangers", "The Jedi", "The Jedi Clans", "The Jensaarai", "The Joruba Consortium", "The Katarn Commandos", "The Keetael", "The Kilian Rangers", "The Killik Colony", "The Knights of Ren", "The Kolkpravis", "The Korunnai", "The Krath", "The Lakhasa Caravan", "The Lok Revenants", "The Lugubraa Hordes", "The Luka Sene", "The Mandalorian Protectors", "The Mandalorians", "The Matukai", "The Mecrosa Order", "The Merchants' Consortium", "The Miners' Union", "The Mistryl Shadow Guard", "The Mnggal-Mnggal", "The Morgukai", "The Naboo Resistance", "The Native Tribes", "The Nature Priests", "The New Republic", "The Nihil", "The Nimbus Commandos", "The Nyriaanan Clans", "The O'reenian Imperium", "The Old Republic", "The Onderon Beast Riders", "The Order of Shasa", "The Ordu Aspectu", "The Parallax Chain", "The Peace Brigade", "The Pius Dea", "The Praetorite Vong", "The Prophets of the Dark Side", "The Razor Penitents", "The Rebel Alliance", "The Red Fury Brotherhood", "The Republic Rocket-Jumpers", "The Resistance", "The Sable Dawn", "The Senate Guard", "The Seyugi Dervishes", "The Shapers of Kro Var", "The Sith", "The Sith Empire", "The Sith Rebels", "The Sorcerers of Tund", "The Ssi-Ruuvi Imperium", "The Sun Guards of Thyrsus", "The Tapani Noble Houses", "The Techno Union", "The Tenloss Syndicate", "The Thalassian Slavers", "The Tof Kingdom", "The Tommaba Brotherhood", "The Trianii Rangers", "The Tusken Tribes", "The Tyia", "The Vagaari Empire", "The Veroleem Resistance", "The Vipers", "The Wardens of the Sky", "The Wavelength Gale", "The Wing Guard", "The Witches of Dathomir", "The Xim Empire", "The Yuuzhan Vong Empire", "The Zann Consortium", "The Zeison Sha", "The Zygerrian Slavers' Guild", "Thunder Road Crew", "Tor-Ro-Bo Industries", "Wraith Squadron");

    private static final List<String> TALENTS = List.of("Dull the Pain", "Interrogator", "Medical Droid", "Known Vulnerability", "Medical Analyzer", "Science Analyzer", "Triage Scan", "Adept Assistant", "Mechanics Mastery", "Vehicle Mechanic", "Burst Transfer", "On-Board System Link", "Quick Astrogation", "Scomp Link Slicer", "Etiquette", "Helpful", "Protocol", "Nuanced", "Observant", "Supervising Droid", "Talkdroid", "Combat Repairs", "Droid Smash", "Targeting Package", "Just a Scratch", "Target Acquisition", "Target Lock", "Weapons Power Surge", "Cargo Hauler", "Environmentally Shielded", "Power Supply", "Durable", "Heavy-Duty Actuators", "Load Launcher", "Task Optimization", "Forward Patrol", "Mobile Combatant (Advance Patrol Talent Tree)", "Trailblazer", "Watchful Step", "Battlefield Medic", "Bring Them Back", "Emergency Team", "Extra First Aid", "Medical Miracle", "Natural Healing", "Second Chance", "Steady Under Pressure", "Psychiatric Caregiver", "Mental Health Specialist", "Buried Presence", "Conceal Other", "Insightful Aim", "Vanish", "Aura of Freedom", "Folded Space Mastery", "Liberate", "Many Shades of the Force", "Spatial Integrity", "Disciplined Strike", "Telekinetic Power", "Telekinetic Savant", "Aversion", "Force Flow", "Illusion", "Illusion Bond", "Influence Savant", "Link", "Masquerade", "Move Massive Object", "Suppress Force", "Telekinetic Prodigy", "Telepathic Influence", "Telepathic Link", "Force Bond", "Force Prodigy", "Influence Natural", "Kinetic Might", "Telekinetic Natural", "Transfer Force", "Ambush Specialist", "Destructive Ambusher", "Keep It Going", "Keep Them Reeling (Ambusher Talent Tree)", "Perceptive Ambusher", "Spring the Trap", "Anticipate Movement", "Forewarn Allies", "Get Down", "Heavy Fire Zone", "Summon Aid", "Armor Mastery (Armor Specialist Talent Tree)", "Armored Defense", "Improved Armored Defense", "Juggernaut", "Second Skin", "Shield Expert", "Advantageous Positioning", "Get Some Distance", "Murderous Arts I", "Murderous Arts II", "Ruthless (Assassin Talent Tree)", "Shift", "Sniping Assassin", "Sniping Marksman", "Sniping Master", "Defensive Electronics", "Ion Resistance 10", "Soft Reset", "Modification Specialist", "Repair Self", "Just a Droid", "Swift Droid", "Acute Senses", "Expert Tracker", "Improved Initiative", "Keen Shot", "Uncanny Dodge I", "Uncanny Dodge II", "Reset Initiative", "Weak Point", "Cyborg Avatar", "Cyborg Martyr", "Droid Receptacle", "Enlightened Meditation", "Serene Courage", "Bando Gora Surge", "Force Fighter", "Resist Enervation", "Victorious Force Mastery", "Enhanced Danger Sense", "Expanded Horizon", "Knowledge and Defense", "Planetary Attunement", "Precognitive Meditation", "Charm Beast (Beastwarden Talent Tree)", "Bonded Mount", "Entreat Beast", "Soothing Presence", "Wild Sense", "Animal Companion", "Animal Senses", "Calming Aura", "Comprehend Speech", "Improved Companion", "Nature Sense", "Shared Aptitude", "Believer Intuition", "Defense Boost", "Hardiness", "High Impact", "Sith Reverence", "Blackguard Initiate", "Wilder Marauder", "Wilder Ravager", "Wilder Trinity Assassin", "Force Directed Shot", "Negate and Redirect", "Rising Anger", "Rising Panic", "Close Cover", "Outrun", "Punch Through", "Small Target", "Watch This", "Another Coat of Paint", "Fly Casual", "SpyNet Agent", "Bothan Resources", "Knowledge is Life", "Knowledge is Power", "Knowledge is Strength", "Six Questions", "Hunter's Mark", "Hunter's Target", "Notorious (Bounty Hunter Talent Tree)", "Nowhere to Hide", "Relentless", "Ruthless Negotiator", "Detective", "Dread", "Electronic Trail", "Familiar Enemies", "Familiar Situation", "Fearsome", "Jedi Hunter", "Nowhere to Run", "Quick Cuffs", "Revealing Secrets", "Signature Item", "Tag", "Expert Grappler", "Gun Club", "Melee Smash", "Stunning Strike", "Unbalance Opponent", "Bayonet Master", "Cantina Brawler", "Counterpunch", "Crowd Control", "Devastating Melee Smash", "Disarm and Engage", "Entangler", "Experienced Brawler", "Grabber", "Hammerblow", "Make Do", "Man Down", "Pick a Fight", "Reverse Strength", "Strong Grab", "Sucker Punch", "Unrelenting Assault", "Cheap Trick", "Easy Prey", "Quick Strike (Brigand Talent Tree)", "Sly Combatant", "Gang Leader", "Melee Assault", "Melee Brute", "Melee Opportunist", "Squad Brutality", "Squad Superiority", "Hidden Movement", "Improved Stealth", "Total Concealment", "Dig In", "Extended Ambush", "Ghost Assailant", "Hide in Plain Sight", "Hunker Down", "Shadow Striker", "Slip By (Camouflage Talent Tree)", "Blowback", "Close Contact", "Multiattack Proficiency (Rifles) (Carbineer Talent Tree)", "Old Faithful", "Opportunity Fire", "Rifle Master", "Shoot from the Hip", "Snap Shot", "Chalactan Adept", "Lesser Mark of Illumination", "Greater Mark of Illumination", "Chalactan Enlightenment", "Clone Scientist", "Gene Splicing", "Mass Cloning", "Master Cloner", "Rapid Cloning", "Retrovirus", "Double Agent", "Enemy Tactics", "Feed Information", "Friendly Fire", "Protection", "Battle Analysis", "Cover Fire", "Demolitionist", "Draw Fire", "Harm's Way", "Indomitable", "Tough as Nails", "Coordinated Effort", "Dedicated Guardian", "Dedicated Protector", "Defensive Position", "Hard Target", "Keep Them at Bay", "Out of Harm's Way (Commando Talent Tree)", "Combat Instincts", "Grenadier", "Damage Reduction 10", "Equilibrium", "Force Focus", "Force Recovery", "Beast Trick", "Channel Energy", "Force Exertion", "Force Harmony", "Force Suppression", "Indomitable Will", "The Will to Resist", "Telekinetic Stability", "Force Absorb", "Force Concealment", "Force Stealth", "Trust the Force", "Velocity", "Vitality Strike", "Competitive Drive", "Competitive Edge", "Corporate Clout", "Impose Confusion", "Impose Hesitation", "Willful Resolve", "Wrong Decision", "Cowards Flight", "Craven Appeal", "Feign Harmlessness", "Harmless Distraction", "Not in the Face", "Deny Move", "Extended Critical Range (Heavy Weapons)", "Extended Critical Range (Rifles)", "Flurry Attack", "Knockback", "Reduce Defense", "Reduce Mobility", "Extended Critical Range (Simple Weapons)", "Channel Aggression", "Channel Anger", "Crippling Strike", "Embrace the Dark Side", "Dark Side Talisman", "Greater Dark Side Talisman", "Power of the Dark Side", "Dark Presence", "Revenge", "Swift Power", "Consumed by Darkness", "Dark Preservation", "Dark Side Savant", "Drain Knowledge", "Transfer Essence", "Wrath of the Dark Side", "Blast of Hatred", "Crushing Power", "Dark Dream", "Dark Power", "Dark Side Maelstrom", "Adept Spellcaster (Dathomiri Witch Talent Tree)", "Charm Beast (Dathomiri Witch Talent Tree)", "Command Beast (Dathomiri Witch Talent Tree)", "Flight", "Cloak of Shadows", "Phantasm", "Revelation", "Shadow Armor", "Shadow Vision", "Ambush (Disgrace Talent Tree)", "Castigate", "Dirty Tactics", "Misplaced Loyalty", "Two-Faced", "Unreadable", "Automated Strike", "Droid Defense", "Droid Mettle", "Expanded Sensors", "Inspire Competence", "Maintain Focus", "Overclocked Troops", "Reinforce Commands", "Force Fortification", "Greater Weapon Focus (Lightsabers)", "Greater Weapon Specialization (Lightsabers)", "Multiattack Proficiency (Lightsabers)", "Severing Strike", "Improved Lightsaber Throw", "Improved Riposte", "Improved Redirect", "Lightsaber Form Savant", "Thrown Lightsaber Mastery", "Shoto Master", "Twin Attack (Lightsabers)", "Break Program", "Heuristic Mastery", "Scripted Routines", "Ultra Resilient", "Initiate of Vahl", "Reading the Flame", "Sword of Vahl", "Vahl's Brand", "Vahl's Flame", "Cover Bracing", "Intentional Crash", "Nonlethal Tactics", "Pursuit", "Respected Officer", "Slowing Stun", "Takedown", "Fade Out", "Keep Together", "Prudent Escape", "Reactive Stealth", "Sizing Up", "Arrogant Bluster", "Band Together", "Galactic Guidance", "Rant", "Self-Reliant", "Elusive Dogfighter", "Full Throttle", "Juke", "Keep it Together (Expert Pilot Talent Tree)", "Relentless Pursuit", "Vehicular Evasion", "Blind Spot", "Clip", "Close Scrape", "Improved Attack Run", "Master Defender", "Renowned Pilot", "Roll Out", "Shunt Damage", "Vehicle Focus", "Wingman", "Charm Beast (Felucian Shaman Talent Tree)", "Command Beast (Felucian Shaman Talent Tree)", "Detonate", "Hive Mind", "Infuse Weapon", "Sickening Blast", "Noble Fencing Style", "Demoralizing Defense", "Leading Feint", "Personal Affront", "Transposing Strike", "Force Power Adept", "Force Treatment (Force Adept Talent Tree)", "Fortified Body", "Instrument of the Force", "Long Call", "Mystical Link", "Battle Precognition", "Force Blank", "Lightsaber Evasion", "Precision Fire", "Steel Mind", "Strong-Willed", "Telekinetic Resistance", "Attune Weapon", "Empower Weapon", "Force Talisman", "Greater Force Talisman", "Focused Force Talisman", "Force Throw", "Greater Focused Force Talisman", "Primitive Block", "Defensive Roll", "Force Intuition (Force Warrior Talent Tree)", "Improved Defensive Roll", "Unarmed Specialisation", "Fool's Luck", "Fortune's Favor", "Gambler", "Knack", "Lucky Shot", "Avert Disaster", "Better Lucky than Dead", "Dumb Luck", "Labyrinthine Mind", "Lucky Stop", "Ricochet Shot", "Uncanny Luck", "Unlikely Shot", "Gambler's Fortune", "Savant", "Barter", "Fringe Savant", "Long Stride", "Jury-Rigger", "Flee", "Keep it Together (Fringer Talent Tree)", "Sidestep", "Surge", "Swift Strider", "Disciplined Trickery", "Group Perception", "Hasty Withdrawal", "Stalwart Subordinates", "Stay in the Fight (Fugitive Commander Talent Tree)", "Stealthy Withdrawal", "Diplomatic Poise", "Living Memory", "Master of Will", "One Word, Two Meanings", "When the Veils Move", "Willful Senator", "Assault Gambit", "Direct Fire", "Face the Foe", "Lead From the Front", "Luck Favors the Bold", "Findsman Ceremonies", "Findsman's Foresight", "Omens", "Target Visions", "Temporal Awareness", "Deadly Repercussions", "Manipulating Strike", "Improved Manipulating Strike", "Pulling the Strings", "Brutal Attack", "Call Out", "Distracting Attack", "Exotic Weapons Master", "Lockdown Strike", "Multiattack Proficiency (Exotic Weapons)", "Personal Vendetta", "Unstoppable", "Crucial Advice", "Distracting Apparition", "Guardian Spirit", "Manifest Guardian Spirit", "Vital Encouragement", "Dogfight Gunner", "Expert Gunner", "Quick Trigger", "System Hit", "Crippling Hit", "Fast Attack Specialist", "Great Shot", "Overcharged Shot", "Synchronized Fire", "Debilitating Shot", "Deceptive Shot", "Improved Quick Draw", "Knockdown Shot", "Multiattack Proficiency (Pistols)", "Ranged Disarm", "Trigger Work", "Blind Shot", "Damaging Disarm", "Keep Them Honest", "Lingering Debilitation", "Mobile Attack (Pistols)", "Pistol Duelist", "Ranged Flank", "Retreating Fire", "Slowing Shot", "Swift Shot", "Deep-Space Gambit", "Guidance", "Hidden Attacker", "Hyperspace Savant", "Vehicle Sneak", "Silent Movement", "Hyperdrive Tinkerer", "Hyperlane Mastery", "Regional Expertise", "Instruction", "Idealist", "Know Your Enemy", "Known Dissident", "Lead by Example (Ideologue Talent Tree)", "Cower Enemies", "Force Interrogation", "Inquisition", "Unsettling Presence", "Adrenaline Implant", "Precision Implant", "Resilience Implant", "Speed Implant", "Strength Implant", "Bigger Bang", "Capture Droid", "Custom Model", "Improved Jury-Rig", "Improvised Device", "Inspire Fear I", "Inspire Fear II", "Inspire Fear III", "Notorious (Infamy Talent Tree)", "Shared Notoriety", "Fear Me", "Frighten", "Master Manipulator (Infamy Talent Tree)", "Small Favor", "Terrify", "Unsavory Reputation", "Always Ready", "Concealed Weapon Expert", "Creeping Approach", "Set for Stun", "Silent Takedown", "Presence", "Demand Surrender", "Improved Weaken Resolve", "Weaken Resolve", "Fluster", "Intimidating Defense", "Allure", "Captivate", "Conflict is my Strength", "Persuasive", "Bolster Ally", "Ignite Fervor", "Inspire Confidence", "Inspire Haste", "Inspire Zeal", "Beloved", "Willpower", "Droid Duelist", "Force Repair", "Heal Droid", "Mask Presence", "Silicon Mind", "Action Exchange", "Force Delay", "Imbue Item", "Knowledge of the Force", "In Balance", "Master of Balance", "Je'daii Blade Expert", "There is No Fear", "Direct", "Impart Knowledge", "Insight of the Force", "Master Advisor", "Scholarly Knowledge", "Call Weapon", "Lightsaber Specialist", "Masterwork Lightsaber", "Perfect Attunement", "Quick Modification", "Defensive Circle", "Force Revive", "Jedi Battle Commander", "Slashing Charge", "Mobile Attack (Lightsabers)", "Adept Negotiator", "Force Persuasion", "Master Negotiator", "Skilled Advisor", "Adversary Lore", "Aggressive Negotiator", "Cleanse Mind", "Collective Visions", "Consular's Vitality", "Consular's Wisdom", "Entreat Aid", "Force of Will", "Guiding Strikes", "Improved Consular's Vitality", "Know Weakness", "Recall", "Renew Vision", "Visionary Attack", "Visionary Defense", "WatchCircle Initiate", "Healing Mastery", "Jedi Healer", "Acrobatic Recovery", "Battle Meditation", "Elusive Target", "Force Intuition (Jedi Guardian Talent Tree)", "Resilience", "Close Maneuvering", "Cover Escape", "Defensive Acuity", "Exposing Strike", "Forceful Warrior", "Grenade Defense", "Guardian Strike", "Hold the Line", "Immovable", "Improved Battle Meditation", "Mobile Combatant (Jedi Guardian Talent Tree)", "Battle Meld", "Blaster and Saber", "Blaster Deflect", "Combat Sense", "Force Meld (Jedi Guardian Talent Tree)", "Guardian's Insight", "Jedi Ready", "Mettle", "Force Treatment (Jedi Healer Talent Tree)", "Healing Boost", "Improved Healing Boost", "Soothe", "Return to Life", "Vital Synchronism", "Apprentice Boon", "Share Force Secret", "Share Force Technique", "Share Talent", "Transfer Power", "Echoes of the Force", "Jedi Quarry", "Prepared for Danger", "Sense Deception", "Unclouded Judgement", "Cover Your Tracks", "Difficult to Sense", "Force Veil", "Jedi Network", "Clear Mind", "Dark Side Sense", "Dark Side Scourge", "Force Haze", "Resist the Dark Side", "Dampen Presence", "Dark Retaliation", "Dark Side Bane", "Gradual Resistance", "Master of the Great Hunt", "Persistent Haze", "Prime Targets", "Reap Retribution", "Sense Primal Force", "Sentinel Strike", "Sentinel's Gambit", "Sentinel's Observation", "Steel Resolve", "Unseen Eyes", "Force Track", "Intuit Danger", "Sentinel's Insight", "Dark Deception", "Improved Sentinel Strike", "Improved Sentinel's Gambit", "Rebuke the Dark", "Taint of the Dark Side", "Force Warning", "Improved Quick Draw (Lightsabers)", "Sheltering Stance", "Vigilance", "Watchman's Advance", "Combat Trance", "Improvised Weapon Mastery", "Twin Weapon Style", "Twin Weapon Mastery", "Shoto Pin", "Attune Armor", "Force Cloak", "Force Cloak Mastery", "Linked Defense", "Conceal Force Use", "Force Direction", "Force Momentum", "Past Visions", "Empower Siang Lance", "Shield Gauntlet Defense", "Shield Gauntlet Deflect", "Shield Gauntlet Redirect", "Siang Lance Mastery", "Armored Augmentation I", "Armored Augmentation II", "Armor Mastery (Knight's Armor Talent Tree)", "Cortosis Defense", "Cortosis Retaliation", "Knight's Morale", "Oath of Duty", "Praetoria Ishu", "Praetoria Vonil", "Strength of the Empire", "Akk Dog Master", "Akk Dog Trainer's Actions", "Akk Dog Attack Training", "Protective Reaction", "Lor Pelek", "Vibroshield Master", "Dark Side Manipulation", "Krath Illusions", "Krath Intuition", "Krath Surge", "Corellian Security Force", "Journeyman Protector", "Sector Ranger", "Born Leader", "Coordinate", "Distant Command", "Fearless Leader", "Rally", "Trust", "Commanding Presence (Leadership Talent Tree)", "Coordinated Leadership", "Reactionary Attack", "Tactical Savvy", "Unwavering Ally", "At Peace", "Attuned", "Focused Attack", "Surge of Light", "Resist Aging", "Block", "Deflect", "Lightsaber Defense", "Weapon Specialization (Lightsabers)", "Lightsaber Throw", "Redirect Shot", "Cortosis Gauntlet Block", "Precise Redirect", "Precision", "Riposte", "Shoto Focus", "Ataru", "Djem So", "Jar'Kai", "Juyo", "Makashi", "Niman", "Shien", "Shii-Cho", "Sokan", "Soresu", "Trakata", "Vaapad", "Dun M�ch", "Maho-Kai", "Tripzest", "Connections", "Educated", "Spontaneous Skill", "Wealth", "Engineer", "Influential Friends", "Powerful Friends", "Cross-Training", "Gifted Entertainer", "Favors", "Inspire Loyalty", "Undying Loyalty", "Punishing Protection", "Protector Actions", "Field Detection", "Improved Force Sight", "Luka Sene Master", "Quickseeing", "Malkite Techniques", "Modify Poison", "Numbing Poison", "Undetectable Poison", "Vicious Poison", "Armored Mandalorian", "Mandalorian Advance", "Mandalorian Ferocity", "Mandalorian Glory", "Echani Expertise", "Hijkata Expertise", "K'tara Expertise", "K'thri Expertise", "Stava Expertise", "Tae-Jitsu Expertise", "Wrruushi Expertise", "Verdanaian Expertise", "Advanced Planning", "Blend In (Master of Intrigue Talent Tree)", "Done It All", "Get Into Position (Master of Intrigue Talent Tree)", "Master Manipulator (Master of Intrigue Talent Tree)", "Retaliation", "Ignore Damage Reduction", "Ter�s K�si Basics", "Ter�s K�si Mastery", "Unarmed Counterstrike", "Unarmed Parry", "Amphistaff Block", "Amphistaff Riposte", "Spearing Accuracy", "Spiral Shower", "Venom Rake", "Piercing Hit", "Quicktrap", "Speedclimber", "Surprisingly Quick", "Tripwire", "Attract Minion", "Impel Ally I", "Impel Ally II", "Attract Superior Minion", "Bodyguard I", "Bodyguard II", "Bodyguard III", "Contingency Plan", "Impel Ally III", "Inspire Wrath", "Master's Orders", "Shelter", "Tactical Superiority", "Tactical Withdraw", "Urgency", "Wealth of Allies", "Body Control", "Physical Surge", "Soft to Solid", "Wan-Shen Defense", "Wan-Shen Kata", "Wan-Shen Mastery", "Engineering Savant", "Enhance Implant", "Identify Droid", "Modify Prosthetic", "Patch Job", "Patient Builder", "Skilled Mechanic", "Technological Master", "Advantageous Strike", "Dirty Tricks", "Dual Weapon Flourish I", "Dual Weapon Flourish II", "Master of Elegance", "Multiattack Proficiency (Advanced Melee Weapons) (Melee Duelist Talent Tree)", "Out of Nowhere", "Single Weapon Flourish I", "Single Weapon Flourish II", "Accurate Blow", "Close-Quarters Fighter", "Ignore Armor", "Improved Stunning Strike", "Whirling Death", "Commanding Presence (Mercenary Talent Tree)", "Dirty Fighting", "Feared Warrior", "Focused Warrior", "Ruthless (Mercenary Talent Tree)", "Combined Fire (Mercenary Talent Tree)", "Mercenary's Determination", "Mercenary's Grit", "Mercenary's Teamwork", "Voices", "Midi-chlorian Challenge", "Midi-chlorian Control", "Stop Messing With Me!", "Midi-chlorian Mastery", "Breach Cover", "Breaching Explosive", "Droid Expert", "Prepared Explosive", "Problem Solver", "Quick Modifications", "Repairs on the Fly", "Sabotage Device", "Tech Savant", "Vehicular Boost", "Assault Tactics", "Deployment Tactics", "Field Tactics", "One for the Team", "Outmaneuver", "Shift Defense I", "Shift Defense II", "Shift Defense III", "Tactical Edge", "Commander's Prerogative", "Exploit Weakness", "Grand Leader", "Irregular Tactics", "Lead by Example (Military Tactics Talent Tree)", "Turn the Tide", "Uncanny Defense", "Dastardly Strike", "Disruptive", "Skirmisher", "Sneak Attack", "Walk the Line", "Backstabber", "Befuddle", "Cunning Strategist", "Hesitate", "Improved Skirmisher", "Improved Sneak Attack", "Seducer", "Seize Object", "Sow Confusion", "Stymie", "Sudden Strike", "Weakening Strike", "Opportunist", "Quick Strike (Misfortune Talent Tree)", "Everyone Has a Face", "Mistryl Weapon Training", "Mistryl Unarmed Combat", "Out of the Shadows", "Battle Mount", "Expert Rider", "Terrain Guidance", "Mechanized Rider", "Armored Morgukai", "Cortosis Staff Block", "Morgukai Resolve", "Multiattack Proficiency (Cortosis Staff)", "Channel Vitality", "Closed Mind", "Esoteric Technique", "Mystic Mastery", "Regimen Mastery", "Thunderous Bellow", "Beast Speech", "Commune with Nature", "Constriction", "Combined Fire (Naval Officer Talent Tree)", "Fleet Deployment", "Fleet Tactics", "It's a Trap!", "Legendary Commander", "Advantageous Opening", "Retribution", "Slip By (Opportunist Talent Tree)", "Thrive on Chaos", "Vindication", "Deception Awareness", "Greater Weapon Focus (Fira)", "Progenitor's Call", "Waveform", "Fira Mastery", "Confounding Attack", "Double Up", "Find an Opening", "Opportunistic Defense", "Preternatural Senses", "Seize the Moment (Outlaw Talent Tree)", "Tangle Up", "Uncanny Instincts", "Fast Repairs", "Hotwire", "Quick Fix", "Personalized Modifications", "Oafish", "Outsider's Eye", "Outsider's Query", "Wary", "Directed Action", "Directed Movement", "Full Control", "Remote Attack", "Bunker Buster", "Defensive Measures", "Enhance Cover", "Escort Fighter", "Launch Point", "Obscuring Defenses", "Relocate", "Safe Passage", "Safe Zone", "Zone of Recuperation", "Bloodthirsty", "Fight to the Death", "Keep Them Reeling (Piracy Talent Tree)", "Raider's Frenzy", "Raider's Surge", "Savage Reputation", "Take Them Alive", "Dash and Blast", "Flanking Fire", "Guaranteed Shot", "Hailfire", "Twin Shot", "Armored Spacer", "Attract Privateer", "Blaster and Blade I", "Blaster and Blade II", "Blaster and Blade III", "Boarder", "Ion Mastery", "Multiattack Proficiency (Advanced Melee Weapons) (Privateer Talent Tree)", "Preserving Shot", "Black Market Buyer", "Excellent Kit", "Just What is Needed", "Only the Finest", "Right Gear for the Job", "Armored Guard", "Bodyguard's Sacrifice", "Guard's Endurance", "Lifesaver", "Out of Harm's Way (Protection Talent Tree)", "Roll With It", "Take the Hit", "Ward", "Cast Suspicion", "Distress to Discord", "Friend or Foe", "Seize the Moment (Provocateur Talent Tree)", "Stolen Advantage", "True Betrayal", "Recruit Enemy", "Bolstered Numbers", "Noble Sacrifice", "Stay in the Fight (Rebel Recruiter Talent Tree)", "Team Recruiting", "Find Openings", "Hit the Deck", "Lure Closer", "Risk for Reward", "Trick Step", "Reconnaissance Team Leader", "Close-Combat Assault", "Get Into Position (Reconnaissance Talent Tree)", "Reconnaissance Actions", "Ambush (Republic Commando Talent Tree)", "Higher Yield", "Rapid Reload", "Shoulder to Shoulder", "Strength in Numbers", "Weapon Shift", "Bomb Thrower", "For the Cause", "Make an Example", "Revolutionary Rhetoric", "Jet Pack Training", "Burning Assault", "Improved Trajectory", "Jet Pack Withdraw", "Aerial Maneuvers", "Cheap Shot", "No Escape", "Opportunistic Strike", "Slippery Strike", "Strike and Run", "Device Jammer", "Droid Jammer", "Extreme Explosion", "Mine Mastery", "Shaped Explosion", "Skilled Demolitionist", "Bioengineering", "Binary Mindset", "Determine Weakness", "Focused Research", "Identify Creature", "Poisoncraft", "Smelling Salts", "Force Perception", "Force Pilot", "Foresight", "Gauge Force Potential", "Visions", "Feel the Force", "Force Reflexes", "Heightened Awareness", "Instinctive Navigation", "Motion of the Future", "Psychometry", "Shift Sense", "Force Meld (Sense Talent Tree)", "Instinctive Astrogation", "Machine Empathy", "Machine Meld", "Reactive Precognition", "Seyugi Cyclone", "Mobile Whirlwind", "Repelling Whirlwind", "Sudden Storm", "Tempest Tossed", "Combustion", "Earth Buckle", "Fluidity", "Thunderclap", "Wind Vortex", "Biotech Mastery", "Expedient Mending", "Expert Shaper", "Master Mender", "Skilled Implanter", "Precision Shot", "Bullseye", "Draw a Bead", "Pinning Shot", "Harrying Shot", "Defensive Jab", "Nimble Dodge", "Retaliation Jab", "Stinging Jab", "Stunning Shockboxer", "Cause Mutation", "Rapid Alchemy", "Sith Alchemy (Sith Alchemy Talent Tree)", "Sith Alchemy Specialist", "Desperate Measures", "Focus Terror", "Incite Rage", "Power of Hatred", "Dark Healing", "Dark Scourge", "Dark Side Adept", "Dark Side Master", "Force Deception", "Improved Dark Healing", "Wicked Strike", "Affliction", "Dark Healing Field", "Drain Force", "Sith Alchemy (Sith Talent Tree)", "Stolen Form", "Force Crush", "Vengeful Spirits", "Guaranteed Boon", "Leading Skill", "Learn from Mistakes", "Try Your Luck", "Gimmick", "Master Slicer", "Trace", "Electronic Forgery", "Electronic Sabotage", "Security Slicer", "Virus", "Crash and Burn", "Ghost in the Machine", "Light Side Slicer", "Art of Concealment", "Fast Talker", "Hidden Weapons", "Illicit Dealings", "Surprise Strike", "Adept Spellcaster (Sorcerer of Tund Talent Tree)", "Dark Lore", "Delusion", "Illusionary Disguise", "Understanding the Force", "Unity of the Force", "Hyperdriven", "Spacehound", "Starship Raider", "Stellar Warrior", "Cramped Quarters Fighting", "Deep Space Raider", "Make a Break for It", "Computer Language", "Computer Master", "Enhanced Manipulation", "Hotwired Processor", "Power Surge", "Skill Conversion", "Power Boost", "Blend In (Spy Talent Tree)", "Incognito", "Improved Surveillance", "Intimate Knowledge", "Surveillance", "Traceless Tampering", "Commanding Officer", "Coordinated Tactics", "Fire at Will", "Squad Actions", "Fall Back", "Form Up", "Full Advance", "Hold Steady", "Search and Destroy", "Begin Attack Run", "Regroup", "Squadron Maneuvers", "Squadron Tactics", "Force Commander", "Diverse Squadron", "Melded Squadron", "Assured Skill", "Critical Skill Success", "Exceptional Skill", "Reliable Boon", "Skill Boon", "Skill Confidence", "Skillful Recovery", "Advanced Intel", "Hidden Eyes", "Hunt the Hunter", "Seek and Destroy", "Spotter", "Evasion", "Extreme Effort", "Sprint", "Surefooted", "Adrenaline Surge", "Mind Probe", "Perfect Telepathy", "Psychic Citadel", "Psychic Defenses", "Telepathic Intruder", "Back on their Feet", "Capable Assistant", "Makeshift Treatment", "Medical Specialization", "Reliable Treatment", "Steady Hands", "Cunning Distraction", "Damaging Deception", "Distracting Shout", "Improved Soft Cover", "Innocuous", "Treacherous", "Comrades in Arms", "Focused Targeting", "Phalanx", "Stick Together", "Watch Your Back", "Blaster Turret I", "Blaster Turret II", "Blaster Turret III", "Ion Turret", "Stun Turret", "Turret Self-Destruct", "Cycle of Harmony", "Force Stabilize", "Repel Discord", "Stifle Conflict", "Tyia Adept", "Flurry of Blows", "Hardened Strike", "Punishing Strike", "Aggressive Surge", "Blast Back", "Fade Away", "Second Strike", "Swerve", "Enhanced Vision", "Impenetrable Cover", "Invisible Attacker", "Mark the Target", "Maximize Cover", "Shellshock", "Soften the Target", "Triangulate", "Adapt and Survive", "Defensive Protection", "Quick on Your Feet", "Ready and Willing", "Unbalancing Adaptation", "Battlefield Remedy", "Grizzled Warrior", "Reckless", "Seen It All", "Tested in Battle", "Brutal Unarmed Strike", "Martial Resurgence", "Rebound Leap", "Simultaneous Strike", "Telekinetic Strike", "Telekinetic Throw", "Champion", "Quick Study", "Simple Opportunity", "Warrior's Awareness", "Warrior's Determination", "Controlled Burst", "Exotic Weapon Mastery", "Greater Devastating Attack", "Greater Penetrating Attack", "Greater Weapon Focus", "Greater Weapon Specialization", "Multiattack Proficiency (Heavy Weapons)", "Multiattack Proficiency (Rifles) (Weapon Master Talent Tree)", "Extended Threat", "Ferocious Assault", "Multiattack Proficiency (Simple Weapons)", "Two-For-One Throw", "Heavy Gunner", "Twin Attack", "Devastating Attack", "Penetrating Attack", "Weapon Specialization", "Autofire Assault", "Crushing Assault", "Disarming Attack", "Impaling Assault", "Improved Suppression Fire", "Stinging Assault", "Force Immersion", "Immerse Another", "Ride the Current", "Surrender to the Current", "White Current Adept", "Concentrate All Fire", "Escort Pilot", "Lose Pursuit", "Run Interference", "Wingman Retribution", "Biotech Adept", "Bugbite", "Curved Throw", "Surprising Weapons", "Veiled Biotech", "Battlefield Sacrifice", "Built to Suffer", "Crusader's Fury", "Embrace the Pain", "Glorious Death", "Hail of Bugs", "Path of Humility", "Pray to the Pardoner", "Priest's Expertise", "Ritual Expertise", "Trickster's Disciple", "Vua'sa Expertise", "Yammka's Devotion", "Discblade Arc", "Distant Discblade Throw", "Recall Discblade", "Telekinetic Vigilance", "Weapon Specialization (Discblade)", "Discblade Mastery");

    private static final List<String> POWERS = List.of("Assured Strike", "Ballistakinesis", "Barrier of Blades", "Battle Strike", "Blind", "Circle of Shelter", "Cloak", "Combustion", "Concentration", "Conduction", "Conjure Doubt", "Contentious Opportunity", "Convection", "Corruption", "Crucitorn", "Cryokinesis", "Dark Rage", "Dark Transfer", "Deadly Sight", "Deflecting Slash", "Destruction", "Detonate", "Disarming Slash", "Drain Energy", "Draw Closer", "Energy Resistance", "Enlighten", "Falling Avalanche", "Falling Leaf Strike", "Farseeing", "Fear", "Flowing Water Cut", "Fluid Riposte", "Fold Space", "Force Blast", "Force Disarm", "Force Grip", "Force Light", "Force Lightning", "Force Powers", "Force Projection", "Force Scream", "Force Shield", "Force Slam", "Force Stasis", "Force Storm", "Force Stun", "Force Thrust", "Force Track", "Force Whirlwind", "Gaze of Darkness", "Glowball", "Hatred", "Hawk-Bat Swoop", "High Ground Defense", "Inertia", "Inflict Pain", "Inspire", "Instill Turmoil", "Intercept", "Ionize", "Kinetic Combat", "Levitate", "Lightning Burst", "Makashi Riposte", "Malacia", "Memory Walk", "Mind Shard", "Mind Trick", "Morichro", "Move Object", "Negate Energy", "Obscure", "Pass the Blade", "Phase", "Plant Surge", "Prescience", "Pushing Slash", "Rearrangement", "Rebuke", "Rend", "Repulse", "Resist Force", "Rising Whirlwind", "Saber Swarm", "Sarlacc Sweep", "Sever Force", "Shadow Fog", "Shatterpoint", "Shien Deflection", "Siphon Life", "Sith Curse", "Sith Word", "Slow", "Stagger", "Surge", "Swift Flank", "Technometry", "Tempered Aggression", "Thought Bomb", "Twin Strike", "Unbalancing Block", "Unhindered Charge", "Valor", "Vital Transfer", "Vornskr's Ferocity", "Wound");
    private static final List<String> FEATS = List.of("A Few Maneuvers", "Abject Cowardice", "Accelerated Strike", "Acrobatic Ally", "Acrobatic Dodge", "Acrobatic Strike", "Adaptable Talent", "Adept Networkers", "Advanced Education", "Advantageous Attack", "Advantageous Cover", "Agile Riposte", "Aiming Accuracy", "Aiwha Rider", "Alertness", "Ample Foraging", "Analytical Detachment", "Angled Throw", "Annealing Rage", "Anointed Hunter", "Aquatic Specialists", "Armor Proficiency (Heavy)", "Armor Proficiency (Light)", "Armor Proficiency (Medium)", "Artillery Shot", "Ascension Specialists", "Assured Attack", "Attack Combo (Fire and Strike)", "Attack Combo (Melee)", "Attack Combo (Ranged)", "Aura of Oppression", "Autofire Assault", "Autofire Sweep", "Bad Feeling", "Banter", "Bantha Herder", "Bantha Rush", "Baragwin Connections", "Battering Attack", "Battle Anthem", "Beaked Subspecies", "Binary Mind", "Biologist Field Team", "Biotech Designer", "Biotech Specialist", "Biotech Surgery", "Bite Attack", "Blaster Barrage", "Blaster Geometry", "Blessing of Uuru", "Bloodthirsty (Feat)", "Bone Crusher", "Booming Voice", "Botanist", "Bothan Will", "Bowcaster Marksman", "Brachiated Movement", "Brilliant Defense", "Brink of Death", "Brutish", "Bureaucratic Specialist", "Burrowing Flank", "Burrowing Tusks", "Burst Fire", "Burst of Speed", "Canine Senses", "Careful Shot", "Carouser", "Channel Rage", "Charging Fire", "Clawed Subspecies", "Cleave", "Close Combat Escape", "Cold Resistance", "Collateral Damage", "Colonies", "Combat Reflexes", "Combat Trickery", "Committed", "Composer", "Conditioning", "Confident", "Confident Success", "Controlled Rage", "Coordinated Attack", "Coordinated Barrage", "Core Worlds", "Cornered", "Covert Operatives", "Cowardly", "Critical Strike", "Crocodilian Bite", "Crossfire", "Crowd Fighting", "Crush", "Cunning Attack", "Cut the Red Tape", "Cybernetic Surgery", "Damage Conversion", "Dark Inspiration", "Darkness Dweller", "Dashade Connections", "Dashade Heritage", "Deadeye", "Deadly Sniper", "Deceitful", "Deceptive Drop", "Deep Core", "Deep Sight", "Deft Charge", "Demoralizing Strike", "Desert Native", "Desperate Gambit", "Destructive Force", "Devastating Bellow", "Dexterous Feint", "Disabler", "Disarming Charm", "Disarming Scream", "Disavowed", "Distracting Droid", "Disturbing Presence", "Dive for Cover", "Diving Attack", "Dodge", "Dominating Intelligence", "Double Attack", "Drag Away", "Dreadful Countenance", "Dreadful Rage", "Droid Focus", "Droid Hunter", "Droid Shield Mastery", "Droidcraft", "Dual Weapon Defense", "Dual Weapon Mastery I", "Dual Weapon Mastery II", "Dual Weapon Mastery III", "Dual Weapon Strike", "Duck and Cover", "Duplicitous Team", "Dust Farmer", "Duty Bound", "Easily Repaired", "Echani Training", "Elder's Knowledge", "Elomin Force Adept", "Empathic Inspiration", "Erratic Target", "Exceptional Hearing", "Exile's Adaptation", "Exotic Weapon Proficiency", "Expansion Region", "Experienced Medic", "Experienced Negotiators", "Expert Briber", "Expert Droid Repair", "Expert Mime", "Expert Warrior", "Explosives Expert", "Extensive Connections", "Extra Rage", "Extra Second Wind", "Familiar Face", "Far Shot", "Fast Style", "Fast Surge", "Fast Swimmer", "Fatal Hit", "Feat of Strength", "Feline Agility", "Fight Through Pain", "Flash and Clear", "Flawless Mechanic", "Flawless Pilot", "Fleet Tactics", "Fleet-Footed", "Flood of Fire", "Flurry", "Fl�che", "Focused Rage", "Focusing Ritual", "Follow Through", "Follower of Quay", "Force Boon", "Force of Personality", "Force Readiness", "Force Regimen Mastery", "Force Resistance", "Force Sensitivity", "Force Training", "Forceful Blast", "Forceful Recovery", "Forest Stalker", "Fortifying Recovery", "Friends in Low Places", "Frightening Cleave", "Fringe Benefits", "Gaderffii Master", "Galactic Alliance Military Training", "Gand Heritage", "Gearhead", "Givin Designer", "Gossam Commando Training", "Grab Back", "Grand Army of the Republic Training", "Grapple Resistance", "Grazing Shot", "Great Cleave", "Great Pride", "Greater Accuracy", "Greater Awareness", "Greater Confusion", "Greater Deathstrike", "Greedy", "Gungan Weapon Master", "Gunnery Specialist", "Halt", "Hand Gestures", "Hasty Modification", "Heavy Hitter", "Heightened Senses", "Hideous Visage", "Hijkata Training", "Hobbling Strike", "Hold Together", "Human Heritage", "Hunter's Instincts", "Hyperblazer", "I Own Your Name", "Imperceptible Liar", "Imperial Military Training", "Impersonate", "Impetuous Move", "Implant Training", "Imposing Glare", "Improved Bantha Rush", "Improved Charge", "Improved Damage Threshold", "Improved Defenses", "Improved Disarm", "Improved Diving Attack", "Improved Grab", "Improved Natural Healing", "Improved Natural Telepathy", "Improved Opportunistic Trickery", "Improved Rapid Strike", "Improved Sleight of Hand", "Improvised Tools", "Improvised Weapon Mastery", "Impulsive Flight", "In Tune with the Force", "Inborn Resilience", "Increased Agility", "Increased Resistance", "Indomitable Personality", "Informer", "Inner Rim", "Insectoid Animosity", "Insightful Diplomat", "Instinctive Acrobat", "Instinctive Attack", "Instinctive Defense", "Instinctive Diver", "Instinctive Flexibility", "Instinctive Perception", "Instinctive Tinkerer", "Intimidator", "Ion Shielding", "Irrefutable Logic", "Jack of All Trades", "Jango Jumper", "Jedi Familiarity", "Jedi Heritage", "Jee-dai Heretic", "Justice Seeker", "K'tara Training", "K'thri Training", "Kaminoan Grace", "Keen Hearing", "Keen Scent", "Keeping a Secret", "Kilmaulsi Heritage", "Knife Trick", "Knock Heads", "Lanvarok Master", "Lapti Nek", "Larcenous Scavenging", "Lasting Influence", "Leader of Droids", "Learned", "Lightning Draw", "Linguist", "Logic Upgrade: Cross-Platform", "Logic Upgrade: Pyrowall", "Logic Upgrade: Self-Defense", "Logic Upgrade: Skill Swap", "Logic Upgrade: Tactician", "Long Haft Strike", "Mandalorian Training", "Maniacal Charge", "Marksman", "Martial Arts I", "Martial Arts II", "Martial Arts III", "Massan Archaeology", "Master of Disguise", "Master Terraformer", "Master Tracker", "Mathematical Mind", "Maze Navigator", "Meat Shield", "Mechanical Martial Arts", "Medical Expertise", "Medical Team", "Medium Style", "Melee Defense", "Metamorph", "Metamorph II", "Metamorph III", "Methodical Technician", "Micro Vision", "Mid Rim", "Mighty Swing", "Mighty Throw", "Mind of Reason", "Mission Specialist", "Mobility", "Momentum Strike", "Mon Calamari Shipwright", "Mounted Combat", "Mounted Defense", "Mounted Regiment", "Moving Target", "Multi-Grab", "Multi-Targeting", "Musical Genius", "Musician", "Natural Leader", "Natural Storyteller", "Nature Specialist", "Navicomputer Brain", "Networking Contacts", "Never Surrender", "New Republic Military Training", "Nikto Survival", "Nimble Hands and Feet", "Nimble Team", "Node", "Oathbound", "Officer Candidacy Training", "One with the Force", "Opportunistic Retreat", "Opportunistic Shooter", "Opportunistic Trickery", "Outer Rim", "Overlooked", "Overwhelming Attack", "Pacifist", "Pall of the Dark Side", "Partisan Upbringing", "Peace Brigade Commander", "Perfect Intuition", "Perfect Swimmer", "Perseverance", "Pheromone Familiarity", "Pin", "Pincer", "Pinpoint Accuracy", "Pirate Heritage", "Pistoleer", "Pitiless Warrior", "Planetary Peacemaker", "Point-Blank Shot", "Poison Resistance", "Power Attack", "Power Blast", "Powerful Charge", "Powerful Faith", "Powerful Rage", "Precise Shot", "Predictive Defense", "Prehensile Trunks", "Prime Shot", "Primitive Warrior", "Punishment", "Quick Comeback", "Quick Draw", "Quick Read", "Quick Skill", "Rancor Crush", "Rapid Assault", "Rapid Reaction", "Rapid Shot", "Rapid Strike", "Rapid Takedown", "Rapport", "Read the Winds", "Reading the Swarm", "Rebel Military Training", "Recall", "Recovering Surge", "Recurring Success", "Reesarian Bond", "Reesarian Cooperation", "Regeneration", "Regenerative Healing", "Rejuvenating Rest", "Relentless Attack", "Republic Military Training", "Resilient Strength", "Resolute Stance", "Resurgence", "Resurgent Vitality", "Return Fire", "Returning Bug", "Riflemaster", "Risk Taker", "Ritual Mastery", "Ritualistic Tattoos", "Roper", "Running Assault", "Running Attack", "Ryn Network", "Sabacc Face", "Sadistic Strike", "Salvage Expert", "Savage Attack", "Scavenger", "Scion of Dorin", "Sense Force Alignment", "Sensor Link", "Sentient Tech Affinity", "Separatist Military Training", "Shadowbox", "Shady Contacts", "Shake It Off", "Sharp Senses", "Shield Surge", "Shrewd Bargainer", "Signature Device", "Silver Tongue", "Sith Heritage", "Sith Military Training", "Skill Challenge: Catastrophic Avoidance", "Skill Challenge: Last Resort", "Skill Challenge: Recovery", "Skill Focus", "Skill Mastery", "Skill Training", "Slammer", "Slicer Team", "Slippery Maneuver", "Sniper", "Sniper Shot", "Solo Flourish", "Spacer's Surge", "Sport Hunter", "Spray Shot", "Staggering Attack", "Staggering Attack (GaW)", "Stand Tall", "Starship Designer", "Starship Tactics", "Stava Training", "Stay Up", "Steadying Position", "Stoicism", "Strafe", "Strong Bellow", "Strong in the Force", "Strong Style", "Studio Musician", "Subterranean", "Superior Shaping", "Superior Tech", "Suppression Fire", "Sure Climber", "Surgical Expertise", "Survivor of Ryloth", "Swarm", "Swift Claws", "Tactical Advantage", "Tactical Genius", "Tae-Jitsu Training", "Tail Technique", "Tail Trick", "Tal-Gun", "Talented", "Tamer", "Targeted Area", "Targeted Research", "Taung Heritage", "Tech Specialist", "Technical Experts", "Ter�s K�si Training", "Thick Skin", "Thisspiasian Trance", "Throw", "Tireless Pursuer", "Tireless Squad", "Tonal Qualities", "Tool Frenzy", "Tough Hide", "Toughness", "Trample", "Treacherous", "Tree Climber", "Trench Warrior", "Trip", "Triple Attack", "Triple Crit", "Trodatome Repairs", "Tumble Defense", "Turn and Burn", "Tusken Heritage", "Unassuming Presence", "Underestimated", "Unhindered Approach", "Unified Squadron", "Unknown Regions", "Unleashed", "Unreliable Repairs", "Unstoppable Combatant", "Unstoppable Force", "Unswerving Resolve", "Unwavering Devotion", "Unwavering Focus", "Unwavering Resolve", "Vehicle Drag", "Vehicle Systems Expertise", "Vehicular Combat", "Vehicular Surge", "Veknoid Brew", "Verdanaian Training", "Verpine Tech", "Veteran Spacer", "Vigilant Squad", "Vitality Surge", "Vong's Faith", "Vua'sa Training", "Warrior Heritage", "Wary Defender", "Wary Sentries", "Weapon Finesse", "Weapon Focus", "Weapon Proficiency", "Web Brain", "Web-Engineering", "Whiner", "Whirlwind Attack", "Wicked Strike", "Wide Frequency Vision", "Wilderness First Aid", "Wilderness Specialists", "Withdrawal Strike", "Wookiee Grip", "Wroshyr Rage", "Wrruushi Training", "Zero Range");

    private static final List<String> SECRETS = List.of("Corrupted Power", "Debilitating Power", "Devastating Power", "Distant Power", "Enlarged Power", "Extend Power", "Holocron Loremaster", "Linked Power", "Mentor", "Multitarget Power", "Quicken Power", "Remote Power", "Shaped Power", "Unconditional Power", "Pure Power");

    private static final List<String> TECHNIQUES = List.of("Advanced Vital Transfer", "Cure Disease", "Dark Lightning Web", "Detoxify Poison", "Dominate Mind", "Extended Blind", "Extended Force Disarm", "Extended Force Grip", "Extended Force Thrust", "Extended Move Object", "Force Point Recovery", "Force Power Mastery", "Improved Ballistakinesis", "Improved Battle Strike", "Improved Cloak", "Improved Convection", "Improved Crucitorn", "Improved Cryokinesis", "Improved Dark Rage", "Improved Dark Transfer", "Improved Detonate", "Improved Energy Resistance", "Improved Enlighten", "Improved Fold Space", "Improved Force Blast", "Improved Force Disarm", "Improved Force Grip", "Improved Force Light", "Improved Force Lightning", "Improved Force Shield", "Improved Force Slam", "Improved Force Storm", "Improved Force Stun", "Improved Force Thrust", "Improved Force Trance", "Improved Ionize", "Improved Kinetic Combat", "Improved Levitate", "Improved Lightning Burst", "Improved Malacia", "Improved Mind Trick", "Improved Move Light Object", "Improved Obscure", "Improved Phase", "Improved Plant Surge", "Improved Rebuke", "Improved Rend", "Improved Repulse", "Improved Resist Force", "Improved Sense Force", "Improved Sense Surroundings", "Improved Shadow Fog", "Improved Shatterpoint", "Improved Stagger", "Improved Technometry", "Improved Telepathy", "Improved Thought Bomb", "Improved Valor", "Improved Vital Transfer", "Language Absorption", "Mechu-Deru");

    private static final Pattern NEAR_HUMAN_PATTERN = Pattern.compile("Near-Human \\(([\\w\\s]*)\\)");
    private static final Pattern SHARD_DROID_TYPE_PATTERN = Pattern.compile("Shard \\(([\\w\\s]*)\\)");
    private static final Pattern AQUALISH_TYPE_PATTERN = Pattern.compile("Aqualish \\(([\\w\\s]*)\\)");
    private static final Pattern POSSIBLE_MULTIPLIER_TYPE_PATTERN = Pattern.compile("\\(([\\w\\s]*)\\)");
    public static final Pattern VALUE_AND_PAYLOADS = Pattern.compile("(Multiattack Proficiency \\(Advanced Melee Weapons\\)|Multiattack Proficiency \\(Rifles\\)|[\\w\\s'-]+)(?:\\()?([\\s\\w,-;+-]+)?(?:\\))?(?:\\()?([\\s\\w,-;+-]+)?(?:\\))?");
    private static final Table<String, String, String> ITEM_TALENT_MAPPING = HashBasedTable.create();

    private static Map<String, String> namedCrewPosition = new HashMap<>();
    private static Pattern classPattern;
    private static Pattern speciesPattern;
    private static Pattern speciesTypePattern;
    private static Pattern traitTypePattern;
    private static Pattern templateTypePattern;
    private static Pattern ageTypePattern;
    private int i = 0;

    public static void main(String[] args) {

        List<String> nonHeroicUnits = new ArrayList<>(getAlphaLinks("/wiki/Category:Nonheroic_Units?from="));
        List<String> heroicUnits = new ArrayList<>(getAlphaLinks("/wiki/Category:Heroic_Units?from="));

        classPattern = Pattern.compile("(" + String.join("|", CLASSES) + "|" + String.join("|", FOLLOWERS) + ")(?: )?(\\d+)?");
        speciesPattern = Pattern.compile("(" + SPECIES.stream().map(Pattern::quote).collect(Collectors.joining("|")) + "|" + String.join("|", DROID_TYPES) + "|Near-Human)");
        speciesTypePattern = Pattern.compile("(" + SPECIES_TYPE.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")");
        traitTypePattern = Pattern.compile("(" + TRAITS.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")");
        templateTypePattern = Pattern.compile("(" + TEMPLATES.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")");
        ageTypePattern = Pattern.compile("(" + AGES.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")");

        populateTalentMappings();


        List<JSONObject> entries = new UnitExporter().getEntriesFromCategoryPage(nonHeroicUnits);
        entries.addAll(new UnitExporter().getEntriesFromCategoryPage(heroicUnits));


        System.out.println("processed " + entries.size() + " of 647");

        writeToJSON(new File(JSON_OUTPUT), entries, hasArg(args, "d"));
    }


    protected List<JSONy> parseItem(String itemLink, boolean overwrite) {
        if (null == itemLink) {
            return new ArrayList<>();
        }

        Matcher variant = VARIANT_QUALIFIER.matcher(itemLink);
        String variantQualifier = "";
        if (variant.find()) {
            variantQualifier = " " + variant.group(1);
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null) {
            return new ArrayList<>();
        }
        List<String> categories = doc.select("li.category").stream().map(Element::text).collect(Collectors.toList());


        Element title = doc.select("h1.page-header__title").first();

        if (title == null || title.text().trim().equalsIgnoreCase("AAT-2")) {
            return new ArrayList<>();
        }

//        if (!title.text().trim().equalsIgnoreCase("T-65B X-Wing Starfighter")) {
//            return new ArrayList<>();
//        }

//        if (!title.text().trim().equalsIgnoreCase("Protodeka Tank Droid")) {
//            return new ArrayList<>();
//        }
//        if (!title.text().trim().equalsIgnoreCase("A-24 Sleuth Scout Ship")) {
//            return new ArrayList<>();
//        }

        List<Unit> items = new LinkedList<>();

        String itemName = title.text().trim();
        Unit current = Unit.create(itemName + variantQualifier);
        items.add(current);

        String subHeader = "";

        Elements select = doc.select("div.mw-parser-output");
        Element first = select.first();

        boolean isProtocolSection = false;
        for (Element cursor : first.children()) {
            boolean found = false;

            final String text = cursor.text();

            if ("See also: Protocol Format".equals(text)) {
                isProtocolSection = true;
            }

            if (!isProtocolSection) {

                if (sizeClassAndSpeciesLine(current, text, itemName)) {
                    continue;
                }

                if (text.startsWith("Possessions: ")) {
                    handlePossessions(current, text);
                    continue;
                }

                if (text.startsWith("Affiliation")) {
                    List<String> affiliations = List.of(text.split(": ")[1].split(", "));

                    for(String affiliation : affiliations){
                        if(AFFILIATIONS.contains(affiliation)){
                            current.withProvided(ProvidedItem.create(affiliation, ItemType.AFFILIATION));
                        } else {
                            //printUnique("MISSING AFFILIATION: " + affiliation);
                        }
                    }
                    continue;
                }

                if (text.startsWith("Talents:") || text.startsWith("Talent:")) {

                    handleTalents(itemName, current, text, itemLink, cursor);
                    continue;
                }

                if (text.startsWith("Feats:")) {

                    handleFeats(itemName, current, cursor);
                    continue;
                }

                if (text.startsWith("Force Power Suite")) {
                    handleForcePowers(itemName, current, cursor);
                    continue;
                }

                if (text.startsWith("Force Secrets:") || text.startsWith("Force Techniques:")) {
                    handleForceSecretsAndPowers(current, cursor);
                    continue;
                }

                if (text.startsWith("Force Regimens:")) {

                    continue;
                }

//                if (text.startsWith("Force Techniques:")) {
//
//                    continue;
//                }

                if (text.startsWith("Droid Systems:")) {

                    continue;
                }

                if (text.startsWith("Languages:")) {

                    continue;
                }

                if (text.startsWith("Species Traits:")) {

                    continue;
                }

                if (text.startsWith("Fighting Space:")) {

                    continue;
                }

                if (text.startsWith("Availability:")) {

                    continue;
                }

                if (text.startsWith("Dark Side Score:") || text.startsWith("Faith Points:") || text.startsWith("Destiny Points:") || text.startsWith("Force Points:")) {

                    continue;
                }

                if (text.startsWith("Occupation")) {

                    continue;
                }

                if (text.startsWith("Event")) {

                    continue;
                }

                if (text.startsWith("Destiny")) {

                    continue;
                }

                if (text.startsWith("Planet of Origin")) {

                    continue;
                }

                if (text.startsWith("Organization Score")) {

                    continue;
                }

                //possibly a validator
                if (text.startsWith("Abilities:")) {

                    continue;
                }

                if (text.startsWith("Hit Points:")) {

                    continue;
                }

                //validator values

                if (text.startsWith("Ranged:") || text.startsWith("Melee:") || text.startsWith("Attack Options:")) {

                    continue;
                }

                if (text.startsWith("Skills:")) {

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

        }


        return new ArrayList<>(items);
    }

    private void handleForceSecretsAndPowers(Unit current, Element cursor) {
        String awaitingPayload = "";
        ItemType awaitingPayloadType = null;
        for(Element child : cursor.children()){
            String secret = child.text();
            if(secret.startsWith("Force Secrets:") || secret.startsWith("Force Techniques:")){
                continue;
            }

            if(child.tag().equals(Tag.valueOf("i"))){
                ProvidedItem providedItem = ProvidedItem.create(awaitingPayload, awaitingPayloadType);
                providedItem.withPayload(secret);
                current.withProvided(providedItem);
            }
            else if("Force Power Mastery".equals(secret)){
                awaitingPayload = "Force Power Mastery";
                awaitingPayloadType = ItemType.FORCE_TECHNIQUE;
            } else if(SECRETS.contains(secret)){
                current.withProvided(ProvidedItem.create(secret, ItemType.FORCE_SECRET));
            } else if(TECHNIQUES.contains(secret)){
                current.withProvided(ProvidedItem.create(secret, ItemType.FORCE_TECHNIQUE));
            }
            else {
                printUnique(secret);
            }
        }
    }

    private void handleTalents(String itemName, Unit current, String text, String itemLink, Element cursor) {
        for(Element child : cursor.children()) {
            String talent = child.text();

            talent = talent.replace("Talents:", "").trim();


            if("".equals(talent) || "Talents".equals(talent) || child.tag().equals(Tag.valueOf("i"))){
                continue;
            }

            if(TALENTS.contains(talent)){
                current.withProvided(ProvidedItem.create(talent, ItemType.TALENT));
            } else {
                Matcher m = VALUE_AND_PAYLOADS.matcher(talent);
                if(m.find()){
                    String talentName = m.group(1).trim();
                    if(TALENTS.contains(talentName)) {
                        ProvidedItem providedItem = ProvidedItem.create(talentName, ItemType.TALENT);
                        if(List.of("Coordinate", "Sneak Attack", "Demolitionist", "Lightsaber Defense", "Sentinel Strike", "Telekinetic Savant").contains(talentName)){


                            Pattern value = Pattern.compile("\\+?(\\d)(?:d\\d)?");

                            Matcher m1 = value.matcher(m.group(2));

                            if(m1.find()){

                                providedItem.withQuantity(m1.group(1));
                            }else{

                                printUnique("unparseable quantity: " + m.group(2));
                            }

                        } else {
                            providedItem.withPayload(m.group(2));
                        }

                        current.withProvided(providedItem);
                        continue;
                    } if(DUPLICATE_TALENT_NAMES.contains(talentName)) {
                        String resolvedTalent = ITEM_TALENT_MAPPING.get(itemName, talentName);


                        if(resolvedTalent != null && !resolvedTalent.contains("|")){
                            if(TALENTS.contains(resolvedTalent)) {
                                ProvidedItem providedItem = ProvidedItem.create(resolvedTalent, ItemType.TALENT);
                                current.withProvided(providedItem);
                            } else {
                                printUnique(resolvedTalent);
                            }
                        } else {

                            List<String> possibleTalents = TALENTS.stream().filter(t -> t.startsWith(talentName)).collect(Collectors.toList());

                            //printUnique("ITEM_TALENT_MAPPING.put(\"" + itemName + "\", \"" + talent + "\", \"" + String.join("|", possibleTalents) + "\");");
                            printUnique(++i + " https://swse.fandom.com" + itemLink + "      " + talent);
                        }
                        continue;
                    }
                       // printUnique("MISSING TALENT1: " + itemName + " : " + talent + " : " + talentName);

                }
            }
        }
    }

    private void handleForcePowers(String itemName, Unit current, Element cursor) {
        for(Element child : cursor.children()) {
            String forcePower = child.text();

            if(forcePower.startsWith("Force Power Suite")){
                continue;
            }

            if(forcePower.endsWith(",")){
                forcePower = forcePower.substring(0, forcePower.length() -1);
            }


            Matcher m = VALUE_AND_PAYLOADS.matcher(forcePower);
            if(m.find()) {

                String group = m.group(1).trim();
                if (POWERS.contains(group)) {
                    ProvidedItem providedItem = ProvidedItem.create(forcePower, ItemType.FORCE_POWER);
                    String modifier = m.group(2);
                    if(modifier !=null){
                        providedItem.withQuantity(modifier);
                    }

                    current.withProvided(providedItem);
                    continue;
                }
            }

            if(FEATS.contains(forcePower) || TALENTS.contains(forcePower)){
                continue;
            }

            printUnique(itemName + " : " + forcePower);

//            if("Feats:".equals(feat)){
//                continue;
//            }
//
//            if(FEATS.contains(feat)){
//                current.withProvided(ProvidedItem.create(feat, ItemType.FEAT));
//            } else {
//                Matcher m = VALUE_AND_PAYLOADS.matcher(feat);
//                if(m.find()){
//                    String featName = m.group(1).trim();
//                    if(FEATS.contains(featName)) {
//                        ProvidedItem providedItem = ProvidedItem.create(featName, ItemType.FEAT);
//                        providedItem.withPayload(m.group(2));
//                        current.withProvided(providedItem);
//                    } else {
//                        //printUnique(itemName + " : " + feat + " : " + featName);
//                    }
//                } else {
//                    printUnique(itemName + " : " + feat);
//                }
//            }
        }
    }

    private void handleFeats(String itemName, Unit current, Element cursor) {
        for(Element child : cursor.children()) {
            String feat = child.text();
            if("Feats:".equals(feat)){
                continue;
            }

            if(FEATS.contains(feat)){
                current.withProvided(ProvidedItem.create(feat, ItemType.FEAT));
            } else {
                Matcher m = VALUE_AND_PAYLOADS.matcher(feat);
                if(m.find()){
                    String featName = m.group(1).trim();
                    if(FEATS.contains(featName)) {
                        ProvidedItem providedItem = ProvidedItem.create(featName, ItemType.FEAT);
                        providedItem.withPayload(m.group(2));
                        current.withProvided(providedItem);
                    } else {
                        //printUnique(itemName + " : " + feat + " : " + featName);
                    }
                } else {
                    //printUnique(itemName + " : " + feat);
                }
            }
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

    }

    private void handlePossessions(Unit current, String text) {
        List<String> possessions = List.of(text.substring(13).split(",(?![^()]*+\\))"));
        for (String possession : possessions) {
            String trim = possession.trim();

            Matcher m = VALUE_AND_PAYLOADS.matcher(trim);

            if (m.find()) {

                String item = m.group(1).trim();
                String modifier = m.group(2) == null ? "" : m.group(2).trim();
                String nameOverride = null;

                if (modifier.toLowerCase().startsWith("as ")) {
                    nameOverride = item;
                    item = modifier.substring(3);
                }

                if (!ITEMS.contains(item)) {
                    if (item.contains(" with ")) {
                        possessionWith(current, item, nameOverride);
                    } else if (item.toLowerCase().contains("credit")) {
                        Pattern CREDITS = Pattern.compile("(\\d+) (Credits|credits|unmarked Credits)");

                        Matcher m1 = CREDITS.matcher(item);
                        if (m1.find()) {
                            ProvidedItem providedItem = ProvidedItem.create("Credit Chip", ItemType.ITEM);
                            providedItem.withProvided(Attribute.create(AttributeKey.CREDIT, m1.group(1)));
                            current.withProvided(providedItem);
                        } else {
                            try {
                                if (!"".equals(modifier) && Integer.parseInt(modifier) > 0) {
                                    ProvidedItem providedItem = ProvidedItem.create("Credit Chip", ItemType.ITEM);
                                    providedItem.withProvided(Attribute.create(AttributeKey.CREDIT, modifier));
                                    current.withProvided(providedItem);
                                } else if (item.equals("Multiple Credit Chips") || item.equals("Thousands of Credits")) {
                                    ProvidedItem providedItem = ProvidedItem.create("Credit Chip", ItemType.ITEM);
                                    providedItem.withProvided(Attribute.create(AttributeKey.CREDIT, "8d100"));
                                    providedItem.withQuantity("1d4");
                                    current.withProvided(providedItem);
                                } else if (item.equals("Credits") || item.equals("Credits for Strong Drinks")) {
                                    ProvidedItem providedItem = ProvidedItem.create("Credit Chip", ItemType.ITEM);
                                    providedItem.withProvided(Attribute.create(AttributeKey.CREDIT, "8d6"));
                                    current.withProvided(providedItem);
                                } else {
                                    //printUnique("MISSING ITEM: " + item + " : " + modifier + " : " + current.getName());
                                }
                            } catch (NumberFormatException e) {
                                //it wasn't a number
                            }
                        }
                    } else {
                        //printUnique("MISSING ITEM: " +item + " : " + modifier + " : " + current.getName());
                    }

                } else {

                    ProvidedItem providedItem = ProvidedItem.create(item, ItemType.ITEM);
                    if (nameOverride != null) {
                        providedItem.withCustomName(nameOverride);
                    }
                    current.withProvided(providedItem);
                }
            }
        }
    }

    private void possessionWith(Unit current, String item, String nameOverride) {
        String[] split = item.split(" with ");

        if (ITEMS.contains(split[0])) {
            ProvidedItem provided = ProvidedItem.create(split[0], ItemType.ITEM);

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
                        provided.withProvided(Attribute.create(AttributeKey.CREDIT, one));
                    } else {
                        if (!ITEMS.contains(two) && !ITEMS.contains(two.substring(0, two.length() - 1))) {
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
        if (startsWithOneOf(sizes, text) && !text.startsWith("Small Appendages:") && !text.startsWith("Small teams of Soldiers")) {
            List<String> children = List.of(text.split(" "));

            String size = getUnitSize(children);

            if (size != null) {
                current.withSize(size);
            }

            Matcher m = classPattern.matcher(text);

            HashMap<String, String> classes = new HashMap<String, String>();

            while (m.find()) {
                String level = m.group(2);
                if (level == null) {
                    level = "1";
                }
                classes.put(m.group(1), level);
            }

            m = speciesPattern.matcher(text);

            String species = null;
            if (m.find()) {
                species = m.group(1);
            }

            m = NEAR_HUMAN_PATTERN.matcher(text);
            if (m.find()) {
                current.withSpeciesSubType(m.group(1));
            }

            m = SHARD_DROID_TYPE_PATTERN.matcher(text);
            if (m.find()) {
                current.withSpeciesSubType(m.group(1));
            }

            m = AQUALISH_TYPE_PATTERN.matcher(text);
            if (m.find()) {
                current.withSpeciesSubType(m.group(1));
            }

            m = ageTypePattern.matcher(text);
            if (m.find()) {
                current.withAge(m.group(1).replaceAll("-", " "));
            }

            m = speciesTypePattern.matcher(text);
            if (m.find()) {
                current.withProvided(ProvidedItem.create(species, ItemType.SPECIES_TYPE));
            }
            m = traitTypePattern.matcher(text);
            if (m.find()) {
                current.withProvided(ProvidedItem.create(species, ItemType.TRAIT));
            }
            m = templateTypePattern.matcher(text);
            if (m.find()) {
                current.withProvided(ProvidedItem.create(species, ItemType.TEMPLATE));
            }

            if (species == null && classes.get("Beast") == null) {
                species = "Human";
            }

            if (species != null) {
                current.withProvided(ProvidedItem.create(species, ItemType.SPECIES));
            }

            if (classes.size() != 0) {
                for (Map.Entry<String, String> entry : classes.entrySet()) {
                    int count = Integer.parseInt(entry.getValue());
                    for (int i = 0; i < count; i++) {
                        current.withProvided(ProvidedItem.create(entry.getKey(), ItemType.CLASS));
                    }
                }
            }
            return true;
        }
        return false;
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
