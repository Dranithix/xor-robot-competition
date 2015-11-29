package com.xor.controller.net;

public enum XOREventOpcode {
	MOTOR_CONTROL(0), PNEUMATICS_CONTROL(1);
	
	private int opcode;
	
	private XOREventOpcode(int opcode) {
		this.opcode = opcode;
	}
	
	public int getOpcode() {
		return opcode;
	}
}
