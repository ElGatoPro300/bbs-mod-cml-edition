package elgatopro300.bbs_cml.forms;

import elgatopro300.bbs_cml.forms.categories.FormCategory;
import elgatopro300.bbs_cml.forms.sections.ExtraFormSection;
import elgatopro300.bbs_cml.forms.sections.FormSection;
import elgatopro300.bbs_cml.forms.sections.ModelFormSection;
import elgatopro300.bbs_cml.forms.sections.ParticleFormSection;
import elgatopro300.bbs_cml.forms.sections.RecentFormSection;
import elgatopro300.bbs_cml.forms.sections.UserFormSection;
import elgatopro300.bbs_cml.utils.watchdog.IWatchDogListener;
import elgatopro300.bbs_cml.utils.watchdog.WatchDogEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FormCategories implements IWatchDogListener
{
    public final VisibilityManager visibility = new VisibilityManager();

    private List<FormSection> sections = new ArrayList<>();
    private RecentFormSection recentForms = new RecentFormSection(this);
    private UserFormSection userForms = new UserFormSection(this);
    private ExtraFormSection extraForms = new ExtraFormSection(this);

    private long lastUpdate;

    /* Setup */

    public void setup()
    {
        this.sections.clear();
        this.sections.add(this.recentForms);
        this.sections.add(this.userForms);
        this.sections.add(new ModelFormSection(this));
        this.sections.add(new ParticleFormSection(this));
        this.sections.add(this.extraForms);

        for (FormSection section : this.sections)
        {
            section.initiate();
        }

        this.markDirty();
        this.visibility.read();
    }

    public long getLastUpdate()
    {
        return lastUpdate;
    }

    public void markDirty()
    {
        this.lastUpdate = System.currentTimeMillis();
    }

    public RecentFormSection getRecentForms()
    {
        return this.recentForms;
    }

    public UserFormSection getUserForms()
    {
        return this.userForms;
    }

    public ExtraFormSection getExtraForms()
    {
        return this.extraForms;
    }

    public List<FormCategory> getAllCategories()
    {
        List<FormCategory> formCategories = new ArrayList<>();

        for (FormSection section : this.sections)
        {
            formCategories.addAll(section.getCategories());
        }

        return formCategories;
    }

    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        for (FormSection section : this.sections)
        {
            section.accept(path, event);
        }
    }
}