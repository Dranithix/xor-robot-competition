package com.xor.controller.packet;

public class MotorControlPacket extends XORPacket {
	public static final int MOTOR_LEFT = 1;
	public static final int MOTOR_RIGHT = 2;
	
	private int type, magnitude, direction;

	public MotorControlPacket(int type, int magnitude, int direction) {
		super(XORPacketOpcode.MOTOR_CONTROL);
		
		setType(type);
		setMagnitude(magnitude);
		setDirection(direction);
		
		initPacket();
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
	void initPacket() {
		putInt(type);
		putInt(magnitude);
		putInt(direction);
	}

}
