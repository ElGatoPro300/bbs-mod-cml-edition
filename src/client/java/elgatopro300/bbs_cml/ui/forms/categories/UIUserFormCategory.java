package elgatopro300.bbs_cml.ui.forms.categories;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.categories.FormCategory;
import elgatopro300.bbs_cml.forms.categories.UserFormCategory;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.sections.UserFormSection;
import elgatopro300.bbs_cml.graphics.window.Window;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.UIFormList;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIConfirmOverlayPanel;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlay;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIPromptOverlayPanel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;

public class UIUserFormCategory extends UIFormCategory
{
    public UIUserFormCategory(FormCategory category, UIFormList list)
    {
        super(category, list);

        this.context((menu) ->
        {
            UserFormSection userForms = BBSModClient.getFormCategories().getUserForms();

            menu.action(Icons.EDIT, UIKeys.FORMS_CATEGORIES_CONTEXT_RENAME_CATEGORY, () ->
            {
                UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
                    UIKeys.FORMS_CATEGORIES_RENAME_CATEGORY_TITLE,
                    UIKeys.FORMS_CATEGORIES_RENAME_CATEGORY_DESCRIPTION,
                    (str) ->
                    {
                        this.getCategory().title = IKey.constant(str);
                        userForms.writeUserCategories();
                    }
                );

                panel.text.setText(this.getCategory().title.get());

                UIOverlay.addOverlay(this.getContext(), panel);
            });

            try
            {
                MapType data = Window.getClipboardMap();
                Form form = FormUtils.fromData(data);

                menu.action(Icons.PASTE, UIKeys.FORMS_CATEGORIES_CONTEXT_PASTE_FORM, () -> this.category.addForm(form));
            }
            catch (Exception e)
            {}

            if (this.selected != null)
            {
                menu.action(Icons.REMOVE, UIKeys.FORMS_CATEGORIES_CONTEXT_REMOVE_FORM, () ->
                {
                    this.category.removeForm(this.selected);
                    this.select(null, false);
                });
            }
            else
            {
                menu.action(Icons.TRASH, UIKeys.FORMS_CATEGORIES_CONTEXT_REMOVE_CATEGORY, () ->
                {
                    UIConfirmOverlayPanel panel = new UIConfirmOverlayPanel(
                        UIKeys.FORMS_CATEGORIES_REMOVE_CATEGORY_TITLE.format(this.category.getProcessedTitle()),
                        UIKeys.FORMS_CATEGORIES_REMOVE_CATEGORY_DESCRIPTION,
                        (confirm) ->
                        {
                            if (confirm)
                            {
                                userForms.removeUserCategory((UserFormCategory) this.category);

                                UIElement parent = this.getParentContainer();

                                this.removeFromParent();
                                parent.resize();
                            }
                        }
                    );

                    UIOverlay.addOverlay(this.getContext(), panel);
                });
            }
        });
    }

    private UserFormCategory getCategory()
    {
        return (UserFormCategory) this.category;
    }
}