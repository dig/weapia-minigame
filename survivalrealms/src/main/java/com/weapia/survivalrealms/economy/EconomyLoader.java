package com.weapia.survivalrealms.economy;

import com.google.inject.Inject;
import net.milkbowl.vault.economy.Economy;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyLoader implements Facet, Enableable {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private SimpleEconomy economy;

    @Override
    public void enable() {
        plugin.getServer().getServicesManager().register(Economy.class, economy, plugin, ServicePriority.High);
    }
}
