package mchorse.bbs_mod.simulation;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.film.BaseFilmController;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.renderers.utils.MatrixCache;
import mchorse.bbs_mod.forms.renderers.utils.MatrixCacheEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class FluidController
{
    public void update(IEntity entity, FluidSimulation simulation, float scaleX, float scaleZ, float sensitivity, Matrix4f surfaceMatrixWorld)
    {
        if (entity == null || simulation == null)
        {
            return;
        }

        if (surfaceMatrixWorld == null)
        {
            return;
        }

        /* Self-movement check */
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double lastX = entity.getPrevX();
        double lastZ = entity.getPrevZ();
        
        double dx = x - lastX;
        double dz = z - lastZ;
        double speed = Math.sqrt(dx * dx + dz * dz);
        
        int width = simulation.getWidth();
        int height = simulation.getHeight();

        if (speed > 0.01)
        {
            int cx = width / 2;
            int cy = height / 2;
            simulation.addForce(cx, cy, (float)speed * sensitivity);
        }

        Matrix4f inverseSurface = new Matrix4f(surfaceMatrixWorld);

        if (Math.abs(inverseSurface.determinant()) < 1e-8f)
        {
            return;
        }

        inverseSurface.invert();

        Vector3f surfaceCenter = surfaceMatrixWorld.getTranslation(new Vector3f());
        Vector3f axisX = surfaceMatrixWorld.getColumn(0, new Vector3f());
        Vector3f axisZ = surfaceMatrixWorld.getColumn(2, new Vector3f());

        double worldHalfX = axisX.length() * (scaleX * 0.5);
        double worldHalfZ = axisZ.length() * (scaleZ * 0.5);
        double maxRadius = Math.max(worldHalfX, worldHalfZ) * 1.5;

        List<Vec3d> samples = new ArrayList<>();

        World world = entity.getWorld();

        if (world != null)
        {
            double minX = surfaceCenter.x - maxRadius;
            double maxX = surfaceCenter.x + maxRadius;
            double minZ = surfaceCenter.z - maxRadius;
            double maxZ = surfaceCenter.z + maxRadius;
            double minY = surfaceCenter.y - 2.0;
            double maxY = surfaceCenter.y + 2.0;

            Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
            List<Entity> entities = world.getOtherEntities(null, box);

            for (Entity e : entities)
            {
                samples.add(e.getPos());
            }
        }

        for (BaseFilmController controller : BBSModClient.getFilms().getControllers())
        {
            for (IEntity replayEntity : controller.getEntities().values())
            {
                Form form = replayEntity.getForm();

                if (form == null)
                {
                    continue;
                }

                var renderer = FormUtilsClient.getRenderer(form);

                if (renderer == null)
                {
                    continue;
                }

                MatrixCache map = renderer.collectMatrices(replayEntity, 0F);

                Matrix4f defaultMatrix = BaseFilmController.getMatrixForRenderWithRotation(replayEntity, 0, 0, 0, 0F);
                var totalMatrix = BaseFilmController.getTotalMatrix(controller.getEntities(), form.anchor.get(), defaultMatrix, 0, 0, 0, 0F, 0);
                Matrix4f entityMatrix = totalMatrix != null && totalMatrix.a != null ? totalMatrix.a : defaultMatrix;

                for (var mapEntry : map.entrySet())
                {
                    MatrixCacheEntry entry = mapEntry.getValue();

                    if (entry == null || entry.matrix() == null)
                    {
                        continue;
                    }

                    Matrix4f boneMatrix = new Matrix4f(entityMatrix).mul(entry.matrix());
                    Vector3f t = boneMatrix.getTranslation(new Vector3f());

                    samples.add(new Vec3d(t.x, t.y, t.z));
                }
            }
        }

        for (Vec3d p : samples)
        {
            Vector3f local = inverseSurface.transformPosition((float) p.x, (float) p.y, (float) p.z, new Vector3f());

            if (Math.abs(local.y) > 1.5f)
            {
                continue;
            }

            float u = (local.x / scaleX) + 0.5f;
            float v = (local.z / scaleZ) + 0.5f;

            if (u >= 0 && u <= 1 && v >= 0 && v <= 1)
            {
                int gx = (int) (u * width);
                int gz = (int) (v * height);

                float force = sensitivity;

                simulation.addForce(gx, gz, force);
            }
        }
    }
}
