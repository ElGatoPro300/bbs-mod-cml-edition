package elgatopro300.bbs_cml.cubic.model.loaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.cubic.ModelInstance;
import elgatopro300.bbs_cml.cubic.data.animation.Animations;
import elgatopro300.bbs_cml.cubic.data.model.Model;
import elgatopro300.bbs_cml.cubic.geo.GeoAnimationParser;
import elgatopro300.bbs_cml.cubic.geo.GeoModelParser;
import elgatopro300.bbs_cml.cubic.model.ModelManager;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.utils.IOUtils;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class GeoCubicModelLoader implements IModelLoader
{
    @Override
    public ModelInstance load(String id, ModelManager models, Link model, Collection<Link> links, MapType config)
    {
        Collection<Link> recursiveLinks = BBSMod.getProvider().getLinksFromPath(model, true);
        List<Link> modelGeo = IModelLoader.getLinks(links, ".geo.json");
        List<Link> modelAnimation = IModelLoader.getLinks(recursiveLinks, ".animation.json");
        Link modelTexture = IModelLoader.getLink(model.combine("model.png"), recursiveLinks, ".png");

        if (modelGeo.isEmpty())
        {
            return null;
        }

        try (InputStream geoStream = BBSMod.getProvider().getAsset(modelGeo.get(0)))
        {
            JsonObject modelJson = JsonParser.parseString(IOUtils.readText(geoStream)).getAsJsonObject();
            Animations modelAnimations = new Animations(models.parser);

            for (Link link : modelAnimation)
            {
                try (InputStream stream = BBSMod.getProvider().getAsset(link))
                {
                    JsonObject jsonObject = JsonParser.parseString(IOUtils.readText(stream)).getAsJsonObject();
                    JsonObject animationsJson = jsonObject.get("animations").getAsJsonObject();

                    for (String key : animationsJson.keySet())
                    {
                        JsonObject animation = animationsJson.getAsJsonObject(key);

                        modelAnimations.animations.put(key, GeoAnimationParser.parse(models.parser, key, animation));
                    }
                }
                catch (Exception e)
                {
                    System.err.println("Failed to load Bedrock entity .animation.json for model: " + model + " in " + link);
                }
            }

            Model modelModel = GeoModelParser.parse(modelJson, models.parser);
            ModelInstance newModel = new ModelInstance(id, modelModel, modelAnimations, modelTexture);

            if (modelModel.topGroups.isEmpty())
            {
                return null;
            }

            newModel.applyConfig(config);

            return newModel;
        }
        catch (Exception e)
        {
            System.err.println("Failed to load Bedrock entity .geo.json for model: " + model);

            e.printStackTrace();
        }

        return null;
    }
}