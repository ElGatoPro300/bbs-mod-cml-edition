package mchorse.bbs_mod.ui.framework.elements.input.keyframes;

import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.utils.keyframes.Keyframe;

public interface IAxisConverter
{
    public String format(double value);

    public double from(double x);

    public double to(double x);

    public void updateField(UITrackpad element);

    public boolean forceInteger(Keyframe keyframe, boolean forceInteger);
}