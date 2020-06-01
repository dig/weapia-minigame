package com.minevasion.naturaldisaster.state;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.NaturalDisaster;
import com.minevasion.naturaldisaster.config.WorldConfiguration;
import com.minevasion.naturaldisaster.disaster.BaseDisaster;
import com.minevasion.naturaldisaster.disaster.Disaster;
import lombok.Setter;
import lombok.extern.java.Log;
import net.sunken.common.server.Game;
import net.sunken.common.util.RandomUtil;
import net.sunken.core.engine.state.PlayerSpectatorState;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardEntry;
import net.sunken.core.scoreboard.ScoreboardWrapper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log
public class GameState extends EventGameState {

    @Inject
    private JavaPlugin javaPlugin;
    @Inject
    private PostGameState postGameState;

    @Setter
    private WorldConfiguration worldConfiguration;
    private long finishTimeMillis;

    private List<BaseDisaster> disasters;

    @Override
    public void start(BaseGameState previous) {
        finishTimeMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        disasters = new ArrayList<>();
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
        event.getDrops().clear();
        setState(event.getEntity().getUniqueId(), new PlayerSpectatorState(event.getEntity()));
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
        return !(target instanceof Player);
    }

    @Override
    public void tick(int tickCount) {
        //--- Disasters
        if (tickCount % 10 == 0) {
            //--- Remove expired
            for (BaseDisaster baseDisaster : disasters) {
                if (baseDisaster.isExpired()) {
                    baseDisaster.stop();
                    HandlerList.unregisterAll(baseDisaster);
                }
            }
            disasters.removeIf(BaseDisaster::isExpired);

            //--- Add new
            while (disasters.size() < worldConfiguration.getMinimumDisasters()) {
                add(RandomUtil.randomEnum(Disaster.class));
            }
        }
        disasters.forEach(disaster -> disaster.tick(tickCount));

        //--- Water damage
        if (tickCount % 20 == 0) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.getGameMode() != GameMode.SPECTATOR && !player.isDead()) {
                    Material material = player.getLocation().getBlock().getType();

                    switch (material) {
                        case WATER:
                        case STATIONARY_WATER:
                            player.damage(2.0);
                            break;
                    }
                }
            });
        }

        //--- Scoreboard
        if (tickCount % 20 == 0) {
            long playingCount = getPlayingCount();
            long timeDiff = (finishTimeMillis > System.currentTimeMillis() ? finishTimeMillis - System.currentTimeMillis() : 0);
            String timeStr = ChatColor.LIGHT_PURPLE + " " + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeDiff), TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff)));

            playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
                CorePlayer corePlayer = (CorePlayer) abstractPlayer;
                ScoreboardWrapper scoreboardWrapper = corePlayer.getScoreboardWrapper();
                Game game = pluginInform.getServer().getGame();

                //--- Players
                ScoreboardEntry playersEntry = scoreboardWrapper.getEntry("PlayersValue");
                if (playersEntry != null) playersEntry.update(ChatColor.GREEN + " " + playingCount + "/" + game.getMaxPlayers());

                //--- Time
                ScoreboardEntry timeTitleEntry = scoreboardWrapper.getEntry("TimeTitle");
                if (timeTitleEntry != null) timeTitleEntry.update(ChatColor.WHITE + " \u2996 Time Left");

                ScoreboardEntry timeEntry = scoreboardWrapper.getEntry("TimeValue");
                if (timeEntry != null) timeEntry.update(timeStr);

                //--- Events
                for (int i = 0; i < 3; i++) {
                    ScoreboardEntry eventEntry = scoreboardWrapper.getEntry(String.format("EventsValue%d", i));
                    if (eventEntry != null) {
                        if (disasters.size() > i) {
                            BaseDisaster baseDisaster = disasters.get(i);
                            //--- Issue with scoreboard splitting prefix & suffix, suffix loses the chatcolour. Need to sort that out at some point, for now hacky fix :-)
                            eventEntry.update(baseDisaster.getColour() + " " + baseDisaster.getName() + " " + baseDisaster.getColour() + "(" + baseDisaster.getColour() + TimeUnit.MILLISECONDS.toSeconds(baseDisaster.getExpireTime()) + baseDisaster.getColour() + "s)");
                        } else {
                            eventEntry.update(ChatColor.RED + " None");
                        }
                    }
                }
            });
        }

        //--- Finish
        if (tickCount % 20 == 0) {
            long playingCount = getPlayingCount();

            if (finishTimeMillis > 0 && (finishTimeMillis <= System.currentTimeMillis() || playingCount <= 1)) {
                finishTimeMillis = 0;
                postGameState.setWorldConfiguration(worldConfiguration);
                engineManager.setState(postGameState);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        long maxRadiusSquared = worldConfiguration.getMaxRadius() * worldConfiguration.getMaxRadius();
        if (event.getTo().distanceSquared(worldConfiguration.getSpawn().toLocation()) >= maxRadiusSquared) {
            event.setCancelled(true);
        }
    }

    public void add(Disaster disaster) {
        NaturalDisaster naturalDisasterPlugin = (NaturalDisaster) javaPlugin;
        BaseDisaster disasterInstance = naturalDisasterPlugin.getInjector().getInstance(disaster.getDisasterClass());
        disasterInstance.setWorldConfiguration(worldConfiguration);

        Bukkit.getPluginManager().registerEvents(disasterInstance, javaPlugin);
        disasterInstance.start();

        disasters.add(disasterInstance);
        log.info(String.format("Added new disaster. (%s)", disaster.toString()));
    }

}
