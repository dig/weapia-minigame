package com.minevasion.naturaldisaster.disaster;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.config.AcidRainConfiguration;
import com.minevasion.naturaldisaster.config.WorldConfiguration;
import lombok.Setter;
import net.sunken.common.config.InjectConfig;
import net.sunken.core.util.LocationUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

public class AcidRainDisaster extends BaseDisaster {

    @Inject @InjectConfig
    private AcidRainConfiguration configuration;

    @Override
    public void start() {
        expireTimeMillis = System.currentTimeMillis() + configuration.getExpire();
        Bukkit.getWorlds().forEach(world -> world.setStorm(true));
        announce(configuration.getColour(), configuration.getName(), configuration.getDescription());
    }

    @Override
    public void stop() {
        Bukkit.getWorlds().forEach(world -> world.setStorm(false));
    }

    @Override
    public void tick(int tickCount) {
        //--- Damage entities
        if (tickCount % 20 == 0) {
            Bukkit.getWorlds().forEach(world -> {
                for (LivingEntity entity : world.getLivingEntities()) {
                    if (world.getHighestBlockYAt(entity.getLocation()) <= entity.getLocation().getY()) {
                        entity.damage(configuration.getDamagePerTick());
                    }
                }
            });
        }

        //--- Remove blocks
        int radius = worldConfiguration.getDisasterRadius();
        for (int i = 0; i < 2; i++) {
            Location locationToRemove = LocationUtil.findRandomSafeLocation(worldConfiguration.getSpawn().toLocation(), -radius, radius);
            locationToRemove.setY(locationToRemove.getWorld().getHighestBlockYAt(locationToRemove) - 1);
            Block blockToRemove = locationToRemove.getBlock();

            if (blockToRemove != null && blockToRemove.getType() != Material.WATER && blockToRemove.getType() != Material.WATER)
                blockToRemove.setType(Material.AIR);
        }
    }

    @Override
    public String getName() { return configuration.getName(); }

    @Override
    public ChatColor getColour() { return configuration.getColour(); }

}
