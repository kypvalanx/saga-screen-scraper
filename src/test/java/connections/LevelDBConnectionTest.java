package connections;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LevelDBConnectionTest {

    @Test
    void getHashes() throws IOException {
        LevelDBConnection connection = new LevelDBConnection(DBPaths.TalentsDB);
        assertNotNull(connection);
        Map<String, byte[]> hashes = connection.getHashes();
        assertNotNull(hashes);
    }
    @Test
    void getIds() throws IOException {
        LevelDBConnection connection = new LevelDBConnection(DBPaths.TalentsDB);
        assertNotNull(connection);
        Map<String, String> hashes = connection.getIds();
        assertNotNull(hashes);
    }
}