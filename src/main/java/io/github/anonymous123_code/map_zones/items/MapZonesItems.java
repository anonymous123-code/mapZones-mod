package io.github.anonymous123_code.map_zones.items;

import io.github.anonymous123_code.map_zones.MapZones;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class MapZonesItems {
	public static final ZoneCorner ZONE_CORNER = new ZoneCorner(new QuiltItemSettings());
	public static final ZoneWrench ZONE_WRENCH = new ZoneWrench(new QuiltItemSettings());

	public static void register() {
		Registry.register(Registries.ITEM, MapZones.id("zone_corner"), ZONE_CORNER);
		Registry.register(Registries.ITEM, MapZones.id("zone_wrench"), ZONE_WRENCH);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR_UTILITIES).register((entries -> {
			entries.addItem(MapZonesItems.ZONE_CORNER);
			entries.addItem(MapZonesItems.ZONE_WRENCH);
		}));

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
