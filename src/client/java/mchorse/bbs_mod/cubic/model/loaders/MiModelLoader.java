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
                    Vector3f rootOrigin = new Vector3f();

                    for (BaseType part : map.getList("parts"))
                    {
                        if (part.isMap())
                        {
                            ModelGroup group = this.parseGroup(part.asMap(), cubicModel, null, rootOrigin);
                            if (group != null)
                            {
                                cubicModel.topGroups.add(group);
                            }
                        }
                    }
                }

                if (cubicModel.topGroups.isEmpty()) {
                    System.err.println("[MiModelLoader] WARNING: No top-level groups loaded! Model will be invisible.");
                } else {
                    System.out.println("[MiModelLoader] Loaded " + cubicModel.topGroups.size() + " top-level groups.");
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
                }
                
                // Fallback: Try to find ANY png if explicit texture is missing
                if (texture == null) {
                    System.out.println("[MiModelLoader] Explicit texture not found. Searching for any PNG...");
                    for (Link l : links) {
                        if (l.path.endsWith(".png")) {
                            texture = l;
                            System.out.println("[MiModelLoader] Using fallback texture: " + l.toString());
                            break;
                        }
                    }
                }

                if (texture == null) {
                     System.err.println("[MiModelLoader] CRITICAL: No texture found for model " + id + ". It might render black or invisible.");
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

    private Vector3f swapYZ(Vector3f v)
    {
        return new Vector3f(-v.x, v.y, -v.z);
    }

    private ModelGroup parseGroup(MapType data, Model model, ModelGroup parent, Vector3f parentOrigin)
    {
        String name = data.getString("name");
        System.out.println("[MiModelLoader] Parsing group: " + name);
        ModelGroup group = new ModelGroup(name);
        Vector3f parentOriginAbs = parentOrigin != null ? new Vector3f(parentOrigin) : new Vector3f();
        Vector3f localOrigin = new Vector3f();
        
        if (data.has("position"))
        {
            localOrigin.set(DataStorageUtils.vector3fFromData(data.getList("position")));
        }
        
        Vector3f groupOriginAbs = this.swapYZ(localOrigin).add(parentOriginAbs);
        group.initial.translate.set(groupOriginAbs);
        group.initial.pivot.set(groupOriginAbs);
        
        if (data.has("rotation"))
        {
             Vector3f rot = DataStorageUtils.vector3fFromData(data.getList("rotation"));
             group.initial.rotate.set(this.swapYZ(rot));
        }
        
        // Shapes (Cubes)
        if (data.has("shapes"))
        {
            for (BaseType shapeData : data.getList("shapes"))
            {
                if (shapeData.isMap())
                {
                    ModelCube cube = this.parseCube(shapeData.asMap(), model, groupOriginAbs);
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
                    ModelGroup child = this.parseGroup(part.asMap(), model, group, groupOriginAbs);
                    if (child != null)
                    {
                        group.children.add(child);
                    }
                }
            }
        }
        
        return group;
    }

    private ModelCube parseCube(MapType data, Model model, Vector3f groupOriginAbs)
    {
        String type = data.has("type") ? data.getString("type") : "unknown";

        ModelCube cube = new ModelCube();
        
        Vector3f from = new Vector3f();
        Vector3f to = new Vector3f();
        
        if (data.has("from")) from.set(DataStorageUtils.vector3fFromData(data.getList("from")));
        if (data.has("to")) to.set(DataStorageUtils.vector3fFromData(data.getList("to")));
        
        float inflate = data.has("inflate") ? data.getFloat("inflate") : 0;
        if (inflate != 0)
        {
            from.sub(inflate, inflate, inflate);
            to.add(inflate, inflate, inflate);
        }

        if (data.has("scale"))
        {
            Vector3f scale = DataStorageUtils.vector3fFromData(data.getList("scale"));
            from.mul(scale);
            to.mul(scale);
        }
        
        Vector3f localFrom = this.swapYZ(from);
        Vector3f localTo = this.swapYZ(to);
        
        Vector3f cubeLocalPos = new Vector3f();
        if (data.has("position"))
        {
            cubeLocalPos.set(DataStorageUtils.vector3fFromData(data.getList("position")));
        }
        Vector3f cubeOriginAbs = this.swapYZ(cubeLocalPos).add(groupOriginAbs);
        
        Vector3f minLocal = new Vector3f(
            Math.min(localFrom.x, localTo.x),
            Math.min(localFrom.y, localTo.y),
            Math.min(localFrom.z, localTo.z)
        );
        Vector3f maxLocal = new Vector3f(
            Math.max(localFrom.x, localTo.x),
            Math.max(localFrom.y, localTo.y),
            Math.max(localFrom.z, localTo.z)
        );
        
        Vector3f min = new Vector3f(minLocal).add(cubeOriginAbs);
        Vector3f max = new Vector3f(maxLocal).add(cubeOriginAbs);
        
        cube.size.set(max).sub(min);

        if (cube.size.lengthSquared() < 0.0001f)
        {
            System.out.println("[MiModelLoader] WARNING: Cube has zero size! " + cube.size + " (Original from: " + from + ", to: " + to + ")");
        }
        
        if (data.has("rotation"))
        {
            cube.rotate.set(this.swapYZ(DataStorageUtils.vector3fFromData(data.getList("rotation"))));
        }
        
        if (data.has("uv"))
        {
            ListType uvList = data.getList("uv");
            if (uvList.size() >= 2)
            {
                Vector2f uv = new Vector2f(uvList.getFloat(0), uvList.getFloat(1));
                boolean mirror = data.has("texture_mirror") && data.getBool("texture_mirror");
                cube.setupBoxUV(uv, mirror);
            }
        }
        
        cube.origin.set(min);

        if (type.equals("plane"))
        {
             float minThickness = 0.01f;
             if (Math.abs(cube.size.x) < minThickness) cube.size.x = minThickness;
             if (Math.abs(cube.size.y) < minThickness) cube.size.y = minThickness;
             if (Math.abs(cube.size.z) < minThickness) cube.size.z = minThickness;
        }
        
        cube.pivot.set(cubeOriginAbs);
        if (model.textureWidth > 0 && model.textureHeight > 0)
        {
            cube.generateQuads(model.textureWidth, model.textureHeight);
        }
        
        return cube;
    }
}
