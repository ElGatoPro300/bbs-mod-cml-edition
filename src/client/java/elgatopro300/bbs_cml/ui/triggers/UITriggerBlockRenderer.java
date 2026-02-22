package elgatopro300.bbs_cml.ui.triggers;

import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.IUIElement;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UIModelRenderer;

public class UITriggerBlockRenderer extends UIModelRenderer
{
    @Override
    public void render(UIContext context)
    {
        this.setupPosition();
        this.processInputs(context);
        
        for (IUIElement child : this.getChildren())
        {
            child.render(context);
        }
    }

    @Override
    protected void renderUserModel(UIContext context)
    {}
}
