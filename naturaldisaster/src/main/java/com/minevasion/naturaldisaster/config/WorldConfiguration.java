package com.minevasion.naturaldisaster.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class WorldConfiguration {

    @Setting
    private LocationConfiguration spawn;

    @Setting
    private int spawnRadius;

    @Setting
    private int maxRadius;

    @Setting
    private int disasterRadius;

    @Setting
    private int minimumDisasters;

    @Setting
    private int seaLevel;

    @Setting
    private int maxSeaLevel;

}
