package io.github.anonymous123_code.map_zones.networking;

import com.mojang.brigadier.ParseResults;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;

public class OpenConfigScreenPacket {
	private final List<String> onEnterCommands;
	private final List<String> onTickCommands;
	private final List<String> onExitCommands;
	public OpenConfigScreenPacket(List<ParseResults<ServerCommandSource>> onEnterCommands, List<ParseResults<ServerCommandSource>> onTickCommands, List<ParseResults<ServerCommandSource>> onExitCommands) {
		this.onEnterCommands = onEnterCommands.stream().map(command->command.getReader().getString()).toList();
		this.onTickCommands = onTickCommands.stream().map(command->command.getReader().getString()).toList();
		this.onExitCommands = onExitCommands.stream().map(command->command.getReader().getString()).toList();
	}

	public OpenConfigScreenPacket(PacketByteBuf buf) {
		this.onEnterCommands = buf.readList(PacketByteBuf::readString);
		this.onTickCommands = buf.readList(PacketByteBuf::readString);
		this.onExitCommands = buf.readList(PacketByteBuf::readString);
	}

	public void send(ServerPlayerEntity player) {
		PacketByteBuf packetByteBuf = PacketByteBufs.create();
		packetByteBuf.writeCollection(this.onEnterCommands, PacketByteBuf::writeString);
		packetByteBuf.writeCollection(this.onTickCommands, PacketByteBuf::writeString);
		packetByteBuf.writeCollection(this.onExitCommands, PacketByteBuf::writeString);
		ServerPlayNetworking.send(player, MapZonesPackets.OPEN_CONFIG_SCREEN, packetByteBuf);
	}

	public List<String> getOnEnterCommands() {
		return this.onEnterCommands;
	}

	public List<String> getOnTickCommands() {
		return this.onTickCommands;
	}

	public List<String> getOnExitCommands() {
		return this.onExitCommands;
	}
}
