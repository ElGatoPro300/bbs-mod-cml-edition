package elgatopro300.bbs_cml.camera.controller;

import elgatopro300.bbs_cml.camera.Camera;
import elgatopro300.bbs_cml.camera.clips.CameraClipContext;
import elgatopro300.bbs_cml.camera.clips.misc.AudioClientClip;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.utils.clips.Clips;

public abstract class CameraWorkCameraController implements ICameraController
{
    protected CameraClipContext context;
    protected Position position = new Position();

    public CameraWorkCameraController()
    {
        this.context = new CameraClipContext();
    }

    public CameraWorkCameraController setWork(Clips clips)
    {
        this.context.clips = clips;

        return this;
    }

    public CameraClipContext getContext()
    {
        return this.context;
    }

    public Position getPosition()
    {
        return this.position;
    }

    protected void apply(Camera camera, int ticks, float transition)
    {
        if (camera != null)
        {
            this.position.set(camera);
        }

        this.context.clipData.clear();
        this.context.setup(ticks, transition);

        for (Clip clip : this.context.clips.getClips(ticks))
        {
            this.context.apply(clip, this.position);
        }

        AudioClientClip.manageSounds(this.context);

        this.context.currentLayer = 0;

        if (camera != null)
        {
            this.position.apply(camera);
        }
    }

    @Override
    public int getPriority()
    {
        return 10;
    }
}