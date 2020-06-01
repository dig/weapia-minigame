package com.minevasion.naturaldisaster.disaster;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.config.FlashFloodingConfiguration;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@Log
public class FlashFloodingDisaster extends BaseDisaster {

    @Inject @InjectConfig
    private FlashFloodingConfiguration configuration;

    private int tickNextLevel;
    private double currentLevel;

    private double minPosX;
    private double maxPosX;

    private double minPosZ;
    private double maxPosZ;

    private boolean revert;

    @Override
    public void start() {
        expireTimeMillis = System.currentTimeMillis() + configuration.getExpire();

        int seaDiff = worldConfiguration.getMaxSeaLevel() - worldConfiguration.getSeaLevel();
        tickNextLevel = (int) (((configuration.getExpire() / 2) / seaDiff) * 0.02);
        currentLevel = worldConfiguration.getSeaLevel();

        minPosX = worldConfiguration.getSpawn().getX() - worldConfiguration.getDisasterRadius();
        maxPosX = worldConfiguration.getSpawn().getX() + worldConfiguration.getDisasterRadius();

        minPosZ = worldConfiguration.getSpawn().getZ() - worldConfiguration.getDisasterRadius();
        maxPosZ = worldConfiguration.getSpawn().getZ() + worldConfiguration.getDisasterRadius();

        revert = false;

        announce(configuration.getColour(), configuration.getName(), configuration.getDescription());
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(int tickCount) {
        if (tickCount % tickNextLevel == 0) {
            if (!revert) {
                if (currentLevel < worldConfiguration.getMaxSeaLevel()) {
                    currentLevel++;

                    for (double x = minPosX; x <= maxPosX; x++) {
                        for (double z = minPosZ; z <= maxPosZ; z++) {
                            Block block = new Location(Bukkit.getWorld("map"), x, currentLevel, z).getBlock();
                            if (block.getType() == Material.AIR) block.setType(Material.WATER);
                        }
                    }

                    log.info(String.format("Flood up. (%s)", String.valueOf(currentLevel)));
                } else {
                    revert = true;
                }
            } else {
                if (currentLevel >= worldConfiguration.getSeaLevel()) {
                    for (double x = minPosX; x <= maxPosX; x++) {
                        for (double z = minPosZ; z <= maxPosZ; z++) {
                            Block block = new Location(Bukkit.getWorld("map"), x, currentLevel, z).getBlock();
                            if (block.getType() == Material.WATER) block.setType(Material.AIR);
                        }
                    }

                    currentLevel--;
                    log.info(String.format("Flood down. (%s)", String.valueOf(currentLevel)));
                }
            }
        }
    }

    @Override
    public String getName() { return configuration.getName(); }

    @Override
    public ChatColor getColour() { return configuration.getColour(); }

}
