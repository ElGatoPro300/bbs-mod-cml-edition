package elgatopro300.bbs_cml.actions.types.item;

import elgatopro300.bbs_cml.actions.types.ActionClip;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.settings.values.mc.ValueItemStack;

public abstract class ItemActionClip extends ActionClip
{
    public final ValueItemStack itemStack = new ValueItemStack("stack");
    public final ValueBoolean hand = new ValueBoolean("hand", true);

    public ItemActionClip()
    {
        super();

        this.add(this.itemStack);
        this.add(this.hand);
    }
}