package mchorse.bbs_mod.cubic;

import mchorse.bbs_mod.cubic.data.animation.Animations;
import mchorse.bbs_mod.cubic.data.model.IKChain;
import mchorse.bbs_mod.cubic.data.model.Model;
import mchorse.bbs_mod.data.DataToString;
import mchorse.bbs_mod.data.IMapSerializable;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ListType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.math.molang.MolangParser;
import mchorse.bbs_mod.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class CubicLoader
{
    public static MapType loadFile(InputStream stream)
    {
        try
        {
            return DataToString.mapFromString(loadStringFile(stream));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String loadStringFile(InputStream stream) throws IOException
    {
        String content = IOUtils.readText(stream);

        stream.close();

        return content;
    }

    public LoadingInfo load(MolangParser parser, InputStream stream, String path)
    {
        LoadingInfo info = new LoadingInfo();

        try
        {
            MapType root = loadFile(stream);

            if (root.has("model"))
            {
                info.model = new Model(parser);
                info.model.fromData(root.getMap("model"));

                if (root.has("kinematics"))
                {
                    MapType kinematics = root.getMap("kinematics");

                    if (kinematics.has("ik_chains"))
                    {
                        for (BaseType chainData : kinematics.getList("ik_chains"))
                        {
                            if (chainData instanceof MapType)
                            {
                                IKChain chain = new IKChain();

                                chain.fromData((MapType) chainData);
                                info.model.ikChains.add(chain);
                            }
                        }
                    }
                }

                info.model.initialize();
            }

            if (root.has("animations"))
            {
                info.animations = new Animations(parser);
                info.animations.fromData(root.getMap("animations"));
            }
        }
        catch (Exception e)
        {
            System.err.println("An error happened when parsing BBS model file: " + path);
            e.printStackTrace();
        }

        return info;
    }

    public static MapType toData(IModelInstance model)
    {
        MapType data = new MapType();

        if (model.getModel() instanceof IMapSerializable serializable)
        {
            data.put("model", serializable.toData());
        }

        if (model.getModel() instanceof Model)
        {
            Model m = (Model) model.getModel();

            if (!m.ikChains.isEmpty())
            {
                MapType kinematics = new MapType();
                ListType chains = new ListType();

                for (IKChain chain : m.ikChains)
                {
                    MapType chainData = new MapType();

                    chain.toData(chainData);
                    chains.add(chainData);
                }

                kinematics.put("ik_chains", chains);
                data.put("kinematics", kinematics);
            }
        }

        if (model.getAnimations() != null)
        {
            data.put("animations", model.getAnimations().toData());
        }

        return data;
    }

    public static class LoadingInfo
    {
        public Animations animations;
        public Model model;
    }
}
