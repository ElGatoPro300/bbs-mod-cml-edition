package elgatopro300.bbs_cml.ui.film.replays.overlays;

import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.replays.UIReplaysEditor;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.input.UIColor;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIMessageBarOverlayPanel;
import elgatopro300.bbs_cml.ui.utils.UI;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UIRenameSheetOverlayPanel extends UIMessageBarOverlayPanel
{
    public UITextbox text;
    public UIColor color;

    private final String sheetId;
    private final Replay replay;
    private final BiConsumer<String, Integer> callback;

    public UIRenameSheetOverlayPanel(IKey title, IKey message, Replay replay, String sheetId, BiConsumer<String, Integer> callback)
    {
        super(title, message);

        this.replay = replay;
        this.sheetId = sheetId;
        this.callback = callback;

        this.text = new UITextbox(null);

        Integer existingColor = replay.getSheetColor(sheetId);
        int baseColor = UIReplaysEditor.getColor(sheetId);
        int initialColor = existingColor != null ? existingColor : baseColor;

        final int[] currentColor = new int[] { initialColor };

        this.color = new UIColor((c) ->
        {
            currentColor[0] = c;
        }).withAlpha();

        this.color.setColor(initialColor);
        this.color.tooltip(UIKeys.FILM_REPLAY_RENAME_SHEET_COLOR_TOOLTIP);
        
        UIElement inputs = UI.column(this.color, this.text);
        inputs.relative(this.content).x(0.5F).y(0.5F).w(160).anchor(0.5F, 0.5F);
        
        this.content.add(inputs);

        this.callbackWrapper = (name) -> this.callback.accept(name, currentColor[0]);
    }

    @Override
    protected void onAdd(UIElement parent)
    {
        super.onAdd(parent);

        this.text.textbox.moveCursorToEnd();
        parent.getContext().focus(this.text);
    }

    @Override
    public void confirm()
    {
        super.confirm();

        this.callbackWrapper.accept(this.text.getText());
    }

    private final Consumer<String> callbackWrapper;
}
