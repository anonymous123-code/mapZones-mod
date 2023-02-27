package io.github.anonymous123_code.map_zones.networking;

import io.github.anonymous123_code.map_zones.entities.MapZone;
import net.minecraft.network.PacketByteBuf;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

import java.util.List;
import java.util.UUID;

public class ZoneCommandSyncPacket {
	private final UUID zoneUUID;
	private final List<String> onEnterCommands;
	private final List<String> onTickCommands;
	private final List<String> onExitCommands;

	public ZoneCommandSyncPacket(UUID zoneUUID, List<String> onEnterCommands, List<String> onTickCommands, List<String> onExitCommands) {
		this.zoneUUID = zoneUUID;
		this.onEnterCommands = onEnterCommands;
		this.onTickCommands = onTickCommands;
		this.onExitCommands = onExitCommands;
	}

	public static ZoneCommandSyncPacket from(MapZone entity) {
		return new ZoneCommandSyncPacket(
				entity.getUuid(),
				entity.getOnEnterCommands().stream().map(command->command.getReader().getString()).toList(),
				entity.getOnTickCommands().stream().map(command->command.getReader().getString()).toList(),
				entity.getOnExitCommands().stream().map(command->command.getReader().getString()).toList()
		);
	}

	public ZoneCommandSyncPacket(PacketByteBuf buf) {
		this.zoneUUID = buf.readUuid();
		this.onEnterCommands = buf.readList(PacketByteBuf::readString);
		this.onTickCommands = buf.readList(PacketByteBuf::readString);
		this.onExitCommands = buf.readList(PacketByteBuf::readString);
	}

	public PacketByteBuf getByteBuffer() {
		PacketByteBuf packetByteBuf = PacketByteBufs.create();
		packetByteBuf.writeUuid(this.zoneUUID);
		packetByteBuf.writeCollection(this.onEnterCommands, PacketByteBuf::writeString);
		packetByteBuf.writeCollection(this.onTickCommands, PacketByteBuf::writeString);
		packetByteBuf.writeCollection(this.onExitCommands, PacketByteBuf::writeString);
		return packetByteBuf;
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

	public UUID getZoneUUID() {
		return this.zoneUUID;
	}
}
