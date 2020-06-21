package com.weapia.survivalrealms.world;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.util.DummyGeneratorAccess;

public class ChunkOverrider<C extends GeneratorSettingsDefault> extends ChunkGenerator<C> {

    final ChunkGenerator<C> parent;

    public ChunkOverrider(ChunkGenerator<C> parent) {
        super(DummyGeneratorAccess.INSTANCE, null, null);
        this.parent = parent;
    }


    @Override
    public void buildBase(RegionLimitedWorldAccess regionLimitedWorldAccess, IChunkAccess iChunkAccess) {
        parent.buildBase(regionLimitedWorldAccess, iChunkAccess);
    }

    @Override
    public int getSpawnHeight() {
        return parent.getSpawnHeight();
    }

    @Override
    public void buildNoise(GeneratorAccess generatorAccess, IChunkAccess iChunkAccess) {
        parent.buildNoise(generatorAccess, iChunkAccess);
    }

    @Override
    public int getBaseHeight(int i, int i1, HeightMap.Type type) {
        return parent.getBaseHeight(i, i1, type);
    }
}
