package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.settings.SettingsBuilder;
import elgatopro300.bbs_cml.ui.utils.icons.Icon;

import java.io.File;
import java.util.function.Consumer;

public class RegisterSettingsEvent
{
    public void register(Icon icon, String id, Consumer<SettingsBuilder> consumer)
    {
        BBSMod.setupConfig(icon, id, new File(BBSMod.getSettingsFolder(), id + ".json"), consumer);
    }
}
