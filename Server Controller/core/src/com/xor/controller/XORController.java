package com.xor.controller;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.xor.controller.packet.MotorControlPacket;

public class XORController extends ApplicationAdapter {
	public static final int COM_PORT = 12;
	public static final String COM_PORT_ADDRESS = "COM".concat(Integer
			.toString(COM_PORT));

	static final int WORLD_WIDTH = 128;
	static final int WORLD_HEIGHT = 156;

	private SerialThread serialThread;

	private BitmapFont font;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;

	private OrthographicCamera cam;

	int xInc = 0, yInc = 0;
	int x = 0, y = 0;

	@Override
	public void create() {
		cam = new OrthographicCamera(128, 156);
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);

		batch = new SpriteBatch();
		font = new BitmapFont();
		shapeRenderer = new ShapeRenderer();

		Thread thread = new Thread(serialThread = new SerialThread());
		thread.start();
	}

	@Override
	public void render() {
		cam.update();
		x += xInc;
		y += yInc;

		if (serialThread.isRunning()) {
			if (Gdx.input.isKeyPressed(Keys.W)) {
				serialThread.sendPacket(new MotorControlPacket(
						MotorControlPacket.MOTOR_LEFT,
						new Random().nextInt(10), new Random().nextInt(10)));
			} else if (Gdx.input.isKeyPressed(Keys.A)) {
				serialThread.sendPacket(new MotorControlPacket(
						MotorControlPacket.MOTOR_LEFT,
						new Random().nextInt(10), new Random().nextInt(10)));
			} else if (Gdx.input.isKeyPressed(Keys.S)) {
				serialThread.sendPacket(new MotorControlPacket(
						MotorControlPacket.MOTOR_LEFT,
						new Random().nextInt(10), new Random().nextInt(10)));
			} else if (Gdx.input.isKeyPressed(Keys.D)) {
				serialThread.sendPacket(new MotorControlPacket(
						MotorControlPacket.MOTOR_LEFT,
						new Random().nextInt(10), new Random().nextInt(10)));
			}
			Array<Vector2> inputSignal = serialThread.getInputSignal();
			Array<Vector2> outputSignal = serialThread.getOutputSignal();

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			shapeRenderer.setProjectionMatrix(cam.combined);
			shapeRenderer.begin(ShapeType.Line);
//			shapeRenderer.setColor(Color.GREEN);
//			try {
//				for (int i = 1; i < inputSignal.size; i++) {
//					Vector2 last = inputSignal.get(i - 1);
//					Vector2 current = inputSignal.get(i);
//					if (last != null && current != null)
//						shapeRenderer.line(last, current);
//				}
//			} catch (Exception ex) {
//			}
			shapeRenderer.setColor(Color.WHITE);
			try {
				for (int i = 1; i < outputSignal.size; i++) {
					Vector2 last = outputSignal.get(i - 1);
					Vector2 current = outputSignal.get(i);
					if (last != null && current != null)
						shapeRenderer.line(last, current);
				}
			} catch (Exception ex) {
			}
			shapeRenderer.end();

			batch.begin();
			font.draw(
					batch,
					"Left Area to Right Area Ratio: "
							+ serialThread
									.getFilteredAreas().toString(), 5, 15);
			batch.end();
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		serialThread.dispose();
	}
}
