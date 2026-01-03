package mchorse.bbs_mod.utils.resources;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.resources.ISourcePack;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.DataPath;
import mchorse.bbs_mod.utils.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MinecraftSourcePack implements ISourcePack
{
    private Map<String, Object> links = new HashMap<>();
    private boolean initialized = false;
    private String prefix = "minecraft";

    public MinecraftSourcePack()
    {
    }

    public MinecraftSourcePack(String prefix)
    {
        this.prefix = prefix;
    }

    private ResourceManager getManager()
    {
        return MinecraftClient.getInstance().getResourceManager();
    }
    
    private ResourceManager getEffectiveManager(Link link)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        // Check if we are in a world and if the link points to a structure (data pack resource)
        // Structures are usually under "structures/" path or have .nbt extension
        if (mc.getServer() != null && (link.path.startsWith("structures/") || link.path.endsWith(".nbt")))
        {
            return mc.getServer().getResourceManager();
        }
        
        return this.getManager();
    }

    public void setupPaths()
    {
        ResourceManager manager = this.getManager();
        
        if (manager == null)
        {
            return;
        }

        Map<Identifier, List<Resource>> map = manager.findAllResources("textures", (l) -> l.getNamespace().equals("minecraft") && l.getPath().endsWith(".png"));

        for (Identifier id : map.keySet())
        {
            DataPath path = new DataPath(id.getPath());

            this.insert(path);
        }
        
        this.initialized = true;
    }
    
    private void ensureInitialized()
    {
        if (!this.initialized && this.getManager() != null)
        {
            this.setupPaths();
        }
    }

    private void insert(DataPath path)
    {
        Map<String, Object> links = this.links;

        for (String string : path.strings)
        {
            if (string.endsWith(".png"))
            {
                links.put(string, string);

                return;
            }
            else
            {
                if (!links.containsKey(string))
                {
                    links.put(string, new HashMap<>());
                }

                links = (Map<String, Object>) links.get(string);
            }
        }
    }


    @Override
    public String getPrefix()
    {
        return this.prefix;
    }

    private Identifier getIdentifier(Link link)
    {
        String namespace = link.source;
        String path = link.path;

        if (Link.isAssets(link))
        {
            namespace = BBSMod.MOD_ID;
            path = "assets/" + path;
        }

        return Identifier.of(namespace, path);
    }

    @Override
    public boolean hasAsset(Link link)
    {
        this.ensureInitialized();
        
        Identifier id = this.getIdentifier(link);
        ResourceManager effectiveManager = this.getEffectiveManager(link);
        
        if (effectiveManager == null)
        {
            return false;
        }
        
        if (effectiveManager.getResource(id).isPresent())
        {
            return true;
        }
        
        // Try prepending "structures/" if missing and it looks like a structure
        if (!link.path.startsWith("structures/") && link.path.endsWith(".nbt"))
        {
             Identifier structureId = Identifier.of(id.getNamespace(), "structures/" + id.getPath());
             if (effectiveManager.getResource(structureId).isPresent())
             {
                 return true;
             }
        }
        
        return false;
    }

    @Override
    public InputStream getAsset(Link link) throws IOException
    {
        this.ensureInitialized();
        
        Identifier id = this.getIdentifier(link);
        ResourceManager effectiveManager = this.getEffectiveManager(link);
        
        if (effectiveManager == null)
        {
            return null;
        }
        
        Optional<Resource> resource = effectiveManager.getResource(id);

        // Try prepending "structures/" if missing and it looks like a structure
        if (resource.isEmpty() && !link.path.startsWith("structures/") && link.path.endsWith(".nbt"))
        {
             Identifier structureId = Identifier.of(id.getNamespace(), "structures/" + id.getPath());
             resource = effectiveManager.getResource(structureId);
        }

        if (resource.isPresent())
        {
            return resource.get().getInputStream();
        }

        return null;
    }

    @Override
    public File getFile(Link link)
    {
        return null;
    }

    @Override
    public Link getLink(File file)
    {
        return null;
    }

    @Override
    public void getLinksFromPath(Collection<Link> links, Link link, boolean recursive)
    {
        this.ensureInitialized();
        
        String path = link.path.endsWith("/") ? link.path.substring(0, link.path.length() - 1) : link.path;
        Map<String, Object> allLinks = this.findBasePath(path);

        if (allLinks != null)
        {
            this.traverse(links, path, allLinks, recursive);
        }
    }

    private Map<String, Object> findBasePath(String path)
    {
        if (path.isEmpty())
        {
            return this.links;
        }

        DataPath dataPath = new DataPath(path);
        Map<String, Object> map = this.links;

        for (String next : dataPath.strings)
        {
            Object o = map.get(next);

            if (o instanceof Map)
            {
                map = (Map<String, Object>) o;
            }
            else
            {
                return null;
            }
        }

        return map;
    }

    private void traverse(Collection<Link> links, String path, Map<String, Object> allLinks, boolean recursive)
    {
        for (Map.Entry<String, Object> entry : allLinks.entrySet())
        {
            if (entry.getValue() instanceof Map)
            {
                if (recursive)
                {
                    this.traverse(links, StringUtils.combinePaths(path, entry.getKey()), (Map<String, Object>) entry.getValue(), recursive);
                }

                links.add(new Link(this.getPrefix(), StringUtils.combinePaths(path, entry.getKey()) + "/"));
            }
            else
            {
                links.add(new Link(this.getPrefix(), StringUtils.combinePaths(path, entry.getKey())));
            }
        }
    }
}
