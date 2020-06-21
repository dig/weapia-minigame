package com.weapia.survivalrealms.world;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.util.DummyGeneratorAccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class ChunkOverrider<C extends GeneratorSettingsDefault> extends ChunkGenerator<C> {
    
    private static Method getBiome;

    static {
        try {
            getBiome = ChunkGenerator.class.getDeclaredMethod("getBiome", BiomeManager.class, BlockPosition.class);
            getBiome.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private final ChunkGenerator<C> parent;
    public ChunkOverrider(GeneratorAccess generatorAccess, ChunkGenerator<C> parent) {
        super(generatorAccess, null, null);
        this.parent = parent;
    }

    @Override
    public void createBiomes(IChunkAccess ichunkaccess) {
        parent.createBiomes(ichunkaccess);
    }

    @Override
    protected BiomeBase getBiome(BiomeManager biomemanager, BlockPosition blockposition) {
        try {
            return (BiomeBase) getBiome.invoke(parent, biomemanager, blockposition);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void doCarving(BiomeManager biomemanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {
        parent.doCarving(biomemanager, ichunkaccess, worldgenstage_features);
    }

    @Override
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockPosition, int i, boolean b) {
        return parent.findNearestMapFeature(world, s, blockPosition, i, b);
    }

    @Override
    public void addDecorations(RegionLimitedWorldAccess regionLimitedWorldAccess) {
        parent.addDecorations(regionLimitedWorldAccess);
    }

    @Override
    public void buildBase(RegionLimitedWorldAccess regionLimitedWorldAccess, IChunkAccess iChunkAccess) {
        parent.buildBase(regionLimitedWorldAccess, iChunkAccess);
    }

    @Override
    public void addMobs(RegionLimitedWorldAccess regionLimitedWorldAccess) {
        parent.addMobs(regionLimitedWorldAccess);
    }

    @Override
    public C getSettings() {
        return parent.getSettings();
    }

    @Override
    public int getSpawnHeight() {
        return parent.getSpawnHeight();
    }

    @Override
    public void doMobSpawning(WorldServer worldserver, boolean flag, boolean flag1) {
        parent.doMobSpawning(worldserver, flag, flag1);
    }

    @Override
    public boolean canSpawnStructure(BiomeBase biomeBase, StructureGenerator<? extends WorldGenFeatureConfiguration> structureGenerator) {
        return parent.canSpawnStructure(biomeBase, structureGenerator);
    }

    @Override
    public <C1 extends WorldGenFeatureConfiguration> C1 getFeatureConfiguration(BiomeBase biomebase, StructureGenerator<C1> structuregenerator) {
        return parent.getFeatureConfiguration(biomebase, structuregenerator);
    }

    @Override
    public WorldChunkManager getWorldChunkManager() {
        return parent.getWorldChunkManager();
    }

    @Override
    public long getSeed() {
        return parent.getSeed();
    }

    @Override
    public int getGenerationDepth() {
        return parent.getGenerationDepth();
    }

    @Override
    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return parent.getMobsFor(enumcreaturetype, blockposition);
    }

    @Override
    public void createStructures(BiomeManager biomemanager, IChunkAccess ichunkaccess, ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager) {
        parent.createStructures(biomemanager, ichunkaccess, chunkgenerator, definedstructuremanager);
    }

    @Override
    public void storeStructures(GeneratorAccess generatoraccess, IChunkAccess ichunkaccess) {
        parent.storeStructures(generatoraccess, ichunkaccess);
    }

    @Override
    public void buildNoise(GeneratorAccess generatorAccess, IChunkAccess iChunkAccess) {
        parent.buildNoise(generatorAccess, iChunkAccess);
    }

    @Override
    public int getSeaLevel() {
        return parent.getSeaLevel();
    }

    @Override
    public int getBaseHeight(int i, int i1, HeightMap.Type type) {
        return parent.getBaseHeight(i, i1, type);
    }

    @Override
    public int b(int i, int j, HeightMap.Type heightmap_type) {
        return parent.b(i, j, heightmap_type);
    }

    @Override
    public int c(int i, int j, HeightMap.Type heightmap_type) {
        return parent.c(i, j, heightmap_type);
    }
}
