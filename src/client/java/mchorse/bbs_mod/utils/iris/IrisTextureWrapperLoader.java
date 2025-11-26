package mchorse.bbs_mod.utils.iris;

import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.StringUtils;
import mchorse.bbs_mod.utils.resources.FilteredLink;
import mchorse.bbs_mod.utils.resources.MultiLink;

/**
 * Stubbed IrisTextureWrapperLoader for 1.21.4 migration.
 * The Iris PBR loader API changed; this class remains as a placeholder
 * to keep compilation working without a hard dependency on Iris internals.
 */
public class IrisTextureWrapperLoader
{
    private Link createPrefixedCopy(Link link, String suffix)
    {
        if (link instanceof MultiLink multiLink)
        {
            MultiLink newMultiLink = (MultiLink) multiLink.copy();

            for (FilteredLink child : newMultiLink.children)
            {
                if (child.path != null)
                {
                    child.path = this.createPrefixedCopy(child.path, suffix);
                }
            }

            return newMultiLink;
        }

        return new Link(link.source, StringUtils.removeExtension(link.path) + suffix);
    }
}
