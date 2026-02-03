package mchorse.bbs_mod.forms.forms.shape;

import mchorse.bbs_mod.forms.forms.shape.nodes.*;
import mchorse.bbs_mod.utils.math.Noise;
import mchorse.bbs_mod.utils.colors.Color;
import java.util.HashMap;
import java.util.Map;

public class ShapeGraphEvaluator
{
    private final Map<Integer, ShapeNode> nodes = new HashMap<>();
    private final Map<Integer, Map<Integer, ShapeConnection>> inputs = new HashMap<>();
    private OutputNode output;
    private Noise noiseGen;

    public ShapeGraphEvaluator(ShapeFormGraph graph)
    {
        for (ShapeNode node : graph.nodes)
        {
            this.nodes.put(node.id, node);
            if (node instanceof OutputNode)
            {
                this.output = (OutputNode) node;
            }
        }

        for (ShapeConnection c : graph.connections)
        {
            this.inputs.computeIfAbsent(c.inputNodeId, k -> new HashMap<>()).put(c.inputIndex, c);
        }
        
        this.noiseGen = new Noise(0);
    }

    public double compute(double x, double y, double z, double time)
    {
        return this.evaluate(this.output, 0, x, y, z, time);
    }

    public int computeColor(double x, double y, double z, double time)
    {
        int color = -1;

        if (this.output != null && this.hasInput(this.output.id, 1))
        {
            color = (int) this.evaluate(this.output, 1, x, y, z, time);
        }

        return color;
    }

