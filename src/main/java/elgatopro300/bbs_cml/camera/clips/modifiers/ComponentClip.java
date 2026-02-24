package elgatopro300.bbs_cml.camera.clips.modifiers;

import elgatopro300.bbs_cml.camera.clips.CameraClip;
import elgatopro300.bbs_cml.settings.values.numeric.ValueInt;

public abstract class ComponentClip extends CameraClip
{
    /**
     * Active value that uses only 7 bits for determining which components
     * should be processed.
     */
    public final ValueInt active = new ValueInt("active", 0, 0, 0b11111111);

    public ComponentClip()
    {
        super();

        this.add(this.active);
    }

    /**
     * Whether current given bit is 1 
     */
    public boolean isActive(int bit)
    {
        return (this.active.get() >> bit & 1) == 1;
    }
}