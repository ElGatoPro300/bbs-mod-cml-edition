package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.morphing.IEntityCaptureHandler;

import java.util.List;

public class RegisterEntityCaptureHandlersEvent
{
    private final List<IEntityCaptureHandler> handlers;

    public RegisterEntityCaptureHandlersEvent(List<IEntityCaptureHandler> handlers)
    {
        this.handlers = handlers;
    }

    public void register(IEntityCaptureHandler handler)
    {
        this.handlers.add(handler);
    }
}
