package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.forms.FormUtilsClient;
import elgatopro300.bbs_cml.forms.FormUtilsClient.IFormRendererFactory;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.ui.forms.editors.UIFormEditor;
import elgatopro300.bbs_cml.ui.forms.editors.forms.UIForm;
import elgatopro300.bbs_cml.ui.forms.editors.utils.UIPickableFormRenderer;

import java.util.function.Function;
import java.util.function.Supplier;

public class RegisterFormsRenderersEvent
{
    public <T extends Form> void registerRenderer(Class<T> clazz, IFormRendererFactory<T> factory)
    {
        FormUtilsClient.register(clazz, factory);
    }

    public void registerPanel(Class<? extends Form> clazz, Supplier<UIForm> supplier)
    {
        UIFormEditor.panels.put(clazz, supplier);
    }

    public void registerEditorRenderer(Function<UIFormEditor, UIPickableFormRenderer> factory)
    {
        UIFormEditor.rendererFactory = factory;
    }
}
