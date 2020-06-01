package com.weapia.icerunner.state;

import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameState extends EventGameState {

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

    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {

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
        return true;
    }

    @Override
    public boolean canDealDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return true;
    }

}
