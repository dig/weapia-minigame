package com.weapia.survivalrealms;

import com.weapia.survivalrealms.command.SpawnCommand;
import com.weapia.survivalrealms.config.WorldConfiguration;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.core.CoreModule;
import net.sunken.core.engine.EngineModule;
import net.sunken.core.inject.PluginModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SurvivalRealmsPluginModule extends PluginModule {

    public SurvivalRealmsPluginModule(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void configurePlugin() {
        install(new ConfigModule(new File("config/world.conf"), WorldConfiguration.class));

        install(new CoreModule());
        install(new EngineModule());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(SpawnCommand.class);
    }

}
