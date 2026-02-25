package elgatopro300.bbs_cml.forms.sections;

import elgatopro300.bbs_cml.forms.FormCategories;
import elgatopro300.bbs_cml.forms.categories.FormCategory;
import elgatopro300.bbs_cml.utils.watchdog.IWatchDogListener;
import elgatopro300.bbs_cml.utils.watchdog.WatchDogEvent;

import java.nio.file.Path;
import java.util.List;

public abstract class FormSection implements IWatchDogListener
{
    protected FormCategories parent;

    public FormSection(FormCategories parent)
    {
        this.parent = parent;
    }

    public abstract void initiate();

    public abstract List<FormCategory> getCategories();

    @Override
    public void accept(Path path, WatchDogEvent event)
    {}
}