package com.minevasion.naturaldisaster.player;

import com.google.inject.Inject;
import net.sunken.core.scoreboard.ScoreboardManager;

import java.util.UUID;

public class MinigamePlayerFactory {

    @Inject
    private ScoreboardManager scoreboardManager;

    public MinigamePlayer createPlayer(UUID uuid, String username) {
        return new MinigamePlayer(uuid, username, scoreboardManager);
    }

}
