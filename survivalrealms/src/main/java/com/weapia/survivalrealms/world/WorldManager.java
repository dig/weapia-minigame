package com.weapia.survivalrealms.world;

import com.weapia.survivalrealms.config.*;
import lombok.extern.java.*;
import net.sunken.common.config.*;
import net.sunken.common.inject.*;
import net.sunken.core.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.*;
import org.bukkit.plugin.java.*;
import org.bukkit.scheduler.*;

import javax.inject.*;
import java.util.*;
import java.util.concurrent.*;

@Log
@Singleton
public class WorldManager implements Facet, Enableable, Listener {

    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

    private static final long UNLOAD_AFTER_TICKS_OFFLINE = Ticks.from(1, TimeUnit.MINUTES);

    private final Set<UUID> loadingWorlds = new HashSet<>();
    private final Map<UUID, World> loadedWorlds = new HashMap<>();
    private final Map<UUID, BukkitTask> scheduledUnloadWorlds = new HashMap<>();

    @Inject
    private JavaPlugin plugin;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!worldConfiguration.isAdventure()) {
            if (isUnloadWorldScheduled(player)) {
                unscheduleUnloadWorld(player);

                World loadedWorld = loadedWorlds.get(player.getUniqueId());
                if (loadedWorld != null) {
                    player.teleport(loadedWorld.getSpawnLocation());
                }
            } else {
                loadWorld(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!worldConfiguration.isAdventure()) {
            scheduleUnloadWorld(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
        World newlyLoadedWorld = event.getWorld();
        log.info(String.format("World load with name %s", newlyLoadedWorld.getName()));

        UUID playerUUID;
        try {
            playerUUID = UUID.fromString(newlyLoadedWorld.getName());
        } catch (IllegalArgumentException e) {
            return;
        }
        if (loadingWorlds.contains(playerUUID)) {
            loadingWorlds.remove(playerUUID);

            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.teleport(newlyLoadedWorld.getSpawnLocation());
            }
        }
    }

    private void loadWorld(Player player) {
        log.info(String.format("Loading world for %s", player.getName()));

        loadingWorlds.add(player.getUniqueId());
        World world = new WorldCreator(player.getUniqueId().toString())
                .createWorld();
        loadedWorlds.put(player.getUniqueId(), world);
    }

    private boolean isUnloadWorldScheduled(Player player) {
        return scheduledUnloadWorlds.containsKey(player.getUniqueId());
    }

    private void scheduleUnloadWorld(Player player) {
        BukkitTask unloadWorldTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                unloadWorld(player.getUniqueId());
            }
        }, UNLOAD_AFTER_TICKS_OFFLINE);
        scheduledUnloadWorlds.put(player.getUniqueId(), unloadWorldTask);
    }

    private void unscheduleUnloadWorld(Player player) {
        BukkitTask unloadWorldTask = scheduledUnloadWorlds.remove(player.getUniqueId());
        if (unloadWorldTask != null && !unloadWorldTask.isCancelled()) {
            unloadWorldTask.cancel();
        }
    }

    private void unloadWorld(UUID playerUUID) {
        log.info(String.format("Unloading world for %s", playerUUID));

        World worldToUnload = loadedWorlds.remove(playerUUID);
        if (worldToUnload != null) {
            // teleport all players to local spawn
            worldToUnload.getPlayers().forEach(player -> player.teleport(worldConfiguration.getSpawn().toLocation()));
            Bukkit.unloadWorld(worldToUnload, false);
        }
    }

    private void unloadAllWorlds() {
        for (UUID playerUUID : loadedWorlds.keySet()) {
            unloadWorld(playerUUID);
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
        unloadAllWorlds();
    }
}
