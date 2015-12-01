package com.xor.controller;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.xor.controller.input.ControllerEvent;
import com.xor.controller.input.ControllerRecorder;
import com.xor.controller.net.events.MotorControlEvent;
import com.xor.controller.net.events.PneumaticsControlEvent;
import com.xor.controller.windows.ConsoleWindow;
import com.xor.controller.windows.LinearCCDWindow;
import com.xor.controller.windows.MapEditWindow;
import com.xor.controller.windows.SettingsWindow;
import com.xor.controller.windows.TrendlineWindow;

public class XORController extends ControllerAdapter implements
		ApplicationListener {
	public static boolean MANUAL_MODE = true;

	public static final int COM_PORT = 9;
	public static final String COM_PORT_ADDRESS = "COM".concat(Integer
			.toString(COM_PORT));
	public static final int MAX_MOTOR_PWM = 200;

	private SerialThread serialThread;

	private LinearCCDWindow ccdWindow;
	private ConsoleWindow consoleWindow;
	private SettingsWindow settingsWindow;
	private MapEditWindow mapEditWindow;

	private ControllerRecorder startRecorder, endRecorder;
	private long recorderInitTime;
	private boolean recordingStart = false, recordingEnd = false;

	private Stage stage;

	@Override
	public void create() {
		Thread thread = new Thread(serialThread = new SerialThread());
		thread.start();

		VisUI.load();

		stage = new Stage(new FitViewport(1280, 768));
		Gdx.input.setInputProcessor(stage);

		VisTable layout = new VisTable(true);
		layout.defaults().fill().expand().pad(10);

		VisTable commandLayout = new VisTable(true);
		commandLayout.defaults().fill().expand();
		commandLayout.add(ccdWindow = new LinearCCDWindow(serialThread)).row();
		commandLayout.add(consoleWindow = new ConsoleWindow()).row();

		layout.add(commandLayout);

		VisTable settingsLayout = new VisTable(true);
		settingsLayout.defaults().fillX().expandX();
		settingsLayout.add(settingsWindow = new SettingsWindow()).row();
		settingsLayout.add(mapEditWindow = new MapEditWindow(this)).fillY()
				.expandY().row();

		layout.add(settingsLayout);

		VisTable dataLayout = new VisTable(true);
		dataLayout.defaults().fill().expand();
		dataLayout.add(new TrendlineWindow()).row();

		layout.add(dataLayout);

		layout.setFillParent(true);

		stage.addActor(layout);

		Controllers.addListener(this);

		startRecorder = new ControllerRecorder("start");
		endRecorder = new ControllerRecorder("end");
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();

		if (serialThread.isRunning()) {
			settingsWindow.showTouchingHolder(
					serialThread.isCorrectLeftHolder(),
					serialThread.isCorrectRightHolder());
		}
	}

	@Override
	public void dispose() {
		stage.dispose();
		serialThread.dispose();

		VisUI.dispose();
	}

	boolean tileTestMode = false, timing = false;
	long lastTick;

	float getDegreeDelay45(int x) {
		return ((float) (1.3977 * Math.pow(x, 2) - 12.817 * x + 368.68)) / 1000f;
	}
	
	float getTilesPerDistance(float x) {
		System.out.println(((float) (0.05 * Math.pow(x, 2) + 0.35 * x + 0.3)));
		return ((float) (0.05 * Math.pow(x, 2) + 0.35 * x + 0.3));
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonIndex) {
		switch (buttonIndex) {
		case 1:
			// serialThread.sendEvent(new MotorControlEvent(
			// MotorControlEvent.MOTOR_LEFT,
			// MotorControlEvent.MOTOR_FORWARD, 200));
			// serialThread.sendEvent(new MotorControlEvent(
			// MotorControlEvent.MOTOR_RIGHT,
			// MotorControlEvent.MOTOR_FORWARD, 200));
			// System.out.println(System.currentTimeMillis() - lastTick);
			break;
		}
		return super.buttonUp(controller, buttonIndex);
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		System.out.println("Button Code: " + buttonCode);
		switch (buttonCode) {
		case 0: // Shoot
			serialThread.sendEvent(new PneumaticsControlEvent(
					PneumaticsControlEvent.VALVE_SERVE_SHUTTLECOCK, true));
			Timer.instance().scheduleTask(new Task() {

				@Override
				public void run() {
					serialThread.sendEvent(new PneumaticsControlEvent(
							PneumaticsControlEvent.VALVE_SERVE_RACKET, true));
				}

			}, 0.15f);
			Timer.instance().scheduleTask(new Task() {

				@Override
				public void run() {
					serialThread.sendEvent(new PneumaticsControlEvent(
							PneumaticsControlEvent.VALVE_SERVE_RACKET, false));
					serialThread.sendEvent(new PneumaticsControlEvent(
							PneumaticsControlEvent.VALVE_SERVE_SHUTTLECOCK,
							false));
				}

			}, 1.12f);
			break;
		case 1: // Single Tile Movement Testing 0.7 for 1 tile, 1.2 for 2 tiles
//			this.moveSec(Timer.instance(), 1.8f, 0.3f);
			// serialThread.sendEvent(new MotorControlEvent(
			// MotorControlEvent.MOTOR_LEFT,
			// MotorControlEvent.MOTOR_FORWARD, 0));
			// serialThread.sendEvent(new MotorControlEvent(
			// MotorControlEvent.MOTOR_RIGHT,
			// MotorControlEvent.MOTOR_FORWARD, 0));
			break;
		case 2: // Rotation Value Testing
			timing = !timing;
			if (timing) {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_FORWARD, 0));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_BACKWARD, 0));

				lastTick = System.currentTimeMillis();
			} else {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_FORWARD, 200));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_FORWARD, 200));
				System.out.println(System.currentTimeMillis() - lastTick);
			}
			break;
		case 4: // L1 Left Gripper
			leftHolderGrab = !leftHolderGrab;
			serialThread.sendEvent(new PneumaticsControlEvent(
					PneumaticsControlEvent.VALVE_LEFT_GRIPPER, leftHolderGrab));
			break;
		case 5: // R1 Right Gripper
			rightHolderGrab = !rightHolderGrab;
			serialThread
					.sendEvent(new PneumaticsControlEvent(
							PneumaticsControlEvent.VALVE_RIGHT_GRIPPER,
							rightHolderGrab));
			break;
		case 6: // L2 Recording Start Track
			recordingStart = !recordingStart;
			System.out.println(recordingStart ? "Start track - Started recording."
					: "Start track - Ended recording.");
			if (recordingStart) {
				startRecorder.clearMovesFile();
				recorderInitTime = System.currentTimeMillis();
			}
			break;
		case 7: // R2 Recording End Track
			recordingEnd = !recordingEnd;
			System.out.println(recordingEnd ? "End track - Started recording."
					: "End track - Ended recording.");
			if (recordingEnd) {
				endRecorder.clearMovesFile();
				recorderInitTime = System.currentTimeMillis();
			}
			break;
		case 8: // Select Button - Playback Start Track
			startRecorder.playbackMoves(serialThread);
			break;
		case 9: // Start Button - Playback End Track
			endRecorder.playbackMoves(serialThread);
			break;
			
		case 11: //Emergy Stop Button (Right Button Joystick Thing)
			serialThread.sendEvent(new MotorControlEvent(
					MotorControlEvent.MOTOR_LEFT,
					MotorControlEvent.MOTOR_FORWARD, 200));
			serialThread.sendEvent(new MotorControlEvent(
					MotorControlEvent.MOTOR_RIGHT,
					MotorControlEvent.MOTOR_FORWARD, 200));
			Timer.instance().clear();
			break;
		}
		return false;
	}

	boolean leftHolderGrab = false, rightHolderGrab = false;
	boolean holdersUp = false;

	boolean odd = false;

	public static final int RIGHT_X_AXIS = 0;
	public static final int RIGHT_Y_AXIS = 1;
	public static final int LEFT_X_AXIS = 2;
	public static final int LEFT_Y_AXIS = 3;

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {

		float leftMotor = (-controller.getAxis(LEFT_X_AXIS) + controller
				.getAxis(LEFT_Y_AXIS));
		float rightMotor = (-controller.getAxis(LEFT_X_AXIS) - controller
				.getAxis(LEFT_Y_AXIS));

		int leftMotorDir = leftMotor >= 0 ? MotorControlEvent.MOTOR_FORWARD
				: MotorControlEvent.MOTOR_BACKWARD;
		int rightMotorDir = rightMotor >= 0 ? MotorControlEvent.MOTOR_FORWARD
				: MotorControlEvent.MOTOR_BACKWARD;

		float initialPwm = MAX_MOTOR_PWM / 2;
		int leftMotorScaled = MAX_MOTOR_PWM
				- MathUtils.round(Math.abs(leftMotor) * initialPwm);
		int rightMotorScaled = MAX_MOTOR_PWM
				- MathUtils.round(Math.abs(rightMotor) * initialPwm);

		if (recordingStart) {
			startRecorder.logMove(new ControllerEvent(System
					.currentTimeMillis() - recorderInitTime, leftMotorScaled,
					rightMotorScaled, leftMotorDir, rightMotorDir));
		}
		
		if (recordingEnd) {
			endRecorder.logMove(new ControllerEvent(System
					.currentTimeMillis() - recorderInitTime, leftMotorScaled,
					rightMotorScaled, leftMotorDir, rightMotorDir));
		}

		settingsWindow.showMotorPos(MathUtils.ceil(leftMotor * initialPwm),
				MathUtils.ceil(rightMotor * initialPwm));

		serialThread.sendEvent(new MotorControlEvent(
				MotorControlEvent.MOTOR_LEFT, leftMotorDir, leftMotorScaled));
		serialThread
				.sendEvent(new MotorControlEvent(MotorControlEvent.MOTOR_RIGHT,
						rightMotorDir, rightMotorScaled));
		return false;
	}

	int count = 0;

	public float rotate(final Timer timer, float degrees, float startTime) {
		float timeFactor = (Math.abs(degrees) / 90f) * 0.52f;
		final boolean direction = degrees > 0; // True = CW, False = CCW

		if (direction) {
			if (Math.abs(degrees) == 45)
				timeFactor = 0.2853f;
			else if (Math.abs(degrees) == 90)
				timeFactor = 0.6356f;
		} else {
			if (Math.abs(degrees) == 45)
				timeFactor = 0.2657f;
			else if (Math.abs(degrees) == 90)
				timeFactor = 0.479f;
		}

		System.out.println(degrees + " degrees "
				+ (direction ? "Clockwise" : "Counter-Clockwise") + " costing "
				+ timeFactor + " seconds.");
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						direction ? MotorControlEvent.MOTOR_FORWARD
								: MotorControlEvent.MOTOR_BACKWARD, 0));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						direction ? MotorControlEvent.MOTOR_BACKWARD
								: MotorControlEvent.MOTOR_FORWARD, 0));
			}

		}, startTime);
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						direction ? MotorControlEvent.MOTOR_FORWARD
								: MotorControlEvent.MOTOR_BACKWARD, 200));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						direction ? MotorControlEvent.MOTOR_BACKWARD
								: MotorControlEvent.MOTOR_FORWARD, 200));
			}

		}, startTime + timeFactor);
		return timeFactor;
	}

	public float move(final Timer timer, final float f, final float startTime) {
		final float timePerTile = 0.425f;
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_FORWARD, 0));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_FORWARD, 0));
			}

		}, startTime);
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_FORWARD, 200));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_FORWARD, 200));
			}

		}, startTime + timePerTile * f);
		return timePerTile * f;
	}

	public float moveSec(final Timer timer, float time,
			final float reboundTime, final float startTime) {
		time = this.getTilesPerDistance(time) * 1.2f;
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT, MotorControlEvent.MOTOR_FORWARD,
						0));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT, MotorControlEvent.MOTOR_FORWARD,
						0));
			}

		}, startTime);

		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_BACKWARD, 150));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_BACKWARD, 150));
			}

		}, startTime + time);
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_FORWARD, 200));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_FORWARD, 200));
			}

		}, startTime + time + reboundTime);
		return time + reboundTime;
	}

	// Moves through tiles with a 0.05 second backwards movement to mitigate
	// drift.
	public float moveTile(final Timer timer, final float f,
			final float startTime) {
		final float timePerTile = 0.65f;
		float accTime = startTime;
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_FORWARD, 0));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_FORWARD, 0));
			}

		}, accTime);

		accTime += 0.05f;
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_BACKWARD, 0));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_BACKWARD, 0));
			}

		}, accTime);

		accTime += timePerTile * f;
		timer.scheduleTask(new Task() {

			@Override
			public void run() {
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_LEFT,
						MotorControlEvent.MOTOR_FORWARD, 200));
				serialThread.sendEvent(new MotorControlEvent(
						MotorControlEvent.MOTOR_RIGHT,
						MotorControlEvent.MOTOR_FORWARD, 200));
			}

		}, accTime);

		return accTime;
	}

	public void followPath(List<GridCell> path) {
		Timer t = new Timer();

//		float lastAngle = 0;
//		float time = 0;
//		for (int i = 1; i < path.size(); i++) {
//			GridCell last = path.get(i - 1);
//			GridCell current = path.get(i);
//
//			float angle = new Vector2(current.x - last.x, current.y - last.y)
//					.angle();
//			time += rotate(t, (int) (angle - lastAngle), time) + 0.1f;
//			time += this.moveSec(t, new Vector2(last.x, last.y).dst(new Vector2(
//					current.x, current.y)), 0.3f, time);
//			lastAngle = angle;
//
//		}
		
		float time = 0;
		time += this.rotate(t, 45, time);
		time += this.moveSec(t, (float) Math.sqrt(2), 0.3f,  time) + 0.5f;
		time += this.rotate(t, 45, time);
		time += this.moveSec(t, 3, 0.3f, time) + 0.5f;
		time += this.rotate(t, -45, time);
		time += this.moveSec(t, 2, 0.1f, time + 0.5f) + 1f;
		time += this.rotate(t, -30, time + 0.5f) + 1f;
		time += this.moveSec(t, 1, 0.5f, time + 0.5f) + 0.5f;
		t.start();
	}

	public void reversePath(List<GridCell> path) {
		Timer t = new Timer();

		float lastAngle = 0;
		float time = 0;
		for (int i = path.size() - 1; i > 0; i--) {
			GridCell last = path.get(i);
			GridCell current = path.get(i - 1);

			float angle = new Vector2(current.x - last.x, current.y - last.y)
					.angle();
			time += rotate(t, (int) (angle - lastAngle), time);
			time += move(t, new Vector2(last.x, last.y).dst(new Vector2(
					current.x, current.y)), time);
			lastAngle = angle;

		}
		t.start();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
