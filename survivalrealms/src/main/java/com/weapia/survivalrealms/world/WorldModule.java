package com.weapia.survivalrealms.world;

import com.google.inject.*;
import net.sunken.common.command.impl.*;
import net.sunken.common.inject.*;
import net.sunken.master.command.example.*;

public class WorldModule extends AbstractModule {

    @Override
    protected void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(WorldManager.class);
    }

}
