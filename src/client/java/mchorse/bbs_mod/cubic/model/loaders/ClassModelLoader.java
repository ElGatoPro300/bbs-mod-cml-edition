package mchorse.bbs_mod.cubic.model.loaders;

import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.cubic.data.animation.Animations;
import mchorse.bbs_mod.cubic.model.ModelManager;
import mchorse.bbs_mod.cubic.model.bobj.BOBJModel;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.bobj.BOBJArmature;
import mchorse.bbs_mod.bobj.BOBJBone;
import mchorse.bbs_mod.bobj.BOBJLoader;
import org.joml.Matrix4f;
import org.objectweb.asm.ClassReader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads MCreator .class models via ASM bytecode analysis.
 * This is a simplified parser and might not support all MCreator features.
 */
public class ClassModelLoader implements IModelLoader
{
    @Override
    public ModelInstance load(String id, ModelManager models, Link model, Collection<Link> links, MapType config)
    {
        Link classLink = IModelLoader.getLink(model.combine("model.class"), links, ".class");
        
        if (classLink == null)
        {
            for (Link l : links) {
                if (l.path.endsWith(".class")) {
                    classLink = l;
                    break;
                }
            }
        }

        if (classLink == null)
        {
            return null;
        }

        System.out.println("[ClassModelLoader] Found .class model: " + classLink);

        try (InputStream stream = models.provider.getAsset(classLink))
        {
            ClassReader reader = new ClassReader(stream);
            ModelClassVisitor visitor = new ModelClassVisitor();
            reader.accept(visitor, 0);
            
            System.out.println("[ClassModelLoader] Parsed class: " + visitor.className);
            System.out.println("[ClassModelLoader] Found potential parts: " + visitor.partNames);
            
            // TODO: Construct actual model from visitor data
            // This requires mapping the extracted ASM instructions to BOBJ/Cubic structure
            // For now, we return null as this requires a full decompiler logic
            return null; 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static class ModelClassVisitor extends ClassVisitor
    {
        public String className;
        public List<String> partNames = new ArrayList<>();
        
        public ModelClassVisitor()
        {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
        {
            this.className = name;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
        {
            // Scan constructor and createBodyLayer for part definitions
            if (name.equals("<init>") || name.equals("createBodyLayer") || name.equals("getTexturedModelData"))
            {
                return new ModelMethodVisitor(this);
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private static class ModelMethodVisitor extends MethodVisitor
    {
        private final ModelClassVisitor classVisitor;

        public ModelMethodVisitor(ModelClassVisitor classVisitor)
        {
            super(Opcodes.ASM9);
            this.classVisitor = classVisitor;
        }

        @Override
        public void visitLdcInsn(Object value)
        {
            // Capture string constants which are likely model part names (e.g., "head", "body")
            if (value instanceof String)
            {
                String str = (String) value;
                if (!str.isEmpty() && str.length() < 20 && !str.contains("/"))
                {
                    classVisitor.partNames.add(str);
                }
            }
            super.visitLdcInsn(value);
        }
    }
}
