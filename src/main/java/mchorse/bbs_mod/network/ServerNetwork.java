package mchorse.bbs_mod.network;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.actions.ActionManager;
import mchorse.bbs_mod.actions.ActionPlayer;
import mchorse.bbs_mod.actions.ActionRecorder;
import mchorse.bbs_mod.actions.ActionState;
import mchorse.bbs_mod.actions.PlayerType;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.data.DataStorageUtils;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ByteType;
import mchorse.bbs_mod.data.types.ListType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.entity.GunProjectileEntity;
import mchorse.bbs_mod.entity.IEntityFormProvider;
import mchorse.bbs_mod.film.Film;
import mchorse.bbs_mod.film.FilmManager;
import mchorse.bbs_mod.forms.FormUtils;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.items.GunProperties;
import mchorse.bbs_mod.morphing.Morph;
import mchorse.bbs_mod.utils.DataPath;
import mchorse.bbs_mod.utils.EnumUtils;
import mchorse.bbs_mod.utils.PermissionUtils;
import mchorse.bbs_mod.utils.clips.Clips;
import mchorse.bbs_mod.utils.repos.RepositoryOperation;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServerNetwork
{
    public static final int STATE_TRIGGER_MORPH = 0;
    public static final int STATE_TRIGGER_MAIN_HAND_ITEM = 1;
    public static final int STATE_TRIGGER_OFF_HAND_ITEM = 2;

    public static final Identifier CLIENT_CLICKED_MODEL_BLOCK_PACKET = Identifier.of(BBSMod.MOD_ID, "c1");
    public static final Identifier CLIENT_PLAYER_FORM_PACKET = Identifier.of(BBSMod.MOD_ID, "c2");
    public static final Identifier CLIENT_PLAY_FILM_PACKET = Identifier.of(BBSMod.MOD_ID, "c3");
    public static final Identifier CLIENT_MANAGER_DATA_PACKET = Identifier.of(BBSMod.MOD_ID, "c4");
    public static final Identifier CLIENT_STOP_FILM_PACKET = Identifier.of(BBSMod.MOD_ID, "c5");
    public static final Identifier CLIENT_HANDSHAKE = Identifier.of(BBSMod.MOD_ID, "c6");
    public static final Identifier CLIENT_RECORDED_ACTIONS = Identifier.of(BBSMod.MOD_ID, "c7");
    public static final Identifier CLIENT_ANIMATION_STATE_TRIGGER = Identifier.of(BBSMod.MOD_ID, "c8");
    public static final Identifier CLIENT_CHEATS_PERMISSION = Identifier.of(BBSMod.MOD_ID, "c9");
    public static final Identifier CLIENT_SHARED_FORM = Identifier.of(BBSMod.MOD_ID, "c10");
    public static final Identifier CLIENT_ENTITY_FORM = Identifier.of(BBSMod.MOD_ID, "c11");
    public static final Identifier CLIENT_ACTORS = Identifier.of(BBSMod.MOD_ID, "c12");
    public static final Identifier CLIENT_GUN_PROPERTIES = Identifier.of(BBSMod.MOD_ID, "c13");
    public static final Identifier CLIENT_PAUSE_FILM = Identifier.of(BBSMod.MOD_ID, "c14");
    public static final Identifier CLIENT_SELECTED_SLOT = Identifier.of(BBSMod.MOD_ID, "c15");
    public static final Identifier CLIENT_ANIMATION_STATE_MODEL_BLOCK_TRIGGER = Identifier.of(BBSMod.MOD_ID, "c16");
    public static final Identifier CLIENT_REFRESH_MODEL_BLOCKS = Identifier.of(BBSMod.MOD_ID, "c17");

    public static final Identifier SERVER_MODEL_BLOCK_FORM_PACKET = Identifier.of(BBSMod.MOD_ID, "s1");
    public static final Identifier SERVER_MODEL_BLOCK_TRANSFORMS_PACKET = Identifier.of(BBSMod.MOD_ID, "s2");
    public static final Identifier SERVER_PLAYER_FORM_PACKET = Identifier.of(BBSMod.MOD_ID, "s3");
    public static final Identifier SERVER_MANAGER_DATA_PACKET = Identifier.of(BBSMod.MOD_ID, "s4");
    public static final Identifier SERVER_ACTION_RECORDING = Identifier.of(BBSMod.MOD_ID, "s5");
    public static final Identifier SERVER_TOGGLE_FILM = Identifier.of(BBSMod.MOD_ID, "s6");
    public static final Identifier SERVER_ACTION_CONTROL = Identifier.of(BBSMod.MOD_ID, "s7");
    public static final Identifier SERVER_FILM_DATA_SYNC = Identifier.of(BBSMod.MOD_ID, "s8");
    public static final Identifier SERVER_PLAYER_TP = Identifier.of(BBSMod.MOD_ID, "s9");
    public static final Identifier SERVER_ANIMATION_STATE_TRIGGER = Identifier.of(BBSMod.MOD_ID, "s10");
    public static final Identifier SERVER_SHARED_FORM = Identifier.of(BBSMod.MOD_ID, "s11");
    public static final Identifier SERVER_ZOOM = Identifier.of(BBSMod.MOD_ID, "s12");
    public static final Identifier SERVER_PAUSE_FILM = Identifier.of(BBSMod.MOD_ID, "s13");

    private static ServerPacketCrusher crusher = new ServerPacketCrusher();

    /* Typed payload IDs for Fabric 1.21.1 */
    public static final CustomPayload.Id<BufPayload> C_CLICKED_MODEL_BLOCK_ID = new CustomPayload.Id<>(CLIENT_CLICKED_MODEL_BLOCK_PACKET);
    public static final CustomPayload.Id<BufPayload> C_PLAYER_FORM_ID = new CustomPayload.Id<>(CLIENT_PLAYER_FORM_PACKET);
    public static final CustomPayload.Id<BufPayload> C_PLAY_FILM_ID = new CustomPayload.Id<>(CLIENT_PLAY_FILM_PACKET);
    public static final CustomPayload.Id<BufPayload> C_MANAGER_DATA_ID = new CustomPayload.Id<>(CLIENT_MANAGER_DATA_PACKET);
    public static final CustomPayload.Id<BufPayload> C_STOP_FILM_ID = new CustomPayload.Id<>(CLIENT_STOP_FILM_PACKET);
    public static final CustomPayload.Id<BufPayload> C_HANDSHAKE_ID = new CustomPayload.Id<>(CLIENT_HANDSHAKE);
    public static final CustomPayload.Id<BufPayload> C_RECORDED_ACTIONS_ID = new CustomPayload.Id<>(CLIENT_RECORDED_ACTIONS);
    public static final CustomPayload.Id<BufPayload> C_ANIMATION_STATE_TRIGGER_ID = new CustomPayload.Id<>(CLIENT_ANIMATION_STATE_TRIGGER);
    public static final CustomPayload.Id<BufPayload> C_CHEATS_PERMISSION_ID = new CustomPayload.Id<>(CLIENT_CHEATS_PERMISSION);
    public static final CustomPayload.Id<BufPayload> C_SHARED_FORM_ID = new CustomPayload.Id<>(CLIENT_SHARED_FORM);
    public static final CustomPayload.Id<BufPayload> C_ENTITY_FORM_ID = new CustomPayload.Id<>(CLIENT_ENTITY_FORM);
    public static final CustomPayload.Id<BufPayload> C_ACTORS_ID = new CustomPayload.Id<>(CLIENT_ACTORS);
    public static final CustomPayload.Id<BufPayload> C_GUN_PROPERTIES_ID = new CustomPayload.Id<>(CLIENT_GUN_PROPERTIES);
    public static final CustomPayload.Id<BufPayload> C_PAUSE_FILM_ID = new CustomPayload.Id<>(CLIENT_PAUSE_FILM);
    public static final CustomPayload.Id<BufPayload> C_SELECTED_SLOT_ID = new CustomPayload.Id<>(CLIENT_SELECTED_SLOT);
    public static final CustomPayload.Id<BufPayload> C_ANIMATION_STATE_MODEL_BLOCK_TRIGGER_ID = new CustomPayload.Id<>(CLIENT_ANIMATION_STATE_MODEL_BLOCK_TRIGGER);
    public static final CustomPayload.Id<BufPayload> C_REFRESH_MODEL_BLOCKS_ID = new CustomPayload.Id<>(CLIENT_REFRESH_MODEL_BLOCKS);

    public static final CustomPayload.Id<BufPayload> S_MODEL_BLOCK_FORM_ID = new CustomPayload.Id<>(SERVER_MODEL_BLOCK_FORM_PACKET);
    public static final CustomPayload.Id<BufPayload> S_MODEL_BLOCK_TRANSFORMS_ID = new CustomPayload.Id<>(SERVER_MODEL_BLOCK_TRANSFORMS_PACKET);
    public static final CustomPayload.Id<BufPayload> S_PLAYER_FORM_ID = new CustomPayload.Id<>(SERVER_PLAYER_FORM_PACKET);
    public static final CustomPayload.Id<BufPayload> S_MANAGER_DATA_ID = new CustomPayload.Id<>(SERVER_MANAGER_DATA_PACKET);
    public static final CustomPayload.Id<BufPayload> S_ACTION_RECORDING_ID = new CustomPayload.Id<>(SERVER_ACTION_RECORDING);
    public static final CustomPayload.Id<BufPayload> S_TOGGLE_FILM_ID = new CustomPayload.Id<>(SERVER_TOGGLE_FILM);
    public static final CustomPayload.Id<BufPayload> S_ACTION_CONTROL_ID = new CustomPayload.Id<>(SERVER_ACTION_CONTROL);
    public static final CustomPayload.Id<BufPayload> S_FILM_DATA_SYNC_ID = new CustomPayload.Id<>(SERVER_FILM_DATA_SYNC);
    public static final CustomPayload.Id<BufPayload> S_PLAYER_TP_ID = new CustomPayload.Id<>(SERVER_PLAYER_TP);
    public static final CustomPayload.Id<BufPayload> S_ANIMATION_STATE_TRIGGER_ID = new CustomPayload.Id<>(SERVER_ANIMATION_STATE_TRIGGER);
    public static final CustomPayload.Id<BufPayload> S_SHARED_FORM_ID = new CustomPayload.Id<>(SERVER_SHARED_FORM);
    public static final CustomPayload.Id<BufPayload> S_ZOOM_ID = new CustomPayload.Id<>(SERVER_ZOOM);
    public static final CustomPayload.Id<BufPayload> S_PAUSE_FILM_ID = new CustomPayload.Id<>(SERVER_PAUSE_FILM);

    public static class BufPayload implements CustomPayload
    {
        public static final PacketCodec<RegistryByteBuf, BufPayload> CODEC = PacketCodec.of(
            (BufPayload payload, RegistryByteBuf buf) -> buf.writeBytes(payload.bytes),
            (RegistryByteBuf buf) -> {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                return new BufPayload(bytes, null);
            }
        );

        public static PacketCodec<RegistryByteBuf, BufPayload> codecFor(CustomPayload.Id<BufPayload> id)
        {
            return PacketCodec.of(
                (BufPayload payload, RegistryByteBuf buf) -> buf.writeBytes(payload.bytes),
                (RegistryByteBuf buf) -> {
                    byte[] bytes = new byte[buf.readableBytes()];
                    buf.readBytes(bytes);
                    return new BufPayload(bytes, id);
                }
            );
        }

        private final byte[] bytes;
        private final CustomPayload.Id<BufPayload> id;

        public BufPayload(byte[] bytes, CustomPayload.Id<BufPayload> id)
        {
            this.bytes = bytes;
            this.id = id;
        }

        public static BufPayload from(PacketByteBuf packetBuf, CustomPayload.Id<BufPayload> id)
        {
            byte[] bytes = new byte[packetBuf.readableBytes()];
            packetBuf.readBytes(bytes);
            return new BufPayload(bytes, id);
        }

        public PacketByteBuf asPacketByteBuf()
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBytes(this.bytes);
            return buf;
        }

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId()
        {
            if (id == null)
            {
                throw new IllegalStateException("BufPayload id is null");
            }

            return id;
        }
    }

    public static void reset()
    {
        crusher.reset();
    }

    public static void setup()
    {
        /* Register codecs for C2S */
        PayloadTypeRegistry.playC2S().register(S_MODEL_BLOCK_FORM_ID, BufPayload.codecFor(S_MODEL_BLOCK_FORM_ID));
        PayloadTypeRegistry.playC2S().register(S_MODEL_BLOCK_TRANSFORMS_ID, BufPayload.codecFor(S_MODEL_BLOCK_TRANSFORMS_ID));
        PayloadTypeRegistry.playC2S().register(S_PLAYER_FORM_ID, BufPayload.codecFor(S_PLAYER_FORM_ID));
        PayloadTypeRegistry.playC2S().register(S_MANAGER_DATA_ID, BufPayload.codecFor(S_MANAGER_DATA_ID));
        PayloadTypeRegistry.playC2S().register(S_ACTION_RECORDING_ID, BufPayload.codecFor(S_ACTION_RECORDING_ID));
        PayloadTypeRegistry.playC2S().register(S_TOGGLE_FILM_ID, BufPayload.codecFor(S_TOGGLE_FILM_ID));
        PayloadTypeRegistry.playC2S().register(S_ACTION_CONTROL_ID, BufPayload.codecFor(S_ACTION_CONTROL_ID));
        PayloadTypeRegistry.playC2S().register(S_FILM_DATA_SYNC_ID, BufPayload.codecFor(S_FILM_DATA_SYNC_ID));
        PayloadTypeRegistry.playC2S().register(S_PLAYER_TP_ID, BufPayload.codecFor(S_PLAYER_TP_ID));
        PayloadTypeRegistry.playC2S().register(S_ANIMATION_STATE_TRIGGER_ID, BufPayload.codecFor(S_ANIMATION_STATE_TRIGGER_ID));
        PayloadTypeRegistry.playC2S().register(S_SHARED_FORM_ID, BufPayload.codecFor(S_SHARED_FORM_ID));
        PayloadTypeRegistry.playC2S().register(S_ZOOM_ID, BufPayload.codecFor(S_ZOOM_ID));
        PayloadTypeRegistry.playC2S().register(S_PAUSE_FILM_ID, BufPayload.codecFor(S_PAUSE_FILM_ID));

        /* Register codecs for S2C */
        PayloadTypeRegistry.playS2C().register(C_CLICKED_MODEL_BLOCK_ID, BufPayload.codecFor(C_CLICKED_MODEL_BLOCK_ID));
        PayloadTypeRegistry.playS2C().register(C_PLAYER_FORM_ID, BufPayload.codecFor(C_PLAYER_FORM_ID));
        PayloadTypeRegistry.playS2C().register(C_PLAY_FILM_ID, BufPayload.codecFor(C_PLAY_FILM_ID));
        PayloadTypeRegistry.playS2C().register(C_MANAGER_DATA_ID, BufPayload.codecFor(C_MANAGER_DATA_ID));
        PayloadTypeRegistry.playS2C().register(C_STOP_FILM_ID, BufPayload.codecFor(C_STOP_FILM_ID));
        PayloadTypeRegistry.playS2C().register(C_HANDSHAKE_ID, BufPayload.codecFor(C_HANDSHAKE_ID));
        PayloadTypeRegistry.playS2C().register(C_RECORDED_ACTIONS_ID, BufPayload.codecFor(C_RECORDED_ACTIONS_ID));
        PayloadTypeRegistry.playS2C().register(C_ANIMATION_STATE_TRIGGER_ID, BufPayload.codecFor(C_ANIMATION_STATE_TRIGGER_ID));
        PayloadTypeRegistry.playS2C().register(C_CHEATS_PERMISSION_ID, BufPayload.codecFor(C_CHEATS_PERMISSION_ID));
        PayloadTypeRegistry.playS2C().register(C_SHARED_FORM_ID, BufPayload.codecFor(C_SHARED_FORM_ID));
        PayloadTypeRegistry.playS2C().register(C_ENTITY_FORM_ID, BufPayload.codecFor(C_ENTITY_FORM_ID));
        PayloadTypeRegistry.playS2C().register(C_ACTORS_ID, BufPayload.codecFor(C_ACTORS_ID));
        PayloadTypeRegistry.playS2C().register(C_GUN_PROPERTIES_ID, BufPayload.codecFor(C_GUN_PROPERTIES_ID));
        PayloadTypeRegistry.playS2C().register(C_PAUSE_FILM_ID, BufPayload.codecFor(C_PAUSE_FILM_ID));
        PayloadTypeRegistry.playS2C().register(C_SELECTED_SLOT_ID, BufPayload.codecFor(C_SELECTED_SLOT_ID));
        PayloadTypeRegistry.playS2C().register(C_ANIMATION_STATE_MODEL_BLOCK_TRIGGER_ID, BufPayload.codecFor(C_ANIMATION_STATE_MODEL_BLOCK_TRIGGER_ID));
        PayloadTypeRegistry.playS2C().register(C_REFRESH_MODEL_BLOCKS_ID, BufPayload.codecFor(C_REFRESH_MODEL_BLOCKS_ID));

        /* Register typed payload handlers */
        ServerPlayNetworking.registerGlobalReceiver(S_MODEL_BLOCK_FORM_ID, (payload, context) -> handleModelBlockFormPacket(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_MODEL_BLOCK_TRANSFORMS_ID, (payload, context) -> handleModelBlockTransformsPacket(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_PLAYER_FORM_ID, (payload, context) -> handlePlayerFormPacket(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_MANAGER_DATA_ID, (payload, context) -> handleManagerDataPacket(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_ACTION_RECORDING_ID, (payload, context) -> handleActionRecording(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_TOGGLE_FILM_ID, (payload, context) -> handleToggleFilm(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_ACTION_CONTROL_ID, (payload, context) -> handleActionControl(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_FILM_DATA_SYNC_ID, (payload, context) -> handleSyncData(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_PLAYER_TP_ID, (payload, context) -> handleTeleportPlayer(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_ANIMATION_STATE_TRIGGER_ID, (payload, context) -> handleAnimationStateTriggerPacket(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_SHARED_FORM_ID, (payload, context) -> handleSharedFormPacket(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_ZOOM_ID, (payload, context) -> handleZoomPacket(context.server(), context.player(), payload.asPacketByteBuf()));
        ServerPlayNetworking.registerGlobalReceiver(S_PAUSE_FILM_ID, (payload, context) -> handlePauseFilmPacket(context.server(), context.player(), payload.asPacketByteBuf()));
    }

    /* Handlers */

    private static void handleModelBlockFormPacket(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        crusher.receive(buf, (bytes, packetByteBuf) ->
        {
            BlockPos pos = buf.readBlockPos();

            try
            {
                MapType data = (MapType) DataStorageUtils.readFromBytes(bytes);

                server.execute(() ->
                {
                    World world = player.getWorld();
                    BlockEntity be = world.getBlockEntity(pos);

                    if (be instanceof ModelBlockEntity modelBlock)
                    {
                        modelBlock.updateForm(data, world);
                    }
                });
            }
            catch (Exception e)
            {}
        });
    }

    private static void handleModelBlockTransformsPacket(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        crusher.receive(buf, (bytes, packetByteBuf) ->
        {
            try
            {
                MapType data = (MapType) DataStorageUtils.readFromBytes(bytes);

                server.execute(() ->
                {
                    ItemStack stack = player.getEquippedStack(EquipmentSlot.MAINHAND).copy();

                    if (stack.getItem() == BBSMod.MODEL_BLOCK_ITEM)
                    {
                        // Migrate BlockEntityTag to data components: store properties under BLOCK_ENTITY_DATA
                        stack.apply(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
                            currentNbt.put("Properties", DataStorageUtils.toNbt(data));
                        }));
                    }
                    else if (stack.getItem() == BBSMod.GUN_ITEM)
                    {
                        // Store gun properties under CUSTOM_DATA component with key "GunData"
                        stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
                            currentNbt.put("GunData", DataStorageUtils.toNbt(data));
                        }));
                    }

                    player.equipStack(EquipmentSlot.MAINHAND, stack);
                });
            }
            catch (Exception e)
            {}
        });
    }

    private static void handlePlayerFormPacket(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        crusher.receive(buf, (bytes, packetByteBuf) ->
        {
            Form form = null;

            try
            {
                if (DataStorageUtils.readFromBytes(bytes) instanceof MapType data)
                {
                    form = BBSMod.getForms().fromData(data);
                }
            }
            catch (Exception e)
            {}

            final Form finalForm = form;

            server.execute(() ->
            {
                Morph.getMorph(player).setForm(FormUtils.copy(finalForm));

                sendMorphToTracked(player, finalForm);
            });
        });
    }

    private static void handleManagerDataPacket(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        crusher.receive(buf, (bytes, packetByteBuf) ->
        {
            MapType data = (MapType) DataStorageUtils.readFromBytes(bytes);
            int callbackId = packetByteBuf.readInt();
            RepositoryOperation op = RepositoryOperation.values()[packetByteBuf.readInt()];
            FilmManager films = BBSMod.getFilms();

            if (op == RepositoryOperation.LOAD)
            {
                String id = data.getString("id");
                Film film = films.load(id);

                sendManagerData(player, callbackId, op, film.toData());
            }
            else if (op == RepositoryOperation.SAVE)
            {
                films.save(data.getString("id"), data.getMap("data"));
            }
            else if (op == RepositoryOperation.RENAME)
            {
                films.rename(data.getString("from"), data.getString("to"));
            }
            else if (op == RepositoryOperation.DELETE)
            {
                films.delete(data.getString("id"));
            }
            else if (op == RepositoryOperation.KEYS)
            {
                ListType list = DataStorageUtils.stringListToData(films.getKeys());

                sendManagerData(player, callbackId, op, list);
            }
            else if (op == RepositoryOperation.ADD_FOLDER)
            {
                sendManagerData(player, callbackId, op, new ByteType(films.addFolder(data.getString("folder"))));
            }
            else if (op == RepositoryOperation.RENAME_FOLDER)
            {
                sendManagerData(player, callbackId, op, new ByteType(films.renameFolder(data.getString("from"), data.getString("to"))));
            }
            else if (op == RepositoryOperation.DELETE_FOLDER)
            {
                sendManagerData(player, callbackId, op, new ByteType(films.deleteFolder(data.getString("folder"))));
            }
        });
    }

    private static void handleActionRecording(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        String filmId = buf.readString();
        int replayId = buf.readInt();
        int tick = buf.readInt();
        int countdown = buf.readInt();
        boolean recording = buf.readBoolean();

        server.execute(() ->
        {
            if (recording)
            {
                Film film = BBSMod.getFilms().load(filmId);

                if (film != null)
                {
                    BBSMod.getActions().startRecording(film, player, 0, countdown, replayId);
                }
            }
            else
            {
                ActionRecorder recorder = BBSMod.getActions().stopRecording(player);
                Clips clips = recorder.composeClips();

                /* Send recorded clips to the client */
                sendRecordedActions(player, filmId, replayId, tick, clips);
            }
        });
    }

    private static void handleToggleFilm(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        String filmId = buf.readString();
        boolean withCamera = buf.readBoolean();

        server.execute(() ->
        {
            ActionPlayer actionPlayer = BBSMod.getActions().getPlayer(filmId);

            if (actionPlayer != null)
            {
                BBSMod.getActions().stop(filmId);

                for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList())
                {
                    sendStopFilm(otherPlayer, filmId);
                }
            }
            else
            {
                sendPlayFilm(player, player.getServerWorld(), filmId, withCamera);
            }
        });
    }

    private static void handleActionControl(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        ActionManager actions = BBSMod.getActions();
        String filmId = buf.readString();
        ActionState state = EnumUtils.getValue(buf.readByte(), ActionState.values(), ActionState.STOP);
        int tick = buf.readInt();

        server.execute(() ->
        {
            if (state == ActionState.SEEK)
            {
                ActionPlayer actionPlayer = actions.getPlayer(filmId);

                if (actionPlayer != null)
                {
                    actionPlayer.goTo(tick);
                }
            }
            else if (state == ActionState.PLAY)
            {
                ActionPlayer actionPlayer = actions.getPlayer(filmId);

                if (actionPlayer != null)
                {
                    actionPlayer.goTo(tick);
                    actionPlayer.playing = true;
                }
            }
            else if (state == ActionState.PAUSE)
            {
                ActionPlayer actionPlayer = actions.getPlayer(filmId);

                if (actionPlayer != null)
                {
                    actionPlayer.goTo(tick);
                    actionPlayer.playing = false;
                }
            }
            else if (state == ActionState.RESTART)
            {
                ActionPlayer actionPlayer = actions.getPlayer(filmId);

                if (actionPlayer == null)
                {
                    Film film = BBSMod.getFilms().load(filmId);

                    if (film != null)
                    {
                        actionPlayer = actions.play(player, player.getServerWorld(), film, tick, PlayerType.FILM_EDITOR);
                    }
                }
                else
                {
                    actions.stop(filmId);

                    actionPlayer = actions.play(player, player.getServerWorld(), actionPlayer.film, tick, PlayerType.FILM_EDITOR);
                }

                if (actionPlayer != null)
                {
                    actionPlayer.syncing = true;
                    actionPlayer.playing = false;

                    if (tick != 0)
                    {
                        actionPlayer.goTo(0, tick);
                    }
                }

                sendStopFilm(player, filmId);
            }
            else if (state == ActionState.STOP)
            {
                actions.stop(filmId);
            }
        });
    }

    private static void handleSyncData(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        crusher.receive(buf, (bytes, packetByteBuf) ->
        {
            String filmId = packetByteBuf.readString();
            List<String> path = new ArrayList<>();

            for (int i = 0, c = buf.readInt(); i < c; i++)
            {
                path.add(buf.readString());
            }

            BaseType data = DataStorageUtils.readFromBytes(bytes);

            server.execute(() ->
            {
                BBSMod.getActions().syncData(filmId, new DataPath(path), data);
            });
        });
    }

    private static void handleTeleportPlayer(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float yaw = buf.readFloat();
        float bodyYaw = buf.readFloat();
        float pitch = buf.readFloat();

        server.execute(() ->
        {
            player.requestTeleport(x, y, z);

            player.setYaw(yaw);
            player.setHeadYaw(yaw);
            player.setBodyYaw(bodyYaw);
            player.setPitch(pitch);
        });
    }

    private static void handleAnimationStateTriggerPacket(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        String string = buf.readString();
        int type = buf.readInt();
        PacketByteBuf newBuf = PacketByteBufs.create();

        newBuf.writeInt(player.getId());
        newBuf.writeString(string);
        newBuf.writeInt(type);

        for (ServerPlayerEntity otherPlayer : PlayerLookup.tracking(player))
        {
            ServerPlayNetworking.send(otherPlayer, BufPayload.from(newBuf, C_ANIMATION_STATE_TRIGGER_ID));
        }

        server.execute(() ->
        {
            /* TODO: State Triggers */
        });
    }

    private static void handleSharedFormPacket(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        crusher.receive(buf, (bytes, packetByteBuf) ->
        {
            UUID playerUuid = packetByteBuf.readUuid();
            MapType data = (MapType) DataStorageUtils.readFromBytes(bytes);

            server.execute(() ->
            {
                ServerPlayerEntity otherPlayer = server.getPlayerManager().getPlayer(playerUuid);

                if (otherPlayer != null)
                {
                    sendSharedForm(otherPlayer, data);
                }
            });
        });
    }

    private static void handleZoomPacket(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        boolean zoom = buf.readBoolean();
        ItemStack main = player.getMainHandStack();

        if (main.getItem() == BBSMod.GUN_ITEM)
        {
            GunProperties properties = GunProperties.get(main);
            String command = zoom ? properties.cmdZoomOn : properties.cmdZoomOff;

            if (!command.isEmpty())
            {
                server.getCommandManager().executeWithPrefix(player.getCommandSource(), command);
            }
        }
    }

    private static void handlePauseFilmPacket(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf)
    {
        String filmId = buf.readString();

        ActionPlayer actionPlayer = BBSMod.getActions().getPlayer(filmId);

        if (actionPlayer != null)
        {
            actionPlayer.toggle();
        }

        for (ServerPlayerEntity playerEntity : server.getPlayerManager().getPlayerList())
        {
            sendPauseFilm(playerEntity, filmId);
        }
    }

    /* API */

    public static void sendMorph(ServerPlayerEntity player, int playerId, Form form)
    {
        crusher.send(player, CLIENT_PLAYER_FORM_PACKET, FormUtils.toData(form), (packetByteBuf) ->
        {
            packetByteBuf.writeInt(playerId);
        });
    }

    public static void sendMorphToTracked(ServerPlayerEntity player, Form form)
    {
        sendMorph(player, player.getId(), form);

        for (ServerPlayerEntity otherPlayer : PlayerLookup.tracking(player))
        {
            sendMorph(otherPlayer, player.getId(), form);
        }
    }

    public static void sendClickedModelBlock(ServerPlayerEntity player, BlockPos pos)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(pos);

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_CLICKED_MODEL_BLOCK_ID));
    }

    public static void sendPlayFilm(ServerPlayerEntity player, ServerWorld world, String filmId, boolean withCamera)
    {
        try
        {
            Film film = BBSMod.getFilms().load(filmId);

            if (film != null)
            {
                BBSMod.getActions().play(player, world, film, 0);

                BaseType data = film.toData();

                crusher.send(world.getPlayers().stream().map((p) -> (PlayerEntity) p).toList(), CLIENT_PLAY_FILM_PACKET, data, (packetByteBuf) ->
                {
                    packetByteBuf.writeString(filmId);
                    packetByteBuf.writeBoolean(withCamera);
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void sendPlayFilm(ServerPlayerEntity player, String filmId, boolean withCamera)
    {
        try
        {
            Film film = BBSMod.getFilms().load(filmId);

            if (film != null)
            {
                BBSMod.getActions().play(player, player.getServerWorld(), film, 0);

                crusher.send(player, CLIENT_PLAY_FILM_PACKET, film.toData(), (packetByteBuf) ->
                {
                    packetByteBuf.writeString(filmId);
                    packetByteBuf.writeBoolean(withCamera);
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void sendStopFilm(ServerPlayerEntity player, String filmId)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeString(filmId);

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_STOP_FILM_ID));
    }

    public static void sendManagerData(ServerPlayerEntity player, int callbackId, RepositoryOperation op, BaseType data)
    {
        crusher.send(player, CLIENT_MANAGER_DATA_PACKET, data, (packetByteBuf) ->
        {
            packetByteBuf.writeInt(callbackId);
            packetByteBuf.writeInt(op.ordinal());
        });
    }

    public static void sendRecordedActions(ServerPlayerEntity player, String filmId, int replayId, int tick, Clips clips)
    {
        crusher.send(player, CLIENT_RECORDED_ACTIONS, clips.toData(), (packetByteBuf) ->
        {
            packetByteBuf.writeString(filmId);
            packetByteBuf.writeInt(replayId);
            packetByteBuf.writeInt(tick);
        });
    }

    public static void sendHandshake(MinecraftServer server, PacketSender packetSender)
    {
        packetSender.sendPacket(BufPayload.from(createHandshakeBuf(server), C_HANDSHAKE_ID));
    }

    public static void sendHandshake(MinecraftServer server, ServerPlayerEntity player)
    {
        ServerPlayNetworking.send(player, BufPayload.from(createHandshakeBuf(server), C_HANDSHAKE_ID));
    }

    private static PacketByteBuf createHandshakeBuf(MinecraftServer server)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        String id = "";

        /* No need to do that in singleplayer */
        if (server.isSingleplayer())
        {
            id = "";
        }

        buf.writeString(id);

        return buf;
    }

    public static void sendCheatsPermission(ServerPlayerEntity player, boolean cheats)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBoolean(cheats);

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_CHEATS_PERMISSION_ID));
    }

    public static void sendSharedForm(ServerPlayerEntity player, MapType data)
    {
        crusher.send(player, CLIENT_SHARED_FORM, data, (packetByteBuf) ->
        {});
    }

    public static void sendEntityForm(ServerPlayerEntity player, IEntityFormProvider actor)
    {
        crusher.send(player, CLIENT_ENTITY_FORM, FormUtils.toData(actor.getForm()), (packetByteBuf) ->
        {
            packetByteBuf.writeInt(actor.getEntityId());
        });
    }

    public static void sendActors(ServerPlayerEntity player, String filmId, Map<String, LivingEntity> actors)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeString(filmId);
        buf.writeInt(actors.size());

        for (Map.Entry<String, LivingEntity> entry : actors.entrySet())
        {
            buf.writeString(entry.getKey());
            buf.writeInt(entry.getValue().getId());
        }

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_ACTORS_ID));
    }

    public static void sendGunProperties(ServerPlayerEntity player, GunProjectileEntity projectile)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        GunProperties properties = projectile.getProperties();

        buf.writeInt(projectile.getEntityId());
        properties.toNetwork(buf);

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_GUN_PROPERTIES_ID));
    }

    public static void sendPauseFilm(ServerPlayerEntity player, String filmId)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeString(filmId);

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_PAUSE_FILM_ID));
    }

    public static void sendSelectedSlot(ServerPlayerEntity player, int slot)
    {
        player.getInventory().selectedSlot = slot;

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(slot);

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_SELECTED_SLOT_ID));
    }

    public static void sendModelBlockState(ServerPlayerEntity player, BlockPos pos, String trigger)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(pos);
        buf.writeString(trigger);

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_ANIMATION_STATE_MODEL_BLOCK_TRIGGER_ID));
    }

    public static void sendReloadModelBlocks(ServerPlayerEntity player, int tickRandom)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(tickRandom);

        ServerPlayNetworking.send(player, BufPayload.from(buf, C_REFRESH_MODEL_BLOCKS_ID));
    }
}