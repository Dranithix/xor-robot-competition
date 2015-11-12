package com.xor.controller.packet;

public enum XORPacketOpcode {
	MOTOR_CONTROL(0);
	
	private int opcode;
	
	private XORPacketOpcode(int opcode) {
		this.opcode = opcode;
	}
	
	public int getOpcode() {
		return opcode;
	}
}
