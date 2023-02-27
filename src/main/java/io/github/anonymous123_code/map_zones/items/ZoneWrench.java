package io.github.anonymous123_code.map_zones.items;

import io.github.anonymous123_code.map_zones.entities.MapZone;
import io.github.anonymous123_code.map_zones.networking.MapZonesPackets;
import io.github.anonymous123_code.map_zones.networking.ZoneCommandSyncPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;


public class ZoneWrench extends Item implements MapZonesItem {
	public ZoneWrench(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		HitResult hitResult = MapZonesItem.raycast(user, 0);
		if (hitResult instanceof EntityHitResult entityHitResult
				&& entityHitResult.getEntity() instanceof MapZone zone) {
			if (user instanceof ServerPlayerEntity serverPlayerEntity) {
				ServerPlayNetworking.send(serverPlayerEntity, MapZonesPackets.OPEN_CONFIG_SCREEN, ZoneCommandSyncPacket.from(zone).getByteBuffer());
			}
			return TypedActionResult.success(user.getStackInHand(hand));
		}
		return TypedActionResult.pass(user.getStackInHand(hand));
	}
}
