package io.github.anonymous123_code.map_zones.items;

import io.github.anonymous123_code.map_zones.entities.MapZone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;


public class ZoneWrench extends Item implements MapZonesItem {
	public ZoneWrench(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		HitResult hitResult = MapZonesItem.raycast(user, 0);
		if (hitResult instanceof EntityHitResult entityHitResult
				&& entityHitResult.getEntity() instanceof MapZone) {

		}
		return TypedActionResult.success(user.getStackInHand(hand));
	}
}