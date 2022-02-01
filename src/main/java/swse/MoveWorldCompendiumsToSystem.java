package swse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class MoveWorldCompendiumsToSystem {
    public static void main(String[] args) throws IOException {
        String target = "G:/FoundryVTT/Data/systems/swse/packs";
        String source = "G:/FoundryVTT/Data/worlds/89Test/packs";
        String jsonFolder = "G:/FoundryVTT/Data/systems/swse/raw_export";
        String itemDB = "G:/FoundryVTT/Data/worlds/89Test/data/items.db";


        System.out.println("MOVING");
        for (File file :
                Objects.requireNonNull(new File(source).listFiles())) {
            System.out.println(file.getName());
            //file.moveTo(source);
            Files.move(Paths.get(source + "/" + file.getName()), Paths.get(target + "/" + file.getName()), StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("DELETING");
        for (File file :
                Objects.requireNonNull(new File(jsonFolder).listFiles())) {

            System.out.println(file.getName());
            Files.delete(Paths.get(jsonFolder + "/" + file.getName()));
        }


        System.out.println(itemDB);
        Files.delete(Paths.get(itemDB));
//
//        for (File file :
//                Objects.requireNonNull(new File(target).listFiles())) {
//            System.out.println(file.getName());
//            //file.moveTo(source);
//        }
    }
}
