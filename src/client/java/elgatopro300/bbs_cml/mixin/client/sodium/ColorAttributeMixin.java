package elgatopro300.bbs_cml.mixin.client.sodium;

import elgatopro300.bbs_cml.forms.renderers.utils.RecolorVertexConsumer;
import elgatopro300.bbs_cml.utils.colors.Colors;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.ColorAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ColorAttribute.class)
public class ColorAttributeMixin
{
    @ModifyVariable(method = "set", at = @At("HEAD"), ordinal = 0, remap = false)
    private static int onSet(int color)
    {
        if (RecolorVertexConsumer.newColor != null)
        {
            Colors.COLOR.set(color);
            Colors.COLOR.mul(RecolorVertexConsumer.newColor);

            return Colors.COLOR.getARGBColor();
        }

        return color;
    }
}