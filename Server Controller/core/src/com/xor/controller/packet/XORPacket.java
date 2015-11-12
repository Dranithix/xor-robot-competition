package com.xor.controller.packet;

import com.badlogic.gdx.utils.Array;

public abstract class XORPacket {
	public static final char PACKET_SEPARATOR = '|';

	private XORPacketOpcode header;
	private Array<String> contents = new Array<String>();

	public XORPacket(XORPacketOpcode header) {
		this.header = header;
	}

	abstract void initPacket();

	public String getRawPacket() {
		StringBuilder packetBuilder = new StringBuilder(String.valueOf(header
				.getOpcode()));
		for (String data : contents) {
			packetBuilder.append(PACKET_SEPARATOR);
			packetBuilder.append(data);
		}
		packetBuilder.append('\n');
		return packetBuilder.toString();
	}

	public void putInt(int val) {
		putString(String.valueOf(val));
	}

	public void putBoolean(boolean val) {
		putInt(val ? 1 : 0);
	}

	public void putString(String val) {
		contents.add(val);
	}
}
