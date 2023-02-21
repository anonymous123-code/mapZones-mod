package io.github.anonymous123_code.map_zones.client;

import io.github.anonymous123_code.map_zones.entities.MapZonesEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class ClientMapZones implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		EntityRendererRegistry.register(MapZonesEntities.MAP_ZONE, EmptyEntityRenderer::new);
	}
}
