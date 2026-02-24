package elgatopro300.bbs_cml.ui.film.clips.renderer;

import elgatopro300.bbs_cml.audio.SoundBuffer;
import elgatopro300.bbs_cml.camera.clips.misc.VideoClip;
import elgatopro300.bbs_cml.camera.utils.TimeUtils;
import elgatopro300.bbs_cml.client.video.VideoWaveformCache;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.Area;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class UIVideoClipRenderer extends UIClipRenderer<VideoClip>
{
    @Override
    protected void renderBackground(UIContext context, int color, VideoClip clip, Area area, boolean selected, boolean current)
    {
        SoundBuffer buffer = VideoWaveformCache.get(clip.video.get());

        if (buffer != null && buffer.getWaveform() != null)
        {
            int offset = clip.offset.get();

            context.batcher.box(area.x, area.y, area.ex(), area.ey(), Colors.mulRGB(color, 0.6F));
            buffer.getWaveform().render(context.batcher, Colors.WHITE, area.x, area.y, area.w, area.h,
                TimeUtils.toSeconds(offset), TimeUtils.toSeconds(offset + clip.duration.get()));
        }
        else
        {
            super.renderBackground(context, color, clip, area, selected, current);
        }
    }
}
