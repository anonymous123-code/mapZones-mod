package io.github.anonymous123_code.map_zones.items;

import io.github.anonymous123_code.map_zones.client.gui.ZoneConfigScreen;
import io.github.anonymous123_code.map_zones.entities.MapZone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class ZoneWrench extends Item implements MapZonesItem {
	public ZoneWrench(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		HitResult hitResult = MapZonesItem.raycast(user, 0);
		if (hitResult instanceof EntityHitResult entityHitResult
				&& entityHitResult.getEntity() instanceof MapZone) {
			if (user instanceof ClientPlayerEntity) {
				MinecraftClient.getInstance().setScreen(new ZoneConfigScreen(new ArrayList<>(), List.of("Hi", "hello"), List.of("bye")));
			}
		}
		return TypedActionResult.success(user.getStackInHand(hand));
	}
}
