package com.xor.controller.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.building.OneColumnTableBuilder;
import com.kotcrab.vis.ui.building.TableBuilder;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.xor.controller.SerialThread;
import com.xor.controller.actors.CCDGraphActor;
import com.xor.controller.net.tasks.LineTracerTask.TracingDirection;

public class LinearCCDWindow extends VisWindow {
	private VisLabel lblTracerDirection;
	private SerialThread serialThread;
	
	public LinearCCDWindow(SerialThread serialThread) {
		super("Linear CCD");
		
		this.serialThread = serialThread;

		TableUtils.setSpacingDefaults(this);
		TableBuilder builder = new OneColumnTableBuilder(new Padding(2, 3));

		setMovable(false);

		builder.append(CellWidget.of(new CCDGraphActor(serialThread)).expandX()
				.expandY().fillX().fillY().wrap());
		builder.row();

		builder.append(lblTracerDirection = new VisLabel("Direction: STRAIGHT"));
		builder.row();

		Table table = builder.build();
		add(table).expand().fill();
	}
	
	public SerialThread getSerialThread() {
		return serialThread;
	}

	public void setSerialThread(SerialThread serialThread) {
		this.serialThread = serialThread;
	}

	public void showDirection(TracingDirection tracerDirection) {
		lblTracerDirection.setText("Direction: " + tracerDirection.name());
	}
}
