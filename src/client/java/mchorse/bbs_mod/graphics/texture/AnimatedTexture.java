package elgatopro300.bbs_cml.graphics.texture;

import elgatopro300.bbs_cml.data.DataToString;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.ListType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.utils.CollectionUtils;
import elgatopro300.bbs_cml.utils.IOUtils;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeChannel;
import elgatopro300.bbs_cml.utils.keyframes.KeyframeSegment;
import elgatopro300.bbs_cml.utils.keyframes.factories.KeyframeFactories;
import elgatopro300.bbs_cml.utils.resources.Pixels;
import org.lwjgl.opengl.GL11;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AnimatedTexture
{
    public final List<Texture> textures = new ArrayList<>();
    public final KeyframeChannel<Integer> index;
    public final int length;

    public static AnimatedTexture load(InputStream stream, Pixels pixels) throws Exception
    {
        MapType data = DataToString.mapFromString(IOUtils.readText(stream));
        MapType animation = data.getMap("animation");
        ListType listFrames = animation.getList("frames");

        int frameTime = animation.getInt("frametime", 1);
        int w = animation.getInt("width", Math.min(pixels.width, pixels.height));
        int h = animation.getInt("height", Math.min(pixels.width, pixels.height));

        AnimatedTexture texture = computeFrames(pixels, listFrames, h, frameTime);

        for (int i = 0, c = pixels.height / h; i < c; i++)
        {
            Pixels newPixels = Pixels.fromSize(w, h);

            newPixels.drawPixels(pixels, 0, 0, w, h, 0, i * h, w, i * h + h);
            newPixels.rewindBuffer();

            Texture e = Texture.textureFromPixels(newPixels, GL11.GL_NEAREST);

            texture.textures.add(e);
            e.setParent(texture);
        }

        if (texture.textures.isEmpty())
        {
            throw new Exception("For some reason, the animated texture is empty...");
        }

        return texture;
    }

    private static AnimatedTexture computeFrames(Pixels pixels, ListType frames, int h, int frameTime)
    {
        KeyframeChannel<Integer> index = new KeyframeChannel<>("", KeyframeFactories.INTEGER);
        int length = 0;

        if (frames == null || frames.isEmpty())
        {
            int c = pixels.height / h;

            for (int i = 0; i < c; i++)
            {
                index.insert(i * frameTime, i);
            }

            length = c * frameTime;
        }
        else
        {
            int x = 0;

            for (BaseType frame : frames)
            {
                int i = 0;
                int time = frameTime;

                if (frame.isNumeric())
                {
                    i = frame.asNumeric().intValue();
                }
                else if (frame.isMap())
                {
                    MapType map = frame.asMap();

                    i = map.getInt("index", 0);
                    time = map.getInt("time", frameTime);
                }

                index.insert(x, i);

                x += time;
            }

            length = x;
        }

        return new AnimatedTexture(index, length);
    }

    public AnimatedTexture(KeyframeChannel<Integer> index, int length)
    {
        this.index = index;
        this.length = length;
    }

    public Texture getTexture(int tick)
    {
        if (this.length == 0)
        {
            return null;
        }

        KeyframeSegment<Integer> segment = this.index.find(tick % this.length);
        int frame = segment == null ? 0 : segment.a.getValue();
        Texture texture = CollectionUtils.getSafe(this.textures, frame);

        return texture == null ? CollectionUtils.getSafe(this.textures, 0) : texture;
    }

    public void delete()
    {
        for (Texture texture : this.textures)
        {
            texture.delete();
        }
    }
}