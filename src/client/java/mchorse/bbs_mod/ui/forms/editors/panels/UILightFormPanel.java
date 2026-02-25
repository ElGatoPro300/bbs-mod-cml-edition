package elgatopro300.bbs_cml.ui.forms.editors.panels;

import elgatopro300.bbs_cml.forms.forms.LightForm;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.forms.UIForm;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITrackpad;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class UILightFormPanel extends UIFormPanel<LightForm>
{
    public UIToggle enabled;
    public UITrackpad level;

    public UILightFormPanel(UIForm editor)
    {
        super(editor);

        this.enabled = new UIToggle(IKey.raw("Enable"), false, (t) ->
        {
            if (this.form != null)
            {
                this.form.enabled.set(t.getValue());
            }
        });

        this.level = new UITrackpad((v) ->
        {
            int lvl = v.intValue();

            if (this.form != null)
            {
                this.form.level.set(Math.max(0, Math.min(15, lvl)));
            }
        }).integer().limit(0, 15);

        this.level.textbox.setColor(Colors.YELLOW);
        this.level.w(1F);
        this.level.tooltip(UIKeys.MODEL_BLOCKS_LIGHT_LEVEL);

        UIElement icon = new UIElement()
        {
            @Override
            public void render(UIContext context)
            {
                super.render(context);

                context.batcher.icon(Icons.LIGHT, Colors.WHITE, this.area.mx(), this.area.my(), 0.5F, 0.5F);
            }
        }.w(20).h(20);

        this.options.add(this.enabled);
        this.options.add(UI.row(5, 0, 20, icon, this.level));
    }

    @Override
    public void startEdit(LightForm form)
    {
        super.startEdit(form);

        this.enabled.setValue(form.enabled.get());
        this.level.setValue((double) Math.max(0, Math.min(15, form.level.get())));
    }
}
