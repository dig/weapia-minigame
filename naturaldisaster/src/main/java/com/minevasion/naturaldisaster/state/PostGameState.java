package com.minevasion.naturaldisaster.state;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.NaturalDisaster;
import com.minevasion.naturaldisaster.config.WorldConfiguration;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.util.StringUtil;
import net.sunken.core.Constants;
import net.sunken.core.engine.state.PlayerSpectatorState;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.util.ColourUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PostGameState extends EventGameState {

    @Inject
    private JavaPlugin javaPlugin;

    @Setter
    private WorldConfiguration worldConfiguration;
    private long shutdownTimeMillis;

    @Override
    public void start(BaseGameState previous) {
        shutdownTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20);

        //--- Winner
        Set<AbstractPlayer> players = getPlaying();

        Bukkit.broadcastMessage(StringUtil.center(" NATURAL DISASTER", 154));
        Bukkit.broadcastMessage(" ");

        Bukkit.broadcastMessage(StringUtil.center(players.stream()
                .map(player -> ColourUtil.fromColourCode(player.getRank().getColourCode()) + player.getUsername())
                .collect(Collectors.joining(ChatColor.WHITE + ", ")) + ChatColor.WHITE + " survived!", 154));
        Bukkit.broadcastMessage(" ");

        TextComponent playAgain = new TextComponent(Constants.GAME_PLAY_AGAIN);
        playAgain.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/play natural_disaster"));
        Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(playAgain));

    }

    @Override
    public void stop(BaseGameState next) {
    }

    @Override
    public void onJoin(Player player) {
    }

    @Override
    public void onQuit(Player player) {
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(worldConfiguration.getSpawn().toLocation());
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Block block) {
        return true;
    }

    @Override
    public boolean canTakeEntityDamage(Player target, Entity instigator, EntityDamageEvent.DamageCause damageCause) {
        return false;
    }

    @Override
    public boolean canDealEntityDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return false;
    }

    @Override
    public boolean canTakeDamage(Player instigator, double finalDamage, double damage) {
        return false;
    }

    @Override
    public void tick(int tickCount) {
        if (shutdownTimeMillis > 0 && shutdownTimeMillis <= System.currentTimeMillis()) {
            shutdownTimeMillis = 0;
            ((NaturalDisaster) javaPlugin).handleGraceShutdown();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityByEntityDamage(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

}
