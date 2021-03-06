package com.dfsek.terra.fabric.world.handles.chunk;

import com.dfsek.terra.api.platform.block.Block;
import com.dfsek.terra.api.platform.world.Chunk;
import com.dfsek.terra.api.platform.world.World;

public class FabricChunk implements Chunk {
    private final net.minecraft.world.chunk.Chunk chunk;

    public FabricChunk(net.minecraft.world.chunk.Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public int getX() {
        return chunk.getPos().x;
    }

    @Override
    public int getZ() {
        return chunk.getPos().z;
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return null;
    }

    @Override
    public net.minecraft.world.chunk.Chunk getHandle() {
        return chunk;
    }
}
