package swse.talents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.FoundryItem;
import swse.common.ProvidedItem;
import swse.prerequisite.Prerequisite;

class Talent extends FoundryItem<Talent>
{
    public static final String FORCE_TALENT = "Force Talent Tree";
    private final List<String> talentProviders;
    private String talentTree;
    private String bonusTalentTree;
    private String talentTreeUrl;

    public Talent(String name)
    {
        super(name);
        this.talentProviders = new ArrayList<>();
    }

    @Nonnull
    public JSONObject toJSON()
    {
        JSONObject json = super.toJSON();
        json.put("type", "talent");

        JSONObject data = json.getJSONObject("data");


        data.put("talentTreeUrl", talentTreeUrl);
        data.put("possibleProviders", talentProviders);
        if(bonusTalentTree != null)
        {
            data.put("bonusTalentTree", bonusTalentTree);
        }
        data.put("talentTree", talentTree);

        return json;
    }


        public static Talent create(String name)
        {
            return new Talent(name);
        }

        public Talent withTalentTree(String talentTree)
        {
            if(talentTree.contains("Droid Talent Tree")){
                this.bonusTalentTree = talentTree;
            }
            this.talentTree = talentTree;
            return this;
        }

        public Talent withCategories(Set<ProvidedItem> categories)
        {
            Set<ProvidedItem> filtered = new HashSet<>();
            for(ProvidedItem cat:categories){
                if(cat.getName().equals("Talent Trees")){
                    continue;
                }

                if (cat.getName().equals("Force Talent Trees"))
                {
                    this.bonusTalentTree = FORCE_TALENT;
                    filtered.add(cat);
                    break;
                }
                if(cat.getName().endsWith(" Tree")){
                    //this is the name of the talent tree it's alreadyprovided by the page name.
                    continue;
                    //printUnique("TALENTTREE:"+cat.getTraits());
                }
                if(cat.getName().endsWith(" Trees")){

                    //filtered.add(cat);
                    talentProviders.add(cat.getName());
                    //printUnique("CLASS:"+cat.getTraits());
                } else {

                    //printUnique("??:"+cat.getTraits());
                }
            }
            return this;
        }

        public Talent withForceTradition(String tradition)
        {
            if(tradition!=null && !tradition.isEmpty()){
                prerequisite = Prerequisite.create(tradition);
            }
            return this;
        }

        public Talent withTelentTreeUrl(String talentTreeUrl)
        {
            this.talentTreeUrl = talentTreeUrl;
            return this;
        }

}
