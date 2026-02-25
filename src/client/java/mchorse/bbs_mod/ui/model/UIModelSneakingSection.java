package elgatopro300.bbs_cml.ui.model;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.utils.UILabel;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.ui.utils.presets.UIDataContextMenu;
import elgatopro300.bbs_cml.utils.pose.PoseManager;

public class UIModelSneakingSection extends UIModelSection
{
    public UIButton menu;

    public UIModelSneakingSection(UIModelPanel editor)
    {
        super(editor);

        this.menu = new UIButton(IKey.constant("Pick a sneaking pose..."), (b) ->
            {
                if (this.config == null)
                {
                    return;
                }

                String group = this.config.poseGroup.get();

                if (group.isEmpty())
                {
                    group = this.config.getId();
                }

                UIDataContextMenu menu = new UIDataContextMenu(PoseManager.INSTANCE, group, () ->
                {
                    BaseType data = this.config.sneakingPose.toData();
                    return data.isMap() ? data.asMap() : new MapType();
                }, (data) ->
                {
                    this.config.sneakingPose.fromData(data);
                    this.editor.dirty();
                });

                menu.remove(menu.row);
                menu.entries.relative(menu).y(5).h(1F, -10);

                this.getContext().setContextMenu(menu);
            });

        this.fields.add(this.menu);
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.MODELS_SNEAKING_TITLE;
    }
}
