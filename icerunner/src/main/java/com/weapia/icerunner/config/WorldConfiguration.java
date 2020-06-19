package com.weapia.icerunner.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class WorldConfiguration {

    @Setting
    private List<LocationConfiguration> spawns;

    @Setting
    private double scoreToWin;

    @Setting
    private List<CaptureConfiguration> capturePoints;

}
