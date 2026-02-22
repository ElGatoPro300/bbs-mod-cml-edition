package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories;

import elgatopro300.bbs_cml.ui.forms.editors.panels.widgets.UIItemStack;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframes;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;
import net.minecraft.item.ItemStack;

public class UIItemStackKeyframeFactory extends UIKeyframeFactory<ItemStack>
{
    private UIItemStack editor;

    public UIItemStackKeyframeFactory(Keyframe<ItemStack> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        this.editor = new UIItemStack(this::setValue);
        this.editor.setStack(keyframe.getValue());

        this.scroll.add(this.editor);
    }
}