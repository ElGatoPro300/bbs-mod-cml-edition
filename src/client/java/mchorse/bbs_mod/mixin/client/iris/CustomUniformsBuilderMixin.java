package mchorse.bbs_mod.mixin.client.iris;

import mchorse.bbs_mod.utils.iris.ShaderCurves;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import java.lang.reflect.Field;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomUniforms.Builder.class)
public class CustomUniformsBuilderMixin
{
    @Inject(method = "build(Lnet/irisshaders/iris/uniforms/custom/CustomUniformFixedInputUniformsHolder;)Lnet/irisshaders/iris/uniforms/custom/CustomUniforms;", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    public void onBuild(CallbackInfoReturnable<CustomUniforms> info)
    {
        CustomUniforms uniforms = info.getReturnValue();
        if (uniforms != null)
        {
            try
            {
                Field f = uniforms.getClass().getDeclaredField("uniformOrder");
                f.setAccessible(true);
                List order = (List) f.get(uniforms);
                ShaderCurves.addUniforms(order);
            }
            catch (Throwable t)
            {
                // Silently ignore if Iris changed internals
            }
        }
    }
}
