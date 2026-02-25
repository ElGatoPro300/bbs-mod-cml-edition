package elgatopro300.bbs_cml.importers.types;

import elgatopro300.bbs_cml.importers.ImporterContext;
import elgatopro300.bbs_cml.importers.ImporterUtils;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.utils.FFMpegUtils;
import elgatopro300.bbs_cml.utils.StringUtils;

import java.io.File;

public class GIFImporter implements IImporter
{
    @Override
    public IKey getName()
    {
        return UIKeys.IMPORTER_GIF;
    }

    @Override
    public boolean canImport(ImporterContext context)
    {
        return ImporterUtils.checkFileExtension(context.files, ".gif");
    }

    @Override
    public void importFiles(ImporterContext context)
    {
        for (File file : context.files)
        {
            String name = StringUtils.removeExtension(file.getName()) + "_%d.png";
            File destination = context.getDestination(this);

            FFMpegUtils.execute(destination, "-y", "-i", file.getAbsolutePath(), ImporterUtils.getName(destination, name));
        }
    }
}