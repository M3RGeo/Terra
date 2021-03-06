package com.dfsek.terra.api.structures.script.functions;

import com.dfsek.terra.TerraWorld;
import com.dfsek.terra.api.math.vector.Location;
import com.dfsek.terra.api.math.vector.Vector2;
import com.dfsek.terra.api.math.vector.Vector3;
import com.dfsek.terra.api.platform.TerraPlugin;
import com.dfsek.terra.api.platform.world.World;
import com.dfsek.terra.api.structures.parser.lang.ImplementationArguments;
import com.dfsek.terra.api.structures.parser.lang.Returnable;
import com.dfsek.terra.api.structures.parser.lang.functions.Function;
import com.dfsek.terra.api.structures.script.TerraImplementationArguments;
import com.dfsek.terra.api.structures.structure.RotationUtil;
import com.dfsek.terra.api.structures.tokenizer.Position;
import com.dfsek.terra.api.world.generation.GenerationPhase;
import com.dfsek.terra.biome.UserDefinedBiome;
import com.dfsek.terra.biome.grid.master.TerraBiomeGrid;
import com.dfsek.terra.config.templates.BiomeTemplate;
import com.dfsek.terra.generation.math.SamplerCache;
import net.jafama.FastMath;

public class CheckFunction implements Function<String> {
    private final TerraPlugin main;
    private final Returnable<Number> x, y, z;
    private final Position position;
    private final SamplerCache cache;

    public CheckFunction(TerraPlugin main, Returnable<Number> x, Returnable<Number> y, Returnable<Number> z, SamplerCache cache, Position position) {
        this.main = main;
        this.x = x;
        this.y = y;
        this.z = z;
        this.position = position;
        this.cache = cache;
    }


    @Override
    public String apply(ImplementationArguments implementationArguments) {
        TerraImplementationArguments arguments = (TerraImplementationArguments) implementationArguments;

        Vector2 xz = new Vector2(x.apply(implementationArguments).doubleValue(), z.apply(implementationArguments).doubleValue());

        RotationUtil.rotateVector(xz, arguments.getRotation());

        Location location = arguments.getBuffer().getOrigin().clone().add(new Vector3(FastMath.roundToInt(xz.getX()), y.apply(implementationArguments).doubleValue(), FastMath.roundToInt(xz.getZ())));

        return apply(location, arguments.getBuffer().getOrigin().getWorld());
    }

    private String apply(Location vector, World world) {
        TerraWorld tw = main.getWorld(world);
        double comp = sample(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), world);

        if(comp > 0) return "LAND"; // If noise val is greater than zero, location will always be land.

        TerraBiomeGrid grid = tw.getGrid();
        UserDefinedBiome b = (UserDefinedBiome) grid.getBiome(vector.getBlockX(), vector.getBlockZ(), GenerationPhase.POPULATE);
        BiomeTemplate c = b.getConfig();

        if(vector.getY() > c.getSeaLevel()) return "AIR"; // Above sea level
        return "OCEAN"; // Below sea level
    }

    private double sample(int x, int y, int z, World w) {
        int cx = FastMath.floorDiv(x, 16);
        int cz = FastMath.floorDiv(z, 16);
        return cache.get(w, x, z).sample(x - (cx << 4), y, z - (cz << 4));
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public ReturnType returnType() {
        return ReturnType.STRING;
    }
}
