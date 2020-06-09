package com.weapia.icerunner.state;

import com.google.inject.Inject;
import com.weapia.icerunner.team.MinigameTeamFactory;
import com.weapia.icerunner.team.state.AliveTeamState;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.BasePreGameState;
import net.sunken.core.team.TeamManager;
import org.bukkit.WorldCreator;

public class PreGameState extends BasePreGameState {

    @Inject
    private TeamManager teamManager;
    @Inject
    private AliveTeamState aliveTeamState;
    @Inject
    private MinigameTeamFactory minigameTeamFactory;

    @Inject
    public PreGameState(GameState gameState) {
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

        teamManager.setTeamConfigMapper(teamSingleConfiguration -> minigameTeamFactory.createTeam(teamSingleConfiguration.getId(), teamSingleConfiguration.getColour(), teamSingleConfiguration.getDisplayName(), teamSingleConfiguration.getMaxPlayers(), aliveTeamState));
        teamManager.allocateTeams();
    }

}
