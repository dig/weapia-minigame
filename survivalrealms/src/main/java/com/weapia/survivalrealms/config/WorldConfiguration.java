package com.weapia.survivalrealms.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class WorldConfiguration {

    @Setting
    private boolean adventure;

    @Setting
    private LocationConfiguration spawn;

}
