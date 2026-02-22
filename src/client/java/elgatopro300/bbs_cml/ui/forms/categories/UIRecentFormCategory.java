package elgatopro300.bbs_cml.ui.forms.categories;

import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.categories.FormCategory;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.graphics.window.Window;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.UIFormList;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class UIRecentFormCategory extends UIFormCategory
{
    public UIRecentFormCategory(FormCategory category, UIFormList list)
    {
        super(category, list);

        this.context((menu) ->
        {
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
                menu.action(Icons.TRASH, UIKeys.FORMS_CATEGORIES_CONTEXT_REMOVE_ALL_FORM, Colors.RED, () ->
                {
                    this.category.getDirectForms().clear();
                    this.select(null, false);
                });

                menu.action(Icons.REMOVE, UIKeys.FORMS_CATEGORIES_CONTEXT_REMOVE_FORM, Colors.RED, () ->
                {
                    this.category.removeForm(this.selected);
                    this.select(null, false);
                });
            }
        });
    }
}