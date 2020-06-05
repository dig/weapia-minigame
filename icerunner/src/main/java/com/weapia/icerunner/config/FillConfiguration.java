package com.weapia.icerunner.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

@Getter
@ConfigSerializable
public class FillConfiguration {

    @Setting
    private Material defaultType;

    @Setting
    private LocationConfiguration min;

    @Setting
    private LocationConfiguration max;

}
