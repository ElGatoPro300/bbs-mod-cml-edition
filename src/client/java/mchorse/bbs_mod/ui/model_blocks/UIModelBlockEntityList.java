package elgatopro300.bbs_cml.ui.model_blocks;

import elgatopro300.bbs_cml.blocks.entities.ModelBlockEntity;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.input.list.UIList;
import elgatopro300.bbs_cml.ui.framework.elements.input.list.UIStringList;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Consumer;

public class UIModelBlockEntityList extends UIList<ModelBlockEntity>
{
    public UIModelBlockEntityList(Consumer<List<ModelBlockEntity>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = UIStringList.DEFAULT_HEIGHT;
    }

    @Override
    protected String elementToString(UIContext context, int i, ModelBlockEntity element)
    {
        return element.getName();
    }
}