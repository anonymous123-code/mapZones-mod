package io.github.anonymous123_code.map_zones.client.gui;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ZoneConfigScreen extends BaseUIModelScreen<FlowLayout> {
	public final ArrayList<DeletableStringRef> onEnterCommands = new ArrayList<>();
	public final ArrayList<DeletableStringRef> onTickCommands = new ArrayList<>();
	public final ArrayList<DeletableStringRef> onExitCommands = new ArrayList<>();

	private final List<String> initialOnEnterCommands;
	private final List<String> initialOnTickCommands;
	private final List<String> initialOnExitCommands;

	public ZoneConfigScreen(List<String> initialOnEnterCommands, List<String> initialOnTickCommands, List<String> initialOnExitCommands) {
		super(FlowLayout.class, DataSource.file("../src/main/resources/assets/map_zones/ui/zone_config.xml"));
		this.initialOnEnterCommands = initialOnEnterCommands;
		this.initialOnTickCommands = initialOnTickCommands;
		this.initialOnExitCommands = initialOnExitCommands;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		rootComponent.childById(ButtonComponent.class, "deleteAndExitButton").onPress(element->{
			// TODO: implement
		});

		rootComponent.childById(ButtonComponent.class, "cancelButton").onPress(element -> this.closeScreen());

		rootComponent.childById(ButtonComponent.class, "doneButton").onPress(element -> {
			// TODO: send packet to save data
			this.closeScreen();
		});

		FlowLayout eventList = rootComponent.childById(FlowLayout.class, "eventList");
		FlowLayout enterEventComponent = this.model.expandTemplate(FlowLayout.class, "eventInputList", Map.of("event_name", "On Enter:"));
		enterEventComponent.childById(ButtonComponent.class, "appendCommandButton").onPress(this.createOnPressListener(this.onEnterCommands));
		this.setupCommandListWithInitialList(enterEventComponent, this.onEnterCommands, this.initialOnEnterCommands);
		eventList.child(enterEventComponent);

		FlowLayout tickEventComponent = this.model.expandTemplate(FlowLayout.class, "eventInputList", Map.of("event_name", "On Tick:"));
		tickEventComponent.childById(ButtonComponent.class, "appendCommandButton").onPress(this.createOnPressListener(this.onTickCommands));
		this.setupCommandListWithInitialList(tickEventComponent, this.onTickCommands, this.initialOnTickCommands);
		eventList.child(tickEventComponent);

		FlowLayout exitEventComponent = this.model.expandTemplate(FlowLayout.class, "eventInputList", Map.of("event_name", "On Exit:"));
		exitEventComponent.childById(ButtonComponent.class, "appendCommandButton").onPress(this.createOnPressListener(this.onExitCommands));
		this.setupCommandListWithInitialList(exitEventComponent, this.onExitCommands, this.initialOnExitCommands);
		eventList.child(exitEventComponent);
	}

	private void setupCommandListWithInitialList(FlowLayout listElement, List<DeletableStringRef> targetList, List<String> originList) {
		for (String command : originList) {
			this.addChildToCommandList(listElement, new DeletableStringRef(targetList, command));
		}
	}

	private Consumer<ButtonComponent> createOnPressListener(List<DeletableStringRef> list) {
		return element -> this.addChildToCommandList((FlowLayout) element.parent(), new DeletableStringRef(list));
	}

	private void addChildToCommandList(FlowLayout listElement, DeletableStringRef command) {
		FlowLayout commandInputField = this.model.expandTemplate(FlowLayout.class, "commandInput", Map.of("command", command.get()));
		commandInputField.childById(TextBoxComponent.class, "commandTextBox").onChanged().subscribe(command::set);
		commandInputField.childById(ButtonComponent.class, "deleteCommandButton").onPress(button -> {
			command.delete();
			button.parent().remove();
		});
		listElement.child(listElement.children().size()-1, commandInputField);
	}

	public static class DeletableStringRef {
		private final List<DeletableStringRef> parent;

		private String content;

		public DeletableStringRef(List<DeletableStringRef> parent) {
			this(parent, "");
		}

		public DeletableStringRef(List<DeletableStringRef> parent, String content) {
			this.parent = parent;
			this.parent.add(this);
			this.content = content;
		}

		public void set(String content) {
			this.content = content;
		}

		public String get() {
			return this.content;
		}

		public void delete() {
			this.parent.remove(this);
		}
	}
}
