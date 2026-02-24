package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.ui.film.UIFilmPreview;
import java.util.function.Consumer;

public class RegisterFilmPreviewEvent
{
    public void register(Consumer<UIFilmPreview> consumer)
    {
        UIFilmPreview.extensions.add(consumer);
    }
}
