package com.xor.controller.net.tasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer.Task;
import com.xor.controller.SerialThread;
import com.xor.controller.XORController;
import com.xor.controller.net.events.MotorControlEvent;
import com.xor.controller.windows.LinearCCDWindow;

public class LineTracerTask extends Task {
	public enum TracingDirection {
		LEFT, STRAIGHT, TRACING, RIGHT;
	}

	private TracingDirection tracingDirection = TracingDirection.STRAIGHT;
	private LinearCCDWindow ccdWindow;
	private SerialThread serialThread;

	private float detectionRatio = 0.35f;

	public LineTracerTask(LinearCCDWindow ccdWindow) {
		this.serialThread = ccdWindow.getSerialThread();
		this.ccdWindow = ccdWindow;
	}

	public TracingDirection getTracingDirection() {
		return tracingDirection;
	}

	public void setTracingDirection(TracingDirection tracingDirection) {
		this.tracingDirection = tracingDirection;
	}

	public float getDetectionRatio() {
		return detectionRatio;
	}

	public void setDetectionRatio(float detectionRatio) {
		this.detectionRatio = detectionRatio;
	}

	@Override
	public void run() {
		if (serialThread.isRunning()) {

			Array<Vector2> localMaximas = serialThread.getLocalMaximas();
			if (localMaximas != null) {
				if (localMaximas.size == 0) {
					tracingDirection = TracingDirection.TRACING;
				} else {
					int leftCount = 0, centerCount = 0, rightCount = 0;
					for (Vector2 max : localMaximas) {
						if (max.x < 55)
							leftCount++;
						else if (max.x > 85)
							rightCount++;
						else
							centerCount++;
					}

					if (leftCount > centerCount && leftCount > rightCount)
						tracingDirection = TracingDirection.LEFT;
					if (centerCount > leftCount && centerCount > rightCount)
						tracingDirection = TracingDirection.STRAIGHT;
					if (rightCount > centerCount && rightCount > leftCount)
						tracingDirection = TracingDirection.RIGHT;
				}

				if (!XORController.MANUAL_MODE) {
					switch (tracingDirection) {
					case LEFT:
						serialThread.sendEvent(new MotorControlEvent(
								MotorControlEvent.MOTOR_LEFT,
								MotorControlEvent.MOTOR_BACKWARD, 150));
						serialThread.sendEvent(new MotorControlEvent(
								MotorControlEvent.MOTOR_RIGHT,
								MotorControlEvent.MOTOR_FORWARD, 150));
						break;
					case RIGHT:
						serialThread.sendEvent(new MotorControlEvent(
								MotorControlEvent.MOTOR_LEFT,
								MotorControlEvent.MOTOR_FORWARD, 150));
						serialThread.sendEvent(new MotorControlEvent(
								MotorControlEvent.MOTOR_RIGHT,
								MotorControlEvent.MOTOR_BACKWARD, 150));
						break;
					case STRAIGHT:
						serialThread.sendEvent(new MotorControlEvent(
								MotorControlEvent.MOTOR_LEFT,
								MotorControlEvent.MOTOR_FORWARD, 150));
						serialThread.sendEvent(new MotorControlEvent(
								MotorControlEvent.MOTOR_RIGHT,
								MotorControlEvent.MOTOR_FORWARD, 150));
						break;
					case TRACING:
						serialThread.sendEvent(new MotorControlEvent(
								MotorControlEvent.MOTOR_LEFT,
								MotorControlEvent.MOTOR_BACKWARD, 130));
						serialThread.sendEvent(new MotorControlEvent(
								MotorControlEvent.MOTOR_RIGHT,
								MotorControlEvent.MOTOR_BACKWARD, 130));
					}
				}

				ccdWindow.showDirection(tracingDirection);
			}
		}
	}
}
