package mchorse.bbs_mod.cubic.model.loaders;

import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.cubic.data.animation.Animations;
import mchorse.bbs_mod.cubic.data.model.Model;
import mchorse.bbs_mod.cubic.data.model.ModelCube;
import mchorse.bbs_mod.cubic.data.model.ModelGroup;
import mchorse.bbs_mod.cubic.model.ModelManager;
import mchorse.bbs_mod.data.DataStorageUtils;
import mchorse.bbs_mod.data.DataToString;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ListType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.IOUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.InputStream;
import java.util.Collection;

public class MiModelLoader implements IModelLoader
{
    @Override
    public ModelInstance load(String id, ModelManager models, Link model, Collection<Link> links, MapType config)
    {
        System.out.println("[MiModelLoader] Loading model: " + id);
        Link miLink = null;

        for (Link l : links)
        {
            if (l.path.endsWith(".mimodel"))
            {
                miLink = l;
                break;
            }
        }

        if (miLink == null)
        {
            System.err.println("[MiModelLoader] No .mimodel file found in links for: " + id);
            return null;
        }

        System.out.println("[MiModelLoader] Found .mimodel file: " + miLink.toString());

        try (InputStream stream = models.provider.getAsset(miLink))
        {
            String text = IOUtils.readText(stream);
            BaseType data = DataToString.fromString(text);

            if (data.isMap())
            {
                MapType map = data.asMap();
                Model cubicModel = new Model(models.parser);
                
                // Parse texture size
                if (map.has("texture_size"))
                {
                    ListType size = map.getList("texture_size");
                    if (size.size() >= 2)
                    {
                        cubicModel.textureWidth = size.getInt(0);
                        cubicModel.textureHeight = size.getInt(1);
                        System.out.println("[MiModelLoader] Texture size: " + cubicModel.textureWidth + "x" + cubicModel.textureHeight);
                    }
                }
                
                // Parse parts
                if (map.has("parts"))
                {
                    for (BaseType part : map.getList("parts"))
                    {
                        if (part.isMap())
                        {
                            ModelGroup group = this.parseGroup(part.asMap(), cubicModel, null);
                            if (group != null)
                            {
                                cubicModel.topGroups.add(group);
                            }
                        }
                    }
                }
                
                cubicModel.initialize();
                
                // Find texture
                Link texture = null;
                if (map.has("texture"))
                {
                    String textureName = map.getString("texture");
                    System.out.println("[MiModelLoader] Looking for texture: " + textureName);
                    // Try to find matching png in links
                    for (Link l : links)
                    {
                        if (l.path.endsWith(textureName))
                        {
                            texture = l;
                            System.out.println("[MiModelLoader] Found texture: " + l.toString());
                            break;
                        }
                    }
                    if (texture == null) {
                         System.err.println("[MiModelLoader] Texture not found in links!");
                    }
                }
                
                ModelInstance instance = new ModelInstance(id, cubicModel, new Animations(models.parser), texture);
                instance.applyConfig(config);
                
                return instance;
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to load .mimodel: " + miLink);
            e.printStackTrace();
        }

        return null;
    }

    private ModelGroup parseGroup(MapType data, Model model, ModelGroup parent)
    {
        String name = data.getString("name");
        System.out.println("[MiModelLoader] Parsing group: " + name);
        ModelGroup group = new ModelGroup(name);
        
        // Position
        if (data.has("position"))
        {
            Vector3f pos = DataStorageUtils.vector3fFromData(data.getList("position"));
            group.initial.translate.set(pos);
            group.initial.pivot.set(pos); 
        }
        
        // Rotation
        if (data.has("rotation"))
        {
             Vector3f rot = DataStorageUtils.vector3fFromData(data.getList("rotation"));
             group.initial.rotate.set(rot);
        }
        
        // Shapes (Cubes)
        if (data.has("shapes"))
        {
            for (BaseType shapeData : data.getList("shapes"))
            {
                if (shapeData.isMap())
                {
                    ModelCube cube = this.parseCube(shapeData.asMap());
                    if (cube != null)
                    {
                        group.cubes.add(cube);
                    }
                }
            }
        }
        
        // Children parts
        if (data.has("parts"))
        {
            for (BaseType part : data.getList("parts"))
            {
                if (part.isMap())
                {
                    ModelGroup child = this.parseGroup(part.asMap(), model, group);
                    if (child != null)
                    {
                        group.children.add(child);
                    }
                }
            }
        }
        
        return group;
    }

    private ModelCube parseCube(MapType data)
    {
        String type = data.has("type") ? data.getString("type") : "unknown";
        System.out.println("[MiModelLoader] Parsing cube type: " + type);

        ModelCube cube = new ModelCube();
        
        // From/To -> Origin/Size
        Vector3f from = new Vector3f();
        Vector3f to = new Vector3f();
        
        if (data.has("from")) from.set(DataStorageUtils.vector3fFromData(data.getList("from")));
        if (data.has("to")) to.set(DataStorageUtils.vector3fFromData(data.getList("to")));
        
        // Calculate size and origin
        // 'from' is min, 'to' is max usually.
        // Size = to - from
        cube.size.set(to).sub(from);
        cube.origin.set(from);
        
        System.out.println("[MiModelLoader] Cube size: " + cube.size + ", origin: " + cube.origin);

        // Position/Rotation/Scale
        if (data.has("position")) 
        {
            Vector3f pos = DataStorageUtils.vector3fFromData(data.getList("position"));
            cube.origin.add(pos);
        }
        
        if (data.has("rotation"))
        {
            cube.rotate.set(DataStorageUtils.vector3fFromData(data.getList("rotation")));
        }
        
        if (data.has("scale"))
        {
            Vector3f scale = DataStorageUtils.vector3fFromData(data.getList("scale"));
            cube.size.mul(scale);
             System.out.println("[MiModelLoader] Applied scale: " + scale + " -> New size: " + cube.size);
        }
        
        // UV setup BEFORE inflating for planes to ensure correct mapping
        if (data.has("uv"))
        {
            ListType uvList = data.getList("uv");
            if (uvList.size() >= 2)
            {
                Vector2f uv = new Vector2f(uvList.getFloat(0), uvList.getFloat(1));
                cube.setupBoxUV(uv, false);
            }
        }

        // If it's a plane, we might need to inflate it slightly to be visible
        if (type.equals("plane"))
        {
             System.out.println("[MiModelLoader] Detected flat plane, checking dimensions...");
             // Enforce minimum thickness for visibility
             float minThickness = 0.01f;
             if (Math.abs(cube.size.x) < minThickness) cube.size.x = minThickness;
             if (Math.abs(cube.size.y) < minThickness) cube.size.y = minThickness;
             if (Math.abs(cube.size.z) < minThickness) cube.size.z = minThickness;
             System.out.println("[MiModelLoader] Adjusted plane size to: " + cube.size);
        }
        
        return cube;
    }
}
