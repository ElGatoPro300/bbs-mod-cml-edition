package elgatopro300.bbs_cml.film.replays;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.ListType;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory extends BaseValue
{
    private List<ItemStack> stacks = new ArrayList<>();

    public Inventory(String id)
    {
        super(id);
    }

    public List<ItemStack> getStacks()
    {
        return Collections.unmodifiableList(this.stacks);
    }

    public void fromPlayer(PlayerEntity player)
    {
        this.stacks.clear();

        for (int i = 0; i < player.getInventory().size(); i++)
        {
            this.stacks.add(player.getInventory().getStack(i).copy());
        }
    }

    @Override
    public BaseType toData()
    {
        ListType data = new ListType();

        for (ItemStack stack : this.stacks)
        {
            if (stack == null)
            {
                stack = ItemStack.EMPTY;
            }

            data.add(KeyframeFactories.ITEM_STACK.toData(stack));
        }

        return data;
    }

    @Override
    public void fromData(BaseType data)
    {
        this.stacks.clear();

        if (data.isList())
        {
            ListType list = data.asList();

            for (BaseType type : list)
            {
                ItemStack stack = KeyframeFactories.ITEM_STACK.fromData(type);

                if (stack == null)
                {
                    stack = ItemStack.EMPTY;
                }

                this.stacks.add(stack);
            }
        }
    }
}