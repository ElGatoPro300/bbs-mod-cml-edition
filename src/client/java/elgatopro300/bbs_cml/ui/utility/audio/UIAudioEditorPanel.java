package elgatopro300.bbs_cml.ui.utility.audio;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.audio.SoundManager;
import elgatopro300.bbs_cml.audio.SoundPlayer;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;
import elgatopro300.bbs_cml.ui.dashboard.panels.UISidebarDashboardPanel;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlay;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UISoundOverlayPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIAudioEditorPanel extends UISidebarDashboardPanel
{
    public UIIcon pickAudio;
    public UIIcon plause;
    public UIIcon saveColors;
    public UIAudioEditor audioEditor;

    public UIAudioEditorPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.pickAudio = new UIIcon(Icons.MORE, (b) -> UIOverlay.addOverlay(this.getContext(), new UISoundOverlayPanel(this::openAudio)));
        this.plause = new UIIcon(() ->
        {
            SoundPlayer player = this.audioEditor.getPlayer();

            if (player == null)
            {
                return Icons.STOP;
            }

            return player.isPlaying() ? Icons.PAUSE : Icons.PLAY;
        }, (b) -> this.audioEditor.togglePlayback());
        this.saveColors = new UIIcon(Icons.SAVED, (b) -> this.saveColors());
        this.audioEditor = new UIAudioEditor();
        this.audioEditor.full(this.editor);

        this.iconBar.add(this.pickAudio, this.plause, this.saveColors);
        this.add(this.audioEditor);

        this.openAudio(null);

        this.keys().register(Keys.PLAUSE, this.audioEditor::togglePlayback);
        this.keys().register(Keys.SAVE, this::saveColors);
        this.keys().register(Keys.OPEN_DATA_MANAGER, this.pickAudio::clickItself);
    }

    @Override
    public void requestNames()
    {}

    private void openAudio(Link link)
    {
        this.audioEditor.setup(link);
        this.saveColors.setEnabled(this.audioEditor.isEditing());
    }

    private void saveColors()
    {
        Link audio = this.audioEditor.getAudio();
        SoundManager sounds = BBSModClient.getSounds();

        sounds.saveColorCodes(new Link(audio.source, audio.path + ".json"), this.audioEditor.getColorCodes());
        sounds.deleteSound(audio);
    }
}