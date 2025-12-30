package mchorse.bbs_mod.utils.interps;

import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.utils.interps.types.BaseInterp;
import mchorse.bbs_mod.utils.keyframes.KeyframeChannel;
import mchorse.bbs_mod.utils.keyframes.factories.KeyframeFactories;
import org.lwjgl.glfw.GLFW;

public class CustomInterpolation extends BaseInterp
{
    public KeyframeChannel<Double> channel;

    public CustomInterpolation(String name)
    {
        super(name, GLFW.GLFW_KEY_UNKNOWN);
        this.channel = new KeyframeChannel<>("channel", KeyframeFactories.DOUBLE);
        this.channel.insert(0, 0D);
        this.channel.insert(1, 1D);
    }

    @Override
    public double interpolate(InterpContext context)
    {
        double t = this.channel.interpolate((float) context.x);
        return Lerps.lerp(context.a, context.b, t);
    }

    @Override
    public double interpolate(double a, double b, double x)
    {
        double t = this.channel.interpolate((float) x);
        return Lerps.lerp(a, b, t);
    }

    @Override
    public float interpolate(float a, float b, float x)
    {
        float t = this.channel.interpolate(x).floatValue();
        return Lerps.lerp(a, b, t);
    }

    public void fromData(MapType data)
    {
        if (data.has("channel"))
        {
            this.channel.fromData(data.getMap("channel"));
        }
    }

    public MapType toData()
    {
        MapType data = new MapType();
        data.put("channel", this.channel.toData());
        return data;
    }
}
