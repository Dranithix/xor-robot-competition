package com.xor.controller.net.events;

import com.xor.controller.net.XOREvent;
import com.xor.controller.net.XOREventOpcode;

public class PneumaticsControlEvent extends XOREvent {
	public static final int VALVE_LEFT_GRIPPER = 0;
	public static final int VALVE_RIGHT_GRIPPER = 1;
	public static final int VALVE_PULL_GRIPPERS = 2;
	public static final int VALVE_SERVE_SHUTTLECOCK = 3;
	public static final int VALVE_SERVE_RACKET = 4;
	private int port;
	private boolean state;

	public PneumaticsControlEvent(int port, boolean state) {
		super(XOREventOpcode.PNEUMATICS_CONTROL);

		setPort(port);
		setState(state);

		initEvent();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	protected void initEvent() {
		putInt(port);
		putBoolean(state);
	}

}
