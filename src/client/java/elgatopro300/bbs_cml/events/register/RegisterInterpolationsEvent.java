package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.utils.interps.IInterp;

import java.util.Map;

public class RegisterInterpolationsEvent
{
    public final Map<String, IInterp> interpolations;

    public RegisterInterpolationsEvent(Map<String, IInterp> interpolations)
    {
        this.interpolations = interpolations;
    }

    public void register(String id, IInterp interpolation)
    {
        this.interpolations.put(id, interpolation);
    }
}
