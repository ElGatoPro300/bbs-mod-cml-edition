package elgatopro300.bbs_cml.ui.forms;

import com.mojang.blaze3d.systems.RenderSystem;
import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.forms.FormCategories;
import elgatopro300.bbs_cml.forms.FormUtils;
import elgatopro300.bbs_cml.forms.categories.FormCategory;
import elgatopro300.bbs_cml.forms.categories.UserFormCategory;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.ui.Keys;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.forms.categories.UIFormCategory;
import elgatopro300.bbs_cml.ui.forms.categories.UIRecentFormCategory;
import elgatopro300.bbs_cml.ui.framework.UIContext;
import elgatopro300.bbs_cml.ui.framework.elements.UIElement;
import elgatopro300.bbs_cml.ui.framework.elements.UIScrollView;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.framework.elements.input.text.UITextbox;
import elgatopro300.bbs_cml.ui.framework.elements.utils.FontRenderer;
import elgatopro300.bbs_cml.ui.utils.UI;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.Direction;
import elgatopro300.bbs_cml.utils.colors.Colors;
import elgatopro300.bbs_cml.utils.joml.Matrices;
import net.minecraft.client.render.DiffuseLighting;
import org.joml.Vector3f;
import org.joml.Matrix3f;

import java.util.ArrayList;
import java.util.List;

public class UIFormList extends UIElement
{
    public IUIFormList palette;

    public UIScrollView forms;

    public UIElement bar;
    public UITextbox search;
    public UIIcon edit;
    public UIIcon close;

    private UIFormCategory recent;
    private List<UIFormCategory> categories = new ArrayList<>();

    private long lastUpdate;
    private int lastScroll;

    public UIFormList(IUIFormList palette)
    {
        this.palette = palette;

        this.forms = UI.scrollView(0, 0);
        this.forms.scroll.cancelScrolling();
        this.bar = new UIElement()
        {
            @Override
            public void render(UIContext context)
            {
                // context.batcher.getContext().getMatrices().push();
                // context.batcher.getContext().getMatrices().translate(0, 0, 200);
                super.render(context);
                // context.batcher.getContext().getMatrices().pop();
            }
        };
        this.search = new UITextbox(100, this::search).placeholder(UIKeys.FORMS_LIST_SEARCH);
        this.edit = new UIIcon(Icons.EDIT, this::edit);
        this.edit.tooltip(UIKeys.FORMS_LIST_EDIT, Direction.TOP);
        this.close = new UIIcon(Icons.CLOSE, this::close);

        this.forms.full(this);
        this.bar.relative(this).x(10).y(1F, -30).w(1F, -20).h(20).row().height(20);
        this.close.w(20);

        this.bar.add(this.search, this.edit, this.close);
        this.add(this.forms, this.bar);

        this.search.keys().register(Keys.FORMS_FOCUS, this::focusSearch);

        this.markContainer();
        this.setupForms(BBSModClient.getFormCategories());
    }

    private void focusSearch()
    {
        this.search.clickItself();
    }

    public void setupForms(FormCategories forms)
    {
        this.categories.clear();
        this.forms.removeAll();

        for (FormCategory category : forms.getAllCategories())
        {
            UIFormCategory uiCategory = category.createUI(this);

            this.forms.add(uiCategory);
            this.categories.add(uiCategory);

            if (uiCategory instanceof UIRecentFormCategory)
            {
                this.recent = uiCategory;
            }
        }

        this.categories.get(this.categories.size() - 1).marginBottom(40);
        this.resize();

        this.lastUpdate = forms.getLastUpdate();
    }

    private void search(String search)
    {
        search = search.trim();

        for (UIFormCategory category : this.categories)
        {
            category.search(search);
        }
    }

    private void edit(UIIcon b)
    {
        this.palette.toggleEditor();
    }

    private void close(UIIcon b)
    {
        this.palette.exit();
    }

    public void selectCategory(UIFormCategory category, Form form, boolean notify)
    {
        this.deselect();

        category.selected = form;

        if (notify)
        {
            this.palette.accept(form);
        }
    }

    public void deselect()
    {
        for (UIFormCategory category : this.categories)
        {
            category.selected = null;
        }
    }

    public UIFormCategory getSelectedCategory()
    {
        for (UIFormCategory category : this.categories)
        {
            if (category.selected != null)
            {
                return category;
            }
        }

        return null;
    }

    public Form getSelected()
    {
        UIFormCategory category = this.getSelectedCategory();

        return category == null ? null : category.selected;
    }

    public void setSelected(Form form)
    {
        boolean found = false;

        this.deselect();

        for (UIFormCategory category : this.categories)
        {
            int index = category.category.getForms().indexOf(form);

            if (index == -1)
            {
                category.selected = null;
            }
            else
            {
                found = true;

                category.select(category.category.getForms().get(index), false);
            }
        }

        if (!found && form != null)
        {
            Form copy = FormUtils.copy(form);

            this.recent.category.addForm(copy);
            this.recent.select(copy, false);
        }
    }

    public boolean handleFormDrop(UIFormCategory source, int sourceIndex, int mouseX, int mouseY)
    {
        for (UIFormCategory category : this.categories)
        {
            if (category != source && category.area.isInside(mouseX, mouseY) && category.category instanceof UserFormCategory)
            {
                int index = category.getIndexAt(mouseX, mouseY);
                
                if (index != -1)
                {
                    Form form = source.category.getForms().get(sourceIndex);
                    
                    ((UserFormCategory) category.category).addForm(index, form);
                    source.category.removeForm(form);
                    
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public void render(UIContext context)
    {
        FormCategories categories = BBSModClient.getFormCategories();

        if (this.lastScroll >= 0)
        {
            this.forms.scroll.scrollTo(this.lastScroll);

            this.lastScroll = -1;
        }

        if (this.lastUpdate != categories.getLastUpdate())
        {
            this.lastScroll = (int) this.forms.scroll.getScroll();

            Form selected = this.getSelected();

            this.setupForms(categories);
            this.setSelected(selected);
        }

        Vector3f a = new Vector3f(0.85F, 0.85F, -1F).normalize();
        Vector3f b = new Vector3f(-0.85F, 0.85F, 1F).normalize();

        // RenderSystem.setupLevelDiffuseLighting(a, b);

        super.render(context);

        // DiffuseLighting.disableGuiDepthLighting();

        /* Render form's display name and ID */
        Form selected = this.getSelected();

        if (selected != null)
        {
            String displayName = selected.getDisplayName();
            String id = selected.getFormId();
            FontRenderer font = context.batcher.getFont();

            int w = Math.max(font.getWidth(displayName), font.getWidth(id));
            int x = this.search.area.x;
            int y = this.search.area.y - 24;

            context.batcher.box(x, y, x + w + 8, this.search.area.y, Colors.A50);
            context.batcher.textShadow(displayName, x + 4, y + 4);
            context.batcher.textShadow(id, x + 4, y + 14, Colors.LIGHTEST_GRAY);
        }
    }
}
