package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories;

import elgatopro300.bbs_cml.ui.forms.editors.panels.widgets.UIBlockStateEditor;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframes;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;
import net.minecraft.block.BlockState;

public class UIBlockStateKeyframeFactory extends UIKeyframeFactory<BlockState>
{
    private UIBlockStateEditor editor;

    public UIBlockStateKeyframeFactory(Keyframe<BlockState> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        this.editor = new UIBlockStateEditor(this::setValue);
        this.editor.setBlockState(keyframe.getValue());

        this.scroll.add(this.editor);
    }
}