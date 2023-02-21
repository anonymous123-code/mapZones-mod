package io.github.anonymous123_code.map_zones.items;

import io.github.anonymous123_code.map_zones.MapZones;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class MapZonesItems {
	public static final ZoneCorner ZONE_CORNER = new ZoneCorner(new QuiltItemSettings().rarity(Rarity.EPIC));

	public static void register() {
		Registry.register(Registries.ITEM, MapZones.id("zone_corner"), ZONE_CORNER);
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			if (!player.isSpectator() && player.getMainHandStack().getItem() instanceof ZoneCorner) {
				return ((ZoneCorner) player.getMainHandStack().getItem()).onLeft(player, world, hand, pos, direction);
			}
			return ActionResult.PASS;
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!player.isSpectator() && player.getMainHandStack().getItem() instanceof ZoneCorner) {
				return ((ZoneCorner) player.getMainHandStack().getItem()).onRight(player, world, hand, hitResult);
			}
			return ActionResult.PASS;
		});
	}
}
