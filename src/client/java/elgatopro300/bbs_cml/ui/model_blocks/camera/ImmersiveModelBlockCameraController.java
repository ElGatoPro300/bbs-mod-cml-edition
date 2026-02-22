package elgatopro300.bbs_cml.ui.model_blocks.camera;

import elgatopro300.bbs_cml.blocks.entities.ModelBlockEntity;
import elgatopro300.bbs_cml.camera.Camera;
import elgatopro300.bbs_cml.camera.controller.ICameraController;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UIModelRenderer;
import elgatopro300.bbs_cml.utils.pose.Transform;
import net.minecraft.util.math.BlockPos;

public class ImmersiveModelBlockCameraController implements ICameraController
{
    private UIModelRenderer modelRenderer;
    private ModelBlockEntity modelBlock;

    public ImmersiveModelBlockCameraController(UIModelRenderer modelRenderer, ModelBlockEntity modelBlock)
    {
        this.modelRenderer = modelRenderer;
        this.modelBlock = modelBlock;
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        UIModelRenderer renderer = this.modelRenderer;
        Transform transform = this.modelBlock.getProperties().getTransform();

        renderer.setupPosition();

        BlockPos pos = this.modelBlock.getPos();

        camera.position.set(pos.getX() + transform.translate.x + 0.5D, pos.getY() + transform.translate.y, pos.getZ() + transform.translate.z + 0.5D);
        camera.rotation.set(0, 0, 0);

        Camera rendererCamera = renderer.camera;

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