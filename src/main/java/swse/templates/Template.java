package swse.templates;

import swse.common.Change;
import swse.common.AttributeKey;
import swse.common.Copyable;
import swse.common.FoundryItem;

public class Template extends FoundryItem<Template> implements Copyable<Template>
{

    public Template(String name)
    {
        super(name, "template");
    }

    public static Template create(String itemName)
    {
        return new Template(itemName);
    }



    public Template withApplication(String application)
    {
        this.changes.add(Change.create(AttributeKey.APPLICATION, application));
        return this;
    }


    public Template withPrefix(String prefix)
    {
        this.changes.add(Change.create(AttributeKey.PREFIX, prefix));
        return this;
    }

    public Template withSuffix(String suffix)
    {
        this.changes.add(Change.create(AttributeKey.SUFFIX, suffix));
        return this;
    }

    public Template copy() {
        return null;
    }
}
