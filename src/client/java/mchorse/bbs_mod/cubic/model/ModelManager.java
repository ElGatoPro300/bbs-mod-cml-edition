package mchorse.bbs_mod.cubic.model;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.cubic.MolangHelper;
import mchorse.bbs_mod.cubic.model.addons.ModAddonManager;
import mchorse.bbs_mod.cubic.model.loaders.BOBJModelLoader;
import mchorse.bbs_mod.cubic.model.loaders.ClassModelLoader;
import mchorse.bbs_mod.cubic.model.loaders.CubicModelLoader;
import mchorse.bbs_mod.cubic.model.loaders.GeoCubicModelLoader;
import mchorse.bbs_mod.cubic.model.loaders.GLTFModelLoader;
import mchorse.bbs_mod.cubic.model.loaders.IModelLoader;
import mchorse.bbs_mod.cubic.model.loaders.MiModelLoader;
import mchorse.bbs_mod.cubic.model.loaders.VoxModelLoader;
import mchorse.bbs_mod.data.DataStorageUtils;
import mchorse.bbs_mod.data.DataToString;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.math.molang.MolangParser;
import mchorse.bbs_mod.resources.AssetProvider;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.IOUtils;
import mchorse.bbs_mod.utils.StringUtils;
import mchorse.bbs_mod.utils.pose.PoseManager;
import mchorse.bbs_mod.utils.pose.ShapeKeysManager;
import mchorse.bbs_mod.utils.watchdog.IWatchDogListener;
import mchorse.bbs_mod.utils.watchdog.WatchDogEvent;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModelManager implements IWatchDogListener
{
    public static final String MODELS_PREFIX = "models/";

    public final Map<String, ModelInstance> models = new HashMap<>();
    public final List<IModelLoader> loaders = new ArrayList<>();
    public final AssetProvider provider;
    public final MolangParser parser;

    private ModelLoader loader = new ModelLoader(this);

    public ModelManager(AssetProvider provider)
    {
        this.provider = provider;
        this.parser = new MolangParser();

        MolangHelper.registerVars(this.parser);

        this.setupLoaders();
    }

    private void setupLoaders()
    {
        this.loaders.clear();
        this.loaders.add(new BOBJModelLoader());
        this.loaders.add(new CubicModelLoader());
        this.loaders.add(new GeoCubicModelLoader());
        this.loaders.add(new VoxModelLoader());
        this.loaders.add(new GLTFModelLoader());
        this.loaders.add(new MiModelLoader());
        this.loaders.add(new ClassModelLoader());
    }

    /**
     * Get all models that can be loaded by
     */
    public List<String> getAvailableKeys()
    {
        List<Link> models = new ArrayList<>(BBSMod.getProvider().getLinksFromPath(Link.assets("models"), true));
        models.addAll(ModAddonManager.scanAddons());
        Set<String> keys = new HashSet<>();

        models.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));

        for (Link link : models)
        {
            if (this.isRelodable(link))
            {
                String path = link.path;

                if (path.startsWith(MODELS_PREFIX))
                {
                    int slash = path.indexOf('/');
                    int lastSlash = path.lastIndexOf('/');
    
                    if (slash != lastSlash)
                    {
                        path = path.substring(slash + 1, lastSlash);
    
                        keys.add(path);
                    }
                }
                else if (path.startsWith(ModAddonManager.ADDON_FOLDER + "/"))
                {
                    String relative = path.substring(ModAddonManager.ADDON_FOLDER.length() + 1);
                    int slash = relative.indexOf('/');
                    
                    if (slash != -1)
                    {
                        // It is a folder: modaddon/folder/file
                        keys.add(relative.substring(0, slash));
                    }
                    else
                    {
                        // It is a flat file: modaddon/file.ext
                        String name = StringUtils.fileName(path);
                        
                        if (name.contains("."))
                        {
                            name = StringUtils.removeExtension(name);
                        }
                        
                        keys.add(name);
                    }
                }
            }
        }

        return new ArrayList<>(keys);
    }

    public ModelInstance getModel(String id)
    {
        if (this.models.containsKey(id))
        {
            return this.models.get(id);
        }

        this.models.put(id, null);
        this.loader.add(id);

        return null;
    }

    public ModelInstance loadModel(String id)
    {
        ModelInstance model = null;
        Link modelLink = Link.assets(MODELS_PREFIX + id);
        Collection<Link> links = this.provider.getLinksFromPath(modelLink, true);

        if (links.isEmpty())
        {
            Link addonLink = Link.assets(ModAddonManager.ADDON_FOLDER + "/" + id);
            Collection<Link> addonLinks = this.provider.getLinksFromPath(addonLink, true);

            if (!addonLinks.isEmpty())
            {
                modelLink = addonLink;
                links = addonLinks;
            }
            else
            {
                /* Try to find a flat file in modaddon folder that matches the ID */
                List<Link> allAddons = ModAddonManager.scanAddons();
                
                for (Link link : allAddons)
                {
                    String filename = StringUtils.fileName(link.path);
                    
                    if (filename.contains("."))
                    {
                        filename = StringUtils.removeExtension(filename);
                    }
                    
                    if (filename.equals(id) && this.isRelodable(link))
                    {
                        modelLink = link;
                        links = new ArrayList<>();
                        links.add(link);
                        
                        /* Also try to find config */
                        Link config = Link.assets(ModAddonManager.ADDON_FOLDER + "/" + id + ".json");
                        
                        if (this.provider.hasAsset(config))
                        {
                            links.add(config);
                        }
                        
                        break;
                    }
                }
            }
        }

        MapType config = this.loadConfig(modelLink);

        for (IModelLoader loader : this.loaders)
        {
            model = loader.load(id, this, modelLink, links, config);

            if (model != null)
            {
                break;
            }
        }

        if (model == null)
        {
            System.err.println("Model \"" + id + "\" wasn't loaded properly, or was loaded with no top level groups!");
        }
        else
        {
            System.out.println("Model \"" + id + "\" was loaded!");

            model.setup();
        }

        this.models.put(id, model);

        return model;
    }

    private MapType loadConfig(Link modelLink)
    {
        try (InputStream asset = this.provider.getAsset(modelLink.combine("config.json")))
        {
            String string = IOUtils.readText(asset);

            return (MapType) DataToString.fromString(string);
        }
        catch (Exception e)
        {}

        return null;
    }

    public void saveConfig(String id, MapType data)
    {
        Link link = Link.assets(MODELS_PREFIX + id + "/config.json");
        java.io.File file = this.provider.getFile(link);

        DataToString.writeSilently(file, data, true);
    }

    public void renameModel(String id, String name)
    {
        java.io.File folder = this.provider.getFile(Link.assets(MODELS_PREFIX + id));
        java.io.File newFolder = this.provider.getFile(Link.assets(MODELS_PREFIX + name));
        
        if (folder != null && folder.exists() && newFolder != null)
        {
            folder.renameTo(newFolder);
        }
    }

    public void reload()
    {
        for (ModelInstance model : this.models.values())
        {
            if (model != null)
            {
                model.delete();
            }
        }

        this.models.clear();
        PoseManager.INSTANCE.clear();
        ShapeKeysManager.INSTANCE.clear();
        this.setupLoaders();
    }

    public boolean isRelodable(Link link)
    {
        if (!link.path.startsWith(MODELS_PREFIX) && !link.path.startsWith(ModAddonManager.ADDON_FOLDER + "/"))
        {
            return false;
        }

        if (link.path.contains("/animations/") || link.path.contains("/shapes/"))
        {
            return false;
        }

        return link.path.endsWith(".bbs.json")
            || link.path.endsWith(".geo.json")
            || link.path.endsWith(".bobj")
            || link.path.endsWith(".obj")
            || link.path.endsWith(".gltf")
            || link.path.endsWith(".glb")
            || link.path.endsWith(".mimodel")
            || link.path.endsWith(".class")
            || link.path.endsWith(".animation.json")
            || link.path.endsWith(".vox")
            || link.path.endsWith("/config.json");
    }

    /**
     * Watch dog listener implementation. This is a pretty bad hardcoded
     * solution that would only work for the cubic model loader.
     */
    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        Link link = BBSMod.getProvider().getLink(path.toFile());

        if (link == null)
        {
            return;
        }

        if (this.isRelodable(link))
        {
            String key = "";
            
            if (link.path.startsWith(MODELS_PREFIX))
            {
                key = StringUtils.parentPath(link.path.substring(MODELS_PREFIX.length()));
            }
            else if (link.path.startsWith(ModAddonManager.ADDON_FOLDER + "/"))
            {
                String relative = link.path.substring(ModAddonManager.ADDON_FOLDER.length() + 1);
                int slash = relative.indexOf('/');
                
                if (slash != -1)
                {
                    key = relative.substring(0, slash);
                }
                else
                {
                    key = StringUtils.fileName(link.path);
                    
                    if (key.contains("."))
                    {
                        key = StringUtils.removeExtension(key);
                    }
                }
            }

            ModelInstance model = this.models.remove(key);

            if (model != null)
            {
                model.delete();
            }
        }
    }
}