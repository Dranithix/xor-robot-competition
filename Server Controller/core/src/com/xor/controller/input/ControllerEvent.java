package com.xor.controller.input;

import com.xor.controller.SerialThread;
import com.xor.controller.net.events.MotorControlEvent;

public class ControllerEvent {
	private int leftMotor, rightMotor, leftDirection, rightDirection;
	private long time;

	public ControllerEvent(long time, int leftMotor, int rightMotor,
			int leftDirection, int rightDirection) {
		this.time = time;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftDirection = leftDirection;
		this.rightDirection = rightDirection;
	}

	public void sendNetworkEvent(SerialThread thread) {
		thread.sendEvent(new MotorControlEvent(MotorControlEvent.MOTOR_LEFT,
				leftDirection, leftMotor));
		thread.sendEvent(new MotorControlEvent(MotorControlEvent.MOTOR_RIGHT,
				rightDirection, rightMotor));
	}

	@Override
	public String toString() {
		return time + "|" + leftMotor + "|" + rightMotor + "|" + leftDirection
				+ "|" + rightDirection;
	}
}