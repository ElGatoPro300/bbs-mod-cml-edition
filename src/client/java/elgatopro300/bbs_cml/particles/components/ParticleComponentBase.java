package elgatopro300.bbs_cml.particles.components;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;

public abstract class ParticleComponentBase
{
    public BaseType toData()
    {
        MapType data = new MapType();

        this.toData(data);

        return data;
    }

    protected void toData(MapType data)
    {}

    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        return this;
    }

    public boolean canBeEmpty()
    {
        return false;
    }
}