package elgatopro300.bbs_cml.ui.forms.editors;

import elgatopro300.bbs_cml.ui.forms.IUIFormList;
import elgatopro300.bbs_cml.ui.forms.UIFormList;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.utils.EventPropagation;
import elgatopro300.bbs_cml.utils.colors.Colors;
import org.lwjgl.glfw.GLFW;

public class UIFormEditorList extends UIFormList
{
    public UIFormEditorList(IUIFormList palette)
    {
        super(palette);

        this.edit.removeFromParent();
        this.eventPropagataion(EventPropagation.BLOCK_INSIDE).markContainer();
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            this.palette.exit();
        }

        return true;
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, Colors.A50);

        super.render(context);
    }
}