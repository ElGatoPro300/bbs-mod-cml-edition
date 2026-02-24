package elgatopro300.bbs_cml.camera.clips;

import elgatopro300.bbs_cml.camera.clips.converters.IClipConverter;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.utils.icons.Icon;
import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.utils.colors.Colors;

import java.util.HashMap;
import java.util.Map;

public class ClipFactoryData
{
    public final Icon icon;
    public final int color;
    public final Map<Link, IClipConverter<? extends Clip, ? extends Clip>> converters = new HashMap<>();

    public ClipFactoryData(Icon icon, int color)
    {
        this.icon = icon;
        this.color = color & Colors.RGB;
    }

    public ClipFactoryData withConverter(Link to, IClipConverter<? extends Clip, ? extends Clip> converter)
    {
        this.converters.put(to, converter);

        return this;
    }
}