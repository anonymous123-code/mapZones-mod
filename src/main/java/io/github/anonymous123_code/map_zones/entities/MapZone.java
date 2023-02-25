package io.github.anonymous123_code.map_zones.entities;

import io.github.anonymous123_code.map_zones.MapZones;
import io.github.anonymous123_code.map_zones.api.OverlapCallbacks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class MapZone extends Entity implements OverlapCallbacks {
	private static final TrackedData<BlockPos> first = DataTracker.registerData(MapZone.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<BlockPos> second = DataTracker.registerData(MapZone.class, TrackedDataHandlerRegistry.BLOCK_POS);
	public static final String TAG_PREFIX = "mods/map_zones/collidesWith/";
	private EntityDimensions dimensions = super.getDimensions(null);
	public MapZone(EntityType<?> variant, World world) {
		super(variant, world);
	}

	@Override
	public void tick() {
		super.tick();
		List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox().expand(1.0E-7), EntityPredicates.EXCEPT_SPECTATOR.and(entity -> !(entity instanceof MapZone))); // TODO: replace with MapZones:Shouldn't trigger tag
		if (!list.isEmpty()) {
			for (Entity entity2 : list) {
				if (entity2.getBoundingBox().intersects(this.getBoundingBox())) {
					this.onEntityIntersection(entity2);
				}
			}
		}

	}

	private void onEntityIntersection(Entity other) {
		if (this.getWorld() instanceof ServerWorld) {
			String scoreboardTag = TAG_PREFIX + this.uuidString;
			if (!other.getScoreboardTags().contains(scoreboardTag)) {
				this.onEntityIntersectionBegin(other);
				other.addScoreboardTag(scoreboardTag);
			}
			this.onEntityIntersectionTick(other);
		}
	}

	@Override
	public void onEntityIntersectionBegin(Entity other) {
		MapZones.LOGGER.info("Hello, I'm {}", other);
	}

	@Override
	public void onEntityIntersectionTick(Entity other) {
		MapZones.LOGGER.info("I'm {}", other);
	}

	@Override
	public void onEntityIntersectionEnd(Entity other) {
		MapZones.LOGGER.info("Bye, I'm {}", other);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return this.dimensions;
	}

	@Override
	protected Box calculateBoundsForPose(EntityPose pos) {
		return this.calculateBoundingBox();
	}

	@Override
	protected Box calculateBoundingBox() {
		return new Box(this.getFirst(), this.getSecond()).expand(0.5,0.5,0.5).offset(0.5,0.5,0.5);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(first, BlockPos.ORIGIN);
		this.dataTracker.startTracking(second, BlockPos.ORIGIN);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.setFirst(extractBlockPosFromIntList(nbt.getIntArray("FirstPos")));
		this.setSecond(extractBlockPosFromIntList(nbt.getIntArray("SecondPos")));
		this.updateDimensionsChange();
	}


	private BlockPos getFirst() {
		return this.dataTracker.get(first);
	}

	private BlockPos getSecond() {
		return this.dataTracker.get(second);
	}

	public void setFirst(BlockPos pos) {
		this.dataTracker.set(first, pos);
		this.updateDimensionsChange();
	}

	public void setSecond(BlockPos pos) {
		this.dataTracker.set(second, pos);
		this.updateDimensionsChange();
	}

	private static BlockPos extractBlockPosFromIntList(int[] list) {
		return switch (list.length) {
			case 0 -> BlockPos.ORIGIN;
			case 1 -> new BlockPos(list[0], 0, 0);
			case 2 -> new BlockPos(list[0], list[1], 0);
			default -> new BlockPos(list[0], list[1], list[2]);
		};
	}

	private void updateDimensionsChange() {
		Box box = this.calculateBoundingBox();
		this.dimensions = super.getDimensions(null).scaled(
				(float) Math.max(
						box.getXLength(),
						box.getZLength()
				),
				(float) box.getYLength()
		);
		this.calculateDimensions();
		this.setPosition(box.getCenter().withAxis(Direction.Axis.Y, box.getMin(Direction.Axis.Y)));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putIntArray("FirstPos", new int[]{this.getFirst().getX(), this.getFirst().getY(), this.getFirst().getZ()});
		nbt.putIntArray("SecondPos", new int[]{this.getSecond().getX(), this.getSecond().getY(), this.getSecond().getZ()});
	}
}
