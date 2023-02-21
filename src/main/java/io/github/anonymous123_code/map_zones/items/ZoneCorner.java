package io.github.anonymous123_code.map_zones.items;


import io.github.anonymous123_code.map_zones.api.ItemStackData;
import io.github.anonymous123_code.map_zones.entities.MapZone;
import io.github.anonymous123_code.map_zones.entities.MapZonesEntities;
import io.github.anonymous123_code.map_zones.mixin.InGameHudMixin;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

import java.util.Objects;

public class ZoneCorner extends MapZonesItem {
	public ZoneCorner(Settings settings) {
		super(settings);
	}

	public ActionResult onLeft(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		ItemStackData stack = asItemStackData(player.getStackInHand(hand));
		if (stack.mapZones$getBound() == null) {
			stack.mapZones$setFirstCorner(pos);
			this.createEntityAndClearIfNeeded(stack, world, player);
		} else {
			stack.mapZones$getBound().setFirst(pos);
		}
		refreshHudName();
		return ActionResult.SUCCESS;
	}

	public ActionResult onRight(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		ItemStackData stack = asItemStackData(player.getStackInHand(hand));
		if (stack.mapZones$getBound() == null) {
			stack.mapZones$setSecondCorner(hitResult.getBlockPos());
			this.createEntityAndClearIfNeeded(stack, world, player);
		} else {
			stack.mapZones$getBound().setSecond(hitResult.getBlockPos());
		}
		refreshHudName();
		return ActionResult.SUCCESS;
	}

	public TypedActionResult<ItemStack> useSneakedOnEntity(ItemStack stack, PlayerEntity user, MapZone entity, Hand hand) {
		asItemStackData(stack).mapZones$setBound(entity);
		refreshHudName();
		return TypedActionResult.success(stack);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStackData stack = asItemStackData(user.getStackInHand(hand));
		if (user.isSneaking()) {
			HitResult hitResult = raycast(user, 0);
			if (hitResult instanceof EntityHitResult entityHitResult
					&& entityHitResult.getEntity() instanceof MapZone) {

				TypedActionResult<ItemStack> result = this.useSneakedOnEntity(user.getStackInHand(hand), user, (MapZone) entityHitResult.getEntity(), hand);

				if (Objects.requireNonNull(result.getResult()) != ActionResult.PASS) {
					return result;
				}
			}

			if (stack.mapZones$getBound() != null || stack.mapZones$getSecondCorner() != null || stack.mapZones$getFirstCorner() != null) {
				stack.mapZones$setBound(null);
				stack.mapZones$setFirstCorner(null);
				stack.mapZones$setSecondCorner(null);
			} else {
				stack.mapZones$setAttachmentModeActive(!stack.mapZones$getAttachmentModeActive());
			}
			refreshHudName();
			return TypedActionResult.success(user.getStackInHand(hand));
		}
		return super.use(world, user, hand);
	}

	public void createEntityAndClearIfNeeded(ItemStackData itemStack, World world, PlayerEntity player) {
		if (itemStack.mapZones$getFirstCorner() == null || itemStack.mapZones$getSecondCorner() == null) return;

		if (world instanceof ServerWorld serverWorld) {
			NbtCompound nbtCompound = new NbtCompound();

			nbtCompound.putIntArray("FirstPos", new int[]{itemStack.mapZones$getFirstCorner().getX(), itemStack.mapZones$getFirstCorner().getY(), itemStack.mapZones$getFirstCorner().getZ()});
			nbtCompound.putIntArray("SecondPos", new int[]{itemStack.mapZones$getSecondCorner().getX(), itemStack.mapZones$getSecondCorner().getY(), itemStack.mapZones$getSecondCorner().getZ()});

			nbtCompound.putString("id", MapZonesEntities.MAP_ZONE_ID.toString());

			Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, entityx -> entityx);

			serverWorld.shouldCreateNewEntityWithPassenger(entity);
		}
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) {
			player.sendMessage(Text.literal("Created Zone"), true);
		}
		itemStack.mapZones$setFirstCorner(null);
		itemStack.mapZones$setSecondCorner(null);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		ItemStackData stackData = asItemStackData(stack);
		if (stackData.mapZones$getBound() == null) {
			if (stackData.mapZones$getAttachmentModeActive()) {
				return super.getTranslationKey() + ".attachmentModeActive";
			} else {
				return super.getTranslationKey() + "";
			}
		} else {
			if (stackData.mapZones$getAttachmentModeActive()) {
				return super.getTranslationKey() + ".bound.attachmentModeActive";
			} else {
				return super.getTranslationKey() + ".bound";
			}
		}
	}

	public Text getName(ItemStack stack) {
		ItemStackData stackData = asItemStackData(stack);
		Text leftCornerText = ((MutableText) Text.of("[L] ")).setStyle(Style.EMPTY.withColor(stackData.mapZones$getFirstCorner() == null ? Formatting.GRAY : Formatting.GREEN));
		Text rightCornerText = ((MutableText) Text.of("[R]")).setStyle(Style.EMPTY.withColor(stackData.mapZones$getSecondCorner() == null ? Formatting.GRAY : Formatting.GREEN));
		if (stackData.mapZones$getBound() == null) {
			if (stackData.mapZones$getAttachmentModeActive()) {
				return Text.translatable(super.getTranslationKey()).append(((MutableText) Text.of(" [Mode: Create New] [A] ")).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(leftCornerText).append(rightCornerText);
			} else {
				return Text.translatable(super.getTranslationKey()).append(((MutableText) Text.of(" [Mode: Create New] ")).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(leftCornerText).append(rightCornerText);
			}
		} else {
			if (stackData.mapZones$getAttachmentModeActive()) {
				return Text.translatable(super.getTranslationKey()).append(((MutableText) Text.of(" [Mode: Attach to Bound] ")).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(leftCornerText).append(rightCornerText);
			} else {
				return Text.translatable(super.getTranslationKey()).append(((MutableText) Text.of(" [Mode: Edit Bound] ")).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(leftCornerText).append(rightCornerText);
			}
		}
	}

	private static void refreshHudName() {
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) ((InGameHudMixin) MinecraftClient.getInstance().inGameHud).setHeldItemTooltipFade(60);
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

	private static ItemStackData asItemStackData(ItemStack stack) {
		return ((ItemStackData)(Object)stack);
	}
}
