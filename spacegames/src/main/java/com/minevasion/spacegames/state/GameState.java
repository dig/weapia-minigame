package com.minevasion.spacegames.state;

import com.minevasion.spacegames.config.WorldConfiguration;
import com.minevasion.spacegames.player.MinigamePlayer;
import lombok.Setter;
import net.sunken.common.server.Game;
import net.sunken.core.engine.state.PlayerSpectatorState;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.scoreboard.ScoreboardWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;

import java.util.concurrent.TimeUnit;

public class GameState extends EventGameState {

    @Setter
    private WorldConfiguration worldConfiguration;

    @Override
    public void start(BaseGameState previous) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());

            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 0.7F, 0F);
        });

        Bukkit.broadcastMessage(com.minevasion.spacegames.Constants.GAME_START);
    }

    @Override
    public void stop(BaseGameState next) {

    }

    @Override
    public void onJoin(Player player) {
        setState(player.getUniqueId(), new PlayerSpectatorState(player));
    }

    @Override
    public void onQuit(Player player) {

    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.getLocation().getWorld().strikeLightningEffect(player.getLocation());

        event.getDrops().clear();
        setState(player.getUniqueId(), new PlayerSpectatorState(player));
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
        return true;
    }

    @Override
    public boolean canDealEntityDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return true;
    }

    @Override
    public boolean canTakeDamage(Player instigator, double finalDamage, double damage) {
        return true;
    }

    @Override
    public void tick(int tickCount) {
        if (tickCount % 20 == 0) {
            long playingCount = getPlayingCount();
            playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
                MinigamePlayer minigamePlayer = (MinigamePlayer) abstractPlayer;
                ScoreboardWrapper scoreboardWrapper = minigamePlayer.getScoreboardWrapper();
                Game game = pluginInform.getServer().getGame();

                long timeDiff = 0;
                scoreboardWrapper.getEntry("TimeTitle").update(ChatColor.WHITE + " \u2996 Time Left");
                scoreboardWrapper.getEntry("TimeValue").update(ChatColor.LIGHT_PURPLE + " " + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeDiff), TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff))));
                scoreboardWrapper.getEntry("PlayersValue").update(ChatColor.GREEN + " " + playingCount + "/" + game.getMaxPlayers());
            });
        }
    }

}
