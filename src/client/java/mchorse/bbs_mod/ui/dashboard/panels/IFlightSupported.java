package elgatopro300.bbs_cml.ui.dashboard.panels;

public interface IFlightSupported
{
    public default boolean supportsRollFOVControl()
    {
        return true;
    }
}