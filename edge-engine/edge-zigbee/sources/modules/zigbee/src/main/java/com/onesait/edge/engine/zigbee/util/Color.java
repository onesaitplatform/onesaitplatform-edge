package com.onesait.edge.engine.zigbee.util;

import java.security.InvalidParameterException;

public class Color {

	public enum Mode {
		HSV(0),
		XY(1),
		TEMPERATURE(2);
		
		private int id;
		
		private Mode (int modeId) {
			this.id = modeId;
		}
		
		public int getId() {
			return this.id;
		}
	}
	
	private static final int[] N_PARAMETERS = new int[] {3, 3, 2};
	
	private Mode mode;
	private int[] colorParameters;
	
	public Color(Mode mode, int... colorParameters) {
		this.mode = mode;
		if (colorParameters.length != N_PARAMETERS[mode.getId()]) {
			throw new InvalidParameterException();
		}
		this.colorParameters = colorParameters;
	}
	
	public Mode getMode() {
		return this.mode;
	}
	
	public Integer getHue() {
		return this.mode.getId() == Mode.HSV.getId() ? this.colorParameters[0] : null;
	}
	
	public Integer getSaturation() {
		return this.mode.getId() == Mode.HSV.getId() ? this.colorParameters[1] : null;
	}
	
	public Integer getX() {
		return this.mode.getId() == Mode.XY.getId() ? this.colorParameters[0] : null;
	}
	
	public Integer getY() {
		return this.mode.getId() == Mode.XY.getId() ? this.colorParameters[1] : null;
	}
	
	public Integer getTemperature() {
		return this.mode.getId() == Mode.TEMPERATURE.getId() ? this.colorParameters[0] : null;
	}
	
	public Integer getLevel() {
		int levelIdx = 2;
		if (this.mode.getId() == Mode.TEMPERATURE.getId()) {
			levelIdx = 1;
		}
		return this.colorParameters[levelIdx];
	}
	
	public static long getTempMireds(double temperature) {
		return Math.round(1_000_000 / temperature);
	}
}
