package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.math.functions.Function;
import java.util.Map;

public class RegisterMolangFunctionsEvent
{
    private final Map<String, Class<? extends Function>> functions;

    public RegisterMolangFunctionsEvent(Map<String, Class<? extends Function>> functions)
    {
        this.functions = functions;
    }

    public void register(String name, Class<? extends Function> function)
    {
        this.functions.put(name, function);
    }
}
