package elgatopro300.bbs_cml.camera.controller;

import elgatopro300.bbs_cml.camera.Camera;

public interface ICameraController
{
    public void setup(Camera camera, float transition);

    /**
     * Get camera controller priority. The camera controller with highest
     * priority will get picked.
     */
    public default int getPriority()
    {
        return 0;
    }

    public default void update()
    {}
}