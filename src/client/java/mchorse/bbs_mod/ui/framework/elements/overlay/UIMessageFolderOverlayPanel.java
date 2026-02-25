package elgatopro300.bbs_cml.ui.framework.elements.overlay;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.utils.UIUtils;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

import java.io.File;

public class UIMessageFolderOverlayPanel extends UIMessageOverlayPanel
{
    public UIIcon folder;

    private File file;

    public UIMessageFolderOverlayPanel(IKey title, IKey message, File file)
    {
        super(title, message);

        this.file = file;

        this.folder = new UIIcon(Icons.FOLDER, (b) -> UIUtils.openFolder(this.file));

        this.icons.add(this.folder);
    }
}