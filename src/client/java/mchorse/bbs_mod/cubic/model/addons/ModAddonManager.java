package mchorse.bbs_mod.cubic.model.addons;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.data.DataToString;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.resources.Link;

import mchorse.bbs_mod.utils.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModAddonManager
{
    public static final String ADDON_FOLDER = "modaddon";
    public static final Map<String, AddonInfo> REGISTERED_ADDONS = new HashMap<>();

    /**
     * Scans the assets/modaddon folder for potential models and addons.
     */
    public static List<Link> scanAddons()
    {
        List<Link> addons = new ArrayList<>();
        REGISTERED_ADDONS.clear();
        
        try
        {
            Collection<Link> links = BBSMod.getProvider().getLinksFromPath(Link.assets(ADDON_FOLDER), true);
            
            for (Link link : links)
            {
                if (validate(link))
                {
                    addons.add(link);
                    registerAddon(link);
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to scan modaddon folder: " + e.getMessage());
        }

        return addons;
    }
    
    private static void registerAddon(Link link)
    {
        String name = link.path.substring(link.path.lastIndexOf("/") + 1);
        String type = name.substring(name.lastIndexOf(".") + 1);
        
        AddonInfo info = new AddonInfo(name, type, link);
        
        // Try to find metadata
        Link metaLink = new Link(link.source, link.path.substring(0, link.path.lastIndexOf(".")) + ".json");
        try (InputStream stream = BBSMod.getProvider().getAsset(metaLink))
        {
            if (stream != null)
            {
                String json = IOUtils.readText(stream);
                BaseType data = DataToString.fromString(json);
                if (data instanceof MapType)
                {
                    info.parseMetadata((MapType) data);
                }
            }
        }
        catch (Exception e) {}
        
        if (checkCompatibility(info))
        {
            REGISTERED_ADDONS.put(link.toString(), info);
            System.out.println("[ModAddonManager] Registered addon: " + info.name + " (" + info.type + ")");
        }
        else
        {
            System.err.println("[ModAddonManager] Addon " + info.name + " is incompatible!");
        }
    }
    
    /**
     * Validates an addon resource.
     */
    public static boolean validate(Link link)
    {
        String path = link.path.toLowerCase();
        return path.endsWith(".class") || path.endsWith(".geo") || 
               path.endsWith(".bbs") || path.endsWith(".json");
    }
    
    public static boolean checkCompatibility(AddonInfo info)
    {
        // Check MC version
        if (info.mcVersion != null && !info.mcVersion.isEmpty())
        {
            // Simple check, in reality we would check against current MC version
            // For now, assume 1.20.4
            if (!info.mcVersion.equals("1.20.4") && !info.mcVersion.equals("*"))
            {
                return false;
            }
        }
        
        return true;
    }
    
    public static class AddonInfo
    {
        public String name;
        public String type;
        public Link link;
        public String version = "1.0.0";
        public String mcVersion = "*";
        public String author = "Unknown";
        public List<String> dependencies = new ArrayList<>();
        
        public AddonInfo(String name, String type, Link link)
        {
            this.name = name;
            this.type = type;
            this.link = link;
        }
        
        public void parseMetadata(MapType data)
        {
            if (data.has("version")) this.version = data.getString("version");
            if (data.has("mc_version")) this.mcVersion = data.getString("mc_version");
            if (data.has("author")) this.author = data.getString("author");
            
            if (data.has("dependencies")) // Simplified list check
            {
                // In a real implementation we would parse list
            }
        }
    }
}
