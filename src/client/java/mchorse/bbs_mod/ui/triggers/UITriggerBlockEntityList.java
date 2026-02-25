package elgatopro300.bbs_cml.ui.triggers;

import elgatopro300.bbs_cml.blocks.entities.TriggerBlockEntity;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.input.list.UIList;
import elgatopro300.bbs_cml.ui.framework.elements.input.list.UIStringList;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Consumer;

public class UITriggerBlockEntityList extends UIList<TriggerBlockEntity>
{
    public UITriggerBlockEntityList(Consumer<List<TriggerBlockEntity>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = UIStringList.DEFAULT_HEIGHT;
    }

    @Override
    protected String elementToString(UIContext context, int i, TriggerBlockEntity element)
    {
        BlockPos pos = element.getPos();
        
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }
}
