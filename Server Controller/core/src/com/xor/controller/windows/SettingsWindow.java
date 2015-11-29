package com.xor.controller.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.building.OneColumnTableBuilder;
import com.kotcrab.vis.ui.building.TableBuilder;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.xor.controller.ui.RowLayout;

public class SettingsWindow extends VisWindow {
	private VisTextField txtMotorX, txtMotorY;
	private VisTextField txtCorrectLeftHolder, txtCorrectRightHolder;

	public SettingsWindow() {
		super("Settings");

		TableUtils.setSpacingDefaults(this);
		TableBuilder builder = new OneColumnTableBuilder(new Padding(2, 3));

		setMovable(false);

		RowLayout rowLayout = new RowLayout(new Padding(0, 0, 0, 5));

		builder.append(rowLayout, CellWidget.builder().fillX().expandX(),
				CellWidget.of(new VisLabel("Motor Left:")).wrap(), CellWidget
						.of(txtMotorX = new VisTextField("0")).expandX()
						.fillX().wrap());
		builder.row();

		builder.append(rowLayout, CellWidget.builder().fillX().expandX(),
				CellWidget.of(new VisLabel("Motor Right:")).wrap(), CellWidget
						.of(txtMotorY = new VisTextField("0")).expandX()
						.fillX().wrap());
		builder.row();

		builder.append(CellWidget.of(new Separator()).fillX().wrap());
		builder.row();

		builder.append(
				rowLayout,
				CellWidget.builder().fillX().expandX(),
				CellWidget.of(new VisLabel("Left Holder Conductivity:")).wrap(),
				CellWidget
						.of(txtCorrectLeftHolder = new VisTextField("INCORRECT"))
						.expandX().fillX().wrap());

		builder.append(
				rowLayout,
				CellWidget.builder().fillX().expandX(),
				CellWidget.of(new VisLabel("Right Holder Conductivity:"))
						.wrap(),
				CellWidget
						.of(txtCorrectRightHolder = new VisTextField(
								"INCORRECT")).expandX().fillX().wrap());

		Table table = builder.build();
		add(table).expand().fill();

		pack();
	}

	public void showTouchingHolder(boolean correctLeftHolder,
			boolean correctRightHolder) {
		txtCorrectLeftHolder.setText(correctLeftHolder ? "CORRECT"
				: "INCORRECT");
		txtCorrectRightHolder.setText(correctRightHolder ? "CORRECT"
				: "INCORRECT");
	}

	public void showMotorPos(float leftWheel, float rightWheel) {
		txtMotorX.setText(String.valueOf(leftWheel));
		txtMotorY.setText(String.valueOf(rightWheel));
	}
}
