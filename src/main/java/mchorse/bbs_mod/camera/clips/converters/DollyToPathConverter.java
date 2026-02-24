package elgatopro300.bbs_cml.camera.clips.converters;

import elgatopro300.bbs_cml.camera.clips.CameraClipContext;
import elgatopro300.bbs_cml.camera.clips.overwrite.DollyClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.PathClip;
import elgatopro300.bbs_cml.camera.data.Position;

public class DollyToPathConverter implements IClipConverter<DollyClip, PathClip>
{
    @Override
    public PathClip convert(DollyClip dolly)
    {
        PathClip path = new PathClip();
        Position position = new Position();

        dolly.applyLast(new CameraClipContext(), position);

        path.copy(dolly);
        path.points.reset();
        path.points.add(dolly.position.get().copy());
        path.points.add(position);
        path.interpolationPoint.copy(dolly.interp);
        path.interpolationAngle.copy(dolly.interp);

        return path;
    }
}