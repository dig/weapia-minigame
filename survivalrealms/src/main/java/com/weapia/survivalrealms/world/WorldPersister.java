package com.weapia.survivalrealms.world;

import net.sunken.common.database.*;
import net.sunken.common.inject.*;
import net.sunken.common.server.*;
import org.bukkit.entity.*;

import javax.inject.*;

@Singleton
public class WorldPersister implements Facet, Enableable {

    @Inject
    private MongoConnection mongoConnection;

    public void hasWorld(Player player) {
    }

    public void persistWorld(Player player, World world) {
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
    }
}
