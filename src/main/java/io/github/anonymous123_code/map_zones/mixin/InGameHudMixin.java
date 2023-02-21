package io.github.anonymous123_code.map_zones.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface InGameHudMixin {

	@Accessor("heldItemTooltipFade")
	int getHeldItemTooltipFade();

	@Accessor("heldItemTooltipFade")
	void setHeldItemTooltipFade(int value);
}
