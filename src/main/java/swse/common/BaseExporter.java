package swse.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class BaseExporter
{
    public static String ROOT = "https://swse.fandom.com";


    protected static void writeToJSON(File jsonOutputFile, List<JSONObject> entries, boolean dryRun)
    {
        if(dryRun){
            return;
        }
        JSONObject data = new JSONObject();
        StringWriter writer = new StringWriter();

        JSONArray jsonArray = new JSONArray();

        jsonArray.putAll(entries);

        data.put("version", Instant.now().toEpochMilli());

        data.put("entries", jsonArray);
        data.write(writer);

        try
        {
            jsonOutputFile.createNewFile();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(jsonOutputFile))
        {
            pw.print(writer);
        } catch (FileNotFoundException e)
        {
            System.err.println("oh fuck");
        }
    }

    protected static boolean hasArg(String[] args, String arg){
        return Arrays.stream(args).anyMatch(arg::equalsIgnoreCase);
    }


    protected static String getItemName(Document doc)
    {
        Elements headingElements = doc.select(".page-header__title");

        if(headingElements.size() ==0 ||headingElements.first() == null){
            System.out.println(doc);
        }

        if (headingElements.size() > 1)
        {
            throw new IllegalStateException("too many headers " + headingElements);
        }
        return headingElements.first().text();
    }


    protected static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }

    protected static Document getDoc(String itemPageLink, boolean overwrite)
    {
        try
        {


        File local = new File(clean(itemPageLink) + ".html");

        if(overwrite && local.exists()){
            local.delete();
        }

        String root = "https://swse.fandom.com";
        if(!local.exists()){
            String[] toks = itemPageLink.split("/");
            String s = "C:";
            for(int i = 0; i< toks.length - 1;i++){
                s = s.concat("/"+toks[i]);
                new File(s).mkdir();
            }
            try
            {
                local.getParentFile().mkdirs();
            local.createNewFile();

                FileUtils.copyURLToFile(
                        new URL(root + itemPageLink),
                        local, 10000
                        ,
                        10000);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return Jsoup.parse(local, "UTF-8", root);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    private static String clean(String itemPageLink)
    {
        return itemPageLink.replace(":", "");
    }

    protected static void drawProgressBar(double v)
    {
        String s = "";
        for(int i = 0; i < 100; i++){
            s=s.concat(i< v ?"#":" ");
        }
        System.out.print("|"+s+"|\r");
    }

    public static String getDescription(Element content){
        if(content == null){
            return "";
        }

        content = content.clone();
        content.select("span.mw-editsection").remove();
        content.select("div.toc").remove();
        content.select("img,figure").remove();

        Elements anchors = content.select("a");
        for(Element anchor : anchors){
            if(!anchor.attr("href").startsWith("http"))
            {
                anchor.attr("href", "https://swse.fandom.com" + anchor.attr("href"));
            }
        }
        for (Element element : content.children()){
            String text = element.text().toLowerCase();
            if(text.contains("weapon type:") || text.contains("size:") || text.contains("cost:") || text.contains("damage:") || text.contains("stun setting:")|| text.contains("type:")|| text.contains("weight:")|| text.contains("rate of fire:")|| text.contains("availability:")){
                element.remove();
            }
        }

        return content.toString();
    }

}
