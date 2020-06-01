package com.minevasion.naturaldisaster.disaster;

import com.minevasion.naturaldisaster.config.*;
import lombok.Getter;

public enum Disaster {

    ACID_RAIN (AcidRainDisaster.class, AcidRainConfiguration.class),
    FLASH_FLOODING (FlashFloodingDisaster.class, FlashFloodingConfiguration.class),
    THUNDERSTORM (ThunderstormDisaster.class, ThunderstormConfiguration.class),
    ANVIL_STRIKE (AnvilStrikeDisaster.class, AnvilStrikeConfiguration.class),
    POWER_OUT (PowerOutDisaster.class, PowerOutConfiguration.class);

    @Getter
    private Class<? extends BaseDisaster> disasterClass;
    @Getter
    private Class configClass;

    Disaster(Class<? extends BaseDisaster> disasterClass, Class configClass) {
        this.disasterClass = disasterClass;
        this.configClass = configClass;
    }

}
