package io.github.anonymous123_code.map_zones.items;

import io.github.anonymous123_code.map_zones.entities.MapZone;
import io.github.anonymous123_code.map_zones.networking.MapZonesPackets;
import io.github.anonymous123_code.map_zones.networking.ZoneCommandSyncPacket;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;


public class ZoneWrench extends Item implements MapZonesItem {
	public ZoneWrench(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		HitResult hitResult = MapZonesItem.raycast(user, 0);
		if (hitResult instanceof EntityHitResult entityHitResult
				&& entityHitResult.getEntity() instanceof MapZone zone) {
			if (user instanceof ServerPlayerEntity serverPlayerEntity && Permissions.check(serverPlayerEntity, "map_zones.zone.edit.settings", 2)) {
				ServerPlayNetworking.send(serverPlayerEntity, MapZonesPackets.OPEN_CONFIG_SCREEN, ZoneCommandSyncPacket.from(zone).getByteBuffer());
			}
			return TypedActionResult.success(user.getStackInHand(hand));
		}
		return TypedActionResult.pass(user.getStackInHand(hand));
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("item.map_zones.zone_wrench.tooltip.1"));
	}
}
