package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.forms.FormCategories;

public class RegisterFormCategoriesEvent
{
    private final FormCategories categories;

    public RegisterFormCategoriesEvent(FormCategories categories)
    {
        this.categories = categories;
    }

    public FormCategories getCategories()
    {
        return this.categories;
    }
}
