package com.weapia.icerunner.item;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.util.cooldown.Cooldowns;
import net.sunken.core.item.impl.AnItem;
import net.sunken.core.item.impl.AnItemListener;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

@Log
public class ItemSnowball extends AnItemListener {

    private static final String COOLDOWN_KEY = "ItemSnowballCooldown";

    @Inject
    private Cooldowns cooldowns;

    @Override
    public void onInteract(AnItem anItem, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (cooldowns.canProceed(COOLDOWN_KEY, player.getUniqueId())) {
                cooldowns.create(COOLDOWN_KEY, player.getUniqueId(), System.currentTimeMillis() + anItem.getAttributes().getInt("cooldown"));
                player.launchProjectile(Snowball.class);
            }
        }
    }

}
