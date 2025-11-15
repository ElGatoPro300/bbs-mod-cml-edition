package mchorse.bbs_mod.ui.film.utils;

import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.camera.utils.TimeUtils;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.IAxisConverter;
import mchorse.bbs_mod.utils.TimeUtilsClient;
import mchorse.bbs_mod.utils.keyframes.Keyframe;

public class CameraAxisConverter implements IAxisConverter
{
    @Override
    public String format(double value)
    {
        return TimeUtils.formatTime((long) value);
    }

    @Override
    public double from(double v)
    {
        return TimeUtils.fromTime(v);
    }

    @Override
    public double to(double v)
    {
        return TimeUtils.toTime((int) v);
    }

    @Override
    public void updateField(UITrackpad element)
    {
        TimeUtilsClient.configure(element, 0);

        element.limit(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public boolean forceInteger(Keyframe keyframe, boolean forceInteger)
    {
        return !BBSSettings.editorSeconds.get() && forceInteger;
    }
}