package com.minevasion.spacegames;

import com.google.inject.Inject;
import com.minevasion.spacegames.player.MinigamePlayer;
import com.minevasion.spacegames.player.MinigamePlayerFactory;
import net.sunken.common.inject.Facet;
import net.sunken.core.Constants;
import net.sunken.core.player.ConnectHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

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

}
