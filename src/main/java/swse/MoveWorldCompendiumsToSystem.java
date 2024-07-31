package swse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class MoveWorldCompendiumsToSystem {
    public static void main(String[] args) throws IOException {
        String target = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems/swse/packs";
        String source = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\worlds/test-world/packs";
        String jsonFolder = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\systems/swse/raw_export";
        String itemDB = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\worlds/test-world/data/items";
        String actorDB = "C:\\Users\\lijew\\AppData\\Local\\FoundryVTT\\Data\\worlds/test-world/data/actors.db";


        System.out.println("MOVING");
        for (File file :
                Objects.requireNonNull(new File(source).listFiles())) {
            System.out.println(file.getName());
            //file.moveTo(source);
            Path targetPath = Paths.get(target + "/" + file.getName());
            deleteRecursively(targetPath);
            //Files.delete(targetPath);

            Files.move(Paths.get(source + "/" + file.getName()), targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("DELETING");
        for (File file :
                Objects.requireNonNull(new File(jsonFolder).listFiles())) {

            System.out.println(file.getName());
            Files.delete(Paths.get(jsonFolder + "/" + file.getName()));
        }


        System.out.println(itemDB);
        deleteRecursively(Paths.get(itemDB));
//
//        System.out.println(actorDB);
//        Files.delete(Paths.get(actorDB));
//
//        for (File file :
//                Objects.requireNonNull(new File(target).listFiles())) {
//            System.out.println(file.getName());
//            //file.moveTo(source);
//        }
    }

    private static void deleteRecursively(Path targetPath) throws IOException {
        if(targetPath.toFile().isDirectory()){
            for(File child : Objects.requireNonNull(targetPath.toFile().listFiles())){
                deleteRecursively(child.toPath());
            }
        }
        if(targetPath.toFile().exists()){
            Files.delete(targetPath);
        }

    }
}
