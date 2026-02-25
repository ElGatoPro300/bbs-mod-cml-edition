package elgatopro300.bbs_cml.particles;

import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.utils.manager.BaseManager;
import elgatopro300.bbs_cml.utils.manager.storage.JSONLikeStorage;

import java.io.File;
import java.util.function.Supplier;

public class ParticleManager extends BaseManager<ParticleScheme>
{
    public ParticleManager(Supplier<File> folder)
    {
        super(folder);

        this.storage = new JSONLikeStorage().json();
    }

    @Override
    protected ParticleScheme createData(String id, MapType data)
    {
        ParticleScheme scheme = new ParticleScheme();

        if (data != null)
        {
            try
            {
                System.out.println("Parsing \"" + id + "\" particle effect.");

                ParticleScheme.PARSER.fromData(scheme, data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            scheme.setup();
        }

        return scheme;
    }
}