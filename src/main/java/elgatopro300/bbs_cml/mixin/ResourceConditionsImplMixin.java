package elgatopro300.bbs_cml.mixin;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@Mixin(ResourceConditionsImpl.class)
public class ResourceConditionsImplMixin
{
    /*
    @Shadow(remap = false)
    public static AtomicReference<Map<RegistryKey<?>, Set<Identifier>>> LOADED_TAGS;

    @Inject(method = "setTags", at = @At("HEAD"), remap = false)
    private static void onSetTags(List<?> tags, CallbackInfo ci)
    {
        // Reset tags before setting new ones to avoid "Tags already captured" error
        if (LOADED_TAGS != null)
        {
            LOADED_TAGS.set(null);
        }
    }
    */
}
