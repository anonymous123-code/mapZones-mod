package io.github.anonymous123_code.map_zones.client.gui;

import io.github.anonymous123_code.map_zones.client.gui.widget.CommandListWidget;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;

public class ZoneConfigScreen extends BaseUIModelScreen<FlowLayout> {

	public ZoneConfigScreen() {
		super(FlowLayout.class, DataSource.file("../src/main/resources/assets/map_zones/ui/zone_config.xml"));
	}

	@Override
	protected void build(FlowLayout rootComponent) {
	}

}
