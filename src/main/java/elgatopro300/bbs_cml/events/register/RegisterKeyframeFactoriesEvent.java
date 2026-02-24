package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.utils.keyframes.factories.IKeyframeFactory;

import java.util.Map;

public class RegisterKeyframeFactoriesEvent
{
    private final Map<String, IKeyframeFactory> factories;

    public RegisterKeyframeFactoriesEvent(Map<String, IKeyframeFactory> factories)
    {
        this.factories = factories;
    }

    public void register(String name, IKeyframeFactory factory)
    {
        this.factories.put(name, factory);
    }
}
