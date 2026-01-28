package mchorse.bbs_mod.utils.keyframes;

import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.keyframes.factories.IKeyframeFactory;

/**
 * Keyframe segment.
 *
 * This structure contains all the necessary data to create an interpolated
 * value.
 */
public class KeyframeSegment <T>
{
    public Keyframe<T> a;
    public Keyframe<T> b;

    public Keyframe<T> preA;
    public Keyframe<T> postB;
    public float duration;
    public float offset;
    public float x;

    public KeyframeSegment()
    {}

    public KeyframeSegment(Keyframe<T> a, Keyframe<T> b)
    {
        this.fill(a, b);
    }

    public void setup(Keyframe<T> a, Keyframe<T> b, float ticks)
    {
        this.fill(a, b);
        this.setup(ticks);
    }

    public void fill(Keyframe<T> a, Keyframe<T> b)
    {
        this.a = a;
        this.b = b;

        KeyframeChannel<T> channel = (KeyframeChannel<T>) a.getParent();
        int indexA = channel.getKeyframes().indexOf(a);
        int indexB = channel.getKeyframes().indexOf(b);

        if (indexA >= 0 && indexB >= 0)
        {
            int prevIndex = channel.getPreviousEnabledIndex(indexA - 1);
            int nextIndex = channel.getNextEnabledIndex(indexB + 1);

            this.preA = prevIndex == -1 ? a : channel.get(prevIndex);
            this.postB = nextIndex == -1 ? b : channel.get(nextIndex);
        }
        else
        {
            this.preA = a;
            this.postB = b;
        }
    }

    public void setup(float ticks)
    {
        float forcedDuration = this.a.getDuration();

        this.duration = forcedDuration > 0 ? forcedDuration : this.b.getTick() - this.a.getTick();
        this.offset = ticks - this.a.getTick();
        this.x = MathUtils.clamp(this.duration == 0 ? 0F : this.offset / this.duration, 0F, 1F);
    }

    public T createInterpolated()
    {
        IKeyframeFactory<T> factory = this.a.getFactory();

        if (this.isSame())
        {
            return factory.copy(this.a.getValue());
        }

        return factory.copy(factory.interpolate(this.preA, this.a, this.b, this.postB, this.a.getInterpolation(), this.x));
    }

    public boolean isSame()
    {
        return this.a == this.b;
    }

    public Keyframe<T> getClosest()
    {
        return this.x > 0.5F ? this.b : this.a;
    }
}