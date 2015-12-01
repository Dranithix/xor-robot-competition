package com.xor.controller.net.events;

import com.xor.controller.net.XOREvent;
import com.xor.controller.net.XOREventOpcode;

public class MotorControlEvent extends XOREvent {
	public static final int MOTOR_LEFT = 1;
	public static final int MOTOR_RIGHT = 2;
	
	public static final int MOTOR_FORWARD = 1;
	public static final int MOTOR_BACKWARD = 0;
	
	private int type, magnitude, direction;

	public MotorControlEvent(int type, int direction, int magnitude) {
		super(XOREventOpcode.MOTOR_CONTROL);
		
		setType(type);
		setDirection(direction);
		setMagnitude(magnitude);
	
		initEvent();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(int magnitude) {
		this.magnitude = magnitude;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	protected void initEvent() {
		putInt(type);
		if (type == MOTOR_LEFT) {
			putInt(direction == MOTOR_FORWARD ? MOTOR_BACKWARD : MOTOR_FORWARD);
		} else {
			putInt(direction);
		}
		putInt(type == MOTOR_LEFT ? magnitude : magnitude);
	}

}
