package mchorse.bbs_mod.mixin.client.iris;

import java.util.Collections;
import java.util.List;

public interface CustomUniformsAccessor
{
    default List bbs$uniforms()
    {
        return Collections.emptyList();
    }

    default List bbs$uniformOrder()
    {
        return Collections.emptyList();
    }
}
