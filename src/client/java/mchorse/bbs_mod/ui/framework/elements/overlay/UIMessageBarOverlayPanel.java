package elgatopro300.bbs_cml.ui.framework.elements.overlay;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.utils.UI;

public abstract class UIMessageBarOverlayPanel extends UIMessageOverlayPanel
{
    public UIButton confirm;
    public UIElement bar;

    public UIMessageBarOverlayPanel(IKey title, IKey message)
    {
        super(title, message);

        this.confirm = new UIButton(UIKeys.GENERAL_OK, (b) -> this.confirm());
        this.bar = UI.row(this.confirm);

        this.confirm.w(80);
        this.bar.relative(this.content).x(6).y(1F, -6).w(1F, -12).anchor(0, 1F);

        this.content.add(this.bar);
    }

    @Override
    public void confirm()
    {
        this.close();
    }
}