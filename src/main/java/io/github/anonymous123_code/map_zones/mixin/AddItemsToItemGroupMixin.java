package io.github.anonymous123_code.map_zones.mixin;

import io.github.anonymous123_code.map_zones.items.MapZonesItems;
import net.minecraft.feature_flags.FeatureFlagBitSet;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemGroups.class)
public class AddItemsToItemGroupMixin {
	@Inject(
			method = "m_rowifiqz(Lnet/minecraft/feature_flags/FeatureFlagBitSet;Lnet/minecraft/item/ItemGroup$ItemStackCollector;Z)V",
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/item/ItemGroup$ItemStackCollector.addItem (Lnet/minecraft/item/ItemConvertible;)V",
					ordinal = 0)
			)
	private static void addCreativeItems(FeatureFlagBitSet featureFlagBitSet, ItemGroup.ItemStackCollector collector, boolean bl, CallbackInfo ci) {
		collector.addItem(MapZonesItems.ZONE_CORNER);
		collector.addItem(MapZonesItems.ZONE_WRENCH);
	}
}
