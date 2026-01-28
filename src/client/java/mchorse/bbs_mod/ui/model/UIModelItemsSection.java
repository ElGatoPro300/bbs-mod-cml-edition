package mchorse.bbs_mod.ui.model;

import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.cubic.model.ModelConfig;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.settings.values.core.ValueList;
import mchorse.bbs_mod.settings.values.core.ValueString;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.context.UIContextMenu;
import mchorse.bbs_mod.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs_mod.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs_mod.ui.framework.elements.utils.UILabel;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Colors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class UIModelItemsSection extends UIModelSection
{
    private UIButton mainButton;
    private UIButton offButton;

    public UIModelItemsSection(UIModelPanel editor)
    {
        super(editor);

        this.title.label = UIKeys.MODELS_ITEMS;

        this.mainButton = new UIButton(UIKeys.MODELS_ITEMS_MAIN, (b) -> this.openContextMenu(this.config.itemsMain));
        this.offButton = new UIButton(UIKeys.MODELS_ITEMS_OFF, (b) -> this.openContextMenu(this.config.itemsOff));

        this.fields.add(UI.row(this.mainButton, this.offButton));
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.MODELS_ITEMS;
    }

    private void openContextMenu(ValueList<ValueString> valueList)
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

        UIStringListContextMenu menu = new UIStringListContextMenu(groups, () ->
        {
            List<String> list = new ArrayList<>();

            for (ValueString value : valueList.getList())
            {
                list.add(value.get());
            }

            return list;
        }, (group) ->
        {
            if (group.equals("<none>"))
            {
                valueList.getAllTyped().clear();
            }
            else
            {
                boolean exists = false;
                int index = -1;
                int i = 0;

                for (ValueString value : valueList.getList())
                {
                    if (value.get().equals(group))
                    {
                        exists = true;
                        index = i;
                        break;
                    }

                    i++;
                }

                if (exists)
                {
                    valueList.getAllTyped().remove(index);
                }
                else
                {
                    /* Only one limb is allowed */
                    valueList.getAllTyped().clear();
                    valueList.add(new ValueString(String.valueOf(valueList.getList().size()), group));
                }
            }

            this.editor.dirty();
            this.updateButtons();
        });

        this.getContext().replaceContextMenu(menu);
    }

    private void updateButtons()
    {
        if (this.config == null)
        {
            return;
        }

        this.updateButton(this.mainButton, this.config.itemsMain, UIKeys.MODELS_ITEMS_MAIN);
        this.updateButton(this.offButton, this.config.itemsOff, UIKeys.MODELS_ITEMS_OFF);
    }

    private void updateButton(UIButton button, ValueList<ValueString> list, IKey defaultLabel)
    {
        button.label = defaultLabel;
    }

    @Override
    public void deselect()
    {}

    @Override
    public void setConfig(ModelConfig config)
    {
        super.setConfig(config);

        this.updateButtons();
    }

    public static class UIStringListContextMenu extends UIContextMenu
    {
        public UISearchList<String> list;

        public UIStringListContextMenu(List<String> groups, java.util.function.Supplier<Collection<String>> selected, Consumer<String> callback)
        {
            this.list = new UISearchList<>(new UIStringList((l) ->
            {
                if (l.get(0) != null)
                {
                    callback.accept(l.get(0));
                }
            })
            {
                @Override
                protected void renderElementPart(UIContext context, String element, int i, int x, int y, boolean hover, boolean selectedState)
                {
                    if (selected.get().contains(element))
                    {
                        context.batcher.box(x, y, x + this.area.w, y + this.scroll.scrollItemSize, Colors.A50 | BBSSettings.primaryColor.get());
                    }

                    super.renderElementPart(context, element, i, x, y, hover, selectedState);
                }
            });
            this.list.list.setList(groups);
            this.list.list.background = 0xaa000000;
            this.list.relative(this).xy(5, 5).w(1F, -10).h(1F, -10);
            this.list.search.placeholder(UIKeys.POSE_CONTEXT_NAME);

            this.add(this.list);
        }

        @Override
        public boolean isEmpty()
        {
            return this.list.list.getList().isEmpty();
        }

        @Override
        public void setMouse(UIContext context)
        {
            this.xy(context.mouseX(), context.mouseY()).w(120).h(200).bounds(context.menu.overlay, 5);
        }
    }
}
