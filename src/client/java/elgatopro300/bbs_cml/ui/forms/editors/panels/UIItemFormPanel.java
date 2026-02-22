package elgatopro300.bbs_cml.ui.forms.editors.panels;

import elgatopro300.bbs_cml.forms.forms.ItemForm;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.forms.UIForm;
import elgatopro300.bbs_cml.ui.forms.editors.panels.widgets.UIItemStack;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.input.UIColor;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.colors.Color;
// import net.minecraft.client.render.model.json.ModelTransformationMode;

public class UIItemFormPanel extends UIFormPanel<ItemForm>
{
    public UIColor color;
    public UIButton modelTransform;
    public UIItemStack itemStackEditor;

    public UIItemFormPanel(UIForm editor)
    {
        super(editor);

        this.color = new UIColor((c) -> this.form.color.set(Color.rgba(c))).withAlpha();
        this.modelTransform = new UIButton(IKey.EMPTY, (b) ->
        {
            this.getContext().replaceContextMenu((menu) ->
            {
                /*
                for (ModelTransformationMode value : ModelTransformationMode.values())
                {
                    if (this.form.modelTransform.get() == value)
                    {
                        menu.action(Icons.LINE, IKey.constant(value.asString()), true, () -> {});
                    }
                    else
                    {
                        menu.action(Icons.LINE, IKey.constant(value.asString()), () -> this.setModelTransform(value));
                    }
                }
                */
            });
        });

        this.itemStackEditor = new UIItemStack((itemStack) -> this.form.stack.set(itemStack.copy()));

        this.options.add(this.color, UI.label(UIKeys.FORMS_EDITORS_ITEM_TRANSFORMS), this.modelTransform, this.itemStackEditor);
    }

    /*
    private void setModelTransform(ModelTransformationMode value)
    {
        this.form.modelTransform.set(value);

        this.modelTransform.label = IKey.constant(value.asString());
    }
    */

    @Override
    public void startEdit(ItemForm form)
    {
        super.startEdit(form);

        this.color.setColor(form.color.get().getARGBColor());
        // this.modelTransform.label = IKey.constant(form.modelTransform.get().asString());
        this.itemStackEditor.setStack(form.stack.get());
    }
}