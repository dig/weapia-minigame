package com.weapia.icerunner.team;

import lombok.NonNull;
import net.sunken.core.engine.state.impl.BaseTeamState;
import net.sunken.core.team.impl.Team;
import org.bukkit.ChatColor;

public class MinigameTeam extends Team {

    public MinigameTeam(@NonNull ChatColor colour, @NonNull String displayName, int maxPlayers, BaseTeamState baseTeamState) {
        super(colour, displayName, maxPlayers, baseTeamState);
    }

}
