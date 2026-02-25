package elgatopro300.bbs_cml.ui.film.clips.renderer;

import elgatopro300.bbs_cml.camera.clips.misc.AudioClientClip;
import elgatopro300.bbs_cml.camera.clips.misc.VideoClip;
import elgatopro300.bbs_cml.utils.clips.Clip;

import java.util.HashMap;
import java.util.Map;

public class UIClipRenderers
{
    private UIClipRenderer defaultRenderer;

    private Map<Class, IUIClipRenderer> renderers = new HashMap<>();

    public UIClipRenderers()
    {
        this.defaultRenderer = new UIClipRenderer();

        this.register(AudioClientClip.class, new UIAudioClipRenderer());
        this.register(VideoClip.class, new UIVideoClipRenderer());
    }

    public void register(Class key, IUIClipRenderer renderer)
    {
        this.renderers.put(key, renderer);
    }

    public <T extends Clip> IUIClipRenderer<T> get(T clip)
    {
        IUIClipRenderer renderer = this.renderers.get(clip.getClass());

        return renderer == null ? this.defaultRenderer : renderer;
    }
}