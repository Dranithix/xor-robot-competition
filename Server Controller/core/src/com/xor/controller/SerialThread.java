package com.xor.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.fazecast.jSerialComm.SerialPort;
import com.xor.controller.net.XOREvent;

public class SerialThread implements Disposable, Runnable {
	private Array<Vector2> inputSignal = new Array<Vector2>(),
			outputSignal = new Array<Vector2>();
	private GraphFilter filter = new GraphFilter();
	private boolean correctLeftHolder = false, correctRightHolder = false;

	private SerialPort comPort;
	private BufferedReader in;
	private PrintWriter out;
	private boolean running = false;

	public boolean isRunning() {
		return running;
	}

	public void run() {
		comPort = SerialPort.getCommPort(XORController.COM_PORT_ADDRESS);
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000,
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
					case "CAMERA":
						inputSignal.clear();
						outputSignal.clear();

						String[] lums = contents[1].split(" ");
						if (lums.length == 128) {
							float[] rawData = new float[lums.length];
							for (int i = 0; i < lums.length; i++) {
								inputSignal.add(new Vector2(i, Float
										.parseFloat(lums[i])));
								rawData[i] = Float.parseFloat(lums[i]);
							}

							outputSignal = filter.filterGraph(rawData);
						}
						break;
					case "GRAB":
						correctLeftHolder = Integer.parseInt(contents[1]) == 0;
						correctRightHolder = Integer.parseInt(contents[2]) == 0;
						break;
					}
				} catch (IOException ex) {
					continue;
				} catch (NumberFormatException ex) {
					continue;
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		} else {
			System.out
					.println("XOR: The server's COM Port is currently unavailable.");
		}
	}

	public void sendEvent(final XOREvent packet) {
		if (out != null) {
			out.write(packet.getRawPacket());
			out.flush();
		}
	}

	public float getMax() {
		float max = 0;
		for (int i = 0; i < outputSignal.size; i++) {
			if (outputSignal.get(i) != null)
				if (max < outputSignal.get(i).y)
					max = outputSignal.get(i).y;
		}
		return max;
	}

	public boolean isCorrectLeftHolder() {
		return correctLeftHolder;
	}
	
	public boolean isCorrectRightHolder() {
		return correctRightHolder;
	}
	
	public Array<Vector2> getLocalMaximas() {
		return filter.getLocalMaximas();
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