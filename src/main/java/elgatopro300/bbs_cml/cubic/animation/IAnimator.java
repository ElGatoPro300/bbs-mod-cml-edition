package elgatopro300.bbs_cml.cubic.animation;

import elgatopro300.bbs_cml.cubic.IModelInstance;
import elgatopro300.bbs_cml.forms.entities.IEntity;

import java.util.List;

public interface IAnimator
{
    public List<String> getActions();

    public void setup(IModelInstance model, ActionsConfig actionsConfig, boolean fade);

    public void applyActions(IEntity entity, IModelInstance cubicModel, float transition);

    public void playAnimation(String name);

    public void update(IEntity entity);
}