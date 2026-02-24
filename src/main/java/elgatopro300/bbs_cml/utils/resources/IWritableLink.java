package elgatopro300.bbs_cml.utils.resources;

import elgatopro300.bbs_cml.data.IDataSerializable;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.resources.Link;

public interface IWritableLink extends IDataSerializable<BaseType>
{
    public Link copy();
}