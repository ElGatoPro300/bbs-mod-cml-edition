package elgatopro300.bbs_cml.data;

import elgatopro300.bbs_cml.data.types.BaseType;

public interface IDataSerializable <T extends BaseType>
{
    public T toData();

    public void fromData(T data);
}