package elgatopro300.bbs_cml.importers.types;

import elgatopro300.bbs_cml.BBSMod;
import elgatopro300.bbs_cml.importers.ImporterContext;
import elgatopro300.bbs_cml.importers.ImporterUtils;
import elgatopro300.bbs_cml.l10n.keys.IKey;
import elgatopro300.bbs_cml.ui.UIKeys;
import elgatopro300.bbs_cml.utils.FFMpegUtils;

import java.io.File;

public class WAVImporter implements IImporter
{
    @Override
    public IKey getName()
    {
        return UIKeys.IMPORTER_WAV;
    }

    @Override
    public File getDefaultFolder()
    {
        return BBSMod.getAudioFolder();
    }

    @Override
    public boolean canImport(ImporterContext context)
    {
        return ImporterUtils.checkFileExtension(context.files, ".wav");
    }

    @Override
    public void importFiles(ImporterContext context)
    {
        for (File file : context.files)
        {
            String name = file.getName();
            File destination = context.getDestination(this);

            /* Force the audio to be mono */
            FFMpegUtils.execute(destination, "-y", "-i", file.getAbsolutePath(), "-ac", "1", ImporterUtils.getName(destination, name));
        }
    }
}