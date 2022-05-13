package swse;

import swse.affiliation.AffiliationExporter;
import swse.background.BackgroundExporter;
import swse.character_class.CharacterClassExporter;
import swse.destiny.DestinyExporter;
import swse.feat.FeatExporter;
import swse.forcePowers.ForcePowersExporter;
import swse.forceRegimens.ForceRegimensExporter;
import swse.forceSecrets.ForceSecretsExporter;
import swse.forceTechniques.TechniquesExporter;
import swse.item.ItemExporter;
import swse.language.LanguageExporter;
import swse.species.SpeciesExporter;
import swse.talents.TalentExporter;
import swse.templates.TemplateExporter;
import swse.traits.TraitExporter;
import swse.vehicles.stock.baseType.VehicleBaseTypeExporter;
import swse.vehicles.systems.VehicleSystemsExporter;

public class RefreshItemCompendiums
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
        VehicleBaseTypeExporter.main(args);
        VehicleSystemsExporter.main(args);
        LanguageExporter.main(args);
        BackgroundExporter.main(args);
        DestinyExporter.main(args);

    }
}
