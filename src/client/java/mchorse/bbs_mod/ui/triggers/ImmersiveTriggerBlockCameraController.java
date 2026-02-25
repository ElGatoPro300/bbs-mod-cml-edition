package elgatopro300.bbs_cml.ui.triggers;

import elgatopro300.bbs_cml.blocks.entities.TriggerBlockEntity;
import elgatopro300.bbs_cml.camera.Camera;
import elgatopro300.bbs_cml.camera.controller.ICameraController;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UIModelRenderer;
import net.minecraft.util.math.BlockPos;

public class ImmersiveTriggerBlockCameraController implements ICameraController
{
    private UIModelRenderer renderer;
    private TriggerBlockEntity entity;

    public ImmersiveTriggerBlockCameraController(UIModelRenderer renderer, TriggerBlockEntity entity)
    {
        this.renderer = renderer;
        this.entity = entity;
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        if (this.entity == null)
        {
            return;
        }

        this.renderer.setupPosition();

        BlockPos pos = this.entity.getPos();
        Camera rendererCamera = this.renderer.camera;

        camera.position.set(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        camera.rotation.set(0, 0, 0);

        camera.position.add(rendererCamera.position);
        camera.rotation.add(rendererCamera.rotation);
        camera.fov = rendererCamera.fov;
    }

    @Override
    public int getPriority()
    {
        return 100500;
    }

    @Override
    public void update()
    {}
}
