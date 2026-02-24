package elgatopro300.bbs_cml.forms.forms;

import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.settings.values.numeric.ValueFloat;
import elgatopro300.bbs_cml.settings.values.core.ValueLink;

public class TrailForm extends Form
{
    public final ValueLink texture = new ValueLink("texture", Link.assets("textures/default_trail.png"));
    public final ValueFloat length = new ValueFloat("length", 10F);
    public final ValueBoolean loop = new ValueBoolean("loop", false);
    public final ValueBoolean paused = new ValueBoolean("paused", false);
    
    public TrailForm()
    {
        this.add(this.texture);
        this.add(this.length);
        this.add(this.loop);
        this.add(this.paused);
    }
}