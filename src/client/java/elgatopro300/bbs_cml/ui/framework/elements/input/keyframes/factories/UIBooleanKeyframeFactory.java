package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories;

import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIToggle;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframes;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;

public class UIBooleanKeyframeFactory extends UIKeyframeFactory<Boolean>
{
    private UIToggle toggle;

    public UIBooleanKeyframeFactory(Keyframe<Boolean> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        this.toggle = new UIToggle(UIKeys.GENERIC_KEYFRAMES_BOOLEAN_TRUE, (b) -> this.setValue(b.getValue()));
        this.toggle.setValue(keyframe.getValue());

        this.scroll.add(this.toggle);
    }
}