package mchorse.bbs_mod.camera.utils;

import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.utils.StringUtils;

public class TimeUtils
{
    public static int toTick(float seconds)
    {
        return (int) (seconds * 20);
    }

    public static float toSeconds(float tick)
    {
        return tick / 20F;
    }

    public static String formatTime(long ticks)
    {
        if (BBSSettings.editorSeconds.get())
        {
            long seconds = (long) (ticks / 20D);
            int milliseconds = (int) (ticks % 20 == 0 ? 0 : ticks % 20 * 5D);

            return seconds + "." + StringUtils.leftPad(String.valueOf(milliseconds), 2, "0");
        }

        return String.valueOf(ticks);
    }

    public static double toTime(int ticks)
    {
        return BBSSettings.editorSeconds.get() ? ticks / 20D : ticks;
    }

    public static int fromTime(double time)
    {
        return BBSSettings.editorSeconds.get() ? (int) Math.round(time * 20) : (int) time;
    }
}