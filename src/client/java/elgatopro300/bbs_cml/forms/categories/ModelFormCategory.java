package elgatopro300.bbs_cml.forms.categories;

import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.settings.values.numeric.ValueBoolean;
import elgatopro300.bbs_cml.ui.forms.UIFormList;
import elgatopro300.bbs_cml.ui.forms.categories.UIFormCategory;
import elgatopro300.bbs_cml.ui.forms.categories.UIModelFormCategory;

import java.util.ArrayList;
import java.util.List;

public class ModelFormCategory extends FormCategory
{
    public ModelFormCategory(IKey title, ValueBoolean visibility)
    {
        super(title, visibility);
    }

    @Override
    public UIFormCategory createUI(UIFormList list)
    {
        return new UIModelFormCategory(this, list);
    }

    public static class Folder extends ModelFormCategory
    {
        public final String path;
        public final String name;
        public Folder parent;
        public int depth;
        public final List<Folder> children = new ArrayList<>();

        public Folder(IKey title, ValueBoolean visibility, String path, String name)
        {
            super(title, visibility);

            this.path = path;
            this.name = name;
        }

        public Folder getParent()
        {
            return this.parent;
        }

        public int getDepth()
        {
            return this.depth;
        }

        public List<Folder> getChildren()
        {
            return this.children;
        }
    }
}
