package com.gmail.val59000mc.maploader;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VeinPopulator extends BlockPopulator {
    private final VeinGenerator veinGenerator;

    public VeinPopulator(VeinGenerator veinGenerator) {
        this.veinGenerator = veinGenerator;
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk source) {
        veinGenerator.generateVeinsInChunk(source);
    }
}
