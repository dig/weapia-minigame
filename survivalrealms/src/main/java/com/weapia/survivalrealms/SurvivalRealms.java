package com.weapia.survivalrealms;

import net.sunken.common.server.Server;
import net.sunken.core.Core;
import net.sunken.core.engine.EngineManager;
import net.sunken.core.engine.GameMode;
import net.sunken.core.player.simple.MinigamePlayerFactory;

public class SurvivalRealms extends Core {

    @Override
    public void onEnable() {
        super.onEnable(new SurvivalRealmsPluginModule(this));

        EngineManager engineManager = injector.getInstance(EngineManager.class);
        MinigamePlayerFactory minigamePlayerFactory = injector.getInstance(MinigamePlayerFactory.class);

        engineManager.setGameMode(GameMode.builder()
                .isStateTicking(true)
                // .initialState(() -> injector.getInstance(PreGameState.class))
                .playerMapper(uuidStringTuple -> minigamePlayerFactory.createPlayer(uuidStringTuple.getX(), uuidStringTuple.getY()))
                .build());

        pluginInform.setState(Server.State.OPEN);
    }
}
