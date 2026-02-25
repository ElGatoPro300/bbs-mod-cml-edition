package elgatopro300.bbs_cml.ui.model;

import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.cubic.model.ModelConfig;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UILabel;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.utils.colors.Colors;

public abstract class UIModelSection extends UIElement
{
    public UILabel title;
    public UIElement fields;

    protected ModelConfig config;
    protected UIModelPanel editor;

    public UIModelSection(UIModelPanel editor)
    {
        super();

        this.editor = editor;
        this.title = UI.label(this.getTitle()).background(() -> Colors.A50 | BBSSettings.primaryColor.get());
        this.fields = new UIElement();
        this.fields.column().stretch().vertical().height(20);

        this.column().stretch().vertical();
        this.add(this.title, this.fields);
    }

    public abstract IKey getTitle();

    public void deselect()
    {}

    public void onBoneSelected(String bone)
    {}

    public void setConfig(ModelConfig config)
    {
        this.config = config;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.title.area.isInside(context))
        {
            if (context.mouseButton == 0)
            {
                this.fields.toggleVisible();
                this.resize();
                this.getParent().resize();

                return true;
            }
        }

        return super.subMouseClicked(context);
    }
}
