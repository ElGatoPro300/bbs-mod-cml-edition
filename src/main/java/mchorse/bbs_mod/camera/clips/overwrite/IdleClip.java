package elgatopro300.bbs_cml.camera.clips.overwrite;

import elgatopro300.bbs_cml.camera.Camera;
import elgatopro300.bbs_cml.camera.clips.CameraClip;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.camera.values.ValuePosition;
import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.utils.clips.ClipContext;

public class IdleClip extends CameraClip
{
    public final ValuePosition position = new ValuePosition("position");

    public IdleClip()
    {
        super();

        this.add(this.position);
    }

    @Override
    public void fromCamera(Camera camera)
    {
        this.position.get().set(camera);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        position.copy(this.position.get());
    }

    @Override
    protected Clip create()
    {
        return new IdleClip();
    }
}