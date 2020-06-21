package com.weapia.survivalrealms.world;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.util.DummyGeneratorAccess;


public class CustomGeneratorAccess extends DummyGeneratorAccess {

    @Override
    public long getSeed() {
        return 0L;
    }

    @Override
    public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i) {
        return false;
    }
}
