package com.weapia.survivalrealms.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import net.sunken.core.npc.config.NPCConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class WorldConfiguration {

    @Setting
    private boolean adventure;

    @Setting
    private LocationConfiguration spawn;

    @Setting("npc")
    private List<NPCConfiguration> npcConfigurations;

}
