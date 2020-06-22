package com.weapia.survivalrealms.economy;

import com.google.inject.AbstractModule;
import com.weapia.survivalrealms.economy.command.AddBalanceCommand;
import com.weapia.survivalrealms.economy.command.BalanceCommand;
import com.weapia.survivalrealms.economy.command.SetBalanceCommand;
import net.sunken.common.inject.PluginFacetBinder;

public class EconomyModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(EconomyLoader.class);
        pluginFacetBinder.addBinding(BalanceCommand.class);
        pluginFacetBinder.addBinding(AddBalanceCommand.class);
        pluginFacetBinder.addBinding(SetBalanceCommand.class);
    }
}
