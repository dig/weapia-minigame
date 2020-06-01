package com.minevasion.spacegames;

import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.core.CoreModule;
import net.sunken.core.engine.EngineModule;
import net.sunken.core.inject.PluginModule;
import org.bukkit.plugin.java.JavaPlugin;

public class SpaceGamesPluginModule extends PluginModule {

    public SpaceGamesPluginModule(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void configurePlugin() {
        install(new CoreModule());
        install(new EngineModule());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(GlobalListener.class);
    }

}
