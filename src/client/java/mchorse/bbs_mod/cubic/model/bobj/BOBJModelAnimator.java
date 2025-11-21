package mchorse.bbs_mod.cubic.model.bobj;

import mchorse.bbs_mod.bobj.BOBJBone;
import mchorse.bbs_mod.cubic.CubicModelAnimator;
import mchorse.bbs_mod.cubic.data.animation.Animation;
import mchorse.bbs_mod.cubic.data.animation.AnimationPart;
import mchorse.bbs_mod.utils.interps.Lerps;
import mchorse.bbs_mod.utils.pose.Transform;
import org.joml.Vector3d;

public class BOBJModelAnimator
{
    private static Vector3d p = new Vector3d();
    private static Vector3d s = new Vector3d();
    private static Vector3d r = new Vector3d();

    public static void animate(BOBJModel model, Animation animation, float frame, float blend, boolean skipInitial)
    {
        for (BOBJBone orderedBone : model.getArmature().orderedBones)
        {
            animateGroup(orderedBone, animation, frame, blend, skipInitial);
        }
    }

    private static void animateGroup(BOBJBone group, Animation animation, float frame, float blend, boolean skipInitial)
    {
        boolean applied = false;

        AnimationPart part = animation.parts.get(group.name);

        if (part != null)
        {
            applyGroupAnimation(group, part, frame, blend);

            applied = true;
        }

        if (!applied && !skipInitial)
        {
            Transform initial = Transform.DEFAULT;
            Transform current = group.transform;

            current.translate.lerp(initial.translate, blend);
            current.scale.lerp(initial.scale, blend);
            current.rotate.lerp(initial.rotate, blend);
        }
    }

    private static void applyGroupAnimation(BOBJBone group, AnimationPart animation, float frame, float blend)
    {
        Vector3d position = CubicModelAnimator.interpolateList(p, animation.x, animation.y, animation.z, frame, 0D);
        Vector3d scale = CubicModelAnimator.interpolateList(s, animation.sx, animation.sy, animation.sz, frame, 1D);
        Vector3d rotation = CubicModelAnimator.interpolateList(r, animation.rx, animation.ry, animation.rz, frame, 0D);

        scale.sub(1, 1, 1);

        Transform initial = Transform.DEFAULT;
        Transform current = group.transform;

        current.translate.x = Lerps.lerp(current.translate.x, (float) position.x + initial.translate.x, blend);
        current.translate.y = Lerps.lerp(current.translate.y, (float) position.y + initial.translate.y, blend);
        current.translate.z = Lerps.lerp(current.translate.z, (float) position.z + initial.translate.z, blend);

        current.scale.x = Lerps.lerp(current.scale.x, (float) scale.x + initial.scale.x, blend);
        current.scale.y = Lerps.lerp(current.scale.y, (float) scale.y + initial.scale.y, blend);
        current.scale.z = Lerps.lerp(current.scale.z, (float) scale.z + initial.scale.z, blend);

        current.rotate.x = Lerps.lerp(current.rotate.x, (float) rotation.x + initial.rotate.x, blend);
        current.rotate.y = Lerps.lerp(current.rotate.y, (float) rotation.y + initial.rotate.y, blend);
        current.rotate.z = Lerps.lerp(current.rotate.z, (float) rotation.z + initial.rotate.z, blend);
    }
}
