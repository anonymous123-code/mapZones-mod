package io.github.anonymous123_code.map_zones.items;


import io.github.anonymous123_code.map_zones.api.ItemStackData;
import io.github.anonymous123_code.map_zones.entities.MapZone;
import io.github.anonymous123_code.map_zones.entities.MapZonesEntities;
import io.github.anonymous123_code.map_zones.mixin.InGameHudMixin;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.EnvType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

import java.util.List;
import java.util.Objects;

public class ZoneCorner extends Item implements MapZonesItem {
	public ZoneCorner(Settings settings) {
		super(settings);
	}

	public ActionResult onLeft(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		ItemStackData stack = asItemStackData(player.getStackInHand(hand));
		if (stack.mapZones$getBound() == null) {
			stack.mapZones$setFirstCorner(pos);
			this.createEntityAndClearIfNeeded(stack, world, player);
		} else {
			if (Permissions.check(player, "map_zones.edit.corner", 2)) {
				stack.mapZones$getBound().setFirst(pos);
			}
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
			if (Permissions.check(player, "map_zones.zone.edit.corner", 2)) {
				stack.mapZones$getBound().setSecond(hitResult.getBlockPos());
			}
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
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return false;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStackData stack = asItemStackData(user.getStackInHand(hand));
		if (user.isSneaking()) {
			HitResult hitResult = MapZonesItem.raycast(user, 0);
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
			}// else {
				//stack.mapZones$setAttachmentModeActive(!stack.mapZones$getAttachmentModeActive());
			//}
			refreshHudName();
			return TypedActionResult.success(user.getStackInHand(hand));
		}
		return super.use(world, user, hand);
	}

	public void createEntityAndClearIfNeeded(ItemStackData itemStack, World world, PlayerEntity player) {
		if (itemStack.mapZones$getFirstCorner() == null || itemStack.mapZones$getSecondCorner() == null) return;
		if (!Permissions.check(player, "map_zones.zone.create", 2)) return;

		if (world instanceof ServerWorld serverWorld) {
			NbtCompound nbtCompound = new NbtCompound();

			nbtCompound.putIntArray("FirstPos", new int[]{itemStack.mapZones$getFirstCorner().getX(), itemStack.mapZones$getFirstCorner().getY(), itemStack.mapZones$getFirstCorner().getZ()});
			nbtCompound.putIntArray("SecondPos", new int[]{itemStack.mapZones$getSecondCorner().getX(), itemStack.mapZones$getSecondCorner().getY(), itemStack.mapZones$getSecondCorner().getZ()});

			nbtCompound.putString("id", MapZonesEntities.MAP_ZONE_ID.toString());

			Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, entityx -> entityx);

			serverWorld.shouldCreateNewEntityWithPassenger(entity);
		}
		if (world.isClient()) {
			player.sendMessage(Text.translatable("item.map_zones.zone_corner.creation_success"), true);
		}
		itemStack.mapZones$setFirstCorner(null);
		itemStack.mapZones$setSecondCorner(null);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		ItemStackData stackData = asItemStackData(stack);
		if (stackData.mapZones$getBound() == null) {
			//if (stackData.mapZones$getAttachmentModeActive()) {
			//	return super.getTranslationKey() + ".attachmentModeActive";
			//} else {
				return super.getTranslationKey() + "";
			//}
		} else {
			//if (stackData.mapZones$getAttachmentModeActive()) {
			//	return super.getTranslationKey() + ".bound.attachmentModeActive";
			//} else {
				return super.getTranslationKey() + ".bound";
			//}
		}
	}

	@Override
	public Text getName(ItemStack stack) {
		ItemStackData stackData = asItemStackData(stack);
		Text leftCornerText = ((MutableText) Text.of("[L] ")).setStyle(Style.EMPTY.withColor(stackData.mapZones$getFirstCorner() == null ? Formatting.GRAY : Formatting.GREEN));
		Text rightCornerText = ((MutableText) Text.of("[R]")).setStyle(Style.EMPTY.withColor(stackData.mapZones$getSecondCorner() == null ? Formatting.GRAY : Formatting.GREEN));
		if (stackData.mapZones$getBound() == null) {
			//if (stackData.mapZones$getAttachmentModeActive()) {
			//	return Text.translatable(super.getTranslationKey()).append(((MutableText) Text.of(" [Mode: Create New] [A] ")).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(leftCornerText).append(rightCornerText);
			//} else {
				return Text.translatable(super.getTranslationKey()).append(Text.translatable(super.getTranslationKey() + ".create_mode").setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(leftCornerText).append(rightCornerText);
			//}
		} else {
			//if (stackData.mapZones$getAttachmentModeActive()) {
			//	return Text.translatable(super.getTranslationKey()).append(Text.translatable(super.getTranslationKey() + ".attach_mode").setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(leftCornerText).append(rightCornerText);
			//} else {
				return Text.translatable(super.getTranslationKey()).append(Text.translatable(super.getTranslationKey() + ".edit_mode").setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(leftCornerText).append(rightCornerText);
			//}
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("item.map_zones.zone_corner.tooltip.1"));
		tooltip.add(Text.translatable("item.map_zones.zone_corner.tooltip.2"));
		tooltip.add(Text.translatable("item.map_zones.zone_corner.tooltip.3"));
		tooltip.add(Text.translatable("item.map_zones.zone_corner.tooltip.4"));
		tooltip.add(Text.translatable("item.map_zones.zone_corner.tooltip.5"));
		tooltip.add(Text.translatable("item.map_zones.zone_corner.tooltip.6"));
	}

	private static void refreshHudName() {
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) ((InGameHudMixin) MinecraftClient.getInstance().inGameHud).setHeldItemTooltipFade(60);
	}

	private static ItemStackData asItemStackData(ItemStack stack) {
		return ((ItemStackData)(Object)stack);
	}
}
