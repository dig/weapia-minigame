package com.weapia.icerunner.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class CaptureConfiguration {

    @Setting
    private String displayName;

    @Setting
    private double scorePerTick;

    @Setting
    private LocationConfiguration min;

    @Setting
    private LocationConfiguration max;

    @Setting
    private FillConfiguration fill;

}
