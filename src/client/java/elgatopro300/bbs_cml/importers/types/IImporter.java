package elgatopro300.bbs_cml.importers.types;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.importers.ImporterContext;
import elgatopro300.bbs_cml.l10n.keys.IKey;

import java.io.File;

public interface IImporter
{
    public IKey getName();

    public default File getDefaultFolder()
    {
        return BBSMod.getAssetsFolder();
    }

    public boolean canImport(ImporterContext context);

    public void importFiles(ImporterContext context);
}