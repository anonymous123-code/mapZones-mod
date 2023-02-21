package io.github.anonymous123_code.map_zones.items;


import io.github.anonymous123_code.map_zones.MapZones;
import io.github.anonymous123_code.map_zones.entities.MapZone;
import io.github.anonymous123_code.map_zones.entities.MapZonesEntities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Objects;

public class ZoneCorner extends MapZonesItem {
	private BlockPos firstPos = null;
	private BlockPos secondPos = null;

	private MapZone bound = null;

	public ZoneCorner(Settings settings) {
		super(settings);
	}

	public ActionResult onLeft(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (this.bound == null) {
			this.firstPos = pos;
			this.createEntityAndClearIfNeeded(world);
		} else {
			this.bound.setFirst(pos);
		}
		return ActionResult.SUCCESS;
	}

	public ActionResult onRight(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		if (this.bound == null) {
			this.secondPos = hitResult.getBlockPos();
			this.createEntityAndClearIfNeeded(world);
		} else {
			this.bound.setSecond(hitResult.getBlockPos());
		}
		return ActionResult.SUCCESS;
	}

	public TypedActionResult<ItemStack> useSneakedOnEntity(ItemStack stack, PlayerEntity user, MapZone entity, Hand hand) {
		this.bound = entity;
		return TypedActionResult.success(stack);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.isSneaking()) {
			HitResult hitResult = raycast(user, 0);
			if (hitResult instanceof EntityHitResult entityHitResult
					&& entityHitResult.getEntity() instanceof MapZone) {

				TypedActionResult<ItemStack> result = this.useSneakedOnEntity(user.getStackInHand(hand), user, (MapZone) entityHitResult.getEntity(), hand);

				if (Objects.requireNonNull(result.getResult()) != ActionResult.PASS) {
					return result;
				}
			}


			this.bound = null;
			this.firstPos = null;
			this.secondPos = null;
			return TypedActionResult.success(user.getStackInHand(hand));
		}
		return super.use(world, user, hand);
	}

	public void createEntityAndClearIfNeeded(World world) {
		if (this.firstPos == null || this.secondPos == null) return;

		if (world instanceof ServerWorld serverWorld) {
			NbtCompound nbtCompound = new NbtCompound();

			nbtCompound.putIntArray("FirstPos", new int[]{this.firstPos.getX(), this.firstPos.getY(), this.firstPos.getZ()});
			nbtCompound.putIntArray("SecondPos", new int[]{this.secondPos.getX(), this.secondPos.getY(), this.secondPos.getZ()});

			nbtCompound.putString("id", MapZonesEntities.MAP_ZONE_ID.toString());

			Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, entityx -> entityx);

			serverWorld.shouldCreateNewEntityWithPassenger(entity);

			MapZones.LOGGER.error("Should have created entity: {}", entity);
			this.firstPos = null;
			this.secondPos = null;
		}
		if (world instanceof ClientWorld && !MinecraftClient.getInstance().isInSingleplayer()) {
			this.firstPos = null;
			this.secondPos = null;
		}
	}

	// 100% not stolen from gadget
	// https://github.com/wisp-forest/gadget/blob/e26d8a7bccbd536b689c37cff004673f2f71481a/src/main/java/io/wispforest/gadget/client/GadgetClient.java#L229-L247
	// 100% not stolen from owo-whats-this
	// https://github.com/wisp-forest/owo-whats-this/blob/master/src/main/java/io/wispforest/owowhatsthis/OwoWhatsThis.java#L155-L171.
	public static HitResult raycast(Entity entity, float tickDelta) {
		var blockTarget = entity.raycast(5, tickDelta, false);

		var maxReach = entity.getRotationVec(tickDelta).multiply(5);
		var entityTarget = ProjectileUtil.raycast(
				entity,
				entity.getEyePos(),
				entity.getEyePos().add(maxReach),
				entity.getBoundingBox().stretch(maxReach),
				candidate -> true,
				5 * 5
		);

		return entityTarget != null && entityTarget.squaredDistanceTo(entity) < blockTarget.squaredDistanceTo(entity)
				? entityTarget
				: blockTarget;
	}
}
