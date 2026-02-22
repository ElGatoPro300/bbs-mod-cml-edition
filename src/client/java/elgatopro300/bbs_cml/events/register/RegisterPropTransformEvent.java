package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.framework.elements.input.UIPropTransform;
import elgatopro300.bbs_cml.ui.utils.context.ContextMenuManager;

import java.util.function.BiConsumer;

public class RegisterPropTransformEvent
{
    public void register(BiConsumer<UIPropTransform, ContextMenuManager> consumer)
    {
        UIPropTransform.contextMenuExtensions.add(consumer);
    }
}
