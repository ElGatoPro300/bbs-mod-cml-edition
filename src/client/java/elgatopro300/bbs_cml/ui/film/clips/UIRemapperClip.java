package elgatopro300.bbs_cml.ui.film.clips;

import elgatopro300.bbs_cml.camera.clips.modifiers.RemapperClip;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.film.replays.UIReplaysEditor;
import elgatopro300.bbs_cml.ui.film.utils.keyframes.UIFilmKeyframes;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframeEditor;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.utils.clips.Clips;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class UIRemapperClip extends UIClip<RemapperClip>
{
    public UIKeyframeEditor keyframes;
    public UIButton edit;

    public UIRemapperClip(RemapperClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.keyframes = new UIKeyframeEditor((consumer) -> new UIFilmKeyframes(this.editor, consumer));
        this.keyframes.view.backgroundRenderer((context) ->
        {
            UIReplaysEditor.renderBackground(context, this.keyframes.view, (Clips) this.clip.getParent(), this.clip.tick.get());
        });
        this.keyframes.view.single().duration(() -> this.clip.duration.get());
        this.keyframes.setUndoId("remapper_keyframes");

        this.edit = new UIButton(UIKeys.CAMERA_PANELS_EDIT_KEYFRAMES, (b) ->
        {
            this.editor.embedView(this.keyframes);
            this.keyframes.view.resetView();
            this.keyframes.view.editSheet(this.keyframes.view.getGraph().getSheets().get(0));
            this.keyframes.view.getGraph().clearSelection();
        });

        this.edit.keys().register(Keys.FORMS_EDIT, () -> this.edit.clickItself());
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.C_CLIP.get("bbs:remapper")), this.edit).marginTop(12));
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.keyframes.setChannel(this.clip.channel, Colors.ACTIVE);
    }

    @Override
    public void applyUndoData(MapType data)
    {
        super.applyUndoData(data);

        if (data.getString("embed").equals("remapper"))
        {
            this.editor.embedView(this.keyframes);
            this.keyframes.view.editSheet(this.keyframes.view.getGraph().getSheets().get(0));
            this.keyframes.view.resetView();
        }
    }

    @Override
    public void collectUndoData(MapType data)
    {
        super.collectUndoData(data);

        if (this.keyframes.hasParent())
        {
            data.putString("embed", "remapper");
        }
    }
}