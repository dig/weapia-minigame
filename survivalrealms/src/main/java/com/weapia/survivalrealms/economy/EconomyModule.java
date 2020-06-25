package com.weapia.survivalrealms.economy;

import com.google.inject.AbstractModule;
import com.weapia.survivalrealms.economy.command.AddBalanceCommand;
import com.weapia.survivalrealms.economy.command.BalanceCommand;
import com.weapia.survivalrealms.economy.command.SetBalanceCommand;
import net.sunken.common.inject.FacetBinder;

public class EconomyModule extends AbstractModule {

    @Override
    public void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(EconomyLoader.class);
        facetBinder.addBinding(BalanceCommand.class);
        facetBinder.addBinding(AddBalanceCommand.class);
        facetBinder.addBinding(SetBalanceCommand.class);
    }
}
