package elgatopro300.bbs_cml.obj;

import elgatopro300.bbs_cml.resources.Link;

/**
 * OBJ material
 * 
 * This class stores information about OBJ material from MTL file
 */
public class OBJMaterial
{
    public String name;

    public float r = 1;
    public float g = 1;
    public float b = 1;

    public boolean useTexture;
    public boolean linear = false;
    public Link texture;

    public OBJMaterial(String name)
    {
        this.name = name;
    }
}