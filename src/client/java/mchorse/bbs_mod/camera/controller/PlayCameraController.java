package elgatopro300.bbs_cml.camera.controller;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.camera.Camera;
import elgatopro300.bbs_cml.utils.clips.Clips;

public class PlayCameraController extends CameraWorkCameraController
{
    private int ticks;
    private int duration;

    public PlayCameraController(Clips clips)
    {
        super();

        this.setWork(clips);

        this.duration = clips.calculateDuration();
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        this.apply(camera, this.ticks, transition);
    }

    @Override
    public void update()
    {
        super.update();

        this.ticks += 1;

        if (this.ticks >= this.duration)
        {
            BBSModClient.getCameraController().remove(this);
        }
    }
}