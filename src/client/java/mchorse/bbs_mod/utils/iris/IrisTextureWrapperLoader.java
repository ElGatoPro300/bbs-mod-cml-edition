package mchorse.bbs_mod.utils.iris;

import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.StringUtils;
import mchorse.bbs_mod.utils.resources.FilteredLink;
import mchorse.bbs_mod.utils.resources.MultiLink;

/**
 * Compile-safe helper for constructing prefixed texture links.
 * PBR integration is handled reflectively in IrisUtils.setup() and may be a no-op.
 */
public class IrisTextureWrapperLoader
{
    public IrisTextureWrapperLoader() {}

    public Link createPrefixedCopy(Link link, String suffix)
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
