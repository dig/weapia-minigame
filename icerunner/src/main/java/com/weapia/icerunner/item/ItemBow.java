package com.weapia.icerunner.item;

import com.google.inject.Inject;
import net.sunken.core.item.ItemRegistry;
import net.sunken.core.item.impl.AnItem;
import net.sunken.core.item.impl.AnItemListener;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ItemBow extends AnItemListener {

    @Inject
    private ItemRegistry itemRegistry;

    @Override
    public void onShootBow(AnItem anItem, EntityShootBowEvent event) {
        Player player = (Player) event.getEntity();
        ItemStack arrow = itemRegistry.getItem("arrow").get().toItemStack();
        player.getInventory().setItem(17, arrow);
    }

    @Override
    public void onDurabilityDamage(AnItem anItem, PlayerItemDamageEvent event) {
        event.setDamage(0);
        event.setCancelled(true);
    }

}
