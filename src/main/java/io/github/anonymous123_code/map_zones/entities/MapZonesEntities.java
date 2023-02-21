package io.github.anonymous123_code.map_zones.entities;

import io.github.anonymous123_code.map_zones.MapZones;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class MapZonesEntities {
	public static EntityType<MapZone> MAP_ZONE;
	public static Identifier MAP_ZONE_ID;

	public static void register() {
		MAP_ZONE_ID = MapZones.id("map_zone");
		MAP_ZONE = Registry.register(
				Registries.ENTITY_TYPE,
				MAP_ZONE_ID,
				QuiltEntityTypeBuilder.create(SpawnGroup.MISC, MapZone::new)
						.setDimensions(EntityDimensions.changing(1, 1))
						.build()
		);

	}
}
