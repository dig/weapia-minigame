package com.weapia.icerunner.player;

import net.sunken.core.player.CorePlayer;

import java.util.UUID;

public class MinigamePlayer extends CorePlayer {

    public MinigamePlayer(UUID uuid, String username) {
        super(uuid, username);
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public boolean save() {
        return true;
    }

}
