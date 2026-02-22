package elgatopro300.bbs_cml.camera.clips.modifiers;

import elgatopro300.bbs_cml.camera.clips.CameraClip;
import elgatopro300.bbs_cml.camera.clips.CameraClipContext;
import elgatopro300.bbs_cml.camera.data.Point;
import elgatopro300.bbs_cml.camera.data.Position;
import elgatopro300.bbs_cml.camera.values.ValuePoint;
import elgatopro300.bbs_cml.forms.entities.IEntity;
import elgatopro300.bbs_cml.settings.values.numeric.ValueInt;
import elgatopro300.bbs_cml.utils.clips.ClipContext;

import java.util.Collections;
import java.util.List;

/**
 * Abstract entity modifier
 * 
 * Abstract class for any new modifiers which are going to use entity 
 * selector to fetch an entity and apply some modifications to the path 
 * based on the entity.
 */
public abstract class EntityClip extends CameraClip
{
    /**
     * Position which may be used for calculation of relative
     * camera fixture animations
     */
    public Position position = new Position(0, 0, 0, 0, 0);

    public final ValueInt selector = new ValueInt("selector", -1);
    public final ValuePoint offset = new ValuePoint("offset", new Point(0, 0, 0));

    public EntityClip()
    {
        super();

        this.add(this.selector);
        this.add(this.offset);
    }

    public List<IEntity> getEntities(ClipContext context)
    {
        int index = this.selector.get();

        if (context instanceof CameraClipContext cameraClipContext && index >= 0)
        {
            if (cameraClipContext.entities.containsKey(index))
            {
                return Collections.singletonList(cameraClipContext.entities.get(index));
            }
        }

        return Collections.emptyList();
    }
}