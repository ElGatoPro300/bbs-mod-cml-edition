package elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.factories;

import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.editors.utils.UIStructureOverlayPanel;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.input.UITexturePicker;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import elgatopro300.bbs_cml.ui.framework.elements.input.keyframes.UIKeyframes;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlay;
import elgatopro300.bbs_cml.utils.keyframes.Keyframe;

public class UILinkKeyframeFactory extends UIKeyframeFactory<Link>
{
    public UILinkKeyframeFactory(Keyframe<Link> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        UIKeyframeSheet sheet = editor.getGraph().getSheet(keyframe);
        boolean isStructureFile = sheet != null && ("structure_file".equals(sheet.id) || sheet.id.endsWith("/structure_file"));

        if (isStructureFile)
        {
            UIButton pickStructure = new UIButton(UIKeys.FORMS_EDITORS_STRUCTURE_PICK_STRUCTURE, (b) ->
            {
                UIStructureOverlayPanel panel = new UIStructureOverlayPanel(UIKeys.FORMS_EDITORS_STRUCTURE_PICK_STRUCTURE, (link) ->
                {
                    this.editor.getGraph().setValue(link, true);
                });

                Link current = this.keyframe.getValue();
                panel.set(current == null ? "" : current.toString());

                UIOverlay.addOverlay(this.getContext(), panel, 280, 0.5F);
            });

            this.scroll.add(pickStructure);
        }
        else
        {
            this.scroll.add(new UIButton(UIKeys.GENERIC_KEYFRAMES_LINK_PICK_TEXTURE, (b) ->
            {
                UITexturePicker.open(this.getContext(), this.keyframe.getValue(), (l) ->
                {
                    this.editor.getGraph().setValue(l, true);
                });
            }));
        }
    }
}