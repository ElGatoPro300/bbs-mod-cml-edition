package elgatopro300.bbs_cml.entity;

import elgatopro300.bbs_cml.forms.forms.Form;

public interface IEntityFormProvider
{
    public int getEntityId();

    public Form getForm();

    public void setForm(Form form);
}