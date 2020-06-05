package com.weapia.icerunner.capture;

import com.google.common.collect.Lists;
import com.weapia.icerunner.Constants;
import com.weapia.icerunner.team.MinigameTeam;
import com.weapia.icerunner.team.state.AliveTeamState;
import lombok.AllArgsConstructor;
import net.sunken.core.team.TeamManager;
import net.sunken.core.team.impl.Team;
import net.sunken.core.util.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public class CapturePoint {

    private final String displayName;
    private final Cuboid cuboid;
    private final double scorePerTick;
    private final TeamManager teamManager;

    private final List<String> contestants = Lists.newArrayList();

    public void tick(int tickCount) {
        if (tickCount % 20 == 0) {
            String teamlastCaptured = contestants.size() == 1 ? contestants.get(0) : null;
            boolean wasCaptured = contestants.size() >= 1;
            contestants.clear();

            Set<Team> teams = teamManager.hasState(AliveTeamState.class);
            for (Team team : teams) {
                boolean inside = false;
                for (UUID uuid : team.getMembers()) {
                    Player target = Bukkit.getPlayer(uuid);
                    if (cuboid.contains(target.getLocation())) {
                        inside = true;
                        break;
                    }
                }

                if (inside && !contestants.contains(team)) {
                    contestants.add(team.getId());
                }
            }

            if (contestants.size() == 1) {
                Optional<Team> teamOptional = teamManager.getById(contestants.get(0));

                if (teamOptional.isPresent()) {
                    MinigameTeam minigameTeam = (MinigameTeam) teamOptional.get();
                    minigameTeam.addScore(scorePerTick * 20);
                    Bukkit.broadcastMessage(minigameTeam.getDisplayName() + ": " + minigameTeam.getScore() + "");

                    if (teamlastCaptured == null || (teamlastCaptured != null && !teamlastCaptured.equals(minigameTeam.getId()))) {
                        Bukkit.broadcastMessage(String.format(minigameTeam.getColour() + Constants.CAPTURE_POINT_CAPTURED, minigameTeam.getDisplayName(), displayName));
                    }
                }
            } else if (contestants.size() > 1 && teamlastCaptured != null) {
                Bukkit.broadcastMessage(String.format(Constants.CAPTURE_POINT_CONTESTED, displayName));
            } else if (wasCaptured) {
                Bukkit.broadcastMessage(String.format(Constants.CAPTURE_POINT_EMPTY, displayName));
            }
        }
    }

}
