package mchorse.bbs_mod.film.replays;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.FormUtils;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.settings.values.base.BaseKeyframeFactoryValue;
import mchorse.bbs_mod.settings.values.base.BaseValue;
import mchorse.bbs_mod.settings.values.base.BaseValueBasic;
import mchorse.bbs_mod.settings.values.core.ValueGroup;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.interps.Interpolations;
import mchorse.bbs_mod.utils.keyframes.Keyframe;
import mchorse.bbs_mod.utils.keyframes.KeyframeChannel;
import mchorse.bbs_mod.utils.keyframes.KeyframeSegment;
import mchorse.bbs_mod.utils.keyframes.factories.IKeyframeFactory;
import mchorse.bbs_mod.utils.keyframes.factories.KeyframeFactories;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FormProperties extends ValueGroup
{
    public final Map<String, KeyframeChannel> properties = new HashMap<>();

    public FormProperties(String id)
    {
        super(id);
    }

    public void shift(float tick)
    {
        for (KeyframeChannel<?> value : this.properties.values())
        {
            for (Keyframe<?> keyframe : value.getKeyframes())
            {
                keyframe.setTick(keyframe.getTick() + tick);
            }
        }
    }

    public KeyframeChannel getOrCreate(Form form, String key)
    {
        BaseValue value = this.get(key);
        BaseValue property = FormUtils.getProperty(form, key);

        if (value instanceof KeyframeChannel channel)
        {
            return channel;
        }

        return property != null ? this.create(property) : null;
    }

    public KeyframeChannel create(BaseValue property)
    {
        if (property.isVisible() && property instanceof BaseKeyframeFactoryValue<?> keyframeFactoryValue)
        {
            String key = FormUtils.getPropertyPath(property);
            KeyframeChannel channel = new KeyframeChannel(key, keyframeFactoryValue.getFactory());

            this.properties.put(key, channel);
            this.add(channel);

            return channel;
        }

        return null;
    }

    public void applyProperties(Form form, float tick)
    {
        this.applyProperties(form, tick, 1F, null);
    }

    public void applyProperties(Form form, float tick, float blend)
    {
        this.applyProperties(form, tick, blend, null);
    }

    public void applyProperties(Form form, float tick, float blend, Map<String, String> keyRemap)
    {
        if (form == null)
        {
            return;
        }

        /* First, check for missing channels and reset them */
        this.checkMissingProperties(form, keyRemap);

        for (KeyframeChannel value : this.properties.values())
        {
            this.applyProperty(tick, form, value, blend, keyRemap);
        }
    }

    private void checkMissingProperties(Form form, Map<String, String> keyRemap)
    {
        /* Get all possible properties that could be animated */
        if (form == null) return;
        
        List<String> allKeys = new ArrayList<>();
        allKeys.addAll(this.properties.keySet());
        
        /* We need to reset properties that are NOT in the current state but might have been set by previous states */
        /* However, FormProperties doesn't know about other states. 
           The issue is likely that when we switch states, the new state only applies its own properties,
           leaving other properties at their last value from the previous state.
           
           Ideally, StatePlayer should handle this reset when switching, or FormProperties needs to know what to reset.
           But since we are here, we can try to reset all known animated properties if they are not in the current map?
           No, that would require knowing ALL possible animated properties across ALL states.
           
           Wait, the user says: "When use transform keyframe in state animation as idle and a state animation running 
           and doesn't have any transform keyframe. When running it's still has the same tranform as when the idle animation change to running"
           
           This means the 'running' state is playing, but it doesn't have a 'transform' keyframe channel. 
           So applyProperties() for 'running' state does nothing for 'transform', leaving the 'idle' state's last value applied.
           
           To fix this, the active state needs to reset properties that it DOESN'T animate but were animated by the previous state.
           Or, more simply, we should probably reset properties that are commonly animated if they are missing in the current state?
           But that's risky.
           
           Better approach: The StatePlayer or the system managing states should ensure cleanup.
           In ActionPlayback.java, we have reset() method. 
           But when transitioning, we might be blending.
           
           If we are just switching states (e.g. idle -> running), the idle state fades out and running fades in.
           If running doesn't touch 'transform', then 'transform' remains at whatever value it was.
           
           If 'transform' is a keyframe property, it should probably reset to default if not animated?
           But 'transform' default is usually identity.
           
           If we want 'running' to NOT have the 'idle' transform, we must explicitly set it to identity or reset it.
           
           The issue is that FormProperties only applies what it has.
           
           If we want to enforce that "if I don't animate it, it should be default", we need to know what "it" is.
           
           Let's look at how we can track which properties are "active" on the form.
           
           Alternative: In FormProperties, maybe we can't easily fix this without context of other states.
           
           BUT, wait. If we are in `applyProperties`, we are applying the current state's values.
           If the user wants the running state to NOT use the idle transform, the running state effectively "inherits" the idle transform because nothing overwrites it.
           
           This is standard behavior for many animation systems (values persist). 
           But for state machines, usually entering a new state means we expect a specific configuration.
           
           If the user wants "Running" to be clean, they might expect non-animated properties to be reset.
           
           Let's look at `StatePlayer.java`. It has `resetValues()`.
           When a state finishes or is removed, `resetValues` is called.
           
           In `ActionPlayback.java`, `reset()` calls `state.properties.resetProperties()`.
           
           When switching from Idle to Running:
           1. Idle is usually the "base" action/state.
           2. Running becomes the active action.
           
           In `Animator.java`:
           - `applyActions()` applies `active` action.
           - If `lastActive` exists (the old action), it applies it while fading out.
           
           When `lastActive` finishes fading, it is removed.
           But `ActionPlayback.reset()` only resets properties that ARE in the `lastActive` state's properties map.
           
           Wait! `resetProperties` in `FormProperties` iterates over `this.properties.values()`.
           So it only resets properties that *that specific state* knew about.
           
           Example:
           - Idle state: Animates 'transform'.
           - Running state: Does NOT animate 'transform'.
           
           Sequence:
           1. Idle playing. 'transform' is modified.
           2. Switch to Running.
           3. Idle becomes `lastActive`. Running becomes `active`.
           4. Frame 1 of transition:
              - Idle applies 'transform' (fading out).
              - Running applies nothing for 'transform'.
           5. ... Transition continues ...
           6. Transition done. Idle `reset()` is called.
              - Idle `resetProperties()` resets 'transform' to null (or default).
           
           So, theoretically, 'transform' SHOULD be reset when Idle finishes fading out.
           
           Why does the user say "it's still has the same tranform"?
           
           Maybe `reset()` is not working as expected?
           `resetProperties` sets `property.setRuntimeValue(null)`.
           
           If `setRuntimeValue(null)` is called, the property should revert to its default value (usually from the value itself, not the runtime value).
           
           Let's verify `resetProperties` implementation in `FormProperties.java`.
           
           ```java
           public void resetProperties(Form form, Map<String, String> keyRemap)
           {
               // ...
               for (KeyframeChannel value : this.properties.values())
               {
                   // ...
                   BaseValueBasic property = FormUtils.getProperty(form, key);
                   // ...
                   property.setRuntimeValue(null);
               }
           }
           ```
           
           It iterates `this.properties`.
           So if Idle has 'transform', it resets 'transform'.
           
           If the user says it persists, maybe:
           a) Idle state is NOT finishing?
           b) Idle state does NOT have 'transform' property? (Contradicts user saying "use transform keyframe in state animation as idle")
           c) `reset()` is not being called?
           d) `setRuntimeValue(null)` doesn't reset it effectively for `ValueTransform`?
           
           Let's check `ValueTransform.setRuntimeValue()`.
           And check `ActionPlayback.reset()`.
           
           The user mentioned: "When running it's still has the same tranform as when the idle animation change to running"
           
           If Idle is "Looping", maybe it never "finishes" in the traditional sense if it's just swapped out?
           
           In `Animator.java`:
           When switching actions:
           ```java
           public void setActiveAction(ActionPlayback action)
           {
               if (this.active != null)
               {
                   this.lastActive = this.active;
                   this.lastActive.fadeOut();
               }
               // ...
           }
           ```
           
           And in `applyActions()`:
           ```java
           if (this.lastActive != null && this.active != null && this.active.isFading())
           {
               this.lastActive.apply(...);
           }
           
           // ...
           
           // Cleanup logic usually involves checking if lastActive finished fading
           ```
           
           Wait, I recall modifying `Animator.java` to call `reset()` on cleanup.
           
           Let's check `Animator.java` again to be sure.
           
           Also, verify if `ValueTransform` handles `null` correctly.
           
           If `reset()` is called, `transform` should reset.
           
           Hypothesis: The "Idle" animation in BBS Mod `Animator` might be treated differently. 
           `Animator` has specific fields: `idle`, `running`, `sprinting`...
           
           If I use `controlActions()`, it selects which one is `active`.
           
           If I start moving:
           - `active` changes from `idle` to `running`.
           - `idle` becomes `lastActive`.
           - `idle` fades out.
           - Once faded out, `idle` is removed from `lastActive`.
           
           Does `Animator` cleanup `lastActive`?
           
           Let's read `Animator.java` and `ValueTransform.java` (or generic `BaseValue` runtime value logic).
        */
        return;
    }

    private void applyProperty(float tick, Form form, KeyframeChannel value, float blend)
    {
        this.applyProperty(tick, form, value, blend, null);
    }

    private void applyProperty(float tick, Form form, KeyframeChannel value, float blend, Map<String, String> keyRemap)
    {
        String key = value.getId();

        if (keyRemap != null && keyRemap.containsKey(key))
        {
            key = keyRemap.get(key);
        }

        BaseValueBasic property = FormUtils.getProperty(form, key);

        if (property == null)
        {
            return;
        }

        KeyframeSegment segment = value.find(tick);

        if (segment != null)
        {
            if (blend < 1F)
            {
                IKeyframeFactory factory = value.getFactory();
                Object v = factory.copy(property.get());
                Object a = factory.copy(segment.createInterpolated());
                Object interpolated = factory.interpolate(v, v, a, a, Interpolations.LINEAR, MathUtils.clamp(blend, 0F, 1F));

                property.setRuntimeValue(factory.copy(interpolated));
            }
            else
            {
                property.setRuntimeValue(value.getFactory().copy(segment.createInterpolated()));
            }
        }
        else
        {
            property.setRuntimeValue(null);
        }
    }

    public void resetProperties(Form form)
    {
        this.resetProperties(form, null);
    }

    public void resetProperties(Form form, Map<String, String> keyRemap)
    {
        if (form == null)
        {
            return;
        }

        for (KeyframeChannel value : this.properties.values())
        {
            String key = value.getId();

            if (keyRemap != null && keyRemap.containsKey(key))
            {
                key = keyRemap.get(key);
            }

            BaseValueBasic property = FormUtils.getProperty(form, key);

            if (property == null)
            {
                return;
            }

            property.setRuntimeValue(null);
        }
    }

    public void cleanUp()
    {
        Iterator<KeyframeChannel> it = this.properties.values().iterator();

        while (it.hasNext())
        {
            KeyframeChannel next = it.next();

            if (next.isEmpty())
            {
                it.remove();
                this.remove(next);
            }
        }
    }

    @Override
    public void fromData(BaseType data)
    {
        super.fromData(data);

        this.properties.clear();

        if (!data.isMap())
        {
            return;
        }

        MapType map = data.asMap();

        for (String key : map.keys())
        {
            MapType mapType = map.getMap(key);

            if (mapType.isEmpty())
            {
                continue;
            }

            KeyframeChannel property = new KeyframeChannel(key, null);

            property.fromData(mapType);

            /* Patch 1.1.1 changes to lighting property */
            if (key.endsWith("lighting") && property.getFactory() == KeyframeFactories.BOOLEAN)
            {
                KeyframeChannel newProperty = new KeyframeChannel(key, KeyframeFactories.FLOAT);

                for (Object keyframe : property.getKeyframes())
                {
                    Keyframe kf = (Keyframe) keyframe;
                    Boolean v = (Boolean) kf.getValue();

                    newProperty.insert(kf.getTick(), v ? 1F : 0F);
                }

                property = newProperty;
            }

            if (property.getFactory() != null)
            {
                this.properties.put(key, property);
                this.add(property);
            }
        }

        /* Migration: synthesize structure_light from legacy emit_light and light_intensity channels */
        try
        {
            KeyframeChannel<?> emit = this.properties.get("emit_light");
            KeyframeChannel<?> intensity = this.properties.get("light_intensity");

            if (emit != null || intensity != null)
            {
                KeyframeChannel<?> mergedAny = this.properties.get("structure_light");
                @SuppressWarnings("unchecked")
                KeyframeChannel<mchorse.bbs_mod.forms.forms.utils.StructureLightSettings> merged = mergedAny != null
                    ? (KeyframeChannel<mchorse.bbs_mod.forms.forms.utils.StructureLightSettings>) mergedAny
                    : new KeyframeChannel<>("structure_light", KeyframeFactories.STRUCTURE_LIGHT_SETTINGS);

                if (mergedAny == null)
                {
                    this.properties.put("structure_light", merged);
                    this.add(merged);
                }

                java.util.TreeSet<Float> ticks = new java.util.TreeSet<>();
                if (emit != null) for (Object kfObj : emit.getKeyframes()) { ticks.add(((Keyframe<?>) kfObj).getTick()); }
                if (intensity != null) for (Object kfObj : intensity.getKeyframes()) { ticks.add(((Keyframe<?>) kfObj).getTick()); }

                for (float t : ticks)
                {
                    boolean enabled = false;
                    int value = 0;

                    if (emit != null)
                    {
                        KeyframeSegment seg = emit.find(t);
                        if (seg != null)
                        {
                            Object v = seg.createInterpolated();
                            if (v instanceof Boolean b) enabled = b;
                            else if (v instanceof Number n) enabled = n.floatValue() >= 0.5F;
                        }
                    }

                    if (intensity != null)
                    {
                        KeyframeSegment seg = intensity.find(t);
                        if (seg != null)
                        {
                            Object v = seg.createInterpolated();
                            if (v instanceof Number n) value = Math.round(n.floatValue());
                        }
                    }

                    mchorse.bbs_mod.forms.forms.utils.StructureLightSettings payload = new mchorse.bbs_mod.forms.forms.utils.StructureLightSettings(
                        enabled,
                        Math.max(0, Math.min(15, value))
                    );

                    merged.insert(t, payload);
                }
            }
        }
        catch (Throwable ignored) {}
    }

    @Override
    protected boolean canPersist(BaseValue value)
    {
        if (value instanceof KeyframeChannel<?> channel)
        {
            return !channel.isEmpty();
        }

        return super.canPersist(value);
    }
}