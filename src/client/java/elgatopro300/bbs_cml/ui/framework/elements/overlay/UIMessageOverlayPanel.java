package elgatopro300.bbs_cml.ui.framework.elements.overlay;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UIText;

public class UIMessageOverlayPanel extends UIOverlayPanel
{
    public UIText message;

    public UIMessageOverlayPanel(IKey title, IKey message)
    {
        super(title);

        this.message = new UIText().text(message).textAnchorX(0.5F);
        this.message.relative(this.content).x(0.5F).y(12).w(0.7F).anchorX(0.5F);

        this.content.add(this.message);
    }

    public void setMessage(IKey message)
    {
        this.message.text(message);
    }
}