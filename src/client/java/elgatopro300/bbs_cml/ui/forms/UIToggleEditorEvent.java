package elgatopro300.bbs_cml.ui.forms;

import elgatopro300.bbs_cml.ui.framework.elements.events.UIEvent;

public class UIToggleEditorEvent extends UIEvent<UIFormPalette>
{
    /**
     * Whether form will be edited
     */
    public final boolean editing;

    public UIToggleEditorEvent(UIFormPalette element, boolean editing)
    {
        super(element);

        this.editing = editing;
    }
}