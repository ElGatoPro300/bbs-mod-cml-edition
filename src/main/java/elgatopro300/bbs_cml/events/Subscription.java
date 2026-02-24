package elgatopro300.bbs_cml.events;

import java.lang.reflect.Method;

/**
 * Class for all subscribers.
 */
public class Subscription
{
    public final Object target;
    public final Method method;

    public Subscription(Object target, Method method)
    {
        this.target = target;
        this.method = method;

        if (!method.canAccess(target))
        {
            method.setAccessible(true);
        }
    }
}
