package elgatopro300.bbs_cml.addons;

import elgatopro300.bbs_cml.events.BBSAddonMod;
import elgatopro300.bbs_cml.events.Subscribe;
import elgatopro300.bbs_cml.events.register.RegisterActionClipsEvent;
import elgatopro300.bbs_cml.events.register.RegisterCameraClipsEvent;
import elgatopro300.bbs_cml.events.register.RegisterEntityCaptureHandlersEvent;
import elgatopro300.bbs_cml.events.register.RegisterFormsEvent;
import elgatopro300.bbs_cml.events.register.RegisterSettingsEvent;
import elgatopro300.bbs_cml.events.register.RegisterSourcePacksEvent;
import elgatopro300.bbs_cml.events.register.RegisterKeyframeFactoriesEvent;
import elgatopro300.bbs_cml.events.register.RegisterMolangFunctionsEvent;

/**
 * Base class for BBS addons.
 *
 * <p>Extend this class to create a BBS addon. This class provides convenient methods
 * to register content to the mod.</p>
 */
public abstract class BBSAddon implements BBSAddonMod
{
    @Subscribe
    public void onRegisterForms(RegisterFormsEvent event)
    {
        this.registerForms(event);
    }

    @Subscribe
    public void onRegisterCameraClips(RegisterCameraClipsEvent event)
    {
        this.registerCameraClips(event);
    }

    @Subscribe
    public void onRegisterActionClips(RegisterActionClipsEvent event)
    {
        this.registerActionClips(event);
    }

    @Subscribe
    public void onRegisterSettings(RegisterSettingsEvent event)
    {
        this.registerSettings(event);
    }

    @Subscribe
    public void onRegisterSourcePacks(RegisterSourcePacksEvent event)
    {
        this.registerSourcePacks(event);
    }

    protected void registerForms(RegisterFormsEvent event)
    {}

    protected void registerCameraClips(RegisterCameraClipsEvent event)
    {}

    protected void registerActionClips(RegisterActionClipsEvent event)
    {}

    @Subscribe
    public void onRegisterEntityCaptureHandlers(RegisterEntityCaptureHandlersEvent event)
    {
        this.registerEntityCaptureHandlers(event);
    }

    protected void registerEntityCaptureHandlers(RegisterEntityCaptureHandlersEvent event)
    {}

    protected void registerSettings(RegisterSettingsEvent event)
    {}

    protected void registerSourcePacks(RegisterSourcePacksEvent event)
    {}

    @Subscribe
    public void onRegisterKeyframeFactories(RegisterKeyframeFactoriesEvent event)
    {
        this.registerKeyframeFactories(event);
    }

    protected void registerKeyframeFactories(RegisterKeyframeFactoriesEvent event)
    {}

    @Subscribe
    public void onRegisterMolangFunctions(RegisterMolangFunctionsEvent event)
    {
        this.registerMolangFunctions(event);
    }

    protected void registerMolangFunctions(RegisterMolangFunctionsEvent event)
    {}
}
