package swse.prerequisite;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import org.json.JSONObject;

public class NotPrerequisite extends Prerequisite {
    private final Prerequisite child;

    public static Prerequisite not(String plainText,
                                   Prerequisite child) {
        return new NotPrerequisite(plainText, child);
    }

    public static Prerequisite not(Prerequisite child) {
        return new NotPrerequisite(child);
    }

    public NotPrerequisite(String plainText,
                           Prerequisite child) {
        super(plainText, "AND");
        this.child = child;
    }

    public NotPrerequisite(Prerequisite child) {
        super("Not" + child.getPlainText(), "NOT");
        this.child = child;
    }

    @Nonnull
    @Override
    public JSONObject toJSON() {
        return new JSONObject().put("text", plainText).put("type", type).put("child", child.toJSON());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("child", child)
                .add("plainText", plainText)
                .add("type", type)
                .toString();
    }

    @Override
    public Prerequisite copy() {
        return new NotPrerequisite(plainText, child.copy());
    }
}
