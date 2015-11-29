package com.xor.controller.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class ControllerRecorder {
	private FileHandle movesFile;

	public ControllerRecorder() {
		movesFile = Gdx.files.local("moves.xor");
	}
	
	public void clearMovesFile() {
		if (movesFile.exists()) movesFile.delete();
	}

	public void logMove(ControllerEvent event) {
		movesFile.writeString(event.toString() + "\n", true);
	}

	public Array<ControllerEvent> loadMoves() {
		Array<ControllerEvent> events = new Array<ControllerEvent>();
		for (String line : movesFile.readString().split("\n")) {
			System.out.println(line);
			String[] content = line.split("|");
			events.add(new ControllerEvent(Integer.parseInt(content[0]),
					Integer.parseInt(content[1]), Integer.parseInt(content[2]),
					Integer.parseInt(content[3]), Integer.parseInt(content[4])));
		}
		return events;
	}
}
