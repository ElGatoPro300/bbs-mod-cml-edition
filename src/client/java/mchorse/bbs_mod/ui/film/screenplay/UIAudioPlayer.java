package elgatopro300.bbs_cml.ui.film.screenplay;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.audio.ColorCode;
import elgatopro300.bbs_cml.audio.SoundBuffer;
import elgatopro300.bbs_cml.audio.SoundPlayer;
import elgatopro300.bbs_cml.audio.Wave;
import elgatopro300.bbs_cml.audio.Waveform;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.IUITreeEventListener;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.utils.FontRenderer;
import elgatopro300.bbs_cml.ui.utils.Area;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.colors.Colors;

import java.util.List;

public class UIAudioPlayer extends UIElement implements IUITreeEventListener
{
    public static final float PIXELS = 40;

    public UIIcon play;

    private Wave wave;
    private Waveform waveform;
    private SoundBuffer buffer;
    private SoundPlayer player;

    private boolean wasPlaying;

    public UIAudioPlayer()
    {
        this.play = new UIIcon(() -> this.player == null || this.player.isPlaying() ? Icons.PAUSE : Icons.PLAY, (b) -> this.togglePlaying());
        this.play.relative(this).h(1F);

        this.add(this.play);
    }

    public Wave getWave()
    {
        return this.wave;
    }

    public SoundPlayer getPlayer()
    {
        return this.player;
    }

    @Override
    public void onAddedToTree(UIElement element)
    {}

    @Override
    public void onRemovedFromTree(UIElement element)
    {
        this.delete();
    }

    public void delete()
    {
        if (this.waveform != null) this.waveform.delete();
        if (this.buffer != null) this.buffer.delete();
        if (this.player != null) this.player.delete();

        this.wave = null;
        this.waveform = null;
        this.buffer = null;
        this.player = null;
    }

    public void loadAudio(Wave wave, List<ColorCode> colorCodes)
    {
        this.wave = wave;
        this.waveform = new Waveform();

        this.waveform.generate(this.wave, colorCodes, (int) PIXELS, 20);

        this.buffer = new SoundBuffer(null, this.wave, this.waveform);
        this.player = new SoundPlayer(this.buffer);

        this.player.setRelative(true);
        this.player.stop();
    }

    public void togglePlaying()
    {
        if (this.player != null)
        {
            if (this.player.isPlaying())
            {
                this.player.pause();
            }
            else
            {
                this.player.play();
            }

            this.wasPlaying = this.player.isPlaying();
        }
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        Area.SHARED.set(this.area.x + 20, this.area.y, this.area.w - 20, this.area.h);

        if (this.player != null && Area.SHARED.isInside(context) && context.mouseButton == 0)
        {
            float playback = this.player.getPlaybackPosition();
            float offset = playback > 2F ? playback - 2F : 0F;
            float newPlayback = (context.mouseX - (this.area.x + 20)) / PIXELS;

            this.player.setPlaybackPosition(newPlayback + offset);

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, Colors.A75);

        if (this.waveform != null)
        {
            int w = this.area.w - 20;
            float playback = this.player.getPlaybackPosition();
            float offset = playback > 2F ? playback - 2F : 0F;

            this.waveform.render(context.batcher, Colors.WHITE, this.area.x + 20, this.area.y, w, this.area.h, offset, offset + w / PIXELS);

            int x = this.area.x + 20 + (int) (playback * this.waveform.getPixelsPerSecond() - offset * PIXELS);

            context.batcher.box(x, this.area.y, x + 1, this.area.ey(), Colors.CURSOR);

            int color = BBSSettings.primaryColor(Colors.A50);
            String label = String.format("%.1f/%.1f", this.player.getPlaybackPosition(), this.player.getBuffer().getDuration());
            FontRenderer font = context.batcher.getFont();

            context.batcher.textCard(label, this.area.ex() - 5 - font.getWidth(label), this.area.y + (this.area.h - font.getHeight()) / 2, Colors.WHITE, color);
        }

        if (this.player != null && this.wasPlaying != this.player.isPlaying())
        {
            this.wasPlaying = this.player.isPlaying();
        }

        super.render(context);
    }
}