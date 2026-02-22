package elgatopro300.bbs_cml.mixin.client;

import elgatopro300.bbs_cml.selectors.ISelectorOwnerProvider;
import elgatopro300.bbs_cml.selectors.SelectorOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ISelectorOwnerProvider
{
    public SelectorOwner selector = new SelectorOwner((LivingEntity) (Object) this);

    protected LivingEntityMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Override
    public SelectorOwner getOwner()
    {
        return this.selector;
    }
}