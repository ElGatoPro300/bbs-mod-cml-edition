package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.camera.clips.ClipFactoryData;
import elgatopro300.bbs_cml.utils.factory.MapFactory;

public class RegisterCameraClipsEvent
{
    private final MapFactory<Clip, ClipFactoryData> factory;

    public RegisterCameraClipsEvent(MapFactory<Clip, ClipFactoryData> factory)
    {
        this.factory = factory;
    }

    public MapFactory<Clip, ClipFactoryData> getFactory()
    {
        return this.factory;
    }
}
