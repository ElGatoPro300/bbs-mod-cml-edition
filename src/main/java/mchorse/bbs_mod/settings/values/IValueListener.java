package elgatopro300.bbs_cml.settings.values;

import elgatopro300.bbs_cml.settings.values.base.BaseValue;

public interface IValueListener
{
    public static final int FLAG_DEFAULT = 0b0;
    public static final int FLAG_UNMERGEABLE = 0b1;

    public void accept(BaseValue value, int flag);
}