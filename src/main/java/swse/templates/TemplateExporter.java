package swse.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.BaseExporter;

public class TemplateExporter extends BaseExporter
{
    public static final String OUTPUT = "G:\\FoundryVTT\\Data\\templates.csv";
    public static final String JSON_OUTPUT = "G:\\FoundryVTT\\Data\\systems\\swse\\raw_export\\templates.json";
    private static int num = 0;
    private static Set<String> allPrerequisites = new HashSet<String>();

    public static void main(String[] args)
    {
        List<String> templateLinks = new ArrayList<String>();
        templateLinks.add("/wiki/Droid_Templates");
        templateLinks.add("/wiki/Weapon_Templates");
        templateLinks.add("/wiki/Armor_Templates");
        templateLinks.add("/wiki/Vehicle_Templates");
        templateLinks.add("/wiki/General_Templates");

        List<JSONObject> entries = new ArrayList<>();
        for (String templateLink : templateLinks)
        {
            entries.addAll(readItemMenuPage(templateLink, false));
        }

        ArrayList<String> strings = Lists.newArrayList(allPrerequisites);
        strings.sort(String::compareToIgnoreCase);
        for (String field : strings)
        {
            System.out.println(field);
        }

        writeToJSON(new File(JSON_OUTPUT), entries,  hasArg(args, "d"));
        //writeToCSV(new File(OUTPUT), entries);
    }


    private static Collection<? extends JSONObject> readItemMenuPage(String itemPageLink, boolean overwrite)
    {
        Document doc = getDoc(itemPageLink, overwrite);
        if (doc == null)
        {
            return new ArrayList<>();
        }
        Element body = doc.body();

        Elements tables = body.select("table.wikitable");

        List<String> hrefs = new ArrayList<>();
        tables.forEach(table ->
        {
            Elements rows = table.getElementsByTag("tr");
            rows.forEach(row ->
            {
                Elements tds = row.getElementsByTag("td");
                Element first = tds.first();
                if (first != null)
                {
                    Element anchor = first.getElementsByTag("a").first();
                    if (anchor != null)
                    {
                        hrefs.add(anchor.attr("href"));
                    }
                }
            });
        });


        return hrefs.stream().flatMap(itemLink -> parseItem(itemLink, overwrite).stream()).collect(Collectors.toList());

    }

    private static List<JSONObject> parseItem(String itemLink, boolean overwrite)
    {
        if (null == itemLink)
        {
            return new ArrayList<>();
        }

        Document doc = getDoc(itemLink, overwrite);

        if (doc == null)
        {
            return new ArrayList<>();
        }
        String itemName = getItemName(doc);

        if ("home".equals(itemName))
        {
            return new ArrayList<>();
        }

        Element content = doc.getElementsByClass("mw-parser-output").first();

        content.select("img,figure").remove();

        String cost = getCost(content);

        String templateType = getTemplateType(content);

        Application application = Application.getApplication(content);

        List<JSONObject> templates = new ArrayList<>();
        templates.add(Template.create(itemName).withCost(cost).withTemplateType(templateType).withApplication(application.applicationString()).withDescription(content).withPrefix(getPrefix(itemName)).withSuffix(getSuffix(itemName)).toJSON());
        return templates;
    }

    private static String getSuffix(String itemName)
    {
        return "";
    }

    private static String getPrefix(String itemName)
    {
        if (itemName.contains(" Droid Template"))
        {
            return itemName.replaceAll(" Droid Template", "");
        }
        if (itemName.contains(" General Template"))
        {
            return itemName.replaceAll(" General Template", "");
        }
        if (itemName.contains(" Weapon Template"))
        {
            return itemName.replaceAll(" Weapon Template", "");
        }
        if (itemName.contains(" Armor Template"))
        {
            return itemName.replaceAll(" Armor Template", "");
        }
        return "";
    }

