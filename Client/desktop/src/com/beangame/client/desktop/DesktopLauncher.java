package com.beangame.client.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.beangame.client.BeanGame;
import com.beangame.client.Settings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "The Bean Game!!!!";
		config.width = Settings.RES_WIDTH;
		config.height = Settings.RES_HEIGHT;

		new LwjglApplication(new BeanGame(), config);
	}
}
