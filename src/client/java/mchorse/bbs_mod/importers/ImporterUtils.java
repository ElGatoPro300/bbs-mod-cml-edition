package elgatopro300.bbs_cml.importers;

import elgatopro300.bbs_cml.utils.IOUtils;

import java.io.File;
import java.util.List;

public class ImporterUtils
{
    public static boolean checkFileExtension(List<File> files, String... extensions)
    {
        for (File file : files)
        {
            String name = file.getName().toLowerCase();
            boolean hasAny = false;

            for (String extension : extensions)
            {
                if (name.endsWith(extension))
                {
                    hasAny = true;
                }
            }

            if (!hasAny)
            {
                return false;
            }
        }

        return true;
    }

    public static String getName(File directory, String name)
    {
        return IOUtils.findNonExistingFile(new File(directory, name)).getName();
    }
}