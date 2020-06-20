package com.weapia.survivalrealms.adventure;

import com.weapia.survivalrealms.adventure.state.GameState;
import net.sunken.common.server.Server;
import net.sunken.core.Core;
import net.sunken.core.engine.EngineManager;
import net.sunken.core.engine.GameMode;
import net.sunken.core.player.simple.MinigamePlayerFactory;

public class RealmsAdventure extends Core {

    @Override
    public void onEnable() {
        super.onEnable(new RealmsAdventurePluginModule(this));

        EngineManager engineManager = injector.getInstance(EngineManager.class);
        MinigamePlayerFactory playerFactory = injector.getInstance(MinigamePlayerFactory.class);

        engineManager.setGameMode(GameMode.builder()
                .isStateTicking(true)
                .initialState(() -> injector.getInstance(GameState.class))
                .playerMapper(uuidStringTuple -> playerFactory.createPlayer(uuidStringTuple.getX(), uuidStringTuple.getY()))
                .build());

        pluginInform.setState(Server.State.OPEN);
    }
}
