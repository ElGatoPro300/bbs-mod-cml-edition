package mchorse.bbs_mod.cubic.model.loaders;

import mchorse.bbs_mod.bobj.BOBJLoader;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.cubic.data.animation.Animations;
import mchorse.bbs_mod.cubic.model.ModelManager;
import mchorse.bbs_mod.cubic.model.bobj.BOBJModel;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.gltf.GLTFConverter;
import mchorse.bbs_mod.gltf.GLTFParser;
import mchorse.bbs_mod.gltf.data.GLTF;
import mchorse.bbs_mod.resources.Link;

import java.io.InputStream;
import java.util.Base64;
import java.util.Collection;

public class GLTFModelLoader implements IModelLoader
{
    @Override
    public ModelInstance load(String id, ModelManager models, Link model, Collection<Link> links, MapType config)
    {
        Link gltfLink = null;
        
        for (Link l : links)
        {
            if (l.path.endsWith(".gltf") || l.path.endsWith(".glb"))
            {
                gltfLink = l;
                break;
            }
        }
        
        if (gltfLink == null) return null;
        
        Link texture = null;
        for (Link l : links)
        {
            if (l.path.endsWith(".png"))
            {
                texture = l;
                break;
            }
        }
        
        try (InputStream stream = models.provider.getAsset(gltfLink))
        {
            GLTF gltf = GLTFParser.parse(stream);
            
            // Load Buffers
            if (gltf.buffers != null)
            {
                for (GLTF.GLTFBuffer buffer : gltf.buffers)
                {
                    if (buffer.data == null && buffer.uri != null)
                    {
                        if (buffer.uri.startsWith("data:"))
                        {
                            String b64 = buffer.uri.substring(buffer.uri.indexOf(",") + 1);
                            buffer.data = Base64.getDecoder().decode(b64);
                        }
                        else
                        {
                             Link bufferLink = new Link(gltfLink.source, gltfLink.path.substring(0, gltfLink.path.lastIndexOf('/')+1) + buffer.uri);
                             try (InputStream bufStream = models.provider.getAsset(bufferLink))
                             {
                                 buffer.data = bufStream.readAllBytes();
                             }
                        }
                    }
                }
            }
            
            BOBJLoader.BOBJData data = GLTFConverter.convert(gltf);
            
            if (!data.meshes.isEmpty())
            {
                BOBJLoader.BOBJMesh mesh = data.meshes.get(0);
                BOBJLoader.CompiledData compiled = BOBJLoader.compileMesh(data, mesh);
                
                data.initiateArmatures();
                
                BOBJModel bobjModel = new BOBJModel(mesh.armature, compiled, false);
                
                ModelInstance instance = new ModelInstance(id, bobjModel, new Animations(models.parser), texture);
                instance.applyConfig(config);
                
                return instance;
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to load GLTF model: " + gltfLink);
            e.printStackTrace();
        }
        
        return null;
    }
}
