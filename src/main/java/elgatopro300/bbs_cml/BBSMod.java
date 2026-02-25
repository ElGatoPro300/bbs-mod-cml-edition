package elgatopro300.bbs_cml;

import elgatopro300.bbs_cml.actions.ActionHandler;
import elgatopro300.bbs_cml.actions.ActionManager;
import elgatopro300.bbs_cml.actions.types.AttackActionClip;
import elgatopro300.bbs_cml.actions.types.DamageActionClip;
import elgatopro300.bbs_cml.actions.types.SwipeActionClip;
import elgatopro300.bbs_cml.actions.types.blocks.BreakBlockActionClip;
import elgatopro300.bbs_cml.actions.types.blocks.InteractBlockActionClip;
import elgatopro300.bbs_cml.actions.types.blocks.PlaceBlockActionClip;
import elgatopro300.bbs_cml.actions.types.chat.ChatActionClip;
import elgatopro300.bbs_cml.actions.types.chat.CommandActionClip;
import elgatopro300.bbs_cml.actions.types.item.ItemDropActionClip;
import elgatopro300.bbs_cml.actions.types.item.UseBlockItemActionClip;
import elgatopro300.bbs_cml.actions.types.item.UseItemActionClip;
import elgatopro300.bbs_cml.blocks.ModelBlock;
import elgatopro300.bbs_cml.blocks.TriggerBlock;
import elgatopro300.bbs_cml.blocks.entities.ModelBlockEntity;
import elgatopro300.bbs_cml.blocks.entities.ModelProperties;
import elgatopro300.bbs_cml.blocks.entities.TriggerBlockEntity;
import elgatopro300.bbs_cml.camera.clips.ClipFactoryData;
import elgatopro300.bbs_cml.camera.clips.converters.DollyToKeyframeConverter;
import elgatopro300.bbs_cml.camera.clips.converters.DollyToPathConverter;
import elgatopro300.bbs_cml.camera.clips.converters.IdleConverter;
import elgatopro300.bbs_cml.camera.clips.converters.IdleToDollyConverter;
import elgatopro300.bbs_cml.camera.clips.converters.IdleToKeyframeConverter;
import elgatopro300.bbs_cml.camera.clips.converters.IdleToPathConverter;
import elgatopro300.bbs_cml.camera.clips.converters.PathToDollyConverter;
import elgatopro300.bbs_cml.camera.clips.converters.PathToKeyframeConverter;
import elgatopro300.bbs_cml.camera.clips.misc.AudioClip;
import elgatopro300.bbs_cml.camera.clips.misc.CurveClip;
import elgatopro300.bbs_cml.camera.clips.misc.SubtitleClip;
import elgatopro300.bbs_cml.camera.clips.misc.VideoClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.AngleClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.DollyZoomClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.DragClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.LookClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.MathClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.OrbitClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.RemapperClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.ShakeClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.TrackerClip;
import elgatopro300.bbs_cml.camera.clips.modifiers.TranslateClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.DollyClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.IdleClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.KeyframeClip;
import elgatopro300.bbs_cml.camera.clips.overwrite.PathClip;
import elgatopro300.bbs_cml.data.DataStorageUtils;
import elgatopro300.bbs_cml.entity.ActorEntity;
import elgatopro300.bbs_cml.entity.GunProjectileEntity;
import elgatopro300.bbs_cml.events.BBSAddonMod;
import elgatopro300.bbs_cml.events.EventBus;
import elgatopro300.bbs_cml.events.register.RegisterActionClipsEvent;
import elgatopro300.bbs_cml.events.register.RegisterMolangFunctionsEvent;
import elgatopro300.bbs_cml.events.register.RegisterKeyframeFactoriesEvent;
import elgatopro300.bbs_cml.events.register.RegisterCameraClipsEvent;
import elgatopro300.bbs_cml.events.register.RegisterEntityCaptureHandlersEvent;
import elgatopro300.bbs_cml.events.register.RegisterFormsEvent;
import elgatopro300.bbs_cml.events.register.RegisterSettingsEvent;
import elgatopro300.bbs_cml.events.register.RegisterSourcePacksEvent;
import elgatopro300.bbs_cml.film.FilmManager;
import elgatopro300.bbs_cml.forms.FormArchitect;
import elgatopro300.bbs_cml.forms.forms.AnchorForm;
import elgatopro300.bbs_cml.forms.forms.BillboardForm;
import elgatopro300.bbs_cml.forms.forms.BlockForm;
import elgatopro300.bbs_cml.forms.forms.ExtrudedForm;
import elgatopro300.bbs_cml.forms.forms.FluidForm;
import elgatopro300.bbs_cml.forms.forms.FramebufferForm;
import elgatopro300.bbs_cml.forms.forms.StructureForm;
import elgatopro300.bbs_cml.forms.forms.LightForm;
import elgatopro300.bbs_cml.forms.forms.ItemForm;
import elgatopro300.bbs_cml.forms.forms.LabelForm;
import elgatopro300.bbs_cml.forms.forms.MobForm;
import elgatopro300.bbs_cml.forms.forms.ModelForm;
import elgatopro300.bbs_cml.forms.forms.ParticleForm;
import elgatopro300.bbs_cml.forms.forms.TrailForm;
import elgatopro300.bbs_cml.forms.forms.VanillaParticleForm;
import elgatopro300.bbs_cml.items.GunItem;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.morphing.Morph;
import elgatopro300.bbs_cml.network.ServerNetwork;
import elgatopro300.bbs_cml.resources.AssetProvider;
import elgatopro300.bbs_cml.resources.ISourcePack;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.resources.packs.DynamicSourcePack;
import elgatopro300.bbs_cml.resources.packs.ExternalAssetsSourcePack;
import elgatopro300.bbs_cml.resources.packs.InternalAssetsSourcePack;
import elgatopro300.bbs_cml.resources.packs.WorldStructuresSourcePack;
import elgatopro300.bbs_cml.settings.Settings;
import elgatopro300.bbs_cml.settings.SettingsBuilder;
import elgatopro300.bbs_cml.settings.SettingsManager;
import elgatopro300.bbs_cml.ui.utils.icons.Icon;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.clips.Clip;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.factory.MapFactory;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;

