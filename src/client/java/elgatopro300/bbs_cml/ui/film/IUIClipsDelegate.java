package elgatopro300.bbs_cml.ui.film;

import elgatopro300.bbs_cml.camera.Camera;
import elgatopro300.bbs_cml.film.Film;
import elgatopro300.bbs_cml.settings.values.numeric.ValueInt;
import elgatopro300.bbs_cml.settings.values.base.BaseValue;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.utils.clips.Clip;

import java.util.function.Consumer;

public interface IUIClipsDelegate extends ICursor
{
    public Film getFilm();

    public Camera getCamera();

    public Clip getClip();

    public void pickClip(Clip clip);

    public void setFlight(boolean flight);

    public boolean isFlying();

    public boolean isRunning();

    public void togglePlayback();

    public boolean canUseKeybinds();

    public void fillData();

    public void embedView(UIElement element);

    /* Undo/redo */

    public void markLastUndoNoMerging();

    public void editMultiple(ValueInt property, int value);

    public <T extends BaseValue> void editMultiple(T property, Consumer<T> consumer);
}