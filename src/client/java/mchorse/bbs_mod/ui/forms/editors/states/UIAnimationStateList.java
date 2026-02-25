package elgatopro300.bbs_cml.ui.forms.editors.states;

import elgatopro300.bbs_cml.forms.states.AnimationState;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.input.list.UIList;

import java.util.List;
import java.util.function.Consumer;

public class UIAnimationStateList extends UIList<AnimationState>
{
    public UIAnimationStateList(Consumer<List<AnimationState>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected String elementToString(UIContext context, int i, AnimationState element)
    {
        String s = element.customId.get();

        if (!s.trim().isEmpty())
        {
            return s;
        }

        return element.id.get();
    }
}