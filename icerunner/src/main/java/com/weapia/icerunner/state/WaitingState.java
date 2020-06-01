package com.weapia.icerunner.state;

import com.google.inject.Inject;
import com.weapia.icerunner.team.MinigameTeam;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.BaseWaitingState;
import net.sunken.core.team.TeamManager;
import net.sunken.core.team.impl.Team;
import org.bukkit.WorldCreator;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class WaitingState extends BaseWaitingState {

    @Inject
    private TeamManager teamManager;

    @Inject
    public WaitingState(GameState gameState) {
        nextState = gameState;
    }

    @Override
    public void start(BaseGameState previous) {
        super.start(previous);
        new WorldCreator("map").createWorld();
        teamManager.setTeamConfigMapper(teamSingleConfiguration -> new MinigameTeam(teamSingleConfiguration.getColour(), teamSingleConfiguration.getMaxPlayers()));
    }

    @Override
    public void stop(BaseGameState next) {
        super.stop(next);
        teamManager.allocateTeams();
    }
}
