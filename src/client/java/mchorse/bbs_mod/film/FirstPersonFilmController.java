package elgatopro300.bbs_cml.film;

import elgatopro300.bbs_cml.film.replays.Replay;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class FirstPersonFilmController extends WorldFilmController
{
    public FirstPersonFilmController(Film film)
    {
        super(film);
    }

    @Override
    protected void renderEntity(WorldRenderContext context, Replay replay, IEntity entity)
    {
        if (replay.fp.get())
        {
            return;
        }

        super.renderEntity(context, replay, entity);
    }
}