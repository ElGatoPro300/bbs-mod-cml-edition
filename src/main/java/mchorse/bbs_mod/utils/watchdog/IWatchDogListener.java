package elgatopro300.bbs_cml.utils.watchdog;

import java.nio.file.Path;

public interface IWatchDogListener
{
    public void accept(Path path, WatchDogEvent event);
}