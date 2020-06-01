package com.minevasion.spacegames.state;

import com.google.inject.Inject;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.BaseWaitingState;
import org.bukkit.WorldCreator;

public class WaitingState extends BaseWaitingState {

    @Inject
    public WaitingState(PreGameState preGameState) {
        nextState = preGameState;
    }

    @Override
    public void start(BaseGameState previous) {
        super.start(previous);
        new WorldCreator("map").createWorld();
    }

}
