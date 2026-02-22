package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.utils.IRayTracingHandler;
import elgatopro300.bbs_cml.utils.RayTracing;

public class RegisterRayTracingEvent
{
    public void register(IRayTracingHandler handler)
    {
        RayTracing.handlers.add(handler);
    }
}
