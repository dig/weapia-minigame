package com.minevasion.naturaldisaster.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.ChatColor;

@Getter
@ConfigSerializable
public class AnvilStrikeConfiguration {

    @Setting
    private ChatColor colour;

    @Setting
    private String name;

    @Setting
    private String description;

    @Setting
    private long expire;

}
