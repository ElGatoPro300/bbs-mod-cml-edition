package mchorse.bbs_mod.ui.forms.editors.panels.shape;

import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.shape.ShapeConnection;
import mchorse.bbs_mod.forms.forms.shape.ShapeFormGraph;
import mchorse.bbs_mod.forms.forms.shape.nodes.BumpNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.ColorNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.CommentNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.CoordinateNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.MathNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.MixColorNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.NoiseNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.FlowNoiseNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.TriggerNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.OutputNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.ShapeNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.TimeNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.ValueNode;
import mchorse.bbs_mod.forms.forms.shape.nodes.VoronoiNode;
import mchorse.bbs_mod.graphics.window.Window;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIColorOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UINumberOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UITextareaOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.utils.UI;

import mchorse.bbs_mod.ui.utils.context.ContextMenuManager;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.ui.utils.presets.UICopyPasteController;
import mchorse.bbs_mod.utils.colors.Colors;
import mchorse.bbs_mod.utils.presets.PresetManager;

import java.util.List;

public class UIShapeNodeEditor extends UIElement
{
    private ShapeFormGraph graph;
    
    private float scale = 1F;
    private float translateX = 0;
    private float translateY = 0;
    
    private int lastMouseX;
    private int lastMouseY;
    private boolean dragging;
    
    private ShapeNode draggingNode;
    private int draggingConnectionNode = -1;
    private int draggingConnectionIndex = -1;
    private boolean draggingConnectionInput = false;

    private UIElement toolbar;
    private UIIcon presets;
    private UICopyPasteController copyPaste;

