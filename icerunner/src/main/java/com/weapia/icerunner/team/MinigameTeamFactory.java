package com.weapia.icerunner.team;

import com.google.inject.Inject;
import lombok.NonNull;
import net.sunken.common.event.EventManager;
import net.sunken.core.engine.state.impl.BaseTeamState;
import org.bukkit.ChatColor;

public class MinigameTeamFactory {

    @Inject
    private EventManager eventManager;

    public MinigameTeam createTeam(@NonNull String id, @NonNull ChatColor colour, @NonNull String displayName, int maxPlayers, BaseTeamState baseTeamState) {
        return new MinigameTeam(id, colour, displayName, maxPlayers, baseTeamState, eventManager);
    }

}
