package com.weapia.survivalrealms.world;

import com.weapia.survivalrealms.*;
import com.weapia.survivalrealms.config.*;
import com.weapia.survivalrealms.player.WorldType;
import com.weapia.survivalrealms.player.*;
import lombok.extern.java.*;
import net.minecraft.server.v1_15_R1.*;
import net.sunken.common.config.*;
import net.sunken.common.inject.*;
import net.sunken.common.player.*;
import net.sunken.common.util.*;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.util.*;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.*;
import org.bukkit.plugin.java.*;
import org.bukkit.scheduler.*;

import javax.inject.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

@Log
@Singleton
public class WorldManager implements Facet, Enableable, Disableable, Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private WorldPersister worldPersister;
    @Inject
    private PlayerManager playerManager;
    @Inject
    private ScoreboardRegistry scoreboardRegistry;
    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

    private static final long UNLOAD_AFTER_TICKS_OFFLINE = Ticks.from(10, TimeUnit.MINUTES);

    private final Set<UUID> loadingWorlds = new HashSet<>();
    private final Map<UUID, World> loadedWorlds = new HashMap<>();
    private final Map<UUID, BukkitTask> scheduledUnloadWorlds = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!worldConfiguration.isAdventure()) {
            if (isUnloadWorldScheduled(player)) {
                unscheduleUnloadWorld(player);

                World loadedWorld = loadedWorlds.get(player.getUniqueId());
                if (loadedWorld != null) {
                    teleportToWorld(player.getUniqueId(), loadedWorld);
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
            setGenerator(loadingWorld);
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
            teleportToWorld(playerUUID, newlyLoadedWorld);
        }
    }

    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        String realmName = Constants.WORLD_REALM;
        if (worldConfiguration.isAdventure()) {
            realmName = Constants.WORLD_RESOURCE;
        } else if (world.equals(worldConfiguration.getSpawn().toLocation().getWorld())) {
            realmName = Constants.WORLD_SPAWN;
        }

        String finalRealmName = realmName;
        scoreboardRegistry.get(player.getUniqueId().toString())
                .ifPresent(scoreboard -> scoreboard.getEntry("WorldValue").update(finalRealmName));
    }

    private void loadWorld(Player player) {
        log.info(String.format("Loading world for %s", player.getName()));

        AsyncHelper.executor().execute(() -> {
            try {
                worldPersister.downloadWorld(player.getUniqueId(), plugin.getServer().getWorldContainer());
            } catch (IOException e) {
                log.log(Level.SEVERE, String.format("Unable to download world (%s)", player.getUniqueId()), e);
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                loadingWorlds.add(player.getUniqueId());
                World world = new WorldCreator(player.getUniqueId().toString()).createWorld();
                loadedWorlds.put(player.getUniqueId(), world);
            });
        });
    }

    private boolean isUnloadWorldScheduled(Player player) {
        return scheduledUnloadWorlds.containsKey(player.getUniqueId());
    }

    private void scheduleUnloadWorld(Player player) {
        BukkitTask unloadWorldTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                unloadWorld(player.getUniqueId());
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

    private void unloadWorld(UUID playerUUID) {
        log.info(String.format("Unloading world for %s", playerUUID));

        World worldToUnload = loadedWorlds.remove(playerUUID);
        if (worldToUnload != null) {
            worldToUnload.getPlayers()
                    .forEach(player -> {
                        player.sendMessage(Constants.WORLD_UNLOAD);
                        player.teleport(worldConfiguration.getSpawn().toLocation());
                    });

            if (Bukkit.unloadWorld(worldToUnload, true)) {
                AsyncHelper.executor().execute(() -> {
                    try {
                        worldPersister.persistWorld(playerUUID, worldToUnload.getWorldFolder());
                    } catch (IOException e) {
                        log.log(Level.SEVERE, String.format("Could not persist world for %s", playerUUID), e);
                    }
                });
            }
        }
    }

    private void unloadAllWorlds() {
        for (UUID playerUUID : loadedWorlds.keySet()) {
            unloadWorld(playerUUID);
        }
    }

    private void setGenerator(World world) {
        CraftWorld craftWorld = (CraftWorld) world;
        PlayerChunkMap playerChunkMap = craftWorld.getHandle().getChunkProvider().playerChunkMap;

        try {
            Field chunkGeneratorField = PlayerChunkMap.class.getDeclaredField("chunkGenerator");
            chunkGeneratorField.setAccessible(true);
            ChunkGenerator<?> chunkGenerator = (ChunkGenerator<?>) chunkGeneratorField.get(playerChunkMap);

            Field generatorAccessField = ChunkGenerator.class.getDeclaredField("a");
            generatorAccessField.setAccessible(true);
            GeneratorAccess generatorAccess = (GeneratorAccess) generatorAccessField.get(chunkGenerator);

            ChunkOverrider<?> overrider = new ChunkOverrider<>(generatorAccess, chunkGenerator, world);
            chunkGeneratorField.set(playerChunkMap, overrider);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.log(Level.SEVERE, "Could not set generator on world", e);
        }
    }

    private void teleportToWorld(UUID playerUUID, World world) {
        Optional<AbstractPlayer> abstractPlayer = playerManager.get(playerUUID);
        abstractPlayer
                .map(SurvivalPlayer.class::cast)
                .ifPresent(survivalPlayer -> {
                    Player player = survivalPlayer.toPlayer().get();
                    if (survivalPlayer.getWorldType() == WorldType.REALM) {
                        Location target = world.getSpawnLocation();
                        Location lastLocation = survivalPlayer.getLastLocation();
                        if (lastLocation != null) {
                            lastLocation.setWorld(target.getWorld());
                            target = lastLocation;
                        }
                        player.teleport(target);
                    }
                });
    }

    public boolean hasWorld(UUID uuid) {
        return loadedWorlds.containsKey(uuid) || loadingWorlds.contains(uuid);
    }

    public World getWorld(UUID uuid) {
        return loadedWorlds.get(uuid);
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
        unloadAllWorlds();
    }
}
