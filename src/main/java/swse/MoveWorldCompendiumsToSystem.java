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


        for (File file :
                Objects.requireNonNull(new File(source).listFiles())) {
            System.out.println(file.getName());
            //file.moveTo(source);
            Files.move(Paths.get(source + "/" + file.getName()), Paths.get(target + "/" + file.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
//
//        for (File file :
//                Objects.requireNonNull(new File(target).listFiles())) {
//            System.out.println(file.getName());
//            //file.moveTo(source);
//        }
    }
}
