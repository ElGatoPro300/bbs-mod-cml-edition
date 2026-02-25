package elgatopro300.bbs_cml.utils.sodium;

import elgatopro300.bbs_cml.forms.renderers.utils.RecolorVertexSodiumConsumer;
import elgatopro300.bbs_cml.utils.colors.Color;
import net.minecraft.client.render.VertexConsumer;

public class SodiumUtils
{
    public static VertexConsumer createVertexBuffer(VertexConsumer b, Color color)
    {
        return new RecolorVertexSodiumConsumer(b, color);
    }
}