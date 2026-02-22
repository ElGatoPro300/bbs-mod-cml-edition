package elgatopro300.bbs_cml.settings.values.mc;

import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ValueBlockState extends BaseKeyframeFactoryValue<BlockState>
{
    public ValueBlockState(String id)
    {
        super(id, KeyframeFactories.BLOCK_STATE, Blocks.AIR.getDefaultState());
    }
}