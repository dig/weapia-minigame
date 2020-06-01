package com.minevasion.naturaldisaster.disaster;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.config.ThunderstormConfiguration;
import net.sunken.common.config.InjectConfig;
import net.sunken.core.util.LocationUtil;
import org.bukkit.*;
import org.bukkit.block.Block;

public class ThunderstormDisaster extends BaseDisaster {

    @Inject @InjectConfig
    private ThunderstormConfiguration configuration;

    @Override
    public void start() {
        expireTimeMillis = System.currentTimeMillis() + configuration.getExpire();
        announce(configuration.getColour(), configuration.getName(), configuration.getDescription());
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(int tickCount) {
        if (tickCount % configuration.getStrikeEveryTick() == 0) {
            World world = Bukkit.getWorld("map");

            int radius = worldConfiguration.getDisasterRadius();
            Location locationToStrike = LocationUtil.findRandomSafeLocation(worldConfiguration.getSpawn().toLocation(), -radius, radius);
            locationToStrike.setY(locationToStrike.getWorld().getHighestBlockYAt(locationToStrike) - 1);

            world.strikeLightning(locationToStrike);

            //--- Remove block after strike
            Block blockToRemove = locationToStrike.getBlock();
            if (blockToRemove != null && blockToRemove.getType() != Material.WATER)
                blockToRemove.setType(Material.AIR);
        }
    }

    @Override
    public String getName() { return configuration.getName(); }

    @Override
    public ChatColor getColour() { return configuration.getColour(); }

}