    private double evaluate(ShapeNode node, int outputIndex, double x, double y, double z, double time)
    {
        if (node instanceof ValueNode) return ((ValueNode) node).value;
        if (node instanceof TimeNode) return time;
        if (node instanceof CoordinateNode)
        {
            if (outputIndex == 0) return x;
            if (outputIndex == 1) return y;
            if (outputIndex == 2) return z;
            return 0; // UV not supported yet in evaluate() args, simplified
        }
        if (node instanceof ColorNode) return ((ColorNode) node).color.getARGBColor();
        
        if (node instanceof MixColorNode)
        {
            int c1 = (int) this.getInput(node.id, 0, x, y, z, time);
            int c2 = (int) this.getInput(node.id, 1, x, y, z, time);
            double factor = this.getInput(node.id, 2, x, y, z, time);
            
            Color color1 = new Color().set(c1);
            Color color2 = new Color().set(c2);
            
            float r = (float) (color1.r + (color2.r - color1.r) * factor);
            float g = (float) (color1.g + (color2.g - color1.g) * factor);
            float b = (float) (color1.b + (color2.b - color1.b) * factor);
            float a = (float) (color1.a + (color2.a - color1.a) * factor);
            
            return new Color(r, g, b, a).getARGBColor();
        }
        
        if (node instanceof MathNode)
        {
            double a = this.getInput(node.id, 0, x, y, z, time);
            double b = this.getInput(node.id, 1, x, y, z, time);
            int op = ((MathNode) node).operation;
            
            if (op == 0) return a + b;
            if (op == 1) return a - b;
            if (op == 2) return a * b;
            if (op == 3) return b == 0 ? 0 : a / b;
            if (op == 4) return b == 0 ? 0 : a % b;
            if (op == 5) return Math.min(a, b);
            if (op == 6) return Math.max(a, b);
            if (op == 7) return Math.pow(a, b);
            if (op == 10) return b >= a ? 1 : 0;
            return 0;
        }
        
        if (node instanceof NoiseNode)
        {
            NoiseNode n = (NoiseNode) node;
            this.noiseGen.setSeed(n.seed);
            
            double nx = this.getInput(n.id, 0, x, y, z, time);
            double ny = this.getInput(n.id, 1, x, y, z, time);
            double nz = this.getInput(n.id, 2, x, y, z, time);
            double scale = this.getInput(n.id, 3, x, y, z, time);
            
            if (!this.hasInput(n.id, 0)) nx = x;
            if (!this.hasInput(n.id, 1)) ny = y;
            if (!this.hasInput(n.id, 2)) nz = z;
            if (!this.hasInput(n.id, 3)) scale = 1;
            
            return this.noiseGen.noise(nx * scale, ny * scale, nz * scale);
        }

        if (node instanceof FlowNoiseNode)
        {
            FlowNoiseNode n = (FlowNoiseNode) node;
            this.noiseGen.setSeed(n.seed);
            
            double nx = this.hasInput(n.id, 0) ? this.getInput(n.id, 0, x, y, z, time) : x;
            double ny = this.hasInput(n.id, 1) ? this.getInput(n.id, 1, x, y, z, time) : y;
            double nz = this.hasInput(n.id, 2) ? this.getInput(n.id, 2, x, y, z, time) : z;
            double scale = this.getInput(n.id, 3, x, y, z, time);
            double speed = this.getInput(n.id, 4, x, y, z, time);
            double taper = this.getInput(n.id, 5, x, y, z, time);
            
            if (scale == 0 && !this.hasInput(n.id, 3)) scale = 1;
            if (speed == 0 && !this.hasInput(n.id, 4)) speed = 1;
            
            return this.noiseGen.noise(nx * scale, ny * scale - time * speed, nz * scale) + ny * taper;
        }

        if (node instanceof VoronoiNode)
        {
            VoronoiNode n = (VoronoiNode) node;
            this.noiseGen.setSeed(n.seed);
            
            double vx = this.hasInput(n.id, 0) ? this.getInput(n.id, 0, x, y, z, time) : x;
            double vy = this.hasInput(n.id, 1) ? this.getInput(n.id, 1, x, y, z, time) : y;
            double vz = this.hasInput(n.id, 2) ? this.getInput(n.id, 2, x, y, z, time) : z;
            double scale = this.getInput(n.id, 3, x, y, z, time);
            
            if (scale == 0 && !this.hasInput(n.id, 3)) scale = 1;
            
            return this.noiseGen.voronoi(vx * scale, vy * scale, vz * scale);
        }

        if (node instanceof TriggerNode)
        {
            TriggerNode n = (TriggerNode) node;
            
            double val = this.hasInput(n.id, 0) ? this.getInput(n.id, 0, x, y, z, time) : time;
            double target = this.getInput(n.id, 1, x, y, z, time);
            double range = this.getInput(n.id, 2, x, y, z, time);
            
            if (n.mode == 0) return val > target ? 1 : 0; // GREATER
            if (n.mode == 1) return val < target ? 1 : 0; // LESS
            if (n.mode == 2) return Math.abs(val - target) < (range == 0 ? 0.0001 : range) ? 1 : 0; // EQUAL
            if (n.mode == 3) return Math.abs(val - target) >= (range == 0 ? 0.0001 : range) ? 1 : 0; // NOT_EQUAL
            if (n.mode == 4) // PULSE
            {
                if (target <= 0) target = 1;
                return (val % target) < (range <= 0 ? target / 2 : range) ? 1 : 0;
            }
            
            return 0;
        }
        
        if (node instanceof OutputNode)
        {
            if (outputIndex == 1)
            {
                return this.getInput(node.id, 1, x, y, z, time);
            }
            return this.getInput(node.id, 0, x, y, z, time);
        }
        
        return 0;
    }
    
    private boolean hasInput(int nodeId, int index)
    {
        return this.inputs.containsKey(nodeId) && this.inputs.get(nodeId).containsKey(index);
    }

    private double getInput(int nodeId, int index, double x, double y, double z, double time)
    {
        if (this.hasInput(nodeId, index))
        {
            ShapeConnection c = this.inputs.get(nodeId).get(index);
            return this.evaluate(this.nodes.get(c.outputNodeId), c.outputIndex, x, y, z, time);
        }
        return 0;
    }
}