    public UIShapeNodeEditor()
    {
        super();
        
        this.copyPaste = new UICopyPasteController(PresetManager.SHAPE_GRAPHS, "ShapeGraph");
        this.copyPaste.supplier(this::createData);
        this.copyPaste.consumer(this::pasteData);

        this.toolbar = UI.row(0, 0);
        this.toolbar.relative(this).x(10).y(10);
        
        this.presets = new UIIcon(Icons.SAVED, (b) -> this.copyPaste.openPresets(this.getContext(), this.presets.area.x, this.presets.area.y + this.presets.area.h));
        this.presets.tooltip(UIKeys.GENERAL_PRESETS);
        this.presets.context((menu) -> {
            menu.action(Icons.COPY, UIKeys.SCENE_REPLAYS_CONTEXT_COPY, Colors.NEGATIVE, () -> this.copyPaste.copy());
            menu.action(Icons.PASTE, UIKeys.SCENE_REPLAYS_CONTEXT_PASTE, () -> this.copyPaste.paste(this.lastMouseX, this.lastMouseY));
            menu.action(Icons.MORE, UIKeys.GENERAL_PRESETS, () -> this.copyPaste.openPresets(this.getContext(), this.presets.area.x, this.presets.area.y + this.presets.area.h));
        });
        
        this.toolbar.add(this.presets);
        this.add(this.toolbar);

        this.context((menu) -> {
            ContextMenuManager add = new ContextMenuManager();

            add.action(Icons.ADD, IKey.raw("Output"), () -> this.addNode(new OutputNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.ADD, IKey.raw("Math"), () -> this.addNode(new MathNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.ADD, IKey.raw("Value"), () -> this.addNode(new ValueNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.TIME, IKey.raw("Time"), () -> this.addNode(new TimeNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.FULLSCREEN, IKey.raw("Coordinate"), () -> this.addNode(new CoordinateNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.ADD, IKey.raw("Bump"), () -> this.addNode(new BumpNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.MATERIAL, IKey.raw("Color"), () -> this.addNode(new ColorNode(), this.lastMouseX, this.lastMouseY));
        add.action(Icons.MATERIAL, IKey.raw("Mix Color"), () -> this.addNode(new MixColorNode(), this.lastMouseX, this.lastMouseY));
        add.action(Icons.EDIT, IKey.raw("Comment"), () -> this.addNode(new CommentNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.MAXIMIZE, IKey.raw("Noise"), () -> this.addNode(new NoiseNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.BLOCK, IKey.raw("Voronoi"), () -> this.addNode(new VoronoiNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.PARTICLE, IKey.raw("Flow Noise"), () -> this.addNode(new FlowNoiseNode(), this.lastMouseX, this.lastMouseY));
            add.action(Icons.TRIGGER, IKey.raw("Trigger"), () -> this.addNode(new TriggerNode(), this.lastMouseX, this.lastMouseY));

            menu.action(Icons.ADD, IKey.raw("Add"), () -> this.getContext().replaceContextMenu(add.create()));
            
            menu.action(Icons.COPY, UIKeys.SCENE_REPLAYS_CONTEXT_COPY, Colors.NEGATIVE, () -> this.copyPaste.copy());
            menu.action(Icons.PASTE, UIKeys.SCENE_REPLAYS_CONTEXT_PASTE, () -> this.copyPaste.paste(this.lastMouseX, this.lastMouseY));
            menu.action(Icons.MORE, UIKeys.GENERAL_PRESETS, () -> this.copyPaste.openPresets(this.getContext(), this.lastMouseX, this.lastMouseY));
        });
    }
    
    private MapType createData()
    {
        if (this.graph == null) return null;
        MapType data = new MapType();
        this.graph.toData(data);
        return data;
    }
    
    private void pasteData(MapType data, int mouseX, int mouseY)
    {
        if (this.graph != null)
        {
            this.graph.fromData(data);
        }
    }

    private void addNode(ShapeNode node, int x, int y)
    {
        if (this.graph != null) {
            node.x = (-this.translateX + x - this.area.x) / this.scale;
            node.y = (-this.translateY + y - this.area.y) / this.scale;
            this.graph.addNode(node);
        }
    }

    private void removeNode(ShapeNode node)
    {
        if (this.graph != null)
        {
            this.graph.removeNode(node);
        }
    }

    public void setGraph(ShapeFormGraph graph)
    {
        this.graph = graph;
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context))
        {
            int mx = context.mouseX;
            int my = context.mouseY;
            
            if (this.graph != null)
            {
                // Check sockets first
                for (int i = this.graph.nodes.size() - 1; i >= 0; i--)
                {
                    ShapeNode node = this.graph.nodes.get(i);
                    int nx = (int) (this.area.x + this.translateX + node.x * this.scale);
                    int ny = (int) (this.area.y + this.translateY + node.y * this.scale);
                    int w = (int) (120 * this.scale);
                    
                    // Outputs
                    List<String> outputs = node.getOutputs();
                    for (int j = 0; j < outputs.size(); j++)
                    {
                        int sx = nx + w;
                        int sy = ny + (int) ((30 + j * 20) * this.scale);
                        
                        if (Math.abs(mx - sx) < 10 * this.scale && Math.abs(my - sy) < 10 * this.scale)
                        {
                            this.draggingConnectionNode = node.id;
                            this.draggingConnectionIndex = j;
                            this.draggingConnectionInput = false;
                            return true;
                        }
                    }
                    
                    // Inputs
                    List<String> inputs = node.getInputs();
                    for (int j = 0; j < inputs.size(); j++)
                    {
                        int sx = nx;
                        int sy = ny + (int) ((30 + j * 20) * this.scale);
                        
                        if (Math.abs(mx - sx) < 10 * this.scale && Math.abs(my - sy) < 10 * this.scale)
                        {
                            this.draggingConnectionNode = node.id;
                            this.draggingConnectionIndex = j;
                            this.draggingConnectionInput = true;
                            return true;
                        }
                    }
                }

                // Check nodes
                for (int i = this.graph.nodes.size() - 1; i >= 0; i--)
                {
                    ShapeNode node = this.graph.nodes.get(i);
                    int nx = (int) (this.area.x + this.translateX + node.x * this.scale);
                    int ny = (int) (this.area.y + this.translateY + node.y * this.scale);
                    
                    int max = Math.max(node.getInputs().size(), node.getOutputs().size());
                    int w = (int) (120 * this.scale);
                    int h = (int) ((35 + max * 20) * this.scale);

                    if (mx >= nx && mx <= nx + w && my >= ny && my <= ny + h)
                    {
                        if (context.mouseButton == 0)
                        {
                            this.draggingNode = node;
                            this.lastMouseX = mx;
                            this.lastMouseY = my;
                            return true;
                        }
                        else if (context.mouseButton == 1)
                        {
                            this.openNodeContextMenu(context, node);
                            return true;
                        }
                    }
                }
            }

            if (context.mouseButton == 2 || context.mouseButton == 0)
            {
                this.lastMouseX = mx;
                this.lastMouseY = my;
                this.dragging = true;
                return true;
            }
        }

        return super.subMouseClicked(context);
    }
    
    private void openNodeContextMenu(UIContext context, ShapeNode node)
    {
        ContextMenuManager menu = new ContextMenuManager();

        if (node instanceof MathNode)
        {
            MathNode math = (MathNode) node;
            ContextMenuManager op = new ContextMenuManager();

            op.action(Icons.ADD, IKey.raw("Add"), () -> math.operation = 0);
            op.action(Icons.REMOVE, IKey.raw("Sub"), () -> math.operation = 1);
            op.action(Icons.CLOSE, IKey.raw("Mul"), () -> math.operation = 2);
            op.action(Icons.MAXIMIZE, IKey.raw("Div"), () -> math.operation = 3);
            op.action(Icons.REFRESH, IKey.raw("Mod"), () -> math.operation = 4);
            op.action(Icons.DOWNLOAD, IKey.raw("Min"), () -> math.operation = 5);
            op.action(Icons.UPLOAD, IKey.raw("Max"), () -> math.operation = 6);
            op.action(Icons.MORE, IKey.raw("Pow"), () -> math.operation = 7);
            op.action(Icons.VISIBLE, IKey.raw("Step"), () -> math.operation = 10);

            menu.action(Icons.GEAR, IKey.raw("Operation"), () -> context.replaceContextMenu(op.create()));
        }
        else if (node instanceof ValueNode)
        {
            ValueNode valueNode = (ValueNode) node;
            menu.action(Icons.EDIT, IKey.raw("Edit Value"), () -> {
                UINumberOverlayPanel panel = new UINumberOverlayPanel(IKey.raw("Edit Value"), IKey.raw("Enter a new value"), (v) -> valueNode.value = v.floatValue());
                panel.value.setValue((double) valueNode.value);
                UIOverlay.addOverlay(context, panel);
            });
        }
        else if (node instanceof ColorNode)
        {
            ColorNode colorNode = (ColorNode) node;
            menu.action(Icons.MATERIAL, IKey.raw("Edit Color"), () -> {
                UIColorOverlayPanel panel = new UIColorOverlayPanel(IKey.raw("Edit Color"), (c) -> colorNode.color.set(c));
                panel.picker.editAlpha = true;
                panel.picker.setColor(colorNode.color.getARGBColor());
                UIOverlay.addOverlay(context, panel, 250, 160);
            });
        }
        else if (node instanceof CommentNode)
        {
            CommentNode commentNode = (CommentNode) node;
            menu.action(Icons.EDIT, IKey.raw("Edit Title"), () -> {
                UIPromptOverlayPanel panel = new UIPromptOverlayPanel(IKey.raw("Edit Title"), IKey.raw("Enter title"), (s) -> commentNode.title = s);
                panel.text.setText(commentNode.title);
                UIOverlay.addOverlay(context, panel);
            });
            menu.action(Icons.EDIT, IKey.raw("Edit Comment"), () -> {
                UITextareaOverlayPanel panel = new UITextareaOverlayPanel(IKey.raw("Edit Comment"), IKey.raw("Enter comment"), (s) -> commentNode.comment = s);
                panel.text.setText(commentNode.comment);
                UIOverlay.addOverlay(context, panel);
            });
            
            ContextMenuManager resize = new ContextMenuManager();
            resize.action(Icons.MAXIMIZE, IKey.raw("Small"), () -> { commentNode.width = 150; commentNode.height = 100; });
            resize.action(Icons.MAXIMIZE, IKey.raw("Medium"), () -> { commentNode.width = 300; commentNode.height = 200; });
            resize.action(Icons.MAXIMIZE, IKey.raw("Large"), () -> { commentNode.width = 500; commentNode.height = 300; });
            
            menu.action(Icons.FULLSCREEN, IKey.raw("Resize"), () -> context.replaceContextMenu(resize.create()));
        }
        else if (node instanceof NoiseNode)
        {
            NoiseNode noiseNode = (NoiseNode) node;
            menu.action(Icons.EDIT, IKey.raw("Edit Seed"), () -> {
                UIPromptOverlayPanel panel = new UIPromptOverlayPanel(IKey.raw("Edit Seed"), IKey.raw("Enter seed"), (s) -> {
                    try { noiseNode.seed = Integer.parseInt(s); } catch (Exception e) {}
                });
                panel.text.setText(String.valueOf(noiseNode.seed));
                UIOverlay.addOverlay(context, panel);
            });
        }
        else if (node instanceof FlowNoiseNode)
        {
            FlowNoiseNode flowNoiseNode = (FlowNoiseNode) node;
            menu.action(Icons.EDIT, IKey.raw("Edit Seed"), () -> {
                UIPromptOverlayPanel panel = new UIPromptOverlayPanel(IKey.raw("Edit Seed"), IKey.raw("Enter seed"), (s) -> {
                    try { flowNoiseNode.seed = Integer.parseInt(s); } catch (Exception e) {}
                });
                panel.text.setText(String.valueOf(flowNoiseNode.seed));
                UIOverlay.addOverlay(context, panel);
            });
        }
        else if (node instanceof TriggerNode)
        {
            TriggerNode triggerNode = (TriggerNode) node;
            menu.action(Icons.EDIT, IKey.raw("Edit Mode"), () -> {
                ContextMenuManager modeMenu = new ContextMenuManager();
                modeMenu.action(Icons.ARROW_RIGHT, IKey.raw("Greater"), () -> triggerNode.mode = 0);
                modeMenu.action(Icons.ARROW_LEFT, IKey.raw("Less"), () -> triggerNode.mode = 1);
                modeMenu.action(Icons.CHECKMARK, IKey.raw("Equal"), () -> triggerNode.mode = 2);
                modeMenu.action(Icons.CLOSE, IKey.raw("Not Equal"), () -> triggerNode.mode = 3);
                modeMenu.action(Icons.TIME, IKey.raw("Pulse"), () -> triggerNode.mode = 4);
                context.replaceContextMenu(modeMenu.create());
            });
        }
        else if (node instanceof VoronoiNode)
        {
            VoronoiNode voronoiNode = (VoronoiNode) node;
            menu.action(Icons.EDIT, IKey.raw("Edit Seed"), () -> {
                UIPromptOverlayPanel panel = new UIPromptOverlayPanel(IKey.raw("Edit Seed"), IKey.raw("Enter seed"), (s) -> {
                    try { voronoiNode.seed = Integer.parseInt(s); } catch (Exception e) {}
                });
                panel.text.setText(String.valueOf(voronoiNode.seed));
                UIOverlay.addOverlay(context, panel);
            });
        }
        
        menu.action(Icons.REMOVE, UIKeys.GENERAL_REMOVE, Colors.NEGATIVE, () -> this.removeNode(node));
        
        context.replaceContextMenu(menu.create());
    }
    
    @Override
    protected boolean subMouseReleased(UIContext context)
    {
        if (this.draggingConnectionNode != -1)
        {
             if (this.graph != null)
            {
                int mx = context.mouseX;
                int my = context.mouseY;

                for (ShapeNode node : this.graph.nodes)
                {
                    if (node.id == this.draggingConnectionNode) continue;

                    int nx = (int) (this.area.x + this.translateX + node.x * this.scale);
                    int ny = (int) (this.area.y + this.translateY + node.y * this.scale);
                    
                    if (!this.draggingConnectionInput)
                    {
                        List<String> inputs = node.getInputs();
                        for (int j = 0; j < inputs.size(); j++)
                        {
                            int sx = nx;
                            int sy = ny + (int) ((30 + j * 20) * this.scale);
                            
                            if (Math.abs(mx - sx) < 10 * this.scale && Math.abs(my - sy) < 10 * this.scale)
                            {
                                this.graph.connect(this.draggingConnectionNode, this.draggingConnectionIndex, node.id, j);
                                break;
                            }
                        }
                    }
                    else
                    {
                        List<String> outputs = node.getOutputs();
                        for (int j = 0; j < outputs.size(); j++)
                        {
                            int sx = nx + (int) (120 * this.scale);
                            int sy = ny + (int) ((30 + j * 20) * this.scale);
                            
                            if (Math.abs(mx - sx) < 10 * this.scale && Math.abs(my - sy) < 10 * this.scale)
                            {
                                this.graph.connect(node.id, j, this.draggingConnectionNode, this.draggingConnectionIndex);
                                break;
                            }
                        }
                    }
                }
            }
            
            this.draggingConnectionNode = -1;
            this.draggingConnectionIndex = -1;
            this.draggingConnectionInput = false;
            return true;
        }
        
        this.draggingNode = null;
        this.dragging = false;
        return super.subMouseReleased(context);
    }

    @Override
    protected boolean subMouseScrolled(UIContext context)
    {
        if (this.area.isInside(context))
        {
            float oldScale = this.scale;
            this.scale += Math.copySign(0.1F, context.mouseWheel);
            this.scale = Math.max(0.1F, Math.min(this.scale, 5F));
            
            float mx = context.mouseX - this.area.x;
            float my = context.mouseY - this.area.y;
            
            this.translateX = mx - (mx - this.translateX) * this.scale / oldScale;
            this.translateY = my - (my - this.translateY) * this.scale / oldScale;

            return true;
        }

        return super.subMouseScrolled(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.draggingNode != null)
        {
             this.draggingNode.x += (context.mouseX - this.lastMouseX) / this.scale;
             this.draggingNode.y += (context.mouseY - this.lastMouseY) / this.scale;
             this.lastMouseX = context.mouseX;
             this.lastMouseY = context.mouseY;
        }
        else if (this.dragging)
        {
            this.translateX += (context.mouseX - this.lastMouseX);
            this.translateY += (context.mouseY - this.lastMouseY);
            this.lastMouseX = context.mouseX;
            this.lastMouseY = context.mouseY;
            
            if (!Window.isMouseButtonPressed(0) && !Window.isMouseButtonPressed(2))
            {
                this.dragging = false;
            }
        }

        this.renderBackground(context);

        if (this.graph != null)
        {
             for (ShapeConnection c : this.graph.connections)
             {
                 drawConnection(context, c);
             }
             
             if (this.draggingConnectionNode != -1)
             {
                 drawDraggingConnection(context);
             }

             for (ShapeNode node : this.graph.nodes)
             {
                 drawNode(context, node);
             }
        }

        super.render(context);
    }
    
    private void renderBackground(UIContext context)
    {
        context.batcher.box(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.A75 | 0x181818);
        
        context.batcher.clip(this.area, context);
        
        int size = 40;
        int color = Colors.A25 | 0xFFFFFF;
        
        float sc = this.scale;
        float ox = this.translateX % (size * sc);
        float oy = this.translateY % (size * sc);
        
        if (ox < 0) ox += size * sc;
        if (oy < 0) oy += size * sc;
        
        for (float x = ox; x < this.area.w; x += size * sc)
        {
            context.batcher.box(this.area.x + x, this.area.y, this.area.x + x + 1, this.area.ey(), color);
        }
        
        for (float y = oy; y < this.area.h; y += size * sc)
        {
            context.batcher.box(this.area.x, this.area.y + y, this.area.ex(), this.area.y + y + 1, color);
        }
        
        context.batcher.unclip(context);
    }

    private void drawNode(UIContext context, ShapeNode node)
    {
         int x = (int) (this.area.x + this.translateX + node.x * this.scale);
         int y = (int) (this.area.y + this.translateY + node.y * this.scale);
         
         if (node instanceof CommentNode)
         {
             CommentNode comment = (CommentNode) node;
             int w = (int) (comment.width * this.scale);
             int h = (int) (comment.height * this.scale);
             
             context.batcher.box(x, y, x + w, y + h, Colors.A50 | 0x000000);
             context.batcher.outline(x, y, x + w, y + h, Colors.A50 | 0xFFFFFF);
             
             context.batcher.text(comment.title, x + 5, y + 5);
             if (!comment.comment.isEmpty())
             {
                 context.batcher.text(comment.comment, x + 5, y + 20);
             }
             return;
         }
         
         List<String> inputs = node.getInputs();
         List<String> outputs = node.getOutputs();
         int max = Math.max(inputs.size(), outputs.size());
         
         int w = (int) (120 * this.scale);
         int h = (int) ((35 + max * 20) * this.scale);
         int headerH = (int) (20 * this.scale);
         
         // Shadow
         context.batcher.dropShadow(x, y, x + w, y + h, (int) (10 * this.scale), Colors.A50 | 0x000000, 0);
         
         // Header
         context.batcher.box(x, y, x + w, y + headerH, Colors.A100 | 0x444444);
         context.batcher.outline(x, y, x + w, y + headerH, 0xFF222222);
         
         // Body
         context.batcher.box(x, y + headerH, x + w, y + h, Colors.A75 | 0x222222);
         context.batcher.outline(x, y + headerH, x + w, y + h, 0xFF000000);
         
         // Title
         String title = node.getType();
         
         if (node instanceof MathNode)
         {
             String[] ops = {"+", "-", "*", "/"};
             int op = ((MathNode) node).operation;
             if (op >= 0 && op < ops.length) title += " (" + ops[op] + ")";
         }
         else if (node instanceof ValueNode)
        {
            title += ": " + ((ValueNode) node).value;
        }
        else if (node instanceof ColorNode)
        {
            title += ": " + Integer.toHexString(((ColorNode) node).color.getARGBColor()).toUpperCase();
        }
        else if (node instanceof NoiseNode)
        {
            title += " (" + ((NoiseNode) node).seed + ")";
        }
        else if (node instanceof FlowNoiseNode)
        {
            title += " (" + ((FlowNoiseNode) node).seed + ")";
        }
        else if (node instanceof VoronoiNode)
        {
            title += " (" + ((VoronoiNode) node).seed + ")";
        }
        else if (node instanceof TriggerNode)
        {
            int mode = ((TriggerNode) node).mode;
            String label = "";

            if (mode == 0) label = "Greater";
            else if (mode == 1) label = "Less";
            else if (mode == 2) label = "Equal";
            else if (mode == 3) label = "Not Equal";
            else if (mode == 4) label = "Pulse";

            title += " (" + label + ")";
        }
        
        context.batcher.text(title, x + 5, y + 6);
        
        if (node instanceof ColorNode)
        {
             int c = ((ColorNode) node).color.getARGBColor();
             context.batcher.box(x + 5, y + 25, x + w - 5, y + 45, c);
             context.batcher.outline(x + 5, y + 25, x + w - 5, y + 45, 0xFF000000);
        }
        
        float socketSize = 10 * this.scale;
         
         for (int i = 0; i < inputs.size(); i++)
         {
             int sy = y + (int) ((30 + i * 20) * this.scale);
             
             context.batcher.iconArea(Icons.CIRCLE, Colors.WHITE, x - socketSize / 2, sy - socketSize / 2, socketSize, socketSize);
             context.batcher.text(inputs.get(i), x + (int)(8 * this.scale), sy - 4);
         }
         
         for (int i = 0; i < outputs.size(); i++)
         {
             int sy = y + (int) ((30 + i * 20) * this.scale);
             
             context.batcher.iconArea(Icons.CIRCLE, Colors.WHITE, x + w - socketSize / 2, sy - socketSize / 2, socketSize, socketSize);
             String label = outputs.get(i);
             context.batcher.text(label, x + w - (int)(8 * this.scale) - context.batcher.getFont().getWidth(label), sy - 4);
         }
    }
    
    private void drawConnection(UIContext context, ShapeConnection c)
    {
        ShapeNode out = findNode(c.outputNodeId);
        ShapeNode in = findNode(c.inputNodeId);
        
        if (out != null && in != null)
        {
             int x1 = (int) (this.area.x + this.translateX + out.x * this.scale + 120 * this.scale);
             int y1 = (int) (this.area.y + this.translateY + out.y * this.scale + (30 + c.outputIndex * 20) * this.scale);
             
             int x2 = (int) (this.area.x + this.translateX + in.x * this.scale);
             int y2 = (int) (this.area.y + this.translateY + in.y * this.scale + (30 + c.inputIndex * 20) * this.scale);
             
             drawBezier(context, x1, y1, x2, y2, Colors.WHITE, 2F * this.scale);
        }
    }
    
    private void drawDraggingConnection(UIContext context)
    {
        ShapeNode node = findNode(this.draggingConnectionNode);
        if (node != null)
        {
             if (!this.draggingConnectionInput)
            {
                int x1 = (int) (this.area.x + this.translateX + node.x * this.scale + 120 * this.scale);
                int y1 = (int) (this.area.y + this.translateY + node.y * this.scale + (30 + this.draggingConnectionIndex * 20) * this.scale);
                
                drawBezier(context, x1, y1, context.mouseX, context.mouseY, Colors.WHITE, 2F * this.scale);
            }
            else
            {
                int x1 = (int) (this.area.x + this.translateX + node.x * this.scale);
                int y1 = (int) (this.area.y + this.translateY + node.y * this.scale + (30 + this.draggingConnectionIndex * 20) * this.scale);
                
                drawBezier(context, context.mouseX, context.mouseY, x1, y1, Colors.WHITE, 2F * this.scale);
            }
        }
    }

    private void drawBezier(UIContext context, int x1, int y1, int x2, int y2, int color, float thickness)
    {
        int segments = 24;
        float dist = Math.abs(x2 - x1) / 2F;
        
        float px = x1;
        float py = y1;
        
        for (int i = 1; i <= segments; i++)
        {
            float t = i / (float) segments;
            float t1 = 1 - t;
            
            // Cubic Bezier: (1-t)^3*P0 + 3*(1-t)^2*t*P1 + 3*(1-t)*t^2*P2 + t^3*P3
            // P0=(x1,y1), P1=(x1+dist,y1), P2=(x2-dist,y2), P3=(x2,y2)
            
            float c0 = t1 * t1 * t1;
            float c1 = 3 * t1 * t1 * t;
            float c2 = 3 * t1 * t * t;
            float c3 = t * t * t;
            
            float x = c0 * x1 + c1 * (x1 + dist) + c2 * (x2 - dist) + c3 * x2;
            float y = c0 * y1 + c1 * y1 + c2 * y2 + c3 * y2;
            
            context.batcher.line(px, py, x, y, thickness, color);
            
            px = x;
            py = y;
        }
    }
    
    private ShapeNode findNode(int id)
    {
        for (ShapeNode node : this.graph.nodes)
        {
            if (node.id == id) return node;
        }
        return null;
    }
}