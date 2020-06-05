package com.weapia.icerunner.state;

import com.google.inject.Inject;
import com.weapia.icerunner.team.MinigameTeam;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.BasePostGameState;
import net.sunken.core.team.TeamManager;
import net.sunken.core.team.impl.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Optional;

public class PostGameState extends BasePostGameState {

    @Inject
    private TeamManager teamManager;

    @Override
    public void start(BaseGameState previous) {
        super.start(previous);
        Bukkit.broadcastMessage("END OF GAME");
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
