package mchorse.bbs_mod.ui.forms.editors.panels;

import mchorse.bbs_mod.forms.forms.LabelForm;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.forms.editors.forms.UIForm;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.utils.Batcher2D;
import mchorse.bbs_mod.ui.framework.elements.utils.FontRenderer;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.utils.StringUtils;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.colors.Colors;

public class UILabelFormPanel extends UIFormPanel<LabelForm>
{
    public UITextbox text;
    public UIToggle billboard;
    public UIColor color;
    public UITrackpad max;
    public UITrackpad anchorX;
    public UITrackpad anchorY;
    public UIToggle anchorLines;

    public UITrackpad shadowX;
    public UITrackpad shadowY;
    public UIColor shadowColor;

    public UIColor background;
    public UITrackpad offset;

    public UILabelFormPanel(UIForm editor)
    {
        super(editor);

        this.text = new UITextbox(10000, (t) -> this.form.text.set(t));
        this.billboard = new UIToggle(UIKeys.FORMS_EDITORS_BILLBOARD_TITLE, (b) -> this.form.billboard.set(b.getValue()));
        this.color = new UIColor((c) -> this.form.color.set(Color.rgba(c))).withAlpha();
        this.max = new UITrackpad((value) -> this.form.max.set(value.intValue()));
        this.max.limit(-1, Integer.MAX_VALUE, true).increment(10);
        this.anchorX = new UITrackpad((value) -> this.form.anchorX.set(value.floatValue()));
        this.anchorX.values(0.01F);
        this.anchorY = new UITrackpad((value) -> this.form.anchorY.set(value.floatValue()));
        this.anchorY.values(0.01F);
        this.anchorLines = new UIToggle(UIKeys.FORMS_EDITORS_LABEL_ANCHOR_LINES, (value) -> this.form.anchorLines.set(value.getValue()));

        this.shadowX = new UITrackpad((value) -> this.form.shadowX.set(value.floatValue()));
        this.shadowX.limit(-100, 100).values(0.1F, 0.01F, 0.5F).increment(0.1F);
        this.shadowY = new UITrackpad((value) -> this.form.shadowY.set(value.floatValue()));
        this.shadowY.limit(-100, 100).values(0.1F, 0.01F, 0.5F).increment(0.1F);
        this.shadowColor = new UIColor((value) -> this.form.shadowColor.set(Color.rgba(value))).withAlpha();

        this.background = new UIColor((value) -> this.form.background.set(Color.rgba(value))).withAlpha();
        this.offset = new UITrackpad((value) -> this.form.offset.set(value.floatValue()));

        this.options.add(UI.label(UIKeys.FORMS_EDITORS_LABEL_LABEL), this.text, this.billboard, this.color, this.max);

        this.options.add(UI.label(UIKeys.FORMS_EDITORS_LABEL_ANCHOR).marginTop(8), UI.row(this.anchorX, this.anchorY), this.anchorLines);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_LABEL_SHADOW_OFFSET).marginTop(8), this.shadowX, this.shadowY);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_LABEL_SHADOW_COLOR).marginTop(8), this.shadowColor);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_LABEL_BACKGROUND).marginTop(8), this.background, this.offset);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_LABEL_COLOR_FORMAT_GUIDE).marginTop(8), new UIMinecraftColorGuide());
    }

    @Override
    public void startEdit(LabelForm form)
    {
        super.startEdit(form);

        this.text.setText(form.text.get());
        this.billboard.setValue(form.billboard.get());
        this.color.setColor(form.color.get().getARGBColor());
        this.max.setValue(form.max.get());
        this.anchorX.setValue(form.anchorX.get());
        this.anchorY.setValue(form.anchorY.get());
        this.anchorLines.setValue(form.anchorLines.get());

        this.shadowX.setValue(form.shadowX.get());
        this.shadowY.setValue(form.shadowY.get());
        this.shadowColor.setColor(form.shadowColor.get().getARGBColor());

        this.background.setColor(form.background.get().getARGBColor());
        this.offset.setValue(form.offset.get());
    }

    @Override
    public void finishEdit()
    {
        super.finishEdit();

        this.color.picker.removeFromParent();
        this.shadowColor.picker.removeFromParent();
        this.background.picker.removeFromParent();
    }

    private static class UIMinecraftColorGuide extends UIElement
    {
        private static final char[] COLOR_CODES = new char[]
        {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };

        private static final int[] COLOR_VALUES = new int[]
        {
            0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
            0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
            0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
            0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
        };

        private static final IKey PREVIEW_TEXT = UIKeys.FORMS_EDITORS_LABEL_COLOR_EXAMPLE;
        private static final IKey FORMAT_HEADER = UIKeys.FORMS_EDITORS_LABEL_FORMAT_HEADER;
        private static final String PREVIEW_FORMAT_TEXT = "Minecraft";
        private static final char[] FORMAT_CODES = new char[]
        {
            'k', 'l', 'm', 'n', 'o', 'r'
        };
        private static final IKey[] FORMAT_LABELS = new IKey[]
        {
            UIKeys.FORMS_EDITORS_LABEL_FORMAT_GLITCH,
            UIKeys.FORMS_EDITORS_LABEL_FORMAT_BOLD,
            UIKeys.FORMS_EDITORS_LABEL_FORMAT_STRIKETHROUGH,
            UIKeys.FORMS_EDITORS_LABEL_FORMAT_UNDERLINE,
            UIKeys.FORMS_EDITORS_LABEL_FORMAT_ITALIC,
            UIKeys.FORMS_EDITORS_LABEL_FORMAT_RESET
        };

        private final int lineHeight;
        private final int leftPadding = 2;

        public UIMinecraftColorGuide()
        {
            super();

            int baseLine = Batcher2D.getDefaultTextRenderer().getHeight();
            this.lineHeight = baseLine + 2;
            this.h(this.lineHeight * (COLOR_CODES.length + FORMAT_CODES.length + 2) + 4);
        }

        @Override
        public void render(UIContext context)
        {
            FontRenderer font = context.batcher.getFont();
            int x = this.area.x + this.leftPadding;
            int y = this.area.y + 1;
            int codeWidth = font.getWidth("[f]");
            int previewX = x + codeWidth + 6;

            for (int i = 0; i < COLOR_CODES.length; i++)
            {
                String code = "[" + COLOR_CODES[i] + "]";
                int color = Colors.A100 | COLOR_VALUES[i];

                context.batcher.text(code, x, y, Colors.LIGHTER_GRAY, true);
                String previewLabel = PREVIEW_TEXT.get();

                if (COLOR_CODES[i] == '0')
                {
                    int w = font.getWidth(previewLabel);
                    context.batcher.box(previewX - 2, y - 1, previewX + w + 2, y + font.getHeight() + 1, Colors.WHITE);
                }
                context.batcher.text(previewLabel, previewX, y, color, true);

                y += this.lineHeight;
            }

            y += 2;
            context.batcher.text(FORMAT_HEADER.get(), x, y, Colors.GRAY, false);
            y += this.lineHeight;

            for (int i = 0; i < FORMAT_CODES.length; i++)
            {
                String code = "[" + FORMAT_CODES[i] + "]";
                String preview = StringUtils.processColoredText("[" + FORMAT_CODES[i] + PREVIEW_FORMAT_TEXT);

                context.batcher.text(code, x, y, Colors.LIGHTER_GRAY, true);
                context.batcher.text(preview, previewX, y, Colors.WHITE, true);

                if (i < FORMAT_LABELS.length)
                {
                    int labelX = previewX + font.getWidth(PREVIEW_FORMAT_TEXT) + 8;
                    context.batcher.text(FORMAT_LABELS[i].get(), labelX, y, Colors.GRAY, false);
                }

                y += this.lineHeight;
            }

            super.render(context);
        }
    }
}