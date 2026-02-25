package elgatopro300.bbs_cml.forms.forms.utils;

import elgatopro300.bbs_cml.data.IMapSerializable;
import elgatopro300.bbs_cml.data.types.MapType;
import net.minecraft.util.Identifier;

public class ParticleSettings implements IMapSerializable
{
    public Identifier particle = Identifier.of("minecraft", "flame");
    public String arguments = "";

    @Override
    public void toData(MapType data)
    {
        data.putString("particle", this.particle.toString());
        data.putString("args", this.arguments);
    }

    @Override
    public void fromData(MapType data)
    {
        this.particle = Identifier.tryParse(data.getString("particle"));
        this.arguments = data.getString("args");
    }
}