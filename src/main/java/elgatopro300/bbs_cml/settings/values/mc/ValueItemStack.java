package elgatopro300.bbs_cml.settings.values.mc;

import elgatopro300.bbs_cml.settings.values.base.BaseKeyframeFactoryValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import net.minecraft.item.ItemStack;

public class ValueItemStack extends BaseKeyframeFactoryValue<ItemStack>
{
    public ValueItemStack(String id)
    {
        super(id, KeyframeFactories.ITEM_STACK, ItemStack.EMPTY);
    }
}