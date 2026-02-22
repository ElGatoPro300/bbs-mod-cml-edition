package elgatopro300.bbs_cml;

import elgatopro300.bbs_cml.actions.ActionManager;
import elgatopro300.bbs_cml.events.EventBus;
import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.camera.clips.ClipFactoryData;
import elgatopro300.bbs_cml.film.FilmManager;
import elgatopro300.bbs_cml.forms.FormArchitect;
import elgatopro300.bbs_cml.resources.AssetProvider;
import elgatopro300.bbs_cml.resources.packs.DynamicSourcePack;
import elgatopro300.bbs_cml.resources.packs.ExternalAssetsSourcePack;
import elgatopro300.bbs_cml.settings.SettingsManager;
import elgatopro300.bbs_cml.utils.factory.MapFactory;

import java.io.File;

/**
 * BBS utility class that provides easy access to BBS Mod's core components.
 */
public class BBS
{
    public static EventBus getEvents()
    {
        return BBSMod.events;
    }

    public static File getGameFolder()
    {
        return BBSMod.getGameFolder();
    }

    public static File getAssetsFolder()
    {
        return BBSMod.getAssetsFolder();
    }

    public static File getSettingsFolder()
    {
        return BBSMod.getSettingsFolder();
    }

    public static File getWorldFolder()
    {
        return BBSMod.getWorldFolder();
    }

    public static AssetProvider getProvider()
    {
        return BBSMod.getProvider();
    }

    public static DynamicSourcePack getDynamicSourcePack()
    {
        return BBSMod.getDynamicSourcePack();
    }

    public static ExternalAssetsSourcePack getOriginalSourcePack()
    {
        return BBSMod.getOriginalSourcePack();
    }

    public static SettingsManager getSettings()
    {
        return BBSMod.getSettings();
    }

    public static FormArchitect getForms()
    {
        return BBSMod.getForms();
    }

    public static FilmManager getFilms()
    {
        return BBSMod.getFilms();
    }

    public static ActionManager getActions()
    {
        return BBSMod.getActions();
    }

    public static MapFactory<Clip, ClipFactoryData> getFactoryCameraClips()
    {
        return BBSMod.getFactoryCameraClips();
    }

    public static MapFactory<Clip, ClipFactoryData> getFactoryActionClips()
    {
        return BBSMod.getFactoryActionClips();
    }
}
