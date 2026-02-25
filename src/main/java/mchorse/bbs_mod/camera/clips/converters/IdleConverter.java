package elgatopro300.bbs_cml.camera.clips.converters;

import elgatopro300.bbs_cml.camera.clips.CameraClip;
import elgatopro300.bbs_cml.camera.clips.CameraClipContext;
import elgatopro300.bbs_cml.camera.clips.overwrite.IdleClip;

public class IdleConverter
{
    public static final IClipConverter CONVERTER = (clip) ->
    {
        IdleClip idle = new IdleClip();

        idle.copy(clip);

        if (clip instanceof CameraClip)
        {
            ((CameraClip) clip).apply(new CameraClipContext().setup(clip.tick.get(), 0, 0), idle.position.get());
        }

        return idle;
    };
}