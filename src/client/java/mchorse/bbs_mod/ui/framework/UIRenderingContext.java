package elgatopro300.bbs_cml.ui.framework;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.graphics.texture.TextureManager;
import elgatopro300.bbs_cml.ui.framework.elements.utils.Batcher2D;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class UIRenderingContext
{
    public Batcher2D batcher;

    private List<Runnable> runnables = new ArrayList<>();

    public UIRenderingContext(DrawContext context)
    {
        this.batcher = new Batcher2D(context);
    }

    public void setDrawContext(DrawContext context)
    {
        this.batcher.setContext(context);
    }

    /* Rendering context implementations */

    public TextureManager getTextures()
    {
        return BBSModClient.getTextures();
    }

    public void postRunnable(Runnable runnable)
    {
        this.runnables.add(runnable);
    }

    public void executeRunnables()
    {
        for (Runnable runnable : this.runnables)
        {
            runnable.run();
        }

        this.runnables.clear();
    }
}
