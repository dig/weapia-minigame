package com.weapia.icerunner.item;

import net.sunken.core.item.impl.AnItem;
import net.sunken.core.item.impl.AnItemListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class ItemBow extends AnItemListener {

    @Override
    public void onShootBow(AnItem anItem, EntityShootBowEvent event) {
        Player player = (Player) event.getEntity();
        player.getInventory().setItem(17, new ItemStack(Material.ARROW));
    }

}
