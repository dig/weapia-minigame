package com.weapia.survivalrealms.chat;

import com.google.inject.Inject;
import com.weapia.survivalrealms.Constants;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatHandler extends PacketHandler<PlayerChatPacket> implements Facet, Enableable, Listener {

    @Inject
    private PlayerManager playerManager;

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PacketUtil packetUtil;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        if (event.getMessage().length() <= 256) {
            playerManager.get(player.getUniqueId()).ifPresent(abstractPlayer -> packetUtil.send(new PlayerChatPacket(abstractPlayer.toPlayerDetail(), event.getFormat(), event.getMessage())));
        } else {
            player.sendMessage(Constants.CHAT_OVER_MAX_LENGTH);
        }
    }

    @Override
    public void onReceive(PlayerChatPacket packet) {
        Bukkit.broadcastMessage(String.format(packet.getFormat(), packet.getPlayer().getDisplayName(), packet.getMessage()));
    }

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(PlayerChatPacket.class, this);
    }

    @Override
    public void disable() {
    }
}
