package elgatopro300.bbs_cml.ui;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.cubic.model.ModelConfig;
import elgatopro300.bbs_cml.cubic.model.ModelRepository;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.film.FilmManager;
import elgatopro300.bbs_cml.network.ClientNetwork;
import elgatopro300.bbs_cml.particles.ParticleScheme;
import elgatopro300.bbs_cml.settings.values.core.ValueGroup;
import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;
import elgatopro300.bbs_cml.ui.dashboard.panels.UIDataDashboardPanel;
import elgatopro300.bbs_cml.ui.film.UIFilmPanel;
import elgatopro300.bbs_cml.ui.model.UIModelPanel;
import elgatopro300.bbs_cml.ui.particles.UIParticleSchemePanel;
import elgatopro300.bbs_cml.utils.repos.FilmRepository;
import elgatopro300.bbs_cml.utils.repos.FolderManagerRepository;
import elgatopro300.bbs_cml.utils.repos.IRepository;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;

public class ContentType
{
    private static final IRepository<ParticleScheme> PARTICLE_REPOSITORY = new FolderManagerRepository<>(BBSModClient.getParticles());
    private static final IRepository<Film> FILMS_REPOSITORY = new FolderManagerRepository<>(BBSMod.getFilms());
    private static final IRepository<Film> FILMS_LOCAL_REPOSITORY = new FolderManagerRepository<>(new FilmManager(() -> new File(BBSMod.getAssetsFolder().getParentFile(), "data/films")));
    private static final IRepository<Film> FILMS_REMOTE_REPOSITORY = new FilmRepository();

    public static final ContentType PARTICLES = new ContentType("particles", () -> PARTICLE_REPOSITORY, (dashboard) -> dashboard.getPanel(UIParticleSchemePanel.class));
    public static final ContentType MODELS = new ContentType("models", () -> new ModelRepository(BBSModClient.getModels()), (dashboard) -> dashboard.getPanel(UIModelPanel.class));
    public static final ContentType FILMS = new ContentType("films", ContentType::getFilmsRepository, (dashboard) -> dashboard.getPanel(UIFilmPanel.class));

    private static IRepository<? extends ValueGroup> getFilmsRepository()
    {
        if (MinecraftClient.getInstance().isIntegratedServerRunning())
        {
            return FILMS_REPOSITORY;
        }

        return ClientNetwork.isIsBBSModOnServer() ? FILMS_REMOTE_REPOSITORY : FILMS_LOCAL_REPOSITORY;
    }

    private final String id;
    private Supplier<IRepository<? extends ValueGroup>> manager;
    private Function<UIDashboard, UIDataDashboardPanel> dashboardPanel;

    public ContentType(String id, Supplier<IRepository<? extends ValueGroup>> manager, Function<UIDashboard, UIDataDashboardPanel> dashboardPanel)
    {
        this.id = id;
        this.manager = manager;
        this.dashboardPanel = dashboardPanel;
    }

    public String getId()
    {
        return this.id;
    }

    public IRepository<? extends ValueGroup> getRepository()
    {
        return this.manager.get();
    }

    public UIDataDashboardPanel get(UIDashboard dashboard)
    {
        return this.dashboardPanel.apply(dashboard);
    }
}