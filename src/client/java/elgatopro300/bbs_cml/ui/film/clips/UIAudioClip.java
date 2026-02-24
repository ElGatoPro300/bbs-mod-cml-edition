package elgatopro300.bbs_cml.ui.film.clips;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.audio.SoundBuffer;
import elgatopro300.bbs_cml.camera.clips.misc.AudioClip;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlay;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UISoundOverlayPanel;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.UIUtils;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

import java.io.File;

public class UIAudioClip extends UIClip<AudioClip>
{
    public UIButton pickAudio;
    public UIIcon openFolder;
    public UIIcon extendDuration;
    public UITrackpad offset;
    public UITrackpad volume;

    public UIAudioClip(AudioClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.pickAudio = new UIButton(UIKeys.CAMERA_PANELS_AUDIO_PICK_AUDIO, (b) ->
        {
            UISoundOverlayPanel panel = new UISoundOverlayPanel((l) -> this.clip.audio.set(l), this.getContext());

            UIOverlay.addOverlay(this.getContext(), panel.set(this.clip.audio.get()));
        });

        this.openFolder = new UIIcon(Icons.FOLDER, (b) ->
        {
            Link link = this.clip.audio.get();
            File file = BBSMod.getAudioFolder();

            if (link != null)
            {
                File audioFile = BBSMod.getProvider().getFile(link);

                if (audioFile.exists())
                {
                    file = audioFile.getParentFile();
                }
            }

            UIUtils.openFolder(file);
        });

        this.extendDuration = new UIIcon(Icons.RIGHTLOAD, (b) ->
        {
            Link link = this.clip.audio.get();

            if (link != null)
            {
                SoundBuffer buffer = BBSModClient.getSounds().get(link, true);

                if (buffer != null)
                {
                    this.clip.duration.set((int) ((buffer.getDuration() * 20) - this.clip.offset.get()));
                    this.fillData();
                }
            }
        });
        this.extendDuration.tooltip(UIKeys.CAMERA_PANELS_AUDIO_EXTEND_DURATION);

        this.offset = new UITrackpad((v) -> this.clip.offset.set(v.intValue()));
        this.offset.integer();

        this.volume = new UITrackpad((v) -> this.clip.volume.set(v.intValue()));
        this.volume.integer();
        this.volume.limit(0, 400);
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.C_CLIP.get("bbs:audio")), UI.row(this.pickAudio, this.extendDuration, this.openFolder)).marginTop(12));
        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_AUDIO_OFFSET).marginTop(6), this.offset).marginTop(12));
        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_AUDIO_VOLUME).marginTop(6), this.volume).marginTop(12));
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.offset.setValue(this.clip.offset.get());
        this.volume.setValue(this.clip.volume.get());
    }
}
