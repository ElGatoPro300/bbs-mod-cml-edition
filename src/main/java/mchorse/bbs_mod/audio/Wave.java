package mchorse.bbs_mod.audio;

import mchorse.bbs_mod.audio.wav.WaveCue;
import mchorse.bbs_mod.audio.wav.WaveList;
import mchorse.bbs_mod.utils.MathUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Wave
{
    public int audioFormat;
    public int numChannels;
    public int sampleRate;
    public int byteRate;
    public int blockAlign;
    public int bitsPerSample;

    public byte[] data;

    public List<WaveList> lists = new ArrayList<>();
    public List<WaveCue> cues = new ArrayList<>();

    public Wave(int audioFormat, int numChannels, int sampleRate, int bitsPerSample, byte[] data)
    {
        int bytesPerSample = bitsPerSample / 8;
        int byteRate = sampleRate * numChannels * bytesPerSample;
        int blockAlign = numChannels * bytesPerSample;

        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
        this.sampleRate = sampleRate;
        this.byteRate = byteRate;
        this.blockAlign = blockAlign;
        this.bitsPerSample = bitsPerSample;
        this.data = data;
    }

    public Wave(int audioFormat, int numChannels, int sampleRate, int byteRate, int blockAlign, int bitsPerSample, byte[] data)
    {
        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
        this.sampleRate = sampleRate;
        this.byteRate = byteRate;
        this.blockAlign = blockAlign;
        this.bitsPerSample = bitsPerSample;
        this.data = data;
    }

    public int getBytesPerSample()
    {
        return this.bitsPerSample / 8;
    }

    public float getDuration()
    {
        return this.data.length / (float) this.numChannels / (float) this.getBytesPerSample() / (float) this.sampleRate;
    }

    public int getALFormat()
    {
        int bytes = this.getBytesPerSample();

        if (bytes == 1)
        {
            if (this.numChannels == 2)
            {
                return AL10.AL_FORMAT_STEREO8;
            }
            else if (this.numChannels == 1)
            {
                return AL10.AL_FORMAT_MONO8;
            }
        }
        else if (bytes == 2)
        {
            if (this.numChannels == 2)
            {
                return AL10.AL_FORMAT_STEREO16;
            }
            else if (this.numChannels == 1)
            {
                return AL10.AL_FORMAT_MONO16;
            }
        }

        throw new IllegalStateException("Current WAV file has unusual configuration... channels: " + this.numChannels + ", BPS: " + bytes);
    }

    public int getScanRegion(float pixelsPerSecond)
    {
        return (int) (this.sampleRate / pixelsPerSecond) * this.getBytesPerSample() * this.numChannels;
    }

    public Wave convertTo16()
    {
        final int bytes = 16 / 8;

        int c = this.data.length / this.numChannels / this.getBytesPerSample();
        int byteRate = c * this.numChannels * bytes ;
        byte[] data = new byte[byteRate];
        boolean isFloat = this.getBytesPerSample() == 4;

        Wave wave = new Wave(this.audioFormat, this.numChannels, this.sampleRate, byteRate, bytes * this.numChannels, 16, data);

        ByteBuffer sample = MemoryUtil.memAlloc(4);
        ByteBuffer dataBuffer = MemoryUtil.memAlloc(data.length);

        for (int i = 0; i < c * this.numChannels; i++)
        {
            sample.clear();

            for (int j = 0; j < this.getBytesPerSample(); j++)
            {
                sample.put(this.data[i * this.getBytesPerSample() + j]);
            }

            if (isFloat)
            {
                sample.flip();
                dataBuffer.putShort((short) (sample.getFloat() * 0xffff / 2));
            }
            else
            {
                sample.put((byte) 0);
                sample.flip();
                dataBuffer.putShort((short) ((int) (sample.getInt() / (0xffffff / 2F) * (0xffff / 2F))));
            }
        }

        dataBuffer.flip();
        dataBuffer.get(data);

        MemoryUtil.memFree(sample);
        MemoryUtil.memFree(dataBuffer);

        wave.lists = this.lists;
        wave.cues = this.cues;

        return wave;
    }

    public float[] getCues()
    {
        float[] cues = new float[this.cues.size()];
        int i = 0;

        for (WaveCue cue : this.cues)
        {
            cues[i] = cue.position / (float) this.sampleRate;

            i += 1;
        }

        return cues;
    }

    public void add(ByteBuffer buffer, Wave wave, float offset, float shift, float duration)
    {
        int waveStart = this.truncate((int) (shift * wave.byteRate));
        int start = this.truncate((int) (offset * this.byteRate));
        int end = this.truncate((int) ((offset + duration) * this.byteRate));

        end = this.truncate(Math.min(end, this.data.length));

        float ratio = (float) wave.byteRate / (float) this.byteRate;

        for (int i = 0; start + i < end; i += 2)
        {
            int a = this.truncate(waveStart + (int) (i * ratio));
            int b = start + i;

            if (a >= wave.data.length)
            {
                break;
            }

            buffer.position(0);
            buffer.put(wave.data[a]);
            buffer.put(wave.data[a + 1]);

            int waveShort = buffer.getShort(0);

            buffer.position(0);
            buffer.put(this.data[b]);
            buffer.put(this.data[b + 1]);

            int bytesShort = buffer.getShort(0);
            int finalShort = waveShort + bytesShort;

            buffer.putShort(0, (short) MathUtils.clamp(finalShort, Short.MIN_VALUE, Short.MAX_VALUE));

            this.data[b + 1] = buffer.get(1);
            this.data[b] =     buffer.get(0);
        }
    }

    private int truncate(int offset)
    {
        return offset - offset % 2;
    }
}