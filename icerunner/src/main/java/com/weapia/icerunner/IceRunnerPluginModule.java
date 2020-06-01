package com.weapia.icerunner;

import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.core.CoreModule;
import net.sunken.core.engine.EngineModule;
import net.sunken.core.inject.PluginModule;
import net.sunken.core.team.TeamModule;
import org.bukkit.plugin.java.JavaPlugin;

public class IceRunnerPluginModule extends PluginModule {

    public IceRunnerPluginModule(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void configurePlugin() {
        install(new CoreModule());
        install(new EngineModule());
        install(new TeamModule());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(GlobalListener.class);
    }

}
