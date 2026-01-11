package mchorse.bbs_mod.entity;

import mchorse.bbs_mod.film.Film;
import mchorse.bbs_mod.film.replays.Replay;
import mchorse.bbs_mod.forms.entities.MCEntity;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.network.ServerNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorEntity extends LivingEntity implements IEntityFormProvider
{
    public static DefaultAttributeContainer.Builder createActorAttributes()
    {
        return LivingEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1D)
            .add(EntityAttributes.GENERIC_ATTACK_SPEED)
            .add(EntityAttributes.GENERIC_LUCK);
    }

    private boolean despawn;
    private MCEntity entity = new MCEntity(this);
    private Form form;

    private Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();

    /* Film and replay data for item drops */
    private Film film;
    private Replay replay;
    private int currentTick;
    
    /* Track items picked up during replay playback */
    private List<ItemStack> pickedUpItems = new java.util.ArrayList<>();

    public ActorEntity(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    /**
     * Set the film and replay associated with this actor for item dropping on death
     */
    public void setReplayData(Film film, Replay replay, int tick)
    {
        this.film = film;
        this.replay = replay;
        this.currentTick = tick;
    }
    
    /**
     * Update the current tick for accurate item retrieval
     */
    public void updateTick(int tick)
    {
        this.currentTick = tick;
    }

    public MCEntity getEntity()
    {
        return this.entity;
    }

    @Override
    public int getEntityId()
    {
        return this.getId();
    }

    @Override
    public Form getForm()
    {
        return this.form;
    }

    @Override
    public void setForm(Form form)
    {
        Form lastForm = this.form;

        this.form = form;

        if (!this.getWorld().isClient())
        {
            if (lastForm != null) lastForm.onDemorph(this);
            if (form != null) form.onMorph(this);
        }
    }

    @Override
    public boolean shouldRender(double distance)
    {
        double d = this.getBoundingBox().getAverageSideLength();

        if (Double.isNaN(d))
        {
            d = 1D;
        }

        return distance < (d * 256D) * (d * 256D);
    }

    @Override
    public Iterable<ItemStack> getHandItems()
    {
        return List.of(this.getEquippedStack(EquipmentSlot.MAINHAND), this.getEquippedStack(EquipmentSlot.OFFHAND));
    }

    @Override
    public Iterable<ItemStack> getArmorItems()
    {
        return List.of(this.getEquippedStack(EquipmentSlot.FEET), this.getEquippedStack(EquipmentSlot.LEGS), this.getEquippedStack(EquipmentSlot.CHEST), this.getEquippedStack(EquipmentSlot.HEAD));
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot)
    {
        return this.equipment.getOrDefault(slot, ItemStack.EMPTY);
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack)
    {
        this.equipment.put(slot, stack == null ? ItemStack.EMPTY : stack);
    }

    @Override
    public Arm getMainArm()
    {
        return Arm.RIGHT;
    }

    @Override
    public void tick()
    {
        super.tick();

        this.tickHandSwing();

        if (this.form != null)
        {
            this.form.update(this.entity);
        }

        if (this.getWorld().isClient)
        {
            return;
        }

        /* Don't pickup items when dead */
        if (this.isDead())
        {
            return;
        }

        /* Pickup items */
        Box box = this.getBoundingBox().expand(1D, 0.5D, 1D);
        List<Entity> list = this.getWorld().getOtherEntities(this, box);

        for (Entity entity : list)
        {
            if (entity instanceof ItemEntity itemEntity)
            {
                ItemStack itemStack = itemEntity.getStack();
                int i = itemStack.getCount();

                if (!entity.isRemoved() && !itemEntity.cannotPickup())
                {
                    // Add picked up item to the list for later dropping on death
                    this.pickedUpItems.add(itemStack.copy());
                    
                    ((ServerWorld) this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(entity, new ItemPickupAnimationS2CPacket(entity.getId(), this.getId(), i));
                    entity.discard();
                }
            }
        }
    }

        @Override
    public void onDeath(DamageSource damageSource)
    {
        super.onDeath(damageSource);
        
        if (!this.getWorld().isClient() && this.replay != null && this.film != null)
        {
            this.dropReplayItems();
        }
    }
    
    /**
     * Drop items from the replay's inventory and equipment when it dies
     * Mimics vanilla Minecraft item drop behavior
     */
    private void dropReplayItems()
    {
        // Drop equipped items from keyframes at current tick
        if (this.replay.keyframes != null)
        {
            float tick = (float) this.currentTick;
            
            // Drop main hand item
            ItemStack mainHand = this.replay.keyframes.mainHand.interpolate(tick, ItemStack.EMPTY);
            if (!mainHand.isEmpty())
            {
                this.dropItemStack(mainHand.copy());
            }
            
            // Drop off hand item
            ItemStack offHand = this.replay.keyframes.offHand.interpolate(tick, ItemStack.EMPTY);
            if (!offHand.isEmpty())
            {
                this.dropItemStack(offHand.copy());
            }
            
            // Drop armor pieces
            ItemStack armorHead = this.replay.keyframes.armorHead.interpolate(tick, ItemStack.EMPTY);
            if (!armorHead.isEmpty())
            {
                this.dropItemStack(armorHead.copy());
            }
            
            ItemStack armorChest = this.replay.keyframes.armorChest.interpolate(tick, ItemStack.EMPTY);
            if (!armorChest.isEmpty())
            {
                this.dropItemStack(armorChest.copy());
            }
            
            ItemStack armorLegs = this.replay.keyframes.armorLegs.interpolate(tick, ItemStack.EMPTY);
            if (!armorLegs.isEmpty())
            {
                this.dropItemStack(armorLegs.copy());
            }
            
            ItemStack armorFeet = this.replay.keyframes.armorFeet.interpolate(tick, ItemStack.EMPTY);
            if (!armorFeet.isEmpty())
            {
                this.dropItemStack(armorFeet.copy());
            }
        }
        
        // Drop items from replay inventory if available
        if (this.replay.inventory != null && !this.replay.inventory.getStacks().isEmpty())
        {
            for (ItemStack stack : this.replay.inventory.getStacks())
            {
                if (stack != null && !stack.isEmpty())
                {
                    this.dropItemStack(stack.copy());
                }
            }
        }
        
        // Drop items that were picked up during replay playback
        for (ItemStack stack : this.pickedUpItems)
        {
            if (stack != null && !stack.isEmpty())
            {
                this.dropItemStack(stack.copy());
            }
        }
    }
    
    /**
     * Drop a single item stack with configurable physics from replay settings
     */
    private void dropItemStack(ItemStack stack)
    {
        if (stack.isEmpty() || this.replay == null)
        {
            return;
        }
        
        // Create item entity at actor's position
        ItemEntity itemEntity = new ItemEntity(
            this.getWorld(),
            this.getX(),
            this.getY() + 0.5,
            this.getZ(),
            stack
        );
        
        // Apply random velocity using replay's configured values
        float minX = this.replay.dropVelocityMinX.get();
        float maxX = this.replay.dropVelocityMaxX.get();
        float minY = this.replay.dropVelocityMinY.get();
        float maxY = this.replay.dropVelocityMaxY.get();
        float minZ = this.replay.dropVelocityMinZ.get();
        float maxZ = this.replay.dropVelocityMaxZ.get();
        
        // Debug: Print velocity values to console
        System.out.println("[BBS Debug] Drop velocities - X: [" + minX + ", " + maxX + "], Y: [" + minY + ", " + maxY + "], Z: [" + minZ + ", " + maxZ + "]");
        
        double velocityX = minX + this.random.nextDouble() * (maxX - minX);
        double velocityY = minY + this.random.nextDouble() * (maxY - minY);
        double velocityZ = minZ + this.random.nextDouble() * (maxZ - minZ);
        
        itemEntity.setVelocity(velocityX, velocityY, velocityZ);
        itemEntity.setToDefaultPickupDelay();
        
        this.getWorld().spawnEntity(itemEntity);
    }


    @Override
    public void checkDespawn()
    {
        super.checkDespawn();

        if (this.despawn)
        {
            this.discard();
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player)
    {
        super.onStartedTrackingBy(player);

        ServerNetwork.sendEntityForm(player, this);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);

        this.despawn = nbt.getBoolean("despawn");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean("despawn", true);
    }

    @Override
    protected int getPermissionLevel()
    {
        return 4;
    }
}