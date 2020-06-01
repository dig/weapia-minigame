package com.weapia.icerunner.team.state;

import net.sunken.core.engine.state.impl.BaseTeamState;
import net.sunken.core.team.impl.Team;

import java.util.UUID;

public class AliveTeamState extends BaseTeamState {

    public AliveTeamState(Team team) {
        super(team);
    }

    @Override
    public void start(BaseTeamState previous) {
    }

    @Override
    public void stop(BaseTeamState next) {
    }

    @Override
    public void onJoin(UUID uuid) {

    }

    @Override
    public void onQuit(UUID uuid) {

    }

}
