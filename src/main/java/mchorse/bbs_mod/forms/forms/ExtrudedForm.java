package elgatopro300.bbs_cml.forms.forms;

import elgatopro300.bbs_cml.settings.values.core.ValueColor;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.settings.values.core.ValueLink;
import elgatopro300.bbs_cml.utils.colors.Color;

public class ExtrudedForm extends Form
{
    public final ValueLink texture = new ValueLink("texture", null);
    public final ValueColor color = new ValueColor("color", Color.white());
    public final ValueBoolean billboard = new ValueBoolean("billboard", false);
    public final ValueBoolean shading = new ValueBoolean("shading", true);

    public ExtrudedForm()
    {
        super();

        this.add(this.texture);
        this.add(this.color);
        this.add(this.billboard);
        this.add(this.shading);
    }

    @Override
    public String getDefaultDisplayName()
    {
        Link link = this.texture.get();

        return link == null ? "none" : link.toString();
    }
}