package com.weapia.icerunner.team;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.sunken.core.engine.state.impl.BaseTeamState;
import net.sunken.core.team.impl.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class MinigameTeam extends Team {

    @Getter @Setter
    private Location spawn;

    public MinigameTeam(@NonNull ChatColor colour, @NonNull String displayName, int maxPlayers, BaseTeamState baseTeamState) {
        super(colour, displayName, maxPlayers, baseTeamState);
        this.spawn = null;
    }

}
