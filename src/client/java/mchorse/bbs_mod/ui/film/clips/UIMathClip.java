package elgatopro300.bbs_cml.ui.film.clips;

import elgatopro300.bbs_cml.camera.clips.modifiers.MathClip;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.film.IUIClipsDelegate;
import elgatopro300.bbs_cml.ui.film.clips.widgets.UIBitToggle;
import elgatopro300.bbs_cml.ui.film.utils.UITextboxHelp;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.utils.colors.Colors;

public class UIMathClip extends UIClip<MathClip>
{
    public UITextboxHelp expression;
    public UIBitToggle active;

    public UIMathClip(MathClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.expression = new UITextboxHelp(1000, (str) ->
        {
            this.clip.expression.setExpression(str);
            this.expression.setColor(!this.clip.expression.isErrored() ? Colors.WHITE : Colors.RED);
        });
        this.expression.link("https://github.com/mchorse/aperture/wiki/Math-Expressions").tooltip(UIKeys.CAMERA_PANELS_MATH);

        this.active = new UIBitToggle((value) -> this.clip.active.set(value)).all();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_EXPRESSION), this.expression).marginTop(12));
        this.panels.add(this.active);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.expression.setText(this.clip.expression.toString());
        this.expression.setColor(Colors.WHITE);
        this.active.setValue(this.clip.active.get());
    }
}