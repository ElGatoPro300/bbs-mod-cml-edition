package elgatopro300.bbs_cml.forms.forms;

import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.settings.values.numeric.ValueInt;

public class LightForm extends Form
{
    public final ValueBoolean enabled = new ValueBoolean("enabled", true);
    public final ValueInt level = new ValueInt("level", 15);

    public LightForm()
    {
        super();

        this.add(this.enabled);
        this.add(this.level);
    }
}

