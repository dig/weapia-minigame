package com.weapia.survivalrealms.world;

import com.weapia.survivalrealms.Constants;
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
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Log
@Singleton
public class WorldManager implements Facet, Enableable, Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private WorldPersister worldPersister;
    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

    private static final long UNLOAD_AFTER_TICKS_OFFLINE = Ticks.from(5, TimeUnit.SECONDS);

    private final Set<UUID> loadingWorlds = new HashSet<>();
    private final Map<UUID, World> loadedWorlds = new HashMap<>();
    private final Map<UUID, BukkitTask> scheduledUnloadWorlds = new HashMap<>();

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
                try {
                    loadWorld(player);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.severe(String.format("Unable to load world (%s)", player.getUniqueId()));
                }
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

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World loadingWorld = event.getWorld();

        UUID playerUUID;
        try {
            playerUUID = UUID.fromString(loadingWorld.getName());
        } catch (IllegalArgumentException e) {
            return;
        }
        if (loadingWorlds.contains(playerUUID)) {
            loadingWorld.setAutoSave(true);
            loadingWorld.setKeepSpawnInMemory(false);
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

    private void loadWorld(Player player) throws IOException {
        log.info(String.format("Loading world for %s", player.getName()));
        // worldPersister.downloadWorld(player.getUniqueId(), plugin.getServer().getWorldContainer());

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
                try {
                    unloadWorld(player.getUniqueId());
                } catch (IOException e) {
                    log.severe(String.format("Unable to unload world (%s)", player.getUniqueId()));
                }
            }
            unscheduleUnloadWorld(player);
        }, UNLOAD_AFTER_TICKS_OFFLINE);
        scheduledUnloadWorlds.put(player.getUniqueId(), unloadWorldTask);
    }

    private void unscheduleUnloadWorld(Player player) {
        BukkitTask unloadWorldTask = scheduledUnloadWorlds.remove(player.getUniqueId());
        if (unloadWorldTask != null && !unloadWorldTask.isCancelled()) {
            unloadWorldTask.cancel();
        }
    }

    private void unloadWorld(UUID playerUUID) throws IOException {
        log.info(String.format("Unloading world for %s", playerUUID));

        World worldToUnload = loadedWorlds.remove(playerUUID);
        if (worldToUnload != null) {
            worldToUnload.getPlayers()
                    .forEach(player -> {
                        player.sendMessage(Constants.WORLD_UNLOAD);
                        player.teleport(worldConfiguration.getSpawn().toLocation());
                    });

            if (Bukkit.unloadWorld(worldToUnload, true)) {
                worldPersister.persistWorld(playerUUID, worldToUnload.getWorldFolder());
            }
        }
    }

    private void unloadAllWorlds() throws IOException {
        for (UUID playerUUID : loadedWorlds.keySet()) {
            unloadWorld(playerUUID);
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
        try {
            unloadAllWorlds();
        } catch (IOException e) {
            log.severe("Unable to unload all worlds.");
        }
    }
}
