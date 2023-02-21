package io.github.anonymous123_code.map_zones.api;

import io.github.anonymous123_code.map_zones.entities.MapZone;
import net.minecraft.util.math.BlockPos;

public interface ItemStackData {
	MapZone mapZones$getBound();

	void mapZones$setBound(MapZone mapZones$bound);

	BlockPos mapZones$getFirstCorner();

	void mapZones$setFirstCorner(BlockPos mapZones$leftCorner);

	BlockPos mapZones$getSecondCorner();

	void mapZones$setSecondCorner(BlockPos mapZones$rightCorner);
}
