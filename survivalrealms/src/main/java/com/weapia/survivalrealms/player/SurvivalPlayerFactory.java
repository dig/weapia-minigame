package com.weapia.survivalrealms.player;

import com.google.inject.Inject;
import net.sunken.core.scoreboard.ScoreboardRegistry;

import java.util.UUID;

public class SurvivalPlayerFactory {

    @Inject
    private ScoreboardRegistry scoreboardRegistry;

    public SurvivalPlayer createPlayer(UUID uuid, String username) {
        return new SurvivalPlayer(uuid, username, scoreboardRegistry);
    }
}
