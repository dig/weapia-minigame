package com.minevasion.spacegames.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class WorldConfiguration {

    @Setting
    private LocationConfiguration center;
    @Setting
    private List<LocationConfiguration> spawns;

}