import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BBSMod implements ModInitializer
{
    public static final String MOD_ID = "bbs";

    public static final EventBus events = new EventBus();

    private static ActionManager actions;

    /* Important folders */
    private static File gameFolder;
    private static File assetsFolder;
    private static File settingsFolder;

    /* Core services */
    private static AssetProvider provider;
    private static DynamicSourcePack dynamicSourcePack;
    private static ExternalAssetsSourcePack originalSourcePack;

    /* Foundation services */
    private static SettingsManager settings;
    private static FormArchitect forms;

    /* Data */
    private static FilmManager films;

    private static List<Runnable> runnables = new ArrayList<>();

    private static MapFactory<Clip, ClipFactoryData> factoryCameraClips;
    private static MapFactory<Clip, ClipFactoryData> factoryActionClips;

    public static final EntityType<ActorEntity> ACTOR_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(MOD_ID, "actor"),
        EntityType.Builder.create(ActorEntity::new, SpawnGroup.CREATURE)
            .dimensions(0.6F, 1.8F)
            .maxTrackingRange(16)
            .trackingTickInterval(1)
            .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "actor"))));

    public static final EntityType<GunProjectileEntity> GUN_PROJECTILE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(MOD_ID, "gun_projectile"),
        EntityType.Builder.create(GunProjectileEntity::new, SpawnGroup.MISC)
            .dimensions(0.25F, 0.25F)
            .maxTrackingRange(24)
            .trackingTickInterval(1)
            .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "gun_projectile"))));

    public static final Block MODEL_BLOCK = new ModelBlock(AbstractBlock.Settings.create()
        .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "model")))
        .noBlockBreakParticles()
        .dropsNothing()
        .nonOpaque()
        .notSolid()
        .strength(0F)
        .luminance((state) -> state.get(ModelBlock.LIGHT_LEVEL)));
        
    public static final Block TRIGGER_BLOCK = new TriggerBlock(AbstractBlock.Settings.create()
        .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "trigger")))
        .noBlockBreakParticles()
        .dropsNothing()
        .nonOpaque()
        .notSolid()
        .strength(-1F, 3600000F));

    public static final Block CHROMA_RED_BLOCK = createChromaBlock("chroma_red");
    public static final Block CHROMA_GREEN_BLOCK = createChromaBlock("chroma_green");
    public static final Block CHROMA_BLUE_BLOCK = createChromaBlock("chroma_blue");
    public static final Block CHROMA_CYAN_BLOCK = createChromaBlock("chroma_cyan");
    public static final Block CHROMA_MAGENTA_BLOCK = createChromaBlock("chroma_magenta");
    public static final Block CHROMA_YELLOW_BLOCK = createChromaBlock("chroma_yellow");
    public static final Block CHROMA_BLACK_BLOCK = createChromaBlock("chroma_black");
    public static final Block CHROMA_WHITE_BLOCK = createChromaBlock("chroma_white");

    public static final BlockItem MODEL_BLOCK_ITEM = new BlockItem(MODEL_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "model"))));
    public static final BlockItem TRIGGER_BLOCK_ITEM = new BlockItem(TRIGGER_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "trigger"))));
    public static final GunItem GUN_ITEM = new GunItem(new Item.Settings().maxCount(1).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "gun"))));
    public static final BlockItem CHROMA_RED_BLOCK_ITEM = new BlockItem(CHROMA_RED_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "chroma_red"))));
    public static final BlockItem CHROMA_GREEN_BLOCK_ITEM = new BlockItem(CHROMA_GREEN_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "chroma_green"))));
    public static final BlockItem CHROMA_BLUE_BLOCK_ITEM = new BlockItem(CHROMA_BLUE_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "chroma_blue"))));
    public static final BlockItem CHROMA_CYAN_BLOCK_ITEM = new BlockItem(CHROMA_CYAN_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "chroma_cyan"))));
    public static final BlockItem CHROMA_MAGENTA_BLOCK_ITEM = new BlockItem(CHROMA_MAGENTA_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "chroma_magenta"))));
    public static final BlockItem CHROMA_YELLOW_BLOCK_ITEM = new BlockItem(CHROMA_YELLOW_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "chroma_yellow"))));
    public static final BlockItem CHROMA_BLACK_BLOCK_ITEM = new BlockItem(CHROMA_BLACK_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "chroma_black"))));
    public static final BlockItem CHROMA_WHITE_BLOCK_ITEM = new BlockItem(CHROMA_WHITE_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "chroma_white"))));

    public static final GameRule<Boolean> BBS_EDITING_RULE = GameRuleBuilder
        .forBoolean(true)
        .category(GameRuleCategory.MISC)
        .buildAndRegister(Identifier.of(MOD_ID, "bbsEditing"));

    public static final BlockEntityType<ModelBlockEntity> MODEL_BLOCK_ENTITY = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(MOD_ID, "model_block_entity"),
        FabricBlockEntityTypeBuilder.create(ModelBlockEntity::new, MODEL_BLOCK).build()
    );

    public static final BlockEntityType<TriggerBlockEntity> TRIGGER_BLOCK_ENTITY = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(MOD_ID, "trigger_block"),
        FabricBlockEntityTypeBuilder.create(TriggerBlockEntity::new, TRIGGER_BLOCK).build()
    );

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
        .icon(() -> createModelBlockStack(Link.assets("textures/icon.png")))
        .displayName(Text.translatable("itemGroup.bbs.main"))
        .entries((context, entries) ->
        {
            entries.add(createModelBlockStack(Link.assets("textures/model_block.png")));
            entries.add(new ItemStack(TRIGGER_BLOCK_ITEM));
            entries.add(CHROMA_RED_BLOCK_ITEM);
            entries.add(CHROMA_GREEN_BLOCK_ITEM);
            entries.add(CHROMA_BLUE_BLOCK_ITEM);
            entries.add(CHROMA_CYAN_BLOCK_ITEM);
            entries.add(CHROMA_MAGENTA_BLOCK_ITEM);
            entries.add(CHROMA_YELLOW_BLOCK_ITEM);
            entries.add(CHROMA_BLACK_BLOCK_ITEM);
            entries.add(CHROMA_WHITE_BLOCK_ITEM);
            entries.add(new ItemStack(GUN_ITEM));
        })
        .build();

    public static final SoundEvent CLICK = registerSound("click");

    private static SoundEvent registerSound(String path)
    {
        Identifier id = Identifier.of(MOD_ID, path);

        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    private static File worldFolder;

    private static Block createChromaBlock(String name)
    {
        return new Block(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, name)))
            .noBlockBreakParticles()
            .dropsNothing()
            .requiresTool()
            .strength(-1F, 3600000F));
    }

    private static ItemStack createModelBlockStack(Link texture)
    {
        ItemStack stack = new ItemStack(MODEL_BLOCK_ITEM);
        ModelBlockEntity entity = new ModelBlockEntity(BlockPos.ORIGIN, MODEL_BLOCK.getDefaultState());
        BillboardForm form = new BillboardForm();
        ModelProperties properties = entity.getProperties();

        form.transform.get().translate.set(0F, 0.5F, 0F);
        form.texture.set(texture);
        properties.setForm(form);
        properties.getTransformFirstPerson().translate.set(0F, 0F, -0.25F);

        NbtCompound compound = new NbtCompound();
        compound.putString("id", BlockEntityType.getId(MODEL_BLOCK_ENTITY).toString());
        elgatopro300.bbs_cml.data.DataStorageUtils.writeToNbtCompound(compound, "Properties", properties.toData());

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
        stack.set(DataComponentTypes.BLOCK_STATE, new BlockStateComponent(Map.of("light_level", String.valueOf(properties.getLightLevel()))));

        return stack;
    }

    /**
     * Main folder, where all the other folders are located.
     */
    public static File getGameFolder()
    {
        return gameFolder;
    }

    public static File getGamePath(String path)
    {
        return new File(gameFolder, path);
    }

    /**
     * Assets folder within game's folder. It's used to store any assets that can
     * be loaded by {@link #provider}.
     */
    public static File getAssetsFolder()
    {
        ISourcePack sourcePack = getDynamicSourcePack().getSourcePack();

        if (sourcePack instanceof ExternalAssetsSourcePack pack)
        {
            return pack.getFolder();
        }

        return assetsFolder;
    }

    public static File getAudioFolder()
    {
        return getAssetsPath("audio");
    }

    public static File getAssetsPath(String path)
    {
        return new File(getAssetsFolder(), path);
    }

    public static File getAudioCacheFolder()
    {
        return getSettingsPath("audio_cache");
    }

    /**
     * Config folder within game's folder. It's used to store any configuration
     * files.
     */
    public static File getSettingsFolder()
    {
        return settingsFolder;
    }

    public static File getSettingsPath(String path)
    {
        return new File(settingsFolder, path);
    }

    public static File getExportFolder()
    {
        return getGamePath("export");
    }

    public static ActionManager getActions()
    {
        return actions;
    }

    public static AssetProvider getProvider()
    {
        return provider;
    }

    public static DynamicSourcePack getDynamicSourcePack()
    {
        return dynamicSourcePack;
    }

    public static ExternalAssetsSourcePack getOriginalSourcePack()
    {
        return originalSourcePack;
    }

    public static SettingsManager getSettings()
    {
        return settings;
    }

    public static FormArchitect getForms()
    {
        return forms;
    }

    public static FilmManager getFilms()
    {
        return films;
    }

    public static File getWorldFolder()
    {
        return worldFolder;
    }

    public static MapFactory<Clip, ClipFactoryData> getFactoryCameraClips()
    {
        return factoryCameraClips;
    }

    public static MapFactory<Clip, ClipFactoryData> getFactoryActionClips()
    {
        return factoryActionClips;
    }

    @Override
    public void onInitialize()
    {
        /* Core */
        gameFolder = FabricLoader.getInstance().getGameDir().toFile();
        assetsFolder = new File(gameFolder, "config/bbs/assets");
        settingsFolder = new File(gameFolder, "config/bbs/settings");

        assetsFolder.mkdirs();
        new File(assetsFolder, "video").mkdirs();

        FabricLoader.getInstance()
            .getEntrypointContainers("bbs-addon", BBSAddonMod.class)
            .forEach((container) ->
            {
                events.register(container.getEntrypoint());
            });

        events.post(new RegisterMolangFunctionsEvent(MolangParser.CUSTOM_FUNCTIONS));

        actions = new ActionManager();

        originalSourcePack = new ExternalAssetsSourcePack(Link.ASSETS, assetsFolder).providesFiles();
        dynamicSourcePack = new DynamicSourcePack(originalSourcePack);
        provider = new AssetProvider();
        provider.register(dynamicSourcePack);
        provider.registerFirst(new WorldStructuresSourcePack());
        provider.register(new InternalAssetsSourcePack());

        events.post(new RegisterSourcePacksEvent(provider));

        settings = new SettingsManager();
        forms = new FormArchitect();
        forms
            .register(Link.bbs("billboard"), BillboardForm.class, null)
            .register(Link.bbs("fluid"), FluidForm.class, null)
            .register(Link.bbs("label"), LabelForm.class, null)
            .register(Link.bbs("model"), ModelForm.class, null)
            .register(Link.bbs("particle"), ParticleForm.class, null)
            .register(Link.bbs("extruded"), ExtrudedForm.class, null)
            .register(Link.bbs("block"), BlockForm.class, null)
            .register(Link.bbs("item"), ItemForm.class, null)
            .register(Link.bbs("anchor"), AnchorForm.class, null)
            .register(Link.bbs("mob"), MobForm.class, null)
            .register(Link.bbs("vanilla_particles"), VanillaParticleForm.class, null)
            .register(Link.bbs("trail"), TrailForm.class, null)
            .register(Link.bbs("framebuffer"), FramebufferForm.class, null)
            .register(Link.bbs("structure"), StructureForm.class, null)
            .register(Link.bbs("light"), LightForm.class, null);

        events.post(new RegisterFormsEvent(forms));

        films = new FilmManager(() -> new File(worldFolder, "bbs/films"));

        /* Register camera clips */
        events.post(new RegisterKeyframeFactoriesEvent(KeyframeFactories.FACTORIES));
        events.post(new RegisterEntityCaptureHandlersEvent(Morph.HANDLERS));

        factoryCameraClips = new MapFactory<Clip, ClipFactoryData>()
            .register(Link.bbs("idle"), IdleClip.class, new ClipFactoryData(Icons.FRUSTUM, 0x159e64)
                .withConverter(Link.bbs("dolly"), new IdleToDollyConverter())
                .withConverter(Link.bbs("path"), new IdleToPathConverter())
                .withConverter(Link.bbs("keyframe"), new IdleToKeyframeConverter()))
            .register(Link.bbs("dolly"), DollyClip.class, new ClipFactoryData(Icons.CAMERA, 0xffa500)
                .withConverter(Link.bbs("idle"), IdleConverter.CONVERTER)
                .withConverter(Link.bbs("path"), new DollyToPathConverter())
                .withConverter(Link.bbs("keyframe"), new DollyToKeyframeConverter()))
            .register(Link.bbs("path"), PathClip.class, new ClipFactoryData(Icons.GALLERY, 0x6820ad)
                .withConverter(Link.bbs("idle"), IdleConverter.CONVERTER)
                .withConverter(Link.bbs("dolly"), new PathToDollyConverter())
                .withConverter(Link.bbs("keyframe"), new PathToKeyframeConverter()))
            .register(Link.bbs("keyframe"), KeyframeClip.class, new ClipFactoryData(Icons.CURVES, 0xde2e9f)
                .withConverter(Link.bbs("idle"), IdleConverter.CONVERTER))
            .register(Link.bbs("translate"), TranslateClip.class, new ClipFactoryData(Icons.UPLOAD, 0x4ba03e))
            .register(Link.bbs("angle"), AngleClip.class, new ClipFactoryData(Icons.ARC, 0xd77a0a))
            .register(Link.bbs("drag"), DragClip.class, new ClipFactoryData(Icons.FADING, 0x4baff7))
            .register(Link.bbs("shake"), ShakeClip.class, new ClipFactoryData(Icons.EXCHANGE, 0x159e64))
            .register(Link.bbs("math"), MathClip.class, new ClipFactoryData(Icons.GRAPH, 0x6820ad))
            .register(Link.bbs("look"), LookClip.class, new ClipFactoryData(Icons.VISIBLE, 0x197fff))
            .register(Link.bbs("orbit"), OrbitClip.class, new ClipFactoryData(Icons.GLOBE, 0xd82253))
            .register(Link.bbs("remapper"), RemapperClip.class, new ClipFactoryData(Icons.TIME, 0x222222))
            .register(Link.bbs("audio"), AudioClip.class, new ClipFactoryData(Icons.SOUND, 0xffc825))
            .register(Link.bbs("video"), VideoClip.class, new ClipFactoryData(Icons.IMAGE, 0x9933cc))
            .register(Link.bbs("subtitle"), SubtitleClip.class, new ClipFactoryData(Icons.FONT, 0x888899))
            .register(Link.bbs("curve"), CurveClip.class, new ClipFactoryData(Icons.ARC, 0xff1493))
            .register(Link.bbs("tracker"), TrackerClip.class, new ClipFactoryData(Icons.USER, 0xffffff))
            .register(Link.bbs("dolly_zoom"), DollyZoomClip.class, new ClipFactoryData(Icons.FILTER, 0x7d56c9));

        events.post(new RegisterCameraClipsEvent(factoryCameraClips));

        factoryActionClips = new MapFactory<Clip, ClipFactoryData>()
            .register(Link.bbs("chat"), ChatActionClip.class, new ClipFactoryData(Icons.BUBBLE, Colors.YELLOW))
            .register(Link.bbs("command"), CommandActionClip.class, new ClipFactoryData(Icons.PROPERTIES, Colors.ACTIVE))
            .register(Link.bbs("place_block"), PlaceBlockActionClip.class, new ClipFactoryData(Icons.BLOCK, Colors.INACTIVE))
            .register(Link.bbs("interact_block"), InteractBlockActionClip.class, new ClipFactoryData(Icons.FULLSCREEN, Colors.MAGENTA))
            .register(Link.bbs("break_block"), BreakBlockActionClip.class, new ClipFactoryData(Icons.BULLET, Colors.GREEN))
            .register(Link.bbs("use_item"), UseItemActionClip.class, new ClipFactoryData(Icons.POINTER, Colors.BLUE))
            .register(Link.bbs("use_block_item"), UseBlockItemActionClip.class, new ClipFactoryData(Icons.BUCKET, Colors.CYAN))
            .register(Link.bbs("drop_item"), ItemDropActionClip.class, new ClipFactoryData(Icons.ARROW_DOWN, Colors.DEEP_PINK))
            .register(Link.bbs("attack"), AttackActionClip.class, new ClipFactoryData(Icons.DROP, Colors.RED))
            .register(Link.bbs("damage"), DamageActionClip.class, new ClipFactoryData(Icons.SKULL, Colors.CURSOR))
            .register(Link.bbs("swipe"), SwipeActionClip.class, new ClipFactoryData(Icons.LIMB, Colors.ORANGE));

        events.post(new RegisterActionClipsEvent(factoryActionClips));

        setupConfig(Icons.PROCESSOR, "bbs", new File(settingsFolder, "bbs.json"), BBSSettings::register);

        events.post(new RegisterSettingsEvent());

        /* Networking */
        ServerNetwork.setup();

        /* Commands */
        CommandRegistrationCallback.EVENT.register(BBSCommands::register);

        /* Event listener */
        registerEvents();

        /* Entities */
        FabricDefaultAttributeRegistry.register(ACTOR_ENTITY, ActorEntity.createActorAttributes());

        /* Blocks */
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "model"), MODEL_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "trigger"), TRIGGER_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chroma_red"), CHROMA_RED_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chroma_green"), CHROMA_GREEN_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chroma_blue"), CHROMA_BLUE_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chroma_cyan"), CHROMA_CYAN_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chroma_magenta"), CHROMA_MAGENTA_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chroma_yellow"), CHROMA_YELLOW_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chroma_black"), CHROMA_BLACK_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chroma_white"), CHROMA_WHITE_BLOCK);

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "model"), MODEL_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "trigger"), TRIGGER_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "gun"), GUN_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "chroma_red"), CHROMA_RED_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "chroma_green"), CHROMA_GREEN_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "chroma_blue"), CHROMA_BLUE_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "chroma_cyan"), CHROMA_CYAN_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "chroma_magenta"), CHROMA_MAGENTA_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "chroma_yellow"), CHROMA_YELLOW_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "chroma_black"), CHROMA_BLACK_BLOCK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "chroma_white"), CHROMA_WHITE_BLOCK_ITEM);

        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "main"), ITEM_GROUP);
    }

    private void registerEvents()
    {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) ->
        {
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof TriggerBlockEntity trigger)
            {
                if (player.isCreative())
                {
                    return ActionResult.PASS;
                }

                if (world.isClient)
                {
                    return ActionResult.SUCCESS;
                }

                if (player instanceof ServerPlayerEntity serverPlayer)
                {
                    trigger.trigger(serverPlayer, false);
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) ->
        {
            if (entity instanceof ServerPlayerEntity player)
            {
                Morph morph = Morph.getMorph(player);

                ServerNetwork.sendMorphToTracked(player, morph.getForm());
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register((event) -> worldFolder = event.getSavePath(WorldSavePath.ROOT).toFile());
        ServerPlayConnectionEvents.JOIN.register((a, b, c) -> ServerNetwork.sendHandshake(c, b));

        ActionHandler.registerHandlers(actions);

        ServerTickEvents.START_SERVER_TICK.register((server) ->
        {
            actions.tick();
        });

        ServerTickEvents.END_SERVER_TICK.register((server) ->
        {
            for (Runnable runnable : runnables)
            {
                runnable.run();
            }

            runnables.clear();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register((server) ->
        {
            actions.reset();
            ServerNetwork.reset();
        });

        EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) ->
        {
            runnables.add(() ->
            {
                if (trackedEntity instanceof ServerPlayerEntity playerTwo)
                {
                    Morph morph = Morph.getMorph(trackedEntity);

                    if (morph != null)
                    {
                        ServerNetwork.sendMorph(player, playerTwo.getId(), morph.getForm());
                    }
                }
            });
        });
    }

    public static Settings setupConfig(Icon icon, String id, File destination, Consumer<SettingsBuilder> registerer)
    {
        SettingsBuilder builder = new SettingsBuilder(icon, id, destination);
        Settings settings = builder.getConfig();

        registerer.accept(builder);

        BBSMod.settings.modules.put(settings.getId(), settings);
        BBSMod.settings.load(settings, settings.file);

        return settings;
    }
}
