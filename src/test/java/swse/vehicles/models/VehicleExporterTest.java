package swse.vehicles.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleExporterTest {

    @Test
    void nameCleanup() {
       assertEquals("devastating hit", VehicleExporter.nameCleanup("devastating hit []"));

    }
}