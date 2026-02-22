package elgatopro300.bbs_cml.ui.forms.editors.panels;

import elgatopro300.bbs_cml.forms.forms.BlockForm;
import elgatopro300.bbs_cml.ui.forms.editors.forms.UIForm;
import elgatopro300.bbs_cml.ui.forms.editors.panels.widgets.UIBlockStateEditor;
import elgatopro300.bbs_cml.ui.framework.elements.input.UIColor;
import elgatopro300.bbs_cml.utils.colors.Color;
import net.minecraft.block.BlockState;

public class UIBlockFormPanel extends UIFormPanel<BlockForm>
{
    public UIColor color;
    public UIBlockStateEditor stateEditor;

    public UIBlockFormPanel(UIForm editor)
    {
        super(editor);

        this.color = new UIColor((c) -> this.form.color.set(Color.rgba(c))).withAlpha();
        this.stateEditor = new UIBlockStateEditor((blockState) -> this.form.blockState.set(blockState));

        this.options.add(this.color, this.stateEditor);
    }

    @Override
    public void startEdit(BlockForm form)
    {
        super.startEdit(form);

        BlockState blockState = this.form.blockState.get();

        this.color.setColor(form.color.get().getARGBColor());
        this.stateEditor.setBlockState(blockState);
    }
}