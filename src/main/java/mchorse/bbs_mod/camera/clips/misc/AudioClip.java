package elgatopro300.bbs_cml.camera.clips.misc;

import elgatopro300.bbs_cml.camera.clips.CameraClip;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.settings.values.numeric.ValueInt;
import elgatopro300.bbs_cml.settings.values.core.ValueLink;
import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.utils.clips.ClipContext;

import java.util.function.Predicate;

public class AudioClip extends CameraClip
{
    public static final Predicate<Clip> NO_AUDIO = (clip) -> !(clip instanceof AudioClip);

    public ValueLink audio = new ValueLink("audio", null);
    public ValueInt offset = new ValueInt("offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public ValueInt volume = new ValueInt("volume", 100, 0, 400);

    public AudioClip()
    {
        super();

        this.add(this.audio);
        this.add(this.offset);
        this.add(this.volume);
    }

    @Override
    public void shiftLeft(int tick)
    {
        super.shiftLeft(tick);

        this.offset.set(this.offset.get() - (this.tick.get() - tick));
    }

    @Override
    protected void applyClip(ClipContext context, Position position)
    {}

    @Override
    protected Clip create()
    {
        return new AudioClip();
    }

    @Override
    protected void breakDownClip(Clip original, int offset)
    {
        super.breakDownClip(original, offset);

        this.offset.set(this.offset.get() + offset);
    }
}
