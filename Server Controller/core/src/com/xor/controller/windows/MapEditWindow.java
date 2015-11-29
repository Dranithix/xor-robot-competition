package com.xor.controller.windows;

import java.util.List;

import javax.swing.JOptionPane;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.OneColumnTableBuilder;
import com.kotcrab.vis.ui.building.TableBuilder;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.xor.controller.XORController;
import com.xor.controller.actors.GameMapActor;
import com.xor.controller.actors.GameMapActor.GameTileActor;
import com.xor.controller.ui.RowLayout;

public class MapEditWindow extends VisWindow {
	private final GameTileActor[][] map = new GameTileActor[6][8];
	private AStarGridFinder<GridCell> finder = new AStarGridFinder<GridCell>(
			GridCell.class);

	private List<GridCell> path;

	private GameMapActor mapActor;
	private VisTextButton btnCalculatePath, btnFollowPath, btnReversePath;
	private XORController controller;

	public MapEditWindow(final XORController controller) {
		super("Game Map Editor");

		this.controller = controller;

		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[0].length; y++) {
				map[x][y] = new GameTileActor(x, y, GameTileActor.TILE_EMPTY);
			}
		}

		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				map[x][y].setType(GameTileActor.TILE_START);
			}
		}

		for (int x = map.length - 1; x > map.length - 3; x--) {
			for (int y = map[0].length - 1; y > map[0].length - 3; y--) {
				map[x][y].setType(GameTileActor.TILE_END);
			}
		}

		for (int x = map.length - 1; x > map.length - 3; x--) {
			for (int y = 0; y < map[0].length - 2; y++) {
				map[x][y].setType(GameTileActor.TILE_OBSTACLE);
			}
		}

		TableUtils.setSpacingDefaults(this);
		TableBuilder builder = new OneColumnTableBuilder(new Padding(2, 3));

		RowLayout rowLayout = new RowLayout(new Padding(0, 0, 0, 5));

		setMovable(false);

		builder.append(CellWidget.of(mapActor = new GameMapActor()).expandX()
				.expandY().fillX().fillY().wrap());
		builder.row();

		builder.append(
				rowLayout,
				CellWidget.builder().fillX().expandX(),
				CellWidget.of(
						btnCalculatePath = new VisTextButton("Calculate Path"))
						.wrap(),
				CellWidget.of(btnFollowPath = new VisTextButton("Follow Path"))
						.wrap(),
				CellWidget.of(
						btnReversePath = new VisTextButton("Reverse Path"))
						.wrap());
		builder.row();

		Table table = builder.build();
		add(table).expand().fill();

		btnCalculatePath.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				calculatePath();
			}

		});

		btnFollowPath.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (path != null) {
					controller.followPath(path);
				}
			}
		});

		btnReversePath.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (path != null) {
					controller.reversePath(path);
				}
			}
		});

		mapActor.updateMap(map);
	}

	public void calculatePath() {
		GridCell[][] cells = new GridCell[map.length][map[0].length];
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[0].length; y++) {
				cells[x][y] = map[x][y].getCell();
			}
		}

		NavigationGrid<GridCell> navGrid = new NavigationGrid<GridCell>(cells,
				false);

		path = finder.findPath(navGrid.getCell(0, 0), navGrid.getCell(4, 6),
				navGrid);
		if (path != null) {
			path.add(0, navGrid.getCell(0, 0));
			System.out.println(path);

			mapActor.updateMap(map);
			mapActor.updatePath(path);

		} else {
			JOptionPane.showMessageDialog(null, "No path could be computed.");
		}
	}
}
