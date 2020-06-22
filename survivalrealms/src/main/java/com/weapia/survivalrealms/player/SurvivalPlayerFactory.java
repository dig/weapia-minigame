package com.weapia.survivalrealms.player;

import com.google.inject.Inject;
import com.weapia.survivalrealms.config.WorldConfiguration;
import net.sunken.common.config.InjectConfig;
import net.sunken.core.PluginInform;
import net.sunken.core.scoreboard.ScoreboardRegistry;

import java.util.UUID;

public class SurvivalPlayerFactory {

    @Inject
    private ScoreboardRegistry scoreboardRegistry;
    @Inject
    private PluginInform pluginInform;
    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

    public SurvivalPlayer createPlayer(UUID uuid, String username) {
        return new SurvivalPlayer(uuid, username, scoreboardRegistry, pluginInform, worldConfiguration);
    }
}
