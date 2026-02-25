package elgatopro300.bbs_cml.camera.clips.modifiers;

import elgatopro300.bbs_cml.camera.clips.CameraClip;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.settings.values.numeric.ValueFloat;
import elgatopro300.bbs_cml.utils.clips.ClipContext;

public class DollyZoomClip extends CameraClip
{
    public Position position = new Position(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

    public final ValueFloat focus = new ValueFloat("focus", 0F);

    public DollyZoomClip()
    {
        super();

        this.add(this.focus);
    }

    @Override
    protected void applyClip(ClipContext context, Position position)
    {
        if (!context.applyUnderneath(this.tick.get(), 0F, this.position))
        {
            this.position.copy(position);
        }

        float focus = -this.focus.get();
        double dist = focus - focus * Math.tan(Math.toRadians(Math.max(this.position.angle.fov, 0.01F) / 2D)) / Math.tan(Math.toRadians(Math.max(position.angle.fov, 0.01F) / 2D));

        position.point.x += dist * Math.cos(Math.toRadians(position.angle.pitch)) * Math.sin(Math.toRadians(-position.angle.yaw));
        position.point.y += dist * Math.sin(Math.toRadians(position.angle.pitch));
        position.point.z += dist * Math.cos(Math.toRadians(position.angle.pitch)) * Math.cos(Math.toRadians(-position.angle.yaw));
    }

    @Override
    public CameraClip create()
    {
        return new DollyZoomClip();
    }
}