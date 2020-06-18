package com.weapia.survivalrealms;

import net.sunken.core.CoreModule;
import net.sunken.core.engine.EngineModule;
import net.sunken.core.inject.PluginModule;
import org.bukkit.plugin.java.JavaPlugin;

public class SurvivalRealmsPluginModule extends PluginModule {

    public SurvivalRealmsPluginModule(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void configurePlugin() {
        install(new CoreModule());
        install(new EngineModule());
    }

}
