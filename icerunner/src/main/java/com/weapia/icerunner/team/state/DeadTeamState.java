package com.weapia.icerunner.team.state;

import com.google.inject.Inject;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.core.engine.state.PlayerSpectatorState;
import net.sunken.core.engine.state.impl.BaseTeamState;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.team.impl.Team;

import java.util.Optional;
import java.util.UUID;

public class DeadTeamState extends BaseTeamState {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PlayerSpectatorState playerSpectatorState;

    @Override
    public void start(Team team, BaseTeamState previous) {
        team.getMembers().forEach(uuid -> onJoin(team, uuid));
    }

    @Override
    public void stop(Team team, BaseTeamState next) {
    }

    @Override
    public void onJoin(Team team, UUID uuid) {
        super.onJoin(team, uuid);

        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(uuid);
        if (abstractPlayerOptional.isPresent()) {
            CorePlayer corePlayer = (CorePlayer) abstractPlayerOptional.get();
            corePlayer.setState(playerSpectatorState);
        }
    }

}
