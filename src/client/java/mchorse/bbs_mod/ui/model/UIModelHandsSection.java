package elgatopro300.bbs_cml.ui.model;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.cubic.ModelInstance;
import elgatopro300.bbs_cml.cubic.model.ArmorSlot;
import elgatopro300.bbs_cml.cubic.model.ModelConfig;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIButton;
import elgatopro300.bbs_cml.ui.model.UIModelItemsSection.UIStringListContextMenu;
import elgatopro300.bbs_cml.ui.utils.UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIModelHandsSection extends UIModelSection
{
    public UIModelHandsSection(UIModelPanel editor)
    {
        super(editor);

        this.title.label = UIKeys.MODELS_HANDS;
        this.fields.add(UI.row(
            new UIButton(UIKeys.MODELS_ITEMS_FP_MAIN, (b) -> this.openArmorSlotContextMenu(this.config.fpMain)),
            new UIButton(UIKeys.MODELS_ITEMS_FP_OFF, (b) -> this.openArmorSlotContextMenu(this.config.fpOffhand))
        ));

        this.fields.add(new UIButton(UIKeys.MODELS_HANDS_EDIT, (b) ->
        {
            this.editor.dashboard.setPanel(new UIModelFirstPersonTransformEditor(this.editor, this.config));
        }));
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.MODELS_HANDS;
    }

    private void openArmorSlotContextMenu(ArmorSlot slot)
    {
        if (this.config == null) return;

        ModelInstance model = BBSModClient.getModels().getModel(this.config.getId());
        if (model == null) return;

        List<String> groups = new ArrayList<>(model.getModel().getAllGroupKeys());
        Collections.sort(groups);
        groups.add(0, "<none>");

        this.getContext().replaceContextMenu(new UIStringListContextMenu(groups,
            () -> Collections.singleton(slot.group.get().isEmpty() ? "<none>" : slot.group.get()),
            (group) ->
            {
                if (group.equals("<none>"))
                {
                    slot.group.set("");
                }
                else
                {
                    slot.group.set(group);
                }

                this.editor.dirty();
                this.editor.forceSave();
            }));
    }
}
