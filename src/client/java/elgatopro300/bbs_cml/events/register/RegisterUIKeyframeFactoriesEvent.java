package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories.UIKeyframeFactory.IUIKeyframeFactoryFactory;
import elgatopro300.bbs_cml.utils.keyframes.factories.IKeyframeFactory;

import java.util.Map;

public class RegisterUIKeyframeFactoriesEvent
{
    private final Map<IKeyframeFactory, IUIKeyframeFactoryFactory> factories;

    public RegisterUIKeyframeFactoriesEvent(Map<IKeyframeFactory, IUIKeyframeFactoryFactory> factories)
    {
        this.factories = factories;
    }

    public void register(IKeyframeFactory factory, IUIKeyframeFactoryFactory uiFactory)
    {
        this.factories.put(factory, uiFactory);
    }
}
