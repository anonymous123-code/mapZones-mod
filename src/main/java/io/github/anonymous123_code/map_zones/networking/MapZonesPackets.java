package io.github.anonymous123_code.map_zones.networking;

import io.github.anonymous123_code.map_zones.MapZones;
import io.github.anonymous123_code.map_zones.client.gui.ZoneConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class MapZonesPackets {
	public static final Identifier OPEN_CONFIG_SCREEN = MapZones.id("open_config_screen");
	public static void registerClientReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(OPEN_CONFIG_SCREEN, MapZonesPackets::onOpenConfigScreen);
	}

	private static void onOpenConfigScreen(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
		OpenConfigScreenPacket packet = new OpenConfigScreenPacket(packetByteBuf);
		client.execute(() -> client.setScreen(new ZoneConfigScreen(packet.getOnEnterCommands(), packet.getOnTickCommands(), packet.getOnExitCommands())));
	}
}
