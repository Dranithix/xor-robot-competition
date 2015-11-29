package com.xor.controller.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.xor.controller.XORController;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "XOR Team :: Group 5 - Internal Robotics Competition Controller";
		config.width = 1280;
		config.height = 768;
		config.foregroundFPS = 0;
		config.fullscreen = false;
		new LwjglApplication(new XORController(), config);
	}
}
