package mchorse.bbs_mod.cubic.data.model;

import mchorse.bbs_mod.data.IMapSerializable;
import mchorse.bbs_mod.data.types.MapType;

public class IKChain implements IMapSerializable
{
    public String name = "";
    public String root = "";
    public String effector = "";
    public String solver = "2_bone";
    public String poleTarget = "";
    public boolean lockRotation = false;

    @Override
    public void fromData(MapType data)
    {
        this.name = data.getString("name");
        this.root = data.getString("root");
        this.effector = data.getString("effector");
        this.solver = data.getString("solver");
        this.poleTarget = data.getString("pole_target");
        this.lockRotation = data.getBool("lock_rotation");
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("name", this.name);
        data.putString("root", this.root);
        data.putString("effector", this.effector);
        data.putString("solver", this.solver);
        
        if (!this.poleTarget.isEmpty())
        {
            data.putString("pole_target", this.poleTarget);
        }
        
        if (this.lockRotation)
        {
            data.putBool("lock_rotation", this.lockRotation);
        }
    }
}
