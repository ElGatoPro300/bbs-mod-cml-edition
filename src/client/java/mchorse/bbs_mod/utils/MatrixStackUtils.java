package mchorse.bbs_mod.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import mchorse.bbs_mod.utils.joml.Vectors;
import mchorse.bbs_mod.utils.pose.Transform;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4fStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MatrixStackUtils
{
    private static Matrix3f normal = new Matrix3f();

    private static Matrix4f oldProjection = new Matrix4f();
    private static Matrix4f oldMV = new Matrix4f();
    // In 1.21.1, inverse view rotation matrix is managed internally; no manual cache

    public static void scaleStack(MatrixStack stack, float x, float y, float z)
    {
        stack.peek().getPositionMatrix().scale(x, y, z);
        stack.peek().getNormalMatrix().scale(x < 0F ? -1F : 1F, y < 0F ? -1F : 1F, z < 0F ? -1F : 1F);
    }

    public static void cacheMatrices()
    {
        /* Cache the global stuff */
        oldProjection.set(RenderSystem.getProjectionMatrix());
        oldMV.set(RenderSystem.getModelViewMatrix());
        // Inverse view rotation matrix is handled internally; no explicit retrieval

        Matrix4fStack renderStack = RenderSystem.getModelViewStack();

        renderStack.pushMatrix();
        renderStack.identity();
        RenderSystem.applyModelViewMatrix();
        renderStack.popMatrix();
    }

    public static void restoreMatrices()
    {
        /* Return back to orthographic projection */
        RenderSystem.setProjectionMatrix(oldProjection, VertexSorter.BY_Z);
        // No explicit inverse view rotation matrix restore in 1.21.1

        Matrix4fStack renderStack = RenderSystem.getModelViewStack();

        renderStack.pushMatrix();
        renderStack.identity();
        MatrixStackUtils.multiply(renderStack, oldMV);
        RenderSystem.applyModelViewMatrix();
        renderStack.popMatrix();
    }

    public static void applyTransform(MatrixStack stack, Transform transform)
    {
        stack.translate(transform.translate.x, transform.translate.y, transform.translate.z);
        stack.multiply(RotationAxis.POSITIVE_Z.rotation(transform.rotate.z));
        stack.multiply(RotationAxis.POSITIVE_Y.rotation(transform.rotate.y));
        stack.multiply(RotationAxis.POSITIVE_X.rotation(transform.rotate.x));
        stack.multiply(RotationAxis.POSITIVE_Z.rotation(transform.rotate2.z));
        stack.multiply(RotationAxis.POSITIVE_Y.rotation(transform.rotate2.y));
        stack.multiply(RotationAxis.POSITIVE_X.rotation(transform.rotate2.x));
        scaleStack(stack, transform.scale.x, transform.scale.y, transform.scale.z);
    }

    public static void multiply(MatrixStack stack, Matrix4f matrix)
    {
        normal.set(matrix);
        normal.getScale(Vectors.TEMP_3F);

        Vectors.TEMP_3F.x = Vectors.TEMP_3F.x == 0F ? 0F : 1F / Vectors.TEMP_3F.x;
        Vectors.TEMP_3F.y = Vectors.TEMP_3F.y == 0F ? 0F : 1F / Vectors.TEMP_3F.y;
        Vectors.TEMP_3F.z = Vectors.TEMP_3F.z == 0F ? 0F : 1F / Vectors.TEMP_3F.z;

        normal.scale(Vectors.TEMP_3F);

        stack.peek().getPositionMatrix().mul(matrix);
        stack.peek().getNormalMatrix().mul(normal);
    }

    public static void multiply(Matrix4fStack stack, Matrix4f matrix)
    {
        stack.mul(matrix);
    }

    public static void scaleBack(MatrixStack matrices)
    {
        Matrix4f position = matrices.peek().getPositionMatrix();

        float scaleX = (float) Math.sqrt(position.m00() * position.m00() + position.m10() * position.m10() + position.m20() * position.m20());
        float scaleY = (float) Math.sqrt(position.m01() * position.m01() + position.m11() * position.m11() + position.m21() * position.m21());
        float scaleZ = (float) Math.sqrt(position.m02() * position.m02() + position.m12() * position.m12() + position.m22() * position.m22());

        float max = Math.max(scaleX, Math.max(scaleY, scaleZ));

        position.m00(position.m00() / max);
        position.m10(position.m10() / max);
        position.m20(position.m20() / max);

        position.m01(position.m01() / max);
        position.m11(position.m11() / max);
        position.m21(position.m21() / max);

        position.m02(position.m02() / max);
        position.m12(position.m12() / max);
        position.m22(position.m22() / max);
    }

    /* Helpers for view rotation matrix in 1.21.1 where RenderSystem no longer exposes inverse view rotation directly */
    public static Matrix4f getViewRotationMatrix4f()
    {
        Matrix4f mv = new Matrix4f(RenderSystem.getModelViewMatrix());
        Matrix4f rot = new Matrix4f();
        rot.m00(mv.m00()); rot.m01(mv.m01()); rot.m02(mv.m02());
        rot.m10(mv.m10()); rot.m11(mv.m11()); rot.m12(mv.m12());
        rot.m20(mv.m20()); rot.m21(mv.m21()); rot.m22(mv.m22());
        return rot;
    }

    public static Matrix4f getInverseViewRotationMatrix4f()
    {
        Matrix4f rot = getViewRotationMatrix4f();
        rot.invert();
        return rot;
    }

    public static Matrix3f getInverseViewRotationMatrix3f()
    {
        Matrix4f mv = new Matrix4f(RenderSystem.getModelViewMatrix());
        Matrix3f rot3 = new Matrix3f();
        rot3.m00(mv.m00()); rot3.m01(mv.m01()); rot3.m02(mv.m02());
        rot3.m10(mv.m10()); rot3.m11(mv.m11()); rot3.m12(mv.m12());
        rot3.m20(mv.m20()); rot3.m21(mv.m21()); rot3.m22(mv.m22());
        rot3.invert();
        return rot3;
    }
}