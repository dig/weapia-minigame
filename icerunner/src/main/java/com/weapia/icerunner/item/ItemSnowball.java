package com.weapia.icerunner.item;

import com.google.inject.Inject;
import net.sunken.common.util.cooldown.Cooldowns;
import net.sunken.core.item.impl.AnItemListener;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class ItemSnowball extends AnItemListener {

    private static final String COOLDOWN_KEY = "ItemSnowballCooldown";

    @Inject
    private Cooldowns cooldowns;

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        if (!cooldowns.canProceed(COOLDOWN_KEY, player.getUniqueId())) return;
        cooldowns.create(COOLDOWN_KEY, player.getUniqueId(), TimeUnit.SECONDS.toMillis(5));

        player.launchProjectile(Snowball.class);
    }

}
