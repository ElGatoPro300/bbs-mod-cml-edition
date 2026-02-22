package elgatopro300.bbs_cml.events.register;

import elgatopro300.bbs_cml.importers.Importers;
import elgatopro300.bbs_cml.importers.types.IImporter;

public class RegisterImportersEvent
{
    public void register(IImporter importer)
    {
        Importers.register(importer);
    }
}
