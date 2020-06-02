package com.weapia.icerunner.player;

import java.util.UUID;

public class MinigamePlayerFactory {

    public MinigamePlayer createPlayer(UUID uuid, String username) {
        return new MinigamePlayer(uuid, username);
    }

}
