package io.github.anonymous123_code.map_zones.entities;

import io.github.anonymous123_code.map_zones.MapZones;
import io.github.anonymous123_code.map_zones.api.OverlapCallbacks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;
import org.quiltmc.qsl.entity.event.api.ServerEntityTickCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
		ServerEntityTickCallback.EVENT.register((entity, isPassengerTick) -> {
			ArrayList<String> tagsToRemove = new ArrayList<>();
			for (String tag : entity.getScoreboardTags()) {
				if (tag.startsWith(MapZone.TAG_PREFIX)) {
					List<Entity> entityList = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(5), EntityPredicates.EXCEPT_SPECTATOR.and(entity1 -> tag.endsWith(entity1.getUuidAsString())));
					if (!(entityList.size() > 0 && entityList.get(0).getBoundingBox().intersects(entity.getBoundingBox()))) {
						tagsToRemove.add(tag);
					}
				}
			}
			for (String tag : tagsToRemove) {
				UUID uuid = UUID.fromString(tag.substring(MapZone.TAG_PREFIX.length()));

				ServerWorld localWorld = (ServerWorld) entity.getWorld();
				OverlapCallbacks zoneEntity = (OverlapCallbacks) localWorld.getEntity(uuid);
				if (zoneEntity != null) {
					zoneEntity.onEntityIntersectionEnd(entity);
				} else {
					localWorld.getServer().getWorlds().forEach(serverWorld -> {
						if (serverWorld == localWorld) return;

						OverlapCallbacks zoneEntity1 = (OverlapCallbacks) serverWorld.getEntity(uuid);
						if (zoneEntity1 != null) zoneEntity1.onEntityIntersectionEnd(entity);
					});
				}

				entity.removeScoreboardTag(tag);
			}
		});
	}
}
