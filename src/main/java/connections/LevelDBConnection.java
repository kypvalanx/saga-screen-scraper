package connections;

import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class LevelDBConnection implements AutoCloseable {
    private final DB db;
    private final Path path;
    private Map<String, byte[]> hashes;
    private Map<String, String> ids;

    public LevelDBConnection(Path path) throws IOException {
        this.path = path;

        // Rename all .ldb files to .sst
        Files.walk(path).filter(p -> p.toFile().getName().endsWith("ldb")).forEach(LevelDBConnection::copyToSST);

        // Open the database
        Options options = new Options();
        options.createIfMissing(false);
        options.compressionType(CompressionType.NONE);
        this.db = factory.open(path.toFile(), options);
    }

    public DB getDb() {
        return db;
    }

    @Override
    public void close() throws IOException {
        try {
            // Close the database connection
            db.close();

            // Rename all .sst files back to .ldb
            Files.walk(path).filter(p -> p.toFile().getName().endsWith("sst")).forEach(LevelDBConnection::resolveLDB);
        } catch (Exception e) {
            throw new IOException("Error closing database connection", e);
        }
    }


    private static void resolveLDB(Path path) {
        String fileName = path.toFile().getName();
        String newFileName = fileName.replace(".sst", ".ldb");
        Path newPath = path.getParent().resolve(newFileName);
        try {
            Files.deleteIfExists(newPath);
            Files.move(path, newPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyToSST(Path path) {
        String fileName = path.toFile().getName();
        String newFileName = fileName.replace(".ldb", ".sst");
        try {
            Path resolve = path.getParent().resolve(newFileName);
            if (Files.exists(resolve)) {
                Files.delete(resolve);
            }

            Files.copy(path, resolve);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, byte[]> getHashes() {

        if(this.hashes != null){
            return this.hashes;
        }

        Map<String, byte[]> hashes = new HashMap<>();

        db.iterator().forEachRemaining(entry -> {
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");

                        System.out.println(asString(entry.getKey()) + " : " + asString(entry.getValue()));

                        hashes.put(asString(entry.getKey()), md.digest(asString(entry.getValue()).getBytes()));
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
        }
        );

        this.hashes = hashes;

        return hashes; //db.;
    }

    public Map<String, String> getIds() {

        if(this.ids != null){
            return this.ids;
        }

        Map<String, String> ids = new HashMap<>();

        db.iterator().forEachRemaining(entry -> {
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");

                        //System.out.println(asString(entry.getKey()) + " : " + asString(entry.getValue()));

                        String json =asString(entry.getValue());
                        String name = null;

                        JsonFactory factory = new JsonFactory();
                        try (JsonParser parser = factory.createParser(json)) {
                            while (parser.nextToken() != null) {
                                if ("name".equals(parser.getCurrentName())) {
                                    parser.nextToken();
                                    name = parser.getValueAsString();
                                    break;
                                }
                            }
                        }

                        ids.put(name, asString(entry.getKey()));
                        //ids.put(asString(entry.getKey()), md.digest(asString(entry.getValue()).getBytes()));
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        this.ids = ids;

        return ids; //db.;
    }

    public void put(String id, JSONObject o) {
        if(id == null){
            id = "newId"; //Figure this out
        } else {
            id = id.trim();

            hashes = getHashes();
            hashes.get(id);

            String value = o.toString(0);
            String old = asString(db.get(id.getBytes()));

            System.out.println(value);
            System.out.println(old);
            if(value.equals(old)){
                return;
            }
            db.put(id.getBytes(), value.getBytes());
        }
    }
}
//!items!fQnjGg4Lb9vjdE2s : {"img":"systems/swse/icon/talent/default.png","effects":[],"system":{"finalName":"","description":"You are skilled at working on and managing an emergency medical team. Allies automatically succeed on<a href=\"https://swse.fandom.com/wiki/Aid_Another\" title=\"Aid Another\">Aid Another</a>attempts when assisting you with<a href=\"https://swse.fandom.com/wiki/Treat_Injury\" title=\"Treat Injury\">Treat Injury</a>checks.","textDescription":"","sourceString":"","attributes":{},"changes":[],"choices":[],"modes":[],"providedItems":[],"payload":"","buildInstructions":[],"prerequisite":null,"categories":[{"value":"Talent Trees"},{"value":"Shaper Talent Trees"},{"value":"Medic Talent Trees"}],"supplier":{},"possibleProviders":["Shaper Talent Trees","Medic Talent Trees"],"isSupplied":false,"talentTree":"Advanced Medicine Talent Tree","talentTreeSource":"","talentTreeUrl":"https://swse.fandom.com/wiki/Advanced_Medicine_Talent_Tree","bonusTalentTree":"","source":"Star Wars Saga Edition Force Unleashed Campaign Guide","cost":"0"},"name":"Emergency Team","flags":{},"type":"talent","folder":null,"_stats":{"compendiumSource":null,"duplicateSource":null,"coreVersion":"13.351","systemId":"swse","systemVersion":"12.3.6","createdTime":1734385301782,"modifiedTime":1734385301782,"lastModifiedBy":"tih8Q1fJUuDFWVCX","exportSource":null},"_id":"fQnjGg4Lb9vjdE2s","sort":0,"ownership":{"default":0,"tih8Q1fJUuDFWVCX":3}}