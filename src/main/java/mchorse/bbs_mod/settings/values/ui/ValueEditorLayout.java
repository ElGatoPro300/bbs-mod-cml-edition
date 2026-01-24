package mchorse.bbs_mod.settings.values.ui;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.settings.values.base.BaseValue;
import mchorse.bbs_mod.utils.MathUtils;

public class ValueEditorLayout extends BaseValue
{
    public static final int LAYOUT_HORIZONTAL_BOTTOM = 0;
    public static final int LAYOUT_HORIZONTAL_TOP = 1;
    public static final int LAYOUT_VERTICAL_LEFT = 2;
    public static final int LAYOUT_VERTICAL_RIGHT = 3;

    private int layout = LAYOUT_HORIZONTAL_BOTTOM;
    private boolean layoutLocked;
    private float mainSizeH = 0.66F;
    private float mainSizeV = 0.66F;
    private float editorSizeH = 0.5F;
    private float editorSizeV = 0.5F;
    private float stateEditorSizeH = 0.7F;
    private float stateEditorSizeV = 0.25F;

    public ValueEditorLayout(String id)
    {
        super(id);
    }

    public void setHorizontal(boolean horizontal)
    {
        BaseValue.edit(this, (v) -> this.layout = horizontal ? LAYOUT_HORIZONTAL_BOTTOM : LAYOUT_VERTICAL_LEFT);
    }

    public void setLayout(int layout)
    {
        BaseValue.edit(this, (v) -> this.layout = clampLayout(layout));
    }

    public int getLayout()
    {
        return this.layout;
    }

    public void setLayoutLocked(boolean locked)
    {
        BaseValue.edit(this, (v) -> this.layoutLocked = locked);
    }

    public boolean isLayoutLocked()
    {
        return this.layoutLocked;
    }

    public void setMainSizeH(float mainSizeH)
    {
        BaseValue.edit(this, (v) -> this.mainSizeH = mainSizeH);
    }

    public void setMainSizeV(float mainSizeV)
    {
        BaseValue.edit(this, (v) -> this.mainSizeV = mainSizeV);
    }

    public void setEditorSizeH(float editorSizeH)
    {
        BaseValue.edit(this, (v) -> this.editorSizeH = editorSizeH);
    }

    public void setEditorSizeV(float editorSizeV)
    {
        BaseValue.edit(this, (v) -> this.editorSizeV = editorSizeV);
    }

    public void setStateEditorSizeH(float editorSizeH)
    {
        BaseValue.edit(this, (v) -> this.stateEditorSizeH = editorSizeH);
    }

    public void setStateEditorSizeV(float editorSizeV)
    {
        BaseValue.edit(this, (v) -> this.stateEditorSizeV = editorSizeV);
    }

    public boolean isHorizontal()
    {
        return this.layout == LAYOUT_HORIZONTAL_BOTTOM || this.layout == LAYOUT_HORIZONTAL_TOP;
    }

    public boolean isMainOnTop()
    {
        return this.layout == LAYOUT_HORIZONTAL_TOP;
    }

    public boolean isMainOnLeft()
    {
        return this.layout == LAYOUT_VERTICAL_LEFT;
    }

    public float getMainSizeH()
    {
        return this.mainSizeH;
    }

    public float getMainSizeV()
    {
        return this.mainSizeV;
    }

    public float getEditorSizeH()
    {
        return this.editorSizeH;
    }

    public float getEditorSizeV()
    {
        return this.editorSizeV;
    }

    public float getStateEditorSizeH()
    {
        return MathUtils.clamp(this.stateEditorSizeH, 0.1F, 0.9F);
    }

    public float getStateEditorSizeV()
    {
        return MathUtils.clamp(this.stateEditorSizeV, 0.1F, 0.9F);
    }

    @Override
    public BaseType toData()
    {
        MapType data = new MapType();

        data.putInt("layout", this.layout);
        data.putBool("horizontal", this.isHorizontal());
        data.putBool("layout_locked", this.layoutLocked);
        data.putFloat("main_size_h", this.mainSizeH);
        data.putFloat("main_size_v", this.mainSizeV);
        data.putFloat("editor_size_h", this.editorSizeH);
        data.putFloat("editor_size_v", this.editorSizeV);
        data.putFloat("state_editor_size_h", this.stateEditorSizeH);
        data.putFloat("state_editor_size_v", this.stateEditorSizeV);

        return data;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isMap())
        {
            MapType map = data.asMap();

            if (map.has("layout"))
            {
                this.layout = clampLayout(map.getInt("layout"));
            }
            else
            {
                this.layout = map.getBool("horizontal") ? LAYOUT_HORIZONTAL_BOTTOM : LAYOUT_VERTICAL_LEFT;
            }
            this.layoutLocked = map.getBool("layout_locked", false);
            this.mainSizeH = map.getFloat("main_size_h", 0.66F);
            this.mainSizeV = map.getFloat("main_size_v", 0.66F);
            this.editorSizeH = map.getFloat("editor_size_h", 0.5F);
            this.editorSizeV = map.getFloat("editor_size_v", 0.5F);
            this.stateEditorSizeH = map.getFloat("state_editor_size_h", 0.7F);
            this.stateEditorSizeV = map.getFloat("state_editor_size_v", 0.25F);
        }
    }

    private int clampLayout(int layout)
    {
        if (layout < LAYOUT_HORIZONTAL_BOTTOM || layout > LAYOUT_VERTICAL_RIGHT)
        {
            return LAYOUT_HORIZONTAL_BOTTOM;
        }

        return layout;
    }
}