package com.xor.controller.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.OneColumnTableBuilder;
import com.kotcrab.vis.ui.building.TableBuilder;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.xor.controller.ui.RowLayout;

public class ConsoleWindow extends VisWindow {
	private VisTextButton btnSend;
	private VisTextField txtInput;
	private VisTextArea txtOutput;

	private PopupMenu helperMenu;

	public ConsoleWindow() {
		super("Console");

		TableUtils.setSpacingDefaults(this);
		TableBuilder builder = new OneColumnTableBuilder(new Padding(2, 3));

		setMovable(false);

		RowLayout rowLayout = new RowLayout(new Padding(0, 0, 0, 5));

		builder.append(CellWidget.of(txtOutput = new VisTextArea()).fillX()
				.fillY().expandX().expandY().wrap());
		builder.row();

		builder.append(rowLayout, CellWidget.builder().fillX().expandX(),
				CellWidget.of(new VisLabel("Input: ")).wrap(),
				CellWidget.of(txtInput = new VisTextField()).expandX().fillX()
						.wrap(),
				CellWidget.of(btnSend = new VisTextButton("Send")).wrap());
		builder.row();

		Table table = builder.build();
		add(table).expand().fill();

		helperMenu = new PopupMenu();
		helperMenu.addItem(new AutocompleteEntry("test-autocomplete"));
		helperMenu.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
			
			}
			
		});

		btnSend.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				txtOutput.appendText(txtInput.getText() + "\n");
				txtInput.setText("");
			}

		});
		txtInput.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if ("test-autocomplete".startsWith(txtInput.getText())) {
					helperMenu.showMenu(getStage(), txtInput.getRight(),
							txtInput.getTop());
				} else {
					helperMenu.remove();
				}
			}

		});
		txtOutput.setDisabled(true);
	}
	
	private class AutocompleteEntry extends MenuItem {

		public AutocompleteEntry(final String text) {
			super(text, new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					txtInput.setText(text);
				}
				
			});
		}
	}
}
