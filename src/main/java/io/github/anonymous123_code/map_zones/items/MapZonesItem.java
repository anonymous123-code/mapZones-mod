package io.github.anonymous123_code.map_zones.items;


import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.HitResult;

public interface MapZonesItem {


	// 100% not stolen from gadget
	// https://github.com/wisp-forest/gadget/blob/e26d8a7bccbd536b689c37cff004673f2f71481a/src/main/java/io/wispforest/gadget/client/GadgetClient.java#L229-L247
	// 100% not stolen from owo-whats-this
	// https://github.com/wisp-forest/owo-whats-this/blob/master/src/main/java/io/wispforest/owowhatsthis/OwoWhatsThis.java#L155-L171.
	static HitResult raycast(Entity entity, float tickDelta) {
		var blockTarget = entity.raycast(5, tickDelta, false);

		var maxReach = entity.getRotationVec(tickDelta).multiply(5);
		var entityTarget = ProjectileUtil.raycast(
				entity,
				entity.getEyePos(),
				entity.getEyePos().add(maxReach),
				entity.getBoundingBox().stretch(maxReach),
				candidate -> true,
				5 * 5
		);

		return entityTarget != null && entityTarget.squaredDistanceTo(entity) < blockTarget.squaredDistanceTo(entity)
				? entityTarget
				: blockTarget;
	}
}
