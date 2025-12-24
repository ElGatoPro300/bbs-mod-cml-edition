package mchorse.bbs_mod.graphics.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.resources.AssetProvider;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.StringUtils;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.resources.MultiLink;
import mchorse.bbs_mod.utils.resources.MultiLinkThread;
import mchorse.bbs_mod.utils.resources.Pixels;
import mchorse.bbs_mod.utils.watchdog.IWatchDogListener;
import mchorse.bbs_mod.utils.watchdog.WatchDogEvent;
import org.lwjgl.opengl.GL11;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TextureManager implements IWatchDogListener
{
    public final Map<Link, Texture> textures = new HashMap<>();
    public final Map<Link, AnimatedTexture> animatedTextures = new HashMap<>();
    public final Map<Link, org.joml.Vector2i> textureDimensionsHints = new java.util.concurrent.ConcurrentHashMap<>();
    public AssetProvider provider;

    private Texture error;
    private TextureExtruder extruder = new TextureExtruder();
    private int tick;

    public TextureManager(AssetProvider provider)
    {
        this.provider = provider;
    }

    public TextureExtruder getExtruder()
    {
        return this.extruder;
    }

    public Texture getError()
    {
        if (this.error == null)
        {
            try
            {
                Pixels pixels = Pixels.fromSize(16, 16);
                Color a = new Color().set(0xff009fe0);
                Color b = new Color().set(0xffe00073);

                for (int x = 0; x < pixels.width; x++)
                {
                    for (int y = 0; y < pixels.height; y++)
                    {
                        Color color = a;

                        if ((x / 4) % 2 == 0 ^ (y / 4) % 2 == 0)
                        {
                            color = b;
                        }

                        pixels.setColor(x, y, color);
                    }
                }

                pixels.rewindBuffer();

                Texture texture = new Texture();
                texture.setFilter(GL11.GL_NEAREST);
                texture.uploadTexture(pixels);
                texture.unbind();

                this.error = texture;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return this.error;
    }

    public void bindTexture(Link texture)
    {
        this.bindTexture(texture, 0);
    }

    public void bindTexture(Link texture, int unit)
    {
        this.bindTexture(this.getTexture(texture), unit);
    }

    public void bindTexture(Texture texture)
    {
        this.bindTexture(texture, 0);
    }

    public void bindTexture(Texture texture, int unit)
    {
        BBSRendering.trackTexture(texture);

        RenderSystem.setShaderTexture(unit, texture.id);
    }

    public void bind(Link texture)
    {
        this.getTexture(texture).bind();
    }

    public void bind(Link texture, int unit)
    {
        this.getTexture(texture).bind(unit);
    }

    public boolean has(Link link)
    {
        return this.getTexture(link) != this.getError();
    }

    public void delete(Link link)
    {
        Texture texture = this.textures.remove(link);

        if (texture != null)
        {
            texture.delete();
        }

        AnimatedTexture animatedTexture = this.animatedTextures.remove(link);

        if (animatedTexture != null)
        {
            animatedTexture.delete();
        }
    }

    public Texture createTexture(Link link)
    {
        return this.createTexture(link, GL11.GL_NEAREST);
    }

    public Texture createTexture(Link link, int filter)
    {
        Texture texture = this.textures.get(link);

        if (texture == null || texture == this.getError())
        {
            texture = new Texture();
            texture.setFilter(filter);

            this.textures.put(link, texture);
        }

        return texture;
    }

    public Pixels getPixels(Link link) throws Exception
    {
        Pixels pixels;

        if (link instanceof MultiLink)
        {
            pixels = MultiLinkThread.getStreamForMultiLink((MultiLink) link);
        }
        else
        {
            try (InputStream asset = this.provider.getAsset(link))
            {
                pixels = Pixels.fromPNGStream(asset);
            }
        }

        return pixels;
    }

    public Texture getTexture(Link link)
    {
        return this.getTexture(link, GL11.GL_NEAREST);
    }

    public Texture getTexture(Link link, int filter)
    {
        return this.getTexture(link, filter, false);
    }

    public Texture getTexture(Link link, int filter, boolean silent)
    {
        Texture texture = this.get(link);

        if (texture == null)
        {
            try
            {
                Pixels pixels = this.getPixels(link);

                if (pixels != null)
                {
                    if (!link.source.startsWith("http"))
                    {
                        try (InputStream stream = this.provider.getAsset(new Link(link.source, link.path + ".mcmeta")))
                    {
                        AnimatedTexture animatedTexture = AnimatedTexture.load(stream, pixels);

                        texture = animatedTexture.getTexture(this.tick);

                        System.out.println("Animated texture \"" + link + "\" was loaded!");

                        this.animatedTextures.put(link, animatedTexture);

                        return texture;
                    }
                    catch (Exception e)
                    {}
                    }

                    if (this.textureDimensionsHints.containsKey(link))
                    {
                        org.joml.Vector2i dims = this.textureDimensionsHints.get(link);
                        
                        if (dims.x > 0 && dims.y > 0)
                        {
                            /* Check Vertical Animation (Stack of frames) */
                            /* Priority: Square Frames > Hint Frames */
                            long scaleW = pixels.width / dims.x;
                            if (scaleW < 1) scaleW = 1;
                            long minFH = dims.y * scaleW;
                            
                            /* Candidate 1: Square Frame */
                            long sqFH = pixels.width;
                            if (pixels.height % sqFH == 0 && sqFH >= minFH && pixels.height > sqFH)
                            {
                                try
                                {
                                    AnimatedTexture animatedTexture = AnimatedTexture.create(pixels, null, pixels.width, (int) sqFH, 3);
                                    texture = animatedTexture.getTexture(this.tick);
                                    System.out.println("Animated texture (Vertical Square) \"" + link + "\" detected (frame " + pixels.width + "x" + sqFH + ") and loaded!");
                                    this.animatedTextures.put(link, animatedTexture);
                                    return texture;
                                }
                                catch (Exception e) { e.printStackTrace(); }
                            }
                            
                            /* Candidate 2: Hint Frame */
                            if (pixels.height % minFH == 0 && pixels.height > minFH)
                            {
                                try
                                {
                                    AnimatedTexture animatedTexture = AnimatedTexture.create(pixels, null, pixels.width, (int) minFH, 3);
                                    texture = animatedTexture.getTexture(this.tick);
                                    System.out.println("Animated texture (Vertical Hint) \"" + link + "\" detected (frame " + pixels.width + "x" + minFH + ") and loaded!");
                                    this.animatedTextures.put(link, animatedTexture);
                                    return texture;
                                }
                                catch (Exception e) { e.printStackTrace(); }
                            }

                            /* Check Horizontal Animation (Row of frames) */
                            long scaleH = pixels.height / dims.y;
                            if (scaleH < 1) scaleH = 1;
                            long minFW = dims.x * scaleH;
                            
                            /* Candidate 1: Square Frame */
                            long sqFW = pixels.height;
                            if (pixels.width % sqFW == 0 && sqFW >= minFW && pixels.width > sqFW)
                            {
                                try
                                {
                                    AnimatedTexture animatedTexture = AnimatedTexture.create(pixels, null, (int) sqFW, pixels.height, 3);
                                    texture = animatedTexture.getTexture(this.tick);
                                    System.out.println("Animated texture (Horizontal Square) \"" + link + "\" detected (frame " + sqFW + "x" + pixels.height + ") and loaded!");
                                    this.animatedTextures.put(link, animatedTexture);
                                    return texture;
                                }
                                catch (Exception e) { e.printStackTrace(); }
                            }
                            
                            /* Candidate 2: Hint Frame */
                            if (pixels.width % minFW == 0 && pixels.width > minFW)
                            {
                                try
                                {
                                    AnimatedTexture animatedTexture = AnimatedTexture.create(pixels, null, (int) minFW, pixels.height, 3);
                                    texture = animatedTexture.getTexture(this.tick);
                                    System.out.println("Animated texture (Horizontal Hint) \"" + link + "\" detected (frame " + minFW + "x" + pixels.height + ") and loaded!");
                                    this.animatedTextures.put(link, animatedTexture);
                                    return texture;
                                }
                                catch (Exception e) { e.printStackTrace(); }
                            }
                        }
                    }
                    else if (link.path.toLowerCase().contains("models") && pixels.height > pixels.width && pixels.height % pixels.width == 0)
                    {
                        try
                        {
                            AnimatedTexture animatedTexture = AnimatedTexture.create(pixels, null, pixels.width, pixels.width, 3);

                            texture = animatedTexture.getTexture(this.tick);

                            System.out.println("Animated texture (Generic) \"" + link + "\" was detected and loaded!");

                            this.animatedTextures.put(link, animatedTexture);

                            return texture;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    texture = Texture.textureFromPixels(pixels, filter);

                    System.out.println("Texture \"" + link + "\" was loaded!");

                    this.textures.put(link, texture);
                }
                else
                {
                    this.textures.put(link, this.getError());

                    return this.getError();
                }
            }
            catch (Exception e)
            {
                if (!silent)
                {
                    e.printStackTrace();
                }

                texture = this.getError();

                this.textures.put(link, texture);
            }
        }

        return texture;
    }

    private Texture get(Link link)
    {
        if (this.animatedTextures.containsKey(link))
        {
            Texture texture = this.animatedTextures.get(link).getTexture(this.tick);

            return texture == null ? this.error : texture;
        }

        return this.textures.get(link);
    }

    public void delete()
    {
        for (Texture texture : this.textures.values())
        {
            texture.delete();
        }

        for (AnimatedTexture animatedTexture : this.animatedTextures.values())
        {
            animatedTexture.delete();
        }

        this.textures.clear();
        this.animatedTextures.clear();
        this.extruder.deleteAll();
    }

    public void registerTextureDimensions(Link link, int width, int height)
    {
        this.textureDimensionsHints.put(link, new org.joml.Vector2i(width, height));
        System.out.println("Registered texture dimension hint for " + link + ": " + width + "x" + height);
    }

    public void registerAnimatedTexture(Link link, Pixels pixels, int frameWidth, int frameHeight, int frameTime)
    {
        try
        {
            AnimatedTexture animatedTexture = AnimatedTexture.create(pixels, null, frameWidth, frameHeight, frameTime);
            Texture texture = animatedTexture.getTexture(this.tick);

            this.animatedTextures.put(link, animatedTexture);
            this.textures.put(link, texture);
            
            System.out.println("Registered manual animated texture for \"" + link + "\" (" + frameWidth + "x" + frameHeight + ")");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void update()
    {
        this.tick += 1;
    }

    /**
     * Watch dog listener implementation. This method should reload any texture
     * from "assets" source (which is in game's assets folder).
     */
    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        Link link = BBSMod.getProvider().getLink(path.toFile());

        if (link == null)
        {
            return;
        }

        if (link.path.endsWith(".mcmeta"))
        {
            link = new Link(link.source, StringUtils.removeExtension(link.path));
        }

        Texture texture = this.textures.remove(link);

        if (texture != null)
        {
            texture.delete();
        }

        AnimatedTexture remove = this.animatedTextures.remove(link);

        if (remove != null)
        {
            remove.delete();
        }

        this.extruder.delete(link);
    }
}