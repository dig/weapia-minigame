package com.weapia.survivalrealms.world;

import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.Blocks;
import net.minecraft.server.v1_15_R1.ChunkSection;
import net.minecraft.server.v1_15_R1.IBlockData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftMagicNumbers;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;

import java.util.HashSet;
import java.util.Set;

public class CustomChunkData implements ChunkGenerator.ChunkData {

    private final int maxHeight;
    private final ChunkSection[] sections;
    private Set<BlockPosition> tiles;

    public CustomChunkData(World world) {
        this(world.getMaxHeight());
    }

    CustomChunkData(int maxHeight) {
        if (maxHeight > 256) {
            throw new IllegalArgumentException("World height exceeded max chunk height");
        } else {
            this.maxHeight = maxHeight;
            this.sections = new ChunkSection[maxHeight >> 4];
        }
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public void setBlock(int x, int y, int z, Material material) {
        this.setBlock(x, y, z, material.createBlockData());
    }

    public void setBlock(int x, int y, int z, MaterialData material) {
        this.setBlock(x, y, z, CraftMagicNumbers.getBlock(material));
    }

    public void setBlock(int x, int y, int z, BlockData blockData) {
        this.setBlock(x, y, z, ((CraftBlockData)blockData).getState());
    }

    public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, Material material) {
        this.setRegion(xMin, yMin, zMin, xMax, yMax, zMax, material.createBlockData());
    }

    public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, MaterialData material) {
        this.setRegion(xMin, yMin, zMin, xMax, yMax, zMax, CraftMagicNumbers.getBlock(material));
    }

    public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, BlockData blockData) {
        this.setRegion(xMin, yMin, zMin, xMax, yMax, zMax, ((CraftBlockData)blockData).getState());
    }

    public Material getType(int x, int y, int z) {
        return CraftMagicNumbers.getMaterial(this.getTypeId(x, y, z).getBlock());
    }

    public MaterialData getTypeAndData(int x, int y, int z) {
        return CraftMagicNumbers.getMaterial(this.getTypeId(x, y, z));
    }

    public BlockData getBlockData(int x, int y, int z) {
        return CraftBlockData.fromData(this.getTypeId(x, y, z));
    }

    public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, IBlockData type) {
        if (xMin <= 15 && yMin < this.maxHeight && zMin <= 15) {
            if (xMin < 0) {
                xMin = 0;
            }

            if (yMin < 0) {
                yMin = 0;
            }

            if (zMin < 0) {
                zMin = 0;
            }

            if (xMax > 16) {
                xMax = 16;
            }

            if (yMax > this.maxHeight) {
                yMax = this.maxHeight;
            }

            if (zMax > 16) {
                zMax = 16;
            }

            if (xMin < xMax && yMin < yMax && zMin < zMax) {
                for(int y = yMin; y < yMax; ++y) {
                    ChunkSection section = this.getChunkSection(y, true);
                    int offsetBase = y & 15;

                    for(int x = xMin; x < xMax; ++x) {
                        for(int z = zMin; z < zMax; ++z) {
                            section.setType(x, offsetBase, z, type);
                        }
                    }
                }
            }
        }
    }

    public IBlockData getTypeId(int x, int y, int z) {
        if (x == (x & 15) && y >= 0 && y < this.maxHeight && z == (z & 15)) {
            ChunkSection section = this.getChunkSection(y, false);
            return section == null ? Blocks.AIR.getBlockData() : section.getType(x, y & 15, z);
        } else {
            return Blocks.AIR.getBlockData();
        }
    }

    public byte getData(int x, int y, int z) {
        return CraftMagicNumbers.toLegacyData(this.getTypeId(x, y, z));
    }

    private void setBlock(int x, int y, int z, IBlockData type) {
        if (x == (x & 15) && y >= 0 && y < this.maxHeight && z == (z & 15)) {
            ChunkSection section = this.getChunkSection(y, true);
            section.setType(x, y & 15, z, type);
            if (type.getBlock().isTileEntity()) {
                if (this.tiles == null) {
                    this.tiles = new HashSet();
                }

                this.tiles.add(new BlockPosition(x, y, z));
            }

        }
    }

    private ChunkSection getChunkSection(int y, boolean create) {
        ChunkSection section = this.sections[y >> 4];
        if (create && section == null) {
            this.sections[y >> 4] = section = new ChunkSection(y >> 4 << 4);
        }

        return section;
    }

    public ChunkSection[] getRawChunkData() {
        return this.sections;
    }

    Set<BlockPosition> getTiles() {
        return this.tiles;
    }
}
