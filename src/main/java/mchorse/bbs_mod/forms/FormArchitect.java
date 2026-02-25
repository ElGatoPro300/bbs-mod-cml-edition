package elgatopro300.bbs_cml.forms;

import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.utils.factory.MapFactory;

public class FormArchitect extends MapFactory<Form, Void>
{
    @Override
    public String getTypeKey()
    {
        return "id";
    }

    public boolean has(MapType data)
    {
        if (data.has(this.getTypeKey()))
        {
            Link id = Link.create(data.getString(this.getTypeKey()));

            return this.factory.containsKey(id);
        }

        return false;
    }
}