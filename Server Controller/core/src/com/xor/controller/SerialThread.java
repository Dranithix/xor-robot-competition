package com.xor.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.fazecast.jSerialComm.SerialPort;
import com.xor.controller.packet.XORPacket;

public class SerialThread implements Disposable, Runnable {
	private Array<Vector2> inputSignal = new Array<Vector2>(),
			outputSignal = new Array<Vector2>();
	private GraphFilter filter = new GraphFilter();

	private SerialPort comPort;
	private BufferedReader in;
	private PrintWriter out;
	private boolean running = false;

	public boolean isRunning() {
		return running;
	}

	public void run() {
		comPort = SerialPort.getCommPort(XORController.COM_PORT_ADDRESS);
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100,
				0);
		comPort.setBaudRate(115200);

		if (running = comPort.openPort()) {
			System.out.format(
					"XOR: Server started on %s." + System.lineSeparator(),
					XORController.COM_PORT_ADDRESS);
			in = new BufferedReader(new InputStreamReader(
					comPort.getInputStream()));
			out = new PrintWriter(comPort.getOutputStream(), false);

			while (true) {
				try {
					final String line = in.readLine();
					if (line.isEmpty())
						continue;

					String[] contents = line.split(Pattern.quote("|"));
					switch (contents[0]) {
					case "CCD":
						inputSignal.clear();
						outputSignal.clear();

						String[] lums = contents[1].split(" ");
						float[] rawData = new float[lums.length];
						for (int i = 0; i < lums.length; i++) {
							inputSignal.add(new Vector2(i, Float
									.parseFloat(lums[i])));
							rawData[i] = Float.parseFloat(lums[i]);
						}

						outputSignal = filter.filterGraph(rawData);
						break;
					}
				} catch (IOException ex) {
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		} else {
			System.out
					.println("XOR: The server's COM Port is currently unavailable.");
		}
	}

	public void sendPacket(final XORPacket packet) {
		System.out.print(packet.getRawPacket());
		if (out != null) {
			out.write(packet.getRawPacket());
			out.flush();
		}
	}

	public FloatArray getFilteredAreas() {
		return filter.getFilteredAreas();
	}

	public Array<Vector2> getInputSignal() {
		return inputSignal;
	}

	public Array<Vector2> getOutputSignal() {
		return outputSignal;
	}

	@Override
	public void dispose() {
		comPort.closePort();
	}
}