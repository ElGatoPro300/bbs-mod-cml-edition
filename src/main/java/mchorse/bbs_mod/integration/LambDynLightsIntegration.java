package elgatopro300.bbs_cml.integration;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.entity.ActorEntity;
import elgatopro300.bbs_cml.entity.GunProjectileEntity;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.forms.LightForm;
import elgatopro300.bbs_cml.morphing.Morph;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

public class LambDynLightsIntegration implements DynamicLightsInitializer
{
    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager)
    {
        DynamicLightHandlers.registerDynamicLightHandler(BBSMod.ACTOR_ENTITY, (ActorEntity entity) -> getLightLevelFromForm(entity.getForm()));
        DynamicLightHandlers.registerDynamicLightHandler(BBSMod.GUN_PROJECTILE_ENTITY, (GunProjectileEntity entity) -> getLightLevelFromForm(entity.getForm()));
        DynamicLightHandlers.registerDynamicLightHandler(EntityType.PLAYER, (PlayerEntity player) ->
        {
            Morph morph = Morph.getMorph(player);

            if (morph == null)
            {
                return 0;
            }

            return getLightLevelFromForm(morph.getForm());
        });
    }

    private static int getLightLevelFromForm(Form form)
    {
        if (!(form instanceof LightForm lightForm))
        {
            return 0;
        }

        if (!lightForm.enabled.get())
        {
            return 0;
        }

        int level = lightForm.level.get();

        if (level < 0)
        {
            level = 0;
        }
        else if (level > 15)
        {
            level = 15;
        }

        return level;
    }
}

