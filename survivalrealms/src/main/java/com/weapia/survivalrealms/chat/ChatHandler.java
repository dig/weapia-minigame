package com.weapia.survivalrealms.chat;

import com.google.inject.Inject;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.module.PlayerManager;
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
        playerManager.get(player.getUniqueId()).ifPresent(abstractPlayer -> packetUtil.send(new PlayerChatPacket(abstractPlayer.toPlayerDetail(), event.getFormat(), event.getMessage())));
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
