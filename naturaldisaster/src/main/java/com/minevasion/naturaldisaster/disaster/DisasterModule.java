package com.minevasion.naturaldisaster.disaster;

import com.google.inject.AbstractModule;
import com.minevasion.naturaldisaster.config.AcidRainConfiguration;
import net.sunken.common.config.ConfigModule;

import java.io.File;

public class DisasterModule extends AbstractModule {

    @Override
    public void configure() {
        for (Disaster disaster : Disaster.values())
            install(new ConfigModule(new File(String.format("config/disaster/%s.conf", disaster.toString())), disaster.getConfigClass()));
    }

}
