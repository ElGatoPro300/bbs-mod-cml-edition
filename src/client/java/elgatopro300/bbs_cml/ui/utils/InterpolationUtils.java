package elgatopro300.bbs_cml.ui.utils;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.utils.context.ContextAction;
import elgatopro300.bbs_cml.utils.interps.CustomInterpolation;
import elgatopro300.bbs_cml.utils.interps.IInterp;
import elgatopro300.bbs_cml.utils.interps.Interpolation;
import org.lwjgl.glfw.GLFW;

public class InterpolationUtils
{
    public static void setupKeybind(IInterp interp, ContextAction action, IKey category)
    {
        String key = interp.getKey();

        if (key.endsWith("_in"))
        {
            action.key(category, interp.getKeyCode(), GLFW.GLFW_KEY_LEFT_SHIFT);
        }
        else if (key.endsWith("_out"))
        {
            action.key(category, interp.getKeyCode(), GLFW.GLFW_KEY_LEFT_CONTROL);
        }
        else
        {
            action.key(category, interp.getKeyCode());
        }
    }

    public static IKey getName(IInterp interp)
    {
        if (interp instanceof CustomInterpolation)
        {
            return IKey.raw(interp.getKey());
        }
        else if (interp instanceof Interpolation)
        {
            if (((Interpolation) interp).getInterp() instanceof CustomInterpolation)
            {
                return IKey.raw(interp.getKey());
            }
        }

        return UIKeys.C_INTERPOLATION.get(interp.getKey());
    }
}