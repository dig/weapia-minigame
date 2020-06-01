package com.minevasion.spacegames;

import com.minevasion.spacegames.state.WaitingState;
import net.sunken.common.server.Server;
import net.sunken.core.Core;
import net.sunken.core.engine.EngineManager;
import net.sunken.core.engine.GameMode;

public class SpaceGames extends Core {

    @Override
    public void onEnable() {
        super.onEnable(new SpaceGamesPluginModule(this));

        //--- Engine
        EngineManager engineManager = injector.getInstance(EngineManager.class);
        engineManager.setGameMode(GameMode.builder()
                .isStateTicking(true)
                .initialState(() -> injector.getInstance(WaitingState.class))
                .build());

        //--- Change state to open
        pluginInform.setState(Server.State.OPEN);
    }

}
