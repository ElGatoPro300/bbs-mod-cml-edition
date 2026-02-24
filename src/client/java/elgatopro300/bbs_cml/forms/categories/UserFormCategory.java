package elgatopro300.bbs_cml.forms.categories;

import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.sections.UserFormSection;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.ui.forms.UIFormList;
import elgatopro300.bbs_cml.ui.forms.categories.UIFormCategory;
import elgatopro300.bbs_cml.ui.forms.categories.UIUserFormCategory;

public class UserFormCategory extends FormCategory
{
    private UserFormSection section;

    public UserFormCategory(IKey title, ValueBoolean visibility, UserFormSection section)
    {
        super(title, visibility);

        this.section = section;
    }

    @Override
    public boolean canModify(Form form)
    {
        return true;
    }

    @Override
    public UIFormCategory createUI(UIFormList list)
    {
        return new UIUserFormCategory(this, list);
    }

    @Override
    public void addForm(Form form)
    {
        super.addForm(form);

        this.section.writeUserCategories(this);
    }

    @Override
    public void addForm(int index, Form form)
    {
        super.addForm(index, form);

        this.section.writeUserCategories(this);
    }

    @Override
    public void replaceForm(int index, Form form)
    {
        super.replaceForm(index, form);

        this.section.writeUserCategories(this);
    }

    @Override
    public void removeForm(Form form)
    {
        super.removeForm(form);

        this.section.writeUserCategories(this);
    }

    public void moveForm(int from, int to)
    {
        java.util.List<Form> forms = this.getDirectForms();

        if (from >= 0 && from < forms.size() && to >= 0 && to < forms.size() && from != to)
        {
            Form form = forms.remove(from);
            forms.add(to, form);

            this.section.writeUserCategories(this);
        }
    }
}