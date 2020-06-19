package com.weapia.icerunner.state;

import com.google.inject.Inject;
import com.weapia.icerunner.config.WorldConfiguration;
import com.weapia.icerunner.team.MinigameTeam;
import com.weapia.icerunner.team.state.AliveTeamState;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.BasePostGameState;
import net.sunken.core.team.TeamManager;
import net.sunken.core.team.impl.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PostGameState extends BasePostGameState {

    @Inject
    private TeamManager teamManager;
    private WorldConfiguration worldConfiguration;

    @Override
    public void start(BaseGameState previous) {
        super.start(previous);
        worldConfiguration = loadConfig(String.format("config/world/%s.conf", pluginInform.getServer().getWorld().toString()), WorldConfiguration.class);

        Set<Team> aliveTeams = teamManager.hasState(AliveTeamState.class);
        Optional<MinigameTeam> winningTeamOptional = Optional.empty();

        if (aliveTeams.size() == 1) {
            winningTeamOptional = Optional.ofNullable((MinigameTeam) aliveTeams.iterator().next());
        }

        for (Team team : aliveTeams) {
            MinigameTeam minigameTeam = (MinigameTeam) team;
            if (minigameTeam.getScore() >= worldConfiguration.getScoreToWin()) {
                winningTeamOptional = Optional.of(minigameTeam);
                break;
            }
        }

        if (winningTeamOptional.isPresent()) {
            MinigameTeam winningTeam = winningTeamOptional.get();

            String teamMembers = "";
            for (UUID uuid : winningTeam.getMembers()) {
                Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(uuid);
                if (abstractPlayerOptional.isPresent()) {
                    AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
                    String name = ChatColor.valueOf(abstractPlayer.getRank().getColour()) + abstractPlayer.getUsername();
                    teamMembers += teamMembers == "" ? name : ", " + name;
                }
            }

            Arrays.asList(
                    " ",
                    ChatColor.AQUA + "" + ChatColor.BOLD + " ICE RUNNER",
                    ChatColor.YELLOW + " We have a winner! Team " + winningTeam.getColour() + "" + ChatColor.BOLD + winningTeam.getDisplayName(),
                    ChatColor.YELLOW + " Members: " + teamMembers,
                    " ",
                    ChatColor.GOLD + "" + ChatColor.BOLD + " Click here " + ChatColor.GREEN + "to join a new game",
                    " "
            ).forEach(Bukkit::broadcastMessage);
        } else {
            Arrays.asList(
                    " ",
                    ChatColor.AQUA + "" + ChatColor.BOLD + " ICE RUNNER",
                    ChatColor.RED + " No winners... just losers!",
                    " ",
                    ChatColor.GOLD + "" + ChatColor.BOLD + " Click here " + ChatColor.GREEN + "to join a new game",
                    " "
            ).forEach(Bukkit::broadcastMessage);
        }
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
