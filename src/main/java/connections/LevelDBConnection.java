package connections;

import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;
import static swse.common.BaseExporter.SYSTEM_LOCATION;

public class LevelDBConnection {
    public static void main(String[] args) throws Exception {
        Path path = Paths.get(SYSTEM_LOCATION + "/packs/armor");

        Files.walk(path).filter(p -> p.toFile().getName().endsWith("ldb")).forEach(p -> {
            LevelDBConnection.remameToSST(p);
        });

        Options options = new Options();
        options.createIfMissing(false); // Do not create, open existing only
        options.compressionType(CompressionType.NONE);
        //options.
        DB db = factory.open(path.toFile(), options);

        try {
            // Example: read key "mykey"
//            byte[] value = db.get(bytes("mykey"));
//            if (value != null) {
//                System.out.println("Value: " + asString(value));
//            } else {
//                System.out.println("Key not found.");
//            }

            db.iterator().forEachRemaining(entry -> {
                System.out.println(asString(entry.getKey()) + " : " + asString(entry.getValue()));
            });
        } finally {
            db.close();
        }


        Files.walk(path).filter(p -> p.toFile().getName().endsWith("sst")).forEach(p -> {
            LevelDBConnection.remameToLDB(p);
        });
    }

    private static void remameToLDB(Path path) {
        String fileName = path.toFile().getName();
        String newFileName = fileName.replace(".sst", ".ldb");
        try {
            Files.move(path, path.getParent().resolve(newFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void remameToSST(Path path) {
        String fileName = path.toFile().getName();
        String newFileName = fileName.replace(".ldb", ".sst");
        try {
            Files.move(path, path.getParent().resolve(newFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
