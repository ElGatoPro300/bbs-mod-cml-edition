package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.forms.FormArchitect;

public class RegisterFormsEvent
{
    private final FormArchitect forms;

    public RegisterFormsEvent(FormArchitect forms)
    {
        this.forms = forms;
    }

    public FormArchitect getForms()
    {
        return this.forms;
    }
}
