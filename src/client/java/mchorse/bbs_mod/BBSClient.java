package elgatopro300.bbs_cml;

import elgatopro300.bbs_cml.audio.SoundManager;
import elgatopro300.bbs_cml.camera.controller.CameraController;
import elgatopro300.bbs_cml.cubic.model.ModelManager;
import elgatopro300.bbs_cml.film.Films;
import elgatopro300.bbs_cml.forms.FormCategories;
import elgatopro300.bbs_cml.graphics.FramebufferManager;
import elgatopro300.bbs_cml.graphics.texture.TextureManager;
import elgatopro300.bbs_cml.items.GunZoom;
import elgatopro300.bbs_cml.l10n.L10n;
import elgatopro300.bbs_cml.particles.ParticleManager;
import elgatopro300.bbs_cml.selectors.EntitySelectors;
import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;
import elgatopro300.bbs_cml.utils.ScreenshotRecorder;
import elgatopro300.bbs_cml.utils.VideoRecorder;

/**
 * BBS utility class that provides easy access to BBS Mod's client components.
 */
public class BBSClient
{
    public static TextureManager getTextures()
    {
        return BBSModClient.getTextures();
    }

    public static FramebufferManager getFramebuffers()
    {
        return BBSModClient.getFramebuffers();
    }

    public static SoundManager getSounds()
    {
        return BBSModClient.getSounds();
    }

    public static L10n getL10n()
    {
        return BBSModClient.getL10n();
    }

    public static ModelManager getModels()
    {
        return BBSModClient.getModels();
    }

    public static FormCategories getFormCategories()
    {
        return BBSModClient.getFormCategories();
    }

    public static ScreenshotRecorder getScreenshotRecorder()
    {
        return BBSModClient.getScreenshotRecorder();
    }

    public static VideoRecorder getVideoRecorder()
    {
        return BBSModClient.getVideoRecorder();
    }

    public static EntitySelectors getSelectors()
    {
        return BBSModClient.getSelectors();
    }

    public static ParticleManager getParticles()
    {
        return BBSModClient.getParticles();
    }

    public static CameraController getCameraController()
    {
        return BBSModClient.getCameraController();
    }

    public static Films getFilms()
    {
        return BBSModClient.getFilms();
    }

    public static GunZoom getGunZoom()
    {
        return BBSModClient.getGunZoom();
    }

    public static UIDashboard getDashboard()
    {
        return BBSModClient.getDashboard();
    }
}
