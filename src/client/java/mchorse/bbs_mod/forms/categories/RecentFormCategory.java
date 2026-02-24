package elgatopro300.bbs_cml.forms.categories;

import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.UIFormList;
import elgatopro300.bbs_cml.ui.forms.categories.UIFormCategory;
import elgatopro300.bbs_cml.ui.forms.categories.UIRecentFormCategory;

public class RecentFormCategory extends FormCategory
{
    public RecentFormCategory(ValueBoolean visibility)
    {
        super(UIKeys.FORMS_CATEGORIES_RECENT, visibility);
    }

    @Override
    public boolean canModify(Form form)
    {
        return true;
    }

    @Override
    public UIFormCategory createUI(UIFormList list)
    {
        return new UIRecentFormCategory(this, list);
    }
}