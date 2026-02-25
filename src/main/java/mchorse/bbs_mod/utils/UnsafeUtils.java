package elgatopro300.bbs_cml.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtils
{
    public static Unsafe getUnsafe()
    {
        try
        {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");

            theUnsafe.setAccessible(true);

            return (Unsafe) theUnsafe.get(null);
        }
        catch (Exception e)
        {}

        return null;
    }
}