package mchorse.bbs_mod.bobj;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BOBJArmature
{
    /**
     * Name of this armature 
     */
    public String name;

    /**
     * Map of all bones in this armature 
     */
    public Map<String, BOBJBone> bones = new HashMap<String, BOBJBone>();

    /**
     * List of all bones stored in {@link #bones}, but ordered by index 
     */
    public List<BOBJBone> orderedBones = new ArrayList<BOBJBone>();

    /**
     * Array of matrices which are going to be used for transforming 
     * vertices.
     */
    public Matrix4f[] matrices;

    /**
     * Whether this armature was initialized already 
     */
    private boolean initialized;

    public BOBJArmature(String name)
    {
        this.name = name;
    }

    public void addBone(BOBJBone bone)
    {
        this.bones.put(bone.name, bone);
        this.orderedBones.add(bone);
    }

    /**
     * Initiate the armature. This method is responsible for connecting 
     * parent bones to their children and initializing matrix array. 
     * This method should be invoked only once.
     */
    public void initArmature()
    {
        if (!this.initialized)
        {
            /* "Connect" parent bones to children bones */
            for (BOBJBone bone : this.bones.values())
            {
                if (!bone.parent.isEmpty())
                {
                    bone.parentBone = this.bones.get(bone.parent);
                    bone.relBoneMat.set(bone.parentBone.boneMat);
                    bone.relBoneMat.invert();
                    bone.relBoneMat.mul(bone.boneMat);
                }
                else
                {
                    bone.relBoneMat.set(bone.boneMat);
                }
            }

            /* Sort bones topologically (parents first) to ensure correct matrix calculation order */
            List<BOBJBone> sorted = new ArrayList<>();
            Set<BOBJBone> visited = new HashSet<>();
            
            for (BOBJBone bone : this.bones.values())
            {
                this.sortBone(bone, sorted, visited);
            }
            
            this.orderedBones = sorted;

            int maxIndex = 0;
            for (BOBJBone b : this.orderedBones)
            {
                if (b.index > maxIndex)
                {
                    maxIndex = b.index;
                }
            }
            this.matrices = new Matrix4f[maxIndex + 1];
            this.initialized = true;
        }
    }

    private void sortBone(BOBJBone bone, List<BOBJBone> sorted, Set<BOBJBone> visited)
    {
        if (visited.contains(bone))
        {
            return;
        }

        if (bone.parentBone != null)
        {
            this.sortBone(bone.parentBone, sorted, visited);
        }

        visited.add(bone);
        sorted.add(bone);
    }

    /**
     * Setup matrices  
     */
    public void setupMatrices()
    {
        for (BOBJBone bone : this.orderedBones)
        {
            if (bone.index >= this.matrices.length)
            {
                Matrix4f[] newMatrices = new Matrix4f[bone.index + 1];
                
                System.arraycopy(this.matrices, 0, newMatrices, 0, this.matrices.length);
                this.matrices = newMatrices;
            }
            
            this.matrices[bone.index] = bone.compute();
        }
    }

    public void copyOrder(BOBJArmature armature)
    {
        for (BOBJBone bone : armature.orderedBones)
        {
            BOBJBone thisBone = this.bones.get(bone.name);

            if (thisBone != null)
            {
                thisBone.index = bone.index;
            }
        }

        Collections.sort(this.orderedBones, (o1, o2) -> o1.index - o2.index);
    }

    public BOBJArmature copy()
    {
        BOBJArmature armature = new BOBJArmature(this.name);
        
        for (BOBJBone bone : this.orderedBones)
        {
            armature.addBone(bone.copy());
        }
        
        armature.initArmature();
        armature.setupMatrices();
        
        return armature;
    }
}
