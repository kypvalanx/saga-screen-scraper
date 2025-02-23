package swse.starshipManeuvers;

import org.junit.jupiter.api.Test;
import swse.common.JSONy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StarshipManeuverExporterTest {

    @Test
    void parseItem() {
        List<JSONy> response = new StarshipManeuverExporter().parseItem("/wiki/Devastating_Hit", true, List.of(), List.of());
    }
}