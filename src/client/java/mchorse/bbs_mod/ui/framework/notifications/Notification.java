package elgatopro300.bbs_cml.ui.framework.notifications;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.interps.Interpolations;
import elgatopro300.bbs_cml.utils.interps.Lerps;

public class Notification
{
    public static final int TOTAL_LENGTH = 80;

    public IKey message;
    public int background;
    public int color;

    public int tick;

    public Notification(IKey message, int background, int color)
    {
        this.message = message;
        this.background = background | Colors.A100;
        this.color = color| Colors.A100;

        this.tick = TOTAL_LENGTH;
    }

    public boolean isExpired()
    {
        return this.tick <= 0;
    }

    public float getFactor(float transition)
    {
        float envelope = Lerps.envelope(this.tick - transition, 0F, 20F, 70F, 80F);

        return Interpolations.QUAD_INOUT.interpolate(0F, 1F, envelope);
    }

    public void update()
    {
        this.tick -= 1;
    }
}