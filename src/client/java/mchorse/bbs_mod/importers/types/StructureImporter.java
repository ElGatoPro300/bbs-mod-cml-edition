package elgatopro300.bbs_cml.importers.types;

import com.google.common.io.Files;
import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.importers.ImporterContext;
import elgatopro300.bbs_cml.importers.ImporterUtils;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;

import java.io.File;
import java.io.IOException;

public class StructureImporter implements IImporter
{
    @Override
    public IKey getName()
    {
        return UIKeys.IMPORTER_STRUCTURE_NBT;
    }

    @Override
    public File getDefaultFolder()
    {
        return new File(BBSMod.getAssetsFolder(), "structures");
    }

    @Override
    public boolean canImport(ImporterContext context)
    {
        return ImporterUtils.checkFileExtension(context.files, ".nbt");
    }

    @Override
    public void importFiles(ImporterContext context)
    {
        File destination = context.getDestination(this);

        if (!destination.exists())
        {
            destination.mkdirs();
        }

        for (File file : context.files)
        {
            try
            {
                Files.copy(file, new File(destination, file.getName()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}