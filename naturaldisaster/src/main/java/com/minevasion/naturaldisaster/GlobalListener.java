package com.minevasion.naturaldisaster;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.player.MinigamePlayerFactory;
import net.sunken.common.inject.Facet;
import net.sunken.core.Constants;
import net.sunken.core.player.ConnectHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class GlobalListener implements Facet, Listener {

    @Inject
    private ConnectHandler connectHandler;
    @Inject
    private MinigamePlayerFactory minigamePlayerFactory;

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (connectHandler.handlePreLogin(minigamePlayerFactory.createPlayer(event.getUniqueId(), event.getName()))) {
            event.allow();
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Constants.FAILED_LOAD_DATA);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof ArmorStand)
            event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(15);
        event.setCancelled(true);
    }

}
