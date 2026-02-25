package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.resources.AssetProvider;

public class RegisterSourcePacksEvent
{
    public final AssetProvider provider;

    public RegisterSourcePacksEvent(AssetProvider provider)
    {
        this.provider = provider;
    }
}
