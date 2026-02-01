package mchorse.bbs_mod.forms.states;

import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.MobForm;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.utils.interps.Lerps;

import java.util.Collections;
import java.util.Map;

public class StatePlayer
{
    private static final Map<String, String> POSE_REMAP = Map.of(
        "pose", "pose_state",
        "pose_overlay", "pose_state_overlay"
    );

    private AnimationState state;
    private int tick;

    private boolean kill;
    private int killTimer;
    private boolean first = true;

    public StatePlayer(AnimationState state)
    {
        this.state = state;
    }

    public AnimationState getState()
    {
        return this.state;
    }

    public boolean canBeRemoved()
    {
        if (this.kill)
        {
            return this.killTimer <= 0;
        }

        if (this.state.main.get() || this.state.looping.get())
        {
            return false;
        }

        return this.tick >= this.state.duration.get();
    }

    public void update()
    {
        this.tick += 1;

        if ((this.state.main.get() || this.state.looping.get()) && this.tick >= this.state.duration.get())
        {
            this.tick = this.state.offset.get();
            this.first = false;
        }

        if (this.kill)
        {
            this.killTimer -= 1;
        }
    }

    public void assignValues(Form form, float transition)
    {
        float t = this.tick + transition;
        int duration = this.state.duration.get();
        float blend = Lerps.envelope(t, 0, this.state.fadeIn.get(), duration - this.state.fadeOut.get(), duration);

        if (this.state.looping.get())
        {
            blend = this.first ? Lerps.envelope(t, 0, this.state.fadeIn.get(), duration, duration) : 1F;
            blend *= this.kill ? (this.killTimer - transition) / (float) this.state.fadeOut.get() : 1F;
        }

        this.state.properties.applyProperties(form, t, blend, form instanceof MobForm || form instanceof ModelForm ? POSE_REMAP : null);
    }

    public void resetValues(Form form)
    {
        this.state.properties.resetProperties(form, POSE_REMAP);
    }

    public void expire()
    {
        this.kill = true;
        this.killTimer = this.state.fadeOut.get();
    }
}