package swse.talents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import swse.common.Category;
import swse.common.Copyable;
import swse.common.FoundryItem;
import swse.prerequisite.Prerequisite;
import static swse.util.Util.printUnique;

class Talent extends FoundryItem<Talent> implements Copyable<Talent>
{
    public static final String FORCE_TALENT = "Force Talent Trees";
    private final List<String> talentProviders;
    private String talentTree;
    private String bonusTalentTree;
    private String talentTreeUrl;

    public Talent(String name)
    {
        super(name, "talent");
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

        @Override
        public Talent withProvided(Collection<?> objects)
        {
            super.withProvided(objects);
            for(Object o:objects){
                if(o instanceof Category){

                    if (((Category) o).getValue().equals("Force Talent Trees"))
                    {
                        this.bonusTalentTree = FORCE_TALENT;
                        break;
                    }
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

        public Talent withTalentTreeUrl(String talentTreeUrl)
        {
            this.talentTreeUrl = talentTreeUrl;
            return this;
        }

    public Talent withPossibleProviders(List<String> talentProviders) {
        printUnique(talentProviders);
        this.talentProviders.addAll(talentProviders);
        return this;
    }

    @Override
    public Talent copy() {
        return null;
    }
}
