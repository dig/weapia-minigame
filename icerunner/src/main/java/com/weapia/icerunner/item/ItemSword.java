package com.weapia.icerunner.item;

import net.sunken.core.item.impl.AnItem;
import net.sunken.core.item.impl.AnItemListener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemSword extends AnItemListener {

    @Override
    public void onDurabilityDamage(AnItem anItem, PlayerItemDamageEvent event) {
        event.setDamage(0);
        event.setCancelled(true);
    }

}
