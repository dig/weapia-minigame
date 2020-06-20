package com.weapia.survivalrealms.state;

import com.google.inject.Inject;
import com.weapia.survivalrealms.config.WorldConfiguration;
import net.sunken.common.config.InjectConfig;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameState extends EventGameState {

    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

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
        // TODO: check if player has a world loaded here, if so tp to that
        player.teleport(worldConfiguration.getSpawn().toLocation());
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
        return player.getWorld().getName().equals(player.getUniqueId().toString());
    }

    @Override
    public boolean canPlace(Player player, Block block) {
        return player.getWorld().getName().equals(player.getUniqueId().toString());
    }

    @Override
    public boolean canTakeEntityDamage(Player target, Entity instigator, EntityDamageEvent.DamageCause damageCause) {
        return !(instigator instanceof Player);
    }

    @Override
    public boolean canDealEntityDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return !(target instanceof Player);
    }

    @Override
    public boolean canTakeDamage(Player instigator, double finalDamage, double damage) {
        return instigator.getWorld().getName().equals(instigator.getUniqueId().toString());
    }
}
