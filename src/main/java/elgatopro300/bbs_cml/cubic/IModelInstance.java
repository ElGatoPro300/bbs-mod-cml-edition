package elgatopro300.bbs_cml.cubic;

import elgatopro300.bbs_cml.cubic.data.animation.Animations;
import elgatopro300.bbs_cml.utils.pose.Pose;

public interface IModelInstance
{
    public IModel getModel();

    public Pose getSneakingPose();

    public Animations getAnimations();
}