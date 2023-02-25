package io.github.anonymous123_code.map_zones.api;

import net.minecraft.entity.Entity;

public interface OverlapCallbacks {
	void onEntityIntersectionBegin(Entity other);
	void onEntityIntersectionTick(Entity other);
	void onEntityIntersectionEnd(Entity other);
}
