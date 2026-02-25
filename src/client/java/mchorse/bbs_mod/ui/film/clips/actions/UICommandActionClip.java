package elgatopro300.bbs_cml.ui.film.clips.actions;

import elgatopro300.bbs_cml.actions.types.chat.CommandActionClip;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UICommandActionClip extends UIActionClip<CommandActionClip>
{
    public UITextbox command;

    public UICommandActionClip(CommandActionClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.command = new UITextbox(10000, (t) -> this.clip.command.set(t));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.label(UIKeys.ACTIONS_COMMAND_COMMAND).marginTop(12), this.command);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.command.setText(this.clip.command.get());
    }
}