package com.weapia.survivalrealms.world;

import com.google.inject.*;
import net.sunken.common.inject.*;

public class WorldModule extends AbstractModule {

    @Override
    protected void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(WorldManager.class);
    }
}
