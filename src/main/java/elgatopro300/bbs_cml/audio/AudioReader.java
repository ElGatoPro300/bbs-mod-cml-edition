package elgatopro300.bbs_cml.audio;

import elgatopro300.bbs_cml.audio.ogg.VorbisReader;
import elgatopro300.bbs_cml.audio.wav.WaveReader;
import elgatopro300.bbs_cml.resources.AssetProvider;
import elgatopro300.bbs_cml.resources.Link;

import java.io.InputStream;

public class AudioReader
{
    public static Wave read(AssetProvider provider, Link link) throws Exception
    {
        String pathLower = link.path.toLowerCase();

        if (!pathLower.endsWith(".wav") && !pathLower.endsWith(".ogg"))
        {
            return null;
        }

        /* System.out.println("Reading: " + link); */

        try (InputStream asset = provider.getAsset(link))
        {
            if (pathLower.endsWith(".wav"))
            {
                return new WaveReader().read(asset);
            }
            else if (pathLower.endsWith(".ogg"))
            {
                return VorbisReader.read(link, asset);
            }
        }

        throw new IllegalStateException("Given link " + link + " isn't a Wave or a Vorbis file!");
    }
}