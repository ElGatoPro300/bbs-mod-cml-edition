package mchorse.bbs_mod.ui.model;

import mchorse.bbs_mod.cubic.model.ArmorSlot;
import mchorse.bbs_mod.cubic.model.ArmorType;
import mchorse.bbs_mod.cubic.model.ModelConfig;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs_mod.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs_mod.ui.framework.elements.utils.UILabel;
import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.pose.UIPoseEditor;
import mchorse.bbs_mod.utils.colors.Colors;
import mchorse.bbs_mod.BBSSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIModelArmorSection extends UIModelSection
{
    public UISearchList<String> types;
    public UIStringList typesList;
    public UIButton pickBone;

    private ArmorType type;

    public UIModelArmorSection(UIModelPanel editor)
    {
        super(editor);

        this.pickBone = new UIButton(IKey.constant("<none>"), (b) -> this.openLimbMenu());

        this.typesList = new UIStringList((l) -> this.fillData());
        this.typesList.background = 0x88000000;
        
        this.types = new UISearchList<>(this.typesList);
        this.types.label(UIKeys.GENERAL_SEARCH);

        for (ArmorType type : ArmorType.values())
        {
            this.typesList.add(type.name().toLowerCase());
        }

        this.typesList.sort();
        this.typesList.setIndex(0);

        this.fields.add(this.pickBone);
        this.types.h(5 * 16 + 20);

        UIPoseEditor poseEditor = this.editor.getPoseEditor();

        if (poseEditor != null)
        {
            UILabel label = UI.label(UIKeys.MODELS_ARMOR).background(() -> Colors.A50 | BBSSettings.primaryColor.get());
            label.marginTop(2);

            poseEditor.extra.add(label, this.types);
        }
    }

    @Override
    public void render(UIContext context)
    {
        if (this.editor.getPoseEditor() != null)
        {
            String group = this.editor.getPoseEditor().getGroup();

            this.pickBone.setEnabled(group == null || group.isEmpty());
        }

        super.render(context);
    }

    private void openLimbMenu()
    {
        if (this.config == null)
        {
            return;
        }

        ModelInstance model = BBSModClient.getModels().getModel(this.config.getId());

        if (model == null)
        {
            return;
        }

        List<String> groups = new ArrayList<>(model.getModel().getAllGroupKeys());
        Collections.sort(groups);
        groups.add(0, "<none>");

        UIModelItemsSection.UIStringListContextMenu menu = new UIModelItemsSection.UIStringListContextMenu(groups, () ->
        {
            String label = this.pickBone.label.get();

            return Collections.singleton(label.isEmpty() ? "<none>" : label);
        }, (group) ->
        {
            if (group.equals("<none>"))
            {
                group = "";
            }

            this.pickBone.label = IKey.constant(group.isEmpty() ? "<none>" : group);

            ArmorSlot slot = this.getSlot();

            if (slot != null)
            {
                slot.group.set(group);
                this.editor.dirty();
            }
        });

        this.getContext().replaceContextMenu(menu);
        menu.xy(this.pickBone.area.x, this.pickBone.area.ey()).w(this.pickBone.area.w).h(200).bounds(this.getContext().menu.overlay, 5);
    }

    private ArmorSlot getSlot()
    {
        if (this.config == null || this.type == null)
        {
            return null;
        }

        return this.config.armorSlots.get(this.type);
    }

    private void fillData()
    {
        if (this.typesList == null)
        {
            return;
        }

        String selected = this.typesList.getIndex() >= 0 ? this.typesList.getList().get(this.typesList.getIndex()) : null;

        if (selected == null)
        {
            this.type = null;
            return;
        }

        try
        {
            this.type = ArmorType.valueOf(selected.toUpperCase());
        }
        catch (Exception e)
        {
            this.type = null;
        }

        ArmorSlot slot = this.getSlot();

        if (slot != null)
        {
            String group = slot.group.get();

            this.pickBone.label = IKey.constant(group.isEmpty() ? "<none>" : group);

            UIPoseEditor poseEditor = this.editor.getPoseEditor();

            if (poseEditor != null)
            {
                poseEditor.setTransform(slot.transform);
                poseEditor.onChange = this.editor::dirty;
                poseEditor.transform.callbacks(() -> slot.preNotify(0), () ->
                {
                    slot.postNotify(0);
                    this.editor.dirty();
                });

                this.editor.setRight(poseEditor);
            }
        }
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.title.area.isInside(context) && context.mouseButton == 0)
        {
            UIPoseEditor poseEditor = this.editor.getPoseEditor();

            if (poseEditor != null)
            {
                this.editor.setRight(poseEditor);
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.MODELS_ARMOR;
    }

    @Override
    public void deselect()
    {
        this.typesList.deselect();
    }

    @Override
    public void setConfig(ModelConfig config)
    {
        super.setConfig(config);
        this.fillData();
    }
}
