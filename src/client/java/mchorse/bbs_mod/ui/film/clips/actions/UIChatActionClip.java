package elgatopro300.bbs_cml.ui.film.clips.actions;

import elgatopro300.bbs_cml.actions.types.chat.ChatActionClip;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;
import elgatopro300.bbs_cml.ui.utils.UI;

public class UIChatActionClip extends UIActionClip<ChatActionClip>
{
    public UITextbox message;

    public UIChatActionClip(ChatActionClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.message = new UITextbox(1000, (t) -> this.clip.message.set(t));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.label(UIKeys.ACTIONS_CHAT_MESSAGE).marginTop(12), this.message);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.message.setText(this.clip.message.get());
    }
}