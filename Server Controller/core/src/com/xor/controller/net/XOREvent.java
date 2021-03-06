
package com.xor.controller.net;

import com.badlogic.gdx.utils.Array;

public abstract class XOREvent {
	public static final char PACKET_SEPARATOR = '|';

	private XOREventOpcode header;
	private Array<String> contents = new Array<String>();

	public XOREvent(XOREventOpcode header) {
		this.header = header;
	}

	protected abstract void initEvent();

	public String getRawPacket() {
		StringBuilder eventBuilder = new StringBuilder(String.valueOf(header
				.getOpcode()));
		for (String data : contents) {
			eventBuilder.append(PACKET_SEPARATOR);
			eventBuilder.append(data);
		}
		eventBuilder.append('\n');
		return eventBuilder.toString();
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
