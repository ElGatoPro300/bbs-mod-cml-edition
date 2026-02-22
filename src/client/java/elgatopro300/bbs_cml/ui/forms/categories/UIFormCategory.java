package elgatopro300.bbs_cml.ui.forms.categories;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.BBSSettings;
import elgatopro300.bbs_cml.cubic.model.ModelManager;
import elgatopro300.bbs_cml.data.DataStringifier;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.forms.FormCategories;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.categories.FormCategory;
import elgatopro300.bbs_cml.forms.categories.UserFormCategory;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.forms.forms.ModelForm;
import elgatopro300.bbs_cml.forms.forms.StructureForm;
import elgatopro300.bbs_cml.forms.sections.UserFormSection;
import elgatopro300.bbs_cml.graphics.window.Window;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.network.ClientNetwork;
import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.UIFormList;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIOverlay;
import elgatopro300.bbs_cml.ui.framework.elements.overlay.UIPromptOverlayPanel;
import elgatopro300.bbs_cml.ui.utils.UIUtils;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.colors.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UIFormCategory extends UIElement
{
    public static final int HEADER_HEIGHT = 20;
    public static final int CELL_WIDTH = 60;
    public static final int CELL_HEIGHT = 80;

    public UIFormList list;
    public FormCategory category;
    public Form selected;

    private int last;
    private String search = "";
    private List<Form> searched = new ArrayList<>();

    private boolean dragging;
    private int dragIndex = -1;
    private long dragStart;

    public UIFormCategory(FormCategory category, UIFormList list)
    {
        this.category = category;
        this.list = list;

        this.context((menu) ->
        {
            FormCategories formCategories = BBSModClient.getFormCategories();
            UserFormSection userForms = formCategories.getUserForms();

            menu.action(Icons.EDIT, UIKeys.GENERAL_EDIT, () ->
            {
                this.list.palette.toggleEditor();
            });

            if (this.selected instanceof ModelForm)
            {
                menu.action(Icons.FOLDER, UIKeys.FORMS_CATEGORIES_CONTEXT_OPEN_MODEL_FOLDER, () ->
                {
                    ModelForm form = (ModelForm) this.selected;

                    UIUtils.openFolder(BBSMod.getAssetsPath(ModelManager.MODELS_PREFIX + form.model.get() + "/"));
                });
            }

            menu.action(Icons.ADD, UIKeys.FORMS_CATEGORIES_CONTEXT_ADD_CATEGORY, () ->
            {
                UIOverlay.addOverlay(this.getContext(), new UIPromptOverlayPanel(
                    UIKeys.FORMS_CATEGORIES_ADD_CATEGORY_TITLE,
                    UIKeys.FORMS_CATEGORIES_ADD_CATEGORY_DESCRIPTION,
                    (str) ->
                    {
                        userForms.addUserCategory(new UserFormCategory(IKey.constant(str), formCategories.visibility.get(UUID.randomUUID().toString()), userForms));
                        list.setupForms(formCategories);
                    }
                ));
            });

            menu.action(Icons.REFRESH, UIKeys.FORMS_CATEGORIES_ORDER, () ->
            {
                UIOverlay.addOverlay(this.getContext(), new UIOrderCategoriesOverlayPanel(userForms, () ->
                {
                    list.setupForms(formCategories);
                }), 240, 0.6F);
            });

            if (this.selected != null)
            {
                menu.action(Icons.COPY, UIKeys.FORMS_CATEGORIES_CONTEXT_COPY_FORM, () -> Window.setClipboard(FormUtils.toData(this.selected)));
                menu.action(Icons.COPY, UIKeys.FORMS_CATEGORIES_CONTEXT_COPY_TO_CATEGORY, () ->
                {
                    this.getContext().replaceContextMenu((m) ->
                    {
                        for (UserFormCategory formCategory : userForms.categories)
                        {
                            if (formCategory == this.category)
                            {
                                continue;
                            }

                            m.action(Icons.ADD, UIKeys.FORMS_CATEGORIES_CONTEXT_COPY_TO.format(formCategory.getProcessedTitle()), () ->
                            {
                                formCategory.addForm(FormUtils.copy(this.selected));
                            });
                        }
                    });
                });
                menu.action(Icons.COPY, UIKeys.FORMS_CATEGORIES_CONTEXT_COPY_COMMAND, () ->
                {
                    MapType data = FormUtils.toData(this.selected);
                    DataStringifier stringifier = new DataStringifier();
                    String name = MinecraftClient.getInstance().player.getGameProfile().getName();

                    stringifier.jsonLike();
                    stringifier.indent = "";

                    Window.setClipboard("/bbs morph " + name + " " + stringifier.toString(data));
                });

                Collection<PlayerListEntry> playerList = MinecraftClient.getInstance().getNetworkHandler().getPlayerList();

                if (playerList.size() > 1)
                {
                    menu.action(Icons.ARROW_RIGHT, UIKeys.FORMS_CATEGORIES_CONTEXT_SHARE_FORM, () ->
                    {
                        this.getContext().replaceContextMenu((newMenu) ->
                        {
                            for (PlayerListEntry entry : playerList)
                            {
                                if (entry.getProfile().getId().equals(MinecraftClient.getInstance().player.getGameProfile().getId()))
                                {
                                    continue;
                                }

                                newMenu.action(Icons.ARROW_RIGHT, IKey.constant(entry.getProfile().getName()), () ->
                                {
                                    ClientNetwork.sendSharedForm(this.selected, entry.getProfile().getId());
                                });
                            }
                        });
                    });
                }
            }
        });

        this.h(20);
    }

    public void search(String search)
    {
        this.search = search.toLowerCase();

        this.searched.clear();

        if (search.isEmpty())
        {
            return;
        }

        for (Form form : this.category.getForms())
        {
            if (form.getFormId().toLowerCase().contains(search) || form.getDisplayName().toLowerCase().contains(search))
            {
                this.searched.add(form);
            }
        }
    }

    public List<Form> getForms()
    {
        if (this.search.isEmpty())
        {
            return this.category.getForms();
        }

        return this.searched;
    }

    public int getIndexAt(int mouseX, int mouseY)
    {
        int x = mouseX - this.area.x;
        int y = mouseY - this.area.y - HEADER_HEIGHT;
        int perRow = this.area.w / CELL_WIDTH;

        if (x >= 0 && y >= 0)
        {
            x /= CELL_WIDTH;
            y /= CELL_HEIGHT;
            int i = x + y * perRow;
            int size = this.getForms().size();

            if (i >= 0 && i <= size)
            {
                return Math.min(i, size);
            }
        }
        
        // If below the last row, return size (append)
        if (y >= 0 && (mouseY - this.area.y) < this.area.h) {
             return this.getForms().size();
        }

        return -1;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context))
        {
            int x = context.mouseX - this.area.x;
            int y = context.mouseY - this.area.y - HEADER_HEIGHT;
            int perRow = this.area.w / CELL_WIDTH;

            if (y < 0)
            {
                if (x < this.area.x + 30 + context.batcher.getFont().getWidth(this.category.title.get()))
                {
                    this.category.visible.set(!this.category.visible.get());

                    return true;
                }
                else
                {
                    return super.subMouseClicked(context);
                }
            }

            x /= CELL_WIDTH;
            y /= CELL_HEIGHT;

            List<Form> forms = this.getForms();
            int i = x + y * perRow;

            if (i >= 0 && i < forms.size())
            {
                if (context.mouseButton == 0 && this.category instanceof UserFormCategory && this.search.isEmpty())
                {
                    this.dragIndex = i;
                    this.dragStart = System.currentTimeMillis();
                    this.dragging = false;
                }

                this.select(forms.get(i), true);
            }
            else
            {
                this.select(null, true);
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        if (this.dragIndex != -1)
        {
            if (this.dragging && this.category instanceof UserFormCategory)
            {
                int x = context.mouseX - this.area.x;
                int y = context.mouseY - this.area.y - HEADER_HEIGHT;
                int perRow = this.area.w / CELL_WIDTH;

                if (this.area.isInside(context.mouseX, context.mouseY))
                {
                    x /= CELL_WIDTH;
                    y /= CELL_HEIGHT;
                    int i = x + y * perRow;

                    if (i >= 0 && i < this.getForms().size())
                    {
                        ((UserFormCategory) this.category).moveForm(this.dragIndex, i);
                    }
                }
                else
                {
                     this.list.handleFormDrop(this, this.dragIndex, context.mouseX, context.mouseY);
                }
            }

            this.dragIndex = -1;
            this.dragging = false;
        }

        return super.subMouseReleased(context);
    }

    public void select(Form form, boolean notify)
    {
        if (this.list != null)
        {
            this.list.selectCategory(this, form, notify);
        }

        this.selected = form;
    }

    @Override
    public void render(UIContext context)
    {
        if (this.dragIndex != -1 && !this.dragging && this.category instanceof UserFormCategory)
        {
            if (System.currentTimeMillis() - this.dragStart > 250)
            {
                this.dragging = true;
            }
        }

        super.render(context);

        context.batcher.textCard(this.category.getProcessedTitle(), this.area.x + 26, this.area.y + 6);

        if (this.category.visible.get())
        {
            context.batcher.icon(Icons.MOVE_DOWN, this.area.x + 16, this.area.y + 5, 0.5F, 0F);
        }
        else
        {
            context.batcher.icon(Icons.MOVE_UP, this.area.x + 16, this.area.y + 4, 0.5F, 0F);
        }

        List<Form> forms = this.getForms();
        int h = HEADER_HEIGHT;
        int x = 0;
        int i = 0;
        int perRow = this.area.w / CELL_WIDTH;

        if (!forms.isEmpty() && this.category.visible.get())
        {
            for (Form form : forms)
            {
                if (i == perRow)
                {
                    h += CELL_HEIGHT;
                    x = 0;
                    i = 0;
                }

                int cx = this.area.x + x;
                int cy = this.area.y + h;
                boolean isSelected = this.selected == form;

                context.batcher.clip(cx, cy, CELL_WIDTH, CELL_HEIGHT, context);

                if (isSelected)
                {
                    context.batcher.box(cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT, Colors.A50 | BBSSettings.primaryColor.get());
                    context.batcher.outline(cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT, Colors.A50 | BBSSettings.primaryColor.get(), 2);
                }

                FormUtilsClient.renderUI(form, context, cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT);

                context.batcher.unclip(context);

                x += CELL_WIDTH;
                i += 1;
            }

            h += CELL_HEIGHT;
        }

        if (this.last != h)
        {
            this.last = h;

            UIElement container = this.getParentContainer();

            if (container != null)
            {
                this.h(h);
                container.resize();
            }
        }

        if (this.dragging && this.dragIndex != -1 && this.category instanceof UserFormCategory)
        {
            Form form = this.getForms().get(this.dragIndex);
            int cx = context.mouseX - CELL_WIDTH / 2;
            int cy = context.mouseY - CELL_HEIGHT / 2;

            context.batcher.box(cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT, Colors.A50 | BBSSettings.primaryColor.get());
            context.batcher.outline(cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT, Colors.A50 | BBSSettings.primaryColor.get(), 2);

            FormUtilsClient.renderUI(form, context, cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT);
        }
    }
}