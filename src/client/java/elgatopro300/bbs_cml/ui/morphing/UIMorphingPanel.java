package elgatopro300.bbs_cml.ui.morphing;

import elgatopro300.bbs_cml.BBSModClient;
import elgatopro300.bbs_cml.forms.forms.Form;
import elgatopro300.bbs_cml.morphing.IMorphProvider;
import elgatopro300.bbs_cml.morphing.Morph;
import elgatopro300.bbs_cml.network.ClientNetwork;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.ui.dashboard.UIDashboard;
import elgatopro300.bbs_cml.ui.dashboard.panels.UIDashboardPanel;
import elgatopro300.bbs_cml.ui.forms.UIFormPalette;
import elgatopro300.bbs_cml.ui.framework.elements.buttons.UIIcon;
import elgatopro300.bbs_cml.ui.morphing.camera.ImmersiveMorphingCameraController;
import elgatopro300.bbs_cml.ui.utils.icons.Icons;
import elgatopro300.bbs_cml.utils.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;

public class UIMorphingPanel extends UIDashboardPanel
{
    public UIFormPalette palette;
    public UIIcon demorph;
    public UIIcon fromMob;

    private ImmersiveMorphingCameraController controller;

    public UIMorphingPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.palette = new UIFormPalette(this::setForm);
        this.palette.updatable().cantExit();
        this.palette.immersive();
        this.palette.full(this);
        this.palette.editor.renderer.full(dashboard.getRoot());
        this.palette.noBackground();
        this.palette.canModify();

        this.demorph = new UIIcon(Icons.POSE, (b) ->
        {
            this.palette.setSelected(null);
            this.setForm(null);
        });
        this.demorph.tooltip(UIKeys.MORPHING_DEMORPH, Direction.TOP);
        this.fromMob = new UIIcon(Icons.MORPH, (b) ->
        {
            Form form = Morph.getMobForm(MinecraftClient.getInstance().player);

            if (form != null)
            {
                this.palette.setSelected(form);
                this.setForm(form);
            }
        });
        this.fromMob.tooltip(UIKeys.MORPHING_FROM_MOB, Direction.TOP);

        this.palette.list.bar.add(this.fromMob, this.demorph);

        this.add(this.palette);

        this.controller = new ImmersiveMorphingCameraController(() -> this.palette.editor.isEditing() ? this.palette.editor.renderer : null);
    }

    private void setForm(Form form)
    {
        ClientNetwork.sendPlayerForm(form);
    }

    @Override
    public boolean needsBackground()
    {
        return !this.palette.editor.isEditing();
    }

    @Override
    public void appear()
    {
        super.appear();

        Morph morph = ((IMorphProvider) MinecraftClient.getInstance().player).getMorph();

        this.palette.list.setupForms(BBSModClient.getFormCategories());
        this.palette.setSelected(morph.getForm());

        BBSModClient.getCameraController().add(this.controller);
        MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

    @Override
    public void disappear()
    {
        super.disappear();

        BBSModClient.getCameraController().remove(this.controller);
        MinecraftClient.getInstance().options.setPerspective(Perspective.FIRST_PERSON);
    }

    @Override
    public void close()
    {
        super.close();

        BBSModClient.getCameraController().remove(this.controller);
    }
}