    private static void printFields(Element content)
    {
        for (Element element : content.children())
        {
            if (element.text().contains(":"))
            {
                if (element.text().toLowerCase().startsWith("weapons"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("cost"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("armor"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("template type"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("to create a"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("availability"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("damage threshold"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("energy cell"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("maintenance"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("manufactured by"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("effects"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("applicable to"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("refitting"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("repairs"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("special"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("homebrew reference book") || element.text().toLowerCase().startsWith("reference book"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("tractor beams"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("challenge level"))
                {
                    //System.out.println(element.text());
                } else if (element.text().toLowerCase().startsWith("patchwork"))
                {
                    //System.out.println(element.text());
                } else
                {
                    //allPrerequisites.add(element.text().toLowerCase().split(":")[0]);
                }
            } else
            {
                //System.out.println(element.text());
            }
        }
    }

    private static String getTemplateType(Element content)
    {
        for (Element element : content.children())
        {
            if (element.text().toLowerCase().startsWith("template type"))
            {
                return element.text().substring(14).trim();
            }
        }
        return null;
    }

    private static String getCost(Element content)
    {
        for (Element element : content.children())
        {
            if (element.text().toLowerCase().startsWith("cost"))
            {
                if ("10% or 1,000 credits more (Whichever is higher) than base item".equals(element.text().substring(6)))
                {
                    return "max(1000,@cost*0.1)";
                } else if ("20% or 2,000 credits more (Whichever is higher) than base item".equals(element.text().substring(6)))
                {
                    return "max(2000,@cost*0.2)";
                } else if ("30% or 3,000 credits more (Whichever is higher) than base item".equals(element.text().substring(6)))
                {
                    return "max(3000,@cost*0.3)";
                } else if ("10% more than base item".equals(element.text().substring(6)) || "10% credits more than base item".equals(element.text().substring(6)))
                {
                    return "@cost*0.1";
                } else if ("20% more than base item".equals(element.text().substring(6)) || "20% credits more than base item".equals(element.text().substring(6)))
                {
                    return "@cost*0.2";
                } else if ("30,000 credits more than base item".equals(element.text().substring(6)) || "20% credits more than base item".equals(element.text().substring(6)))
                {
                    return "30000";
                } else if ("Archaic Vehicles generally can be obtained on the Black Market for 50% of their original cost.".equals(element.text().substring(6)) || "20% credits more than base item".equals(element.text().substring(6)))
                {
                    return "-0.5*@Cost";
                } else
                {
                    return "??";
                }
            }
        }
        return null;
    }

    private static class Application
    {
        private final String buildApplicationString;

        public Application(String buildApplicationString)
        {
            this.buildApplicationString = buildApplicationString;
        }

        private static Application getApplication(Element content)
        {
            for (Element element : content.children())
            {
                if (element.text().toLowerCase().startsWith("applicable to"))
                {
                    String payload = element.text().substring(15);
                    return new Application(buildApplicationString(payload));
                }
            }
            return new Application("");
        }

        private static String buildApplicationString(String payload)
        {
            if (payload.contains(", "))
            {
                String[] parts = payload.split(", ");
                return Arrays.stream(parts).map(Application::buildApplicationString).collect(Collectors.joining(" OR "));
            } else
            {
                String toLowerCase = payload.toLowerCase();
                if (toLowerCase.contains("armor"))
                {
                    return getArmorApplicationString(toLowerCase);
                } else if (toLowerCase.contains("weapon"))
                {
                    return getWeaponApplicationString(toLowerCase);
                }
            }


            return payload;
        }

        private static String getArmorApplicationString(String toLowerCase)
        {
            List<String> armorAttributes = Lists.newArrayList();
            armorAttributes.add("ARMOR");

            if(toLowerCase.contains("fortitude defence")){
                armorAttributes.add("FORTITUDE");
            }
            return "(" + String.join(" AND ", armorAttributes) + ")";
        }

        private static String getWeaponApplicationString(String toLowerCase)
        {
            List<String> weaponAttributes = Lists.newArrayList();
            weaponAttributes.add("WEAPON");

            if ("any advanced melee weapon or simple weapon (melee)".equals(toLowerCase))
            {
                weaponAttributes.add("(SIMPLE OR ADVANCED)");
            } else if(toLowerCase.contains("simple")){
                weaponAttributes.add("SIMPLE");
            }

            if (toLowerCase.contains("antiqued"))
            {
                weaponAttributes.add("ANTIQUE");
            }

            if (toLowerCase.contains("melee"))
            {
                weaponAttributes.add("MELEE");
            }

            if (toLowerCase.contains("ranged"))
            {
                weaponAttributes.add("RANGED");
            }

            if(toLowerCase.contains("non-energy")){
                weaponAttributes.add("!ENERGY");
            }else if(toLowerCase.contains("energy")){
                weaponAttributes.add("ENERGY");
            }

            if (toLowerCase.contains("ion or stun"))
            {
                weaponAttributes.add("(ION OR STUN)");
            } else if (toLowerCase.contains("ion"))
            {
                weaponAttributes.add("ION");
            } else if (toLowerCase.contains("stun"))
            {
                weaponAttributes.add("STUN");
            }
            if (toLowerCase.contains("slashing or piercing"))
            {
                weaponAttributes.add("(SLASH OR PIERCE)");
            } else if(toLowerCase.contains("piercing")){
                weaponAttributes.add("PIERCING");
            }
            return "(" + String.join(" AND ", weaponAttributes) + ")";
        }

        @Override
        public String toString()
        {
            return "Application{" +
                    "buildApplicationString='" + buildApplicationString + '\'' +
                    '}';
        }

        public String applicationString()
        {
            return buildApplicationString;
        }
    }
}
