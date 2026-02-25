package elgatopro300.bbs_cml.utils.iris;

import elgatopro300.bbs_cml.resources.Link;
import elgatopro300.bbs_cml.utils.StringUtils;
import elgatopro300.bbs_cml.utils.resources.FilteredLink;
import elgatopro300.bbs_cml.utils.resources.MultiLink;

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
