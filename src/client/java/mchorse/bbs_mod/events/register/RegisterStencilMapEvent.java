package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.framework.elements.utils.StencilMap;
import java.util.function.Consumer;

public class RegisterStencilMapEvent
{
    public void register(Consumer<StencilMap> consumer)
    {
        StencilMap.extensions.add(consumer);
    }
}
