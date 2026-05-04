package connections;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DBPaths {
    public static final String DB_FOLDER = "/home/andy/foundryuserdata/Data/systems/swse/packs";

    public static String getDBFolderPath() {
        return DB_FOLDER;
    }
    
    //public static Path ItemsDB = Path.of(DB_FOLDER + "/items");
    public static Path TalentsDB = Path.of(DB_FOLDER + "/talents");
    
    
    public static String[] getDBPaths(){
        List<String> dbPaths = new ArrayList<>();

        for (Field field : DBPaths.class.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    java.lang.reflect.Modifier.isPublic(field.getModifiers()) &&
                    field.getType().equals(Path.class) &&
                    field.getName().endsWith("DB")) {
                try {
                    dbPaths.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return dbPaths.toArray(new String[0]);
    }
}
