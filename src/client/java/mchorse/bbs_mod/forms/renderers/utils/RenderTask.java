package mchorse.bbs_mod.forms.renderers.utils;

public class RenderTask
{
    public Runnable task;
    public double distance;
    public int layer;

    public RenderTask(Runnable task, double distance, int layer)
    {
        this.task = task;
        this.distance = distance;
        this.layer = layer;
    }

    public void run()
    {
        this.task.run();
    }
}
