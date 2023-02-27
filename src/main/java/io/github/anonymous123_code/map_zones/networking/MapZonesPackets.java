package io.github.anonymous123_code.map_zones.networking;

import io.github.anonymous123_code.map_zones.MapZones;
import io.github.anonymous123_code.map_zones.client.gui.ZoneConfigScreen;
import io.github.anonymous123_code.map_zones.entities.MapZone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class MapZonesPackets {
	public static final Identifier OPEN_CONFIG_SCREEN = MapZones.id("open_config_screen");
	public static final Identifier SAVE_CONFIG_SCREEN = MapZones.id("save_config_screen");
	public static void registerClientReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(OPEN_CONFIG_SCREEN, MapZonesPackets::onOpenConfigScreen);
	}

	public static void registerServerReceivers() {
		ServerPlayNetworking.registerGlobalReceiver(SAVE_CONFIG_SCREEN, MapZonesPackets::onSaveConfigScreen);
	}

	private static void onSaveConfigScreen(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler playNetworkHandler, PacketByteBuf buf, PacketSender sender) {
		ZoneCommandSyncPacket packet = new ZoneCommandSyncPacket(buf);
		server.execute(() -> {
			Entity shouldBeZone = player.getWorld().getEntity(packet.getZoneUUID());
			if (shouldBeZone instanceof MapZone zone) {
				zone.setListeners(packet.getOnEnterCommands(), packet.getOnTickCommands(), packet.getOnExitCommands());
			} else {
				MapZones.LOGGER.warn("Player {}, send a Save Config Screen Packet with a invalid UUID", player);
			}
		});
	}

	private static void onOpenConfigScreen(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
		ZoneCommandSyncPacket packet = new ZoneCommandSyncPacket(packetByteBuf);
		client.execute(() -> client.setScreen(new ZoneConfigScreen(packet.getZoneUUID(), packet.getOnEnterCommands(), packet.getOnTickCommands(), packet.getOnExitCommands())));
	}
}
