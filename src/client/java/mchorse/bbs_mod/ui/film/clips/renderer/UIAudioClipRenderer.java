package elgatopro300.bbs_cml.ui.film.clips.renderer;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.audio.SoundBuffer;
import elgatopro300.bbs_cml.camera.clips.misc.AudioClip;
import elgatopro300.bbs_cml.camera.utils.TimeUtils;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.Area;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class UIAudioClipRenderer extends UIClipRenderer<AudioClip>
{
    @Override
    protected void renderBackground(UIContext context, int color, AudioClip clip, Area area, boolean selected, boolean current)
    {
        Link link = clip.audio.get();

        if (link != null)
        {
            SoundBuffer player = BBSModClient.getSounds().get(link, true);

            if (player != null)
            {
                int offset = clip.offset.get();

                context.batcher.box(area.x, area.y, area.ex(), area.ey(), Colors.mulRGB(color, 0.6F));
                player.getWaveform().render(context.batcher, Colors.WHITE, area.x, area.y, area.w, area.h, TimeUtils.toSeconds(offset), TimeUtils.toSeconds(offset + clip.duration.get()));
            }
        }
        else
        {
            super.renderBackground(context, color, clip, area, selected, current);
        }
    }
}