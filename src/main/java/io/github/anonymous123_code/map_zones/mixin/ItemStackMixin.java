package io.github.anonymous123_code.map_zones.mixin;

import io.github.anonymous123_code.map_zones.entities.MapZone;
import io.github.anonymous123_code.map_zones.api.ItemStackData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackData {
	public MapZone mapZones$bound;
	public BlockPos mapZones$firstCorner;
	public BlockPos mapZones$secondCorner;
	//public boolean attachmentModeActive;

	@Override
	public MapZone mapZones$getBound() {
		return this.mapZones$bound;
	}

	@Override
	public void mapZones$setBound(MapZone mapZones$bound) {
		this.mapZones$bound = mapZones$bound;
	}

	/*
	@Override
	public boolean mapZones$getAttachmentModeActive() {
		return this.attachmentModeActive;
	}

	@Override
	public void mapZones$setAttachmentModeActive(boolean mapZones$attachmentModeActive) {
		this.attachmentModeActive = mapZones$attachmentModeActive;
	}*/

	@Override
	public BlockPos mapZones$getFirstCorner() {
		return this.mapZones$firstCorner;
	}

	@Override
	public void mapZones$setFirstCorner(BlockPos mapZones$leftCorner) {
		this.mapZones$firstCorner = mapZones$leftCorner;
	}

	@Override
	public BlockPos mapZones$getSecondCorner() {
		return this.mapZones$secondCorner;
	}

	@Override
	public void mapZones$setSecondCorner(BlockPos mapZones$rightCorner) {
		this.mapZones$secondCorner = mapZones$rightCorner;
	}
}
