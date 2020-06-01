package com.weapia.icerunner.team.state;

import net.sunken.core.engine.state.impl.BaseTeamState;
import net.sunken.core.team.impl.Team;

import java.util.UUID;

public class AliveTeamState extends BaseTeamState {

    @Override
    public void start(Team team, BaseTeamState previous) {

    }

    @Override
    public void stop(Team team, BaseTeamState next) {

    }

    @Override
    public void onJoin(Team team, UUID uuid) {
        super.onJoin(team, uuid);
    }

    @Override
    public void onQuit(Team team, UUID uuid) {
        super.onQuit(team, uuid);
    }

}
