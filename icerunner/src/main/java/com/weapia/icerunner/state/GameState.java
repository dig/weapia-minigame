package com.weapia.icerunner.state;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.weapia.icerunner.config.WorldConfiguration;
import com.weapia.icerunner.team.MinigameTeam;
import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.DataWatcherRegistry;
import net.sunken.core.config.LocationConfiguration;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.item.ItemRegistry;
import net.sunken.core.team.TeamManager;
import net.sunken.core.team.impl.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class GameState extends EventGameState {

    @Inject
    private TeamManager teamManager;

    private WorldConfiguration worldConfiguration;
    private Set<Projectile> activeProjectiles = Sets.newHashSet();
    private Queue<Location> nextTickIcePlace = Queues.newLinkedBlockingQueue();

    @Override
    public void start(BaseGameState previous) {
        worldConfiguration = loadConfig(String.format("config/world/%s.conf", pluginInform.getServer().getWorld().toString()), WorldConfiguration.class);

        Queue<Location> spawns = new LinkedList<>();
        for (LocationConfiguration locationConfiguration : worldConfiguration.getSpawns())
            spawns.add(locationConfiguration.toLocation());

        teamManager.getTeamsList().forEach(team -> {
            MinigameTeam minigameTeam = (MinigameTeam) team;
            Location next = spawns.poll();
            minigameTeam.setSpawn(next);

            for (UUID uuid : minigameTeam.getMembers()) {
                Player target = Bukkit.getPlayer(uuid);
                if (target != null) {
                    target.teleport(next);
                }
            }
        });
    }

    @Override
    public void stop(BaseGameState next) {
    }

    @Override
    public void tick(int tickCount) {
        while (!nextTickIcePlace.isEmpty()) {
            Location location = nextTickIcePlace.poll();
            World world = location.getWorld();
            Block block = world.getBlockAt(location);

            if (block.getType() == Material.AIR) {
                block.setType(Material.ICE);
            }
        }

        if (activeProjectiles.size() > 0) {
            activeProjectiles.forEach(projectile -> nextTickIcePlace.add(projectile.getLocation().subtract(0, 2, 0)));
        }
    }

    @Override
    public void onJoin(Player player) {
    }

    @Override
    public void onQuit(Player player) {
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        handleDeath(event.getEntity());
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        handleDeath(player);

        Optional<Team> teamOptional = teamManager.getByMemberUUID(player.getUniqueId());
        if (teamOptional.isPresent()) {
            MinigameTeam minigameTeam = (MinigameTeam) teamOptional.get();
            event.setRespawnLocation(minigameTeam.getSpawn());
        }
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        return block.getType() == Material.ICE;
    }

    @Override
    public boolean canPlace(Player player, Block block) {
        return false;
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
        if (instigator.getHealth() <= finalDamage) {
            handleDeath(instigator);
            return false;
        }

        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() <= 0) {
            handleDeath(player);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.SNOWBALL) {
            activeProjectiles.add(event.getEntity());
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntityType() == EntityType.SNOWBALL) {
            activeProjectiles.remove(event.getEntity());
        } else if (event.getEntityType() == EntityType.ARROW) {
            if (event.getHitEntity() instanceof Player) {
                Player target = (Player) event.getHitEntity();
                CraftPlayer craftPlayer = (CraftPlayer) target;
                craftPlayer.getHandle().getDataWatcher().set(new DataWatcherObject<>(10, DataWatcherRegistry.b), 0);
            }
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupArrowEvent event) {
        event.getArrow().remove();
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(15);
        event.setCancelled(true);
    }

    private void handleDeath(Player player) {
        Optional<Team> teamOptional = teamManager.getByMemberUUID(player.getUniqueId());
        if (teamOptional.isPresent()) {
            MinigameTeam minigameTeam = (MinigameTeam) teamOptional.get();

            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());

            player.setHealth(20.0);
            player.teleport(minigameTeam.getSpawn());
        }
    }

}
