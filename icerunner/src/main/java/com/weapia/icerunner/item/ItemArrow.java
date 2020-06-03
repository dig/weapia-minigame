package com.weapia.icerunner.item;

import net.sunken.core.item.impl.AnItem;
import net.sunken.core.item.impl.AnItemListener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemArrow extends AnItemListener {

    @Override
    public void onInventoryClick(AnItem anItem, InventoryClickEvent event) {
        event.setCancelled(true);
    }

}
