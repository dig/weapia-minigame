package com.weapia.survivalrealms;

import com.weapia.survivalrealms.player.SurvivalPlayerFactory;
import com.weapia.survivalrealms.state.GameState;
import net.sunken.common.server.Server;
import net.sunken.core.Core;
import net.sunken.core.engine.EngineManager;
import net.sunken.core.engine.GameMode;

public class SurvivalRealms extends Core {

    @Override
    public void onEnable() {
        super.onEnable(new SurvivalRealmsPluginModule(this));

        EngineManager engineManager = injector.getInstance(EngineManager.class);
        SurvivalPlayerFactory playerFactory = injector.getInstance(SurvivalPlayerFactory.class);

        engineManager.setGameMode(GameMode.builder()
                .isStateTicking(true)
                .initialState(() -> injector.getInstance(GameState.class))
                .playerMapper(uuidStringTuple -> playerFactory.createPlayer(uuidStringTuple.getX(), uuidStringTuple.getY()))
                .build());

        pluginInform.setState(Server.State.OPEN);
    }
}
