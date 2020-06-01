package com.minevasion.naturaldisaster.disaster;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.config.PowerOutConfiguration;
import net.sunken.common.config.InjectConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PowerOutDisaster extends BaseDisaster {

    @Inject @InjectConfig
    private PowerOutConfiguration configuration;

    @Override
    public void start() {
        expireTimeMillis = System.currentTimeMillis() + configuration.getExpire();

        Bukkit.getWorlds().forEach(world -> world.setTime(15000L));
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getGameMode() != GameMode.SPECTATOR)
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false));
        });

        announce(configuration.getColour(), configuration.getName(), configuration.getDescription());
    }

    @Override
    public void stop() {
        Bukkit.getWorlds().forEach(world -> world.setTime(0L));
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());
        });
    }

    @Override
    public void tick(int tickCount) {
        if (tickCount % 20 == 0) {
            Bukkit.getWorlds().forEach(world -> world.setTime(15000L));
        }
    }

    @Override
    public String getName() { return configuration.getName(); }

    @Override
    public ChatColor getColour() { return configuration.getColour(); }

}
