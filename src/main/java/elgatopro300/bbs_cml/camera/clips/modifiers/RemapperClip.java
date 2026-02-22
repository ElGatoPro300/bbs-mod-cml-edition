package elgatopro300.bbs_cml.camera.clips.modifiers;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.camera.clips.CameraClip;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.utils.MathUtils;
import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.utils.clips.ClipContext;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeChannel;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;

public class RemapperClip extends CameraClip
{
    public final KeyframeChannel<Double> channel = new KeyframeChannel<>("channel", KeyframeFactories.DOUBLE);

    public RemapperClip()
    {
        super();

        this.add(this.channel);

        this.channel.insert(0, 0D);
        this.channel.insert(BBSSettings.getDefaultDuration(), 1D);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        double factor = this.channel.interpolate(context.relativeTick + context.transition);
        int duration = this.duration.get();

        factor *= duration;
        factor = MathUtils.clamp(factor, 0, duration - 0.0001F);

        context.applyUnderneath(this.tick.get() + (int) factor, (float) (factor % 1), position);
    }

    @Override
    public Clip create()
    {
        return new RemapperClip();
    }

    @Override
    protected void breakDownClip(Clip original, int offset)
    {
        super.breakDownClip(original, offset);

        this.channel.moveX(-offset);
    }
}