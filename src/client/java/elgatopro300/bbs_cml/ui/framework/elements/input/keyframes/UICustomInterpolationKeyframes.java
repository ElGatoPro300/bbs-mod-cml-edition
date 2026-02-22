package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes;

import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.graphs.UICustomInterpolationGraph;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;

import java.util.function.Consumer;

public class UICustomInterpolationKeyframes extends UIKeyframes
{
    public UICustomInterpolationKeyframes(Consumer<Keyframe> callback)
    {
        super(callback);
    }

    @Override
    public void editSheet(UIKeyframeSheet sheet)
    {
        if (sheet == null)
        {
            this.currentGraph = this.dopeSheet;
        }
        else
        {
            this.dopeSheet.clearSelection();
            this.dopeSheet.pickSelected();

            this.currentGraph = new UICustomInterpolationGraph(this, sheet);

            this.resetView();
        }
    }
}
