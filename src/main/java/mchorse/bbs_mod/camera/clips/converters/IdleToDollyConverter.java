package elgatopro300.bbs_cml.camera.clips.converters;

import elgatopro300.bbs_cml.camera.clips.overwrite.DollyClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.IdleClip;
import elgatopro300.bbs_cml.camera.data.Position;

public class IdleToDollyConverter implements IClipConverter<IdleClip, DollyClip>
{
    @Override
    public DollyClip convert(IdleClip clip)
    {
        DollyClip dollyClip = new DollyClip();
        Position position = clip.position.get();

        dollyClip.copy(clip);
        dollyClip.position.get().copy(position);
        dollyClip.yaw.set(position.angle.yaw);
        dollyClip.pitch.set(position.angle.pitch);

        return dollyClip;
    }
}