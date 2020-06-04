package com.weapia.icerunner.state;

import com.google.inject.Inject;
import com.weapia.icerunner.team.MinigameTeam;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.team.TeamManager;
import net.sunken.core.team.impl.Team;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Optional;

public class PostGameState extends EventGameState {

    @Inject
    private TeamManager teamManager;

    @Override
    public void start(BaseGameState previous) {
    }

    @Override
    public void stop(BaseGameState next) {
    }

    @Override
    public void tick(int tickCount) {
    }

    @Override
    public void onJoin(Player player) {
    }

    @Override
    public void onQuit(Player player) {
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        event.setKeepInventory(true);
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Optional<Team> teamOptional = teamManager.getByMemberUUID(player.getUniqueId());
        if (teamOptional.isPresent()) {
            MinigameTeam minigameTeam = (MinigameTeam) teamOptional.get();
            event.setRespawnLocation(minigameTeam.getSpawn());
        }
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() <= 0) {
            Optional<Team> teamOptional = teamManager.getByMemberUUID(player.getUniqueId());
            if (teamOptional.isPresent()) {
                MinigameTeam minigameTeam = (MinigameTeam) teamOptional.get();
                player.teleport(minigameTeam.getSpawn());
            }
        }
    }

}
