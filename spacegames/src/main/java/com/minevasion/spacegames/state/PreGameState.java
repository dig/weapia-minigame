package com.minevasion.spacegames.state;

import com.google.inject.Inject;
import com.minevasion.spacegames.config.WorldConfiguration;
import com.minevasion.spacegames.player.MinigamePlayer;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.core.Constants;
import net.sunken.core.config.LocationConfiguration;
import net.sunken.core.engine.state.PlayerSpectatorState;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.BasePreGameState;
import net.sunken.core.engine.state.impl.BaseWaitingState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardWrapper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class PreGameState extends BasePreGameState {

    private WorldConfiguration worldConfiguration;
    private Queue<Location> spawns;

    @Inject
    public PreGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void start(BaseGameState previous) {
        super.start(previous);

        worldConfiguration = loadConfig(String.format("config/world/%s.conf", pluginInform.getServer().getWorld().toString()), WorldConfiguration.class);
        if (gameState instanceof GameState) ((GameState) gameState).setWorldConfiguration(worldConfiguration);

        spawns = new LinkedList<>();
        for (LocationConfiguration locationConfiguration : worldConfiguration.getSpawns())
            spawns.add(locationConfiguration.toLocation());

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);

            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());

            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 250, false, false));

            try {
                Location next = spawns.remove();
                player.teleport(next);
            } catch (NoSuchElementException ex) {
                player.sendMessage(Constants.GAME_ERROR);
                packetUtil.send(new PlayerRequestServerPacket(player.getUniqueId(), Server.Type.LOBBY, true));
            }
        });

        spawns.clear();

        //--- Setup scoreboard
        long playingCount = getPlayingCount();
        playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
            MinigamePlayer minigamePlayer = (MinigamePlayer) abstractPlayer;
            ScoreboardWrapper scoreboardWrapper = minigamePlayer.getScoreboardWrapper();
            Game game = pluginInform.getServer().getGame();

            scoreboardWrapper.add("Spacer1", ChatColor.WHITE + " ", 10);

            scoreboardWrapper.add("KillsTitle", ChatColor.WHITE + " \u2996 Kills", 9);
            scoreboardWrapper.add("KillsValue", ChatColor.RED + " 0", 8);
            scoreboardWrapper.add("Spacer2", ChatColor.WHITE + " ", 7);

            scoreboardWrapper.add("PlayersTitle", ChatColor.WHITE + " \u2996 Players", 6);
            scoreboardWrapper.add("PlayersValue", ChatColor.GREEN + " " + playingCount + "/" + game.getMaxPlayers(), 5);
            scoreboardWrapper.add("Spacer3", ChatColor.WHITE + " ", 4);

            long timeDiff = (gameStartTimeMillis > System.currentTimeMillis() ? gameStartTimeMillis - System.currentTimeMillis() : 0);
            scoreboardWrapper.add("TimeTitle", ChatColor.WHITE + " \u2996 Starting in", 3);
            scoreboardWrapper.add("TimeValue", ChatColor.LIGHT_PURPLE + " " + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeDiff), TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff))), 2);
            scoreboardWrapper.add("Spacer4", ChatColor.WHITE + " ", 1);

            scoreboardWrapper.add("URL", ChatColor.GREEN + "minevasion.com", 0);
        });
    }

    @Override
    public void stop(BaseGameState next) {
    }

    @Override
    public void onQuit(Player player) {
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(worldConfiguration.getCenter().toLocation());
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        return player.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public boolean canPlace(Player player, Block block) {
        return player.getGameMode() == GameMode.CREATIVE;
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

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location locationTo = event.getTo();
        Location locationFrom = event.getFrom();

        if ((locationTo.getX() != locationFrom.getX())
                || (locationTo.getY() != locationFrom.getY())
                || (locationTo.getZ() != locationFrom.getZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(15);
        event.setCancelled(true);
    }

}
