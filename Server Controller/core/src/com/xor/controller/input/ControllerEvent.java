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

	public int getLeftMotor() {
		return leftMotor;
	}

	public void setLeftMotor(int leftMotor) {
		this.leftMotor = leftMotor;
	}

	public int getRightMotor() {
		return rightMotor;
	}

	public void setRightMotor(int rightMotor) {
		this.rightMotor = rightMotor;
	}

	public int getLeftDirection() {
		return leftDirection;
	}

	public void setLeftDirection(int leftDirection) {
		this.leftDirection = leftDirection;
	}

	public int getRightDirection() {
		return rightDirection;
	}

	public void setRightDirection(int rightDirection) {
		this.rightDirection = rightDirection;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void sendNetworkEvent(SerialThread thread) {
		thread.sendEvent(new MotorControlEvent(MotorControlEvent.MOTOR_LEFT,
				leftDirection, leftMotor));
		thread.sendEvent(new MotorControlEvent(MotorControlEvent.MOTOR_RIGHT,
				rightDirection, rightMotor));
	}

	String delimiter = "@";

	@Override
	public String toString() {
		return time + delimiter + leftMotor + delimiter + rightMotor
				+ delimiter + leftDirection + delimiter + rightDirection;
	}
}