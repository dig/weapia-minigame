package com.weapia.icerunner.team.event;

import com.weapia.icerunner.team.MinigameTeam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.event.SunkenEvent;

/**
 * Called when a team gets score.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class TeamScoreAddEvent extends SunkenEvent {

    private MinigameTeam team;
    private double amount;
    private double total;

}

