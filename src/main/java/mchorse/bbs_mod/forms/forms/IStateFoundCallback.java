package elgatopro300.bbs_cml.forms.forms;

import elgatopro300.bbs_cml.forms.states.AnimationState;

public interface IStateFoundCallback
{
    public void acceptState(Form form, AnimationState state);
}