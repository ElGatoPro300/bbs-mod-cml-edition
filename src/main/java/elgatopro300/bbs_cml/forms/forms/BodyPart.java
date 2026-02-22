package elgatopro300.bbs_cml.forms.forms;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.forms.entities.StubEntity;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.settings.values.core.ValueString;
import elgatopro300.bbs_cml.settings.values.core.ValueTransform;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.utils.pose.Transform;

public class BodyPart extends ValueGroup
{
    private Form form;

    public final ValueTransform transform = new ValueTransform("transform", new Transform());
    public final ValueString bone = new ValueString("bone", "");
    public final ValueBoolean useTarget = new ValueBoolean("useTarget", false);

    private IEntity entity = new StubEntity();

    public BodyPart(String id)
    {
        super(id);

        this.add(this.transform);
        this.add(this.bone);
        this.add(this.useTarget);
    }

    public Form getForm()
    {
        return this.form;
    }

    public IEntity getEntity()
    {
        return this.entity;
    }

    public BodyPartManager getManager()
    {
        return this.parent instanceof BodyPartManager parts ? parts : null;
    }

    public void setForm(Form form)
    {
        this.preNotify();
        this.setInternalForm(form);
        this.postNotify();
    }

    private void setInternalForm(Form form)
    {
        if (this.form != null)
        {
            this.remove(this.form);
        }

        this.form = form;

        if (this.form != null)
        {
            form.setId("form");
            this.add(this.form);
        }
    }

    public void update(IEntity target)
    {
        if (this.form != null)
        {
            this.form.update(this.useTarget.get() ? target : this.entity);
        }

        this.entity.update();
    }

    public BodyPart copy()
    {
        BodyPart part = new BodyPart(this.id);

        part.fromData(this.toData());

        return part;
    }

    @Override
    public void fromData(BaseType data)
    {
        super.fromData(data);

        if (data.isMap())
        {
            MapType map = data.asMap();
            Form form = map.has("form") ? FormUtils.fromData(map.getMap("form")) : null;

            this.setInternalForm(form);
        }
    }
}