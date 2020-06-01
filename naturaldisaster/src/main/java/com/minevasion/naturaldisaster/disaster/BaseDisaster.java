package com.minevasion.naturaldisaster.disaster;

import com.minevasion.naturaldisaster.config.WorldConfiguration;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public abstract class BaseDisaster implements Listener {

    @Setter
    protected WorldConfiguration worldConfiguration;
    protected long expireTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);

    public abstract void start();

    public abstract void stop();

    public abstract void tick(int tickCount);

    public abstract String getName();

    public abstract ChatColor getColour();

    public boolean isExpired() {
        return expireTimeMillis <= System.currentTimeMillis();
    }

    public long getExpireTime() {
        return expireTimeMillis - System.currentTimeMillis();
    }

    protected void announce(ChatColor colour, String name, String description) {
        Bukkit.broadcastMessage(ChatColor.RED + " \u2996 " + colour + name + " " + ChatColor.WHITE + description);
    }

}
