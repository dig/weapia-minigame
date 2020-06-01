package com.minevasion.naturaldisaster;

import com.minevasion.naturaldisaster.command.ForceDisasterCommand;
import com.minevasion.naturaldisaster.disaster.DisasterModule;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.core.CoreModule;
import net.sunken.core.engine.EngineModule;
import net.sunken.core.inject.PluginModule;
import org.bukkit.plugin.java.JavaPlugin;

public class NaturalDisasterPluginModule extends PluginModule {

    public NaturalDisasterPluginModule(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void configurePlugin() {
        install(new CoreModule());
        install(new EngineModule());
        install(new DisasterModule());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(GlobalListener.class);
        pluginFacetBinder.addBinding(ForceDisasterCommand.class);
    }

}
