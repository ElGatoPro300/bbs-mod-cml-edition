package mchorse.bbs_mod.client.video;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.watermedia.api.player.videolan.VideoPlayer;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.bbs_mod.utils.clips.Clip;
import mchorse.bbs_mod.camera.clips.misc.VideoClip;
import mchorse.bbs_mod.ui.utils.Area;
import mchorse.bbs_mod.ui.framework.elements.utils.Batcher2D;
import mchorse.bbs_mod.ui.framework.UIContext;

public class VideoRenderer
{
    private static class PlayerWrapper
    {
        public VideoPlayer player;
        public long lastBbsTime = -1;
        public long lastSeekTime = 0;
        public Boolean wasPlaying = null;
        public int lastVolume = -1;
        public Boolean lastLoops = null;
        public long lastVideoTime = -1;
        public long lastRenderTime = 0;

        public PlayerWrapper(VideoPlayer player)
        {
            this.player = player;
        }
    }

    private static final Map<String, PlayerWrapper> PLAYERS = new HashMap<>();
    private static MediaPlayerFactory FACTORY;

    public static void renderClips(MatrixStack stack, Batcher2D batcher, List<Clip> clips, int tick, boolean isRunning, Area viewport, Area globalArea, UIContext context, int screenWidth, int screenHeight, boolean renderGlobal)
    {
        for (Clip clip : clips)
        {
            if (clip instanceof VideoClip && clip.isInside(tick) && clip.enabled.get())
            {
                VideoClip video = (VideoClip) clip;

                if (video.global.get() != renderGlobal)
                {
                    continue;
                }

                Area baseArea = viewport;
                int actualW = getVideoWidth(video.video.get());
                int actualH = getVideoHeight(video.video.get());

                int baseW = baseArea.w;
                int baseH = baseArea.h;

            {
                wrapper.player.release();
                toRemove.add(entry.getKey());
            }
        }
        
        for (String key : toRemove)
        {
            PLAYERS.remove(key);
        }
    }

    public static void cleanup()
    {
        for (PlayerWrapper wrapper : PLAYERS.values())
        {
            wrapper.player.release();
        }
        
        PLAYERS.clear();

        if (FACTORY != null)
        {
            FACTORY.release();
            FACTORY = null;
        }
    }
}
