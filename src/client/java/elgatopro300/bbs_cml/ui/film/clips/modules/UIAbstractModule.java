package elgatopro300.bbs_cml.ui.film.clips.modules;

import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;

public abstract class UIAbstractModule extends UIElement
{
    protected IUIClipsDelegate editor;

    public UIAbstractModule(IUIClipsDelegate editor)
    {
        super();

        this.editor = editor;
    }
}