package elgatopro300.bbs_cml.ui.framework.elements;

import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.utils.Area;

public interface IUIElement
{
    /**
     * Should be called when position has to be recalculated
     */
    public void resize();

    /**
     * Whether this element is enabled (and can accept any input) 
     */
    public boolean isEnabled();

    /**
     * Whether this element is visible
     */
    public boolean isVisible();

    /**
     * Mouse was clicked
     */
    public IUIElement mouseClicked(UIContext context);

    /**
     * Mouse wheel was scrolled
     */
    public IUIElement mouseScrolled(UIContext context);

    /**
     * Mouse was released
     */
    public IUIElement mouseReleased(UIContext context);

    /**
     * Key was typed
     */
    public IUIElement keyPressed(UIContext context);

    /**
     * Text was inputted
     */
    public IUIElement textInput(UIContext context);

    /**
     * Determines whether this element can be rendered on the screen
     */
    public boolean canBeRendered(Area viewport);

    /**
     * Draw its components on the screen
     */
    public void render(UIContext context);
}