package com.weapia.icerunner.state;

import com.google.inject.Inject;
import com.weapia.icerunner.config.WorldConfiguration;
import com.weapia.icerunner.team.MinigameTeam;
import net.sunken.common.config.InjectConfig;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.team.TeamManager;
import net.sunken.core.team.config.TeamConfiguration;
import net.sunken.core.team.impl.Team;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

public class GameState extends EventGameState {

    @Inject
    private TeamManager teamManager;
    @Inject @InjectConfig
    private TeamConfiguration teamConfiguration;

    private WorldConfiguration worldConfiguration;

    @Override
    public void start(BaseGameState previous) {
        worldConfiguration = loadConfig(String.format("config/world/%s.conf", pluginInform.getServer().getWorld().toString()), WorldConfiguration.class);

        Queue<MinigameTeam> teams = new LinkedList<>();
        for (ChatColor )
        teamManager.assignOnlinePlayers(teams);
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
