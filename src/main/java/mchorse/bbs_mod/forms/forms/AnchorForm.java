package mchorse.bbs_mod.forms.forms;

import mchorse.bbs_mod.settings.values.core.ValueString;

public class AnchorForm extends Form
{
    public final ValueString ikChain = new ValueString("ik_chain", "");

    public AnchorForm()
    {
        super();

        this.add(this.ikChain);
    }
}