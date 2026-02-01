package mchorse.bbs_mod.cubic.animation;

import mchorse.bbs_mod.cubic.IModelInstance;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.forms.Form;

import java.util.List;

public interface IAnimator
{
    public List<String> getActions();

    public void setup(IModelInstance model, ActionsConfig actionsConfig, boolean fade, Form form);

    public void applyActions(IEntity entity, IModelInstance cubicModel, float transition);

    public void playAnimation(String name);

    public void update(IEntity entity);
}