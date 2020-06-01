package com.minevasion.naturaldisaster.disaster;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.config.AnvilStrikeConfiguration;
import net.sunken.common.config.InjectConfig;
import net.sunken.core.util.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.Random;

public class AnvilStrikeDisaster extends BaseDisaster {

    @Inject @InjectConfig
    private AnvilStrikeConfiguration configuration;

    private Random random = new Random();

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
        if (tickCount % 5 == 0) {
            int radius = worldConfiguration.getDisasterRadius();
            int min = worldConfiguration.getSeaLevel() + 30;
            int max = min + 10;

            Location locationToFall = LocationUtil.findRandomSafeLocation(worldConfiguration.getSpawn().toLocation(), -radius, radius);
            locationToFall.setY(random.nextInt(max - min) + min);
            locationToFall.getBlock().setType(Material.ANVIL);
        }
    }

    @Override
    public String getName() { return configuration.getName(); }

    @Override
    public ChatColor getColour() { return configuration.getColour(); }

    @EventHandler
    public void onBlockForm(EntityChangeBlockEvent event) {
        if (event.getEntity().getType() == EntityType.FALLING_BLOCK && event.getTo() == Material.ANVIL) {
            Location location = event.getBlock().getLocation().add(0, -1, 0);
            location.getBlock().setType(Material.AIR);

            location.getWorld().playSound(location, Sound.BLOCK_ANVIL_LAND, 0.5f, 0.5f);

            event.setCancelled(true);
        }
    }

}
