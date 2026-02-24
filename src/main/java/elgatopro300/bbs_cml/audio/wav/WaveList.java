package elgatopro300.bbs_cml.audio.wav;

import elgatopro300.bbs_cml.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class WaveList
{
    public String type;
    public List<Pair<String, String>> entries = new ArrayList<>();

    public WaveList(String type)
    {
        this.type = type;
    }
}