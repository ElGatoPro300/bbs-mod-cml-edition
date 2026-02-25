package elgatopro300.bbs_cml.obj;

public class MeshOBJ extends Mesh
{
    public OBJMaterial material;

    public MeshOBJ(int faces)
    {
        super(faces);
    }

    public MeshOBJ(float[] posData, float[] texData, float[] normData)
    {
        super(posData, texData, normData);
    }
}