package com.weapia.survivalrealms.player;

import com.google.inject.Inject;
import net.sunken.core.PluginInform;
import net.sunken.core.scoreboard.ScoreboardRegistry;

import java.util.UUID;

public class SurvivalPlayerFactory {

    @Inject
    private ScoreboardRegistry scoreboardRegistry;
    @Inject
    private PluginInform pluginInform;

    public SurvivalPlayer createPlayer(UUID uuid, String username) {
        return new SurvivalPlayer(uuid, username, scoreboardRegistry, pluginInform);
    }
}
