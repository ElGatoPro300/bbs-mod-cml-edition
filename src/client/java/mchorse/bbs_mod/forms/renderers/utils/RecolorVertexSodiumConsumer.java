package mchorse.bbs_mod.forms.renderers.utils;

import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.render.VertexConsumer;
import org.lwjgl.system.MemoryStack;

public class RecolorVertexSodiumConsumer extends RecolorVertexConsumer
{
    public RecolorVertexSodiumConsumer(VertexConsumer consumer, Color color)
    {
        super(consumer, color);

        newColor = color;
    }

    // Sodium integration removed for 1.21.4 compile; VertexBufferWriter is optional
}
