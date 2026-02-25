package elgatopro300.bbs_cml.forms.sections;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.forms.FormCategories;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.forms.ParticleForm;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.utils.watchdog.WatchDogEvent;

import java.nio.file.Path;
import java.util.Objects;

public class ParticleFormSection extends SubFormSection
{
    public ParticleFormSection(FormCategories parent)
    {
        super(parent);
    }

    @Override
    public void initiate()
    {
        for (String key : BBSModClient.getParticles().getKeys())
        {
            this.add(key);
        }
    }

    @Override
    protected IKey getTitle()
    {
        return UIKeys.FORMS_CATEGORIES_PARTICLES;
    }

    @Override
    protected Form create(String key)
    {
        ParticleForm form = new ParticleForm();

        form.effect.set(key);

        return form;
    }

    @Override
    protected boolean isEqual(Form form, String key)
    {
        ParticleForm particleForm = (ParticleForm) form;

        return Objects.equals(particleForm.effect.get(), key);
    }

    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        Link link = BBSMod.getProvider().getLink(path.toFile());

        if (link.path.startsWith("particles/") && link.path.endsWith(".json"))
        {
            String key = link.path.substring("particles/".length());

            key = key.substring(0, key.length() - ".json".length());

            if (event == WatchDogEvent.DELETED)
            {
                this.remove(key);
            }
            else
            {
                this.add(key);
            }

            this.parent.markDirty();
        }
    }
}