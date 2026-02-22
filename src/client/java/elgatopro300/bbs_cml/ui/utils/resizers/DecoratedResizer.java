package elgatopro300.bbs_cml.ui.utils.resizers;

public abstract class DecoratedResizer extends BaseResizer
{
    public IResizer resizer;

    public DecoratedResizer(IResizer resizer)
    {
        this.resizer = resizer;
    }
}