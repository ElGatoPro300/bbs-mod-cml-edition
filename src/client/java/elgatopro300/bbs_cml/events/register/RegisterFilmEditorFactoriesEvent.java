package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.film.UIFilmPanel;
import elgatopro300.bbs_cml.ui.film.UIFilmPreview;
import elgatopro300.bbs_cml.ui.film.UIFilmRecorder;
import elgatopro300.bbs_cml.ui.film.controller.UIFilmController;
import elgatopro300.bbs_cml.ui.film.replays.UIReplaysEditor;

import java.util.function.Function;

public class RegisterFilmEditorFactoriesEvent
{
    private Function<UIFilmPanel, UIFilmController> controller = UIFilmController::new;
    private Function<UIFilmPanel, UIFilmRecorder> recorder = UIFilmRecorder::new;
    private Function<UIFilmPanel, UIFilmPreview> preview = UIFilmPreview::new;
    private Function<UIFilmPanel, UIReplaysEditor> replayEditor = UIReplaysEditor::new;

    public void registerController(Function<UIFilmPanel, UIFilmController> controller)
    {
        this.controller = controller;
    }

    public void registerRecorder(Function<UIFilmPanel, UIFilmRecorder> recorder)
    {
        this.recorder = recorder;
    }

    public void registerPreview(Function<UIFilmPanel, UIFilmPreview> preview)
    {
        this.preview = preview;
    }

    public void registerReplayEditor(Function<UIFilmPanel, UIReplaysEditor> replayEditor)
    {
        this.replayEditor = replayEditor;
    }

    public UIFilmController createController(UIFilmPanel panel)
    {
        return this.controller.apply(panel);
    }

    public UIFilmRecorder createRecorder(UIFilmPanel panel)
    {
        return this.recorder.apply(panel);
    }

    public UIFilmPreview createPreview(UIFilmPanel panel)
    {
        return this.preview.apply(panel);
    }

    public UIReplaysEditor createReplayEditor(UIFilmPanel panel)
    {
        return this.replayEditor.apply(panel);
    }
}
