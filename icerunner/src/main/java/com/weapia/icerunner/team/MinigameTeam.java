package com.weapia.icerunner.team;

import com.weapia.icerunner.team.event.TeamScoreAddEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.sunken.common.event.EventManager;
import net.sunken.core.engine.state.impl.BaseTeamState;
import net.sunken.core.team.impl.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class MinigameTeam extends Team {

    @Getter @Setter
    private Location spawn;
    @Getter
    private double score;
    private EventManager eventManager;

    public MinigameTeam(@NonNull String id, @NonNull ChatColor colour, @NonNull String displayName, int maxPlayers, BaseTeamState baseTeamState, @NonNull EventManager eventManager) {
        super(id, colour, displayName, maxPlayers, baseTeamState);
        this.spawn = null;
        this.score = 0;
        this.eventManager = eventManager;
    }

    public void addScore(double amount) {
        score += amount;
        eventManager.callEvent(new TeamScoreAddEvent(this, amount, score));
    }

}
