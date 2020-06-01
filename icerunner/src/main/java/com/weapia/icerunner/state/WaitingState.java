package com.weapia.icerunner.state;

import com.google.inject.Inject;
import com.weapia.icerunner.config.WorldConfiguration;
import com.weapia.icerunner.team.MinigameTeam;
import com.weapia.icerunner.team.state.AliveTeamState;
import net.sunken.common.config.InjectConfig;
import net.sunken.core.config.LocationConfiguration;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.BaseWaitingState;
import net.sunken.core.team.TeamManager;
import net.sunken.core.team.impl.Team;
import org.bukkit.Location;
import org.bukkit.WorldCreator;

import java.util.*;

public class WaitingState extends BaseWaitingState {

    @Inject
    private TeamManager teamManager;
    @Inject
    private AliveTeamState aliveTeamState;
    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

    @Inject
    public WaitingState(GameState gameState) {
        nextState = gameState;
    }

    @Override
    public void start(BaseGameState previous) {
        super.start(previous);
        new WorldCreator("map").createWorld();
    }

    @Override
    public void stop(BaseGameState next) {
        super.stop(next);

        teamManager.setTeamConfigMapper(teamSingleConfiguration -> new MinigameTeam(teamSingleConfiguration.getColour(), teamSingleConfiguration.getDisplayName(), teamSingleConfiguration.getMaxPlayers(), aliveTeamState));
        teamManager.allocateTeams();
    }

}
