package swse;

import swse.character_class.CharacterClassExporter;
import swse.feat.FeatExporter;
import swse.forcePowers.ForcePowersExporter;
import swse.forceRegimens.ForceRegimensExporter;
import swse.forceSecrets.ForceSecretsExporter;
import swse.forceTechniques.TechniquesExporter;
import swse.affiliation.AffiliationExporter;
import swse.item.ItemExporter;
import swse.species.SpeciesExporter;
import swse.traits.TraitExporter;
import swse.talents.TalentExporter;
import swse.templates.TemplateExporter;
import swse.vehicles.models.VehicleExporter;
import swse.vehicles.stock.templates.VehicleStockTemplateExporter;
import swse.vehicles.systems.VehicleSystemsExporter;

public class RefreshCompendiums
{
    public static void main(String[] args)
    {
        CharacterClassExporter.main(args);
        FeatExporter.main(args);
        ForcePowersExporter.main(args);
        ForceRegimensExporter.main(args);
        ForceSecretsExporter.main(args);
        TechniquesExporter.main(args);
        AffiliationExporter.main(args);
        ItemExporter.main(args);
        SpeciesExporter.main(args);
        TalentExporter.main(args);
        TemplateExporter.main(args);
        TraitExporter.main(args);
        VehicleStockTemplateExporter.main(args);
        VehicleSystemsExporter.main(args);
        VehicleExporter.main(args);

    }
}
