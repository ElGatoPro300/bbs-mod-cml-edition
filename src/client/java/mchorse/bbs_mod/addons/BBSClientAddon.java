package elgatopro300.bbs_cml.addons;

import elgatopro300.bbs_cml.events.BBSAddonMod;
import elgatopro300.bbs_cml.events.Subscribe;
import elgatopro300.bbs_cml.events.register.RegisterClientSettingsEvent;
import elgatopro300.bbs_cml.events.register.RegisterDashboardPanelsEvent;
import elgatopro300.bbs_cml.events.register.RegisterFilmEditorFactoriesEvent;
import elgatopro300.bbs_cml.events.register.RegisterFormCategoriesEvent;
import elgatopro300.bbs_cml.events.register.RegisterGizmoEvent;
import elgatopro300.bbs_cml.events.register.RegisterImportersEvent;
import elgatopro300.bbs_cml.events.register.RegisterInterpolationsEvent;
import elgatopro300.bbs_cml.events.register.RegisterIconsEvent;
import elgatopro300.bbs_cml.events.register.RegisterUIKeyframeFactoriesEvent;
import elgatopro300.bbs_cml.events.register.RegisterFormsRenderersEvent;
import elgatopro300.bbs_cml.events.register.RegisterFormEditorsEvent;
import elgatopro300.bbs_cml.events.register.RegisterL10nEvent;
import elgatopro300.bbs_cml.events.register.RegisterParticleComponentsEvent;
import elgatopro300.bbs_cml.events.register.RegisterPropTransformEvent;
import elgatopro300.bbs_cml.events.register.RegisterStencilMapEvent;
import elgatopro300.bbs_cml.events.register.RegisterRayTracingEvent;
import elgatopro300.bbs_cml.events.register.RegisterFilmPreviewEvent;
import elgatopro300.bbs_cml.events.register.RegisterReplayListContextMenuEvent;
import elgatopro300.bbs_cml.events.register.RegisterReplayPanelEvent;
import elgatopro300.bbs_cml.events.register.RegisterShadersEvent;
import elgatopro300.bbs_cml.events.register.RegisterSourcePacksEvent;
import elgatopro300.bbs_cml.events.register.RegisterKeyframeShapesEvent;
import elgatopro300.bbs_cml.events.register.RegisterUIValueFactoriesEvent;

/**
 * Base class for BBS client addons.
 *
 * <p>Use this class for client-side only addons.
 * In fabric.mod.json, register this using "bbs-addon-client" entrypoint.</p>
 */
public abstract class BBSClientAddon implements BBSAddonMod
{
    @Subscribe
    public void onRegisterClientSettings(RegisterClientSettingsEvent event)
    {
        this.registerClientSettings(event);
    }

    @Subscribe
    public void onRegisterDashboardPanels(RegisterDashboardPanelsEvent event)
    {
        this.registerDashboardPanels(event);
    }

    @Subscribe
    public void onRegisterFormCategories(RegisterFormCategoriesEvent event)
    {
        this.registerFormCategories(event);
    }

    @Subscribe
    public void onRegisterL10n(RegisterL10nEvent event)
    {
        this.registerL10n(event);
    }

    @Subscribe
    public void onRegisterImporters(RegisterImportersEvent event)
    {
        this.registerImporters(event);
    }

    @Subscribe
    public void onRegisterParticleComponents(RegisterParticleComponentsEvent event)
    {
        this.registerParticleComponents(event);
    }

    protected void registerClientSettings(RegisterClientSettingsEvent event)
    {}

    protected void registerDashboardPanels(RegisterDashboardPanelsEvent event)
    {}

    protected void registerFormCategories(RegisterFormCategoriesEvent event)
    {}

    protected void registerL10n(RegisterL10nEvent event)
    {}

    protected void registerImporters(RegisterImportersEvent event)
    {}

    protected void registerParticleComponents(RegisterParticleComponentsEvent event)
    {}

    @Subscribe
    public void onRegisterInterpolations(RegisterInterpolationsEvent event)
    {
        this.registerInterpolations(event);
    }

    @Subscribe
    public void onRegisterFilmEditorFactories(RegisterFilmEditorFactoriesEvent event)
    {
        this.registerFilmEditorFactories(event);
    }

    protected void registerFilmEditorFactories(RegisterFilmEditorFactoriesEvent event)
    {}

    @Subscribe
    public void onRegisterFormsRenderers(RegisterFormsRenderersEvent event)
    {
        this.registerFormsRenderers(event);
    }

    @Subscribe
    public void onRegisterGizmos(RegisterGizmoEvent event)
    {
        this.registerGizmos(event);
    }

    protected void registerGizmos(RegisterGizmoEvent event)
    {}

    @Subscribe
    public void onRegisterIcons(RegisterIconsEvent event)
    {
        this.registerIcons(event);
    }

    @Subscribe
    public void onRegisterUIKeyframeFactories(RegisterUIKeyframeFactoriesEvent event)
    {
        this.registerUIKeyframeFactories(event);
    }

    protected void registerInterpolations(RegisterInterpolationsEvent event)
    {}

    protected void registerFormsRenderers(RegisterFormsRenderersEvent event)
    {}

    protected void registerIcons(RegisterIconsEvent event)
    {}

    protected void registerUIKeyframeFactories(RegisterUIKeyframeFactoriesEvent event)
    {}

    @Subscribe
    public void onRegisterFormEditors(RegisterFormEditorsEvent event)
    {
        this.registerFormEditors(event);
    }

    protected void registerFormEditors(RegisterFormEditorsEvent event)
    {}

    @Subscribe
    public void onRegisterKeyframeShapes(RegisterKeyframeShapesEvent event)
    {
        this.registerKeyframeShapes(event);
    }

    protected void registerKeyframeShapes(RegisterKeyframeShapesEvent event)
    {}

    @Subscribe
    public void onRegisterUIValueFactories(RegisterUIValueFactoriesEvent event)
    {
        this.registerUIValueFactories(event);
    }

    protected void registerUIValueFactories(RegisterUIValueFactoriesEvent event)
    {}

    @Subscribe
    public void onRegisterPropTransforms(RegisterPropTransformEvent event)
    {
        this.registerPropTransforms(event);
    }

    protected void registerPropTransforms(RegisterPropTransformEvent event)
    {}

    @Subscribe
    public void onRegisterStencilMap(RegisterStencilMapEvent event)
    {
        this.registerStencilMap(event);
    }

    protected void registerStencilMap(RegisterStencilMapEvent event)
    {}

    @Subscribe
    public void onRegisterRayTracing(RegisterRayTracingEvent event)
    {
        this.registerRayTracing(event);
    }

    protected void registerRayTracing(RegisterRayTracingEvent event)
    {}

    @Subscribe
    public void onRegisterFilmPreview(RegisterFilmPreviewEvent event)
    {
        this.registerFilmPreview(event);
    }

    protected void registerFilmPreview(RegisterFilmPreviewEvent event)
    {}

    @Subscribe
    public void onRegisterReplayListContextMenu(RegisterReplayListContextMenuEvent event)
    {
        this.registerReplayListContextMenu(event);
    }

    protected void registerReplayListContextMenu(RegisterReplayListContextMenuEvent event)
    {}

    @Subscribe
    public void onRegisterReplayPanel(RegisterReplayPanelEvent event)
    {
        this.registerReplayPanel(event);
    }

    protected void registerReplayPanel(RegisterReplayPanelEvent event)
    {}
}
