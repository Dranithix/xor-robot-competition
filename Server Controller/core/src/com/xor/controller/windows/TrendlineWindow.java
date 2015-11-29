package com.xor.controller.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.building.OneColumnTableBuilder;
import com.kotcrab.vis.ui.building.TableBuilder;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.xor.controller.trendline.PolyTrendLine;
import com.xor.controller.trendline.TrendLine;
import com.xor.controller.ui.RowLayout;

public class TrendlineWindow extends VisWindow {
	private ShapeRenderer renderer = new ShapeRenderer();
	Table tabContentTable = new Table();

	public TrendlineWindow() {
		super("Data Regression");

		TabbedPane tabbedPane = new TabbedPane();
		tabbedPane.add(new RegressionTab("Rotation") {

		});
		tabbedPane.add(new RegressionTab("Velocity") {

		});
		tabbedPane.add(new RegressionTab("Shuttlecock") {

		});
		tabbedPane.addListener(new TabbedPaneAdapter() {
			@Override
			public void switchedTab(Tab tab) {
				Table content = tab.getContentTable();

				tabContentTable.clearChildren();
				tabContentTable.add(content).expand().fill();
			}
		});

		TableUtils.setSpacingDefaults(this);
		TableBuilder builder = new OneColumnTableBuilder(new Padding(2, 3));

		setMovable(false);

		builder.append(CellWidget.of(tabbedPane.getTable()).expandX().fillX()
				.wrap());
		builder.row();

		tabContentTable.add(tabbedPane.getActiveTab().getContentTable())
				.expand().fill();

		builder.append(CellWidget.of(tabContentTable).expandX().expandY()
				.fillX().fillY().wrap());
		builder.row();

		Table table = builder.build();
		add(table).expand().fill();
	}

	abstract class RegressionTab extends Tab {
		Table content;
		String dataLabel;
		VisTextButton btnCollectData, btnRegressData;

		DataGraphActor dataGraphActor;

		TrendLine trendline = new PolyTrendLine(0);

		public List<Double> x = new ArrayList<Double>();
		public List<Double> y = new ArrayList<Double>();

		public Array<Vector2> regressedData = new Array<Vector2>();

		public RegressionTab(String dataLabel) {
			super(true, false);

			this.dataLabel = dataLabel;

			TableBuilder builder = new OneColumnTableBuilder(new Padding(2, 3));

			setMovable(false);

			RowLayout rowLayout = new RowLayout(new Padding(0, 0, 0, 5));

			builder.append(CellWidget.of(dataGraphActor = new DataGraphActor())
					.expandX().expandY().fillX().fillY().wrap());
			builder.row();

			builder.append(
					rowLayout,
					CellWidget.builder().fillX().expandX(),
					CellWidget.of(
							btnCollectData = new VisTextButton("Collect Data"))
							.wrap(),
					CellWidget.of(
							btnRegressData = new VisTextButton("Regress Data"))
							.wrap());
			builder.row();

			btnCollectData.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					addRegressionData(new Random().nextDouble() * 5);
				}

			});

			content = builder.build();
			content.setFillParent(true);
		}

		void addRegressionData(double y) {
			this.x.add((double) (x.size() + 1));
			this.y.add(y);

			updateGraph();
		}

		void updateGraph() {
			if (x.size() > 1) {
				regressedData.clear();
				
				trendline = new PolyTrendLine(x.size() - 2);

				trendline.setValues(y.stream().mapToDouble(i -> i).toArray(), x
						.stream().mapToDouble(i -> i).toArray());
			}

			Array<Vector2> originalData = new Array<Vector2>();
			for (int x = 0; x < this.x.size(); x++) {
				originalData.add(new Vector2(x, y.get(x).floatValue()));
				if (this.x.size() > 1) {
					regressedData.add(new Vector2(x, (float) trendline
							.predict(x)));
				}
			}

			dataGraphActor.displayData(originalData, regressedData);
		}

		@Override
		public String getTabTitle() {
			return dataLabel;
		}

		@Override
		public Table getContentTable() {
			return content;
		}
	}

	class DataGraphActor extends VisTable {
		private Array<Vector2> originalData, regressedData;

		public DataGraphActor() {
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.BLACK);
			pixmap.fill();

			setBackground(new TextureRegionDrawable(new TextureRegion(
					new Texture(pixmap))));

			pixmap.dispose();
		}

		public void displayData(Array<Vector2> originalData,
				Array<Vector2> regressedData) {
			this.originalData = originalData;
			this.regressedData = regressedData;

			System.out.println(Arrays.toString(regressedData.toArray()));
		}

		float max(Array<Vector2> arr) {
			float max = 0;
			for (int i = 0; i < arr.size; i++) {
				if (arr.get(i).y > max)
					max = arr.get(i).y;
			}
			return max;
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);

			batch.end();

			if (originalData != null) {
				float[] polyLine = new float[originalData.size * 2];
				int x = 0;
				renderer.begin(ShapeType.Filled);
				renderer.setColor(Color.WHITE);
				for (Vector2 point : originalData) {
					Vector2 p = point
							.cpy()
							.scl(1f / originalData.size, 1f / max(originalData))
							.scl(getWidth(), getHeight());

					this.localToStageCoordinates(p);

					polyLine[x++] = p.x;
					polyLine[x++] = p.y;

					renderer.circle(p.x, p.y, 2);
				}
				renderer.end();

				if (originalData.size > 2) {
					renderer.begin(ShapeType.Line);
					renderer.polyline(polyLine);
					renderer.end();
				}
			}

			if (regressedData != null) {
				float[] polyLine = new float[regressedData.size * 2];
				int x = 0;

				renderer.begin(ShapeType.Filled);
				renderer.setColor(Color.GREEN);
				for (Vector2 point : regressedData) {
					Vector2 p = point
							.cpy()
							.scl(1f / regressedData.size,
									1f / max(regressedData))
							.scl(getWidth(), getHeight());

					this.localToStageCoordinates(p);

					polyLine[x++] = p.x;
					polyLine[x++] = p.y;

					renderer.circle(p.x, p.y, 2);
				}
				renderer.end();

				if (regressedData.size > 2) {
					renderer.begin(ShapeType.Line);
					renderer.polyline(polyLine);
					renderer.end();
				}
			}

			batch.begin();
		}
	}
}
