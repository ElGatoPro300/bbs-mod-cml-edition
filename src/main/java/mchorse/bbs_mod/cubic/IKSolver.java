package mchorse.bbs_mod.cubic;

import mchorse.bbs_mod.cubic.data.model.IKChain;
import mchorse.bbs_mod.cubic.data.model.Model;
import mchorse.bbs_mod.cubic.data.model.ModelGroup;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.forms.AnchorForm;
import mchorse.bbs_mod.forms.forms.BodyPart;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.utils.MathUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IKSolver
{
    private static final int ITERATIONS = 10;
    private static final float EPSILON = 0.001F;
    private static final Matrix4f globalMat = new Matrix4f();
    private static final Vector3f effectorPos = new Vector3f();
    private static final Vector3f targetPos = new Vector3f();
    private static final Vector3f jointPos = new Vector3f();
    private static final Vector3f targetVector = new Vector3f();
    private static final Vector3f effectorVector = new Vector3f();
    private static final Vector3f cross = new Vector3f();
    private static final Quaternionf rotation = new Quaternionf();
    private static final Quaternionf localRotation = new Quaternionf();
    private static final Vector3f euler = new Vector3f();

    public static void resolveIK(Model model, IEntity entity)
    {
        if (model.ikChains.isEmpty())
        {
            return;
        }

        Map<String, Vector3f> overrides = new HashMap<>();

        // 1. Check for Anchor Forms (Body Part) acting as IK Controllers
        if (entity != null && entity.getForm() != null)
        {
            Form form = entity.getForm();

            for (BodyPart part : form.parts.getAllTyped())
            {
                if (part.getForm() instanceof AnchorForm)
                {
                    AnchorForm anchor = (AnchorForm) part.getForm();
                    String chainName = anchor.ikChain.get();

                    if (!chainName.isEmpty())
                    {
                        ModelGroup bone = model.getGroup(part.bone.get());
                        Matrix4f boneMat = new Matrix4f();

                        if (bone != null)
                        {
                            getGlobalMatrix(bone, boneMat);
                        }
                        else
                        {
                            boneMat.identity();
                        }

                        part.transform.get().setupMatrix(boneMat);
                        anchor.transform.get().setupMatrix(boneMat);

                        Vector3f pos = new Vector3f();

                        boneMat.getTranslation(pos);
                        overrides.put(chainName, pos);
                    }
                }
            }
        }

        // 2. Solve chains
        for (IKChain chain : model.ikChains)
        {
            Vector3f override = overrides.get(chain.name);
            ModelGroup targetGroup = model.getGroup(chain.name);
            
            // Determine the target position
            Vector3f finalTargetPos = new Vector3f();
            if (override != null)
            {
                finalTargetPos.set(override);
            }
            else if (targetGroup != null)
            {
                getGlobalPosition(targetGroup, finalTargetPos);
            }
            else
            {
                continue; // No target found
            }
            
            // Determine Pole Target position
            Vector3f polePos = null;
            if (chain.poleTarget != null && !chain.poleTarget.isEmpty())
            {
                ModelGroup poleGroup = model.getGroup(chain.poleTarget);
                if (poleGroup != null)
                {
                    polePos = new Vector3f();
                    getGlobalPosition(poleGroup, polePos);
                }
            }

            if ("2_bone".equals(chain.solver))
            {
                solveTwoBone(model, chain, finalTargetPos, polePos);
            }
            else
            {
                solveChainCCD(model, chain, finalTargetPos);
            }
        }
    }

    private static void solveTwoBone(Model model, IKChain chain, Vector3f targetPos, Vector3f polePos)
    {
        ModelGroup effector = model.getGroup(chain.effector);
        ModelGroup root = model.getGroup(chain.root);

        if (effector == null || root == null) return;

        // Collect bones: Effector -> Middle -> Root
        List<ModelGroup> bones = new ArrayList<>();
        ModelGroup current = effector;
        while (current != null)
        {
            bones.add(current);
            if (current == root) break;
            current = current.parent;
        }

        // 2-Bone solver requires exactly 3 nodes: Root, Middle, Effector
        // bones list is [Effector, Middle, Root]
        if (bones.size() != 3)
        {
            // Fallback to CCD if chain length is wrong
            solveChainCCD(model, chain, targetPos);
            return;
        }

        ModelGroup mid = bones.get(1);

        // Get Global Positions
        Vector3f rootP = new Vector3f();
        Vector3f midP = new Vector3f();
        Vector3f endP = new Vector3f();

        getGlobalPosition(root, rootP);
        getGlobalPosition(mid, midP);
        getGlobalPosition(effector, endP);

        // Calculate Bone Lengths
        float len1 = rootP.distance(midP);
        float len2 = midP.distance(endP);
        float distToTarget = rootP.distance(targetPos);

        // Law of Cosines
        float dist = Math.min(distToTarget, len1 + len2 - EPSILON);
        dist = Math.max(dist, EPSILON); // Prevent zero division

        float cosAngle1 = (len1 * len1 + dist * dist - len2 * len2) / (2 * len1 * dist);
        float cosAngle2 = (len1 * len1 + len2 * len2 - dist * dist) / (2 * len1 * len2);

        // Clamp cosines
        cosAngle1 = MathUtils.clamp(cosAngle1, -1F, 1F);
        cosAngle2 = MathUtils.clamp(cosAngle2, -1F, 1F);

        float angle1 = (float) Math.acos(cosAngle1);
        float angle2 = (float) Math.acos(cosAngle2); // Interior angle at joint

        // 1. Orientation of Root
        // Vector from Root to Target
        Vector3f rootToTarget = new Vector3f(targetPos).sub(rootP).normalize();
        
        // Step A: Apply Pole Vector (if exists) or default plane
        Vector3f planeNormal = new Vector3f();
        if (polePos != null)
        {
            // Normal defined by Root, Target, Pole
            Vector3f rootToPole = new Vector3f(polePos).sub(rootP);
            rootToTarget.cross(rootToPole, planeNormal);
        }
        else
        {
            // Default plane: Cross(RootToMid, RootToTarget)?
            // Try to maintain current bend plane
            Vector3f currentBend = new Vector3f(midP).sub(rootP);
            rootToTarget.cross(currentBend, planeNormal);
        }
        
        if (planeNormal.lengthSquared() < EPSILON)
        {
            // Degenerate case, pick arbitrary Up
             rootToTarget.cross(new Vector3f(0, 1, 0), planeNormal);
        }
        planeNormal.normalize();
        
        // Step B: Calculate Final Positions of Mid and Effector based on angles and plane
        // We rotate rootToTarget by angle1 around planeNormal to get new Root->Mid vector
        Quaternionf rot1 = new Quaternionf().setAngleAxis(angle1, planeNormal.x, planeNormal.y, planeNormal.z);
        Vector3f newMidDir = new Vector3f(rootToTarget);
        rot1.transform(newMidDir); // This gives direction from Root to Mid
        newMidDir.normalize();
        
        Vector3f newMidPos = new Vector3f(newMidDir).mul(len1).add(rootP);
        
        // For the second bone, it connects NewMid to Target.
        Vector3f newEndDir = new Vector3f(targetPos).sub(newMidPos).normalize();
        
        // Step C: Apply rotations to Bones
        applyBoneAlignment(root, mid, newMidDir);
        applyBoneAlignment(mid, effector, newEndDir);
    }
    
    private static void applyBoneAlignment(ModelGroup bone, ModelGroup child, Vector3f targetDirWorld)
    {
        // Get current global positions
        Vector3f bonePos = new Vector3f();
        Vector3f childPos = new Vector3f();
        getGlobalPosition(bone, bonePos);
        getGlobalPosition(child, childPos);
        
        // Current Vector to Child in World Space
        Vector3f currentDir = new Vector3f(childPos).sub(bonePos).normalize();
        
        // Calculate Rotation needed to go from currentDir to targetDirWorld
        Quaternionf alignRot = new Quaternionf().rotationTo(currentDir, targetDirWorld);
        
        // Apply this World Space rotation to the bone
        // NewGlobal = AlignRot * OldGlobal
        // NewLocal = InvParent * AlignRot * Parent * OldLocal
        
        // Get Parent Matrix
        Matrix4f parentMatrix = new Matrix4f();
        getGlobalMatrix(bone.parent, parentMatrix);
        Quaternionf parentRot = new Quaternionf();
        parentMatrix.getUnnormalizedRotation(parentRot);
        
        // Get Current Local
        Quaternionf localRot = new Quaternionf()
            .rotateZ(MathUtils.toRad(bone.current.rotate.z))
            .rotateY(MathUtils.toRad(bone.current.rotate.y))
            .rotateX(MathUtils.toRad(bone.current.rotate.x));
            
        // Calculate New Local
        Quaternionf newLocal = new Quaternionf(parentRot).invert().mul(alignRot).mul(parentRot).mul(localRot);
        
        // Convert to Euler and Apply
        Vector3f euler = new Vector3f();
        newLocal.getEulerAnglesZYX(euler);
        
        if (!Float.isNaN(euler.x) && !Float.isNaN(euler.y) && !Float.isNaN(euler.z))
        {
            bone.current.rotate.set(
                (float) Math.toDegrees(euler.x),
                (float) Math.toDegrees(euler.y),
                (float) Math.toDegrees(euler.z)
            );
        }
    }

    private static void solveChainCCD(Model model, IKChain chain, Vector3f targetPos)
    {
        ModelGroup target = model.getGroup(chain.name);
        ModelGroup effector = model.getGroup(chain.effector);
        ModelGroup root = model.getGroup(chain.root);

        if (effector == null || root == null) return;
        
        // Build chain
        List<ModelGroup> bones = new ArrayList<>();
        ModelGroup current = effector;
        while (current != null)
        {
            bones.add(current);
            if (current == root) break;
            current = current.parent;
        }
        
        if (bones.isEmpty() || bones.get(bones.size() - 1) != root) return;

        // CCD Solver Loop
        for (int i = 0; i < ITERATIONS; i++)
        {
            // Iterate from parent of effector to Root
            for (int j = 1; j < bones.size(); j++)
            {
                ModelGroup bone = bones.get(j);

                getGlobalPosition(effector, effectorPos);
                float dist = effectorPos.distance(targetPos);
                if (dist < EPSILON) return;

                getGlobalPosition(bone, jointPos);

                effectorVector.set(effectorPos).sub(jointPos).normalize();
                targetVector.set(targetPos).sub(jointPos).normalize();

                float dot = effectorVector.dot(targetVector);
                if (dot >= 1.0F - EPSILON) continue;

                if (dot < -1.0F) dot = -1.0F;
                if (dot > 1.0F) dot = 1.0F;

                float angle = (float) Math.acos(dot);
                
                // Damping
                if (angle > Math.toRadians(20))
                {
                     angle = (float) Math.toRadians(20);
                }

                effectorVector.cross(targetVector, cross).normalize();

                if (cross.lengthSquared() < EPSILON) continue;

                rotation.identity().setAngleAxis(angle, cross.x, cross.y, cross.z);

                Matrix4f parentMatrix = new Matrix4f();
                getGlobalMatrix(bone.parent, parentMatrix);
                
                Quaternionf parentRot = new Quaternionf();
                parentMatrix.getUnnormalizedRotation(parentRot);
                
                Quaternionf localRotation = new Quaternionf()
                    .rotateZ(MathUtils.toRad(bone.current.rotate.z))
                    .rotateY(MathUtils.toRad(bone.current.rotate.y))
                    .rotateX(MathUtils.toRad(bone.current.rotate.x));

                Quaternionf newLocalRot = new Quaternionf(parentRot).invert().mul(rotation).mul(parentRot).mul(localRotation);
                
                newLocalRot.getEulerAnglesZYX(euler);
                
                if (Float.isNaN(euler.x) || Float.isNaN(euler.y) || Float.isNaN(euler.z)) continue;

                bone.current.rotate.set(
                    (float) Math.toDegrees(euler.x),
                    (float) Math.toDegrees(euler.y),
                    (float) Math.toDegrees(euler.z)
                );
            }
        }
    }

    private static void getGlobalPosition(ModelGroup group, Vector3f dest)
    {
        getGlobalMatrix(group, globalMat);
        globalMat.getTranslation(dest);
    }

    private static void getGlobalMatrix(ModelGroup group, Matrix4f dest)
    {
        dest.identity();
        
        if (group == null)
        {
            return;
        }

        // Build stack
        List<ModelGroup> path = new ArrayList<>();
        ModelGroup current = group;
        while (current != null)
        {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        
        for (ModelGroup g : path)
        {
            dest.translate(g.current.translate);

            if (g.current.pivot.x != 0F || g.current.pivot.y != 0F || g.current.pivot.z != 0F)
            {
                dest.translate(g.current.pivot);
            }

            // Apply rotations in correct order: Z * Y * X
            // Use values AS IS (already negated by Animator if needed)
            dest.rotateZ(MathUtils.toRad(g.current.rotate.z));
            dest.rotateY(MathUtils.toRad(g.current.rotate.y));
            dest.rotateX(MathUtils.toRad(g.current.rotate.x));
            
            // Apply second rotation channel if needed (usually 0)
            dest.rotateZ(MathUtils.toRad(g.current.rotate2.z));
            dest.rotateY(MathUtils.toRad(g.current.rotate2.y));
            dest.rotateX(MathUtils.toRad(g.current.rotate2.x));
            
            dest.scale(g.current.scale);

            if (g.current.pivot.x != 0F || g.current.pivot.y != 0F || g.current.pivot.z != 0F)
            {
                dest.translate(-g.current.pivot.x, -g.current.pivot.y, -g.current.pivot.z);
            }
        }
    }
}
