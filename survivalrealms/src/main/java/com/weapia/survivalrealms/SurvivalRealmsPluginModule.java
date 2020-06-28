package com.weapia.survivalrealms;

import com.weapia.survivalrealms.chat.ChatHandler;
import com.weapia.survivalrealms.command.AdventureCommand;
import com.weapia.survivalrealms.command.RealmCommand;
import com.weapia.survivalrealms.command.SpawnCommand;
import com.weapia.survivalrealms.config.WorldConfiguration;
import com.weapia.survivalrealms.economy.EconomyModule;
import com.weapia.survivalrealms.world.*;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.FacetBinder;
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
        install(new WorldModule());
        install(new EconomyModule());

        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(ChatHandler.class);
        facetBinder.addBinding(SpawnCommand.class);
        facetBinder.addBinding(AdventureCommand.class);
        facetBinder.addBinding(RealmCommand.class);
    }
}
