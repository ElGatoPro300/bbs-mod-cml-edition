package mchorse.bbs_mod.forms.renderers.utils;

import net.minecraft.client.render.VertexConsumerProvider;
import java.util.function.Consumer;

public class RenderTask
{
    public Consumer<VertexConsumerProvider> task;
    public double distance;
    public int layer;

    public RenderTask(Consumer<VertexConsumerProvider> task, double distance, int layer)
    {
        this.task = task;
        this.distance = distance;
        this.layer = layer;
    }

    public void run(VertexConsumerProvider vcp)
    {
        this.task.accept(vcp);
    }
}
