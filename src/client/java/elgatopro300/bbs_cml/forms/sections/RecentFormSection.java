package elgatopro300.bbs_cml.forms.sections;

import elgatopro300.bbs_cml.forms.FormCategories;
import elgatopro300.bbs_cml.forms.categories.FormCategory;
import elgatopro300.bbs_cml.forms.categories.RecentFormCategory;

import java.util.Collections;
import java.util.List;

public class RecentFormSection extends FormSection
{
    private RecentFormCategory recent;

    public RecentFormSection(FormCategories parent)
    {
        super(parent);
    }

    @Override
    public void initiate()
    {
        this.recent = new RecentFormCategory(this.parent.visibility.get("recent"));
    }

    @Override
    public List<FormCategory> getCategories()
    {
        return Collections.singletonList(this.recent);
    }
}