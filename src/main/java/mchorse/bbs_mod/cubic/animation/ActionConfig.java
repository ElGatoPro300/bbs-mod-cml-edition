package mchorse.bbs_mod.cubic.animation;

import mchorse.bbs_mod.data.IMapSerializable;
import mchorse.bbs_mod.data.types.MapType;

import java.util.Objects;

public class ActionConfig implements IMapSerializable
{
    public String name = "";
    public boolean isState = false;
    public boolean loop = true;
    public float speed = 1;
    public float fade = 5;
    public int tick = 0;

    public ActionConfig()
    {}

    public ActionConfig(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof ActionConfig)
        {
            ActionConfig config = (ActionConfig) obj;

            return Objects.equals(this.name, config.name)
                && this.isState == config.isState
                && this.loop == config.loop
                && this.speed == config.speed
                && this.fade == config.fade
                && this.tick == config.tick;
        }

        return false;
    }

    public ActionConfig copy()
    {
        ActionConfig config = new ActionConfig(this.name);

        config.isState = this.isState;
        config.loop = this.loop;
        config.speed = this.speed;
        config.fade = this.fade;
        config.tick = this.tick;

        return config;
    }

    public boolean isDefault(String key)
    {
        return this.isDefault() && this.name.equals(key);
    }

    public boolean isDefault()
    {
        return !this.isState && this.loop && this.speed == 1 && this.fade == 5 && this.tick == 0;
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("name", this.name);
        if (this.isState) data.putBool("isState", this.isState);
        data.putBool("loop", this.loop);
        data.putFloat("speed", this.speed);
        data.putFloat("fade", this.fade);
        data.putInt("tick", this.tick);
    }

    @Override
    public void fromData(MapType data)
    {
        this.name = data.getString("name");
        if (data.has("isState")) this.isState = data.getBool("isState");
        this.loop = data.getBool("loop");
        this.speed = data.getFloat("speed");
        this.fade = data.getFloat("fade");
        this.tick = data.getInt("tick");
    }
}