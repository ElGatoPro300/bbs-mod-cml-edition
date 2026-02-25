package elgatopro300.bbs_cml.forms.forms;

import elgatopro300.bbs_cml.settings.values.mc.ValueBlockState;
import elgatopro300.bbs_cml.settings.values.core.ValueColor;
import elgatopro300.bbs_cml.utils.colors.Color;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;

public class BlockForm extends Form
{
    public final ValueBlockState blockState = new ValueBlockState("block_state");
    public final ValueColor color = new ValueColor("color", Color.white());

    public BlockForm()
    {
        this.add(this.blockState);
        this.add(this.color);
    }

    @Override
    protected String getDefaultDisplayName()
    {
        return Registries.BLOCK.getId(this.blockState.get().getBlock()).toString();
    }
}