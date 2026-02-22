package elgatopro300.bbs_cml.camera.clips.converters;

import elgatopro300.bbs_cml.utils.clips.Clip;

public interface IClipConverter <A extends Clip, B extends Clip>
{
    public B convert(A clip);
}