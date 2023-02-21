package io.github.anonymous123_code.map_zones;

import io.github.anonymous123_code.map_zones.entities.MapZonesEntities;
import io.github.anonymous123_code.map_zones.items.MapZonesItems;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapZones implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Example Mod");

	private static String modId;

	public static Identifier id(String path) {
		return new Identifier(modId, path);
	}

	@Override
	public void onInitialize(ModContainer mod) {
		modId = mod.metadata().id();
		
		MapZonesItems.register();
		MapZonesEntities.register();
	}
}
