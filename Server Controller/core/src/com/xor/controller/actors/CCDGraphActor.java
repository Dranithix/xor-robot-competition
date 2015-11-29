package com.xor.controller.actors;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisTable;
import com.xor.controller.SerialThread;

public class CCDGraphActor extends VisTable {
	private ShapeRenderer renderer = new ShapeRenderer();
	private SerialThread serialThread;

	public CCDGraphActor(SerialThread serialThread) {
		this.serialThread = serialThread;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		batch.end();

		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.setTransformMatrix(batch.getTransformMatrix());

		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BLACK);
		renderer.rect(getX(), getY(), getRight(), getTop());
		if (serialThread != null && serialThread.isRunning()) {
			for (Vector2 max : serialThread.getLocalMaximas()) {
				if (max != null) {
				max = max.cpy().scl(1f / 128f, 1f / 156f)
						.scl(getRight(), getTop()).add(getX(), getY());
				renderer.setColor(Color.GREEN);
				renderer.rect(max.x - 5, max.y - 10, 10, 10);
				}
			}

		}
		renderer.end();

		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.WHITE);
		try {
			if (serialThread != null && serialThread.isRunning()) {
				Array<Vector2> outputSignal = serialThread.getOutputSignal();

				for (int i = 1; i < outputSignal.size; i++) {
					Vector2 last = outputSignal.get(i - 1);
					Vector2 current = outputSignal.get(i);

					if (last != null && current != null) {
						renderer.line(
								last.cpy().scl(1f / 128f, 1f / 156f)
										.scl(getRight(), getTop())
										.add(getX(), getY()),
								current.cpy().scl(1f / 128f, 1f / 156f)
										.scl(getRight(), getTop())
										.add(getX(), getY()));
					}
				}
			} else {
				renderer.line(getX(), getY() + getTop() / 2, getRight(), getY()
						+ getTop() / 2);
			}
		} catch (Exception ex) {
		}

		renderer.end();

		batch.begin();

	}
}
