package elgatopro300.bbs_cml.events;

import elgatopro300.bbs_cml.blocks.entities.TriggerBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface TriggerBlockEntityUpdateCallback
{
    public static Event<TriggerBlockEntityUpdateCallback> EVENT = EventFactory.createArrayBacked(TriggerBlockEntityUpdateCallback.class, (listeners) ->
    {
        return (entity) ->
        {
            for (TriggerBlockEntityUpdateCallback listener : listeners)
            {
                listener.update(entity);
            }
        };
    });

    public void update(TriggerBlockEntity entity);
}
