package elgatopro300.bbs_cml.cubic.data.animation;

import elgatopro300.bbs_cml.data.IMapSerializable;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.molang.MolangParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Animations implements IMapSerializable
{
    public MolangParser parser;
    public Map<String, Animation> animations = new HashMap<>();

    public Animations(MolangParser parser)
    {
        this.parser = parser;
    }

    public Collection<Animation> getAll()
    {
        return this.animations.values();
    }

    public void add(Animation animation)
    {
        this.animations.put(animation.id, animation);
    }

    public Animation get(String id)
    {
        return this.animations.get(id);
    }

    @Override
    public void fromData(MapType data)
    {
        for (String key : data.keys())
        {
            Animation animation = new Animation(key, this.parser);

            animation.fromData(data.getMap(key));

            this.add(animation);
        }
    }

    @Override
    public void toData(MapType data)
    {
        for (Map.Entry<String, Animation> entry : this.animations.entrySet())
        {
            data.put(entry.getKey(), entry.getValue().toData());
        }
    }
}