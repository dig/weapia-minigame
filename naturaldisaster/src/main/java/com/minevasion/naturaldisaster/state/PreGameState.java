package com.minevasion.naturaldisaster.state;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.config.WorldConfiguration;
import com.minevasion.naturaldisaster.player.MinigamePlayer;
import net.sunken.common.server.Game;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.BasePreGameState;
import net.sunken.core.scoreboard.ScoreboardWrapper;
import net.sunken.core.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PreGameState extends BasePreGameState {

    private WorldConfiguration worldConfiguration;

    @Inject
    public PreGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void start(BaseGameState previous) {
        super.start(previous);

        worldConfiguration = loadConfig(String.format("config/world/%s.conf", pluginInform.getServer().getWorld().toString()), WorldConfiguration.class);
        if (gameState instanceof GameState) ((GameState) gameState).setWorldConfiguration(worldConfiguration);

        //--- Teleport to spawn
        Random random = new Random();
        int max = worldConfiguration.getSpawnRadius();
        int min = -worldConfiguration.getSpawnRadius();

        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(LocationUtil.findRandomLocation(worldConfiguration.getSpawn().toLocation(), min, max)));

        //--- Setup scoreboard
        long playingCount = getPlayingCount();
        playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
            MinigamePlayer minigamePlayer = (MinigamePlayer) abstractPlayer;
            ScoreboardWrapper scoreboardWrapper = minigamePlayer.getScoreboardWrapper();
            Game game = pluginInform.getServer().getGame();

            scoreboardWrapper.add("Spacer1", ChatColor.WHITE + " ", 12);

            scoreboardWrapper.add("EventsTitle", ChatColor.WHITE + " \u2996 Events           ", 11);
            scoreboardWrapper.add("EventsValue0", ChatColor.RED + " None", 10);
            scoreboardWrapper.add("EventsValue1", ChatColor.RED + " None", 9);
            scoreboardWrapper.add("EventsValue2", ChatColor.RED + " None", 8);
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
        event.setRespawnLocation(worldConfiguration.getSpawn().toLocation());
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        return false;
    }

    @Override
    public boolean canPlace(Player player, Block block) {
        return false;
    }

    @Override
    public boolean canTakeDamage(Player target, Entity instigator, EntityDamageEvent.DamageCause damageCause) {
        return !(instigator instanceof Player);
    }

    @Override
    public boolean canDealDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        long maxRadiusSquared = worldConfiguration.getMaxRadius() * worldConfiguration.getMaxRadius();
        if (event.getTo().distanceSquared(worldConfiguration.getSpawn().toLocation()) >= maxRadiusSquared) {
            event.setCancelled(true);
        }
    }

}
