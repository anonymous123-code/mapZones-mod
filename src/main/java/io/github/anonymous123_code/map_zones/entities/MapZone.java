package io.github.anonymous123_code.map_zones.entities;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.anonymous123_code.map_zones.MapZones;
import io.github.anonymous123_code.map_zones.api.OverlapCallbacks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.CommonTexts;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MapZone extends Entity implements OverlapCallbacks {
	public static final String TAG_PREFIX = "mods/map_zones/collidesWith/";
	private static final TrackedData<BlockPos> first = DataTracker.registerData(MapZone.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<BlockPos> second = DataTracker.registerData(MapZone.class, TrackedDataHandlerRegistry.BLOCK_POS);

	private final List<ParseResults<ServerCommandSource>> onEnterCommands = new ArrayList<>();
	private final List<ParseResults<ServerCommandSource>> onTickCommands = new ArrayList<>();
	private final List<ParseResults<ServerCommandSource>> onExitCommands = new ArrayList<>();
	private EntityDimensions dimensions = super.getDimensions(null);
	public MapZone(EntityType<?> variant, World world) {
		super(variant, world);
	}

	@Override
	public void tick() {
		super.tick();
		List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(1.0E-7), EntityPredicates.EXCEPT_SPECTATOR.and(entity -> !(entity instanceof MapZone))); // TODO: replace with MapZones:Shouldn't trigger tag
		if (!list.isEmpty()) {
			for (Entity entity2 : list) {
				if (entity2.getBoundingBox().intersects(this.getBoundingBox())) {
					this.onEntityIntersection(entity2);
				}
			}
		}

	}

	private void onEntityIntersection(Entity other) {
		if (!this.getWorld().isClient()) {
			String scoreboardTag = TAG_PREFIX + this.uuidString;
			if (!other.getScoreboardTags().contains(scoreboardTag)) {
				this.onEntityIntersectionBegin(other);
				other.addScoreboardTag(scoreboardTag);
			}
			this.onEntityIntersectionTick(other);
		}
	}

	@Override
	public void onEntityIntersectionBegin(Entity other) {
		this.executeCommandList(this.onEnterCommands, other.getCommandSource());
	}

	@Override
	public void onEntityIntersectionTick(Entity other) {
		this.executeCommandList(this.onTickCommands, other.getCommandSource());
	}

	@Override
	public void onEntityIntersectionEnd(Entity other) {
		this.executeCommandList(this.onExitCommands, other.getCommandSource());
	}

	private void executeCommandList(List<ParseResults<ServerCommandSource>> commands, ServerCommandSource source) {
		for (ParseResults<ServerCommandSource> command : commands) {
			try {
				this.getServer().getCommandManager().getDispatcher().execute(CommandManager.method_45018(command, src -> source));
			} catch (CommandSyntaxException e) {
				MapZones.LOGGER.error("WARNING: Invalid command in zone {} at {}", source.getEntity().getUuidAsString(), source.getEntity().getPos());
			}
		}
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return this.dimensions;
	}

	@Override
	protected Box calculateBoundsForPose(EntityPose pos) {
		return this.calculateBoundingBox();
	}

	@Override
	protected Box calculateBoundingBox() {
		return new Box(this.getFirst(), this.getSecond()).expand(0.5,0.5,0.5).offset(0.5,0.5,0.5);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(first, BlockPos.ORIGIN);
		this.dataTracker.startTracking(second, BlockPos.ORIGIN);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.setFirst(extractBlockPosFromIntList(nbt.getIntArray("FirstPos")));
		this.setSecond(extractBlockPosFromIntList(nbt.getIntArray("SecondPos")));
		this.updateDimensionsChange();

		ServerCommandSource dummySource = new ServerCommandSource(
				CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, 2, "", CommonTexts.EMPTY, null, null
		);

		this.onEnterCommands.clear();
		NbtList onEnterCommands = nbt.getList("OnEnter", NbtElement.STRING_TYPE);
		for (NbtElement command : onEnterCommands) {
			this.onEnterCommands.add(this.getServer().getCommandManager().getDispatcher().parse(command.asString(), dummySource));
		}

		this.onTickCommands.clear();
		NbtList onTickCommands = nbt.getList("OnTick", NbtElement.STRING_TYPE);
		for (NbtElement command : onTickCommands) {
			this.onTickCommands.add(this.getServer().getCommandManager().getDispatcher().parse(command.asString(), dummySource));
		}

		this.onExitCommands.clear();
		NbtList onExitCommands = nbt.getList("OnExit", NbtElement.STRING_TYPE);
		for (NbtElement command : onExitCommands) {
			this.onExitCommands.add(this.getServer().getCommandManager().getDispatcher().parse(command.asString(), dummySource));
		}
	}

	public void setListeners (List<String> onEnterCommands, List<String> onTickCommands, List<String> onExitCommands) {
		ServerCommandSource dummySource = new ServerCommandSource(
				CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, 2, "", CommonTexts.EMPTY, null, null
		);

		this.onEnterCommands.clear();
		for (String command : onEnterCommands) {
			this.onEnterCommands.add(this.getServer().getCommandManager().getDispatcher().parse(command, dummySource));
		}

		this.onTickCommands.clear();
		for (String command : onTickCommands) {
			this.onTickCommands.add(this.getServer().getCommandManager().getDispatcher().parse(command, dummySource));
		}

		this.onExitCommands.clear();
		for (String command : onExitCommands) {
			this.onExitCommands.add(this.getServer().getCommandManager().getDispatcher().parse(command, dummySource));
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putIntArray("FirstPos", new int[]{this.getFirst().getX(), this.getFirst().getY(), this.getFirst().getZ()});
		nbt.putIntArray("SecondPos", new int[]{this.getSecond().getX(), this.getSecond().getY(), this.getSecond().getZ()});

		NbtList enterCommands = new NbtList();
		enterCommands.addAll(this.onEnterCommands.stream().map(command -> NbtString.of(command.getReader().getString())).toList());
		nbt.put("OnEnter", enterCommands);

		NbtList tickCommands = new NbtList();
		tickCommands.addAll(this.onTickCommands.stream().map(command -> NbtString.of(command.getReader().getString())).toList());
		nbt.put("OnTick", tickCommands);

		NbtList exitCommands = new NbtList();
		exitCommands.addAll(this.onExitCommands.stream().map(command -> NbtString.of(command.getReader().getString())).toList());
		nbt.put("OnExit", exitCommands);
	}

	private BlockPos getFirst() {
		return this.dataTracker.get(first);
	}

	private BlockPos getSecond() {
		return this.dataTracker.get(second);
	}

	public void setFirst(BlockPos pos) {
		this.dataTracker.set(first, pos);
		this.updateDimensionsChange();
	}

	public void setSecond(BlockPos pos) {
		this.dataTracker.set(second, pos);
		this.updateDimensionsChange();
	}

	public List<ParseResults<ServerCommandSource>> getOnEnterCommands() {
		return this.onEnterCommands;
	}

	public List<ParseResults<ServerCommandSource>> getOnTickCommands() {
		return this.onTickCommands;
	}

	public List<ParseResults<ServerCommandSource>> getOnExitCommands() {
		return this.onExitCommands;
	}

	private static BlockPos extractBlockPosFromIntList(int[] list) {
		return switch (list.length) {
			case 0 -> BlockPos.ORIGIN;
			case 1 -> new BlockPos(list[0], 0, 0);
			case 2 -> new BlockPos(list[0], list[1], 0);
			default -> new BlockPos(list[0], list[1], list[2]);
		};
	}

	private void updateDimensionsChange() {
		Box box = this.calculateBoundingBox();
		this.dimensions = super.getDimensions(null).scaled(
				(float) Math.max(
						box.getXLength(),
						box.getZLength()
				),
				(float) box.getYLength()
		);
		this.calculateDimensions();
		this.setPosition(box.getCenter().withAxis(Direction.Axis.Y, box.getMin(Direction.Axis.Y)));
	}
}
