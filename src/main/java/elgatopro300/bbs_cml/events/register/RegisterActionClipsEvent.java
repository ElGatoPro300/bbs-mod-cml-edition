package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.camera.clips.ClipFactoryData;
import elgatopro300.bbs_cml.utils.factory.MapFactory;

public class RegisterActionClipsEvent
{
    private final MapFactory<Clip, ClipFactoryData> factory;

    public RegisterActionClipsEvent(MapFactory<Clip, ClipFactoryData> factory)
    {
        this.factory = factory;
    }

    public MapFactory<Clip, ClipFactoryData> getFactory()
    {
        return this.factory;
    }
}
