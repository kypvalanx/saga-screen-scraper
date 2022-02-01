package swse.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Template extends FoundryItem<Template> implements Copyable<Template>
{
    private String cost;
    private String templateType;
    private String application;
    private String prefix;
    private String suffix;

    public Template(String name)
    {
        super(name);
    }

    public static Template create(String itemName)
    {
        return new Template(itemName);
    }

    public Template withCost(String cost)
    {
        this.cost = cost;
        return this;
    }

    public Template withTemplateType(String templateType)
    {
        this.templateType = templateType;
        return this;
    }

    public Template withApplication(String application)
    {
        this.application = application;
        return this;
    }

    public Template withDescription(Element content)
    {
        this.description = content.html();
        return this;
    }

    @Nonnull
    public JSONObject toJSON(){
        JSONObject json = super.toJSON();
        json.put("type", "template");

        JSONObject data = json.getJSONObject("data");

        List<JSONObject> attributes = new ArrayList<>();
        attributes.add(createAttribute("templateType", templateType));
        attributes.add(createAttribute("application", application));
        attributes.add(createAttribute("prefix", prefix));
        attributes.add(createAttribute("suffix", suffix));

        data.put("cost", cost);
        data.put("attributes", createAttributes(attributes.stream().filter(Objects::nonNull).collect(Collectors.toList())));

        return json;
    }

    public Template withPrefix(String prefix)
    {
        this.prefix = prefix;
        return this;
    }

    public Template withSuffix(String suffix)
    {
        this.suffix = suffix;
        return this;
    }

    public Template copy() {
        return null;
    }
}
